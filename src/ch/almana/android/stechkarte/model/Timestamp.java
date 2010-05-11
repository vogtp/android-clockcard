package ch.almana.android.stechkarte.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;

public class Timestamp {
	
	public final static int TYPE_UNDEF = -1;
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
		this.timestamp = values.getAsLong(Timestamps.NAME_TIMESTAMP);
		this.timestampType = values.getAsInteger(Timestamps.NAME_TIMESTAMP_TYPE);
		this.id = values.getAsLong(DB.NAME_ID);
		this.dayRef = values.getAsLong(DB.Timestamps.NAME_DAYREF);
	}
	
	public Timestamp(Cursor cursor) {
		super();
		this.id = cursor.getLong(DB.INDEX_ID);
		this.timestamp = cursor.getLong(Timestamps.INDEX_TIMESTAMP);
		this.timestampType = cursor.getInt(Timestamps.INDEX_TIMESTAMP_TYPE);
		
		if (cursor.getColumnCount() > DB.Timestamps.INDEX_DAYREF && !cursor.isNull(DB.Timestamps.INDEX_DAYREF)) {
			this.dayRef = cursor.getLong(DB.Timestamps.INDEX_DAYREF);
		}
	}
	
	public Timestamp(Timestamp timestamp) {
		super();
		this.timestamp = timestamp.getTimestamp();
		this.timestampType = timestamp.timestampType;
		this.id = timestamp.id;
		this.dayRef = timestamp.dayRef;
	}
	
	public Timestamp(Bundle savedInstanceState) {
		super();
		readFromBundle(savedInstanceState);
	}
	
	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Timestamps.NAME_TIMESTAMP, getTimestamp());
		values.put(Timestamps.NAME_TIMESTAMP_TYPE, getTimestampType());
		values.put(Timestamps.NAME_DAYREF, getDayRef());
		return values;
	}
	
	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Timestamps.NAME_TIMESTAMP, getTimestamp());
		bundle.putInt(Timestamps.NAME_TIMESTAMP_TYPE, getTimestampType());
		bundle.putLong(Timestamps.NAME_DAYREF, getDayRef());
	}
	
	public void readFromBundle(Bundle bundle) {
		timestamp = bundle.getLong(Timestamps.NAME_TIMESTAMP);
		timestampType = bundle.getInt(Timestamps.NAME_TIMESTAMP_TYPE);
		dayRef = bundle.getLong(Timestamps.NAME_DAYREF);
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
	
	private static SimpleDateFormat toStingDatetimeFormat = new SimpleDateFormat("HH:mm:ss (dd.MM.yyyy)");
	
	public static String timestampToString(long time) {
		return formatTime(time, toStingDatetimeFormat);
	}
	
	private static String formatTime(long time, SimpleDateFormat datetimeFormat) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return datetimeFormat.format(calendar.getTime());
	}
	
	@Override
	public String toString() {
		return timestampToString(getTimestamp());
	}
	
	private static SimpleDateFormat hmsDatetimeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public String getHMS() {
		return formatTime(getTimestamp(), hmsDatetimeFormat);
	}
	
	private static SimpleDateFormat hmDatetimeFormat = new SimpleDateFormat("HH:mm");
	
	public String getHM() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getTimestamp());
		int sec = calendar.get(Calendar.SECOND);
		if (sec > 30) {
			calendar.add(Calendar.MINUTE, 1);
		}
		return formatTime(calendar.getTimeInMillis(), hmDatetimeFormat);
	}
	
	public static int invertTimestampType(Timestamp timestamp) {
		if (timestamp == null) {
			return TYPE_IN;
		}
		int type = timestamp.getTimestampType() == TYPE_IN ? TYPE_OUT : TYPE_IN;
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
	
	public long getDayRef() {
		return DayAccess.dayRefFromTimestamp(timestamp);
	}
	
	public void setDayRef(long dayRef) {
		this.dayRef = dayRef;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
