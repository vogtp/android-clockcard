package ch.almana.android.stechkarte.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.utils.StechkarteBackupAgentHelper;

public class StechkartePreferenceActivity extends PreferenceActivity {

	public static final int PAY_TAB_WEEK = 1;
	public static final int PAY_TAB_MONTH = 2;
	public static final int PAY_TAB_HIDE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Settings settings = Settings.getInstance();
		if (settings.hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.preferencesTitle);
		} else {
			setTitle(getString(R.string.app_name) + ": " + getString(R.string.preferencesTitle));
		}

		boolean payVersion = settings.isPayVersion();


		Preference payRateOvertimePreference = findPreference(getString(R.string.prefKeyPaymentOvertime));
		payRateOvertimePreference.setEnabled(payVersion);
		Preference payRatePreference = findPreference(getString(R.string.prefKeyPaymentRegular));
		payRatePreference.setEnabled(payVersion);
		if (!payVersion) {
			payRateOvertimePreference.setSummary(R.string.prefPaymentLong);
			payRatePreference.setSummary(R.string.prefPaymentLong);
		}

		boolean emailExportEnabled = settings.isEmailExportEnabled();
		Preference emailPreference = findPreference(getString(R.string.prefKeyEmailAddress));
		emailPreference.setEnabled(emailExportEnabled);
		findPreference(getString(R.string.prefKeyCsvFieldSeparator)).setEnabled(emailExportEnabled);

		StringBuffer versionSB = new StringBuffer();
		versionSB.append(getString(R.string.label_version)).append(" ").append(settings.getVersionName());
		findPreference("prefKeyVersion").setTitle(versionSB.toString());
		findPreference("prefKeyChangelog").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(StechkartePreferenceActivity.this, ChangelogActivity.class);
				startActivity(i);
				return true;
			}
		});

		findPreference("prefKeyVersion").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				Uri fromParts = Uri.parse("market://search?q=pname:" + getPackageName());
				i.setData(fromParts);
				startActivity(i);
				return true;
			}
		});

		findPreference("prefKeyBackupRestore").setEnabled(settings.isBackupEnabled());
		findPreference("prefKeyRestore").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (Settings.getInstance().isBackupEnabled()) {
					startActivity(new Intent(StechkartePreferenceActivity.this, BackupRestoreActivity.class));
				} else {
					DialogHelper.showFreeVersionDialog(StechkartePreferenceActivity.this);
				}
				return true;
			}
		});
		
	}

	@Override
	protected void onPause() {
		StechkarteBackupAgentHelper.dataChanged();
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		StechkarteBackupAgentHelper.dataChanged();
		super.onSaveInstanceState(outState);
	}

}
