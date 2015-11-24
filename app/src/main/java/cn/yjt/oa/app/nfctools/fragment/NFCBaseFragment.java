package cn.yjt.oa.app.nfctools.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**NFC基类的Fragment*/
public class NFCBaseFragment extends Fragment{

	protected void toActivity(Class<?> cls){
		Intent intent = new Intent(getActivity(),cls);
		startActivity(intent);
	}
}
