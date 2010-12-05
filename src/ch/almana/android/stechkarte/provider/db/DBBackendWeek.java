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
import ch.almana.android.stechkarte.provider.db.DB.Weeks;

public class DBBackendWeek {

	private static HashMap<String, String> sProjectionMap;

	private static final int WEEK = 1;
	private static final int WEEK_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case WEEK:
			count = db.delete(Weeks.TABLE_NAME, selection, selectionArgs);
			break;

		case WEEK_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(Weeks.TABLE_NAME, DB.NAME_ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case WEEK:
			return Weeks.CONTENT_TYPE;

		case WEEK_ID:
			return Weeks.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Weeks.CONTENT_ITEM_NAME, WEEK);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Weeks.CONTENT_ITEM_NAME + "/#", WEEK_ID);

		sProjectionMap = new HashMap<String, String>();
		for (String col : Weeks.colNames) {
			sProjectionMap.put(col, col);
		}
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != WEEK) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		// if (values.getAsFloat(Weeks.NAME_HOURS_TARGET) < 0) {
		// FIXME leftover from days
		// values.put(Weeks.NAME_HOURS_TARGET,
		// Settings.getInstance().getHoursTarget(values.getAsLong(Weeks.NAME_MONTHREF)));
		// }
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(Weeks.TABLE_NAME, Weeks.NAME_WEEKREF, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Weeks.CONTENT_URI, rowId);

			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(Weeks.TABLE_NAME);
		qb.setProjectionMap(sProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case WEEK:
			break;

		case WEEK_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Weeks.DEFAULT_SORTORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		return c;
	}

	public static int update(OpenHelper openHelper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case WEEK:
			count = db.update(Weeks.TABLE_NAME, values, selection, selectionArgs);
			break;

		case WEEK_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(Weeks.TABLE_NAME, values, DB.NAME_ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}
}
