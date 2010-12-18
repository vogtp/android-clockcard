package ch.almana.android.stechkarte.utils;

import android.app.Activity;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;

public class ProgressWrapperActivity implements IProgressWrapper {

	private final Activity progressAct;
	// private int max;
	private final CharSequence origTitle;
	private int inc;

	public ProgressWrapperActivity(Activity act) throws Exception {
		this.progressAct = act;
		// progressAct.getWindow().re
		this.origTitle = progressAct.getTitle();
		//
		// progressAct.setProgressBarVisibility(true);
		// progressAct.setProgress(500);
	}

	@Override
	public void dismiss() {
		try {
			progressAct.setTitle(origTitle);
			progressAct.setProgressBarVisibility(false);
		} catch (Throwable e) {
			Log.w(Logger.LOG_TAG, "Cannot dismiss progress", e);
		}
	}

	@Override
	public void incrementEvery(int i) {

	}

	@Override
	public void setMax(int i) {
		if (i > 0) {
			this.inc = 10000 / i;
		} else {
			this.inc = 1;
		}
	}

	@Override
	public void setProgress(int i) {
		try {
			progressAct.setProgress(i * inc);
		} catch (Throwable e) {
			Log.w(Logger.LOG_TAG, "Cannot set progress to " + i, e);
		}
	}

	@Override
	public void setTitle(String title) {
		try {
			progressAct.setTitle(title);
		} catch (Throwable e) {
			Log.w(Logger.LOG_TAG, "Cannot set progress title to " + title, e);
		}
	}

	@Override
	public void show() {
		try {
			progressAct.setProgressBarVisibility(true);
		} catch (Throwable e) {
			Log.w(Logger.LOG_TAG, "Cannot show progress", e);
		}
	}

}
