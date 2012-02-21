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
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Weeks;
import ch.almana.android.stechkarte.utils.DialogCallback;
import ch.almana.android.stechkarte.utils.Formater;
import ch.almana.android.stechkarte.utils.MenuHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class PaymentWeekListFragment extends ListFragment implements DialogCallback, LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView listView = getListView();
		View header = getLayoutInflater(savedInstanceState).inflate(R.layout.paylist_header, listView, false);
		listView.addHeaderView(header);
		((TextView) header.findViewById(R.id.labelRef)).setText(R.string.label_week);

		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.paylist_item, null,
				new String[] { DB.NAME_ID, DB.Weeks.NAME_WEEKREF, DB.Weeks.NAME_HOURS_WORKED, DB.Weeks.NAME_OVERTIME, DB.Weeks.NAME_HOURS_TARGET },
				new int[] {
						R.id.TextViewMonthRef, R.id.TextViewMonthRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget, }, 0);

		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}

				if (columnIndex == DB.Weeks.INDEX_WEEKREF) {
					Week m = new Week(cursor);
					int color = Color.GREEN;
					if (m.isError()) {
						color = Color.RED;
					}
					TextView errorView = (TextView) view.findViewById(R.id.TextViewMonthRef);
					errorView.setTextColor(color);
					// since we do not set the dayref no: return true;
				} else if (columnIndex == DB.Weeks.INDEX_OVERTIME) {
					Week w = new Week(cursor);
					float payRate = Settings.getInstance().getPayRateOvertime();
					float pay = w.getOvertime() * payRate;
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(Formater.formatPayment(pay));
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = w.getHoursWorked() - w.getHoursTarget();
					pay = overtime * payRate;
					tv.setText(Formater.formatPayment(pay));
					return true;
				} else if (columnIndex == DB.Weeks.INDEX_HOURS_WORKED) {
					float pay = cursor.getFloat(Weeks.INDEX_HOURS_WORKED) * Settings.getInstance().getPayRate();

					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatPayment(pay));
					return true;
				} else if (columnIndex == DB.Weeks.INDEX_HOURS_TARGET) {
					Week w = new Week(cursor);
					((TextView) view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatPayment(w.getHoursTarget()
							* Settings.getInstance().getPayRate()));
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.monthlist_option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuHelper.handleCommonOptions(getContext(), item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (id < 0) {
			return;
		}
		Uri uri = ContentUris.withAppendedId(Weeks.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}


	@Override
	public void finished(boolean success) {
		// TODO Auto-generated method stub

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
