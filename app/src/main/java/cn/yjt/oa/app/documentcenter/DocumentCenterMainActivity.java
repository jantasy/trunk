package cn.yjt.oa.app.documentcenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.ViewUtil;

public class DocumentCenterMainActivity extends TitleFragmentActivity {

	private static final int REQUEST_CODE = 1001;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {
			initView();
		}
	}

	/**
	 * 初始化页面展示
	 */
	private void initView() {
		setContentView(R.layout.document_center_main_layout);
		// 初始化顶部菜单
		initTitleBar();
		//
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment fragment = new DocumentCenterMainFragment();
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
		// 弹出选择框让用户选择上传的文件
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		
		Intent wrapperIntent = Intent.createChooser(intent, null);
		startActivityForResult(wrapperIntent, REQUEST_CODE);  
		
//		startActivityForResult(intent, REQUEST_CODE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i("===resultCode = " + resultCode);
		LogUtils.i("===requestCode = " + requestCode);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			// TODO:跳到上传界面
			Intent intent = new Intent(this, DocumentCenterAddActivity.class);
			if (data != null) {
				intent.setData(data.getData());
				Uri uri = data.getData();
				LogUtils.i("===uri = " + uri);
			}
			startActivity(intent);
		}
	}
	
}
