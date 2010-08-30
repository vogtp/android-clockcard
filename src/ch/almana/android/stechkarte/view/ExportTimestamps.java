package ch.almana.android.stechkarte.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import ch.almana.android.stechkarte.model.io.StechkarteCsvIO;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.utils.CurInfo;
import ch.almana.android.stechkarte.utils.RebuildDaysTask;
import ch.almana.android.stechkarte.utils.Settings;

public class ExportTimestamps extends Activity {

	// private static final String separator = "\t";
	private static final SimpleDateFormat yyyymmddFromat = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat mmddyyyFromat = new SimpleDateFormat("MM/dd/yyyy");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RebuildDaysTask.rebuildDaysIfNeeded(this);
		BufferedWriter writer = null;
		String fileName = StechkarteCsvIO.getBasePath() + "export.csv";
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writeCSV(writer);
			writer.close();
			sendMail(fileName);
		} catch (IOException e) {
			Log.e(Logger.LOG_TAG, "Export days", e);
			Toast.makeText(this, "Cannot open " + fileName, Toast.LENGTH_LONG).show();
		} finally {
			// if (writer != null) {
			// writer.flush();
			// writer.close();
			// }
			finish();
		}
	}

	private String[] header = { "day", "Hours worked", "Overtime of day", "Overtime", "holiday", "holiday left", "IN", "OUT", "IN", "OUT", "IN", "OUT" };

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
		Calendar cal = null;
		while (cursor.moveToNext()) {
			Day day = new Day(cursor);
			if (cal == null) {
				cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, day.getYear());
				cal.set(Calendar.MONTH, day.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, day.getDay());
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				Date date = cal.getTime();
				long dr = DayAccess.dayRefFromDate(date);
				while (dr < day.getDayRef()) {
					writer.write("\"" + formatDate(date) + "\"\n");
					cal.add(Calendar.DAY_OF_MONTH, 1);
					date = cal.getTime();
					dr = DayAccess.dayRefFromDate(date);
				}
			}
			String dayString = formatDateString(day.getDayString());
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
		writer.flush();
	}

	private String formatDateString(String dayString) {
		try {
			Date date = yyyymmddFromat.parse(dayString);
			return formatDate(date);
		} catch (ParseException e) {
			Log.e(Logger.LOG_TAG, "Cannot parse " + dayString, e);
			return dayString;
		}
	}

	private String formatDate(Date date) {
		return mmddyyyFromat.format(date);
	}

	private void sendMail(String filename) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);

		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { Settings.getInstance().getEmailAddress() });
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.emailExportSubject));
		StringBuilder body = new StringBuilder(getString(R.string.mailBody));
		CurInfo curInfo = new CurInfo(this);
		body.append(getString(R.string.TextViewOvertimeShort));
		body.append(": ");
		body.append(curInfo.getOvertimeString());
		body.append("\n");
		body.append(getString(R.string.HintHolidaysLeft));
		body.append(": ");
		body.append(curInfo.getHolydayLeft());
		body.append("\n");
		sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
}
