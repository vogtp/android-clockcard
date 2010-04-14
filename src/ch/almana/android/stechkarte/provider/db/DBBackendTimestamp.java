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
import ch.almana.android.stechkarte.provider.db.DB.OpenHelper;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;

public class DBBackendTimestamp {

	private static HashMap<String, String> sTimestampProjectionMap;

	private static final int TIMESTAMP = 1;
	private static final int TIMESTAMP_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			count = db.delete(DB.Timestamps.TABLE_NAME, selection, selectionArgs);
			break;

		case TIMESTAMP_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(DB.Timestamps.TABLE_NAME, DB.NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return DB.Timestamps.CONTENT_TYPE;

		case TIMESTAMP_ID:
			return DB.Timestamps.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Timestamps.CONTENT_ITEM_NAME, TIMESTAMP);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Timestamps.CONTENT_ITEM_NAME + "/#", TIMESTAMP_ID);

		sTimestampProjectionMap = new HashMap<String, String>();
		for (String col : Timestamps.colNames) {
			sTimestampProjectionMap.put(col, col);
		}
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(DB.Timestamps.TABLE_NAME);
		qb.setProjectionMap(sTimestampProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			break;

		case TIMESTAMP_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = DB.Timestamps.DEFAUL_SORTORDER;
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
		case TIMESTAMP:
			count = db.update(DB.Timestamps.TABLE_NAME, values, selection, selectionArgs);
			break;

		case TIMESTAMP_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(DB.Timestamps.TABLE_NAME, values, DB.NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != TIMESTAMP) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(DB.Timestamps.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Timestamps.CONTENT_URI, rowId);
			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}
}
