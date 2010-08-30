package ch.almana.android.stechkarte.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressWrapperDialog implements IProgressWrapper {

	private ProgressDialog progressDialog;

	// private int incrementEvery;

	public ProgressWrapperDialog(Context ctx) {
		progressDialog = new ProgressDialog(ctx);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progressDialog.setMax(100);
		// progressDialog.setProgress(0);
		// progressDialog.setMessage("Starting");
	}

	@Override
	public void dismiss() {
		progressDialog.dismiss();
	}

	@Override
	public void incrementEvery(int i) {
		// this.incrementEvery = i;
	}

	@Override
	public void setMax(int max) {
		progressDialog.setMax(max);
	}

	@Override
	public void setProgress(int i) {
		progressDialog.setProgress(i); // incrementEvery);
	}

	@Override
	public void setTitle(String title) {
		progressDialog.setTitle(title);
	}

	@Override
	public void show() {
		progressDialog.show();
	}

}
