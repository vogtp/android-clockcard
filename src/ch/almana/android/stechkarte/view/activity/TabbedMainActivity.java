package ch.almana.android.stechkarte.view.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.calc.RebuildDaysTask;
import ch.almana.android.stechkarte.utils.MenuHelper;
import ch.almana.android.stechkarte.utils.Settings;
import ch.almana.android.stechkarte.view.adapter.TabManager;
import ch.almana.android.stechkarte.view.adapter.ViewPagerManager;
import ch.almana.android.stechkarte.view.fragment.CheckinFragment;
import ch.almana.android.stechkarte.view.fragment.DaysListFragment;
import ch.almana.android.stechkarte.view.fragment.HolidaysListFragment;
import ch.almana.android.stechkarte.view.fragment.MonthsListFragment;
import ch.almana.android.stechkarte.view.fragment.PaymentMonthListFragment;
import ch.almana.android.stechkarte.view.fragment.PaymentWeekListFragment;
import ch.almana.android.stechkarte.view.fragment.WeeksListFragment;

public class TabbedMainActivity extends FragmentActivity {
	public static final String ACTION_TIMESTAMP_TOGGLE = "ch.almana.android.stechkarte.actions.timestampToggle";
	public static final String ACTION_TIMESTAMP_IN = "ch.almana.android.stechkarte.actions.timestampIn";
	public static final String ACTION_TIMESTAMP_OUT = "ch.almana.android.stechkarte.actions.timestampOut";
	private TabHost tabHost;
	private ViewPager viewPager;
	private ViewPagerManager viewPagerManager;

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

		setContentView(R.layout.tabbed_main_view);
		setTitle(R.string.app_name);

		Settings settings = Settings.getInstance();
		int payTabType = settings.getPayTabType();
		Class<? extends ListFragment> payList = null;

		if (payTabType == StechkartePreferenceActivity.PAY_TAB_WEEK) {
			payList = PaymentWeekListFragment.class;
		} else if (payTabType == StechkartePreferenceActivity.PAY_TAB_MONTH) {
			payList = PaymentMonthListFragment.class;
		}

		if (settings.hasHoloTheme()) {
			viewPager = new ViewPager(this);
			viewPager.setId(R.id.pager);
			setContentView(viewPager);

			final ActionBar bar = getActionBar();
			bar.setTitle(R.string.app_name);
			if (Logger.DEBUG) {
				bar.setSubtitle("DEBUG MODE" + " (" + settings.getVersionName() + ")");
			}
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			viewPagerManager = new ViewPagerManager(this, viewPager);
			viewPagerManager.addTab(bar.newTab().setText(R.string.label_tab_main), CheckinFragment.class, null);
			viewPagerManager.addTab(bar.newTab().setText(R.string.label_tab_days), DaysListFragment.class, null);
			viewPagerManager.addTab(bar.newTab().setText(R.string.label_tab_holidays), HolidaysListFragment.class, null);
			viewPagerManager.addTab(bar.newTab().setText(R.string.label_tab_weeks), WeeksListFragment.class, null);
			viewPagerManager.addTab(bar.newTab().setText(R.string.tabel_tab_months), MonthsListFragment.class, null);
			if (payList != null) {
				viewPagerManager.addTab(bar.newTab().setText(R.string.label_tab_payment), payList, null);
			}

		}
		else {
			if (Logger.DEBUG) {
				String title = getString(R.string.app_name);
				title = title + " - DEBUG MODE" + " (" + settings.getVersionName() + ")";
				setTitle(title);
			}
			tabHost = (TabHost) findViewById(android.R.id.tabhost);
			tabHost.setup();
			TabManager mTabManager = new TabManager(this, tabHost, R.id.realtabcontent);

			mTabManager.addTab(tabHost.newTabSpec("tabCheckin").setIndicator(getString(R.string.label_tab_main), getResources().getDrawable(R.drawable.tab_main)),
					CheckinFragment.class, null);
			mTabManager.addTab(tabHost.newTabSpec("tabDays").setIndicator(getString(R.string.label_tab_days), getResources().getDrawable(R.drawable.tab_day)),
					DaysListFragment.class, null);
			mTabManager.addTab(tabHost.newTabSpec("tabHolidays").setIndicator(getString(R.string.label_tab_holidays), getResources().getDrawable(R.drawable.tab_holidays)),
					HolidaysListFragment.class, null);
			mTabManager.addTab(tabHost.newTabSpec("tabWeek").setIndicator(getString(R.string.label_tab_weeks), getResources().getDrawable(R.drawable.tab_week)),
					WeeksListFragment.class, null);

			mTabManager.addTab(tabHost.newTabSpec("tabMonth").setIndicator(getString(R.string.tabel_tab_months), getResources().getDrawable(R.drawable.tab_month)),
					MonthsListFragment.class, null);
			if (payList != null) {
				mTabManager.addTab(tabHost.newTabSpec("tabMonthPay").setIndicator(getString(R.string.label_tab_payment), getResources().getDrawable(R.drawable.payment)),
						payList, null);
			}

			if (savedInstanceState != null) {
				tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		RebuildDaysTask.setActiviy(this);
		RebuildDaysTask.rebuildDaysIfNeeded(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		RebuildDaysTask.setActiviy(null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (Settings.getInstance().hasHoloTheme()) {
			outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
		} else {
			outState.putString("tab", tabHost.getCurrentTabTag());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (Settings.getInstance().hasHoloTheme()) {
			getMenuInflater().inflate(R.menu.daylist_option, menu);
			//			viewPager.getCurrentItem();
			//			viewPager.get
		}
		getMenuInflater().inflate(R.menu.general_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuHelper.handleCommonOptions(this, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		boolean emailExportEnabled = Settings.getInstance().isEmailExportEnabled();

		menu.findItem(R.id.itemExportTimestamps).setEnabled(emailExportEnabled);

		menu.findItem(R.id.itemHolidayEditor).setVisible(Settings.getInstance().isBetaVersion());

		return true;
	}
}
