package ch.almana.android.stechkarte.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.calc.RebuildDaysTask;
import ch.almana.android.stechkarte.provider.db.DB.Days;
import ch.almana.android.stechkarte.provider.db.DB.Timestamps;
import ch.almana.android.stechkarte.view.activity.BackupRestoreActivity;
import ch.almana.android.stechkarte.view.activity.ExportTimestamps;
import ch.almana.android.stechkarte.view.activity.HolidaysEditor;
import ch.almana.android.stechkarte.view.activity.StechkartePreferenceActivity;

public class MenuHelper {

	public static boolean handleCommonOptions(Context ctx, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemExportTimestamps:
			if (Settings.getInstance().isEmailExportEnabled()) {
				ctx.startActivity(new Intent(ctx, ExportTimestamps.class));
			} else {
				DialogHelper.showFreeVersionDialog(ctx);
			}
			break;
		case R.id.itemReadInTimestmaps:
			if (Settings.getInstance().isBackupEnabled()) {
				ctx.startActivity(new Intent(ctx, BackupRestoreActivity.class));
			} else {
				DialogHelper.showFreeVersionDialog(ctx);
			}
			break;
		case R.id.itemPreferences:
			ctx.startActivity(new Intent(ctx.getApplicationContext(), StechkartePreferenceActivity.class));
			break;
		case R.id.itemHolidayEditor:
			ctx.startActivity(new Intent(ctx, HolidaysEditor.class));
			break;
		case R.id.itemFAQ:
			ctx.startActivity(new Intent(Intent.ACTION_DEFAULT, Uri.parse("http://clockcard.sourceforge.net/faq.html")));
			break;
		case R.id.itemDaylistRebuild:
			RebuildDaysTask.rebuildDays(ctx, null);
			break;
		case R.id.itemDaylistInsertDay:
			ctx.startActivity(new Intent(Intent.ACTION_INSERT, Days.CONTENT_URI));
			break;
		case R.id.itemDaylistInsertTImestamp:
			ctx.startActivity(new Intent(Intent.ACTION_INSERT, Timestamps.CONTENT_URI));
			break;
		default:
			return false;

		}
		return true;
	}

}
