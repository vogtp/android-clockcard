package ch.almana.android.stechkarte.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface IAccess {

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	public abstract int delete(Uri uri, String selection, String[] selectionArgs);

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#getType(android.net.Uri)
	 */
	public abstract String getType(Uri uri);

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#insert(android.net.Uri, android.content.ContentValues)
	 */
	public abstract Uri insert(Uri uri, ContentValues initialValues);

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#onCreate()
	 */
	public abstract boolean onCreate();

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	public abstract Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder);

	/* (non-Javadoc)
	 * @see ch.almana.android.stechkarte.provider.IDataAccessObject#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	public abstract int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);

}