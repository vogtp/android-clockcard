package ch.almana.android.stechkarte.view.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Month;
import ch.almana.android.stechkarte.model.MonthAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Months;
import ch.almana.android.stechkarte.utils.DialogCallback;
import ch.almana.android.stechkarte.utils.Formater;
import ch.almana.android.stechkarte.utils.RebuildDaysTask;

public class ListMonths extends ListActivity implements DialogCallback {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.month_listview);
		setTitle(R.string.monthListTitle);

		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Months.CONTENT_URI);
		}

		Cursor cursor = managedQuery(DB.Months.CONTENT_URI, DB.Months.DEFAULT_PROJECTION, null, null, Months.DEFAULT_SORTORDER);

		if (cursor.getCount() < 1) {
			MonthAccess.getInstance().rebuildFromDayRef(0);
			cursor = managedQuery(DB.Months.CONTENT_URI, DB.Months.DEFAULT_PROJECTION, null, null, Months.DEFAULT_SORTORDER);
		}

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.monthlist_item, cursor, new String[] { DB.NAME_ID, DB.Months.NAME_MONTHREF,
				DB.Months.NAME_HOURS_WORKED, DB.Months.NAME_OVERTIME, DB.Months.NAME_HOURS_TARGET, DB.Months.NAME_HOLIDAY, DB.Months.NAME_HOLIDAY_LEFT }, new int[] {
				R.id.TextViewMonthRef, R.id.TextViewMonthRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget, R.id.TextViewHoliday,
				R.id.TextViewHolidaysLeft });

		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Months.INDEX_MONTHREF) {
					Month m = new Month(cursor);
					int color = Color.GREEN;
					if (m.isError()) {
						color = Color.RED;
					}
					TextView errorView = (TextView) view.findViewById(R.id.TextViewMonthRef);
					errorView.setTextColor(color);
					// since we do not set the dayref no: return true;
				} else if (columnIndex == DB.Months.INDEX_OVERTIME) {
					Month m = new Month(cursor);
					CharSequence formatHourMinFromHours = Formater.formatHourMinFromHours(m.getOvertime());
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(formatHourMinFromHours);
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = m.getHoursWorked() - m.getHoursTarget();
					tv.setText(Formater.formatHourMinFromHours(overtime));
					tv.setTextColor(Color.LTGRAY);
					if (overtime > 150) {
						tv.setTextColor(Color.RED);
					} else if (overtime > 90) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Months.INDEX_HOURS_WORKED) {
					float hoursWorked = cursor.getFloat(Months.INDEX_HOURS_WORKED);
					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatHourMinFromHours(hoursWorked));
					tv.setTextColor(Color.LTGRAY);
					if (hoursWorked > 360) {
						tv.setTextColor(Color.RED);
					} else if (hoursWorked > 300) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Months.INDEX_HOURS_TARGET) {
					Month m = new Month(cursor);
					((TextView) view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatHourMinFromHours(m.getHoursTarget()));
					return true;
				}
				return false;
			}
		});

		getListView().setAdapter(adapter);
		getListView().setOnCreateContextMenuListener(this);
		// dia.dismiss();
	}

	@Override
	protected void onResume() {
		Context ctx = this;
		if (TabbedMainActivity.instance != null) {
			ctx = TabbedMainActivity.instance;
		}
		RebuildDaysTask.rebuildDaysIfNeeded(ctx);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.monthlist_option, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemDaylistRebuild:
			rebuildDays();
			break;

		default:
			return super.onOptionsItemSelected(item);

		}
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
		}
	}

	private void rebuildDays() {
		Context ctx = this;
		if (TabbedMainActivity.instance != null) {
			ctx = TabbedMainActivity.instance;
		}
		RebuildDaysTask.rebuildDays(ctx, null);
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo
	// menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// getMenuInflater().inflate(R.menu.daylist_context, menu);
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// super.onContextItemSelected(item);
	//
	// AdapterView.AdapterContextMenuInfo info;
	// try {
	// info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	// } catch (ClassCastException e) {
	// Log.e(Logger.LOG_TAG, "bad menuInfo", e);
	// return false;
	// }
	//
	// // Uri uri = ContentUris.withAppendedId(Days.CONTENT_URI, info.id);
	// switch (item.getItemId()) {
	// case R.id.itemDeleteDay: {
	//
	// DeleteDayDialog alert = new DeleteDayDialog(this, info.id);
	// alert.setTitle("Delete Day...");
	// alert.show();
	// return true;
	// }
	// }
	// return false;
	//
	// }

	@Override
	public void finished(boolean success) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		return this;
	}

}
