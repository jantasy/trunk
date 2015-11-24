package cn.yjt.oa.app.attendance;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beacon.Beacon;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;
/**蓝牙考勤的界面*/
public class BeaconShakeAttendanceActivity extends BackTitleFragmentActivity
		implements OnClickListener, OnPageChangeListener {

	/**点一点*/
	private TextView mTvSHandAttendance;
	/**摇一摇*/
	private TextView mTvShakeAttendance;
	/**自动考勤*/
	private TextView mTvAutoAttendance;
	/**当前界面的viewpager*/
	private ViewPager mViewPager;
	
	/**自动考勤的Fragment*/
	private AutoBeaconSettingFragment mAutoAttendanceFragment;
	/**摇一摇的Fragment*/
	private BeaconShakeFragment mShakeAttendanceFragment;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {
			setContentView(R.layout.activity_beacon_shake_attendance);
			/*记录操作 0805*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_ATTENDANCE_BEACON);
			initView();
			fillData();
			setListener();
		}
	}

	/**初始化控件*/
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTvShakeAttendance = (TextView) findViewById(R.id.shake);
		mTvAutoAttendance = (TextView) findViewById(R.id.auto_attendance);
		mTvSHandAttendance = (TextView) findViewById(R.id.hand_attendance);
		mAutoAttendanceFragment = new AutoBeaconSettingFragment();
		mShakeAttendanceFragment = new BeaconShakeFragment();
	}
	/**填充数据*/
	private void fillData() {
		mViewPager.setAdapter(new BeaconPagerAdapter(getSupportFragmentManager()));
		mTvShakeAttendance.setSelected(true);
		mTvAutoAttendance.setSelected(false);
		mTvSHandAttendance.setSelected(false);
	}
	/**设置监听*/
	private void setListener() {
		mTvShakeAttendance.setOnClickListener(this);
		mTvAutoAttendance.setOnClickListener(this);
		mTvSHandAttendance.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
	}

	/**自定义的viewpager适配器，其实只有两个Fragment*/
	private class BeaconPagerAdapter extends FragmentPagerAdapter {

		public BeaconPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (i == 0) {
				return mShakeAttendanceFragment;
			} else if(i == 1){
				return mAutoAttendanceFragment;
			}else{
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		//点击摇一摇
		case R.id.shake:
			selectShake();

			/*记录操作 0806*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ATTENDANCE_SHAKE);
			break;
			
		//点击自动考勤
		case R.id.auto_attendance:
			selectAutoAttendance();


			break;
			
		//点击点一点
		case R.id.hand_attendance:

			/*记录操作 0807*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ATTENDANCE_CLICK);

			List<Beacon> beacons = mShakeAttendanceFragment.getBeacons();
			if(beacons == null || beacons.isEmpty()){
				Toast.makeText(getApplicationContext(), "您的周围没有蓝牙标签", Toast.LENGTH_SHORT).show();
				return;
			}
			ArrayList<String> uumms = new ArrayList<String>();
			for (Beacon beacon : beacons) {
				uumms.add(beacon.getUumm());
			}
			BeaconAttendanceActivity.launch(BeaconShakeAttendanceActivity.this,uumms);
			break;
		default:
			break;
		}
	}

	private void selectShake() {
		select(0);
	}

	private void selectAutoAttendance() {
		select(1);
	}

	private void select(int i) {
		mViewPager.setCurrentItem(i);
		selectTab(i);
	}

	private void selectTab(int i) {
		if (i == 0) {
			mTvShakeAttendance.setSelected(true);
			mTvAutoAttendance.setSelected(false);
		} else {
			mTvShakeAttendance.setSelected(false);
			mTvAutoAttendance.setSelected(true);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int i) {
		selectTab(i);
	}
}
