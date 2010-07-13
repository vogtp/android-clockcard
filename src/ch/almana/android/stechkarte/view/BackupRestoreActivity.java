package ch.almana.android.stechkarte.view;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import ch.almana.android.stechkarte.model.DayAccess;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.io.StechkarteCsvIO;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.RebuildDaysTask;
import ch.almana.android.stechkarte.utils.Settings;

public class BackupRestoreActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] filenames = getFilenames();
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filenames);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				CharSequence text = ((TextView) view.findViewById(android.R.id.text1)).getText();
				Toast.makeText(BackupRestoreActivity.this, "Loading timestamps from " + text, Toast.LENGTH_LONG).show();
				restoreTimestamps(text.toString());
				finish();
			}
		});
	}

	private String[] getFilenames() {
		File exportDir = new File(StechkarteCsvIO.getBasePath());
		String[] list = exportDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				// if (filename == null) {
				// return false;
				// }
				// return
				// filename.startsWith(StechkarteCsvIO.filenameTimestampsStem());
				return true;
			}
		});
		int length = list.length;
		String[] files = new String[length];
		for (String file : list) {
			files[--length] = file;
		}
		return files;
	}

	private void restoreTimestamps(String filename) {
		if (Settings.getInstance().isBackupEnabled()) {
			StechkarteCsvIO csvIO = new StechkarteCsvIO();
			csvIO.restoreTimestamps(filename);
			csvIO = new StechkarteCsvIO();
			csvIO.restoreDays(filename);
			RebuildDaysTask.rebuildDays(this, null);
		} else {
			DialogHelper.showFreeVersionDialog(this);
		}
	}

	static public void backupDbToCsv() {
		StechkarteCsvIO csv = new StechkarteCsvIO();
		Cursor c = TimestampAccess.getInstance().query(null, null);
		csv.writeTimestamps(c);
		c.close();
		csv = new StechkarteCsvIO();
		c = DayAccess.getInstance().query(null, null);
		csv.writeDays(c);
		c.close();
	}

}
