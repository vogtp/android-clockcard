package ch.almana.android.stechkarte.view.appwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.utils.CurInfo;
import ch.almana.android.stechkarte.view.activity.TabbedMainActivity;

public class StechkarteAppwidget extends AppWidgetProvider {

	private static boolean doNotUpdate = false;

	public static void setDoNotUpdate(boolean b) {
		doNotUpdate = b;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(Logger.TAG, "onUpdate started");
		StechkarteAppwidget.updateView(context);
	}

	public static void updateView(Context context) {
		context.startService(new Intent(context, UpdateAppWidgetService.class));
	}

	public static class UpdateAppWidgetService extends Service {

		@Override
		public void onStart(Intent intent, int startId) {
			if (doNotUpdate) {
				return;
			}
			Log.i(Logger.TAG, "appwidget update service started");
			// getContentResolver().notifyChange(Timestamps.CONTENT_URI,
			// observer);
			RemoteViews rViews = createAppWidgetView(this);
			ComponentName compName = new ComponentName(this, StechkarteAppwidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(compName, rViews);
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		public static RemoteViews createAppWidgetView(Context context) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_1x1);

			CurInfo curInfo = new CurInfo(context);

			int color = R.color.timestampTypeOut;
			CharSequence addInfo;
			String labelAddInfo;
			if (curInfo.hasData()) {
				if (Timestamp.TYPE_IN == curInfo.getTimestampType()) {
					color = R.color.timestampTypeIn;
					labelAddInfo = "Leave at";
					addInfo = curInfo.getLeaveAtString();
				} else {
					labelAddInfo = "Overtime";
					addInfo = curInfo.getOvertimeString();
				}
			} else {
				addInfo = "";
				labelAddInfo = "";
			}

			views.setTextColor(R.id.TextViewAppWidgetInOut, context.getResources().getColor(color));

			views.setTextViewText(R.id.TextViewAppWidgetInOut, curInfo.getInOutString());
			long unixTimestamp = curInfo.getUnixTimestamp();
			if (unixTimestamp > 0) {
				views.setTextViewText(R.id.TextViewAppWidgetLastTSTime, Timestamp.timestampToString(unixTimestamp));
			} else {
				views.setTextViewText(R.id.TextViewAppWidgetLastTSTime, "none");
			}
			views.setTextViewText(R.id.LabelAddInfo, labelAddInfo);
			views.setTextViewText(R.id.TextViewAppWidgetAddInfo, addInfo);

			Intent intent = new Intent(TabbedMainActivity.ACTION_TIMESTAMP_TOGGLE);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.LinearLayoutAppwidget1x1, pendingIntent);
			return views;
		}
	}
}
