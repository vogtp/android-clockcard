package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;

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
				startClockCardInstall(BuyFullVersion.this);
				finish();
			}
		});
	}

	public static void startClockCardInstall(Context ctx) {
		try {
			String ccMarket = "market://search?q=pname:ch.almana.android.stechkarteLicense";
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(ccMarket));
			ctx.startActivity(i);
		} catch (Exception e1) {
			Log.e(Logger.LOG_TAG, "Cannot install clock card license", e1);
			Toast.makeText(ctx,
					"Cannot launch market to install clock card license",
					Toast.LENGTH_LONG).show();
		}
	}
}
