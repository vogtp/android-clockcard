package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DB;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;

public class DayEditor extends Activity {

	private Day day;
	private Day origDay;
	private TextView dayRefTextView;
	private EditText holiday;
	private EditText holidayLeft;
	private EditText overtime;
	private EditText hoursTarget;
	private TextView hoursWorked;
	private CheckBox fixed;
	private TimestampsAdaptorFactory tsAdaptorFactory;
	private ListView timestamps;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.day_editor);

		Intent intent = getIntent();
		String action = intent.getAction();
		if (savedInstanceState != null) {
			Log.w(Logger.LOG_TAG, "Reading day information from savedInstanceState");
			if (day != null) {
				day.readFromBundle(savedInstanceState);
			} else {
				day = new Day(savedInstanceState);
			}
		} else if (Intent.ACTION_INSERT.equals(action)) {
			day = new Day(20091101); // FIXME get today
		} else if (Intent.ACTION_EDIT.equals(action)) {
			Cursor c = managedQuery(intent.getData(), DB.Days.DEFAULT_PROJECTION, null, null, null);
			if (c.moveToFirst()) {
				day = new Day(c);
			}
			c.close();
		}

		origDay = new Day(day);
		dayRefTextView = (TextView) findViewById(R.id.TextViewDayRef);
		holiday = (EditText) findViewById(R.id.EditTextHoliday);
		holidayLeft = (EditText) findViewById(R.id.EditTextHolidaysLeft);
		overtime = (EditText) findViewById(R.id.EditTextOvertime);
		hoursTarget = (EditText) findViewById(R.id.EditTextHoursTarget);
		hoursWorked = (TextView) findViewById(R.id.TextViewHoursWorkedDayEditor);
		fixed = (CheckBox) findViewById(R.id.CheckBoxFixed);

		timestamps = (ListView) findViewById(R.id.ListViewTimestamps);
		tsAdaptorFactory = new TimestampsAdaptorFactory(timestamps, day.getDayRef());
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateFields();
		tsAdaptorFactory.onResume();
	}

	private void updateFields() {
		dayRefTextView.setText(day.getDayString());
		holiday.setText(day.getHolyday() + "");
		holidayLeft.setText(day.getHolydayLeft() + "");
		overtime.setText(day.getOvertime() + "");
		fixed.setChecked(day.isFixed());
		hoursTarget.setText(day.getHoursTarget() + "");
		hoursWorked.setText(day.getHoursWorked() + "");
	}

	private void updateModel() {
		try {
			day.setHolyday(Float.parseFloat(holiday.getText().toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Cannot parse number " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		try {
			day.setHolydayLeft(Float.parseFloat(holidayLeft.getText().toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Cannot parse number " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		try {
			day.setOvertime(Float.parseFloat(overtime.getText().toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Cannot parse number " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		try {
			day.setHoursTarget(Float.parseFloat(hoursTarget.getText().toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Cannot parse number " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		day.setFixed(fixed.isChecked());
	}

	@Override
	protected void onPause() {
		super.onPause();
		updateModel();
		if (origDay.equals(day)) {
			return;
		}
		String action = getIntent().getAction();
		if (Intent.ACTION_INSERT.equals(action)) {
			DayAccess access = DayAccess.getInstance(getApplicationContext());
			access.insert(day);
		} else if (Intent.ACTION_EDIT.equals(action)) {
			DayAccess access = DayAccess.getInstance(getApplicationContext());
			access.update(day);
		}
		DayAccess.getInstance(this).recalculate(this, day);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		day.saveToBundle(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (day != null) {
			day.readFromBundle(savedInstanceState);
		} else {
			day = new Day(savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
}
