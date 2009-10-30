package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class TimestampEditor extends Activity implements OnTimeChangedListener {

	private static final int DIA_DATE_SELECT = 0;
	protected Timestamp timestamp;
	protected Timestamp origTimestamp;
	private Button dateField;
	private TimePicker timeField;
	private Button inOutField;
	protected TextView timeDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timestamp_editor);
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_INSERT.equals(action)) {
			int type = getIntent().getIntExtra(Timestamps.COL_NAME_TIMESTAMP_TYPE, 0);
			timestamp = new Timestamp(System.currentTimeMillis(), type);
		} else if (Intent.ACTION_EDIT.equals(action)) {
			Cursor c = managedQuery(intent.getData(), Timestamps.DEFAULT_PROJECTION, null, null, null);
			if (c.moveToFirst()) {
				timestamp = new Timestamp(c);
			}
			c.close();
		}
		origTimestamp = new Timestamp(timestamp);
		dateField = (Button) findViewById(R.id.ButtonDate);
		timeField = (TimePicker) findViewById(R.id.TimePicker01);
		inOutField = (Button) findViewById(R.id.ButtonInOut);
		timeDisplay = (TextView) findViewById(R.id.TextViewTime);
		inOutField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timestamp.setTimestampType(Timestamp.invertTimestampType(getTimestamp()));
				updateDisplayFields();
			}
		});
		dateField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIA_DATE_SELECT);

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIA_DATE_SELECT:
			OnDateSetListener callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					timestamp.setYear(year);
					timestamp.setMonth(monthOfYear);
					timestamp.setDay(dayOfMonth);
					updateDisplayFields();
				}
			};
			return new DatePickerDialog(this, callBack, getTimestamp().getYear(), getTimestamp().getMonth(),
					getTimestamp().getDay());

		default:
			return super.onCreateDialog(id);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		timeField.setIs24HourView(true);
		timeField.setCurrentHour(timestamp.getHour());
		timeField.setCurrentMinute(timestamp.getMinute());
		updateDisplayFields();

		timeField.setOnTimeChangedListener(this);

	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		getTimestamp().setHour(hourOfDay);
		getTimestamp().setMinute(minute);
		updateDisplayFields();
	};

	protected Timestamp getTimestamp() {
		return timestamp;
	}

	private void updateDisplayFields() {
		inOutField.setText(Timestamp.getTimestampTypeAsString(this, timestamp.getTimestampType()));
		timeDisplay.setText(Timestamp.formatTime(timestamp));
		dateField.setText(Timestamp.formatTimeDateOnly(timestamp));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (origTimestamp.equals(timestamp)) {
			return;
		}
		String action = getIntent().getAction();
		if (Intent.ACTION_INSERT.equals(action)) {
			TimestampAccess access = TimestampAccess.getInstance(getApplicationContext());
			access.insert(timestamp);
		} else if (Intent.ACTION_EDIT.equals(action)) {
			TimestampAccess access = TimestampAccess.getInstance(getApplicationContext());
			access.update(timestamp);
		}
	}

}
