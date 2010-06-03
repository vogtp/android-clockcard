package ch.almana.android.stechkarte.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.io.TimestampsCsvIO;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.utils.Settings;

public class ExportTimestamps extends Activity {
	

	// private static final String separator = "\t";
	private static final SimpleDateFormat yyyymmddFromat = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat mmddyyyFromat = new SimpleDateFormat("MM/dd/yyyy");
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		BufferedWriter writer = null;
		String fileName = TimestampsCsvIO.getPath() + "export.csv";
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writeCSV(writer);
			writer.close();
			sendMail(fileName);
		} catch (IOException e) {
			Log.e(Logger.LOG_TAG, "Export days", e);
			Toast.makeText(this, "Cannot open " + fileName, Toast.LENGTH_LONG).show();
		} finally {
			finish();
		}
	}
	
	private String[] header = { "day", "Hours worked", "Overtime of day",
			"Overtime", "holiday", "holiday left", "IN", "OUT", "IN", "OUT",
			"IN", "OUT" };
	
	private void writeCSV(BufferedWriter writer) throws IOException {
		DayAccess dayAccess = DayAccess.getInstance();
		String separator = Settings.getInstance().getCsvSeparator();
		if ("\\t".equals(separator)) {
			separator = "\t";
		}
		DecimalFormat df = Settings.getInstance().getCsvDecimalFormat();
		Cursor cursor = dayAccess.query(null, Days.REVERSE_SORTORDER);
		for (int i = 0; i < header.length; i++) {
			writer.write(header[i]);
			writer.write(separator);
		}
		writer.write("\n");
		while (cursor.moveToNext()) {
			Day day = new Day(cursor);
			String dayString = formatDate(day.getDayString());
			writer.write("\"" + dayString + "\"");
			writer.write(separator);
			writer.write("\"" + df.format(day.getHoursWorked()) + "\"");
			writer.write(separator);
			writer.write("\"" + df.format(day.getDayOvertime()) + "\"");
			writer.write(separator);
			String overtime = df.format(day.getOvertime());
			writer.write("\"" + overtime + "\"");
			writer.write(separator);
			writer.write("\"" + df.format(day.getHolyday()) + "\"");
			writer.write(separator);
			writer.write("\"" + df.format(day.getHolydayLeft()) + "\"");
			Cursor timestamps = day.getTimestamps();
			while (timestamps.moveToNext()) {
				writer.write(separator);
				Timestamp ts = new Timestamp(timestamps);
				writer.write("\"" + ts.getHM() + "\"");
			}
			writer.write("\n");
			timestamps.close();
		}
	}
	
	private String formatDate(String dayString) {
		try {
			Date date = yyyymmddFromat.parse(dayString);
			return mmddyyyFromat.format(date);
		} catch (ParseException e) {
			Log.e(Logger.LOG_TAG, "Cannot parse " + dayString, e);
			return dayString;
		}
	}
	
	private void sendMail(String filename) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		
		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { Settings.getInstance().getEmailAddress() });
		sendIntent.putExtra(Intent.EXTRA_SUBJECT,
				getText(R.string.emailExportSubject));
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
	
}
