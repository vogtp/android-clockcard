package ch.almana.android.stechkarte.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import ch.almana.android.stechkarte.provider.DB.Timestamps;

public class StechkarteTimestampProvider extends ContentProvider {


	public static final String AUTHORITY = "ch.almana.android.stechkarte";


	private static final String LOG_TAG = "Timestamps";
	private static final int DATABASE_VERSION = 1;


	private static HashMap<String, String> sTimestampProjectionMap;


	private static final int TIMESTAMP = 1;
	private static final int TIMESTAMP_ID = 2;

	private static final UriMatcher sUriMatcher;

	private static final String DATABASE_CREATE = "create table " + DB.Timestamps.TABLE_NAME + " ("
			+ Timestamps.COL_NAME_ID
			+ " integer primary key, " + Timestamps.COL_NAME_TIMESTAMP_TYPE + " int,"
			+ Timestamps.COL_NAME_TIMESTAMP + " long);";

	private DBHelper mOpenHelper;

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			Log.i(LOG_TAG, "Created table " + DB.Timestamps.TABLE_NAME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}



	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			count = db.delete(DB.Timestamps.TABLE_NAME, selection, selectionArgs);
			break;

		case TIMESTAMP_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(DB.Timestamps.TABLE_NAME, Timestamps.COL_NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return DB.Timestamps.CONTENT_TYPE;

		case TIMESTAMP_ID:
			return DB.Timestamps.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
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
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(DB.Timestamps.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(Timestamps.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#onCreate()
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new DBHelper(getContext());
		return true;
	}

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(DB.Timestamps.TABLE_NAME);
		qb.setProjectionMap(sTimestampProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			break;

		case TIMESTAMP_ID:
			qb.appendWhere(Timestamps.COL_NAME_ID + "=" + uri.getPathSegments().get(1));
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
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			count = db.update(DB.Timestamps.TABLE_NAME, values, selection, selectionArgs);
			break;

		case TIMESTAMP_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(DB.Timestamps.TABLE_NAME, values, Timestamps.COL_NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME, TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME + "/#", TIMESTAMP_ID);

		sTimestampProjectionMap = new HashMap<String, String>();
		for (String col : Timestamps.colNames) {
			sTimestampProjectionMap.put(col, col);
		}
	}

}
