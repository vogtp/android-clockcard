package ch.almana.android.stechkarte.application;

import android.app.Application;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.OpenHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class StechkarteApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Settings.initInstance(this);
		TimestampAccess.initInstance(this);
		DayAccess.initInstance(this);
		DB.OpenHelper oh = new OpenHelper(this);
		oh.getWritableDatabase();
		oh.close();
	}
	
	
}
