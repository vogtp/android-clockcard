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
import ch.almana.android.stechkarte.model.DB.Timestamps;
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
	private static final float HOURS_IN_MILLIES = 1000 * 60 * 60;
	private static float hoursTargetDefault = 8.24f;

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
		insert(DB.Days.CONTENT_URI, day.getValues());
	}

	public boolean hasDayRef(long dayref) {
		Cursor c = null;
		try {
			c = query(Days.CONTENT_URI, DB.Days.PROJECTTION_DAYREF, DB.Days.COL_NAME_DAYREF + "=" + dayref, null,
					DB.Days.DEFAULT_SORTORDER);
			return c.moveToFirst();
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}

		}
	}

	public Cursor query(String selection) {
		return query(selection, Days.DEFAULT_SORTORDER);
	}

	public Cursor query(String selection, String sortOrder) {
		return query(DB.Days.CONTENT_URI, DB.Days.DEFAULT_PROJECTION, selection, null, sortOrder);
	}

	public Day getOrCreateDay(long dayref) {
		Day d;
		Cursor c = query(Days.COL_NAME_DAYREF + "=" + dayref);
		if (c.moveToFirst()) {
			d = new Day(c);
		} else {
			d = new Day(dayref);
		}
		c.close();
		return d;
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
	public void recalculateDayFromTimestamp(Timestamp timestamp) {
		String selection = null;
		if (timestamp != null) {
			selection = DB.COL_NAME_ID + "=" + timestamp.getId();
		}
		DayAccess dayAccess = DayAccess.getInstance(getContext());
		// delete all days
		dayAccess.delete(Days.CONTENT_URI, selection, null);
		Cursor c = TimestampAccess.getInstance(getContext()).query(selection, Timestamps.REVERSE_SORTORDER);
		while (c.moveToNext()) {
			Timestamp ts = new Timestamp(c);
			long dayref = ts.getDayRef();
			Day curDay = dayAccess.getOrCreateDay(dayref);
			ts.setDayRef(curDay.getDayRef());
			TimestampAccess.getInstance(getContext()).update(ts);
			Log.i(LOG_TAG, "Recalculated day " + dayref);
		}
		c.close();
	}

	/**
	 * 
	 * @param currentDay
	 * @return the day before or null if none exists
	 */
	private Day getDayBefore(Day currentDay) {
		Day d = null;
		Cursor c = query(Days.COL_NAME_DAYREF + "<" + currentDay.getDayRef(), Days.REVERSE_SORTORDER);
		if (c.moveToFirst()) {
			d = new Day(c);
		}
		c.close();
		return d;
	}

	public void recalculate(Context context, long dayRef) {
		if (dayRef < 1) {
			return;
		}
		Day day = getOrCreateDay(dayRef);
		Day previousDay = getDayBefore(day);
		if (previousDay == null) {
			previousDay = new Day(0);
		}
		float hoursWorked = 0;
		// calculate for timestamps
		Cursor c = day.getTimestamps(context);
		while (c.moveToNext()) {
			// what a timestamp is in an other day?
			Timestamp t1 = new Timestamp(c);
			if (t1.getTimestampType() == Timestamp.TYPE_IN) {
				if (c.moveToNext()) {
					Timestamp t2 = new Timestamp(c);
					float diff = (t2.getTimestamp() - t1.getTimestamp()) / HOURS_IN_MILLIES;
					hoursWorked = hoursWorked + diff;
				} else {
					day.setError();
				}
			} else {
				day.setError();
			}
		}
		c.close();

		day.setHoursWorked(hoursWorked);
		day.setHoursTarget(getHoursTargetDefault() - day.getOvertimeCompensation() - day.getHolyday()
				* getHoursTargetDefault());
		day.setOvertime(previousDay.getOvertime() + hoursWorked - day.getHoursTarget());
		day.setHolydayLeft(previousDay.getHolydayLeft() - day.getHolyday());
		insertOrUpdate(day);
	}

	public static void setHoursTargetDefault(float hoursTargetDefault) {
		DayAccess.hoursTargetDefault = hoursTargetDefault;
	}

	public static float getHoursTargetDefault() {
		return hoursTargetDefault;
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
