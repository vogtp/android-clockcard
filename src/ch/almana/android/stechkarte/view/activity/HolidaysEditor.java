package ch.almana.android.stechkarte.view.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Holiday;
import ch.almana.android.stechkarte.model.Holiday.BorderType;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Holidays;
import ch.almana.android.stechkarte.provider.DB.holidayTypes;
import ch.almana.android.stechkarte.utils.GuiUtils;
import ch.almana.android.stechkarte.utils.Settings;

public class HolidaysEditor extends Activity {
	private static final long HOLIDAY_BORDER_TYPE_ALLDAY = 0;
	private static final long HOLIDAY_BORDER_TYPE_HALFDAY = 1;
	private static final long HOLIDAY_BORDER_TYPE_RESTDAY = 2;
	private static final long HOLIDAY_BORDER_TYPE_SPECIFY = 3;

	protected static final int DIA_START_DATE_SELECT = 1;
	protected static final int DIA_END_DATE_SELECT = 2;
	private Button buHoldidayStart;
	private Button buHoldidayEnd;
	private SpecialBorderFields endSpecBorder;
	private SpecialBorderFields startSpecBorder;
	private Spinner spHolidayDurationStart;
	private Spinner spHolidayDurationEnd;
	private Spinner spHolidayType;
	private CheckBox cbIsPayed;
	private CheckBox cbIsHoliday;
	private CheckBox cbIsYearly;
	private Button buEditTimeoffType;
	private CheckBox cbYieldOvertime;
	private Holiday holiday;
	private Holiday origHoliday;
	private EditText etComment;
	private EditText etNumHolidayDays;

	private static class SpecialBorderFields {
		EditText editText;
		TextView label;
	}

	private class HolidayBorderAdapter implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			SpecialBorderFields spf = null;
			BorderType bt = BorderType.allDay;
			if (id == HOLIDAY_BORDER_TYPE_ALLDAY) {
				bt = BorderType.allDay;
			} else if (id == HOLIDAY_BORDER_TYPE_HALFDAY) {
				bt = BorderType.halfDay;
			} else if (id == HOLIDAY_BORDER_TYPE_RESTDAY) {
				bt = BorderType.restOfDay;
			} else if (id == HOLIDAY_BORDER_TYPE_SPECIFY) {
				bt = BorderType.specifyed;
			}
			if (parent == spHolidayDurationStart) {
				spf = startSpecBorder;
				holiday.setStartType(bt);
			} else if (parent == spHolidayDurationEnd) {
				spf = endSpecBorder;
				holiday.setEndType(bt);
			} else {
				return;
			}
			
			if (bt == BorderType.specifyed) {
				spf.label.setVisibility(View.VISIBLE);
				spf.editText.setVisibility(View.VISIBLE);
			} else {
				spf.label.setVisibility(View.GONE);
				spf.editText.setVisibility(View.GONE);
			}
			updateView();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.e(Logger.TAG, "No holiday duration selected");
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		OnDateSetListener callBack = null;
		Calendar cal = null;
		switch (id) {
		case DIA_START_DATE_SELECT:
			cal = holiday.getStartAsCalendar();
			callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Calendar holidayStart = Calendar.getInstance();
					holidayStart.set(Calendar.YEAR, year);
					holidayStart.set(Calendar.MONTH, monthOfYear);
					holidayStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					holiday.setStart(holidayStart.getTimeInMillis());
					updateView();
				}

			};
			break;
		case DIA_END_DATE_SELECT:
			cal = holiday.getEndAsCalendar();
			callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Calendar holidayEnd = Calendar.getInstance();
					holidayEnd.set(Calendar.YEAR, year);
					holidayEnd.set(Calendar.MONTH, monthOfYear);
					holidayEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					holiday.setEnd(holidayEnd.getTimeInMillis());
					updateView();
				}
			};
			break;

		}
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		DatePickerDialog datePickerDialog = new DatePickerDialog(this, callBack, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.getDatePicker().setCalendarViewShown(true);
		datePickerDialog.getDatePicker().setSpinnersShown(false);
		return datePickerDialog;

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holidays_editor);
		if (Settings.getInstance().hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.holidayEditorTitle);
		} else {
			setTitle(getString(R.string.app_name) + ": " + getString(R.string.holidayEditorTitle));
		}

		startSpecBorder = new SpecialBorderFields();
		endSpecBorder = new SpecialBorderFields();

		startSpecBorder.editText = (EditText) findViewById(R.id.EditTextHolidayDurationStart);
		startSpecBorder.label = (TextView) findViewById(R.id.LabelHolidayDurationStart);

		endSpecBorder.editText = (EditText) findViewById(R.id.EditTextHolidayDurationEnd);
		endSpecBorder.label = (TextView) findViewById(R.id.LabelHolidayDurationEnd);

		buHoldidayStart = (Button) findViewById(R.id.ButtonHolidayDateStart);
		buHoldidayEnd = (Button) findViewById(R.id.ButtonHolidayDateEnd);
		buEditTimeoffType = (Button) findViewById(R.id.buEditTimeoffType);

		spHolidayDurationStart = (Spinner) findViewById(R.id.SpinnerHodidayDurationStart);
		spHolidayDurationEnd = (Spinner) findViewById(R.id.SpinnerHodidayDurationEnd);

		spHolidayType = (Spinner) findViewById(R.id.SpinnerHolidayType);
		cbIsPayed = (CheckBox) findViewById(R.id.CheckBoxIsPayed);
		cbIsHoliday = (CheckBox) findViewById(R.id.cbIsHoliday);
		cbIsYearly = (CheckBox) findViewById(R.id.cbIsYearly);
		cbYieldOvertime = (CheckBox) findViewById(R.id.cbYieldOvertime);
		etNumHolidayDays = (EditText) findViewById(R.id.etNumHolidayDays);
		etComment = (EditText) findViewById(R.id.etComment);

		Intent intent = getIntent();
		String action = intent.getAction();
		if (savedInstanceState != null) {
			Log.w(Logger.TAG, "Reading day information from savedInstanceState");
			if (holiday != null) {
				holiday.readFromBundle(savedInstanceState);
			} else {
				holiday = new Holiday(savedInstanceState);
			}
		} else if (Intent.ACTION_INSERT.equals(action)) {
			holiday = new Holiday();
		} else if (Intent.ACTION_EDIT.equals(action)) {
			CursorLoader cursorLoader = new CursorLoader(this, intent.getData(), Holidays.DEFAULT_PROJECTION, null, null, null);
			Cursor c = cursorLoader.loadInBackground();
			if (c.moveToFirst()) {
				holiday = new Holiday(c);
			}
			c.close();
		}

		if (holiday == null) {
			holiday = new Holiday();
		}

		origHoliday = new Holiday(holiday);


		buHoldidayStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIA_START_DATE_SELECT);
			}
		});
		buHoldidayEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIA_END_DATE_SELECT);
			}
		});

		HolidayBorderAdapter holidayBorderAdaptor = new HolidayBorderAdapter();
		spHolidayDurationStart.setOnItemSelectedListener(holidayBorderAdaptor);
		spHolidayDurationEnd.setOnItemSelectedListener(holidayBorderAdaptor);

		CursorLoader cursorLoader = new CursorLoader(this, holidayTypes.CONTENT_URI, holidayTypes.DEFAULT_PROJECTION, null, null, null);
		Cursor c = cursorLoader.loadInBackground();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c,
				new String[] { holidayTypes.NAME_NAME }, new int[] { android.R.id.text1 }, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spHolidayType.setAdapter(adapter);
		spHolidayType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long id) {
				holiday.setHolidayType(id);
				updateView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});


		//FIXME implement
		buEditTimeoffType.setEnabled(false);
		etNumHolidayDays.setFocusable(false);

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}

	@Override
	protected void onPause() {
		super.onPause();
		updateModel();
		if (origHoliday.equals(holiday) || holiday.getStart() < 0 || holiday.getEnd() < 0) {
			return;
		}
		try {
			if (holiday.getId() < 0) {
				getContentResolver().insert(Holidays.CONTENT_URI, holiday.getValues());
			} else {
				getContentResolver().update(Holidays.CONTENT_URI, holiday.getValues(), DB.SELECTION_BY_ID, new String[] { Long.toString(holiday.getId()) });
			}
		} catch (Exception e) {
			Logger.e("Cannot save holiday", e);
		}
	}

	private void updateView() {
		updateCalendarButton(buHoldidayStart, holiday.getStartAsCalendar());
		updateCalendarButton(buHoldidayEnd, holiday.getEndAsCalendar());
		setHolidayBorderType(spHolidayDurationStart, holiday.getStartType());
		setHolidayBorderType(spHolidayDurationEnd, holiday.getEndType());
		startSpecBorder.editText.setText(Float.toString(holiday.getStartHours()));
		endSpecBorder.editText.setText(Float.toString(holiday.getEndHours()));
		etNumHolidayDays.setText(Float.toString(holiday.getDays()));
		cbIsPayed.setChecked(holiday.isPaid());
		cbIsHoliday.setChecked(holiday.isHoliday());
		cbIsYearly.setChecked(holiday.isYearly());
		cbYieldOvertime.setChecked(holiday.isYieldOvertime());
		etComment.setText(holiday.getComment());
		long holidayType = holiday.getHolidayType();
		GuiUtils.setSpinner(spHolidayType, holidayType);
		CursorLoader cursorLoader = new CursorLoader(HolidaysEditor.this, holidayTypes.CONTENT_URI, holidayTypes.DEFAULT_PROJECTION, DB.SELECTION_BY_ID,
				new String[] { Long.toString(holidayType) }, null);
		Cursor c = cursorLoader.loadInBackground();
		if (c != null && c.moveToFirst()) {
			cbIsHoliday.setChecked(c.getInt(holidayTypes.INDEX_IS_HOLIDAY) == 1);
			cbIsPayed.setChecked(c.getInt(holidayTypes.INDEX_IS_PAID) == 1);
			cbYieldOvertime.setChecked(c.getInt(holidayTypes.INDEX_YIELDS_OVERTIME) == 1);
		}
		if (c != null) {
			c.close();
		}
	}

	private void updateModel() {
		holiday.setStartHours(Float.parseFloat(startSpecBorder.editText.getText().toString()));
		holiday.setEndHours(Float.parseFloat(endSpecBorder.editText.getText().toString()));
		holiday.setPaid(cbIsPayed.isChecked());
		holiday.setHoliday(cbIsHoliday.isChecked());
		holiday.setYearly(cbIsYearly.isChecked());
		holiday.setYieldOvertime(cbYieldOvertime.isChecked());
		holiday.setComment(etComment.getText().toString());
	}


	private void updateCalendarButton(Button button, Calendar cal) {
		if (cal != null) {
			button.setText(DateFormat.getDateFormat(this).format(cal.getTime()));
		} else {
			button.setText(R.string.ButtonHolidayDateNone);
		}
	}

	private void setHolidayBorderType(Spinner spinner, BorderType borderType) {
		long sel = HOLIDAY_BORDER_TYPE_ALLDAY;
		if (borderType == BorderType.allDay) {
			sel = HOLIDAY_BORDER_TYPE_ALLDAY;
		} else if (borderType == BorderType.halfDay) {
			sel = HOLIDAY_BORDER_TYPE_HALFDAY;
		} else if (borderType == BorderType.restOfDay) {
			sel = HOLIDAY_BORDER_TYPE_RESTDAY;
		} else if (borderType == BorderType.specifyed) {
			sel = HOLIDAY_BORDER_TYPE_SPECIFY;
		}
		spinner.setSelection((int) sel);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		updateModel();
		holiday.saveToBundle(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		holiday.readFromBundle(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
		updateView();
	}
}
