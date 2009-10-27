package ch.almana.android.stechkarte.model;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.widget.Toast;
import ch.almana.android.stechkarte.provider.DB;
import ch.almana.android.stechkarte.provider.DB.Timestamps;

public class TimestampAccess {

	private class AlertDialogHandler implements OnClickListener {

		private static final int ACTION_ADD_INVERTED = 0;
		private static final int ACTION_ADD = 1;
		private static final int ACTION_ADDLAST_ADD = 2;
		private static final int ACTION_CANCAL = 3;
		private static final int ACTION_MAX = 4;

		private Timestamp timestamp;
		private CharSequence[] actions;
		private Context context;

		public AlertDialogHandler(Context context, Timestamp timestamp) {
			this.timestamp = timestamp;
			this.context = context;
			actions = new String[ACTION_MAX];
			String invTsTAsString = Timestamp.getTimestampTypeAsString(context, Timestamp
					.invertTimestampType(timestamp));
			String tstAsString = Timestamp.getTimestampTypeAsString(context, timestamp);
			actions[ACTION_ADD_INVERTED] = "Add " + invTsTAsString;
			actions[ACTION_ADD] = "Add " + tstAsString;
			actions[ACTION_ADDLAST_ADD] = "Add last " + invTsTAsString + " and add " + tstAsString;
			actions[ACTION_CANCAL] = "Cancel";
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case ACTION_ADD_INVERTED:
				timestamp.setTimestampType(Timestamp.invertTimestampType(timestamp));
				insert(timestamp);
				break;
			case ACTION_ADD:
				insert(timestamp);
				break;
			case ACTION_CANCAL:
				Toast.makeText(context, "Canceled action", Toast.LENGTH_SHORT).show();
				break;

			case ACTION_ADDLAST_ADD:
				insert(timestamp);
				Intent i = new Intent(Intent.ACTION_INSERT, Timestamps.CONTENT_URI);
				i.putExtra(Timestamps.COL_NAME_TIMESTAMP_TYPE, Timestamp.invertTimestampType(timestamp));
				context.startActivity(i);
				break;

			default:
				Toast.makeText(context, "Action not implemented.", Toast.LENGTH_SHORT).show();
				break;
			}

		}

		public CharSequence[] getActions() {
			return actions;
		}

	}

	private static final long MIN_TIMESTAMP_DIFF = 1000 * 60;

	private Context context;

	private static TimestampAccess instance;

	public static TimestampAccess getInstance(Context context) {
		if (instance == null) {
			instance = new TimestampAccess(context);
		}
		return instance;
	}

	public TimestampAccess(Context context) {
		super();
		this.context = context;
	}

	public void addOutNow() {
		addOut(System.currentTimeMillis());
	}

	public void addOut(long time) {
		processInOutAdd(new Timestamp(time, Timestamp.TYPE_OUT));
	}

	public void addInNow() {
		addIn(System.currentTimeMillis());
	}

	public void addIn(long time) {
		processInOutAdd(new Timestamp(time, Timestamp.TYPE_IN));
	}

	private void processInOutAdd(Timestamp timestamp) {
		Timestamp lastTs = getLastTimestamp();
		if (lastTs != null) {
			if (Math.abs(timestamp.getTimestamp() - lastTs.getTimestamp()) < MIN_TIMESTAMP_DIFF) {
				String tsTime = Timestamp.formatTime(timestamp);
				String ltsTime = Timestamp.formatTime(lastTs);
				Toast.makeText(
						context,
						"Difference betwenn current and last timestamp is too small!\n" + tsTime + "\n" + ltsTime
								+ "\nIgnoring timestamp.", Toast.LENGTH_LONG).show();
				return;
			}
			if (timestamp.getTimestampType() == lastTs.getTimestampType()) {

				Builder alert = new AlertDialog.Builder(context);
				alert.setTitle("Same timestamp types: " + Timestamp.getTimestampTypeAsString(context, timestamp));
				AlertDialogHandler alertDiaHandler = new TimestampAccess.AlertDialogHandler(context, timestamp);
				alert.setItems(alertDiaHandler.getActions(), alertDiaHandler);
				alert.create().show();
				return;
			}
		}
		insert(timestamp);
	}

	public void insert(Timestamp timestamp) {
		Toast.makeText(context,
				Timestamp.getTimestampTypeAsString(context, timestamp) + ": " + Timestamp.formatTime(timestamp),
				Toast.LENGTH_LONG).show();
		context.getContentResolver().insert(Timestamps.CONTENT_URI, timestamp.getValues());
	}

	public void update(Timestamp timestamp) {
		context.getContentResolver().update(
Timestamps.CONTENT_URI,
				timestamp.getValues(),
				Timestamps.COL_NAME_ID + "=" + timestamp.getId(), null);
	}
	public Cursor query(String selection, String[] selectionArgs) {
		return context.getContentResolver().query(Timestamps.CONTENT_URI,
 Timestamps.DEFAULT_PROJECTION, selection,
				selectionArgs, DB.Timestamps.DEFAUL_SORTORDER);
	}

	private Timestamp getLastTimestamp() {
		Cursor cursor = query(null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return new Timestamp(cursor);
		}
		return null;
	}

}
