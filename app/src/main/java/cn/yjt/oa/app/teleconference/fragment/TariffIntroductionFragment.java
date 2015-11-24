package cn.yjt.oa.app.teleconference.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.yjt.oa.app.R;

public class TariffIntroductionFragment extends TCBaseFragment implements OnClickListener {
	private View btnLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_tariff, container,false);
		btnLayout = root.findViewById(R.id.btn_layout);
		root.findViewById(R.id.btn_agree).setOnClickListener(this);
		root.findViewById(R.id.btn_disagree).setOnClickListener(this);
		
		WebView tariffWeb = (WebView) root.findViewById(R.id.web_tariff);
		tariffWeb.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				btnLayout.setVisibility(View.VISIBLE);
			}
		});
		tariffWeb.loadUrl("file:///android_asset/tariff.html");
		return root;
	}

	@Override
	public String getTitle() {
		return "资费说明";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_agree:
			FragmentBrige brige = (FragmentBrige) getActivity();
			brige.toFragment(new TCIntroductionFragment());
			break;
		case R.id.btn_disagree:
			getActivity().finish();
			break;
		default:
			break;
		}
	}
}
