package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.model.DB.Days;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class Day {


	private long id = -1;
	private long dayRef = 0;
	private float hoursWorked = 0;
	private float hoursTarget = DayAccess.getHoursTargetDefault();
	private float holyday = 0;
	private float holydayLeft = 0;
	private float overtime = 0;
	private boolean error = false;
	private boolean fixed = false;


	public Day(long dayRef) {
		super();
		this.dayRef = dayRef;
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
	}

	public Day(Cursor c) {
		super();
		id = c.getLong(DB.COL_INDEX_ID);
		dayRef = c.getLong(Days.COL_INDEX_DAYREF);
		hoursWorked = c.getFloat(Days.COL_INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Days.COL_INDEX_HOURS_TARGET);
		holyday = c.getFloat(Days.COL_INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Days.COL_INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Days.COL_INDEX_OVERTIME);
		setError(c.getInt(Days.COL_INDEX_ERROR));
		setFixed(c.getInt(Days.COL_INDEX_FIXED));

	}


	public Day(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.COL_NAME_ID, id);
		}
		values.put(Days.COL_NAME_DAYREF, getDayRef());
		values.put(Days.COL_NAME_HOURS_WORKED, getHoursWorked());
		values.put(Days.COL_NAME_HOURS_TARGET, getHoursTarget());
		values.put(Days.COL_NAME_HOLIDAY, getHolyday());
		values.put(Days.COL_NAME_HOLIDAY_LEFT, getHolydayLeft());
		values.put(Days.COL_NAME_OVERTIME, getOvertime());
		values.put(Days.COL_NAME_ERROR, getError());
		values.put(Days.COL_NAME_FIXED, getFixed());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.COL_NAME_ID, id);
		} else {
			bundle.putLong(DB.COL_NAME_ID, -1);
		}
		bundle.putLong(Days.COL_NAME_DAYREF, getDayRef());
		bundle.putFloat(Days.COL_NAME_HOURS_WORKED, getHoursWorked());
		bundle.putFloat(Days.COL_NAME_HOURS_TARGET, getHoursTarget());
		bundle.putFloat(Days.COL_NAME_HOLIDAY, getHolyday());
		bundle.putFloat(Days.COL_NAME_HOLIDAY_LEFT, getHolydayLeft());
		bundle.putFloat(Days.COL_NAME_OVERTIME, getOvertime());
		bundle.putInt(Days.COL_NAME_ERROR, getError());
		bundle.putInt(Days.COL_NAME_FIXED, getFixed());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.COL_NAME_ID);
		dayRef = bundle.getLong(Days.COL_NAME_DAYREF);
		hoursWorked = bundle.getFloat(Days.COL_NAME_HOURS_WORKED);
		hoursTarget = bundle.getFloat(Days.COL_NAME_HOURS_TARGET);
		holyday = bundle.getFloat(Days.COL_NAME_HOLIDAY);
		holydayLeft = bundle.getFloat(Days.COL_NAME_HOLIDAY_LEFT);
		overtime = bundle.getFloat(Days.COL_NAME_OVERTIME);
		setError(bundle.getInt(Days.COL_NAME_ERROR));
		setFixed(bundle.getInt(Days.COL_NAME_FIXED));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Day) {
			Day day = (Day) o;

			return dayRef == day.dayRef && error == day.error && fixed == day.fixed && holyday == day.holyday
					&& holydayLeft == day.holydayLeft && hoursTarget == day.hoursTarget
					&& hoursWorked == day.hoursWorked && overtime == day.overtime;
		}
		return super.equals(o);
	}

	public Cursor getTimestamps(Context context) {
		return TimestampAccess.getInstance(context).query(Timestamps.COL_NAME_DAYREF + "=" + dayRef,
				Timestamps.REVERSE_SORTORDER);
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

	public String getDayString() {
		return getDayRef() + "";
	}

}
