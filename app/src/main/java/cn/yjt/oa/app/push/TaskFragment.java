package cn.yjt.oa.app.push;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ReplyInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.task.TaskDetailActivity;
import cn.yjt.oa.app.task.URLSpanNoUnderline;

public class TaskFragment extends DoubleTapFragment implements OnClickListener {
	private TextView mTaskTitle;
	private TextView mTaskTime;
	private TextView mTaskContent;
	private TextView mCurrentNum;
	private EditText mReplyContent;
	private int index = 0;
	private List<TaskInfo> mTaskList = new ArrayList<TaskInfo>();

	private ProgressDialog mReplyingDialog;
	private Listener<Response<ReplyInfo>> mReplyingTaskListner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.task_message, null);
		initView(root);
		return root;
	}

	private void initView(View root) {
		mTaskTitle = (TextView) root.findViewById(R.id.task_title);
		mTaskTime = (TextView) root.findViewById(R.id.task_time);
		mTaskContent = (TextView) root.findViewById(R.id.task_content);
		mCurrentNum = (TextView) root.findViewById(R.id.current_num);
		mReplyContent = (EditText) root.findViewById(R.id.reply_content);
		root.findViewById(R.id.send).setOnClickListener(this);
		root.findViewById(R.id.previous).setOnClickListener(this);
		root.findViewById(R.id.next).setOnClickListener(this);
		if (mTaskList.size() > 0) {
			refreshData(mTaskList.get(0));

		}
		listenDoubleTapView(mTaskContent);
	}
	
	@Override
	protected void onDoubleTap(View view) {
		TaskDetailActivity.openTaskDetail(getActivity(), mTaskList.get(index));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.send:
			sendTaskReply();
			break;
		case R.id.previous:
			if (index > 0) {
				index--;
				refreshData(mTaskList.get(index));
			}

			break;
		case R.id.next:
			if (index < mTaskList.size() - 1) {
				index++;
				refreshData(mTaskList.get(index));

			}
			break;

		default:
			break;
		}

	}

	public void refreshData(TaskInfo info) {
		mTaskTitle.setText(info.getTitle());
		try {
			mTaskTime.setText(BusinessConstants.formatDate(BusinessConstants
					.parseTime(info.getCreateTime())));
		} catch (ParseException e) {
			e.printStackTrace();
			mTaskTime.setText("");
		}
		Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
				Html.fromHtml(info.getContent()));
		Spannable processedText = URLSpanNoUnderline
				.removeUnderlines(spannedText);
		mTaskContent.setText(processedText);
		String numFormat = "" + (index + 1) + "/" + mTaskList.size();
		mCurrentNum.setText(numFormat);

	}

	public void addTask(TaskInfo info) {
		mTaskList.add(info);
		if (mCurrentNum != null) {
			refreshData(mTaskList.get(index));

		}

	}

	private void sendTaskReply() {
		closeSoftInput();

		ReplyInfo replyInfo = new ReplyInfo();
		replyInfo.setTaskId(mTaskList.get(index).getId());
		// replyInfo.setMark(getMarkColor());
		replyInfo.setFromUser(AccountManager
				.getCurrentSimpleUser(getActivity()));
		if (mReplyContent.getText().length() <= 0) {
			Toast.makeText(getActivity(),
					R.string.task_reply_illegal_no_content, Toast.LENGTH_LONG)
					.show();
			return;
		}
		replyInfo.setContent(mReplyContent.getText().toString());

		if (mReplyingDialog == null) {
			mReplyingDialog = new ProgressDialog(getActivity());
			mReplyingDialog.setMessage(getResources().getString(
					R.string.task_replying_progress_dialog_message));
			mReplyingDialog.setCanceledOnTouchOutside(false);
			mReplyingDialog.setCancelable(false);
		}
		mReplyingDialog.show();

		if (mReplyingTaskListner == null) {
			mReplyingTaskListner = new Listener<Response<ReplyInfo>>() {

				@Override
				public void onErrorResponse(InvocationError arg0) {
					Toast.makeText(getActivity(),
							R.string.task_replying_result_failure,
							Toast.LENGTH_LONG).show();
					mReplyingDialog.dismiss();
				}

				@Override
				public void onResponse(Response<ReplyInfo> arg0) {
					mReplyingDialog.dismiss();
					if(arg0.getCode()==0){
						Toast.makeText(getActivity(),
								R.string.task_replying_result_success,
								Toast.LENGTH_LONG).show();

						mReplyContent.getText().clear();
						
					}else{
						Toast.makeText(getActivity(),
								arg0.getDescription(),
								Toast.LENGTH_LONG).show();
					}
					

				}

			};
		}

		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(AsyncRequest.MODULE_TASK);
		requestBuilder.setModuleItem(mTaskList.get(index).getId() + "/replies");
		requestBuilder.setRequestBody(replyInfo);
		requestBuilder.setResponseType(new TypeToken<Response<ReplyInfo>>() {
		}.getType());
		requestBuilder.setResponseListener(mReplyingTaskListner);
		requestBuilder.build().post();
	}

	private void closeSoftInput() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mReplyContent.getWindowToken(), 0);
	}

}
