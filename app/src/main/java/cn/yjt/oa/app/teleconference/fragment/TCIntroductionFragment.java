package cn.yjt.oa.app.teleconference.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class TCIntroductionFragment extends TCBaseFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_tc_introduction, container,false);
		TextView textView = (TextView) root.findViewById(R.id.introduction_click);
		textView.setText(Html.fromHtml("点击<a href='http://ecp.189.cn/wap/ecpjs.html'> 查看资费标准 </a>（重要）"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		Button button = (Button) root.findViewById(R.id.introduction_ok);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentBrige brige = (FragmentBrige) getActivity();
				brige.toFragment(new TCActivateFragment());
			}
		});
		return root;
	}

	@Override
	public String getTitle() {
		return "资费标准";
	}

}
