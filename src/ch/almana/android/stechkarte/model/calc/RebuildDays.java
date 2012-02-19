package ch.almana.android.stechkarte.model.calc;

import android.content.Context;

public class RebuildDays {

	public static IRebuildDays create(Context context) {
		return new RebuildDaysOld(context);
	}

}
