package ch.almana.android.stechkarte.provider;

import android.net.Uri;

public interface DB {

	public static final String DATABASE_NAME = "stechkarte";

	public interface Timestamps {

		static final String TABLE_NAME = "timestamps";

		public static final String CONTENT_ITEM_NAME = "timestamps";
		public static String CONTENT_URI_STRING = "content://" + StechkarteProvider.AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + StechkarteProvider.AUTHORITY + "."
				+ CONTENT_ITEM_NAME;

		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + StechkarteProvider.AUTHORITY
				+ "." + CONTENT_ITEM_NAME;

		public static final String COL_NAME_TIMESTAMP_TYPE = "type";
		public static final String COL_NAME_TIMESTAMP = "timestamp";
		public static final String COL_NAME_ID = "_id";
		public static final int COL_INDEX_ID = 0;
		public static final int COL_INDEX_TIMESTAMP = 1;
		public static final int COL_INDEX_TIMESTAMP_TYPE = 2;
		public static final String[] colNames = new String[] { COL_NAME_ID, COL_NAME_TIMESTAMP, COL_NAME_TIMESTAMP_TYPE };
		public static final String[] DEFAULT_PROJECTION = colNames;
		public static final String DEFAUL_SORTORDER = Timestamps.COL_NAME_TIMESTAMP + " DESC";

	}

}