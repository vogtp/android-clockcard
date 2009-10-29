package ch.almana.android.stechkarte.model;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import ch.almana.android.stechkarte.model.DB.Days;
import ch.almana.android.stechkarte.provider.IAccess;
import ch.almana.android.stechkarte.provider.StechkarteProvider;

public class DayAccess implements IAccess {
	private static final String LOG_TAG = "DayAccess";

	private static HashMap<String, String> sProjectionMap;

	private static final int DAY = 1;
	private static final int DAY_ID = 2;

	private static final UriMatcher sUriMatcher;

	private DB.OpenHelper mOpenHelper;
	private Context context;

	private static DayAccess instance;

	public static DayAccess getInstance(Context context) {
		// if (instance == null) {
			instance = new DayAccess(context);
		// }
		return instance;
	}

	public DayAccess(Context context) {
		super();
		this.context = context;
		mOpenHelper = new DB.OpenHelper(context);
		// upgrade DB
		mOpenHelper.getWritableDatabase();
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DAY:
			count = db.delete(Days.TABLE_NAME, selection, selectionArgs);
			break;

		case DAY_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(Days.TABLE_NAME, DB.COL_NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DAY:
			return Days.CONTENT_TYPE;

		case DAY_ID:
			return Days.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
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
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(Days.TABLE_NAME, Days.COL_NAME_DAYREF, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Days.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(retUri, null);
			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(Days.TABLE_NAME);
		qb.setProjectionMap(sProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case DAY:
			break;

		case DAY_ID:
			qb.appendWhere(DB.COL_NAME_ID + "=" + uri.getPathSegments().get(1));
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
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DAY:
			count = db.update(Days.TABLE_NAME, values, selection, selectionArgs);
			break;

		case DAY_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(Days.TABLE_NAME, values, DB.COL_NAME_ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public void insert(Day day) {
		// FIXME handle last day
		insert(DB.Days.CONTENT_URI, day.getValues());
	}

	public boolean hasDayRef(long dayref) {
		Cursor c = query(Days.CONTENT_URI, DB.Days.PROJECTTION_DAYREF, DB.Days.COL_NAME_DAYREF + "=" + dayref, null,
				DB.Days.DEFAULT_SORTORDER);
		return c.moveToFirst();
	}

	public Cursor query(String selection) {
		return query(selection, Days.DEFAULT_SORTORDER);
	}

	public Cursor query(String selection, String sortOrder) {
		return query(DB.Days.CONTENT_URI, DB.Days.DEFAULT_PROJECTION, selection, null, sortOrder);
	}



	public Day getOrCreateDay(long dayref) {
		Cursor c = query(Days.COL_NAME_DAYREF + "=" + dayref);
		if (c.moveToFirst()) {
			return new Day(c);
		} else {
			return new Day(dayref);
		}
	}

	public void insertOrUpdate(Day day) {
		if (day.getId() > -1) {
			update(day);
		} else {
			insert(day);
		}
	}

	private void update(Day day) {
		update(Days.CONTENT_URI, day.getValues(), DB.COL_NAME_ID + "=" + day.getId(), null);
	}

	/**
	 * @param timestamp
	 *            Timestamp to recalculate or null to work on all days
	 */
	public void recalculateDay(Timestamp timestamp) {
		String selection = null;
		if (timestamp != null) {
			selection = DB.COL_NAME_ID + "=" + timestamp.getId();
		}
		DayAccess dayAccess = DayAccess.getInstance(getContext());
		Cursor c = query(selection);
		while (c.moveToNext()) {
			Timestamp ts = new Timestamp(c);
			long dayref = ts.getDayRef();
			Day curDay = dayAccess.getOrCreateDay(dayref);
			Day prevDay = dayAccess.getDayBefore(curDay);
			if (prevDay != null) {
				curDay.recalculate(prevDay);
			}
			// insert anyway since it might be the first day
			dayAccess.insertOrUpdate(curDay);
			Log.i(LOG_TAG, "Recalculated day " + dayref);
		}
	}

	/**
	 * 
	 * @param currentDay
	 * @return the day before or null if none exists
	 */
	private Day getDayBefore(Day currentDay) {
		Cursor c = query(Days.COL_NAME_DAYREF + "<" + currentDay.getDayRef(), Days.REVERSE_SORTORDER);
		if (c.moveToFirst()) {
			return new Day(c);
		}
		return null;
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
}
