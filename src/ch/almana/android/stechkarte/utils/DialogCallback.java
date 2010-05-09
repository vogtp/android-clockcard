package ch.almana.android.stechkarte.utils;

import android.content.Context;

public interface DialogCallback {
	
	Context getContext();
	
	void finished(boolean success);
	
}
