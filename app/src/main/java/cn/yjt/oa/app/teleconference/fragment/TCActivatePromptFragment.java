package cn.yjt.oa.app.teleconference.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.TCItem;

public class TCActivatePromptFragment extends TCBaseFragment implements IUserView{
	private UserPresenter presenter;
	private TCHeldFragment nextFragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_tc_activate_prompt, container,false);
		
		Button button = (Button) root.findViewById(R.id.activate_prompt_ok);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((FragmentBrige) getActivity()).toFragment(new TCHeldFragment());
			}
		});
		return root;
	}
	
	@Override
	public String getTitle() {
		return "激活提示";
	}
	
	@Override
	public void setUser(TCItem item) {
		
	}
	
	@Override
	public void setUsers(List<TCItem> items) {
		
	}
	
	@Override
	public void setParams(String mobile, String ecp_token, String access_token) {
		nextFragment = new TCHeldFragment();
		presenter = new UserPresenter(nextFragment);
		presenter.addParams(mobile, ecp_token, access_token);
	}

}
