package cn.yjt.oa.app.nfctools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.nfctools.fragment.FragmentFactory;
import cn.yjt.oa.app.nfctools.fragment.NFCBaseFragment;

public class AddOperationActivity extends NFCBaseActivity{
	
	@Override
	protected NFCBaseFragment getFragment() {
		return FragmentFactory.getAddOperationFragment();
	}
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}
	
	public static void startForResult(Fragment fragment ,int requestCode){
		Intent intent = new Intent(fragment.getActivity(), AddOperationActivity.class);
		fragment.startActivityForResult(intent , requestCode);
	}
}
