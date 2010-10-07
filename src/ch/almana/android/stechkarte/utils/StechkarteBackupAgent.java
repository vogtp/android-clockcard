package ch.almana.android.stechkarte.utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class StechkarteBackupAgent extends BackupAgentHelper {

	static final String PREFS_BACKUP_KEY = "clockcardPrefs";
	static final String DB_BACKUP_KEY = "clockcardDB";
	static final String DAYS_FILE = "days";
	static final String TIMESTAMPS_FILE = "timestamps";

	// Allocate a helper and add it to the backup agent
	public void onCreate() {
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, getDefaultPrefsName());
		addHelper(PREFS_BACKUP_KEY, helper);
		// createFiles();
		// FileBackupHelper fileHelper = new FileBackupHelper(this, DAYS_FILE,
		// TIMESTAMPS_FILE);
		// addHelper(DB_BACKUP_KEY, fileHelper);
	}

	private String getDefaultPrefsName() {
		return this.getPackageName() + "_preferences";
	}
	//
	// @Override
	// public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput
	// data, ParcelFileDescriptor newState) throws IOException {
	// createFiles();
	// super.onBackup(oldState, data, newState);
	// }
	//
	// private void createFiles() {
	// DatabaseCsvIo dbIO = getDbIo();
	// try {
	// Cursor c = TimestampAccess.getInstance().query(null, null);
	// Writer writer = new OutputStreamWriter(openFileOutput(TIMESTAMPS_FILE,
	// 0));
	// dbIO.writeCursor(c, writer);
	// c.close();
	// writer = new OutputStreamWriter(openFileOutput(DAYS_FILE, 0));
	// dbIO.writeCursor(c, writer);
	// c.close();
	// } catch (IOException e) {
	// Log.w(Logger.LOG_TAG, "Cannot create DB export file to save to cloud",
	// e);
	// }
	// }
	//
	// private DatabaseCsvIo getDbIo() {
	// return new DatabaseCsvIo() {
	// @Override
	// protected String buildFilename(String filestem) {
	// return "";
	// }
	// };
	// }
	//
	// @Override
	// public void onRestore(BackupDataInput data, int appVersionCode,
	// ParcelFileDescriptor newState) throws IOException {
	// super.onRestore(data, appVersionCode, newState);
	// loadFiles();
	// }
	//
	// private void loadFiles() {
	// DatabaseCsvIo dbIO = getDbIo();
	// Reader reader;
	// try {
	// reader = new InputStreamReader(openFileInput(TIMESTAMPS_FILE));
	// dbIO.readCursor(reader, TimestampAccess.getInstance(),
	// DB.Timestamps.CONTENT_URI, DB.Timestamps.NAME_TIMESTAMP);
	// } catch (FileNotFoundException e) {
	// Log.w(Logger.LOG_TAG, "Cannot read timestamps DB export from cloud", e);
	// }
	// try {
	// reader = new InputStreamReader(openFileInput(DAYS_FILE));
	// dbIO.readCursor(reader, DayAccess.getInstance(), DB.Days.CONTENT_URI,
	// DB.Days.NAME_DAYREF);
	// } catch (FileNotFoundException e) {
	// Log.w(Logger.LOG_TAG, "Cannot read days DB export from cloud", e);
	// }
	// }
}
