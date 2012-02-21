package ch.almana.android.stechkarte.view.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Days;
import ch.almana.android.stechkarte.utils.Formater;

public class DayItemAdapter extends SimpleCursorAdapter {

	private Context context;
	private static SimpleDateFormat weekDayDateFormat = new SimpleDateFormat("EEE");

	public DayItemAdapter(Context ctx, Cursor cursor) {
		super(ctx, R.layout.daylist_item, cursor, new String[] { DB.NAME_ID, DB.Days.NAME_DAYREF,
				DB.Days.NAME_HOURS_WORKED,
				DB.Days.NAME_OVERTIME, DB.Days.NAME_HOURS_TARGET, DB.Days.NAME_HOLIDAY, DB.Days.NAME_HOLIDAY_LEFT, DB.Days.NAME_FIXED },
				new int[] { R.id.TextViewDayRef, R.id.TextViewDayRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget,
						R.id.TextViewHoliday,
						R.id.TextViewHolidaysLeft, R.id.ImageViewLock }, 0);

		this.context = ctx;

		setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Days.INDEX_DAYREF) {
					Day d = new Day(cursor);
					int color = Color.GREEN;
					if (d.isError()) {
						color = Color.RED;
					}
					TextView tvWeekday = (TextView) ((View) view.getParent()).findViewById(R.id.tvWeekday);
					((TextView) view).setTextColor(color);
					tvWeekday.setTextColor(color);

					long dayRef = cursor.getLong(columnIndex);
					((TextView) view).setText(Long.toString(dayRef));
					tvWeekday.setText(weekDayDateFormat.format(new Date(DayAccess.timestampFromDayRef(dayRef))));
					return true;

				} else if (columnIndex == DB.Days.INDEX_OVERTIME) {
					Day d = new Day(cursor);
					CharSequence formatHourMinFromHours = Formater.formatHourMinFromHours(d.getOvertime());
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(formatHourMinFromHours);
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = d.getHoursWorked() - d.getHoursTarget();
					tv.setText(Formater.formatHourMinFromHours(overtime));
					tv.setTextColor(Color.LTGRAY);
					if (overtime > 5) {
						tv.setTextColor(Color.RED);
					} else if (overtime > 3) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Days.INDEX_HOURS_WORKED) {
					float hoursWorked = cursor.getFloat(Days.INDEX_HOURS_WORKED);
					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatHourMinFromHours(hoursWorked));
					tv.setTextColor(Color.LTGRAY);
					if (hoursWorked > 12) {
						tv.setTextColor(Color.RED);
					} else if (hoursWorked > 10) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Days.INDEX_HOURS_TARGET) {
					Day d = new Day(cursor);
					((TextView) view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatHourMinFromHours(d.getHoursTarget()));
					return true;
				} else if (columnIndex == DB.Days.INDEX_FIXED) {
					ImageView iv = (ImageView) view.findViewById(R.id.ImageViewLock);
					if (cursor.getInt(Days.INDEX_FIXED) > 0) {
						iv.setImageDrawable(context.getResources().getDrawable(R.drawable.locked));
					} else {
						iv.setImageBitmap(null);
					}
					return true;
				}
				return false;
			}
		});
	}
}
