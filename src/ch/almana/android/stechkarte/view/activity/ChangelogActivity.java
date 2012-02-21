package ch.almana.android.stechkarte.view.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.utils.Settings;

public class ChangelogActivity extends Activity {

	private static final String CHANGELOG = "CHANGELOG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changelog);
		if (Settings.getInstance().hasHoloTheme()) {
			getActionBar().setSubtitle(R.string.title_changelog);
		} else {
			setTitle(getString(R.string.app_name) + ": " + getString(R.string.title_changelog));
		}
		TextView tvChangelog = (TextView) findViewById(R.id.tvChangelog);

		try {
			InputStream is = getResources().getAssets().open(CHANGELOG);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			for (int i = 0; i < 5; i++) {
				reader.readLine();
			}
			StringBuffer sb = new StringBuffer();
			String line = reader.readLine();
			while (line != null && !line.startsWith("V 1.6.1")) {
				sb.append(line).append("\n");
				line = reader.readLine();
			}
			tvChangelog.setText(sb.toString());
		} catch (IOException e) {
			Logger.w("Cannot read the changelog", e);
		}

	}
}
