package ch.almana.android.stechkarte.utils;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class GuiUtils {


	public static int setSpinner(Spinner spinner, long dbId) {
		if (spinner == null) {
			return Integer.MIN_VALUE;
		}
		SpinnerAdapter adapter = spinner.getAdapter();
		if (adapter == null) {
			return Integer.MIN_VALUE;
		}
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItemId(i) == dbId) {
				spinner.setSelection(i);
				return i;
			}
		}
		return Integer.MIN_VALUE;
	}

	public static int setSpinner(Spinner spinner, String text) {
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i) == text) {
				spinner.setSelection(i);
				return i;
			}
		}
		return Integer.MIN_VALUE;
	}


	//	public static void setLanguage(Context ctx) {
	//		String lang = Settings.getInstance().getLanguage();
	//		if (!"".equals(lang)) {
	//			GuiUtils.setLanguage(ctx, lang);
	//		}
	//	}

	public static void setLanguage(Context ctx, String lang) {
		Configuration config = new Configuration();
		config.locale = new Locale(lang);
		ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
	}

}
