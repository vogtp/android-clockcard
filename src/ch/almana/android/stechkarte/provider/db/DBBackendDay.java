package ch.almana.android.stechkarte.provider.db;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ch.almana.android.stechkarte.provider.StechkarteProvider;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.provider.db.DB.OpenHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class DBBackendDay {

	private static HashMap<String, String> sProjectionMap;

	private static final int DAY = 1;
	private static final int DAY_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DAY:
			count = db.delete(Days.TABLE_NAME, selection, selectionArgs);
			break;

		case DAY_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(Days.TABLE_NAME, DB.NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DAY:
			return Days.CONTENT_TYPE;

		case DAY_ID:
			return Days.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Days.CONTENT_ITEM_NAME, DAY);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Days.CONTENT_ITEM_NAME + "/#", DAY_ID);

		sProjectionMap = new HashMap<String, String>();
		for (String col : Days.colNames) {
			sProjectionMap.put(col, col);
		}
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != DAY) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (values.getAsFloat(Days.NAME_HOURS_TARGET) < 0) {
			values.put(Days.NAME_HOURS_TARGET, Settings.getInstance().getHoursTarget());
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(Days.TABLE_NAME, Days.NAME_DAYREF, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Days.CONTENT_URI, rowId);

			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(Days.TABLE_NAME);
		qb.setProjectionMap(sProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case DAY:
			break;

		case DAY_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Days.DEFAULT_SORTORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		return c;
	}

	public static int update(OpenHelper openHelper, Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DAY:
			count = db.update(Days.TABLE_NAME, values, selection, selectionArgs);
			break;

		case DAY_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(Days.TABLE_NAME, values, DB.NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}
}
