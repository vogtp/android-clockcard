package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.provider.db.DB.Weeks;

public class Week {

	private long id = -1;
	private long weekRef = 0;
	private float hoursWorked = 0;
	private float hoursTarget = -1;
	private float holyday = 0;
	private float holydayLeft = 0;
	private float overtime = 0;
	private boolean error = false;
	private long lastUpdated = 0;

	public Week() {
		this(WeekAccess.getWeekRefFromTimestamp(System.currentTimeMillis()));
	}

	public Week(long weekRef) {
		super();
		this.weekRef = weekRef;
	}

	public Week(Week day) {
		super();
		id = day.id;
		weekRef = day.weekRef;
		hoursWorked = day.hoursWorked;
		hoursTarget = day.hoursTarget;
		holyday = day.holyday;
		holydayLeft = day.holydayLeft;
		overtime = day.overtime;
		error = day.error;
		lastUpdated = day.lastUpdated;
	}

	public Week(Cursor c) {
		super();
		id = c.getLong(DB.INDEX_ID);
		weekRef = c.getLong(Weeks.INDEX_WEEKREF);
		hoursWorked = c.getFloat(Weeks.INDEX_HOURS_WORKED);
		hoursTarget = c.getFloat(Weeks.INDEX_HOURS_TARGET);
		holyday = c.getFloat(Weeks.INDEX_HOLIDAY);
		holydayLeft = c.getFloat(Weeks.INDEX_HOLIDAY_LEFT);
		overtime = c.getFloat(Weeks.INDEX_OVERTIME);
		setError(c.getInt(Weeks.INDEX_ERROR));
		setLastUpdated(c.getLong(Weeks.INDEX_LAST_UPDATED));
	}

	public Week(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Weeks.NAME_WEEKREF, getWeekRef());
		values.put(Weeks.NAME_HOURS_WORKED, getHoursWorked());
		values.put(Weeks.NAME_HOURS_TARGET, getHoursTarget());
		values.put(Weeks.NAME_HOLIDAY, getHolyday());
		values.put(Weeks.NAME_HOLIDAY_LEFT, getHolydayLeft());
		values.put(Weeks.NAME_OVERTIME, getOvertime());
		values.put(Weeks.NAME_ERROR, getError());
		values.put(Weeks.NAME_LAST_UPDATED, getLastUpdated());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Weeks.NAME_WEEKREF, getWeekRef());
		bundle.putFloat(Weeks.NAME_HOURS_WORKED, getHoursWorked());
		bundle.putFloat(Weeks.NAME_HOURS_TARGET, getHoursTarget());
		bundle.putFloat(Weeks.NAME_HOLIDAY, getHolyday());
		bundle.putFloat(Weeks.NAME_HOLIDAY_LEFT, getHolydayLeft());
		bundle.putFloat(Weeks.NAME_OVERTIME, getOvertime());
		bundle.putInt(Weeks.NAME_ERROR, getError());
		bundle.putLong(Weeks.NAME_LAST_UPDATED, getLastUpdated());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.NAME_ID);
		weekRef = bundle.getLong(Weeks.NAME_WEEKREF);
		hoursWorked = bundle.getFloat(Weeks.NAME_HOURS_WORKED);
		hoursTarget = bundle.getFloat(Weeks.NAME_HOURS_TARGET);
		holyday = bundle.getFloat(Weeks.NAME_HOLIDAY);
		holydayLeft = bundle.getFloat(Weeks.NAME_HOLIDAY_LEFT);
		overtime = bundle.getFloat(Weeks.NAME_OVERTIME);
		setError(bundle.getInt(Weeks.NAME_ERROR));
		lastUpdated = bundle.getLong(Weeks.NAME_LAST_UPDATED);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Week) {
			Week week = (Week) o;

			return weekRef == week.weekRef && error == week.error && holyday == week.holyday && holydayLeft == week.holydayLeft
					&& hoursTarget == week.hoursTarget
					&& hoursWorked == week.hoursWorked && overtime == week.overtime;
		}
		return super.equals(o);
	}

	public Cursor getDays() {
		return DayAccess.getInstance().query(Days.NAME_WEEKREF + "=" + weekRef, Days.REVERSE_SORTORDER);
	}

	public long getWeekRef() {
		return weekRef;
	}

	public void setweekRef(long weekRef) {
		this.weekRef = weekRef;
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

	public String getWeekString() {
		return getWeekRef() + "";
	}

	public void setLastUpdated(long currentTimeMillis) {
		this.lastUpdated = currentTimeMillis;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

}
