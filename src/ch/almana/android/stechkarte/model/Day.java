package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.almana.android.stechkarte.model.DB.Days;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class Day {

	private float hoursTargetDefault = 8.24f;

	private int id = -1;
	private long dayRef = 0;
	private float hoursWorked = 0;
	private float hoursTarget = hoursTargetDefault;
	private float holyday = 0;
	private float holydayLeft = 0;
	private float overtime = 0;
	private float overtimeCompensation = 0;

	private static final float HOURS_IN_MILLIES = 1000 * 60 * 60;


	public Day(long dayRef) {
		super();
		this.dayRef = dayRef;
	}

	public Day(Cursor c) {
		id = c.getInt(DB.COL_INDEX_ID);
		dayRef = c.getLong(Days.COL_INDEX_DAYREF);
		hoursWorked = c.getFloat(Days.COL_INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Days.COL_INDEX_HOURS_TARGET);
		holyday = c.getFloat(Days.COL_INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Days.COL_INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Days.COL_INDEX_OVERTIME);
		overtimeCompensation = c.getFloat(Days.COL_INDEX_OVERTIME);
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
		values.put(Days.COL_NAME_OVERTIME_COMPENSATION, getOvertimeCompensation());
		return values;
	}

	public void recalculate(Context context, Day previousDay) {
		if (previousDay == null) {
			previousDay = new Day(0);
		}
		float hoursWorked = 0;
		// calculate for timestamps
		Cursor c = getTimestamps(context);
		while (c.moveToNext()) {
			// what a timestamp is in an other day?
			Timestamp t1 = new Timestamp(c);
			if (t1.getTimestampType() == Timestamp.TYPE_IN) {
				if (c.moveToNext()) {
					Timestamp t2 = new Timestamp(c);
					float diff = (t2.getTimestamp() - t1.getTimestamp()) / HOURS_IN_MILLIES;
					hoursWorked = hoursWorked + diff;
				} else {
					setError();
				}
			} else {
				setError();
			}
		}
		c.close();

		this.hoursTarget = hoursTargetDefault - overtimeCompensation - holyday * hoursTargetDefault;
		this.overtime = previousDay.getOvertime() + hoursWorked - hoursTarget;
		this.hoursWorked = hoursWorked;
		this.holydayLeft = previousDay.getHolydayLeft() - holyday;
	}

	private void setError() {
		// FIXME Auto-generated method stub

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

	public float getOvertimeCompensation() {
		return overtimeCompensation;
	}

	public void setOvertimeCompensation(float overtimeCompensation) {
		this.overtimeCompensation = overtimeCompensation;
	}


	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}


}
