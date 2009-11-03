package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.DB;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Formater;
import ch.almana.android.stechkarte.model.DB.Days;

public class ListDays extends ListActivity {

	private static final int MENU_ITEM_REBUILD = Menu.FIRST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

		// ProgressDialog dia = new ProgressDialog(this);
		// dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dia.setTitle("Loading days");
		// dia.show();

		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Days.CONTENT_URI);
		}

		rebuildDays();

		Cursor cursor = DayAccess.getInstance(this).query(null);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.daylist_item, cursor, new String[] {
				DB.Days.COL_NAME_DAYREF, DB.Days.COL_NAME_HOURS_WORKED, DB.Days.COL_NAME_OVERTIME,
				DB.Days.COL_NAME_HOURS_TARGET, DB.Days.COL_NAME_HOLIDAY, DB.Days.COL_NAME_HOLIDAY_LEFT },
 new int[] {
				R.id.TextViewDayRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget,
				R.id.TextViewHoliday, R.id.TextViewHolidaysLeft });
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Days.COL_INDEX_DAYREF) {
					Day d = new Day(cursor);
					int color = Color.GREEN;
					if (d.isError()) {
						color = Color.RED;
					}
					TextView errorView = (TextView) view.findViewById(R.id.TextViewDayRef);
					errorView.setTextColor(color);
					// since we do not set the dayref no: return true;
				} else if (columnIndex == DB.Days.COL_INDEX_OVERTIME) {
					Day d = new Day(cursor);
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(Formater.formatHourMinFromHours(d
							.getOvertime()));
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = d.getHoursWorked() - d.getHoursTarget();
					tv.setText(Formater.formatHourMinFromHours(overtime));
					return true;
				}
				return false;
			}
		});
		
		setListAdapter(adapter);
		// dia.dismiss();
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
			rebuildDays();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void rebuildDays() {
		DayAccess.getInstance(getApplicationContext()).recalculateDayFromTimestamp(null);
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
			// Intent i = new Intent(this, ListTimeStamps.class);
			// long dayRef =
			// TimestampAccess.getInstance(this).getTimestampById(id).getDayRef();
			// i.putExtra(ListTimeStamps.FILTER_DAYREF, dayRef);
			//			
			// startActivity(i);
			
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
		}
	}
}
