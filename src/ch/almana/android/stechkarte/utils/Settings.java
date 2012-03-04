package ch.almana.android.stechkarte.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.calc.RebuildDaysTask;
import ch.almana.android.stechkarte.view.activity.BuyFullVersion;

public class Settings extends SettingsBase {

	private static float hoursTargetDefault = 8.4f;
	private static final long SECONDS_IN_MILLIES = 1000;
	private static final int MIN_LICENSE_VERSION = 201011190;
	private static final String MARKETLICENSE_PACKEBAME = "ch.almana.android.stechkarteLicense";
	private static final String PREF_DEFAULT_PROFILES_VERSION = "prefKeyDefaultProfileVersion";

	private boolean featuresChanged = false;
	static final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE");
	protected static Settings instance;

	public static Settings getInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
		return instance;
	}

	public static Settings getInstance() {
		return instance;
	}
	private Settings(Context ctx) {
		super(ctx);
	}

	public float getHoursTarget(long dayref) {
		float hoursTarget = -1;
		try {
			long timestamp = DayAccess.timestampFromDayRef(dayref);
			String prefKey = context.getString(R.string.prefKeyWorkHoursWeekBase);
			prefKey = prefKey + weekdayFormat.format(timestamp);
			hoursTarget = Float.parseFloat(getPreferences().getString(prefKey, "-1"));
		} catch (Exception e) {
			Log.w(Logger.TAG, "Error parsing setting hours per weekday", e);
			hoursTarget = -1;
		}
		if (hoursTarget < 0) {
			try {
				hoursTarget = getPrefAsFloat(R.string.prefKeyHoursPerDay, R.string.prefHoursPerDayDefault);
			} catch (Exception e) {
				Log.w(Logger.TAG, "Error parsing setting hours per day", e);
				hoursTarget = hoursTargetDefault;
			}
		}

		return hoursTarget;
	}

	private boolean checkLicense() {
		// ComponentName componentName = new ComponentName(context,
		// packageName);
		try {
			PackageManager pm = context.getPackageManager();
			Signature[] mySignatures = context.getApplicationContext().getPackageManager()
					.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
			List<PackageInfo> preferredPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
			for (Iterator<PackageInfo> iterator = preferredPackages.iterator(); iterator.hasNext();) {
				PackageInfo packageInfo = iterator.next();
				// Log.d(Logger.LOG_TAG, "Package: " + packageInfo.packageName);
				if (MARKETLICENSE_PACKEBAME.equals(packageInfo.packageName)) {
					if (checkMarketLicense(mySignatures, packageInfo)) {
						featuresChanged = true;
						return true;
					}
				}
				// else if
				// (NONMARKETLICENSE_PACKEBAME.equals(packageInfo.packageName))
				// {
				// if (checkNonMarketLicense(mySignatures, packageInfo)) {
				// featuresChanged = true;
				// return true;
				// }
				// }
			}

			// PackageInfo packageInfo = pm.getPackageInfo(packageName,
			// PackageManager.GET_SIGNATURES);

		} catch (Exception e) {
			Log.d(Logger.TAG, "Exception while looking for  license", e);
		}
		Log.i(Logger.TAG, "No license found");
		return false;
	}

	private boolean checkMarketLicense(Signature[] mySignatures, PackageInfo packageInfo) {
		Log.d(Logger.TAG, "Found package: " + packageInfo.packageName);
		if (packageInfo.versionCode >= MIN_LICENSE_VERSION) {
			Signature[] signatures = packageInfo.signatures;

			if (Arrays.equals(signatures, mySignatures)) {
				Log.i(Logger.TAG, "Found valid license");
				return true;
			} else {
				Toast.makeText(context, "Wrong license signature.", Toast.LENGTH_LONG).show();
				Log.i(Logger.TAG, "Wrong license signature.");
			}

		} else {
			Toast.makeText(context, "License version to low, please update.", Toast.LENGTH_LONG).show();
			Log.i(Logger.TAG, "License version to low, please update.");
			BuyFullVersion.startClockCardInstall(context);
		}
		return false;
	}

	// TODO make more finegrained
	public boolean isPayVersion() {
		if (checkLicense()) {
			return true;
		}
		return false;
	}

	public boolean isBetaVersion() {
		String lic = getPrefAsString(R.string.prefKeyLicence, R.string.prefLicenceDefault);
		if ("sonnenscheinInBasel".equals(lic)) {
			return true;
		}
		return false;
	}

	public long getMinTimestampDiff() {
		try {
			long diff = getPrefAsLong(R.string.prefKeyMinTimestampDiffInSecs, R.string.prefMinTimestampDiffDefault);
			return diff * SECONDS_IN_MILLIES;
		} catch (Exception e) {
			return SECONDS_IN_MILLIES;
		}
	}

	public String getEmailAddress() {
		try {
			return getPrefAsString(R.string.prefKeyEmailAddress, R.string.prefEmailAddressDefault);
		} catch (Exception e) {
			return "";
		}

	}

	public String getCsvSeparator() {
		try {
			return getPrefAsString(R.string.prefKeyCsvFieldSeparator, R.string.prefCsvFieldSeparatorDefault);
		} catch (Exception e) {
			return context.getString(R.string.prefCsvFieldSeparatorDefault);
		}
	}

	public DecimalFormat getCsvDecimalFormat() {
		return (DecimalFormat) NumberFormat.getNumberInstance();
	}

	public boolean isEmailExportEnabled() {
		return isPayVersion();
	}

	public boolean isBackupEnabled() {
		return isPayVersion();
	}

	public void setLastDaysRebuild(long currentTimeMillis) {
		putLong(RebuildDaysTask.PREF_KEY_LAST_UPDATE, currentTimeMillis);

	}

	public long getLastDaysRebuild() {
		return getPreferences().getLong(RebuildDaysTask.PREF_KEY_LAST_UPDATE, 0);
	}

	public boolean isNightshiftEnabled() {
		return getPreferences().getBoolean(context.getString(R.string.prefKeyNightshift), false);
	}

	public float getOvertimeResetValue() {
		try {
			return getPrefAsFloat(R.string.prefKeyOvertimeResetMinTime, R.string.prefOvertimeResetMinTimeDefault);
		} catch (Exception e) {
			Log.w(Logger.TAG, "Error getting min overtime reset time", e);
			return 0;
		}
	}

	public boolean isResetOvertimeIfBigger() {
		return getPreferences().getBoolean(context.getString(R.string.prefKeyOvertimeResetIfBigger), false);
	}

	private String getOvertimeResetPoint() {
		return getPreferences().getString(context.getString(R.string.prefKeyOvertimeResetPoint), "0");
	}

	public boolean isWeeklyOvertimeReset() {
		return "1".equals(getOvertimeResetPoint());
	}

	public boolean isMonthlyOvertimeReset() {
		return "2".equals(getOvertimeResetPoint());
	}

	public boolean isYearlyOvertimeReset() {
		return "3".equals(getOvertimeResetPoint());
	}

	public boolean isFeaturesChanged() {
		if (featuresChanged) {
			featuresChanged = false;
			return true;
		}
		return featuresChanged;
	}

	public boolean is24hours() {
		return getPrefAsBoolean(R.string.prefKeyIs24hours, false);
	}

	public float getPayRateOvertime() {
		try {
			return getPrefAsFloat(R.string.prefKeyPaymentOvertime, R.string.prefKeyPaymentOvertimeDefault);
		} catch (Exception e) {
			Log.e(Logger.TAG, "Error parsing overtime pay rate", e);
			return 0;
		}
	}

	public float getPayRate() {
		try {
			return getPrefAsFloat(R.string.prefKeyPaymentRegular, R.string.prefKeyPaymentRegularDefault);
		} catch (Exception e) {
			Log.e(Logger.TAG, "Error parsing regular pay rate", e);
			return 0;
		}
	}

	public int getPayTabType() {
		try {
			return Integer.parseInt(getPrefAsString(R.string.prefKeyPaymentTabType, R.string.prefPaymentTabTypeDefault));
		} catch (NumberFormatException e) {
			Log.e(Logger.TAG, "Error parsing pay tab type", e);
			return 1;
		}
	}

	public int getFirstDayOfWeek() {
		try {
			String prefAsString = getPrefAsString(R.string.prefKeyFirstDayOfWeek, R.string.prefFirstDayOfWeekDefault);
			int i = Integer.parseInt(prefAsString);
			return i;
		} catch (NumberFormatException e) {
			Log.e(Logger.TAG, "Error parsing first day of week", e);
			return -1;
		}
	}

	public boolean isUseCalendarDays() {
		// FIXME make confable
		return isBetaVersion();
	}

	public String getVersionName() {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			Logger.i("Cannot get clock card version", e);
		}
		return "";
	}

	public boolean hasHoloTheme() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public int getDefaultProfilesVersion() {
		return getLocalPreferences().getInt(PREF_DEFAULT_PROFILES_VERSION, 0);
	}

	public void setDefaultProfilesVersion(int version) {
		Editor editor = getLocalPreferences().edit();
		editor.putInt(PREF_DEFAULT_PROFILES_VERSION, version);
		editor.commit();
	}
}
