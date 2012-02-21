package ch.almana.android.stechkarte.view.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.utils.Formater;
import ch.almana.android.stechkarte.utils.Settings;

public class HolidaysEditor extends Activity {

	protected static final int DIA_START_DATE_SELECT = 1;
	protected static final int DIA_END_DATE_SELECT = 2;
	private Button buHoldidayStart;
	private Button buHoldidayEnd;
	private SpecialBorderFields endSpecBorder;
	private SpecialBorderFields startSpecBorder;
	private Spinner spHolidayDurationStart;
	private Spinner spHolidayDurationEnd;
	private Calendar holidayStart = null;
	private  Calendar holidayEnd = null;
	private Spinner spHolidayType;
	private CheckBox cbIsPayed;

	private static class SpecialBorderFields {
		EditText editText;
		TextView label;
	}

	private class HolidayBorderAdapter implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			SpecialBorderFields spf = null;
			if (parent == spHolidayDurationStart) {
				spf = startSpecBorder;
			} else if (parent == spHolidayDurationEnd) {
				spf = endSpecBorder;
			} else {
				return;
			}
			if (id == 3) {
				spf.label.setVisibility(View.VISIBLE);
				spf.editText.setVisibility(View.VISIBLE);
			} else {
				spf.label.setVisibility(View.GONE);
				spf.editText.setVisibility(View.GONE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.e(Logger.TAG, "No holiday duration selected");
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		OnDateSetListener callBack = null;
		switch (id) {
		case DIA_START_DATE_SELECT:
			callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					if (holidayStart == null) {
						holidayStart = getNewDateCalendar();
					}
					holidayStart.set(Calendar.YEAR, year);
					holidayStart.set(Calendar.MONTH, monthOfYear);
					holidayStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateView();
				}

			};
			break;
		case DIA_END_DATE_SELECT:
			callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					if (holidayEnd == null) {
						holidayEnd = getNewDateCalendar();
					}
					holidayEnd.set(Calendar.YEAR, year);
					holidayEnd.set(Calendar.MONTH, monthOfYear);
					holidayEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateView();
				}
			};
			break;

		}
		Calendar cal = Calendar.getInstance();
		return new DatePickerDialog(this, callBack, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

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
		startSpecBorder.editText = (EditText) findViewById(R.id.EditTextHolidayDurationStart);
		startSpecBorder.label = (TextView) findViewById(R.id.LabelHolidayDurationStart);

		endSpecBorder = new SpecialBorderFields();
		endSpecBorder.editText = (EditText) findViewById(R.id.EditTextHolidayDurationEnd);
		endSpecBorder.label = (TextView) findViewById(R.id.LabelHolidayDurationEnd);

		buHoldidayStart = (Button) findViewById(R.id.ButtonHolidayDateStart);
		buHoldidayEnd = (Button) findViewById(R.id.ButtonHolidayDateEnd);
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
		spHolidayDurationStart = (Spinner) findViewById(R.id.SpinnerHodidayDurationStart);
		spHolidayDurationStart.setOnItemSelectedListener(holidayBorderAdaptor);
		spHolidayDurationEnd = (Spinner) findViewById(R.id.SpinnerHodidayDurationEnd);
		spHolidayDurationEnd.setOnItemSelectedListener(holidayBorderAdaptor);

		spHolidayType = (Spinner) findViewById(R.id.SpinnerHolidayType);
		//		SpinnerAdapter adapter = new ArrayAdapt;
		//		spHolidayType.setAdapter(adapter );
		//		cbIsPayed = (CheckBox) findViewById(R.id.CheckBoxIsPayed); 
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		updateCalendarButton(buHoldidayStart, holidayStart);
		updateCalendarButton(buHoldidayEnd, holidayEnd);
		// TODO Auto-generated method stub

	}

	private void updateCalendarButton(Button button, Calendar cal) {
		if (cal != null) {
			button.setText(Formater.formatDate(cal.getTime()));
		}else {
			button.setText(R.string.ButtonHolidayDateNone);
		}
	}

	private Calendar getNewDateCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
}
