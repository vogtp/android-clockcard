package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Days;
import ch.almana.android.stechkarte.provider.DB.Months;

public class Month {

	private long id = -1;
	private long monthRef = 0;
	private float hoursWorked = 0;
	private float hoursTarget = -1;
	private float holyday = 0;
	private float holydayLeft = 0;
	private float overtime = 0;
	private boolean error = false;
	private long lastUpdated = 0;

	public Month() {
		this(MonthAccess.getMonthRefFromTimestamp(System.currentTimeMillis()));
	}

	public Month(long monthRef) {
		super();
		this.monthRef = monthRef;
	}

	public Month(Month day) {
		super();
		id = day.id;
		monthRef = day.monthRef;
		hoursWorked = day.hoursWorked;
		hoursTarget = day.hoursTarget;
		holyday = day.holyday;
		holydayLeft = day.holydayLeft;
		overtime = day.overtime;
		error = day.error;
		lastUpdated = day.lastUpdated;
	}

	public Month(Cursor c) {
		super();
		id = c.getLong(DB.INDEX_ID);
		monthRef = c.getLong(Months.INDEX_MONTHREF);
		hoursWorked = c.getFloat(Months.INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Months.INDEX_HOURS_TARGET);
		holyday = c.getFloat(Months.INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Months.INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Months.INDEX_OVERTIME);
		setError(c.getInt(Months.INDEX_ERROR));
		setLastUpdated(c.getLong(Months.INDEX_LAST_UPDATED));
	}

	public Month(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Months.NAME_MONTHREF, getMonthRef());
		values.put(Months.NAME_HOURS_WORKED, getHoursWorked());
		values.put(Months.NAME_HOURS_TARGET, getHoursTarget());
		values.put(Months.NAME_HOLIDAY, getHolyday());
		values.put(Months.NAME_HOLIDAY_LEFT, getHolydayLeft());
		values.put(Months.NAME_OVERTIME, getOvertime());
		values.put(Months.NAME_ERROR, getError());
		values.put(Months.NAME_LAST_UPDATED, getLastUpdated());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Months.NAME_MONTHREF, getMonthRef());
		bundle.putFloat(Months.NAME_HOURS_WORKED, getHoursWorked());
		bundle.putFloat(Months.NAME_HOURS_TARGET, getHoursTarget());
		bundle.putFloat(Months.NAME_HOLIDAY, getHolyday());
		bundle.putFloat(Months.NAME_HOLIDAY_LEFT, getHolydayLeft());
		bundle.putFloat(Months.NAME_OVERTIME, getOvertime());
		bundle.putInt(Months.NAME_ERROR, getError());
		bundle.putLong(Months.NAME_LAST_UPDATED, getLastUpdated());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.NAME_ID);
		monthRef = bundle.getLong(Months.NAME_MONTHREF);
		hoursWorked = bundle.getFloat(Months.NAME_HOURS_WORKED);
		hoursTarget = bundle.getFloat(Months.NAME_HOURS_TARGET);
		holyday = bundle.getFloat(Months.NAME_HOLIDAY);
		holydayLeft = bundle.getFloat(Months.NAME_HOLIDAY_LEFT);
		overtime = bundle.getFloat(Months.NAME_OVERTIME);
		setError(bundle.getInt(Months.NAME_ERROR));
		lastUpdated = bundle.getLong(Months.NAME_LAST_UPDATED);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Month) {
			Month month = (Month) o;

			return monthRef == month.monthRef && error == month.error && holyday == month.holyday && holydayLeft == month.holydayLeft
					&& hoursTarget == month.hoursTarget
					&& hoursWorked == month.hoursWorked && overtime == month.overtime;
		}
		return super.equals(o);
	}

	public Cursor getDays() {
		return DayAccess.getInstance().query(Days.NAME_MONTHREF + "=" + monthRef, Days.REVERSE_SORTORDER);
	}

	public long getMonthRef() {
		return monthRef;
	}

	public void setMonthRef(long monthRef) {
		this.monthRef = monthRef;
	}

	public float getHoursWorked() {
		return hoursWorked;
	}

	public void setHoursWorked(float hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public float getHoursTarget() {
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

	public String getMonthString() {
		return getMonthRef() + "";
	}

	//
	// public void setYear(int year) {
	// monthRef = year * 100 + getMonthNotZeroBased() ;
	// }
	//
	// public void setMonth(int monthOfYear) {
	// monthRef = getYear() * 100 + (monthOfYear + 1) ;
	// }
	//
	// public int getYear() {
	// String s = monthRef + "";
	// int i = Integer.parseInt(s.substring(0, 4));
	// return i;
	// }
	//
	// public int getMonth() {
	// return getMonthNotZeroBased() - 1;
	// }
	//
	// private int getMonthNotZeroBased() {
	// String s = monthRef + "";
	// String substring = s.substring(4, 6);
	// int i = Integer.parseInt(substring);
	// return i;
	// }

	public void setLastUpdated(long currentTimeMillis) {
		this.lastUpdated = currentTimeMillis;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

}
