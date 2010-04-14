package ch.almana.android.stechkarte.view;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;

public class TimestampsAdaptorFactory {

	
	private SimpleCursorAdapter adapter;

	private Context context;

	private ListView listview;

	public TimestampsAdaptorFactory(ListView timestamps, long dayRef) {
		super();
		this.listview = timestamps;
		String selection = null;
		if (dayRef > 0) {
			selection = DB.Days.NAME_DAYREF + "=" + dayRef;
		}

		context = timestamps.getContext();
		Cursor cursor = TimestampAccess.getInstance().query(selection);
		// Used to map notes entries from the database to views
		adapter = new SimpleCursorAdapter(context, R.layout.timestamplist_item, cursor, new String[] {
				Timestamps.NAME_TIMESTAMP, Timestamps.NAME_TIMESTAMP_TYPE }, new int[] {
				R.id.TextViewTimestamp, R.id.TextViewTimestampType });
		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (cursor == null) {
					return false;
				}
				if (columnIndex == Timestamps.INDEX_TIMESTAMP) {
					TextView ts = (TextView) view.findViewById(R.id.TextViewTimestamp);
					long time = cursor.getLong(Timestamps.INDEX_TIMESTAMP);
					ts.setText(Timestamp.timestampToString(time));
				} else if (columnIndex == Timestamps.INDEX_TIMESTAMP_TYPE) {
					String txt = "unknown";
					int type = cursor.getInt(Timestamps.INDEX_TIMESTAMP_TYPE);
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

		timestamps.setAdapter(getAdapter());
	}

	public SimpleCursorAdapter getAdapter() {
		return adapter;
	}



	public void onResume() {
		listview.setAdapter(getAdapter());
	}

}
