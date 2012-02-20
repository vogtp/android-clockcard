package ch.almana.android.stechkarte.model.calc;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.utils.IProgressWrapper;
import ch.almana.android.stechkarte.utils.Settings;

public abstract class RebuildDays {

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
		Settings settings = Settings.getInstance();
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
		Logger.i("Recalculating " + dayRef + " with prev day " + previousDay.getDayRef());
		long worked = 0;
		// calculate for timestamps
		Cursor timestampCursor = day.getTimestamps();
		int error = 0;
		boolean first = true;
		Timestamp t1 = null;
		while (timestampCursor.moveToNext()) {
			t1 = new Timestamp(timestampCursor);
			if (t1.getTimestampType() == Timestamp.TYPE_IN) {
				if (timestampCursor.moveToNext()) {
					Timestamp t2 = new Timestamp(timestampCursor);
					if (t2.getTimestampType() == Timestamp.TYPE_OUT) {
						long diff = (t2.getTimestamp() - t1.getTimestamp());
						worked = worked + diff;
						Logger.i("Worked " + diff / DayAccess.HOURS_IN_MILLIES + " form " + t1.toString() + " to " + t2.toString());
						t1 = null;
					}
				} else {
					error++;
				}
			} else {
				if (first) {
					// fist ts is OUT -> we worked over night....
					long ts1 = t1.getTimestamp();
					long diff = ts1 - getFirstMilliOfDay(ts1);
					worked = worked + diff;
					Logger.i("Worked overnight " + diff / DayAccess.HOURS_IN_MILLIES + " form midnight to" + t1.toString());
					if (!settings.isNightshiftEnabled()) {
						// if nightshift is not enabled we display an error
						error++;
					}
				} else {
					error++;
				}
			}
			first = false;
		}

		if (t1 != null && t1.getTimestampType() == Timestamp.TYPE_IN) {
			// last ts is OUT -> we will be working overnight
			if (day.isToday()) {
				recalculateDay(previousDay);
			} else {
				long ts1 = t1.getTimestamp();
				long diff = getLastMilliOfDay(ts1) - ts1;
				worked = worked + diff;
				Logger.i("Worked overnight " + diff / DayAccess.HOURS_IN_MILLIES + " form " + t1.toString() + " to midnight");
				if (settings.isNightshiftEnabled()) {
					// if nightshift is not enabled we display an error
					error--;
				}
			}
		}

		if (!timestampCursor.isClosed()) {
			timestampCursor.close();
		}
		day.setError(error != 0);
		float hoursTargetDefault = settings.getHoursTarget(day.getDayRef());
		float hoursTarget = day.getHoursTarget();
		if (hoursTarget == hoursTargetDefault || hoursTarget < 0f) {
			float hoursTargetReal = hoursTargetDefault - day.getHolyday() * hoursTargetDefault;
			day.setHoursTarget(hoursTargetReal);
		}
		float hoursWorked = worked / DayAccess.HOURS_IN_MILLIES;
		float overtime = hoursWorked - day.getHoursTarget();
		Logger.i("Total hours worked: " + hoursWorked + " yields overtime: " + overtime);
		day.setHoursWorked(hoursWorked);
		if (!day.isFixed()) {
			day.setOvertime(getTotalOvertime(day, previousDay, overtime));
			day.setHolydayLeft(previousDay.getHolydayLeft() - day.getHolyday());
		}
		day.setWeekRef(-1);
		day.getWeekRef();
		Logger.d("Rebuild days: " + dayRef + " w:" + hoursWorked + " ovti " + overtime + " tot ovti " + previousDay.getOvertime() + overtime);
		day.setLastUpdated(System.currentTimeMillis());
		dayAccess.insertOrUpdate(day);
	}

	private long getFirstMilliOfDay(long ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 1);
		return cal.getTimeInMillis();
	}

	private long getLastMilliOfDay(long ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTimeInMillis();
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
				Logger.i("Resetting overtime");
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
