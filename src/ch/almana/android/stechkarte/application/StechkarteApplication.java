package ch.almana.android.stechkarte.application;

import android.app.Application;
import ch.almana.android.stechkarte.utils.Settings;

public class StechkarteApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Settings.initInstance(this);
	}
}
