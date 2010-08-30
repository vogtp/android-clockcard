package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.utils.CurInfo;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class CheckinActivity extends Activity {

	public static final String ACTION_TIMESTAMP_TOGGLE = "ch.almana.android.stechkarte.actions.timestampToggle";
	public static final String ACTION_TIMESTAMP_IN = "ch.almana.android.stechkarte.actions.timestampIn";
	public static final String ACTION_TIMESTAMP_OUT = "ch.almana.android.stechkarte.actions.timestampOut";
	private TextView status;
	private TextView overtime;
	private TextView hoursWorked;
	private TextView leaveAt;

	private TextView holidaysLeft;
	private TextView labelLeaveAt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String action = getIntent().getAction();
		try {
			if (ACTION_TIMESTAMP_IN.equals(action)) {
				if (TimestampAccess.getInstance().addInNow(this)) {
					finish();
				}
			} else if (ACTION_TIMESTAMP_OUT.equals(action)) {
				if (TimestampAccess.getInstance().addOutNow(this)) {
					finish();
				}
			} else if (ACTION_TIMESTAMP_TOGGLE.equals(action)) {
				if (TimestampAccess.getInstance().addToggleTimestampNow(this)) {
					finish();
				}
			}
		} finally {
			if (Settings.getInstance().isBackupEnabled()) {
				BackupRestoreActivity.backupDbToCsv();
			}
		}

		setContentView(R.layout.main);

		Button buttonIn = (Button) findViewById(R.id.ButtonIn);
		Button buttonOut = (Button) findViewById(R.id.ButtonOut);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		width = Math.round(width / 2f);
		int size = Math.round(width / 5f);
		buttonIn.setWidth(width);
		buttonIn.setHeight(width);
		buttonIn.setTextSize(size);
		buttonOut.setWidth(width);
		buttonOut.setHeight(width);
		buttonOut.setTextSize(size);

		buttonIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addInNow(CheckinActivity.this);
				updateFields();
			}
		});

		buttonOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addOutNow(CheckinActivity.this);
				updateFields();
			}
		});

		status = (TextView) findViewById(R.id.TextViewStatus);
		overtime = (TextView) findViewById(R.id.TextViewOvertime);
		hoursWorked = (TextView) findViewById(R.id.TextViewHoursWorked);
		holidaysLeft = (TextView) findViewById(R.id.TextViewHolidaysLeft);
		leaveAt = (TextView) findViewById(R.id.TextViewLeave);
		labelLeaveAt = (TextView) findViewById(R.id.LabelLeavetAt);
	}

	@Override
	protected void onResume() {
		updateFields();
		super.onResume();
	}

	private void updateFields() {
		CurInfo curInfo = new CurInfo(this);

		if (curInfo.getTimestampType() == Timestamp.TYPE_IN) {
			if (curInfo.getLeaveInMillies() > 0l) {
				leaveAt.setText(curInfo.getLeaveAtString());
			} else {
				leaveAt.setText("now");
			}
			leaveAt.setVisibility(TextView.VISIBLE);
			labelLeaveAt.setVisibility(TextView.VISIBLE);
			leaveAt.setHeight(overtime.getHeight());
			labelLeaveAt.setHeight(overtime.getHeight());
		} else {
			leaveAt.setText("");
			leaveAt.setVisibility(TextView.INVISIBLE);
			leaveAt.setHeight(0);
			labelLeaveAt.setVisibility(TextView.INVISIBLE);
			labelLeaveAt.setHeight(0);
		}

		status.setText("You are " + curInfo.getInOutString());
		holidaysLeft.setText(curInfo.getHolydayLeft());

		overtime.setText(curInfo.getOvertimeString());
		hoursWorked.setText(curInfo.getHoursWorked());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.chekin_option, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem moreItems = menu.findItem(R.id.optionMore);

		boolean emailExportEnabled = Settings.getInstance().isEmailExportEnabled();
		boolean backupEnabled = Settings.getInstance().isBackupEnabled();

		moreItems.getSubMenu().findItem(R.id.itemExportTimestamps).setEnabled(emailExportEnabled);
		moreItems.getSubMenu().findItem(R.id.itemReadInTimestmaps).setVisible(backupEnabled);

		menu.findItem(R.id.itemHolidayEditor).setVisible(Settings.getInstance().isBetaVersion());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.itemDaysList:
			i = new Intent(this, ListDays.class);
			startActivity(i);
			break;
		case R.id.itemExportTimestamps:
			if (Settings.getInstance().isEmailExportEnabled()) {
				i = new Intent(this, ExportTimestamps.class);
				startActivity(i);
			} else {
				DialogHelper.showFreeVersionDialog(this);
			}
			break;

		case R.id.itemReadInTimestmaps:
			if (Settings.getInstance().isBackupEnabled()) {
				i = new Intent(this, BackupRestoreActivity.class);
				startActivity(i);
			} else {
				DialogHelper.showFreeVersionDialog(this);
			}
			break;

		case R.id.itemPreferences:
			i = new Intent(getApplicationContext(), StechkartePreferenceActivity.class);
			startActivity(i);
			break;

		case R.id.itemHolidayEditor:
			i = new Intent(this, HolidaysEditor.class);
			startActivity(i);
			break;

		case R.id.itemFAQ:
			i = new Intent(Intent.ACTION_DEFAULT, Uri.parse("http://clockcard.sourceforge.net/faq.html"));
			startActivity(i);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}