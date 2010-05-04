package ch.almana.android.stechkarte.service;

import ch.almana.android.stechkarte.log.Logger;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LocationCheckAction extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	   Log.i(Logger.LOG_TAG, "Start "+this.getClass().getSimpleName());
	}

}
