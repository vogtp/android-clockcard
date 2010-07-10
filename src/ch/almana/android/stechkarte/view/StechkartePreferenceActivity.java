package ch.almana.android.stechkarte.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.utils.Settings;

public class StechkartePreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setTitle(R.string.preferencesTitle);

		Preference buyPreference = findPreference(getString(R.string.prefKeyBuy));
		buyPreference.setEnabled(!Settings.getInstance().isPayVersion());
		buyPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				BuyFullVersion.startClockCardInstall(StechkartePreferenceActivity.this);
				return true;
			}
		});

		Preference betaFeatures = findPreference(getString(R.string.prefKeyLicence));
		betaFeatures.setEnabled(Settings.getInstance().hasBetaFeatures());

		boolean emailExportEnabled = Settings.getInstance().isEmailExportEnabled();
		Preference emailPreference = findPreference(getString(R.string.prefKeyEmailAddress));
		emailPreference.setEnabled(emailExportEnabled);
		findPreference(getString(R.string.prefKeyDecimalFormat)).setEnabled(false);
		findPreference(getString(R.string.prefKeyCsvFieldSeparator)).setEnabled(emailExportEnabled);
	}

}
