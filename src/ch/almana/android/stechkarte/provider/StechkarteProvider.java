package ch.almana.android.stechkarte.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.DB.Timestamps;

public class StechkarteProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.almana.android.stechkarte";

	private static final String LOG_TAG = "StechkarteProvider";

	private static final int TIMESTAMP = 1;

	private static final UriMatcher sUriMatcher;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return TimestampAccess.getInstance(getContext()).delete(uri, selection, selectionArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return TimestampAccess.getInstance(getContext()).getType(uri);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return TimestampAccess.getInstance(getContext()).insert(uri, initialValues);

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
			return TimestampAccess.getInstance(getContext())
					.query(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		switch (sUriMatcher.match(uri)) {
		case TIMESTAMP:
			return TimestampAccess.getInstance(getContext()).update(uri, values, selection, selectionArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME, TIMESTAMP);
		sUriMatcher.addURI(AUTHORITY, Timestamps.CONTENT_ITEM_NAME + "/#", TIMESTAMP);

	}

}
