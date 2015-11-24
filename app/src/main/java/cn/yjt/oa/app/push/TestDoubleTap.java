package cn.yjt.oa.app.push;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;

public class TestDoubleTap extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consume);
		getSupportFragmentManager().beginTransaction().add(R.id.label_panel, new TestDoubleTapFragment()).commit();
	}
	
	class TestDoubleTapFragment extends DoubleTapFragment{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView textView = new TextView(getActivity());
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			textView.setText("protected void onCreate(Bundle savedInstanceState)");
			listenDoubleTapView(textView);
			return textView;
		}
		@Override
		protected void onDoubleTap(View view) {
			Toast.makeText(getApplicationContext(), "onDoubleTap", Toast.LENGTH_SHORT).show();
		}
	}
}
