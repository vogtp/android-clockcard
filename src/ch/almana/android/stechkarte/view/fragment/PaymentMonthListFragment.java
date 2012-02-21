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
import ch.almana.android.stechkarte.model.Month;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Months;
import ch.almana.android.stechkarte.utils.DialogCallback;
import ch.almana.android.stechkarte.utils.Formater;
import ch.almana.android.stechkarte.utils.MenuHelper;
import ch.almana.android.stechkarte.utils.Settings;

public class PaymentMonthListFragment extends ListFragment implements DialogCallback, LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.paylist_item, null,
				new String[] { DB.NAME_ID, DB.Months.NAME_MONTHREF, DB.Months.NAME_HOURS_WORKED, DB.Months.NAME_OVERTIME, DB.Months.NAME_HOURS_TARGET },
				new int[] {
						R.id.TextViewMonthRef, R.id.TextViewMonthRef, R.id.TextViewHoursWorked, R.id.TextViewOvertime, R.id.TextViewHoursTarget, }, 0);

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
					float payRate = Settings.getInstance().getPayRateOvertime();
					float pay = m.getOvertime() * payRate;
					((TextView) view.findViewById(R.id.TextViewOvertime)).setText(Formater.formatPayment(pay));
					TextView tv = (TextView) ((View) view.getParent()).findViewById(R.id.TextViewOvertimeCur);
					float overtime = m.getHoursWorked() - m.getHoursTarget();
					pay = overtime * payRate;
					tv.setText(Formater.formatPayment(pay));
					return true;
				} else if (columnIndex == DB.Months.INDEX_HOURS_WORKED) {
					float pay = cursor.getFloat(Months.INDEX_HOURS_WORKED) * Settings.getInstance().getPayRate();

					TextView tv = (TextView) view.findViewById(R.id.TextViewHoursWorked);
					tv.setText(Formater.formatPayment(pay));
					return true;
				} else if (columnIndex == DB.Months.INDEX_HOURS_TARGET) {
					Month m = new Month(cursor);
					((TextView) view.findViewById(R.id.TextViewHoursTarget)).setText(Formater.formatPayment(m.getHoursTarget()
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
		Uri uri = ContentUris.withAppendedId(Months.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));

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
		return new CursorLoader(getActivity(), DB.Months.CONTENT_URI, DB.Months.DEFAULT_PROJECTION, null, null, Months.DEFAULT_SORTORDER);
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
