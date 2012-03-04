package ch.almana.android.stechkarte.view.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
	private Button buDeleteLastTs;

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
		buDeleteLastTs = (Button) v.findViewById(R.id.buDeleteLastTs);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final FragmentActivity act = getActivity();
		int width = act.getWindowManager().getDefaultDisplay().getWidth();
		width = Math.round(width / 2f);
		buttonIn.setWidth(width);
		buttonIn.setHeight(width);
		buttonOut.setWidth(width);
		buttonOut.setHeight(width);

		buttonIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addInNow(act);
				updateView();
			}
		});

		buttonOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TimestampAccess.getInstance().addOutNow(act);
				updateView();
			}
		});
		buDeleteLastTs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.title_delete_last_timestamp);
				builder.setMessage(R.string.msg_delete_last_timestamp);
				builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CurInfo curInfo = new CurInfo(getActivity());
						TimestampAccess.getInstance().delete(curInfo.getTimestamp());
						updateView();
					}
				});
				builder.setNegativeButton(android.R.string.no, null);
				builder.create().show();
			}
		});

	}


	@Override
	public void onResume() {
		updateView();
		super.onResume();
	}

	private void updateView() {
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
			tvLastTimestamp.setText(Timestamp.timestampToString(unixTimestamp, true));
		} else {
			tvLastTimestamp.setText("none");
		}
		buDeleteLastTs.setHeight(hoursWorked.getHeight());
	}

}