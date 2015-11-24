package cn.yjt.oa.app.enterprise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ApplyInfo;

public class ApplicantHandleFragment extends Fragment implements OnClickListener {
	
	
	private ApplyInfo applyInfo;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_applicant_handler, container,false);
		TextView title = (TextView) root.findViewById(R.id.applicant_handle_title);
		TextView message = (TextView) root.findViewById(R.id.applicant_handle_message);
		ImageView icon = (ImageView) root.findViewById(R.id.applicant_handle_icon);
		ImageView tip = (ImageView) root.findViewById(R.id.applicant_handle_icon_tip);
		Button handleButton = (Button) root.findViewById(R.id.applicant_handle_btn);
		handleButton.setOnClickListener(this);
		applyInfo = getArguments().getParcelable("applyinfo");
		title.setText(applyInfo.getTitle());
		message.setText(applyInfo.getContent());
		switchStatus(icon,tip,applyInfo.getHandleStatus());
		switchButton(handleButton, applyInfo.getHandleStatus());
		return root;
	}
	
	private void switchButton(Button button,int status){
		switch (status) {
		case 0:
			//Do nothing.
			break;
		case 1:
			if(applyInfo.getCustId()==Long.parseLong(AccountManager.getCurrent(getActivity()).getCustId())){
				button.setText("查看通讯录");
			}else{
				button.setText("查看企业列表");
			}
			break;
		case -1:
			button.setText("重新选择企业");
			break;

		default:
			break;
		}
	}
	
	private void switchStatus(ImageView icon,ImageView tip,int status){
		switch (status) {
		case 0:
			icon.setImageBitmap(null);
			tip.setImageBitmap(null);
			break;
		case 1:
			icon.setImageResource(R.drawable.ic_apply_accept);
			tip.setImageResource(R.drawable.ic_apply_accept_tip);
			break;
		case -1:
			icon.setImageResource(R.drawable.ic_apply_refuse);
			tip.setImageResource(R.drawable.ic_apply_refuse_tip);
			break;

		default:
			break;
		}
	}

	private void switchButtonClicked(int status){
		switch (status) {
		case 0:
			//Do nothing.
			break;
		case 1:
			if(applyInfo.getCustId()==Long.parseLong(AccountManager.getCurrent(getActivity()).getCustId())){
				viewContacts();
			}else{
				EnterpriseListActivity.launch(getActivity());
				getActivity().finish();
			}
			break;
		case -1:
			selectEnterprise();
			break;

		default:
			break;
		}
	}
	
	
	private void selectEnterprise() {
		CreateEnterpriseActivity.launch(getActivity());
	}

	private void viewContacts() {
		MainActivity.launchAndSelectTab(getActivity(), 2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.applicant_handle_btn:
			switchButtonClicked(applyInfo.getHandleStatus());
			break;

		default:
			break;
		}
	}
}
