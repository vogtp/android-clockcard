package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import ch.almana.android.stechkarte.R;

public class TabbedMainActivity extends TabActivity {

	public static Activity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tabCheckin").setIndicator("Main", getResources().getDrawable(R.drawable.tab_main))
				.setContent(new Intent(this, CheckinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		tabHost.addTab(tabHost.newTabSpec("tabDays").setIndicator("Days", getResources().getDrawable(R.drawable.tab_day))
				.setContent(new Intent(this, ListDays.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		// tabHost.addTab(tabHost.newTabSpec("tabWekk").setIndicator("Weeks",
		// getResources().getDrawable(R.drawable.tab_week))
		// .setContent(new Intent(this,
		// ListDays.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		// tabHost.addTab(tabHost.newTabSpec("tabMonth").setIndicator("Months",
		// getResources().getDrawable(R.drawable.tab_month))
		// .setContent(new Intent(this,
		// ListDays.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		instance = this;
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

}
