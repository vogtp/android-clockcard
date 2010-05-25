package ch.almana.android.stechkarte.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.utils.Settings;

public class StechkartePreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference buyPreference = findPreference(getString(R.string.prefKeyBuy));
		buyPreference.setEnabled(!Settings.getInstance().isPayVersion());
		buyPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						BuyFullVersion
								.startClockCardInstall(StechkartePreferenceActivity.this);
						return true;
					}
				});

		Preference betaFeatures = findPreference(getString(R.string.prefKeyLicence));
		betaFeatures.setEnabled(Settings.getInstance().hasBetaFeatures());

		Preference emailPreference = findPreference(getString(R.string.prefKeyEmailAddress));
		emailPreference.setEnabled(Settings.getInstance()
				.isEmailExportEnabled());
	}

}
