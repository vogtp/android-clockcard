package ch.almana.android.stechkarte.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import ch.almana.android.stechkarte.provider.StechkarteProvider;

public interface DB {

	public static final String DATABASE_NAME = "stechkarte";

	public static final String COL_NAME_ID = "_id";
	public static final int COL_INDEX_ID = 0;

	class OpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 2;

		private static final String CREATE_TIMESTAMPS_TABLE = "create table if not exists " + DB.Timestamps.TABLE_NAME
				+ " ("
				+ DB.COL_NAME_ID + " integer primary key, " + Timestamps.COL_NAME_TIMESTAMP_TYPE + " int,"
				+ Timestamps.COL_NAME_TIMESTAMP + " long, " + Timestamps.COL_NAME_DAYREF + " long);";

		private static final String CREATE_DAYS_TABLE = "create table if not exists " + Days.TABLE_NAME + " ("
				+ DB.COL_NAME_ID
				+ " integer primary key, " + Days.COL_NAME_DAYREF + " long," + Days.COL_NAME_HOURS_WORKED + " real,"
				+ Days.COL_NAME_HOURS_TARGET + " real," + Days.COL_NAME_HOLIDAY + " real," + Days.COL_NAME_HOLIDAY_LEFT
				+ " real," + Days.COL_NAME_OVERTIME + " real," + Days.COL_NAME_OVERTIME_COMPENSATION + " real);";

		private static final String LOG_TAG = "OpenHelper";


		private Context context;

		public OpenHelper(Context context) {
			super(context, DB.DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TIMESTAMPS_TABLE);
			db.execSQL(CREATE_DAYS_TABLE);
			Log.i(LOG_TAG, "Created table " + DB.Timestamps.TABLE_NAME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			switch (oldVersion) {
				case 1:
					Log.w(LOG_TAG, "Upgrading to DB Version 2...");
					db.execSQL(CREATE_DAYS_TABLE);
					db.execSQL("alter table " + Timestamps.TABLE_NAME +
							" add column " + Timestamps.COL_NAME_DAYREF + " long;");
					// nobreak

				default:
					Log.w(LOG_TAG, "Finished DB upgrading!");
					break;
				}
		}

	}

	public interface Timestamps {

		static final String TABLE_NAME = "timestamps";

		public static final String CONTENT_ITEM_NAME = "timestamp";
		public static String CONTENT_URI_STRING = "content://" + StechkarteProvider.AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + StechkarteProvider.AUTHORITY + "."
				+ CONTENT_ITEM_NAME;

		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + StechkarteProvider.AUTHORITY + "."
				+ CONTENT_ITEM_NAME;

		public static final String COL_NAME_TIMESTAMP_TYPE = "type";
		public static final String COL_NAME_TIMESTAMP = "timestamp";
		public static final String COL_NAME_DAYREF = "dayRef";

		public static final int COL_INDEX_TIMESTAMP = 1;
		public static final int COL_INDEX_TIMESTAMP_TYPE = 2;
		public static final int COL_INDEX_DAYREF = 3;

		public static final String[] colNames = new String[] { COL_NAME_ID, COL_NAME_TIMESTAMP, COL_NAME_TIMESTAMP_TYPE };
		public static final String[] DEFAULT_PROJECTION = colNames;

		public static final String DEFAUL_SORTORDER = COL_NAME_TIMESTAMP + " DESC";

	}

	public interface Days {
		static final String TABLE_NAME = "days";

		public static final String CONTENT_ITEM_NAME = "day";
		public static String CONTENT_URI_STRING = "content://" + StechkarteProvider.AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + StechkarteProvider.AUTHORITY + "."
				+ CONTENT_ITEM_NAME;

		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + StechkarteProvider.AUTHORITY + "."
				+ CONTENT_ITEM_NAME;

		public static final String COL_NAME_DAYREF = "dayRef";
		public static final String COL_NAME_HOURS_WORKED = "hoursWorked";
		public static final String COL_NAME_HOURS_TARGET = "hoursTarget";
		public static final String COL_NAME_HOLIDAY = "holiday";
		public static final String COL_NAME_HOLIDAY_LEFT = "holidayLeft";
		public static final String COL_NAME_OVERTIME = "overtime";
		public static final String COL_NAME_OVERTIME_COMPENSATION = "overtimeCompensation";

		public static final int COL_INDEX_DAYREF = 1;
		public static final int COL_INDEX_HOURS_WORKED = 2;
		public static final int COL_INDEX_HOURS_TARGET = 3;
		public static final int COL_INDEX_HOLIDAY = 4;
		public static final int COL_INDEX_HOLIDAY_LEFT = 5;
		public static final int COL_INDEX_OVERTIME = 6;
		public static final int COL_INDEX_OVERTIME_COMPENSATION = 6;

		public static final String[] colNames = new String[] { COL_NAME_ID, COL_NAME_DAYREF, COL_NAME_HOURS_WORKED,
				COL_NAME_HOURS_TARGET, COL_NAME_HOLIDAY_LEFT, COL_NAME_OVERTIME, COL_NAME_OVERTIME_COMPENSATION };
		public static final String[] DEFAULT_PROJECTION = colNames;

		public static final String DEFAULT_SORTORDER = COL_NAME_DAYREF + " DESC";
		public static final String REVERSE_SORTORDER = COL_NAME_DAYREF + " ASC";

		static final String[] PROJECTTION_DAYREF = new String[] { COL_NAME_DAYREF };

	}

}