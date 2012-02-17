package ch.almana.android.stechkarte.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.utils.TabManager;
import ch.almana.android.stechkarte.view.fragment.CheckinFragment;
import ch.almana.android.stechkarte.view.fragment.DaysListFragment;
import ch.almana.android.stechkarte.view.fragment.MonthsListFragment;
import ch.almana.android.stechkarte.view.fragment.PaymentMonthListFragment;
import ch.almana.android.stechkarte.view.fragment.PaymentWeekListFragment;
import ch.almana.android.stechkarte.view.fragment.WeeksListFragment;

public class TabbedMainActivity extends FragmentActivity {
	public static final String ACTION_TIMESTAMP_TOGGLE = "ch.almana.android.stechkarte.actions.timestampToggle";
	public static final String ACTION_TIMESTAMP_IN = "ch.almana.android.stechkarte.actions.timestampIn";
	public static final String ACTION_TIMESTAMP_OUT = "ch.almana.android.stechkarte.actions.timestampOut";
	public static Activity instance; // FIXME remove?
	private TabHost tabHost;

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		String action = getIntent().getAction();
		try {
			if (ACTION_TIMESTAMP_IN.equals(action)) {
				TimestampAccess.getInstance().addInNow(this);
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
		}

		instance = this;

		setContentView(R.layout.tabbed_main_view);

		int payTabType = Settings.getInstance().getPayTabType();
		Class<? extends ListFragment> payList = null; 
		
		if (payTabType > StechkartePreferenceActivity.PAY_TAB_HIDE) {
			payList = PaymentMonthListFragment.class;
			if (payTabType == StechkartePreferenceActivity.PAY_TAB_WEEK) {
				payList = PaymentWeekListFragment.class;
			}
		}

		if (Settings.getInstance().hasHoloTheme()) {

			final ActionBar bar = getActionBar();
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

			//			Tab tab = bar.newTab().setText(R.string.label_tab_main);
			//			android.app.ActionBar.TabListener listener = new TabListener() {
			//				
			//				@Override
			//				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			//					// TODO Auto-generated method stub
			//					
			//				}
			//				
			//				@Override
			//				public void onTabSelected(Tab tab, FragmentTransaction ft) {
			//					// TODO Auto-generated method stub
			//					
			//				}
			//				
			//				@Override
			//				public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//					// TODO Auto-generated method stub
			//					
			//				}
			//			};
			//			tab.setTabListener(listener );
			//			bar.addTab(tab );
			//			
			//			bar.addTab(bar.newTab().setText(R.string.label_tab_main).setTabListener(new TabListener<CheckinFragment>(
			//					this, R.string.label_tab_main, CheckinFragment.class)));
			//			bar.addTab(bar.newTab()
			//					.setText("Contacts")
			//					.setTabListener(new TabListener<LoaderCursor.CursorLoaderListFragment>(
			//							this, "contacts", LoaderCursor.CursorLoaderListFragment.class)));
			//			bar.addTab(bar.newTab()
			//					.setText("Apps")
			//					.setTabListener(new TabListener<LoaderCustom.AppListFragment>(
			//							this, "apps", LoaderCustom.AppListFragment.class)));
			//			bar.addTab(bar.newTab()
			//					.setText("Throttle")
			//					.setTabListener(new TabListener<LoaderThrottle.ThrottledLoaderListFragment>(
			//							this, "throttle", LoaderThrottle.ThrottledLoaderListFragment.class)));
			//
			//			if (savedInstanceState != null) {
			//				bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
			//			}
		}
		//		else {

			tabHost = (TabHost) findViewById(android.R.id.tabhost);
			tabHost.setup();
			TabManager mTabManager = new TabManager(this, tabHost, R.id.realtabcontent);

			mTabManager.addTab(tabHost.newTabSpec("tabCheckin").setIndicator(getString(R.string.label_tab_main), getResources().getDrawable(R.drawable.tab_main)),
					CheckinFragment.class, null);
			mTabManager.addTab(tabHost.newTabSpec("tabDays").setIndicator(getString(R.string.label_tab_days), getResources().getDrawable(R.drawable.tab_day)),
					DaysListFragment.class, null);
			mTabManager.addTab(tabHost.newTabSpec("tabWeek").setIndicator(getString(R.string.label_tab_weeks), getResources().getDrawable(R.drawable.tab_week)),
					WeeksListFragment.class, null);

			mTabManager.addTab(tabHost.newTabSpec("tabMonth").setIndicator("Months", getResources().getDrawable(R.drawable.tab_month)),
					MonthsListFragment.class, null);
			if (payList != null) {
				mTabManager.addTab(tabHost.newTabSpec("tabMonthPay").setIndicator(getString(R.string.label_tab_payment), getResources().getDrawable(R.drawable.payment)),
						payList, null);
			}

			if (savedInstanceState != null) {
				tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
			}
		//		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (payTabType != Settings.getInstance().getPayTabType()) {
		// getTabHost().clearAllTabs();
		// initTabs();
		// }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//		if (Settings.getInstance().hasHoloTheme()) {
		//			outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
		//		} else {
			outState.putString("tab", tabHost.getCurrentTabTag());
		//		}
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
		moreItems.getSubMenu().findItem(R.id.itemReadInTimestmaps).setEnabled(backupEnabled);

		menu.findItem(R.id.itemHolidayEditor).setVisible(Settings.getInstance().isBetaVersion());

		return true;
	}
}
