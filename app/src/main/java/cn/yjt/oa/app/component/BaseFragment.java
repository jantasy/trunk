package cn.yjt.oa.app.component;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	protected void goBack() {
		getActivity().finish();
	}
}
