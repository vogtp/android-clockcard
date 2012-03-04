package ch.almana.android.stechkarte.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.MonthAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.WeekAccess;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.OpenHelper;
import ch.almana.android.stechkarte.utils.InstallHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class StechkarteApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Context ctx = getApplicationContext();
		Settings.getInstance(ctx);
		TimestampAccess.initInstance(ctx);
		DayAccess.initInstance(ctx);
		MonthAccess.initInstance(ctx);
		WeekAccess.initInstance(ctx);
		try {
			DB.OpenHelper oh = new OpenHelper(ctx);
			oh.getWritableDatabase().getVersion();
			oh.close();
		} catch (Throwable e) {
			Log.e(Logger.TAG, "Unable to open DB for initialisation", e);
		}
		InstallHelper.initialise(ctx);
	}

}
