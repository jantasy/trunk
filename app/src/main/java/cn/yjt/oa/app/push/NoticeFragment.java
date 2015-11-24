package cn.yjt.oa.app.push;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.notifications.NotificationDetailActivity;
import cn.yjt.oa.app.task.URLSpanNoUnderline;

public class NoticeFragment extends DoubleTapFragment implements
		OnClickListener {
	private TextView mNoticeTitle;
	private TextView mNoticeTime;
	private TextView mNoticeContent;
	private TextView mCurrentNum;
	private int index = 0;

	private List<NoticeInfo> mNoticeList = new ArrayList<NoticeInfo>();
	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.notice_message, null);
		initView(root);
		return root;
	}

	private void initView(View root) {
		mNoticeTitle = (TextView) root.findViewById(R.id.notice_title);
		mNoticeTime = (TextView) root.findViewById(R.id.notice_time);
		mNoticeContent = (TextView) root.findViewById(R.id.notice_content);
		mNoticeContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		mCurrentNum = (TextView) root.findViewById(R.id.current_num);
		root.findViewById(R.id.previous).setOnClickListener(this);
		root.findViewById(R.id.next).setOnClickListener(this);
		if (mNoticeList.size() > 0) {
			refreshData(mNoticeList.get(0));
		}

		listenDoubleTapView(mNoticeContent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.previous:
			if (index > 0) {
				index--;
				refreshData(mNoticeList.get(index));
			}

			break;
		case R.id.next:
			if (index < mNoticeList.size() - 1) {
				index++;
				refreshData(mNoticeList.get(index));

			}
			break;

		default:
			break;
		}

	}

	public void refreshData(NoticeInfo info) {
		mNoticeTitle.setText(info.getTitle() + ":");
		try {
			mNoticeTime.setText(BusinessConstants.formatDate(BusinessConstants
					.parseTime(info.getCreateTime())));
		} catch (ParseException e) {
			e.printStackTrace();
			mNoticeTime.setText("");
		}

		Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
				Html.fromHtml(info.getContent()));
		Spannable processedText = URLSpanNoUnderline
				.removeUnderlines(spannedText);
		mNoticeContent.setText(processedText);
		String numFormat = "" + (index + 1) + "/" + mNoticeList.size();
		mCurrentNum.setText(numFormat);

	}

	public void addNotice(NoticeInfo info) {
		mNoticeList.add(info);
		if (mCurrentNum != null) {
			refreshData(mNoticeList.get(index));
		}

	}

	@Override
	protected void onDoubleTap(View view) {
		if (view == mNoticeContent) {
			NotificationDetailActivity.launch(getActivity(),
					mNoticeList.get(index));
		}
	}

}
