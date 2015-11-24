package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;
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
import cn.yjt.oa.app.beans.ApplyInfo;
import cn.yjt.oa.app.beans.CustJoinInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;

public class AdministratorHandleFragment extends Fragment implements
		OnClickListener {

	private ApplyInfo applyInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_administrator_handler,
				container, false);
		TextView title = (TextView) root
				.findViewById(R.id.administrator_handle_title);
		TextView message = (TextView) root
				.findViewById(R.id.administrator_handle_message);
		Button handleBtnMiddle = (Button) root
				.findViewById(R.id.administrator_handle_button_middle);
		View line = root.findViewById(R.id.tv_line);
		Button handleBtnLeft = (Button) root
				.findViewById(R.id.administrator_handle_button_left);
		Button handleBtnRight = (Button) root
				.findViewById(R.id.administrator_handle_button_right);
		handleBtnMiddle.setOnClickListener(this);
		handleBtnLeft.setOnClickListener(this);
		handleBtnRight.setOnClickListener(this);
		applyInfo = getArguments().getParcelable("applyinfo");
		title.setText(applyInfo.getTitle());
		message.setText(applyInfo.getContent());
		switchButtonText(applyInfo.getHandleStatus(), handleBtnMiddle, line,
				handleBtnLeft, handleBtnRight);

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
			button.setText("您已同意此申请");
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			break;
		case -1:
			button.setVisibility(View.VISIBLE);
			button.setText("您已拒绝此申请");
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
		case R.id.administrator_handle_button_middle:
			// Do nothing.
			break;
		case R.id.administrator_handle_button_left:
			refuse();
			break;
		case R.id.administrator_handle_button_right:
			accept();
			break;

		default:
			break;
		}
	}

	private void refuse() {

		sendHandle(0);

	}

	private void sendHandle(final int operation) {
		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(String.format(
				AsyncRequest.MODULE_CUSTS_APPLIES_ID, applyInfo.getId()));
		requestBuilder
				.addQueryParameter("operation", String.valueOf(operation));
		requestBuilder.setResponseType(new TypeToken<Response<CustJoinInfo>>() {
		}.getType());
		requestBuilder
				.setResponseListener(new Listener<Response<CustJoinInfo>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						if (getActivity() != null) {
							Toast.makeText(getActivity(), "请求失败，请重试",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onResponse(Response<CustJoinInfo> arg0) {
						if (getActivity() != null) {

							if (arg0.getCode() == 0) {
//								if(operation==1){
//									MainApplication.clearContacts();
//								}
								Toast.makeText(getActivity(),
										operation == 0 ? "您已拒绝此申请" : "您已同意此申请",
										Toast.LENGTH_SHORT).show();
								getActivity().finish();
							} else if (arg0.getCode() == 2) {
								Toast.makeText(getActivity(), arg0.getDescription(),
										Toast.LENGTH_SHORT).show();
								getActivity().finish();
							} else if (arg0.getCode() == 3100) {
								Toast.makeText(getActivity(), "此请求已被处理",
										Toast.LENGTH_SHORT).show();
								getActivity().finish();
							}else{
								Toast.makeText(getActivity(), "服务器繁忙，请稍后",
										Toast.LENGTH_SHORT).show();

							}
						}
					}
				});
		requestBuilder.build().put();
	}

	private void accept() {

		/*记录操作 1303*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_AGREE_JOIN_ENTERPRISE);

		sendHandle(1);
	}
}
