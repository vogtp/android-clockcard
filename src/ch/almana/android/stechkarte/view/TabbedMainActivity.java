package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class TabbedMainActivity extends TabActivity {

	public static Activity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		String action = getIntent().getAction();
		try {
			if (CheckinActivity.ACTION_TIMESTAMP_IN.equals(action)) {
				if (TimestampAccess.getInstance().addInNow(this)) {
					finish();
				}
			} else if (CheckinActivity.ACTION_TIMESTAMP_OUT.equals(action)) {
				if (TimestampAccess.getInstance().addOutNow(this)) {
					finish();
				}
			} else if (CheckinActivity.ACTION_TIMESTAMP_TOGGLE.equals(action)) {
				if (TimestampAccess.getInstance().addToggleTimestampNow(this)) {
					finish();
				}
			}
		} finally {

		}
		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tabCheckin").setIndicator("Main", getResources().getDrawable(R.drawable.tab_main))
				.setContent(new Intent(this, CheckinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		tabHost.addTab(tabHost.newTabSpec("tabDays").setIndicator("Days", getResources().getDrawable(R.drawable.tab_day))
				.setContent(new Intent(this, ListDays.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		// tabHost.addTab(tabHost.newTabSpec("tabWekk").setIndicator("Weeks",
		// getResources().getDrawable(R.drawable.tab_week))
		// .setContent(new Intent(this,
		// ListDays.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		tabHost.addTab(tabHost.newTabSpec("tabMonth").setIndicator("Months", getResources().getDrawable(R.drawable.tab_month))
				.setContent(new Intent(this, ListMonths.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		instance = this;
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.general_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		// case R.id.itemDaysList:
		// i = new Intent(this, ListDays.class);
		// startActivity(i);
		// break;
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
}
