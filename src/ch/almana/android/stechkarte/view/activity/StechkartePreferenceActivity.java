package ch.almana.android.stechkarte.view.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import ch.almana.android.stechkarte.R;
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
		if (Settings.getInstance().hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.preferencesTitle);
		} else {
			setTitle(getString(R.string.app_name) + ": " + getString(R.string.preferencesTitle));
		}

		Preference buyPreference = findPreference(getString(R.string.prefKeyBuy));
		boolean payVersion = Settings.getInstance().isPayVersion();
		buyPreference.setEnabled(!payVersion);
		buyPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				BuyFullVersion.startClockCardInstall(StechkartePreferenceActivity.this);
				return true;
			}
		});

		Preference payRateOvertimePreference = findPreference(getString(R.string.prefKeyPaymentOvertime));
		payRateOvertimePreference.setEnabled(payVersion);
		Preference payRatePreference = findPreference(getString(R.string.prefKeyPaymentRegular));
		payRatePreference.setEnabled(payVersion);
		if (!payVersion) {
			payRateOvertimePreference.setSummary(R.string.prefPaymentLong);
			payRatePreference.setSummary(R.string.prefPaymentLong);
		}

		boolean emailExportEnabled = Settings.getInstance().isEmailExportEnabled();
		Preference emailPreference = findPreference(getString(R.string.prefKeyEmailAddress));
		emailPreference.setEnabled(emailExportEnabled);
		findPreference(getString(R.string.prefKeyDecimalFormat)).setEnabled(false);
		findPreference(getString(R.string.prefKeyCsvFieldSeparator)).setEnabled(emailExportEnabled);
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
