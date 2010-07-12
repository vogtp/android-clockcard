package ch.almana.android.stechkarte.model.io;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class StechkarteCsvIO extends DatabaseCsvIo {

	public static final String DIRECTORY = "/clockcard/";

	public StechkarteCsvIO() {
	}

	static public String getPath() {
		String pathName = Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY;
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
		return getPath() + filenameStem + timeString + ".csv";
	}

	static public String filenameTimestampsStem() {
		return "timestamps";
	}

	public void writeTimestamps(Cursor c) {
		writeCursor(c, filenameTimestampsStem());
	}
}
