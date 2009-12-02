package ch.almana.android.stechkarte.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.io.TimestampsCsvIO;

public class ExportTimestamps extends Activity {

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
		}
	}
	
	private void writeCSV(BufferedWriter writer) throws IOException {
		DayAccess dayAccess = DayAccess.getInstance(this);
		Cursor cursor = dayAccess.query(null);
		writer.write("day, holiday, IN, OUT, IN, OUT, IN, OUT\n");
		while (cursor.moveToNext()) {
			Day day = new Day(cursor);
			writer.write(day.getDayString());
			writer.write(", ");
			writer.write("" + day.getHolyday());
			Cursor timestamps = day.getTimestamps(this);
			while (timestamps.moveToNext()) {
				writer.write(", ");
				Timestamp ts = new Timestamp(timestamps);
				writer.write(ts.getHMS());
			}
			writer.write("\n");
		}
	}


	private void sendMail(String filename) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);

		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "patrick.vogt@unibas.ch" });
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Stechkarte timestamps");
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

}
