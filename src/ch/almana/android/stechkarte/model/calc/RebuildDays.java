package ch.almana.android.stechkarte.model.calc;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.utils.IProgressWrapper;
import ch.almana.android.stechkarte.utils.Settings;

public abstract class RebuildDays {

	protected static final String LOG_TAG = Logger.TAG;
	protected Context context;
	protected DayAccess dayAccess;

	public static RebuildDays create(Context context) {
		return new RebuildDaysTimestamps(context);
	}

	RebuildDays(Context context) {
		super();
		this.context = context;
		this.dayAccess = DayAccess.getInstance();
	}

	public abstract void recalculateDays(Timestamp timestamp, IProgressWrapper progressWrapper);


	public void recalculateDay(long dayRef) {
		if (dayRef < 1) {
			return;
		}
		Day day = dayAccess.getOrCreateDay(dayRef);
		recalculateDay(day);
	}

	public void recalculateDay(Day day) {
		day.setLastUpdated(System.currentTimeMillis());
		// if (dayRef < 1) {
		// return;
		// }
		// Day day = getOrCreateDay(dayRef);
		long dayRef = day.getDayRef();
		Day previousDay = dayAccess.getDayBefore(day);
		if (previousDay == null) {
			previousDay = new Day(0);
		}
		Log.i(LOG_TAG, "Recalculating " + dayRef + " with prev day " + previousDay.getDayRef());
		long worked = 0;
		// calculate for timestamps
		Cursor timestampCursor = day.getTimestamps();
		boolean error = false;
		//FIXME handle nightshifts by inserting 00:00
		while (timestampCursor.moveToNext()) {
			// what a timestamp is in an other day?
			Timestamp t1 = new Timestamp(timestampCursor);
			if (t1.getTimestampType() == Timestamp.TYPE_IN) {
				if (timestampCursor.moveToNext()) {
					Timestamp t2 = new Timestamp(timestampCursor);
					long diff = (t2.getTimestamp() - t1.getTimestamp());
					worked = worked + diff;
					Log.i(LOG_TAG, "Worked " + diff / DayAccess.HOURS_IN_MILLIES + " form " + t1.toString() + " to " + t2.toString());
				} else {
					error = true;
				}
			} else {
				error = true;
			}
		}
		if (!timestampCursor.isClosed()) {
			timestampCursor.close();
		}
		day.setError(error);
		// if (day.getHoursTarget() == 0f) {
		// day.setHoursTarget(getHoursTargetDefault());
		// }
		float hoursTargetDefault = Settings.getInstance().getHoursTarget(day.getDayRef());
		float hoursTarget = day.getHoursTarget();
		if (hoursTarget == hoursTargetDefault || hoursTarget < 0f) {
			float hoursTargetReal = hoursTargetDefault - day.getHolyday() * hoursTargetDefault;
			day.setHoursTarget(hoursTargetReal);
		}
		float hoursWorked = worked / DayAccess.HOURS_IN_MILLIES;
		float overtime = hoursWorked - day.getHoursTarget();
		Log.i(LOG_TAG, "Total hours worked: " + hoursWorked + " yields overtime: " + overtime);
		day.setHoursWorked(hoursWorked);
		if (!day.isFixed()) {
			day.setOvertime(getTotalOvertime(day, previousDay, overtime));
			day.setHolydayLeft(previousDay.getHolydayLeft() - day.getHolyday());
		}
		day.setWeekRef(-1);
		day.getWeekRef();
		Log.d(Logger.TAG, "Rebuild days: " + dayRef + " w:" + hoursWorked + " ovti " + overtime + " tot ovti " + previousDay.getOvertime() + overtime);
		day.setLastUpdated(System.currentTimeMillis());
		dayAccess.insertOrUpdate(day);
	}

	private float getTotalOvertime(Day day, Day previousDay, float overtime) {
		float prevOvertime = previousDay.getOvertime();
		float totalOvertime = prevOvertime + overtime;
		Settings settings = Settings.getInstance();
		if (settings.isWeeklyOvertimeReset() || settings.isMonthlyOvertimeReset() || settings.isYearlyOvertimeReset()) {
			Calendar today = Calendar.getInstance();
			today.setTimeInMillis(DayAccess.timestampFromDayRef(day.getDayRef()));
			Calendar yesterday = Calendar.getInstance();
			yesterday.setTimeInMillis(DayAccess.timestampFromDayRef(previousDay.getDayRef()));
			int calField = Calendar.YEAR;
			if (settings.isWeeklyOvertimeReset()) {
				calField = Calendar.WEEK_OF_YEAR;
			} else if (settings.isMonthlyOvertimeReset()) {
				calField = Calendar.MONTH;
			} else if (settings.isYearlyOvertimeReset()) {
				calField = Calendar.YEAR;
			}
			int tVal = today.get(calField);
			int yVal = yesterday.get(calField);
			if (tVal != yVal) {
				float resetValue = settings.getOvertimeResetValue();
				Log.i(LOG_TAG, "Resetting overtime");
				if (settings.isResetOvertimeIfBigger()) {
					if (prevOvertime > resetValue) {
						totalOvertime = overtime + resetValue;
					} else {
						totalOvertime = overtime + prevOvertime;
					}
				} else {
					totalOvertime = overtime + resetValue;
				}
			}
		}
		return totalOvertime;
	}
}
