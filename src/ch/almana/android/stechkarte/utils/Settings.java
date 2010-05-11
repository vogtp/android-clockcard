package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.util.Log;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

public class Settings extends SettingsBase {
	
	private static float hoursTargetDefault = 8.4f;
	private static final long MINUTES_IN_MILLIES = 1000 * 60;
	
	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}
	
	private Settings(Context ctx) {
		super(ctx);
	}
	
	public float getHoursTarget() {
		try {
			return getPrefAsFloat(R.string.prefKeyHoursPerDay, R.string.prefHoursPerDayDefault);
		} catch (Exception e) {
			Log.w(Logger.LOG_TAG, "Error parsing setting hours per day", e);
			return hoursTargetDefault;
		}
		
	}
	
	public boolean isFreeVersion() {
		String lic = getPrefAsString(R.string.prefKeyLicence, R.string.prefLicenceDefault);
		return !"sonnenscheinInBasel".equals(lic);
	}
	
	public long getMinTimestampDiff() {
		try {
			long diff = getPrefAsLong(R.string.prefKeyMinTimestampDiff, R.string.prefMinTimestampDiffDefault);
			return diff * MINUTES_IN_MILLIES;
		} catch (Exception e) {
			return MINUTES_IN_MILLIES;
		}
	}
	
	public String getEmailAddress() {
		try {
			return getPrefAsString(R.string.prefKeyEmailAddress, R.string.prefEmailAddressDefault);
		} catch (Exception e) {
			return "";
		}
		
	}
}
