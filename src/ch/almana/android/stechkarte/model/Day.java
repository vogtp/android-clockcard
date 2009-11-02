package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.almana.android.stechkarte.model.DB.Days;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class Day {


	private int id = -1;
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
		id = c.getInt(DB.COL_INDEX_ID);
		dayRef = c.getLong(Days.COL_INDEX_DAYREF);
		hoursWorked = c.getFloat(Days.COL_INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Days.COL_INDEX_HOURS_TARGET);
		holyday = c.getFloat(Days.COL_INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Days.COL_INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Days.COL_INDEX_OVERTIME);
		setError(c.getInt(Days.COL_INDEX_ERROR));
		setFixed(c.getInt(Days.COL_INDEX_FIXED));

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


	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
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
