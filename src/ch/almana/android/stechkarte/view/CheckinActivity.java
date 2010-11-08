package ch.almana.android.stechkarte.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.utils.CurInfo;

public class CheckinActivity extends Activity {

	public static final String ACTION_TIMESTAMP_TOGGLE = "ch.almana.android.stechkarte.actions.timestampToggle";
	public static final String ACTION_TIMESTAMP_IN = "ch.almana.android.stechkarte.actions.timestampIn";
	public static final String ACTION_TIMESTAMP_OUT = "ch.almana.android.stechkarte.actions.timestampOut";
	private TextView status;
	private TextView overtime;
	private TextView hoursWorked;
	private TextView leaveAt;

	private TextView holidaysLeft;
	private TextView labelLeaveAt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Button buttonIn = (Button) findViewById(R.id.ButtonIn);
		Button buttonOut = (Button) findViewById(R.id.ButtonOut);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		width = Math.round(width / 2f);
		int size = Math.round(width / 5f);
		buttonIn.setWidth(width);
		buttonIn.setHeight(width);
		buttonIn.setTextSize(size);
		buttonOut.setWidth(width);
		buttonOut.setHeight(width);
		buttonOut.setTextSize(size);

		buttonIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addInNow(CheckinActivity.this);
				updateFields();
			}
		});

		buttonOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addOutNow(CheckinActivity.this);
				updateFields();
			}
		});

		status = (TextView) findViewById(R.id.TextViewStatus);
		overtime = (TextView) findViewById(R.id.TextViewOvertime);
		hoursWorked = (TextView) findViewById(R.id.TextViewHoursWorked);
		holidaysLeft = (TextView) findViewById(R.id.TextViewHolidaysLeft);
		leaveAt = (TextView) findViewById(R.id.TextViewLeave);
		labelLeaveAt = (TextView) findViewById(R.id.LabelLeavetAt);
	}

	@Override
	protected void onResume() {
		updateFields();
		super.onResume();
	}

	private void updateFields() {
		CurInfo curInfo = new CurInfo(this);

		if (curInfo.getTimestampType() == Timestamp.TYPE_IN) {
			if (curInfo.getLeaveInMillies() > 0l) {
				leaveAt.setText(curInfo.getLeaveAtString());
			} else {
				leaveAt.setText("now");
			}
			leaveAt.setVisibility(TextView.VISIBLE);
			labelLeaveAt.setVisibility(TextView.VISIBLE);
			leaveAt.setHeight(overtime.getHeight());
			labelLeaveAt.setHeight(overtime.getHeight());
		} else {
			leaveAt.setText("");
			leaveAt.setVisibility(TextView.INVISIBLE);
			leaveAt.setHeight(0);
			labelLeaveAt.setVisibility(TextView.INVISIBLE);
			labelLeaveAt.setHeight(0);
		}

		status.setText("You are " + curInfo.getInOutString());
		holidaysLeft.setText(curInfo.getHolydayLeft());

		overtime.setText(curInfo.getOvertimeString());
		hoursWorked.setText(curInfo.getHoursWorked());

	}

}