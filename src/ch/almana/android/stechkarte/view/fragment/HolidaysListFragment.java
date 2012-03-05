package ch.almana.android.stechkarte.view.fragment;

import java.text.DateFormat;
import java.util.Date;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Holidays;
import ch.almana.android.stechkarte.utils.DialogCallback;
import ch.almana.android.stechkarte.utils.MenuHelper;

public class HolidaysListFragment extends ListFragment implements DialogCallback, LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView listView = getListView();
		//		View header = getLayoutInflater(savedInstanceState).inflate(R.layout.monthlist_header, listView, false);
		//		listView.addHeaderView(header);

		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] { DB.Holidays.NAME_START },
				new int[] { android.R.id.text1 }, 0);

		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == DB.Holidays.INDEX_START) {
					DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
					StringBuilder sb = new StringBuilder();
					long start = cursor.getLong(Holidays.INDEX_START);
					sb.append(dateFormat.format(new Date(start)));
					sb.append(" - ");
					sb.append(dateFormat.format(new Date(cursor.getLong(Holidays.INDEX_END))));
					sb.append(" Days: ");
					sb.append(Float.toString(cursor.getFloat(Holidays.INDEX_DAYS)));
					((TextView) view).setText(sb.toString());
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
		// FIXME inflater.inflate(R.menu.monthlist_option, menu);
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
		Uri uri = ContentUris.withAppendedId(Holidays.CONTENT_URI, id);
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
		return new CursorLoader(getActivity(), DB.Holidays.CONTENT_URI, DB.Holidays.DEFAULT_PROJECTION, null, null, Holidays.DEFAULT_SORTORDER);
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
