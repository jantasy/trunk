package cn.yjt.oa.app.component;

import io.luobo.common.Cancelable;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.AutoCancelable;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.menu.MenuImpl;
import cn.yjt.oa.app.widget.LightOnOffFrameLayout;
import cn.yjt.oa.app.widget.MenuView;

import com.umeng.analytics.MobclickAgent;

/**
 * 带有自定义头标题的Activity
 *
 */
public class TitleFragmentActivity extends UmengBaseActivity implements AutoCancelable {

	/**整个标题控件*/
	private View titleBar;
	//整个标题控件，相比较titleBar少一个最外层的FrameLayout布局
	private View titlePanel;

	private TextView titleTextView;
	private ImageView titleLeftButtonView;
	private ImageView titleRightButtonView;
	private ProgressBar titleProgressBar;

	private LightOnOffFrameLayout lightOnOffView;
	private MenuView menuView;

	private boolean isMenuOpened;

	private MenuImpl menu;

	private Animation slideInAnimation;
	private Animation slideOutAnimation;

	private List<Cancelable> autoCancelables = new ArrayList<Cancelable>();

	private static final long LIGHT_DURATION = 300;

	/**
	 * 点击事件，用来监听一些内部控件
	 * 如标题左按钮，标题右按钮之类的点击监听，然后向外暴露监听后执行的方法即可
	 */
	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == titleLeftButtonView) {
				onLeftButtonClick();
			} else if (v == titleRightButtonView) {
				onRightButtonClick();
			} else if (v == lightOnOffView) {
				if (isMenuOpened) {
					closeOptionsMenu();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		super.setContentView(R.layout.activity_titled);

		//初始化控件
		titleBar = findViewById(R.id.titlebar);
		titlePanel = findViewById(R.id.title_panel);
		titleTextView = (TextView) findViewById(R.id.title_text);
		titleLeftButtonView = (ImageView) findViewById(R.id.left_button);
		titleRightButtonView = (ImageView) findViewById(R.id.right_button);
		titleProgressBar = (ProgressBar) findViewById(R.id.pb_updating);

		lightOnOffView = (LightOnOffFrameLayout) findViewById(R.id.content);
		menuView = (MenuView) findViewById(R.id.menu);

		//
		slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
		slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_top);

		titleLeftButtonView.setOnClickListener(onClickListener);
		titleRightButtonView.setOnClickListener(onClickListener);
		lightOnOffView.setOnClickListener(onClickListener);
		boolean checkNetwork = AsyncRequest.checkNetwork(this);
		if (!checkNetwork) {
			AlertDialogBuilder.newBuilder(this).setMessage(R.string.connect_network_fail).setTitle("提示")
					.setPositiveButton("确定", null).show();
		}
	}

	@Override
	public void finish() {
		//友盟统计当前页面关闭的次数
		MobclickAgent.onEvent(this, "common_back");
		super.finish();
	}

	/**
	 * 设置titleBar的显示状态
	 * @param visibility View.GONE,View.INVISIBLE,View.VISIBLE
	 */
	public void setTitleBarVisibility(int visibility) {
		titleBar.setVisibility(visibility);
	}

	/**
	 * 判断titlebar是否是可见的
	 * @return true可见，false不可见
	 */
	public boolean isTitleBarShow() {
		return titleBar.getVisibility() == View.VISIBLE;
	}

	/**
	 * 展示ProcessBar，隐藏标题右边的按钮
	 */
	public void showProgressBar() {
		titleProgressBar.post(new Runnable() {

			@Override
			public void run() {
				titleProgressBar.setVisibility(View.VISIBLE);
				titleRightButtonView.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 隐藏ProcessBar，显示右边的按钮
	 */
	public void dismissProgressBar() {
		titleProgressBar.post(new Runnable() {

			@Override
			public void run() {
				titleProgressBar.setVisibility(View.GONE);
				titleRightButtonView.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void setContentView(int layoutResID) {
		getLayoutInflater().inflate(layoutResID, lightOnOffView);
	}

	@Override
	public void setContentView(View view) {
		lightOnOffView.addView(view);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		lightOnOffView.addView(view, params);
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		titleTextView.setText(title);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggleOptionsMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void openOptionsMenu() {
		if (isMenuOpened)
			return;
		if (menu == null) {
			menu = new MenuImpl(this);
			menu.setCallBack(new MenuImpl.Callback() {

				@Override
				public void onOpenSubMenu(SubMenu subMenu) {
					//					mOpenedSubMenu = subMenu;
				}

				@Override
				public boolean onMenuItemSelected(MenuItem menuItem) {
					return onOptionsItemSelected(menuItem);
				}

				@Override
				public void onCloseMenu() {
					closeOptionsMenu();
				}
			});
			if (!onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu)) {
				//打开menu失败
				menu = null;
				return;
			}
		}

		if (!onPrepareOptionsMenu(menu))
			return;

		if (!menu.hasVisibleItems())
			return;

		menuView.setMenu(menu);
		menuView.setVisibility(View.VISIBLE);
		menuView.startAnimation(slideInAnimation);
		lightOff();

		isMenuOpened = true;
	}

	public void lightOff() {
		lightOnOffView.lightOff(LIGHT_DURATION);
	}

	public void lightOn() {
		lightOnOffView.lightOn(LIGHT_DURATION);
	}

	@Override
	public void closeOptionsMenu() {
		if (!isMenuOpened)
			return;

		menuView.setVisibility(View.GONE);
		menuView.startAnimation(slideOutAnimation);
		lightOn();
		isMenuOpened = false;
	}

	public boolean isMenuOpened() {
		return isMenuOpened;
	}

	@Override
	public void onBackPressed() {
		if (isMenuOpened) {
			closeOptionsMenu();
		} else {
			hideSoftInput();
			try {
				MobclickAgent.onEvent(this, "common_back");
				super.onBackPressed();
			} catch (Throwable t) {
				//java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
			}
		}
	}

	public void toggleOptionsMenu() {
		if (isMenuOpened) {
			closeOptionsMenu();
		} else {
			openOptionsMenu();
		}
	}

	/**
	 * 标题左按钮被点击(后退按钮)
	 */
	public void onLeftButtonClick() {
		//友盟统计当前页面返回的次数
		MobclickAgent.onEvent(this, "common_navigation_back");
	}

	/**
	 * 标题右按钮被点击
	 */
	public void onRightButtonClick() {
		toggleOptionsMenu();
	}

	/**
	 * 获取标题左按钮的对象（向外暴露，标题左按钮，用于继承者使用）
	 * @return 标题左按钮的ImageView对象
	 */
	public ImageView getLeftbutton() {
		return titleLeftButtonView;
	}

	/**
	 * 获取标题右按钮的对象（向外暴露，标题左按钮，用于继承者使用）
	 * @return 标题右按钮的ImageView对象
	 */
	public ImageView getRightButton() {
		return titleRightButtonView;
	}

	/**
	 * 获取标题文字（向外暴露，标题左文字，用于继承者使用）
	 * @return 标题文字TextView对象
	 */
	public TextView getTextTitle() {
		return titleTextView;
	}

	/**
	 * 设置标题的背景
	 * @param background
	 */
	@SuppressWarnings("deprecation")
	public void setTitleBackground(Drawable background) {
		titlePanel.setBackgroundDrawable(background);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		for (Cancelable cancelable : autoCancelables) {
			try {
				cancelable.cancel();
			} catch (Throwable t) {
			}
		}
		autoCancelables.clear();
		super.onDestroy();
	}

	@Override
	public void addCancelable(Cancelable cancelable) {
		if (cancelable != null) {
			autoCancelables.add(cancelable);
		}
	}

	@Override
	public void removeCancelable(Cancelable cancelable) {
		if (cancelable != null) {
			autoCancelables.remove(cancelable);
		}
	}

	//20150331 zhengran
	public void setTitleOnClickListener(OnClickListener listener) {
		titleTextView.setOnClickListener(listener);
	}
}
