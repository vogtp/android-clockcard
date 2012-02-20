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
import ch.almana.android.stechkarte.model.MonthAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.WeekAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;
import ch.almana.android.stechkarte.utils.IProgressWrapper;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.view.appwidget.StechkarteAppwidget;

class RebuildDaysTimestamps extends RebuildDays {

	RebuildDaysTimestamps(Context context) {
		super(context);
	}

	@Override
	public void recalculateDays(Timestamp timestamp, IProgressWrapper progressWrapper) {
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
			while (c != null && c.moveToNext()) {
				progressWrapper.setProgress(i++);
				Timestamp ts = new Timestamp(c);
				long dayref = timestampAccess.calculateDayrefForTimestamp(ts, lastTs);
				Day curDay = dayAccess.getOrCreateDay(dayref);
				if (curDay.isFixed()) {
					continue;
				}

				ts.setDayRef(dayref);
				if (dayRefs.add(dayref)) {
					Logger.i("Added day " + dayref + " for recalculation");
				}
				long monthref = MonthAccess.getMonthRefFromDayRef(dayref);
				if (monthRefs.add(monthref)) {
					Logger.i("Added month " + monthref + " for recalculation");
				}
				long wekkref = WeekAccess.getWeekRefFromDayRef(dayref);
				if (weekRefs.add(wekkref)) {
					Logger.i("Added week " + wekkref + " for recalculation");
				}

				timestampAccess.update(DB.Timestamps.CONTENT_URI, ts.getValues(), DB.NAME_ID + "=" + ts.getId(), null);
				lastTs = ts;
			}
			c.close();

			for (Iterator<Long> iterator = dayRefs.iterator(); iterator.hasNext();) {
				progressWrapper.setProgress(i++);
				Long dayRef = iterator.next();
				recalculateDay(dayRef);
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


}
