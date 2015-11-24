package cn.yjt.oa.app.enterprise;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.enterprise.InfoCommitUI.CommitFragment;

public class InfoCommitFinishUI extends BaseActivity{

	@Override
	protected Fragment getFragment() {
		return new CommitFinishFragment();
	}
	
	@Override
	protected CharSequence initTitle() {
		return "申请结果";
	}
	
	@Override
	public void onLeftButtonClick() {
		back();
	}
	
	private void back(){
		Intent intent = new Intent(this,MainActivity.class);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		}else{
			finish();
		}
		startActivity(intent);
	}
	
	public static class CommitFinishFragment extends Fragment implements OnClickListener{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.enterprise_info_commit_finish, container, false);
			initView(view);
			return view;
		}
		
		private Button btnExit;
		private TextView name;
		private ImageView auditStatus;
		private ImageView auditImg;
		private TextView auditText;
		
		private void initView(View view) {
			name = (TextView) view.findViewById(R.id.tv_enterprise_name);
			name.setText(getName());
			
			auditStatus = (ImageView) view.findViewById(R.id.iv_audit_status);
			auditImg = (ImageView) view.findViewById(R.id.iv_audit_img);
			auditText = (TextView) view.findViewById(R.id.tv_audit_text);
			
			btnExit = (Button) view.findViewById(R.id.enterprise_btn_next_step);
			btnExit.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			if(R.id.enterprise_btn_next_step == v.getId()){
				exit();
			}
		}
		
		private void auditSuccess(){
			auditStatus.setImageResource(R.drawable.audit_success);
			auditStatus.setVisibility(View.VISIBLE);
			auditImg.setImageResource(R.drawable.audit_success_img);
			auditImg.setVisibility(View.VISIBLE);
			String str1 = getActivity().getResources().getString(R.string.audit_success_1);
			String str2 = getActivity().getResources().getString(R.string.audit_success_2);
			auditText.setText(str1+name+str2);
		}
		
		private void commitSuccess(){
			auditImg.setImageResource(R.drawable.enterprise_commit_succcess);
			auditImg.setVisibility(View.VISIBLE);
		}
		
		private void auditFaile(){
			auditStatus.setImageResource(R.drawable.audit_faile);
			auditStatus.setVisibility(View.VISIBLE);
			auditImg.setImageResource(R.drawable.audit_faile_img);
			auditImg.setVisibility(View.VISIBLE);
			String str = getActivity().getResources().getString(R.string.audit_faile);
			auditText.setText(str);
		}
		
		private String getName(){
			return getActivity().getIntent().getStringExtra(CommitFragment.EXTRA_ENTERPRISE_NAME);
		}
		
		public int getCode() {
			return getActivity().getIntent().getIntExtra(CommitFragment.EXTRA_REPONSE_CODE, -1);
		}
		
		private void exit(){
//			Intent intent = new Intent(getActivity(),MainActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			getActivity().finish();
//			startActivity(intent);
			MainActivity.launchClearTask(getActivity());
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	 back();
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }

	
	

	
}
