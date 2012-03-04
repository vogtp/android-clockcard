package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Holidays;

public class Holiday {

	enum BorderType {
		allDay, halfDay, restOfDay, specifyed
	}

	private long id = -1;
	private long start;
	private BorderType startType;
	private int startHours;
	private long end;
	private BorderType endType;
	private int endHours;
	private boolean isHoliday;
	private boolean isPaid;
	private boolean isYearly;
	private String comment;



	public Holiday(Holiday timeoff) {
		super();
		id = timeoff.id;
		start = timeoff.start;
		startType = timeoff.startType;
		startHours = timeoff.startHours;
		end = timeoff.end;
		endType = timeoff.endType;
		endHours = timeoff.endHours;
		isHoliday = timeoff.isHoliday;
		isPaid = timeoff.isPaid;
		isYearly = timeoff.isYearly;
		comment = timeoff.comment;
	}

	public Holiday(Cursor c) {
		super();
		id = c.getLong(DB.INDEX_ID);
		start = c.getLong(Holidays.INDEX_START);
		startType = BorderType.valueOf(c.getString(Holidays.INDEX_START_TYPE));
		startHours = c.getInt(Holidays.INDEX_START_HOURS);
		end = c.getLong(Holidays.INDEX_END);
		endType = BorderType.valueOf(c.getString(Holidays.INDEX_END_TYPE));
		endHours = c.getInt(Holidays.INDEX_END_HOURS);
		isHoliday = c.getInt(Holidays.INDEX_IS_HOLIDAY) == 1 ? true : false;
		isPaid = c.getInt(Holidays.INDEX_IS_PAID) == 1 ? true : false;
		isYearly = c.getInt(Holidays.INDEX_IS_YEARLY) == 1 ? true : false;
		comment = c.getString(Holidays.INDEX_COMMENT);
	}

	public Holiday(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Holidays.NAME_START, getStart());
		values.put(Holidays.NAME_START_TYPE, getStartType().toString());
		values.put(Holidays.NAME_START_HOURS, getStartHours());
		values.put(Holidays.NAME_END, getEnd());
		values.put(Holidays.NAME_END_TYPE, getEndType().toString());
		values.put(Holidays.NAME_END_HOURS, getEndHours());
		values.put(Holidays.NAME_IS_HOLIDAY, isHoliday ? 1 : 0);
		values.put(Holidays.NAME_IS_PAID, isPaid ? 1 : 0);
		values.put(Holidays.NAME_IS_YEARLY, isYearly ? 1 : 0);
		values.put(Holidays.NAME_COMMENT, getComment());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Holidays.NAME_START, getStart());
		bundle.putString(Holidays.NAME_START_TYPE, getStartType().toString());
		bundle.putInt(Holidays.NAME_START_HOURS, getStartHours());
		bundle.putLong(Holidays.NAME_END, getEnd());
		bundle.putString(Holidays.NAME_END_TYPE, getEndType().toString());
		bundle.putInt(Holidays.NAME_END_HOURS, getEndHours());
		bundle.putInt(Holidays.NAME_IS_HOLIDAY, isHoliday ? 1 : 0);
		bundle.putInt(Holidays.NAME_IS_PAID, isPaid ? 1 : 0);
		bundle.putInt(Holidays.NAME_IS_YEARLY, isYearly ? 1 : 0);
		bundle.putString(Holidays.NAME_COMMENT, getComment());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.NAME_ID);
		start = bundle.getLong(Holidays.NAME_START);
		startType = BorderType.valueOf(bundle.getString(Holidays.NAME_START_TYPE));
		startHours = bundle.getInt(Holidays.NAME_START_HOURS);
		end = bundle.getLong(Holidays.NAME_END);
		endType = BorderType.valueOf(bundle.getString(Holidays.NAME_END_TYPE));
		endHours = bundle.getInt(Holidays.NAME_END_HOURS);
		isHoliday = bundle.getInt(Holidays.NAME_IS_HOLIDAY) == 1 ? true : false;
		isPaid = bundle.getInt(Holidays.NAME_IS_PAID) == 1 ? true : false;
		isYearly = bundle.getInt(Holidays.NAME_IS_YEARLY) == 1 ? true : false;
		comment = bundle.getString(Holidays.NAME_COMMENT);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + endHours;
		result = prime * result + ((endType == null) ? 0 : endType.hashCode());
		result = prime * result + (isHoliday ? 1231 : 1237);
		result = prime * result + (isPaid ? 1231 : 1237);
		result = prime * result + (isYearly ? 1231 : 1237);
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + startHours;
		result = prime * result + ((startType == null) ? 0 : startType.hashCode());
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
		Holiday other = (Holiday) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (end != other.end)
			return false;
		if (endHours != other.endHours)
			return false;
		if (endType != other.endType)
			return false;
		if (isHoliday != other.isHoliday)
			return false;
		if (isPaid != other.isPaid)
			return false;
		if (isYearly != other.isYearly)
			return false;
		if (start != other.start)
			return false;
		if (startHours != other.startHours)
			return false;
		if (startType != other.startType)
			return false;
		return true;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public BorderType getStartType() {
		return startType;
	}

	public void setStartType(BorderType startType) {
		this.startType = startType;
	}

	public int getStartHours() {
		return startHours;
	}

	public void setStartHours(int startHours) {
		this.startHours = startHours;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public BorderType getEndType() {
		return endType;
	}

	public void setEndType(BorderType endType) {
		this.endType = endType;
	}

	public int getEndHours() {
		return endHours;
	}

	public void setEndHours(int endHours) {
		this.endHours = endHours;
	}

	public boolean isHoliday() {
		return isHoliday;
	}

	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}

	public boolean isYearly() {
		return isYearly;
	}

	public void setYearly(boolean isYearly) {
		this.isYearly = isYearly;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
