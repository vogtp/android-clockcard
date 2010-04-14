package ch.almana.android.stechkarte.utils;

public class Formater {

	public static CharSequence formatHourMinFromHours(float hours) {
		int h = (int) Math.floor(hours);
		int m = Math.round((hours - h) * 60); // rather: /100 * 60 * 100
		return h + ":" + String.format("%02d", m);
	}

}
