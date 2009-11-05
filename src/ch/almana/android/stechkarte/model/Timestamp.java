package ch.almana.android.stechkarte.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class Timestamp {

	public final static int TYPE_IN = 0;
	public final static int TYPE_OUT = 1;

	private long timestamp;
	private int timestampType;
	private long id = -1;
	private long dayRef;
	private Calendar cal;

	@Override
	public boolean equals(Object o) {
		if (o instanceof Timestamp) {
			Timestamp ts = (Timestamp) o;
			return timestamp == ts.timestamp && timestampType == ts.timestampType;
		}
		return super.equals(o);
	}

	public Timestamp(long timestamp, int timestampType) {
		super();
		this.timestamp = timestamp;
		this.timestampType = timestampType;
	}

	public Timestamp(ContentValues values) {
		super();
		this.timestamp = values.getAsLong(Timestamps.COL_NAME_TIMESTAMP);
		this.timestampType = values.getAsInteger(Timestamps.COL_NAME_TIMESTAMP_TYPE);
		this.id = values.getAsLong(DB.COL_NAME_ID);
		this.dayRef = values.getAsLong(DB.Timestamps.COL_NAME_DAYREF);
	}

	public Timestamp(Cursor cursor) {
		super();
		this.id = cursor.getLong(DB.COL_INDEX_ID);
		this.timestamp = cursor.getLong(Timestamps.COL_INDEX_TIMESTAMP);
		this.timestampType = cursor.getInt(Timestamps.COL_INDEX_TIMESTAMP_TYPE);
		if (!cursor.isNull(DB.Timestamps.COL_INDEX_DAYREF)) {
			this.dayRef = cursor.getLong(DB.Timestamps.COL_INDEX_DAYREF);
		}
	}

	public Timestamp(Timestamp timestamp) {
		this.timestamp = timestamp.getTimestamp();
		this.timestampType = timestamp.timestampType;
		this.id = timestamp.id;
		this.dayRef = timestamp.dayRef;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		if (cal != null) {
			cal.setTimeInMillis(timestamp);
		}
	}

	public int getTimestampType() {
		return timestampType;
	}

	public void setTimestampType(int timestampType) {
		this.timestampType = timestampType;
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.COL_NAME_ID, id);
		}
		values.put(Timestamps.COL_NAME_TIMESTAMP, getTimestamp());
		values.put(Timestamps.COL_NAME_TIMESTAMP_TYPE, getTimestampType());
		values.put(Timestamps.COL_NAME_DAYREF, getDayRef());
		return values;
	}

	@Override
	public String toString() {
		return formatTime();
	}

	private static SimpleDateFormat simpleDatetimeFormat = new SimpleDateFormat("HH:mm (dd.MM.yyyy)");
	public static String formatTime(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		String timeString = simpleDatetimeFormat.format(calendar.getTime());
		return timeString;
	}

	public String formatTime() {
		return formatTime(getTimestamp());
	}

	public static int invertTimestampType(Timestamp timestamp) {
		int type = timestamp.getTimestampType() == Timestamp.TYPE_IN ? Timestamp.TYPE_OUT : Timestamp.TYPE_IN;
		return type;
	}

	private Calendar getCalendar() {
		// if (cal == null) {
			cal = Calendar.getInstance();
			cal.setTimeInMillis(timestamp);
		// }
		return cal;
	}

	public void setHour(int hourOfDay) {
		Calendar c = getCalendar();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		timestamp = c.getTimeInMillis();
	}


	public void setMinute(int minute) {
		Calendar c = getCalendar();
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		timestamp = c.getTimeInMillis();
	}

	public Integer getHour() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}

	public Integer getMinute() {
		return getCalendar().get(Calendar.MINUTE);
	}

	public String getTimestampTypeAsString(Context context) {
		return getTimestampTypeAsString(context, getTimestampType());
	}

	public static String getTimestampTypeAsString(Context context, int timestampType) {
		if (timestampType == Timestamp.TYPE_IN) {
			return context.getString(R.string.TimestampTypeIn);
		} else if (timestampType == Timestamp.TYPE_OUT) {
			return context.getString(R.string.TimestampTypeOut);
		}
		return context.getString(android.R.string.untitled);
	}

	public String formatTimeDateOnly() {
		return formatTimeDateOnly(getTimestamp());
	}

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public static String formatTimeDateOnly(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		String timeString = simpleDateFormat.format(calendar.getTime());
		return timeString;
	}

	public int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}

	public int getMonth() {
		return getCalendar().get(Calendar.MONTH);
	}

	public int getDay() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public void setYear(int year) {
		Calendar c = getCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		timestamp = c.getTimeInMillis();

	}

	public void setMonth(int monthOfYear) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		timestamp = c.getTimeInMillis();
	}

	public void setDay(int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		timestamp = c.getTimeInMillis();
	}

	public long getId() {
		return id;
	}

	private static SimpleDateFormat dayRefDateFormat = new SimpleDateFormat("yyyyMMdd");

	public long getDayRef() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		String timeString = dayRefDateFormat.format(calendar.getTime());
		return Long.parseLong(timeString);
	}

	public void setDayRef(long dayRef) {
		this.dayRef = dayRef;
	}

	public void setId(long id) {
		this.id = id;
	}
}
