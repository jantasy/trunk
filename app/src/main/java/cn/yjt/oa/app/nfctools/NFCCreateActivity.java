package cn.yjt.oa.app.nfctools;

import cn.yjt.oa.app.nfctools.fragment.FragmentFactory;
import cn.yjt.oa.app.nfctools.fragment.NFCBaseFragment;

public class NFCCreateActivity extends NFCBaseActivity{
	
	@Override
	protected NFCBaseFragment getFragment() {
		return FragmentFactory.getNFCCreateFragment();
	}
}
