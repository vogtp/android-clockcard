package ch.almana.android.stechkarte.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

public class Settings extends SettingsBase {

	private static float hoursTargetDefault = 8.4f;
	private static final long SECONDS_IN_MILLIES = 1000;

	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}

	private Settings(Context ctx) {
		super(ctx);
		// FIXME remove since its only for update 1.0 -> 1.0.1
		String key = context.getResources().getString(
				R.string.prefKeyMinTimestampDiff);
		SharedPreferences preferences = getPreferences();
		if (preferences.contains(key)) {
			String keyNew = context.getResources().getString(
					R.string.prefKeyMinTimestampDiffInSecs);
			Editor editor = preferences.edit();
			int minutes = Integer.parseInt(preferences.getString(key, "1")) * 60;
			editor.putString(keyNew, minutes + "");
			editor.remove(key);
			editor.commit();
		}
		// END remove since its only for update 1.0 -> 1.0.1
	}

	public float getHoursTarget() {
		try {
			return getPrefAsFloat(R.string.prefKeyHoursPerDay,
					R.string.prefHoursPerDayDefault);
		} catch (Exception e) {
			Log.w(Logger.LOG_TAG, "Error parsing setting hours per day", e);
			return hoursTargetDefault;
		}

	}

	private boolean checkLicense() {
		String packageName = "ch.almana.android.stechkarteLicense";
		ComponentName componentName = new ComponentName(context,
				packageName);
		try {
			context.getPackageManager().getActivityInfo(componentName,
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			Log.d(Logger.LOG_TAG, packageName + " for license not found", e);
		}
		return false;
	}

	public boolean isFreeVersion() {
		String lic = getPrefAsString(R.string.prefKeyLicence,
				R.string.prefLicenceDefault);
		return !"sonnenscheinInBasel".equals(lic);
	}

	public long getMinTimestampDiff() {
		try {
			long diff = getPrefAsLong(R.string.prefKeyMinTimestampDiffInSecs,
					R.string.prefMinTimestampDiffDefault);
			return diff * SECONDS_IN_MILLIES;
		} catch (Exception e) {
			return SECONDS_IN_MILLIES;
		}
	}

	public String getEmailAddress() {
		try {
			return getPrefAsString(R.string.prefKeyEmailAddress,
					R.string.prefEmailAddressDefault);
		} catch (Exception e) {
			return "";
		}

	}
}
