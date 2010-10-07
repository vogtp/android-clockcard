package ch.almana.android.stechkarte.utils;

import java.lang.reflect.Method;

public class StechkarteBackupAgentHelper {
	private static boolean hasBackupAgent = true;
	private static Method dataChanged = null;

	public static void dataChanged() {
		if (hasBackupAgent) {
			try {
				if (dataChanged == null) {
					@SuppressWarnings("rawtypes")
					Class bckMgr = Class.forName("android.app.backup.BackupManager");

					dataChanged = bckMgr.getMethod("dataChanged", new Class[] { String.class });
				}
				dataChanged.invoke(null, new Object[] { "ch.almana.android.stechkarte" });
			} catch (Throwable e) {
				hasBackupAgent = false;
			}
		}
	}
}
