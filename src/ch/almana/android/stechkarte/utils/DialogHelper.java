package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.content.Intent;
import ch.almana.android.stechkarte.view.activity.BuyFullVersion;

public class DialogHelper {

	public static void showFreeVersionDialog(Context ctx) {
		Intent i = new Intent(ctx, BuyFullVersion.class);
		ctx.startActivity(i);
	}

}
