package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.DB;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.DB.Days;

public class ListDays extends ListActivity {

	private static final int MENU_ITEM_REBUILD = Menu.FIRST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Days.CONTENT_URI);
		}

		Cursor cursor = DayAccess.getInstance(getApplicationContext()).query(null);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.daylist_item, cursor, new String[] {
				DB.Days.COL_NAME_DAYREF, DB.Days.COL_NAME_HOURS_WORKED, DB.Days.COL_NAME_OVERTIME,
				DB.Days.COL_NAME_HOURS_TARGET, DB.Days.COL_NAME_HOLIDAY, DB.Days.COL_NAME_OVERTIME_COMPENSATION },
				new int[] { R.id.TextViewDayRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime,
						R.id.TextViewHoursTarget, R.id.TextViewHoliday, R.id.TextViewCompensation });
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM_REBUILD, 0, R.string.menu_rebuild).setShortcut('3', 'a').setIcon(
				android.R.drawable.ic_menu_revert);

		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, ListTimeStamps.class), null,
				intent, 0, null);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_REBUILD:
			// startActivity(new Intent(Intent.ACTION_INSERT,
			// getIntent().getData()));
			DayAccess.getInstance(getApplicationContext()).recalculateDayFromTimestamp(null);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}
}
