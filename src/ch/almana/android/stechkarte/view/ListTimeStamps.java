package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.DB.Timestamps;

public class ListTimeStamps extends ListActivity {

	public static final String FILTER_DAYREF = "filterDayref";

	private static final String LOG_TAG = Logger.LOG_TAG;

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
	public static final int MENU_ITEM_EDIT = Menu.FIRST + 2;

	private TimestampsAdaptorFactory timestampsAdaptorFactory;


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

		long dayRef = -1;
		if (intent.hasExtra(FILTER_DAYREF)) {
			dayRef = intent.getExtras().getLong(FILTER_DAYREF);
		}

		timestampsAdaptorFactory = new TimestampsAdaptorFactory(getListView(), dayRef);
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
		return timestampsAdaptorFactory.onContextItemSelected(item);
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
