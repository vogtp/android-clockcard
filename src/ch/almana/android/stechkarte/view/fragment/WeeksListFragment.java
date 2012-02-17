package ch.almana.android.stechkarte.view.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Week;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Weeks;
import ch.almana.android.stechkarte.utils.DialogCallback;
import ch.almana.android.stechkarte.utils.Formater;
import ch.almana.android.stechkarte.utils.RebuildDaysTask;
import ch.almana.android.stechkarte.view.activity.TabbedMainActivity;

public class WeeksListFragment extends ListFragment implements DialogCallback, LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView listView = getListView();
		View header = getLayoutInflater(savedInstanceState).inflate(R.layout.monthlist_header, listView, false);
		listView.addHeaderView(header);
		((TextView) header.findViewById(R.id.labelRef)).setText(R.string.label_week);

		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.monthlist_item, null, new String[] { DB.NAME_ID, DB.Weeks.NAME_WEEKREF,
				DB.Weeks.NAME_HOURS_WORKED, DB.Weeks.NAME_OVERTIME, DB.Weeks.NAME_HOURS_TARGET, DB.Weeks.NAME_HOLIDAY, DB.Weeks.NAME_HOLIDAY_LEFT },
				new int[] {
						R.id.TextViewMonthRef, R.id.TextViewMonthRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget,
						R.id.TextViewHoliday,
						R.id.TextViewHolidaysLeft }, 0);

		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Weeks.INDEX_WEEKREF) {
					Week w = new Week(cursor);
					int color = Color.GREEN;
					if (w.isError()) {
						color = Color.RED;
					}
					TextView errorView = (TextView) view.findViewById(R.id.TextViewMonthRef);
					errorView.setTextColor(color);
					// since we do not set the dayref no: return true;
				} else if (columnIndex == DB.Weeks.INDEX_OVERTIME) {
					Week w = new Week(cursor);
					CharSequence formatHourMinFromHours = Formater.formatHourMinFromHours(w.getOvertime());
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(formatHourMinFromHours);
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = w.getHoursWorked() - w.getHoursTarget();
					tv.setText(Formater.formatHourMinFromHours(overtime));
					tv.setTextColor(Color.LTGRAY);
					if (overtime > 150) {
						tv.setTextColor(Color.RED);
					} else if (overtime > 90) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Weeks.INDEX_HOURS_WORKED) {
					float hoursWorked = cursor.getFloat(Weeks.INDEX_HOURS_WORKED);
					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatHourMinFromHours(hoursWorked));
					tv.setTextColor(Color.LTGRAY);
					if (hoursWorked > 360) {
						tv.setTextColor(Color.RED);
					} else if (hoursWorked > 300) {
						tv.setTextColor(Color.YELLOW);
					}
					return true;
				} else if (columnIndex == DB.Weeks.INDEX_HOURS_TARGET) {
					Week m = new Week(cursor);
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
	public void onResume() {
		Context ctx = getActivity();
		if (TabbedMainActivity.instance != null) {
			ctx = TabbedMainActivity.instance;
		}
		RebuildDaysTask.rebuildDaysIfNeeded(ctx);
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.monthlist_option, menu);
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (id < 0) {
			return;
		}
		Uri uri = ContentUris.withAppendedId(Weeks.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}

	private void rebuildDays() {
		Context ctx = getActivity();
		if (TabbedMainActivity.instance != null) {
			ctx = TabbedMainActivity.instance;
		}
		RebuildDaysTask.rebuildDays(ctx, null);
	}

	@Override
	public void finished(boolean success) {
	}

	@Override
	public Context getContext() {
		return getActivity();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), DB.Weeks.CONTENT_URI, DB.Weeks.DEFAULT_PROJECTION, null, null, Weeks.DEFAULT_SORTORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		adapter.swapCursor(c);

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
}
