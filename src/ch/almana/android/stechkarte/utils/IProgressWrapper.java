package ch.almana.android.stechkarte.utils;

public interface IProgressWrapper {

	public void setTitle(String string);

	public void show();

	public void dismiss();

	public void setMax(int i);

	public void incrementEvery(int i);

	public void setProgress(int i);

}
