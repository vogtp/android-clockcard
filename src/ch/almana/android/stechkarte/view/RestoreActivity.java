package ch.almana.android.stechkarte.view;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.model.io.TimestampsCsvIO;
import ch.almana.android.stechkarte.utils.DialogHelper;
import ch.almana.android.stechkarte.utils.RebuildDaysTask;
import ch.almana.android.stechkarte.utils.Settings;

public class RestoreActivity extends ListActivity {

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
				Toast.makeText(RestoreActivity.this, "Loading timestamps from " + text, Toast.LENGTH_LONG).show();
				restoreTimestamps(text.toString());
				finish();
			}
		});
	}

	private String[] getFilenames() {
		File exportDir = new File(TimestampsCsvIO.getPath());
		String[] list = exportDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename == null) {
					return false;
				}
				return filename.startsWith(TimestampsCsvIO.filenameStem());
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
			TimestampsCsvIO timestampsCsvIO = new TimestampsCsvIO();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(TimestampsCsvIO.getPath());
			stringBuilder.append(filename);
			timestampsCsvIO.readTimestamps(stringBuilder.toString(), TimestampAccess.getInstance());
			RebuildDaysTask.rebuildDays(this, null);
		} else {
			DialogHelper.showFreeVersionDialog(this);
		}
	}

}
