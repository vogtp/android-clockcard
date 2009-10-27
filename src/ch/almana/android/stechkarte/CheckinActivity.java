package ch.almana.android.stechkarte;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.view.ListTimeStamps;

public class CheckinActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        
		Button buttonIn = (Button) findViewById(R.id.ButtonIn);
		Button buttonOut = (Button) findViewById(R.id.ButtonOut);
		Button buttonTSList = (Button) findViewById(R.id.ButtonTSList);
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
    }

}