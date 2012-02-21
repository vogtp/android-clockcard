package ch.almana.android.stechkarte.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Days;

public class DayAccess implements IModelAccess {
	private static final String LOG_TAG = Logger.TAG;

	private static SimpleDateFormat dayRefDateFormat = new SimpleDateFormat("yyyyMMdd");

	private final Context context;

	private static DayAccess instance;
	public static final float HOURS_IN_MILLIES = 1000f * 60f * 60f;

	public static void initInstance(Context context) {
		instance = new DayAccess(context);
	}

	public static DayAccess getInstance() {
		return instance;
	}

	public DayAccess(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = getContext().getContentResolver().delete(uri, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	public int deleteTimestamps(Day day) {
		Cursor c = null;
		int delRows = 0;
		try {
			c = day.getTimestamps();
			while (c.moveToNext()) {
				// delete timestamp
				delRows += TimestampAccess.getInstance().delete(c);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return delRows;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret = getContext().getContentResolver().insert(uri, initialValues);
		getContext().getContentResolver().notifyChange(ret, null);
		return ret;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return getContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return getContext().getContentResolver().update(uri, values, selection, selectionArgs);
	}

	public void insert(Day day) { 
		Uri uri = insert(DB.Days.CONTENT_URI, day.getValues());
		long id = ContentUris.parseId(uri);
		if (id > 0) {
			day.setId(id);
		}
		MonthAccess.getInstance().recalculate(day.getMonthRef());
	}

	public boolean hasDayRef(long dayref) {
		Cursor c = null;
		try {
			c = query(Days.CONTENT_URI, DB.Days.PROJECTTION_DAYREF, DB.Days.NAME_DAYREF + "=" + dayref, null, DB.Days.DEFAULT_SORTORDER);
			return c.moveToFirst();
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}

		}
	}

	@Override
	public Cursor query(String selection) {
		return query(selection, Days.DEFAULT_SORTORDER);
	}

	public Cursor query(String selection, String sortOrder) {
		return query(DB.Days.CONTENT_URI, DB.Days.DEFAULT_PROJECTION, selection, null, sortOrder);
	}

	public Day getOrCreateDay(long dayref) {
		Day d;
		Cursor c = null;
		try {
			c = query(Days.NAME_DAYREF + "=" + dayref);
			if (c.moveToFirst()) {
				d = new Day(c);
			} else {
				d = new Day(dayref);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return d;
	}

	public void insertOrUpdate(Day day) {
		if (day.getId() > -1) {
			update(day);
		} else {
			insert(day);
		}
	}

	public void update(Day day) {
		update(Days.CONTENT_URI, day.getValues(), DB.NAME_ID + "=" + day.getId(), null);
		MonthAccess.getInstance().recalculate(day.getMonthRef());
		WeekAccess.getInstance().recalculate(day.getWeekRef());
	}


	public static long dayRefFromTimestamp(long timestamp) {
		return dayRefFromDate(new Date(timestamp));
	}

	public static long dayRefFromDate(Date date) {
		String timeString = dayRefDateFormat.format(date);
		return Long.parseLong(timeString);
	}

	public static long timestampFromDayRef(long dayref) {
		try {
			return dayRefDateFormat.parse(dayref + "").getTime();
		} catch (ParseException e) {
			Log.w(LOG_TAG, "Cannot parse " + dayref + " as dayref", e);
			return 0;
		}
	}

	public static long getNextFreeDayref(long timestamp) {
		long dayref = dayRefFromTimestamp(timestamp);
		while (exists(dayref)) {
			dayref++;
		}
		return dayref;
	}

	private static boolean exists(long dayref) {
		Cursor c = null;
		try {
			c = getInstance().query(Days.NAME_DAYREF + "=" + dayref);
			if (c != null) {
				return c.moveToFirst();
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}

	/**
	 * 
	 * @param currentDay
	 * @return the day before or null if none exists
	 */
	public Day getDayBefore(Day currentDay) {
		Day d = null;
		Cursor c = query(Days.NAME_DAYREF + "<" + currentDay.getDayRef(), Days.DEFAULT_SORTORDER);
		if (c != null && c.moveToFirst()) {
			d = new Day(c);
		}
		if (c != null) {
			c.close();
		}
		return d;
	}
	public Day getOldestUpdatedDay(long since) {
		Day d = null;
		Cursor c = query(Days.NAME_LAST_UPDATED + " > " + since, Days.NAME_DAYREF + " ASC");
		if (c != null && c.moveToFirst()) {
			d = new Day(c);
		}
		if (c != null) {
			c.close();
		}
		return d;
	}

}
