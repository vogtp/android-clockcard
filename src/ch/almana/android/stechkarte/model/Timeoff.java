package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Timeoffs;

public class Timeoff {

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



	public Timeoff(Timeoff timeoff) {
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

	public Timeoff(Cursor c) {
		super();
		id = c.getLong(DB.INDEX_ID);
		start = c.getLong(Timeoffs.INDEX_START);
		startType = BorderType.valueOf(c.getString(Timeoffs.INDEX_START_TYPE));
		startHours = c.getInt(Timeoffs.INDEX_START_HOURS);
		end = c.getLong(Timeoffs.INDEX_END);
		endType = BorderType.valueOf(c.getString(Timeoffs.INDEX_END_TYPE));
		endHours = c.getInt(Timeoffs.INDEX_END_HOURS);
		isHoliday = c.getInt(Timeoffs.INDEX_IS_HOLIDAY) == 1 ? true : false;
		isPaid = c.getInt(Timeoffs.INDEX_IS_PAID) == 1 ? true : false;
		isYearly = c.getInt(Timeoffs.INDEX_IS_YEARLY) == 1 ? true : false;
		comment = c.getString(Timeoffs.INDEX_COMMENT);
	}

	public Timeoff(Bundle instanceState) {
		super();
		readFromBundle(instanceState);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		if (id > -1) {
			values.put(DB.NAME_ID, id);
		}
		values.put(Timeoffs.NAME_START, getStart());
		values.put(Timeoffs.NAME_START_TYPE, getStartType().toString());
		values.put(Timeoffs.NAME_START_HOURS, getStartHours());
		values.put(Timeoffs.NAME_END, getEnd());
		values.put(Timeoffs.NAME_END_TYPE, getEndType().toString());
		values.put(Timeoffs.NAME_END_HOURS, getEndHours());
		values.put(Timeoffs.NAME_IS_HOLIDAY, isHoliday ? 1 : 0);
		values.put(Timeoffs.NAME_IS_PAID, isPaid ? 1 : 0);
		values.put(Timeoffs.NAME_IS_YEARLY, isYearly ? 1 : 0);
		values.put(Timeoffs.NAME_COMMENT, getComment());
		return values;
	}

	public void saveToBundle(Bundle bundle) {
		if (id > -1) {
			bundle.putLong(DB.NAME_ID, id);
		} else {
			bundle.putLong(DB.NAME_ID, -1);
		}
		bundle.putLong(Timeoffs.NAME_START, getStart());
		bundle.putString(Timeoffs.NAME_START_TYPE, getStartType().toString());
		bundle.putInt(Timeoffs.NAME_START_HOURS, getStartHours());
		bundle.putLong(Timeoffs.NAME_END, getEnd());
		bundle.putString(Timeoffs.NAME_END_TYPE, getEndType().toString());
		bundle.putInt(Timeoffs.NAME_END_HOURS, getEndHours());
		bundle.putInt(Timeoffs.NAME_IS_HOLIDAY, isHoliday ? 1 : 0);
		bundle.putInt(Timeoffs.NAME_IS_PAID, isPaid ? 1 : 0);
		bundle.putInt(Timeoffs.NAME_IS_YEARLY, isYearly ? 1 : 0);
		bundle.putString(Timeoffs.NAME_COMMENT, getComment());
	}

	public void readFromBundle(Bundle bundle) {
		id = bundle.getLong(DB.NAME_ID);
		start = bundle.getLong(Timeoffs.NAME_START);
		startType = BorderType.valueOf(bundle.getString(Timeoffs.NAME_START_TYPE));
		startHours = bundle.getInt(Timeoffs.NAME_START_HOURS);
		end = bundle.getLong(Timeoffs.NAME_END);
		endType = BorderType.valueOf(bundle.getString(Timeoffs.NAME_END_TYPE));
		endHours = bundle.getInt(Timeoffs.NAME_END_HOURS);
		isHoliday = bundle.getInt(Timeoffs.NAME_IS_HOLIDAY) == 1 ? true : false;
		isPaid = bundle.getInt(Timeoffs.NAME_IS_PAID) == 1 ? true : false;
		isYearly = bundle.getInt(Timeoffs.NAME_IS_YEARLY) == 1 ? true : false;
		comment = bundle.getString(Timeoffs.NAME_COMMENT);
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
		Timeoff other = (Timeoff) obj;
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
