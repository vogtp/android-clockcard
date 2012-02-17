package ch.almana.android.stechkarte.application;

import android.app.Application;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.MonthAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.WeekAccess;
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
		MonthAccess.initInstance(this);
		WeekAccess.initInstance(this);
		try {
			DB.OpenHelper oh = new OpenHelper(this);
			oh.getWritableDatabase().getVersion();
			oh.close();
		} catch (Throwable e) {
			Log.e(Logger.TAG, "Unable to open DB for initialisation", e);
		}
	}

}
