package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.util.Log;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

public class Settings extends SettingsBase {
	
	private static float hoursTargetDefault = 8.4f;
	
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
	
}
