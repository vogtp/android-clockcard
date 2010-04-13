package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ch.almana.android.stechkarte.R;

public class BuyFullVersion extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_full_version);
		Button buttonOK = (Button)findViewById(R.id.ButtonOK);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
