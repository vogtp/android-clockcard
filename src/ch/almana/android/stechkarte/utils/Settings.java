package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

public class Settings {

	private static float hoursTargetDefault = 8.4f;

	private static Settings instance;
	private Context context;

	private Settings(Context ctx) {
		this.context = ctx;
	}

	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}

	public static Settings getInstance() {
		return instance;
	}

	private SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	private int getPrefAsInt(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Integer.parseInt(prefValue);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private float getPrefAsFloat(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Float.parseFloat(prefValue);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private long getPrefAsLong(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Long.parseLong(prefValue);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private String getPrefAsString(int key, int defaultValueKey) {
		String defaultValue = context.getResources().getString(defaultValueKey);
		String prefKey = context.getResources().getString(key);
		return getPreferences().getString(prefKey, defaultValue);
	}

	private boolean getPrefAsBoolean(int key, int defaultValueKey) throws Exception {
		String defaultValueString = context.getResources().getString(defaultValueKey);
		boolean defaultValue;
		if ("true".equalsIgnoreCase(defaultValueString)) {
			defaultValue = true;
		} else if ("false".equalsIgnoreCase(defaultValueString)) {
			defaultValue = false;
		} else {
			throw new Exception();
		}
		String prefKey = context.getResources().getString(key);
		return getPreferences().getBoolean(prefKey, defaultValue);
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
		return true;
	}

}
