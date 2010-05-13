package ch.almana.android.stechkarte.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stechkarte.application.StechkarteApplication;
import ch.almana.android.stechkarte.provider.db.DBBackendDay;
import ch.almana.android.stechkarte.provider.db.DBBackendTimestamp;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.provider.db.DB.OpenHelper;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;
import ch.almana.android.stechkarte.view.appwidget.StechkarteAppwidget;
import ch.almana.android.stechkarte.view.appwidget.StechkarteAppwidget.UpdateAppWidgetService;

public class StechkarteProvider extends ContentProvider {
	
	public static final String AUTHORITY = "ch.almana.android.stechkarte";
	
	private static final int TIMESTAMP = 1;
	private static final int DAY = 2;
	
	private static final UriMatcher sUriMatcher;
	
	private OpenHelper openHelper;
	
	@Override
	public boolean onCreate() {
		openHelper = new OpenHelper(getContext());
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			count = DBBackendTimestamp.delete(openHelper, uri, selection, selectionArgs);
			break;
		case DAY:
			count = DBBackendDay.delete(openHelper, uri, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		notifyChange(uri);
		return count;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return DBBackendTimestamp.getType(uri);
			
		case DAY:
			return DBBackendDay.getType(uri);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			ret = DBBackendTimestamp.insert(openHelper, uri, initialValues);
			break;
		case DAY:
			ret = DBBackendDay.insert(openHelper, uri, initialValues);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		notifyChange(uri);
		return ret;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			c = DBBackendTimestamp.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		case DAY:
			c = DBBackendDay.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			count = DBBackendTimestamp.update(openHelper, uri, values, selection, selectionArgs);
			break;
		case DAY:
			count = DBBackendDay.update(openHelper, uri, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		notifyChange(uri);
		return count;
	}
	
	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
		getContext().startService(new Intent(getContext(), UpdateAppWidgetService.class));
		// StechkarteAppwidget.updateView(StechkarteApplication.getAppContext());
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME, TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME + "/#", TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Days.CONTENT_ITEM_NAME, DAY);
		sUriMatcher.addURI(AUTHORITY, Days.CONTENT_ITEM_NAME + "/#", DAY);
		
	}
	
}
