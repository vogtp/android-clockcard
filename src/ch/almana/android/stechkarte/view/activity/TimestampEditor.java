package ch.almana.android.stechkarte.view.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;
import ch.almana.android.stechkarte.utils.Settings;

public class TimestampEditor extends Activity {

	private static final int DIA_DATE_SELECT = 0;
	protected Timestamp timestamp;
	protected Timestamp origTimestamp;
	private TimePicker timeField;
	private Button inOutField;
	private DatePicker dateField;
	private Button buOk;
	private Button buCancel;

	//	protected TextView timeDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timestamp_editor);

		if (Settings.getInstance().hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.timestampEditorTitle);
		}else {
			setTitle(getString(R.string.app_name)+": "+getString(R.string.timestampEditorTitle));
		}
		Intent intent = getIntent();
		String action = intent.getAction();
		if (savedInstanceState != null) {
			Log.w(Logger.TAG, "Reading timestamp information from savedInstanceState");
			if (timestamp != null) {
				timestamp.readFromBundle(savedInstanceState);
			} else {
				timestamp = new Timestamp(savedInstanceState);
			}
		} else if (Intent.ACTION_INSERT.equals(action)) {
			long millies = System.currentTimeMillis();
			if (intent.hasExtra(Timestamps.NAME_TIMESTAMP) && intent.getExtras().getLong(Timestamps.NAME_TIMESTAMP) > 0l) {
				millies = intent.getExtras().getLong(Timestamps.NAME_TIMESTAMP);
			}
			int type = getIntent().getIntExtra(Timestamps.NAME_TIMESTAMP_TYPE, 0);
			timestamp = new Timestamp(millies, type);

		} else if (Intent.ACTION_EDIT.equals(action)) {
			CursorLoader cursorLoader = new CursorLoader(this, intent.getData(), Timestamps.DEFAULT_PROJECTION, null, null, null);
			Cursor c = cursorLoader.loadInBackground();
			if (c.moveToFirst()) {
				timestamp = new Timestamp(c);
			}
			c.close();
		}
		origTimestamp = new Timestamp(timestamp);
		timeField = (TimePicker) findViewById(R.id.TimePicker01);
		dateField = (DatePicker) findViewById(R.id.datePicker1);
		inOutField = (Button) findViewById(R.id.ButtonInOut);
		buOk = (Button) findViewById(R.id.buOk);
		buCancel = (Button) findViewById(R.id.buCancel);
		inOutField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timestamp.setTimestampType(Timestamp.invertTimestampType(getTimestamp()));
				updateDisplayFields();
			}
		});

		buOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		buCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				timestamp = origTimestamp;
				finish();
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
			return new DatePickerDialog(this, callBack, getTimestamp().getYear(), getTimestamp().getMonth(), getTimestamp().getDay());

		default:
			return super.onCreateDialog(id);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		timeField.setIs24HourView(Settings.getInstance().is24hours());
		timeField.setCurrentHour(timestamp.getHour());
		timeField.setCurrentMinute(timestamp.getMinute());
		dateField.updateDate(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay());
		updateDisplayFields();
	}


	protected Timestamp getTimestamp() {
		return timestamp;
	}

	private void updateDisplayFields() {
		inOutField.setText(Timestamp.getTimestampTypeAsString(this, timestamp.getTimestampType()));
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			updateModel();
			String action = getIntent().getAction();
			if (Intent.ACTION_INSERT.equals(action)) {
				TimestampAccess access = TimestampAccess.getInstance();
				access.insert(timestamp);
			} else if (Intent.ACTION_EDIT.equals(action)) {
				if (origTimestamp.equals(timestamp)) {
					return;
				}
				TimestampAccess access = TimestampAccess.getInstance();
				access.update(timestamp);
			}
		} catch (Exception e) {
			Log.w(Logger.TAG, "Cannot insert or update", e);
			Toast.makeText(this, getString(R.string.cannotSaveTS), Toast.LENGTH_LONG);
		}
	}

	private void updateModel() {
		timestamp.setYear(dateField.getYear());
		timestamp.setMonth(dateField.getMonth());
		timestamp.setDay(dateField.getDayOfMonth());
		timestamp.setHour(timeField.getCurrentHour());
		timestamp.setMinute(timeField.getCurrentMinute());

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		updateModel();
		timestamp.saveToBundle(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (timestamp != null) {
			timestamp.readFromBundle(savedInstanceState);
		} else {
			timestamp = new Timestamp(savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

}
