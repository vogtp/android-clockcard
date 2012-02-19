package ch.almana.android.stechkarte.model.calc;

import java.util.Calendar;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.MonthAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.WeekAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;
import ch.almana.android.stechkarte.utils.IProgressWrapper;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.view.appwidget.StechkarteAppwidget;

public class RebuildDaysOld implements IRebuildDays {
	private static final String LOG_TAG = Logger.TAG;
	private final Context context;
	private final DayAccess dayAccess;

	RebuildDaysOld(Context context) {
		super();
		this.context = context;
		this.dayAccess = DayAccess.getInstance();
	}

	public void recalculate(long dayRef) {
		if (dayRef < 1) {
			return;
		}
		Day day = dayAccess.getOrCreateDay(dayRef);
		recalculate(day);
	}

	@Override
	public void recalculate(Day day) {
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
		Log.w(Logger.TAG, "Rebuild days: " + dayRef + " w:" + hoursWorked + " ovti " + overtime + " tot ovti " + previousDay.getOvertime() + overtime);
		day.setLastUpdated(System.currentTimeMillis());
		dayAccess.insertOrUpdate(day);
	}

	@Override
	public void recalculateDayFromTimestamp(Timestamp timestamp, IProgressWrapper progressWrapper) {
		int i = 0;
		StechkarteAppwidget.setDoNotUpdate(true);
		MonthAccess.getInstance().setDoNotRecalculate(true);
		SortedSet<Long> monthRefs = new TreeSet<Long>();
		SortedSet<Long> weekRefs = new TreeSet<Long>();
		try {
			String selection = null;
			if (timestamp != null) {
				selection = DB.Timestamps.NAME_TIMESTAMP + ">=" + timestamp.getTimestamp();
			}

			boolean useCalendarDays = Settings.getInstance().isUseCalendarDays();

			Calendar cal = Calendar.getInstance();
			if (useCalendarDays) {
				cal.setTimeInMillis(timestamp.getTimestamp());
				// TODO: handle progress
			}

			TimestampAccess timestampAccess = TimestampAccess.getInstance();
			Cursor c = timestampAccess.query(selection, Timestamps.REVERSE_SORTORDER);
			SortedSet<Long> dayRefs = new TreeSet<Long>();
			progressWrapper.setMax(c.getCount() * 2);
			progressWrapper.incrementEvery(2);
			Timestamp lastTs = null;
			while (hasMoreTsToProcess(c, cal, useCalendarDays)) {
				progressWrapper.setProgress(i++);
				Timestamp ts = new Timestamp(c);
				long dayref = timestampAccess.calculateDayrefForTimestamp(ts, lastTs);
				Day curDay = dayAccess.getOrCreateDay(dayref);
				if (curDay.isFixed()) {
					continue;
				}

				ts.setDayRef(dayref);
				if (dayRefs.add(dayref)) {
					Log.i(LOG_TAG, "Added day " + dayref + " for recalculation");
				}
				long monthref = MonthAccess.getMonthRefFromDayRef(dayref);
				if (monthRefs.add(monthref)) {
					Log.i(LOG_TAG, "Added month " + monthref + " for recalculation");
				}
				long wekkref = WeekAccess.getWeekRefFromDayRef(dayref);
				if (weekRefs.add(wekkref)) {
					Log.i(LOG_TAG, "Added week " + wekkref + " for recalculation");
				}

				timestampAccess.update(DB.Timestamps.CONTENT_URI, ts.getValues(), DB.NAME_ID + "=" + ts.getId(), null);
				lastTs = ts;
			}
			c.close();

			for (Iterator<Long> iterator = dayRefs.iterator(); iterator.hasNext();) {
				progressWrapper.setProgress(i++);
				Long dayRef = iterator.next();
				recalculate(dayRef);
			}
		} finally {
			StechkarteAppwidget.setDoNotUpdate(false);
			MonthAccess.getInstance().setDoNotRecalculate(false);
			StechkarteAppwidget.updateView(context);
		}
		for (Iterator<Long> iterator = monthRefs.iterator(); iterator.hasNext();) {
			try {
				progressWrapper.setProgress(i++);
			} catch (Exception e) {
				Log.w("Error updating progress from update month view", e);
			}
			Long monthRef = iterator.next();
			MonthAccess.getInstance().recalculate(monthRef);
		}
		for (Iterator<Long> iterator = weekRefs.iterator(); iterator.hasNext();) {
			try {
				progressWrapper.setProgress(i++);
			} catch (Exception e) {
				Log.w("Error updating progress from update week view", e);
			}
			Long weekRef = iterator.next();
			WeekAccess.getInstance().recalculate(weekRef);
		}
	}

	private boolean hasMoreTsToProcess(Cursor c, Calendar cal, boolean useCalendarDays) {
		boolean result = c.moveToNext();
		if (useCalendarDays) {

		}
		return result;
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
