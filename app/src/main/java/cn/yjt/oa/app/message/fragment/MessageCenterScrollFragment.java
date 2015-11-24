package cn.yjt.oa.app.message.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.message.MessageOnResume;
import cn.yjt.oa.app.task.TaskBaseFragment;
import cn.yjt.oa.app.task.TaskPublishingActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingInterruptor;

public class MessageCenterScrollFragment extends BaseFragment implements
		OnClickListener, SlidingInterruptor {
	private ViewPager pager;
	public final MessageFragment[] fragments = new MessageFragment[] {
			new MessageFragment("all"), new MessageFragment("task"),
			new MessageFragment("notice") };
	private MessageCenterScrollPagerAdapter adapter;
	private TextView allView;
	private TextView taskView;
	private TextView noticeView;
	View rootView;

	private MessageOnResume onResume;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public MessageCenterScrollFragment() {
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			if(onResume!=null){
				onResume.messageOnResume();
			}
		}else{
			if(onResume!=null){
				onResume.messageOnPause();
			}
		}
	}
	
	public void setMessageOnResume(MessageOnResume onResume){
		this.onResume = onResume;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity activity = (MainActivity) getActivity();
		activity.addSlidingMenuInterruptor(this);
		activity.addToFragments(0, this);
		if(rootView == null){
			rootView = inflater.inflate(R.layout.message_center_scroll_layout, null);
			allView = (TextView) rootView.findViewById(R.id.all);
			taskView = (TextView) rootView.findViewById(R.id.task);
			noticeView = (TextView) rootView.findViewById(R.id.notice);
			adapter = new MessageCenterScrollPagerAdapter(getActivity().getSupportFragmentManager());
			showSelect(true, false, false);
			allView.setOnClickListener(this);
			taskView.setOnClickListener(this);
			noticeView.setOnClickListener(this);
			pager = (ViewPager) rootView.findViewById(R.id.view_pager);
			pager.setAdapter(adapter);
			pager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					fragments[arg0].refreshData();
					if (arg0 == 0) {
						showSelect(true, false, false);
						// allfragment.refrashUi();
					} else if (arg0 == 1) {
						showSelect(false, true, false);
						// taskfragment.refrashUi();
					} else {
						showSelect(false, false, true);
						// noticefragment.refrashUi();
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
			refreshCurrentPage();
		}
		return rootView;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		 if (TaskBaseFragment.REQUEST_CODE_PUBLISHING_NEW_TASK == requestCode) {
			 if(intent!=null){
				 boolean taskAdded = intent.getBooleanExtra(TaskPublishingActivity.TASK_NEW_ADDED_KEY, false);
	             if (taskAdded) {
	            	 if(pager.getCurrentItem()<2){
	            		 fragments[pager.getCurrentItem()].refreshData();
	            	 }
	            	
	             }
			 }
            
         }
	}

	private void refreshCurrentPage() {
		int currentItem = pager.getCurrentItem();
		if(currentItem == 0){
			pager.setCurrentItem(1);
		}else{
			pager.setCurrentItem(0);
		}
		pager.setCurrentItem(currentItem);
	}
	
	@Override
	public void onFragmentSelected() {
		if(pager != null){
			fragments[pager.getCurrentItem()].refreshData();
		}
	}

	private void showSelect(boolean showAll, boolean showTask,
			boolean showNotice) {
		allView.setSelected(showAll);
		taskView.setSelected(showTask);
		noticeView.setSelected(showNotice);
	}

	class MessageCenterScrollPagerAdapter extends FragmentStatePagerAdapter {

		public MessageCenterScrollPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments[arg0];
		}

		@Override
		public int getCount() {
			return fragments.length;
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
			Object instantiateItem = super.instantiateItem(arg0, arg1);
			fragments[arg1] = (MessageFragment) instantiateItem;
			return instantiateItem;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}

	}

	public interface ChangeTopDataListener {
		void addData(MessageInfo info);

		void deleteDate(MessageInfo info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all:
			pager.setCurrentItem(0);
			showSelect(true, false, false);

             /*记录操作 0405*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MESSAGE_CENTER_ALL);
			break;
		case R.id.task:
			pager.setCurrentItem(1);
			showSelect(false, true, false);

             /*记录操作 0406*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MESSAGE_CENTER_TASK);
			break;
		case R.id.notice:
			pager.setCurrentItem(2);
			showSelect(false, false, true);

            /*记录操作 0407*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MESSAGE_CENTER_NOTICE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}
	}

	@Override
	public CharSequence getPageTitle(Context context) {
		return "消息中心";
	}

	@Override
	public boolean onRightButtonClick() {
//		CaptureActivity.launchWithCallback(getActivity(), (QRScanCallback) getActivity().getApplication());
		TaskPublishingActivity.launch(this);
		return true;
	}

	@Override
	public void configRightButton(ImageView imgView) {
//		imgView.setVisibility(View.GONE);
		imgView.setVisibility(View.VISIBLE);
		imgView.setImageResource(R.drawable.left_menu_feedback);
	}

	@Override
	public boolean getUserVisibleHint(MotionEvent ev) {
		if(pager == null){
			return false;
		}
		boolean userVisibleHint = getUserVisibleHint();
		return userVisibleHint;
	}

	@Override
	public boolean allowTouchInterrupted(MotionEvent ev) {
		int currentItem = pager.getCurrentItem();
		if(currentItem == 0){
			return true;
		}else{
			return false;
		}
	}

	// public MessageFragment[] getMessageFragments(){
	// return fragments;
	// }
	//
	// public ArrayList<String> getTopIds(){
	// return topIds;
	// }
}
