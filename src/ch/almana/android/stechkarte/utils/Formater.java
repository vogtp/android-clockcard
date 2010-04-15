package ch.almana.android.stechkarte.utils;

import android.util.Log;
import ch.almana.android.stechkarte.log.Logger;


public class Formater {

	public static CharSequence formatHourMinFromHours(float hours) {
		int h = (int) Math.floor(hours);
		int m = Math.round((hours - h) * 60); // rather: /100 * 60 * 100
		return h + ":" + String.format("%02d", m);
	}

	public static float getHoursFromHoursMin(String hourMin) {
		float hours = 0f;
		int sepPos = hourMin.indexOf(':');
		if (sepPos < 0) {
			sepPos = hourMin.indexOf('.');
		}
		if (sepPos > -1) {
			String h = hourMin.substring(0, sepPos);
			String m = hourMin.substring(sepPos + 1);
			try {
				hours = Float.parseFloat(h);
			} catch (Exception e) {
				Log.e(Logger.LOG_TAG, "Cannot parse " + h + " als float", e);
			}
			try {
				float min = Float.parseFloat(m);
				hours += min / 60f;
			} catch (Exception e) {
				Log.e(Logger.LOG_TAG, "Cannot parse " + m + " als float", e);
			}
		}
		return hours;
	}

}
