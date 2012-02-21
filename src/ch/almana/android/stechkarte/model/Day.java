package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Days;
import ch.almana.android.stechkarte.provider.DB.Timestamps;
import ch.almana.android.stechkarte.utils.Settings;

public class Day {

	private long id = -1;
	private long dayRef = 0;
	private float hoursWorked = 0;
	private float hoursTarget = -1;
	private float holyday = 0;
	private float holydayLeft = 0;
	private float overtime = 0;
	private boolean error = false;
	private boolean fixed = false;
	private long lastUpdated = 0;
	private long monthRef = 0;
	private long weekRef = 0;
	private String comment;

	public Day() {
		this(DayAccess.dayRefFromTimestamp(System.currentTimeMillis()));
	}

	public Day(long dayRef) {
		super();
		this.dayRef = dayRef;
		this.hoursTarget = Settings.getInstance().getHoursTarget(dayRef);
	}

	public Day(Day day) {
		super();
		id = day.id;
		dayRef = day.dayRef;
		hoursWorked = day.hoursWorked;
		hoursTarget = day.hoursTarget;
		holyday = day.holyday;
		holydayLeft = day.holydayLeft;
		overtime = day.overtime;
		error = day.error;
		fixed = day.fixed;
		lastUpdated = day.lastUpdated;
		monthRef = day.monthRef;
		setWeekRef(day.getWeekRef());
		comment = day.comment;
	}

	public Day(Cursor c) {
		super();
		id = c.getLong(DB.INDEX_ID);
		dayRef = c.getLong(Days.INDEX_DAYREF);
		hoursWorked = c.getFloat(Days.INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Days.INDEX_HOURS_TARGET);
		holyday = c.getFloat(Days.INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Days.INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Days.INDEX_OVERTIME);
		comment = c.getString(Days.INDEX_COMMENT);
		setError(c.getInt(Days.INDEX_ERROR));
		setFixed(c.getInt(Days.INDEX_FIXED));
		setLastUpdated(c.getLong(Days.INDEX_LAST_UPDATED));
		setMonthRef(c.getLong(Days.INDEX_MONTHREF));
		setWeekRef(c.getLong(Days.INDEX_WEEKREF));
	}

	public Day(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Days.NAME_DAYREF, getDayRef());
		values.put(Days.NAME_HOURS_WORKED, getHoursWorked());
		values.put(Days.NAME_HOURS_TARGET, getHoursTarget());
		values.put(Days.NAME_HOLIDAY, getHolyday());
		values.put(Days.NAME_HOLIDAY_LEFT, getHolydayLeft());
		values.put(Days.NAME_OVERTIME, getOvertime());
		values.put(Days.NAME_ERROR, getError());
		values.put(Days.NAME_FIXED, getFixed());
		values.put(Days.NAME_LAST_UPDATED, getLastUpdated());
		values.put(Days.NAME_MONTHREF, getMonthRef());
		values.put(Days.NAME_WEEKREF, getWeekRef());
		values.put(Days.NAME_COMMENT, getComment());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Days.NAME_DAYREF, getDayRef());
		bundle.putFloat(Days.NAME_HOURS_WORKED, getHoursWorked());
		bundle.putFloat(Days.NAME_HOURS_TARGET, getHoursTarget());
		bundle.putFloat(Days.NAME_HOLIDAY, getHolyday());
		bundle.putFloat(Days.NAME_HOLIDAY_LEFT, getHolydayLeft());
		bundle.putFloat(Days.NAME_OVERTIME, getOvertime());
		bundle.putInt(Days.NAME_ERROR, getError());
		bundle.putInt(Days.NAME_FIXED, getFixed());
		bundle.putLong(Days.NAME_LAST_UPDATED, getLastUpdated());
		bundle.putLong(Days.NAME_MONTHREF, getMonthRef());
		bundle.putLong(Days.NAME_WEEKREF, getWeekRef());
		bundle.putString(Days.NAME_COMMENT, getComment());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.NAME_ID);
		dayRef = bundle.getLong(Days.NAME_DAYREF);
		hoursWorked = bundle.getFloat(Days.NAME_HOURS_WORKED);
		hoursTarget = bundle.getFloat(Days.NAME_HOURS_TARGET);
		holyday = bundle.getFloat(Days.NAME_HOLIDAY);
		holydayLeft = bundle.getFloat(Days.NAME_HOLIDAY_LEFT);
		overtime = bundle.getFloat(Days.NAME_OVERTIME);
		setError(bundle.getInt(Days.NAME_ERROR));
		setFixed(bundle.getInt(Days.NAME_FIXED));
		lastUpdated = bundle.getLong(Days.NAME_LAST_UPDATED);
		monthRef = bundle.getLong(Days.NAME_MONTHREF);
		weekRef = bundle.getLong(Days.NAME_WEEKREF);
		comment = bundle.getString(Days.NAME_COMMENT);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + (int) (dayRef ^ (dayRef >>> 32));
		result = prime * result + (error ? 1231 : 1237);
		result = prime * result + (fixed ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(holyday);
		result = prime * result + Float.floatToIntBits(holydayLeft);
		result = prime * result + Float.floatToIntBits(hoursTarget);
		result = prime * result + Float.floatToIntBits(hoursWorked);
		result = prime * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
		result = prime * result + (int) (monthRef ^ (monthRef >>> 32));
		result = prime * result + Float.floatToIntBits(overtime);
		result = prime * result + (int) (weekRef ^ (weekRef >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Day other = (Day) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (dayRef != other.dayRef)
			return false;
		if (error != other.error)
			return false;
		if (fixed != other.fixed)
			return false;
		if (Float.floatToIntBits(holyday) != Float.floatToIntBits(other.holyday))
			return false;
		if (Float.floatToIntBits(holydayLeft) != Float.floatToIntBits(other.holydayLeft))
			return false;
		if (Float.floatToIntBits(hoursTarget) != Float.floatToIntBits(other.hoursTarget))
			return false;
		if (Float.floatToIntBits(hoursWorked) != Float.floatToIntBits(other.hoursWorked))
			return false;
		if (lastUpdated != other.lastUpdated)
			return false;
		if (monthRef != other.monthRef)
			return false;
		if (Float.floatToIntBits(overtime) != Float.floatToIntBits(other.overtime))
			return false;
		if (weekRef != other.weekRef)
			return false;
		return true;
	}

	public Cursor getTimestamps() {
		return TimestampAccess.getInstance().query(Timestamps.NAME_DAYREF + "=" + dayRef, Timestamps.REVERSE_SORTORDER);
	}

	public long getDayRef() {
		return dayRef;
	}

	public void setDayRef(long dayRef) {
		this.dayRef = dayRef;
	}

	public float getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(float hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public float getHoursTarget() {
		if (hoursTarget < 0) {
			hoursTarget = Settings.getInstance().getHoursTarget(dayRef);
		}
		return hoursTarget;
	}

	public void setHoursTarget(float hoursTarget) {
		this.hoursTarget = hoursTarget;
	}

	public float getHolyday() {
		return holyday;
	}

	public void setHolyday(float holyday) {
		this.holyday = holyday;
	}

	public float getHolydayLeft() {
		return holydayLeft;
	}

	public void setHolydayLeft(float holydayLeft) {
		this.holydayLeft = holydayLeft;
	}

	public float getDayOvertime() {
		return hoursWorked - hoursTarget;
	}

	public float getOvertime() {
		return overtime;
	}

	public void setOvertime(float overtime) {
		this.overtime = overtime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setError(int error) {
		this.error = error > 0 ? true : false;
	}

	public int getError() {
		return isError() ? 1 : 0;
	}

	public boolean isError() {
		return error;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed > 0 ? true : false;
	}

	public int getFixed() {
		return fixed ? 1 : 0;
	}

	public long getMonthRef() {
		if (monthRef < 1) {
			monthRef = MonthAccess.getMonthRefFromDayRef(dayRef);
		}
		return monthRef;
	}

	public long getWeekRef() {
		if (weekRef < 1) {
			weekRef = WeekAccess.getWeekRefFromDayRef(dayRef);
		}
		return weekRef;
	}

	public void setMonthRef(long monthRef) {
		this.monthRef = monthRef;
	}

	public String getDayString() {
		return getDayRef() + "";
	}

	public void setYear(int year) {
		dayRef = year * 10000 + getMonthNotZeroBased() * 100 + getDay();
	}

	public void setDay(int dayOfMonth) {
		dayRef = getYear() * 10000 + getMonthNotZeroBased() * 100 + dayOfMonth;
	}

	public void setMonth(int monthOfYear) {
		dayRef = getYear() * 10000 + (monthOfYear + 1) * 100 + getDay();
	}

	public int getYear() {
		String s = dayRef + "";
		int i = Integer.parseInt(s.substring(0, 4));
		return i;
	}

	public int getMonth() {
		return getMonthNotZeroBased() - 1;
	}

	private int getMonthNotZeroBased() {
		String s = dayRef + "";
		String substring = s.substring(4, 6);
		int i = Integer.parseInt(substring);
		return i;
	}

	public int getDay() {
		String s = dayRef + "";
		String substring = s.substring(6, 8);
		int i = Integer.parseInt(substring);
		return i;
	}

	public void setLastUpdated(long currentTimeMillis) {
		this.lastUpdated = currentTimeMillis;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setWeekRef(long weekRef) {
		this.weekRef = weekRef;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isToday() {
		return dayRef == DayAccess.dayRefFromTimestamp(System.currentTimeMillis());
	}

}
