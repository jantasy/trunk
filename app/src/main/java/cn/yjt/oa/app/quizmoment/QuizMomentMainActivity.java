package cn.yjt.oa.app.quizmoment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.picturepicker.DefaultPicturePicker;
import cn.yjt.oa.app.picturepicker.PicturePicker;

public class QuizMomentMainActivity extends TitleFragmentActivity {


	private QuizMomentMainFragment fragment;
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		initView();
	}

	/**
	 * 初始化页面展示
	 */
	private void initView() {
		setContentView(R.layout.quiz_moment_main_layout);
		// 初始化顶部菜单
		initTitleBar();
		//
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		fragment = new QuizMomentMainFragment();
		transaction.add(R.id.quiz_moment_fragment, fragment, "quizmoment");
		transaction.show(fragment);
		transaction.commit();
	}
	private void initTitleBar() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_add_group);

	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
		finish();
	}


	@Override
	public void onRightButtonClick() {
		picturePicker = new DefaultPicturePicker();
		picturePicker.pickPicture(this);
	}
	private PicturePicker picturePicker;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(picturePicker!=null&&picturePicker.isPickerResult(requestCode)){
			Uri uri = picturePicker.getPicture(requestCode, resultCode, data);
			QuizMomentAddActivity.launch(this, uri);
		}
	}

}
