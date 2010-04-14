package ch.almana.android.stechkarte.model.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.db.DB;

public class TimestampsCsvIO {

	public static final String DIRECTORY = "/stechkarte/";

	private static final String LOG_TAG = Logger.LOG_TAG;

	private static final String SEPARATOR = "\t";

	private static final String HEADER_LINEINDICATOR = "H";
	private static final String DATA_LINEINDICATOR = "D";

	private BufferedWriter writer;
	private BufferedReader reader;

	private String[] columnNames;

	public TimestampsCsvIO() {
		File path = new File(getPath());
		if (!path.isDirectory()) {
			if (!path.mkdir()) {
				Log.e(LOG_TAG, "Cannot create " + path.getAbsolutePath());
			}
		}
	}

	static public String getPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY;
	}

	private static SimpleDateFormat simpleDatetimeFormat = new SimpleDateFormat("yyyyMMdd");

	private String buildFilename() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		String timeString = simpleDatetimeFormat.format(calendar.getTime());
		return getPath() + "timestamps" + timeString + ".csv";
	}

	public void writeTimestamps(Cursor c) {
		String filename = buildFilename();
		try {
			if (!c.moveToFirst()) {
				return;
			}
			writer = new BufferedWriter(new FileWriter(filename));
			writeHeaderLine(c);
			writeDataLine(c);
			while (c.moveToNext()) {
				writeDataLine(c);
			}
			writer.flush();
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to process file " + filename + " for writing", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error to closing file " + filename, e);
				}
				writer = null;
			}
		}
	}

	public void readTimestamps(String filename, TimestampAccess timestampAccess) {
		try {
			reader = new BufferedReader(new FileReader(filename));
			readHeaderLine();
			ContentValues values = new ContentValues();
			while (readDataLine(values)) {
				Cursor c = timestampAccess.query(DB.Timestamps.NAME_TIMESTAMP + "="
						+ values.getAsLong(DB.Timestamps.NAME_TIMESTAMP));
				if (!c.moveToFirst()) {
					timestampAccess.insert(DB.Timestamps.CONTENT_URI, values);
				}
				if (c != null && !c.isClosed()) {
					c.close();
				}
			}
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Unable to process file " + buildFilename() + " for reading", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Error to closing file " + filename, e);
				}
				reader = null;
			}
		}

	}

	private boolean writeHeaderLine(Cursor c) {
		return writeLine(buildHeaderLine(c));
	}

	private boolean writeDataLine(Cursor c) {
		return writeLine(buildDataLine(c));
	}

	private boolean writeLine(String s) {
		if (writer == null) {
			return false;
		}
		try {
			writer.write(s);
			writer.newLine();
		} catch (Exception e) {
			Log.e(LOG_TAG, "error writing csv line", e);
			return false;
		}
		return true;
	}

	private boolean readHeaderLine() {
		if (reader == null) {
			return false;
		}
		try {
			String line = reader.readLine();
			return parseHeaderLine(line);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error reading csv header line", e);
			return false;
		}
	}

	private boolean parseHeaderLine(String line) {
		String[] values = line.split(SEPARATOR);
		if (!HEADER_LINEINDICATOR.equals(values[0])) {
			return false;
		}
		columnNames = new String[values.length];
		columnNames[0] = "INDICATOR";
		for (int i = 1; i < values.length; i++) {
			columnNames[i] = values[i];
		}
		return true;
	}

	private boolean readDataLine(ContentValues values) {
		if (reader == null) {
			return false;
		}
		try {
			String line = reader.readLine();
			return parseDataLine(line, values);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error reading csv line", e);
			return false;
		}
	}

	private boolean parseDataLine(String line, ContentValues contentValues) {
		if (TextUtils.isEmpty(line)) {
			return false;
		}
		String[] values = line.split(SEPARATOR);
		if (!DATA_LINEINDICATOR.equals(values[0])) {
			return false;
		}
		for (int i = 1; i < values.length; i++) {
			String columnName = columnNames[i];
			if (DB.NAME_ID.equals(columnName)) {
				continue;
			}
			contentValues.put(columnName, values[i]);
		}
		return true;
	}

	private String buildHeaderLine(Cursor c) {
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER_LINEINDICATOR).append(SEPARATOR);
		for (int i = 0; i < c.getColumnCount(); i++) {
			String columnName = c.getColumnName(i);
			if (DB.NAME_ID.equals(columnName)) {
				continue;
			}
			sb.append(columnName);
			sb.append(SEPARATOR);
		}
		Log.d(LOG_TAG, "CSV Header Line: " + sb);
		return sb.toString();
	}

	private String buildDataLine(Cursor c) {
		StringBuilder sb = new StringBuilder();
		sb.append(DATA_LINEINDICATOR).append(SEPARATOR);
		for (int i = 0; i < c.getColumnCount(); i++) {
			String columnName = c.getColumnName(i);
			if (DB.NAME_ID.equals(columnName)) {
				continue;
			}
			sb.append(c.getString(i));
			sb.append(SEPARATOR);
		}
		Log.d(LOG_TAG, "CSV Data Line:  " + sb);
		return sb.toString();
	}

}
