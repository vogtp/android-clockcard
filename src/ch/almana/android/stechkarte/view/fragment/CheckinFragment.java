package ch.almana.android.stechkarte.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import ch.almana.android.stechkarte.R;
import ch.almana.android.stechkarte.model.Timestamp;
import ch.almana.android.stechkarte.model.TimestampAccess;
import ch.almana.android.stechkarte.utils.CurInfo;

public class CheckinFragment extends Fragment {

	private TextView status;
	private TextView overtime;
	private TextView hoursWorked;
	private TextView leaveAt;

	private TextView holidaysLeft;
	private TextView labelLeaveAt;
	private Button buttonIn;
	private Button buttonOut;
	private TextView tvLastTimestamp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main, container, false);

		status = (TextView) v.findViewById(R.id.TextViewStatus);
		overtime = (TextView) v.findViewById(R.id.TextViewOvertime);
		hoursWorked = (TextView) v.findViewById(R.id.TextViewHoursWorked);
		holidaysLeft = (TextView) v.findViewById(R.id.TextViewHolidaysLeft);
		leaveAt = (TextView) v.findViewById(R.id.TextViewLeave);
		labelLeaveAt = (TextView) v.findViewById(R.id.LabelLeavetAt);
		tvLastTimestamp = (TextView) v.findViewById(R.id.tvLastTimestamp);
		buttonIn = (Button) v.findViewById(R.id.ButtonIn);
		buttonOut = (Button) v.findViewById(R.id.ButtonOut);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final FragmentActivity act = getActivity();
		int width = act.getWindowManager().getDefaultDisplay().getWidth();
		width = Math.round(width / 2f);
		int size = Math.round(width / 5f);
		buttonIn.setWidth(width);
		buttonIn.setHeight(width);
		buttonOut.setWidth(width);
		buttonOut.setHeight(width);

		buttonIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addInNow(act);
				updateFields();
			}
		});

		buttonOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addOutNow(act);
				updateFields();
			}
		});

	}


	@Override
	public void onResume() {
		updateFields();
		super.onResume();
	}

	private void updateFields() {
		CurInfo curInfo = new CurInfo(getActivity());

		if (curInfo.getTimestampType() == Timestamp.TYPE_IN) {
			if (curInfo.getLeaveInMillies() > 0l) {
				leaveAt.setText(curInfo.getLeaveAtString());
			} else {
				leaveAt.setText("now");
			}
			leaveAt.setVisibility(TextView.VISIBLE);
			labelLeaveAt.setVisibility(TextView.VISIBLE);
		} else {
			leaveAt.setText(""); 
			leaveAt.setVisibility(TextView.GONE);
			labelLeaveAt.setVisibility(TextView.GONE);
		}

		status.setText("You are " + curInfo.getInOutString());
		holidaysLeft.setText(curInfo.getHolydayLeft());

		overtime.setText(curInfo.getOvertimeString());
		hoursWorked.setText(curInfo.getHoursWorked());
		long unixTimestamp = curInfo.getUnixTimestamp();
		if (unixTimestamp > 0) {
			tvLastTimestamp.setText(Timestamp.timestampToString(unixTimestamp));
		} else {
			tvLastTimestamp.setText("none");
		}
	}

}