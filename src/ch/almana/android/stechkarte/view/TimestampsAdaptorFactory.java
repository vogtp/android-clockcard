package ch.almana.android.stechkarte.view;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DB;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class TimestampsAdaptorFactory implements OnCreateContextMenuListener, OnItemClickListener {

	
	private SimpleCursorAdapter adapter;

	public static final int MENU_ITEM_DELETE = 100;
	public static final int MENU_ITEM_EDIT = MENU_ITEM_DELETE + 1;

	private Context context;

	private ListView listview;

	public TimestampsAdaptorFactory(ListView timestamps, long dayRef) {
		super();
		this.listview = timestamps;
		String selection = null;
		if (dayRef > 0) {
			selection = DB.Days.COL_NAME_DAYREF + "=" + dayRef;
		}

		context = timestamps.getContext();
		Cursor cursor = TimestampAccess.getInstance(context).query(selection);
		// Used to map notes entries from the database to views
		adapter = new SimpleCursorAdapter(context, R.layout.timestamplist_item, cursor, new String[] {
				Timestamps.COL_NAME_TIMESTAMP, Timestamps.COL_NAME_TIMESTAMP_TYPE }, new int[] {
				R.id.TextViewTimestamp, R.id.TextViewTimestampType });
		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == Timestamps.COL_INDEX_TIMESTAMP) {
					TextView ts = (TextView) view.findViewById(R.id.TextViewTimestamp);
					long time = cursor.getLong(Timestamps.COL_INDEX_TIMESTAMP);
					ts.setText(Timestamp.formatTime(time));
				} else if (columnIndex == Timestamps.COL_INDEX_TIMESTAMP_TYPE) {
					String txt = "unknown";
					int type = cursor.getInt(Timestamps.COL_INDEX_TIMESTAMP_TYPE);
					if (type == Timestamp.TYPE_IN) {
						txt = " IN";
					} else if (type == Timestamp.TYPE_OUT) {
						txt = " OUT";
					}
					((TextView) view.findViewById(R.id.TextViewTimestampType)).setText(txt);
				}
				return true;
			}
		});

		timestamps.setAdapter(getAdapter());
		timestamps.setOnCreateContextMenuListener(this);
		timestamps.setOnItemClickListener(this);
	}

	public SimpleCursorAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(Logger.LOG_TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}
		cursor.close();

		// Setup the menu header
		// menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

		menu.add(0, MENU_ITEM_EDIT, 0, R.string.menu_edit);

		// Add a menu item to delete the note
		menu.add(2, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(Logger.LOG_TAG, "bad menuInfo", e);
			return false;
		}

		Uri tsUri = ContentUris.withAppendedId(Timestamps.CONTENT_URI, info.id);
		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {
			context.getContentResolver().delete(tsUri, null, null);
			return true;
		}
		case MENU_ITEM_EDIT: {
			context.startActivity(new Intent(Intent.ACTION_EDIT, tsUri));
			return true;
		}
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int arg2, long id) {
		// TODO Auto-generated method stub
		Uri uri = ContentUris.withAppendedId(DB.Timestamps.CONTENT_URI, id);
		context.startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}

	public void onResume() {
		listview.setAdapter(getAdapter());
	}

}
