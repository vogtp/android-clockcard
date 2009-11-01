package ch.almana.android.stechkarte;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.io.TimestampsCsvIO;
import ch.almana.android.stechkarte.view.ListDays;
import ch.almana.android.stechkarte.view.ListTimeStamps;

public class CheckinActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        
		writeTimestampsToCsv();

		Button buttonIn = (Button) findViewById(R.id.ButtonIn);
		Button buttonOut = (Button) findViewById(R.id.ButtonOut);
		Button buttonTSList = (Button) findViewById(R.id.ButtonTSList);
		Button buttonDayList = (Button) findViewById(R.id.ButtonDayList);
		Button buttonReadTimestamps = (Button) findViewById(R.id.ButtonReadTimestamps);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		width = Math.round(width / 2);
		int size = Math.round(width / 5);
		buttonIn.setWidth(width);
		buttonIn.setHeight(width);
		buttonIn.setTextSize(size);
		buttonOut.setWidth(width);
		buttonOut.setHeight(width);
		buttonOut.setTextSize(size);

		buttonIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance(v.getContext()).addInNow();
			}
		});

		buttonOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance(v.getContext()).addOutNow();
			}
		});

		buttonTSList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ListTimeStamps.class);

				startActivity(i);
			}
		});
		buttonDayList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ListDays.class);

				startActivity(i);
			}
		});

		buttonReadTimestamps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampsCsvIO timestampsCsvIO = new TimestampsCsvIO();
				timestampsCsvIO.readTimestamps(TimestampsCsvIO.PATH + "timestamps.csv", TimestampAccess
						.getInstance(getApplicationContext()));
			}
		});
    }

	private void writeTimestampsToCsv() {
		TimestampsCsvIO csv = new TimestampsCsvIO();
		Cursor c = TimestampAccess.getInstance(getApplicationContext()).query(null, null);
		csv.writeTimestamps(c);
		c.close();
	}

}