package ch.almana.android.stechkarte.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.provider.DB.TimeoffTypes;

public class InstallHelper {

	private static final int VERSION = 1;

	public static void initialise(final Context ctx) {
		Settings settings = Settings.getInstance(ctx);
		int defaultProfilesVersion = settings.getDefaultProfilesVersion();
		switch (defaultProfilesVersion) {
		case 0:
			Logger.i("Initalising clockcard settings to level 1");
			addDefaultTimeOffTypes(ctx);
		case 1:
		}
		settings.setDefaultProfilesVersion(VERSION);
	}

	private static void addDefaultTimeOffTypes(Context ctx) {
		ContentResolver resolver = ctx.getContentResolver();

		ContentValues values = new ContentValues();
		values.put(TimeoffTypes.NAME_NAME, ctx.getString(R.string.timeoff_type_name_holidays));
		values.put(TimeoffTypes.NAME_DESCRIPTION, "");
		values.put(TimeoffTypes.NAME_IS_HOLIDAY, 1);
		values.put(TimeoffTypes.NAME_IS_PAID, 1);
		resolver.insert(TimeoffTypes.CONTENT_URI, values);

		values = new ContentValues();
		values.put(TimeoffTypes.NAME_NAME, ctx.getString(R.string.timeoff_type_name_illness));
		values.put(TimeoffTypes.NAME_DESCRIPTION, "");
		values.put(TimeoffTypes.NAME_IS_HOLIDAY, 0);
		values.put(TimeoffTypes.NAME_IS_PAID, 1);
		resolver.insert(TimeoffTypes.CONTENT_URI, values);

		values = new ContentValues();
		values.put(TimeoffTypes.NAME_NAME, ctx.getString(R.string.timeoff_type_name_public_holiday));
		values.put(TimeoffTypes.NAME_DESCRIPTION, "");
		values.put(TimeoffTypes.NAME_IS_HOLIDAY, 0);
		values.put(TimeoffTypes.NAME_IS_PAID, 1);
		resolver.insert(TimeoffTypes.CONTENT_URI, values);

		values = new ContentValues();
		values.put(TimeoffTypes.NAME_NAME, ctx.getString(R.string.timeoff_type_name_unpaid_holidays));
		values.put(TimeoffTypes.NAME_DESCRIPTION, "");
		values.put(TimeoffTypes.NAME_IS_HOLIDAY, 0);
		values.put(TimeoffTypes.NAME_IS_PAID, 0);
		resolver.insert(TimeoffTypes.CONTENT_URI, values);
	}

}
