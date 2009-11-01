package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.day_editor);
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_INSERT.equals(action)) {
			day = new Day(20091101);
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

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateFields();
	}

	private void updateFields() {
		dayRefTextView.setText(day.getDayString());
		holiday.setText(day.getHolyday() + "");
		holidayLeft.setText(day.getHolydayLeft() + "");
		overtime.setText(day.getOvertime() + "");
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
	}
}
