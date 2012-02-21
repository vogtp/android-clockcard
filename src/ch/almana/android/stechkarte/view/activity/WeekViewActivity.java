package ch.almana.android.stechkarte.view.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.Week;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Days;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.view.adapter.DayItemAdapter;

public class WeekViewActivity extends ListActivity {

	private Week week;
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
		if (Settings.getInstance().hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.weekViewTitle);
		} else {
			setTitle(getString(R.string.app_name) + ": " + getString(R.string.weekViewTitle));
		}

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
			Log.w(Logger.TAG, "Reading day information from savedInstanceState");
			if (week != null) {
				week.readFromBundle(savedInstanceState);
			} else {
				week = new Week(savedInstanceState);
			}
		} else if (Intent.ACTION_EDIT.equals(action)) {
			CursorLoader cursorLoader = new CursorLoader(this, intent.getData(), DB.Weeks.DEFAULT_PROJECTION, null, null, null);
			Cursor c = cursorLoader.loadInBackground();
			if (c.moveToFirst()) {
				week = new Week(c); 
			}
			c.close();
		}

		if (week == null) {
			week = new Week();
		}
		long weekRef = week.getWeekRef();
		String selection = null;
		if (weekRef > 0) {
			selection = DB.Days.NAME_WEEKREF + "=" + weekRef;
		}
		CursorLoader cursorLoader = new CursorLoader(this, DB.Days.CONTENT_URI, DB.Days.DEFAULT_PROJECTION, selection, null, Days.DEFAULT_SORTORDER);
		Cursor cursor = cursorLoader.loadInBackground();

		getListView().setAdapter(new DayItemAdapter(this, cursor));
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		tvMonthRef.setText("Week " + week.getWeekRef());
		tvHoursWorked.setText(week.getHoursWorked() + "");
		tvHoursTarget.setText(week.getHoursTarget() + "");
		float overtime = week.getHoursWorked() - week.getHoursTarget();
		tvOvertime.setText(overtime + "");
		tvOvertimeCur.setText(week.getOvertime() + "");
		tvViewHoliday.setText(week.getHolyday() + "");
		tvHolidaysLeft.setText(week.getHolydayLeft() + "");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = (Cursor) getListView().getItemAtPosition(position);
		Day day = new Day(c);
		Uri uri = ContentUris.withAppendedId(DB.Days.CONTENT_URI, day.getId());
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}
}
