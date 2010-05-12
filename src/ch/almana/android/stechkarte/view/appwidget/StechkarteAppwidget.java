package ch.almana.android.stechkarte.view.appwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.log.Logger;
import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.view.CheckinActivity;

public class StechkarteAppwidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(Logger.LOG_TAG, "onUpdate started");
		context.startService(new Intent(context, UpdateAppWidgetService.class));
	}
	
	public static class UpdateAppWidgetService extends Service {
		
		@Override
		public void onStart(Intent intent, int startId) {
			Log.i(Logger.LOG_TAG, "UpdateService started");
			
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
			
			String inOut = Timestamp.getTimestampTypeAsString(context, Timestamp.TYPE_OUT);
			Cursor cd = DayAccess.getInstance().query(null);
			Cursor ct = null;
			Timestamp ts = null;
			try {
				if (cd.moveToFirst()) {
					Day day = new Day(cd);
					ct = day.getTimestamps();
					if (ct.moveToLast()) {
						ts = new Timestamp(ct);
						inOut = ts.getTimestampTypeAsString(context);
					}
				}
			} finally {
				if (cd != null && !cd.isClosed()) {
					cd.close();
				}
				if (ct != null && !ct.isClosed()) {
					ct.close();
				}
			}
			
			int color = R.color.timestampTypeOut;
			if (ts != null && Timestamp.TYPE_IN == ts.getTimestampType()) {
				color = R.color.timestampTypeIn;
			}
			views.setTextColor(R.id.TextViewAppWidgetInOut, context.getResources().getColor(color));
			
			views.setTextViewText(R.id.TextViewAppWidgetInOut, inOut);
			views.setTextViewText(R.id.TextViewAppWidgetLastTSTime, ts.getHM());
			views.setTextViewText(R.id.TextViewAppWidgetLastTSDate, ts.getDayRef() + "");
			
			Intent intent = new Intent(context, CheckinActivity.class);
			intent.setAction(CheckinActivity.ACTION_TIMESTAMP_TOGGLE);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.LinearLayoutAppwidget1x1, pendingIntent);
			return views;
		}
	}
}
