package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustJoinInfo;
import cn.yjt.oa.app.beans.CustJoinInviteInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;

import com.google.gson.reflect.TypeToken;

public class CustJoinInviteHandleFragment extends Fragment implements
		OnClickListener, Listener<Response<CustJoinInfo>> {

	private TextView message;
	private TextView title;
	private CustJoinInviteInfo inviteInfo;
	private ProgressDialog progressDialog;
	private Cancelable cancelable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_invite_handle,
				container, false);
		Button refuseBtn = (Button) root
				.findViewById(R.id.invite_handle_button_left);
		refuseBtn.setOnClickListener(this);
		Button handledBtn = (Button) root
				.findViewById(R.id.invite_handle_button_middle);
		handledBtn.setOnClickListener(this);
		Button applyBtn = (Button) root
				.findViewById(R.id.invite_handle_button_right);
		applyBtn.setOnClickListener(this);
		message = (TextView) root.findViewById(R.id.invite_handle_message);
		title = (TextView) root.findViewById(R.id.invite_handle_title);

		inviteInfo = getArguments().getParcelable("inviteInfo");

		title.setText(inviteInfo.getTitle());
		message.setText(inviteInfo.getContent());
		if(AccountManager.getCurrent(getActivity()).getId()==inviteInfo.getInviteUserId()){
			handledBtn.setVisibility(View.VISIBLE);
			handledBtn.setText("确定");
			handledBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getActivity().finish();
					
				}
			});
			refuseBtn.setVisibility(View.GONE);
			applyBtn.setVisibility(View.GONE);
			root.findViewById(R.id.tv_line).setVisibility(View.GONE);
		}else{
			switchButtonText(inviteInfo.getHandleStatus(), handledBtn,
					root.findViewById(R.id.tv_line), refuseBtn, applyBtn);
		}

		return root;

	}

	private void switchButtonText(int status, Button button, View line,
			Button leftButton, Button rightButton) {

		switch (status) {
		case 0:
			button.setVisibility(View.GONE);
			leftButton.setVisibility(View.VISIBLE);
			rightButton.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
			break;
		case 1:
			button.setVisibility(View.VISIBLE);
			button.setText("您已同意此邀请");
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			break;
		case -1:
			button.setVisibility(View.VISIBLE);
			button.setText("您已拒绝此邀请");
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.invite_handle_button_left:
			handle("-1");
			break;
		case R.id.invite_handle_button_middle:
			
			break;
		case R.id.invite_handle_button_right:
			handle("1");
			break;

		default:
			break;
		}
	}

	private void handle(String operation) {
		new TypeToken<CustJoinInfo>() {
		}.getType();
		cancelable = new AsyncRequest.Builder()
				.setModule(AsyncRequest.MODULE_CUSTS_INVITES)
				.setModuleItem(String.valueOf(inviteInfo.getId()))
				.addQueryParameter("operation", operation)
				.setResponseType(new TypeToken<Response<CustJoinInfo>>() {
				}.getType()).setResponseListener(this).build().put();
		progressDialog = ProgressDialog.show(getActivity(), null, "正在提交...");

		/*记录操作 1302*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_ACCEPT_INVITE);
	}

	private void dismissDialog() {
		if (getActivity() == null) {
			return;
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void toast(String message) {
		if (getActivity() != null) {
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onErrorResponse(InvocationError arg0) {
		dismissDialog();
		toast("提交失败。");
	}

	@Override
	public void onDestroy() {
		if(cancelable != null){cancelable.cancel();}
		super.onDestroy();
	}

	@Override
	public void onResponse(Response<CustJoinInfo> resp) {
		dismissDialog();
		if (resp.getCode() == 0) {
			toast("提交成功。");
			if (getActivity() != null) {
				getActivity().finish();
			}
		} else if(resp.getCode() == 2){
			toast(resp.getDescription());
			if (getActivity() != null) {
				getActivity().finish();
			}
		}else{
			toast(resp.getDescription());
		}
	}

}
