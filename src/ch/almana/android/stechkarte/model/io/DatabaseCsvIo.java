package ch.almana.android.stechkarte.model.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.IAccess;

public abstract class DatabaseCsvIo {

	protected static final String LOG_TAG = Logger.TAG;
	private static final String SEPARATOR = "\t";
	private static final String HEADER_LINEINDICATOR = "H";
	private static final String DATA_LINEINDICATOR = "D";
	private BufferedReader reader;
	private String[] columnNames;

	protected abstract String buildFilename(String filestem);

	public DatabaseCsvIo() {
		super();
	}

	public static boolean checkOrCreateDirectory(File parentFile) {
		if (parentFile == null) {
			return false;
		}
		if (parentFile.isDirectory()) {
			return true;
		}
		if (!parentFile.mkdir()) {
			if (checkOrCreateDirectory(parentFile.getParentFile())) {
				return parentFile.mkdir();
			}
		}
		return false;
	}

	public void writeCursor(Cursor c, String filestem) {
		if (c.getCount() < 1) {
			return;
		}
		String filename = buildFilename(filestem);
		BufferedWriter writer = null;
		try {
			File path = new File(filename);
			if (!checkOrCreateDirectory(path.getParentFile())) {
				return;
			}
			writeCursor(c, new FileWriter(filename));
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

	public void writeCursor(Cursor c, Writer writer) throws IOException {
		if (!c.moveToFirst()) {
			return;
		}
		BufferedWriter bWriter = new BufferedWriter(writer);
		writeHeaderLine(bWriter, c);
		writeDataLine(bWriter, c);
		while (c.moveToNext()) {
			writeDataLine(bWriter, c);
		}
		bWriter.flush();
	}

	public void readCursor(Reader reader, IAccess iAccess, Uri contentUri, String indexName) {
		readHeaderLine();
		ContentValues values = new ContentValues();
		while (readDataLine(values)) {
			Cursor c = iAccess.query(indexName + "=" + values.getAsLong(indexName));
			if (!c.moveToFirst()) {
				iAccess.insert(contentUri, values);
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
	}

	public void readCursor(String filename, IAccess iAccess, Uri contentUri, String indexName) {
		try {
			reader = new BufferedReader(new FileReader(filename));
			readCursor(reader, iAccess, contentUri, indexName);
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Unable to process file " + filename + " for reading", e);
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

	private boolean writeHeaderLine(BufferedWriter writer, Cursor c) {
		return writeLine(writer, buildHeaderLine(c));
	}

	private boolean writeDataLine(BufferedWriter writer, Cursor c) {
		return writeLine(writer, buildDataLine(c));
	}

	private boolean writeLine(BufferedWriter writer, String s) {
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