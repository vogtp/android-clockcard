package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public abstract class SettingsBase {

	protected static Settings instance;
	protected final Context context;

	public static void initInstance(Context ctx) {

	}

	public static Settings getInstance() {
		return instance;
	}

	public SettingsBase(Context ctx) {
		super();
		this.context = ctx;
	}

	protected SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	protected int getPrefAsInt(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Integer.parseInt(prefValue);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	protected float getPrefAsFloat(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Float.parseFloat(prefValue);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	protected long getPrefAsLong(int key, int defaultValueKey) throws Exception {
		String prefValue = getPrefAsString(key, defaultValueKey);
		try {
			return Long.parseLong(prefValue);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	protected String getPrefAsString(int key, int defaultValueKey) {
		String defaultValue = context.getResources().getString(defaultValueKey);
		String prefKey = context.getResources().getString(key);
		return getPreferences().getString(prefKey, defaultValue);
	}

	protected boolean getPrefAsBoolean(int key, int defaultValueKey) throws Exception {
		String defaultValueString = context.getResources().getString(defaultValueKey);
		boolean defaultValue;
		if ("true".equalsIgnoreCase(defaultValueString)) {
			defaultValue = true;
		} else if ("false".equalsIgnoreCase(defaultValueString)) {
			defaultValue = false;
		} else {
			throw new Exception("Cannot parse as boolean");
		}
		return getPrefAsBoolean(key, defaultValue);
	}

	protected boolean getPrefAsBoolean(int key, boolean defaultValue) {
		String prefKey = context.getResources().getString(key);
		return getPreferences().getBoolean(prefKey, defaultValue);
	}

	protected void putLong(String prefKey, long val) {
		Editor editor = getPreferences().edit();
		editor.putLong(prefKey, val);
		editor.commit();
	}

	protected void putString(String prefKey, String val) {
		Editor editor = getPreferences().edit();
		editor.putString(prefKey, val);
		editor.commit();
	}

}