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
import ch.almana.android.stechkarte.provider.db.DB.Months;
import ch.almana.android.stechkarte.provider.db.DB.OpenHelper;

public class DBBackendMonth {

	private static HashMap<String, String> sProjectionMap;

	private static final int MONTH = 1;
	private static final int MONTH_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case MONTH:
			count = db.delete(Months.TABLE_NAME, selection, selectionArgs);
			break;

		case MONTH_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(Months.TABLE_NAME, DB.NAME_ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case MONTH:
			return Months.CONTENT_TYPE;

		case MONTH_ID:
			return Months.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Months.CONTENT_ITEM_NAME, MONTH);
		sUriMatcher.addURI(StechkarteProvider.AUTHORITY, Months.CONTENT_ITEM_NAME + "/#", MONTH_ID);

		sProjectionMap = new HashMap<String, String>();
		for (String col : Months.colNames) {
			sProjectionMap.put(col, col);
		}
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != MONTH) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		// if (values.getAsFloat(Months.NAME_HOURS_TARGET) < 0) {
		// FIXME leftover from days
		// values.put(Months.NAME_HOURS_TARGET,
		// Settings.getInstance().getHoursTarget(values.getAsLong(Months.NAME_MONTHREF)));
		// }
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(Months.TABLE_NAME, Months.NAME_MONTHREF, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Months.CONTENT_URI, rowId);

			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(Months.TABLE_NAME);
		qb.setProjectionMap(sProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case MONTH:
			break;

		case MONTH_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Months.DEFAULT_SORTORDER;
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
		case MONTH:
			count = db.update(Months.TABLE_NAME, values, selection, selectionArgs);
			break;

		case MONTH_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(Months.TABLE_NAME, values, DB.NAME_ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}
}
