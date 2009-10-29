package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class ListTimeStamps extends ListActivity {

	private static final String LOG_TAG = ListTimeStamps.class.toString();

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
	public static final int MENU_ITEM_EDIT = Menu.FIRST + 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Timestamps.CONTENT_URI);
		}

		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		Cursor cursor = TimestampAccess.getInstance(getApplicationContext()).query(null, null);
		// Used to map notes entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.timestamplist_item, cursor, new String[] {
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
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "bad menuInfo", e);
			return;
		}

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		// Setup the menu header
		// menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

		menu.add(0, MENU_ITEM_EDIT, 0, R.string.menu_edit);

		// Add a menu item to delete the note
		menu.add(2, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert).setShortcut('3', 'a').setIcon(
				android.R.drawable.ic_menu_add);

		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, ListTimeStamps.class), null,
				intent,
				0, null);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_INSERT:
			// Launch activity to insert a new item
			startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
			return true;
		case MENU_ITEM_EDIT:
			// Launch activity to insert a new item
			startActivity(new Intent(Intent.ACTION_EDIT, getIntent().getData()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "bad menuInfo", e);
			return false;
		}

		Uri tsUri = ContentUris.withAppendedId(Timestamps.CONTENT_URI, info.id);
		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {
			getContentResolver().delete(tsUri, null, null);
			return true;
		}
		case MENU_ITEM_EDIT: {
			startActivity(new Intent(Intent.ACTION_EDIT, tsUri));
			return true;
		}
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a note selected by
			// the user. The have clicked on one, so return it now.
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			// Launch activity to view/edit the currently selected item
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
		}
	}

}
