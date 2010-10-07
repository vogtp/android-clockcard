package ch.almana.android.stechkarte.model.io;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.db.DB;

public class StechkarteCsvIO extends DatabaseCsvIo {

	private static final String CSV_ENDING = ".csv";
	public static final String DIRECTORY = "/clockcard/backup/";

	public StechkarteCsvIO() {
	}

	public static String getBasePath() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY;
		DatabaseCsvIo.checkOrCreateDirectory(new File(path));
		return path;
	}

	static public String getPath(String time) {
		String pathName = getBasePath() + time + "/";
		File path = new File(pathName);
		if (!path.isDirectory()) {
			if (!path.mkdirs()) {
				Log.e(LOG_TAG, "Cannot create " + path.getAbsolutePath());
			}
		}
		return pathName;
	}

	private static SimpleDateFormat simpleDatetimeFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	protected String buildFilename(String filenameStem) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String timeString = simpleDatetimeFormat.format(calendar.getTime());
		return getPath(timeString) + filenameStem + CSV_ENDING;
	}

	static public String filenameTimestampsStem() {
		return "timestamps";
	}

	private String filenameDaysStem() {
		return "days";
	}

	public void writeTimestamps(Cursor c) {
		writeCursor(c, filenameTimestampsStem());
	}

	public void writeDays(Cursor c) {
		writeCursor(c, filenameDaysStem());
	}

	public void restoreTimestamps(String filename) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getBasePath());
		stringBuilder.append(filename);
		stringBuilder.append("/");
		stringBuilder.append(filenameTimestampsStem() + CSV_ENDING);
		readCursor(stringBuilder.toString(), TimestampAccess.getInstance(), DB.Timestamps.CONTENT_URI, DB.Timestamps.NAME_TIMESTAMP);
	}

	public void restoreDays(String filename) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getBasePath());
		stringBuilder.append(filename);
		stringBuilder.append("/");
		stringBuilder.append(filenameDaysStem() + CSV_ENDING);
		readCursor(stringBuilder.toString(), DayAccess.getInstance(), DB.Days.CONTENT_URI, DB.Days.NAME_DAYREF);
	}
}
