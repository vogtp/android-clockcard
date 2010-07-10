package ch.almana.android.stechkarte.view;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

public class HolidaysEditor extends Activity {

	protected static final int DIA_START_DATE_SELECT = 1;
	protected static final int DIA_END_DATE_SELECT = 2;
	private Button buHoldidayStart;
	private Button buHoldidayEnd;
	private SpecialBorderFields endSpecBorder;
	private SpecialBorderFields startSpecBorder;
	private Spinner spHolidayDurationStart;
	private Spinner spHolidayDurationEnd;

	private class SpecialBorderFields {
		EditText editText;
		TextView label;
	}

	private class HolidayBorderAdaptor implements AdapterView.OnItemSelectedListener {
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
				spf.label.setVisibility(View.INVISIBLE);
				spf.editText.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.e(Logger.LOG_TAG, "No holiday duration selected");
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
					buHoldidayStart.setText(year + "" + monthOfYear + 1 + "" + dayOfMonth + "(TODO)");
				}
			};
			break;
		case DIA_END_DATE_SELECT:
			callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					buHoldidayEnd.setText(year + "" + monthOfYear + 1 + "" + dayOfMonth + "(TODO)");
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
		setTitle(R.string.holidayEditorTitle);

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

		HolidayBorderAdaptor holidayBorderAdaptor = new HolidayBorderAdaptor();
		spHolidayDurationStart = (Spinner) findViewById(R.id.SpinnerHodidayDurationStart);
		spHolidayDurationStart.setOnItemSelectedListener(holidayBorderAdaptor);
		spHolidayDurationEnd = (Spinner) findViewById(R.id.SpinnerHodidayDurationEnd);
		spHolidayDurationEnd.setOnItemSelectedListener(holidayBorderAdaptor);
	}
}
