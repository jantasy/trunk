package cn.yjt.oa.app.nfctools.fragment;

public class FragmentFactory {
	
	public static NFCBaseFragment getPagerFragment(){
		return new NFCPagerFragment();
	}
	
	public static NFCBaseFragment createFragment(int position){
		NFCBaseFragment fragment = null;
		switch (position) {
		case 0:
			fragment = new NFCReadFragment();
			break;
		case 1:
			fragment = new NFCWriteFragment();
			break;
		case 2:
			fragment = new NFCOtherFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
	
	public static NFCBaseFragment getNFCCreateFragment(){
		return new NFCCreateFragment();
	}
	
	public static NFCBaseFragment getAddOperationFragment(){
		return new AddOperationFragment();
	}
	
	public static NFCBaseFragment getCreateOkFragment(){
		return new CreateOkFragment();
	}
}
