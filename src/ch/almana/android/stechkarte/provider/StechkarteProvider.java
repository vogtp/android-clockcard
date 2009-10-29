package ch.almana.android.stechkarte.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.DB.Days;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class StechkarteProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.almana.android.stechkarte";

	private static final String LOG_TAG = "StechkarteProvider";

	private static final int TIMESTAMP = 1;
	private static final int DAY = 2;

	private static final UriMatcher sUriMatcher;

	private TimestampAccess timestampAccess;
	private DayAccess dayAccess;

	private TimestampAccess getTimastampAccess() {
		if (timestampAccess == null) {
			timestampAccess = TimestampAccess.getInstance(getContext());
		}
		return timestampAccess;
	}

	private DayAccess getDayAccess() {
		if (dayAccess == null) {
			dayAccess = DayAccess.getInstance(getContext());
		}
		return dayAccess;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return getTimastampAccess().delete(uri, selection, selectionArgs);

		case DAY:
			return getDayAccess().delete(uri, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}


	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return getTimastampAccess().getType(uri);

		case DAY:
			return getDayAccess().getType(uri);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return getTimastampAccess().insert(uri, initialValues);

		case DAY:
			return getDayAccess().insert(uri, initialValues);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return getTimastampAccess()
					.query(uri, projection, selection, selectionArgs, sortOrder);
		case DAY:
			return getDayAccess().query(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return getTimastampAccess().update(uri, values, selection, selectionArgs);

		case DAY:
			return getDayAccess().update(uri, values, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME, TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME + "/#", TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Days.CONTENT_ITEM_NAME, DAY);
		sUriMatcher.addURI(AUTHORITY, Days.CONTENT_ITEM_NAME + "/#", DAY);

	}

}
