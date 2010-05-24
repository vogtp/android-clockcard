package ch.almana.android.stechkarte.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.provider.db.DB;
import ch.almana.android.stechkarte.provider.db.DB.Days;

public class DeleteDayDialog {
	
	private final Builder alert;
	private final DeleteDayHandler diaHandler;
	private final DialogCallback callback;
	
	private Uri uri;

	public DeleteDayDialog(DialogCallback callback, long dbId) {
		this.callback = callback;
		alert = new AlertDialog.Builder(callback.getContext());
		this.uri = ContentUris.withAppendedId(Days.CONTENT_URI, dbId);
		diaHandler = new DeleteDayHandler(uri);
		alert.setItems(diaHandler.getActions(), diaHandler);
		alert.create();
	}
	
	private class DeleteDayHandler implements DialogInterface.OnClickListener {
		
		private static final int ACTION_DEL_DAY = 0;
		private static final int ACTION_DEL_DAY_TIMESTAMPS = 1;
		// private static final int ACTION_CANCEL = 2;
		private static final int ACTIONS_MAX = 3;
		private final Uri uri;
		private Day day;
		private final CharSequence[] actions;
		private boolean isDeleted = false;
		private int timestampCount = 0;
		
		public DeleteDayHandler(Uri uri) {
			this.uri = uri;
			boolean hasTimestamps = false;
			Cursor c = null;
			Cursor ct = null;
			try {
				c = DayAccess.getInstance().query(uri, DB.Days.DEFAULT_PROJECTION, null, null,
						DB.Days.DEFAULT_SORTORDER);
				c.moveToFirst();
				day = new Day(c);
				if (day != null) {
					ct = day.getTimestamps();
					timestampCount = ct.getCount();
					if (timestampCount > 0) {
						hasTimestamps = true;
					}
				}
			} finally {
				if (c != null) {
					c.close();
				}
				if (ct != null) {
					ct.close();
				}
			}
			
			int actionCount = ACTIONS_MAX;
			if (!hasTimestamps) {
				actionCount--;
			}
			actions = new CharSequence[actionCount];
			Context ctx = callback.getContext();
			actions[ACTION_DEL_DAY] = ctx.getString(R.string.deleteDayOnly);
			if (hasTimestamps) {
				actions[ACTION_DEL_DAY_TIMESTAMPS] = ctx
						.getString(R.string.deleteDayAndTS);
			}
			actions[actionCount - 1] = ctx.getString(android.R.string.cancel);
		}
		
		public CharSequence[] getActions() {
			return actions;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case ACTION_DEL_DAY:
				if (deleteDay()) {
					isDeleted = true;
				}
				break;
			case ACTION_DEL_DAY_TIMESTAMPS:
				if (deleteTimestamps()) {
					if (deleteDay()) {
						isDeleted = true;
					}
				}
				break;
			}
			if (isDeleted) {
				callback.finished(isDeleted);
			}
		}
		
		private boolean deleteTimestamps() {
			int delRows = DayAccess.getInstance().deleteTimestamps(day);
			return delRows >= timestampCount;
		}
		
		private boolean deleteDay() {
			int delRows = DayAccess.getInstance().delete(uri, null, null);
			return delRows > 0;
		}
		
		public boolean isDeleted() {
			return isDeleted;
		}
	}
	
	public void setTitle(String title) {
		alert.setTitle(title);
	}
	
	public boolean show() {
		alert.show();
		return diaHandler.isDeleted();
	}
	
	public boolean isDeleted() {
		return diaHandler.isDeleted();
	}

	public Uri getUri() {
		return uri;
	}

}
