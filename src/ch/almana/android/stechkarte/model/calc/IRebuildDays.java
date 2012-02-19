package ch.almana.android.stechkarte.model.calc;

import ch.almana.android.stechkarte.model.Day;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.utils.IProgressWrapper;

public interface IRebuildDays {

	public void recalculateDayFromTimestamp(Timestamp timestamp, IProgressWrapper progressWrapper);

	public void recalculate(Day day);

}
