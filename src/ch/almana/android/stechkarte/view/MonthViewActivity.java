package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Month;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.view.adapter.DayItemAdapter;

public class MonthViewActivity extends ListActivity {

	private Month month;
	private TextView tvMonthRef;
	private TextView tvHoursWorked;
	private TextView tvOvertime;
	private TextView tvHoursTarget;
	private TextView tvViewHoliday;
	private TextView tvHolidaysLeft;
	private TextView tvOvertimeCur;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.month_view);
		setTitle(R.string.monthViewTitle);

		tvMonthRef = (TextView) findViewById(R.id.TextViewMonthRef);
		tvHoursWorked = (TextView) findViewById(R.id.TextViewHoursWorked);
		tvOvertimeCur = (TextView) findViewById(R.id.TextViewOvertimeCur);
		tvOvertime = (TextView) findViewById(R.id.TextViewOvertime);
		tvHoursTarget = (TextView) findViewById(R.id.TextViewHoursTarget);
		tvViewHoliday = (TextView) findViewById(R.id.TextViewHoliday);
		tvHolidaysLeft = (TextView) findViewById(R.id.TextViewHolidaysLeft);

		Intent intent = getIntent();
		String action = intent.getAction();
		if (savedInstanceState != null) {
			Log.w(Logger.LOG_TAG, "Reading day information from savedInstanceState");
			if (month != null) {
				month.readFromBundle(savedInstanceState);
			} else {
				month = new Month(savedInstanceState);
			}
		} else if (Intent.ACTION_EDIT.equals(action)) {
			Cursor c = managedQuery(intent.getData(), DB.Months.DEFAULT_PROJECTION, null, null, null);
			if (c.moveToFirst()) {
				month = new Month(c);
			}
			c.close();
		}

		if (month == null) {
			month = new Month();
		}
		long monthRef = month.getMonthRef();
		String selection = null;
		if (monthRef > 0) {
			selection = DB.Days.NAME_MONTHREF + "=" + monthRef;
		}

		Cursor cursor = DayAccess.getInstance().query(selection);
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.daylist_item, cursor, new String[] { DB.NAME_ID,
		// DB.Days.NAME_DAYREF, DB.Days.NAME_HOURS_WORKED,
		// DB.Days.NAME_OVERTIME, DB.Days.NAME_HOURS_TARGET,
		// DB.Days.NAME_HOLIDAY, DB.Days.NAME_HOLIDAY_LEFT, DB.Days.NAME_FIXED
		// },
		// new int[] { R.id.TextViewDayRef, R.id.TextViewDayRef,
		// R.id.TextViewHoursWorked, R.id.TextViewOvertime,
		// R.id.TextViewHoursTarget, R.id.TextViewHoliday,
		// R.id.TextViewHolidaysLeft, R.id.ImageViewLock });
		//
		// adapter.setViewBinder(new ViewBinder() {
		// @Override
		// public boolean setViewValue(View view, Cursor cursor, int
		// columnIndex) {
		// if (cursor == null) {
		// return false;
		// }
		// if (columnIndex == DB.Days.INDEX_DAYREF) {
		// Day d = new Day(cursor);
		// int color = Color.GREEN;
		// if (d.isError()) {
		// color = Color.RED;
		// }
		// TextView errorView = (TextView)
		// view.findViewById(R.id.TextViewDayRef);
		// errorView.setTextColor(color);
		// // since we do not set the dayref no: return true;
		// } else if (columnIndex == DB.Days.INDEX_OVERTIME) {
		// Day d = new Day(cursor);
		// CharSequence formatHourMinFromHours =
		// Formater.formatHourMinFromHours(d.getOvertime());
		// ((TextView)
		// view.findViewById(R.id.TextViewOvertime)).setText(formatHourMinFromHours);
		// TextView tv = (TextView) ((View)
		// view.getParent()).findViewById(R.id.TextViewOvertimeCur);
		// float overtime = d.getHoursWorked() - d.getHoursTarget();
		// tv.setText(Formater.formatHourMinFromHours(overtime));
		// tv.setTextColor(Color.LTGRAY);
		// if (overtime > 5) {
		// tv.setTextColor(Color.RED);
		// } else if (overtime > 3) {
		// tv.setTextColor(Color.YELLOW);
		// }
		// return true;
		// } else if (columnIndex == DB.Days.INDEX_HOURS_WORKED) {
		// float hoursWorked = cursor.getFloat(Days.INDEX_HOURS_WORKED);
		// TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
		// tv.setText(Formater.formatHourMinFromHours(hoursWorked));
		// tv.setTextColor(Color.LTGRAY);
		// if (hoursWorked > 12) {
		// tv.setTextColor(Color.RED);
		// } else if (hoursWorked > 10) {
		// tv.setTextColor(Color.YELLOW);
		// }
		// return true;
		// } else if (columnIndex == DB.Days.INDEX_HOURS_TARGET) {
		// Day d = new Day(cursor);
		// ((TextView)
		// view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatHourMinFromHours(d.getHoursTarget()));
		// return true;
		// } else if (columnIndex == DB.Days.INDEX_FIXED) {
		// ImageView iv = (ImageView) view.findViewById(R.id.ImageViewLock);
		// if (cursor.getInt(Days.INDEX_FIXED) > 0) {
		// iv.setImageDrawable(getResources().getDrawable(R.drawable.locked));
		// } else {
		// iv.setImageBitmap(null);
		// }
		// return true;
		// }
		// return false;
		// }
		// });

		getListView().setAdapter(new DayItemAdapter(this, cursor));
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		tvMonthRef.setText(month.getMonthRef() + "");
		tvHoursWorked.setText(month.getHoursWorked() + "");
		tvHoursTarget.setText(month.getHoursTarget() + "");
		float overtime = month.getHoursWorked() - month.getHoursTarget();
		tvOvertime.setText(overtime + "");
		tvOvertimeCur.setText(month.getOvertime() + "");
		tvViewHoliday.setText(month.getHolyday() + "");
		tvHolidaysLeft.setText(month.getHolydayLeft() + "");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListView().getItemAtPosition(position);
		Day day = new Day(c);
		Uri uri = ContentUris.withAppendedId(DB.Days.CONTENT_URI, day.getId());
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}
}
