package ch.almana.android.stechkarte.view;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.utils.Formater;

public class ListDays extends ListActivity {

	private static final int MENU_ITEM_REBUILD = Menu.FIRST;
	private ProgressDialog progressDialog = null;

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

		// rebuildDays();

		Cursor cursor = managedQuery(DB.Days.CONTENT_URI, DB.Days.DEFAULT_PROJECTION, null, null,
				Days.DEFAULT_SORTORDER);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.daylist_item, cursor, new String[] {
				DB.Days.NAME_DAYREF, DB.Days.NAME_HOURS_WORKED, DB.Days.NAME_OVERTIME, DB.Days.NAME_HOURS_TARGET,
				DB.Days.NAME_HOLIDAY, DB.Days.NAME_HOLIDAY_LEFT, DB.Days.NAME_FIXED }, new int[] { R.id.TextViewDayRef,
				R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget, R.id.TextViewHoliday,
				R.id.TextViewHolidaysLeft, R.id.ImageViewLock });

		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Days.INDEX_DAYREF) {
					Day d = new Day(cursor);
					int color = Color.GREEN;
					if (d.isError()) {
						color = Color.RED;
					}
					TextView errorView = (TextView) view.findViewById(R.id.TextViewDayRef);
					errorView.setTextColor(color);
					// since we do not set the dayref no: return true;
				} else if (columnIndex == DB.Days.INDEX_OVERTIME) {
					Day d = new Day(cursor);
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(Formater.formatHourMinFromHours(d
							.getOvertime()));
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = d.getHoursWorked() - d.getHoursTarget();
					tv.setText(Formater.formatHourMinFromHours(overtime));
					if (overtime > 5) {
						tv.setTextColor(Color.RED);
					} else if (overtime > 3) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Days.INDEX_HOURS_WORKED) {
					float hoursWorked = cursor.getFloat(Days.INDEX_HOURS_WORKED);
					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatHourMinFromHours(hoursWorked));
					if (hoursWorked > 15) {
						tv.setTextColor(Color.RED);
					} else if (hoursWorked > 12) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Days.INDEX_HOURS_TARGET) {
					Day d = new Day(cursor);
					((TextView) view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatHourMinFromHours(d
							.getHoursTarget()));
					return true;
				} else if (columnIndex == DB.Days.INDEX_FIXED) {
					ImageView iv = (ImageView) view.findViewById(R.id.ImageViewLock);
					if (cursor.getInt(Days.INDEX_FIXED) > 0) {
						iv.setImageDrawable(getResources()
								.getDrawable(android.R.drawable.ic_lock_lock));
					} else {
						iv.setImageBitmap(null);
					}
					return true;
				}
				return false;
			}
		});

		setListAdapter(adapter);

		getListView().setOnCreateContextMenuListener(this);
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
		Handler syncHandler = new Handler();

		final Context context = this;
		syncHandler.post(new Runnable() {

			@Override
			public void run() {
				// progressDialog = new ProgressDialog(context);
				// progressDialog.setIcon(android.R.drawable.ic_dialog_alert);
				// progressDialog.setTitle("Rebuilding days");
				// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// // progressDialog.setMax(100);
				// // progressDialog.setMessage("Checking Authorization...");
				// // progressDialog.setProgress(0);
				// progressDialog.setCancelable(false);
				// progressDialog.show();
				//
				// progressDialog.setMessage("Starting...");

				progressDialog = ProgressDialog.show(context, "Rebuilding days", "Starting up...", true, false);

				DayAccess.getInstance().recalculateDayFromTimestamp(null);
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.daylist_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);

		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(Logger.LOG_TAG, "bad menuInfo", e);
			return false;
		}

		Uri uri = ContentUris.withAppendedId(Days.CONTENT_URI, info.id);
		switch (item.getItemId()) {
		case R.id.itemDeleteDay: {
			// Cursor c = DayAccess.getInstance(this).query(uri,
			// DB.Days.DEFAULT_PROJECTION, null, null,
			// DB.Days.DEFAULT_SORTORDER);
			// Day d = new Day(c);
			// Cursor ct = d.getTimestamps(this);
			// if (ct.getCount() > 0) {
			// // delete timestamps?
			//
			// }
			int delRows = DayAccess.getInstance().delete(uri, null, null);
			return delRows > 0;
		}
		}
		return false;

	}

}
