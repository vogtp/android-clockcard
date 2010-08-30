package ch.almana.android.stechkarte.utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class StechkarteBackupAgent extends BackupAgentHelper {

	// A key to uniquely identify the set of backup data
	static final String PREFS_BACKUP_KEY = "clockcardPrefs";

	// Allocate a helper and add it to the backup agent
	public void onCreate() {
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, getDefaultPrefsName());
		addHelper(PREFS_BACKUP_KEY, helper);
	}

	private String getDefaultPrefsName() {
		return this.getPackageName() + "_preferences";
	}

}
