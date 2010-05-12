package ch.almana.android.stechkarte.utils;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;

public class CurInfo {
	private static final SimpleDateFormat hhmmSimpleDateFormat = new SimpleDateFormat("HH:mm");
	private Timestamp timestamp;
	private long leaveInMillies = 0;
	private String inOutString;
	private Day day;
	private float delta = 0;
	private float curHoursWorked = 0;
	
	public CurInfo(Context context) {
		inOutString = Timestamp.getTimestampTypeAsString(context, Timestamp.TYPE_OUT);
		Cursor c = DayAccess.getInstance().query(null);
		if (c.moveToFirst()) {
			day = new Day(c);
		}
		c.close();
		if (day != null) {
			c = day.getTimestamps();
			timestamp = null;
			
			if (c.moveToLast()) {
				timestamp = new Timestamp(c);
				inOutString = timestamp.getTimestampTypeAsString(context);
				if (timestamp.getTimestampType() == Timestamp.TYPE_IN) {
					delta = (System.currentTimeMillis() - timestamp.getTimestamp()) / DayAccess.HOURS_IN_MILLIES;
				}
			}
			c.close();
			curHoursWorked = day.getHoursWorked() + delta;
			double leave = day.getHoursTarget() - curHoursWorked;
			leaveInMillies = Math.round(leave * 60d * 60d * 1000d);
		}
	}
	
	public int getTimestampType() {
		if (timestamp == null) {
			return Timestamp.TYPE_OUT;
		}
		return timestamp.getTimestampType();
	}
	
	public double getLeaveInMillies() {
		return leaveInMillies;
	}
	
	public CharSequence getLeaveAtString() {
		long at = System.currentTimeMillis() + leaveInMillies;
		if (leaveInMillies > 0l) {
			return hhmmSimpleDateFormat.format(at);
		} else {
			return "now";
		}
	}
	
	public String getInOutString() {
		return inOutString;
	}
	
	public CharSequence getHolydayLeft() {
		if (day == null) {
			return "0";
		}
		return day.getHolydayLeft() + "";
	}
	
	public CharSequence getOvertimeString() {
		if (day == null) {
			return "0";
		}
		return Formater.formatHourMinFromHours(day.getOvertime() + delta);
	}
	
	public CharSequence getHoursWorked() {
		return Formater.formatHourMinFromHours(curHoursWorked);
	}
	
	public long getUnixTimestamp() {
		if (timestamp == null) {
			return -1;
		}
		return timestamp.getTimestamp();
	}
	
	public boolean hasData() {
		return timestamp != null;
	}
}
