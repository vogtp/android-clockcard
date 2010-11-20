package ch.almana.android.stechkarte.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.view.BuyFullVersion;

public class Settings extends SettingsBase {

	private static float hoursTargetDefault = 8.4f;
	private static final long SECONDS_IN_MILLIES = 1000;
	private static final int MIN_LICENSE_VERSION = 201011190;
	private static final String MARKETLICENSE_PACKEBAME = "ch.almana.android.stechkarteLicense";
	private static final String NONMARKETLICENSE_PACKENAME = "ch.almana.android.stechkarteLicenseNonMarket";
	private static final int MIN_NON_MARKET_LICENSE_VERSION = 0;

	private boolean featuresChanged = false;
	static final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE");

	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}

	private Settings(Context ctx) {
		super(ctx);
		// FIXME remove since its only for update 1.0 -> 1.0.1
		String key = context.getResources().getString(R.string.prefKeyMinTimestampDiff);
		SharedPreferences preferences = getPreferences();
		if (preferences.contains(key)) {
			String keyNew = context.getResources().getString(R.string.prefKeyMinTimestampDiffInSecs);
			Editor editor = preferences.edit();
			int minutes = Integer.parseInt(preferences.getString(key, "1")) * 60;
			editor.putString(keyNew, minutes + "");
			editor.remove(key);
			editor.commit();
		}
		// END remove since its only for update 1.0 -> 1.0.1
		// FIXME remove since its only for update 1.2.2 -> 1.2.3
		String value = getCsvSeparator();
		if (value != null && value.contains("\t")) {
			value = value.replace("\t", "\\t");
			key = context.getResources().getString(R.string.prefKeyCsvFieldSeparator);
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
		// END remove since its only for update 1.0 -> 1.0.1
	}

	public float getHoursTarget(long dayref) {
		float hoursTarget = -1;
		try {
			long timestamp = DayAccess.timestampFromDayRef(dayref);
			String prefKey = context.getString(R.string.prefKeyWorkHoursWeekBase);
			prefKey = prefKey + weekdayFormat.format(timestamp);
			hoursTarget = Float.parseFloat(getPreferences().getString(prefKey, "-1"));
		} catch (Exception e) {
			Log.w(Logger.LOG_TAG, "Error parsing setting hours per weekday", e);
			hoursTarget = -1;
		}
		if (hoursTarget < 0) {
			try {
				hoursTarget = getPrefAsFloat(R.string.prefKeyHoursPerDay, R.string.prefHoursPerDayDefault);
			} catch (Exception e) {
				Log.w(Logger.LOG_TAG, "Error parsing setting hours per day", e);
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
			Log.d(Logger.LOG_TAG, "Exception while looking for  license", e);
		}
		Log.i(Logger.LOG_TAG, "No license found");
		return false;
	}

	private boolean checkMarketLicense(Signature[] mySignatures, PackageInfo packageInfo) {
		Log.d(Logger.LOG_TAG, "Found package: " + packageInfo.packageName);
		if (packageInfo.versionCode >= MIN_LICENSE_VERSION) {
			Signature[] signatures = packageInfo.signatures;

			if (Arrays.equals(signatures, mySignatures)) {
				Log.i(Logger.LOG_TAG, "Found valid license");
				return true;
			} else {
				Toast.makeText(context, "Wrong license signature.", Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(context, "License version to low, please update.", Toast.LENGTH_LONG).show();
			BuyFullVersion.startClockCardInstall(context);
		}
		return false;
	}

	private boolean checkNonMarketLicense(Signature[] mySignatures, PackageInfo packageInfo) {
		Log.d(Logger.LOG_TAG, "Found package: " + packageInfo.packageName);
		if (packageInfo.versionCode >= MIN_NON_MARKET_LICENSE_VERSION) {
			Signature[] signatures = packageInfo.signatures;

			if (Arrays.equals(signatures, mySignatures)) {
				Log.i(Logger.LOG_TAG, "Found valid license");
				// return true;
			} else {
				Toast.makeText(context, "Wrong license signature.", Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(context, "License version to low, please update.", Toast.LENGTH_LONG).show();
			BuyFullVersion.startClockCardInstall(context);
		}
		return false;
	}

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
		DecimalFormat df = null;
		try {
			String format = getPrefAsString(R.string.prefKeyDecimalFormat, R.string.prefDecimalFormatDefault);
			if (format != null && !format.trim().equals("")) {
				df = new DecimalFormat(format);
			}
		} catch (Exception e) {
			Log.e(Logger.LOG_TAG, "Error getting decimalformat from perf", e);
			// df = new DecimalFormat();
			// // context
			// // .getString(R.string.prefDecimalFormatDefault));
			// df.setMaximumFractionDigits(2);
		}
		if (df == null) {
			df = (DecimalFormat) NumberFormat.getNumberInstance();
		}
		return df;
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
			Log.w(Logger.LOG_TAG, "Error getting min overtime reset time", e);
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
			Log.e(Logger.LOG_TAG, "Error parsing overtime pay rate", e);
			return 0;
		}
	}

	public float getPayRate() {
		try {
			return getPrefAsFloat(R.string.prefKeyPaymentRegular, R.string.prefKeyPaymentRegularDefault);
		} catch (Exception e) {
			Log.e(Logger.LOG_TAG, "Error parsing regular pay rate", e);
			return 0;
		}
	}

	public boolean isShowPayTab() {
		return getPrefAsBoolean(R.string.prefKeyPaymentShow, true);
	}
}
