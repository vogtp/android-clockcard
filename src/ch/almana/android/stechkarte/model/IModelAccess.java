package ch.almana.android.stechkarte.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


public interface IModelAccess {

	public abstract Uri insert(Uri uri, ContentValues initialValues);

	public abstract Cursor query(String string);

}