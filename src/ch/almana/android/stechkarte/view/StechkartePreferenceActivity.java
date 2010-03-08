package ch.almana.android.stechkarte.view;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import ch.almana.android.stechkarte.R;

public class StechkartePreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}

	
}
