package cn.yjt.oa.app.push;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ApplyInfo;
import cn.yjt.oa.app.beans.CustJoinInviteInfo;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.contactlist.InviteUserHandleActivity;
import cn.yjt.oa.app.enterprise.CreateCustHandleActivity;
import cn.yjt.oa.app.enterprise.CustJoinInviteHandleActivity;
import cn.yjt.oa.app.enterprise.JoinCustHandleActivity;
import cn.yjt.oa.app.enterprise.MessageDetailActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.notifications.NotificationDetailActivity;
import cn.yjt.oa.app.sharelink.ShareLinkActivity;
import cn.yjt.oa.app.task.TaskDetailActivity;
import cn.yjt.oa.app.task.URLSpanNoUnderline;

public class MessageFragment extends DoubleTapFragment implements
		OnClickListener {
	private TextView title;
	private TextView time;
	private TextView content;
	private TextView currentNum;
	private int index = 0;

	private List<PushMessageData> pushMessageDatas = new ArrayList<PushMessageData>();
	private View root;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.notice_message, null);
		initView(root);
		return root;
	}

	private void initView(View root) {
		title = (TextView) root.findViewById(R.id.push_message_title);
		time = (TextView) root.findViewById(R.id.push_message_time);
		content = (TextView) root.findViewById(R.id.push_message_content);
		content.setMovementMethod(ScrollingMovementMethod.getInstance());
		currentNum = (TextView) root.findViewById(R.id.current_num);
		root.findViewById(R.id.previous).setOnClickListener(this);
		root.findViewById(R.id.next).setOnClickListener(this);
		if (pushMessageDatas.size() > 0) {
			refreshData(pushMessageDatas.get(0));
		}

		listenDoubleTapView(content);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.previous:
			if (index > 0) {
				index--;
				refreshData(pushMessageDatas.get(index));
			}

			break;
		case R.id.next:
			if (index < pushMessageDatas.size() - 1) {
				index++;
				refreshData(pushMessageDatas.get(index));

			}
			break;

		default:
			break;
		}

	}

	public void refreshData(PushMessageData info) {
		String titleStr = info.getTitle();
		if (titleStr != null) {
			title.setText(titleStr + ":");
		} else {
			title.setText("");
		}
		try {
			time.setText(BusinessConstants.formatDate(BusinessConstants
					.parseTime(getCreateTime(info))));
		} catch (ParseException e) {
			e.printStackTrace();
			time.setText("");
		}
		String contentStr = info.getContent();
		if (contentStr != null) {
			Spannable spannedText = Spannable.Factory.getInstance()
					.newSpannable(Html.fromHtml(contentStr));
			Spannable processedText = URLSpanNoUnderline
					.removeUnderlines(spannedText);
			content.setText(processedText);
		} else {
			content.setText("");
		}

		String numFormat = "" + (index + 1) + "/" + pushMessageDatas.size();
		currentNum.setText(numFormat);

	}


	private String getCreateTime(PushMessageData data) {
		String createTime;
		if (data instanceof NoticeInfo) {
			createTime = ((NoticeInfo) data).getCreateTime();
		} else if (data instanceof TaskInfo) {
			createTime = ((TaskInfo) data).getCreateTime();
		} else if (data instanceof ApplyInfo) {
			createTime = ((ApplyInfo) data).getCreateTime();
		} else if (data instanceof CustRegisterInfo) {
			createTime = ((CustRegisterInfo) data).getRegisterTime();
		} else if (data instanceof CustJoinInviteInfo) {
			createTime = ((CustJoinInviteInfo) data).getCreateTime();
		} else if (data instanceof MessageInfo) {
			createTime = ((MessageInfo) data).getUpdateTime();
		}  else if (data instanceof InviteUserInfo) {
			createTime = ((InviteUserInfo) data).getCreateTime();
		} else {
			createTime = null;
		}
		return createTime;
	}

	private boolean isCurrentCust(String custId) {
		if (custId != null) {
			String currentCustId = AccountManager.getCurrent(
					MainApplication.getAppContext()).getCustId();
			return custId.equals(currentCustId);
		}

		return false;
	}


	public void addMessage(PushMessageData info) {
		pushMessageDatas.add(info);
		if (currentNum != null) {
			refreshData(pushMessageDatas.get(index));
		}

	}

	private void updateReadStatue(String type, long contentId) {
		Builder builder = new Builder();
		String uploadUrl = "messagecenter/isread";
		builder.addQueryParameter("type", type);
		builder.addQueryParameter("contentId", String.valueOf(contentId));
		builder.setModule(uploadUrl);
		builder.setResponseType(new TypeToken<Response<String>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {

			}

			@Override
			public void onResponse(Response<String> arg0) {

			}
		});
		builder.build().put();
	}

	private boolean isNotNeedSwitch(String custId){
		return "0".equals(custId);
	}
	
	@Override
	protected void onDoubleTap(View view) {
		PushMessageData pushMessageData = pushMessageDatas.get(index);

		if (view == content) {
			if (isNotNeedSwitch(pushMessageData.getUserCustId()) || isCurrentCust(pushMessageData.getUserCustId())) {
				directToDetail(pushMessageData);
				pushMessageDatas.remove(index);
				return;
			} else {
				loginToCust(pushMessageData);
			}
		}
	}

	private void loginToCust(final PushMessageData pushMessageData) {
		progressDialog = ProgressDialog.show(getActivity(), null, "正在切换企业...");
		Type responseType = new TypeToken<Response<UserInfo>>() {
		}.getType();
		UserLoginInfo requestBody = new UserLoginInfo();
		requestBody.setType("CUST");
		try {
		requestBody.setContentId(Long.valueOf(pushMessageData.getUserCustId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		UserInfo userInfo = AccountManager.getCurrent(getActivity());
		requestBody.setPhone(userInfo.getPhone());
		requestBody.setPassword(AccountManager.getPassword(getActivity()));
		
		new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_SWITCHCUST)
				.setRequestBody(requestBody)
				.setResponseType(responseType)
				.setResponseListener(new Listener<Response<UserInfo>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						progressDialog.dismiss();
						progressDialog = null;
						Toast.makeText(getActivity(), "网络不给力，切换失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(Response<UserInfo> response) {
						progressDialog.dismiss();
						progressDialog = null;
						if (response.getCode() == 0) {
							UserInfo userInfo = response.getPayload();
							if (userInfo != null) {
								AccountManager.updateUserInfo(
										MainApplication.getAppContext(),
										userInfo);
							}
							MainApplication.sendLoginBroadcast(MainApplication
									.getAppContext());
							directToDetail(pushMessageData);
							pushMessageDatas.remove(index);
						} else {
							Toast.makeText(getActivity(),
									response.getDescription(),
									Toast.LENGTH_SHORT).show();
						}

					}
				}).build().post();
	}

	private void directToDetail(PushMessageData pushMessageData) {

		if (pushMessageData instanceof NoticeInfo) {
			updateReadStatue(((NoticeInfo) pushMessageData).getHandleCmd(),
					((NoticeInfo) pushMessageData).getId());
			NotificationDetailActivity.launch(getActivity(),
					(NoticeInfo) pushMessageData);
		} else if (pushMessageData instanceof TaskInfo) {
			updateReadStatue(((TaskInfo) pushMessageData).getHandleCmd(),
					((TaskInfo) pushMessageData).getId());
			TaskDetailActivity.openTaskDetail(getActivity(),
					(TaskInfo) pushMessageData);
		} else if (pushMessageData instanceof ApplyInfo) {
			updateReadStatue(((ApplyInfo) pushMessageData).getHandleCmd(),
					((ApplyInfo) pushMessageData).getId());
			JoinCustHandleActivity.launchWithApplyInfo(getActivity(),
					(ApplyInfo) pushMessageData);
			// if(MessageInfo.JOIN_CUST_APPLY.equalsIgnoreCase(((MessageInfo)
			// pushMessageData).getType())){
			// }else
			// if(MessageInfo.CREATE_CUST_APPLY.equalsIgnoreCase(((MessageInfo)
			// pushMessageData).getType())){
			// CreateCustHandleActivity.launchWithMessageInfo(getActivity(),
			// (MessageInfo) pushMessageData);
			// }
		} else if (pushMessageData instanceof CustRegisterInfo) {
			updateReadStatue(
					((CustRegisterInfo) pushMessageData).getHandleCmd(),
					((CustRegisterInfo) pushMessageData).getId());
			CreateCustHandleActivity.launchWithCustRegisterInfo(getActivity(),
					(CustRegisterInfo) pushMessageData);
		} else if (pushMessageData instanceof CustJoinInviteInfo) {
			updateReadStatue(
					((CustJoinInviteInfo) pushMessageData).getHandleCmd(),
					((CustJoinInviteInfo) pushMessageData).getId());
			CustJoinInviteHandleActivity.launchWithInviteInfo(getActivity(),
					(CustJoinInviteInfo) pushMessageData);
		} else if (pushMessageData instanceof MessageInfo) {
			MessageInfo info = (MessageInfo) pushMessageData;
			if(MessageInfo.MESSAGE.equals(info.getType())){
				
				updateReadStatue(((MessageInfo) pushMessageData).getHandleCmd(),
						((MessageInfo) pushMessageData).getId());
				MessageDetailActivity.launch(getActivity(),
						(MessageInfo) pushMessageData);
			}else if(MessageInfo.SHARE_LINK.equals(info.getType())){
				
				updateReadStatue(((MessageInfo) pushMessageData).getHandleCmd(),
						((MessageInfo) pushMessageData).getId());
				ShareLinkActivity.launch(getActivity(),
						(MessageInfo) pushMessageData);
			}
			

		}  else if (pushMessageData instanceof InviteUserInfo) {
			updateReadStatue(((InviteUserInfo) pushMessageData).getHandleCmd(),
					((InviteUserInfo) pushMessageData).getId());
			InviteUserHandleActivity.launch(getActivity(),
					(InviteUserInfo) pushMessageData);

		} else {
			// do nothing
		}
	}

}
