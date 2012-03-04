package ch.almana.android.stechkarte.utils;

import android.app.Activity;
import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;

public class ProgressWrapperActivity implements IProgressWrapper {

	private Activity progressAct;
	private final CharSequence origTitle;
	private int inc;
	private int maxProgress = -1;
	private int curProgress = -1;

	public ProgressWrapperActivity(Activity act) throws Exception {
		this.progressAct = act;
		this.origTitle = progressAct.getTitle();
	}

	public void setActivity(Activity act) {
		progressAct = act;
		show();
		setMax(maxProgress);
		setProgress(curProgress);
	}

	@Override
	public void dismiss() {
		if (progressAct == null) {
			return;
		}
		try {
			progressAct.setTitle(origTitle);
			progressAct.setProgressBarVisibility(false);
		} catch (Throwable e) {
			Log.w(Logger.TAG, "Cannot dismiss progress", e);
		}
	}

	@Override
	public void incrementEvery(int i) {

	}

	@Override
	public void setMax(int i) {
		maxProgress = i;
		if (i > 0) {
			this.inc = 10000 / i;
		} else {
			this.inc = 1;
		}
	}

	@Override
	public void setProgress(int i) {
		if (progressAct == null || i < 0) {
			return;
		}
		curProgress = i;
		try {
			progressAct.setProgress(i * inc);
		} catch (Throwable e) {
			Log.w(Logger.TAG, "Cannot set progress to " + i, e);
		}
	}

	@Override
	public void setTitle(String title) {
		if (progressAct == null) {
			return;
		}
		try {
			progressAct.setTitle(title);
		} catch (Throwable e) {
			Log.w(Logger.TAG, "Cannot set progress title to " + title, e);
		}
	}

	@Override
	public void show() {
		if (progressAct == null) {
			return;
		}
		try {
			progressAct.setProgressBarVisibility(true);
		} catch (Throwable e) {
			Log.w(Logger.TAG, "Cannot show progress", e);
		}
	}

}
