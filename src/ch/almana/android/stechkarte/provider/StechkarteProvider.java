package ch.almana.android.stechkarte.provider;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ch.almana.android.stechkarte.provider.DB.Days;
import ch.almana.android.stechkarte.provider.DB.Months;
import ch.almana.android.stechkarte.provider.DB.OpenHelper;
import ch.almana.android.stechkarte.provider.DB.Timestamps;
import ch.almana.android.stechkarte.provider.DB.Weeks;
import ch.almana.android.stechkarte.view.appwidget.StechkarteAppwidget;

public class StechkarteProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.almana.android.stechkarte";

	private static final int TIMESTAMP = 1;
	private static final int DAY = 2;
	private static final int MONTH = 3;
	private static final int WEEK = 4;

	private static final int CONTENT = 1;
	private static final int CONTENT_ITEM = 2;

	private static final UriMatcher uriTableMatcher;

	private static UriMatcher uriContentTypeMatcher;
	private static Map<Integer, UriTableMapping> uriTableMap;

	private OpenHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new OpenHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		UriTableMapping utm = getUriTableMap(uri);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		switch (uriContentTypeMatcher.match(uri)) {
		case CONTENT:
			count = db.delete(utm.tableName, selection, selectionArgs);
			break;

		case CONTENT_ITEM:
			String id = uri.getPathSegments().get(1);
			count = db.delete(utm.tableName, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		notifyChange(uri);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret;
		// Validate the requested uri
		if (uriContentTypeMatcher.match(uri) != CONTENT) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		UriTableMapping utm = getUriTableMap(uri);
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		//		if (values.getAsFloat(Days.NAME_HOURS_TARGET) < 0) {
		//			values.put(Days.NAME_HOURS_TARGET, Settings.getInstance().getHoursTarget(values.getAsLong(Days.NAME_DAYREF)));
		//		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(utm.tableName, DB.NAME_ID, values);
		if (rowId > 0) {
			 ret = ContentUris.withAppendedId(uri, rowId); // TODO was Days.CONTENT_URI chick if uri is correct
		}else {
			throw new SQLException("Failed to insert row into " + uri);
		}

		notifyChange(uri);
		return ret;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		UriTableMapping utm = getUriTableMap(uri);
		qb.setTables(utm.tableName);
		switch (uriContentTypeMatcher.match(uri)) {
		case CONTENT:
			break;

		case CONTENT_ITEM:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Get the database and run the query
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		UriTableMapping utm = getUriTableMap(uri);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		switch (uriContentTypeMatcher.match(uri)) {
		case CONTENT:
			count = db.update(utm.tableName, values, selection, selectionArgs);
			break;

		case CONTENT_ITEM:
			String id = uri.getPathSegments().get(1);
			count = db.update(utm.tableName, values, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		notifyChange(uri);
		return count;
	}

	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
		StechkarteAppwidget.updateView(getContext());
	}

	@Override
	public String getType(Uri uri) {
		UriTableMapping uriTableMapping = getUriTableMap(uri);
		if (uriTableMapping == null) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		switch (uriContentTypeMatcher.match(uri)) {
		case CONTENT:
			return uriTableMapping.contentType;
		case CONTENT_ITEM:
			return uriTableMapping.contentItemType;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private UriTableMapping getUriTableMap(Uri uri) {
		return uriTableMap.get(uriTableMatcher.match(uri));
	}

	static {

		uriTableMap = new HashMap<Integer, UriTableMapping>();
		uriTableMap.put(TIMESTAMP, Timestamps.URI_TABLE_MAPPING);
		uriTableMap.put(DAY, Days.URI_TABLE_MAPPING);
		uriTableMap.put(MONTH, Months.URI_TABLE_MAPPING);
		uriTableMap.put(WEEK, Weeks.URI_TABLE_MAPPING);

		uriTableMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriContentTypeMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		for (Integer type : uriTableMap.keySet()) {
			String contentItemName = uriTableMap.get(type).contentItemName;
			uriTableMatcher.addURI(AUTHORITY, contentItemName, type);
			uriTableMatcher.addURI(AUTHORITY, contentItemName + "/#", type);
			uriContentTypeMatcher.addURI(AUTHORITY, contentItemName, CONTENT);
			uriContentTypeMatcher.addURI(AUTHORITY, contentItemName + "/#", CONTENT_ITEM);
		}

	}

}
