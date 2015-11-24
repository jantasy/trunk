package cn.yjt.oa.app.app.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.adapter.AppManagerAdapter;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenu;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenuCreator;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenuItem;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenuListView;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenuListView.OnMenuItemClickListener;
import cn.yjt.oa.app.app.widget.swipe.SwipeMenuListView.OnSwipeListener;

public class AppManagerFragment extends BaseFragment {
	private AppManagerAdapter adapter;
	private SwipeMenuListView appListView;
	SwipeMenuCreator menuCreator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			// create "open" item
			SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
			// set item background
			openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
			// set item width
			openItem.setWidth(dp2px(90));
			// set item title
			openItem.setTitle("Open");
			// set item title fontsize
			openItem.setTitleSize(18);
			// set item title font color
			openItem.setTitleColor(Color.WHITE);
			// add to menu
			menu.addMenuItem(openItem);

			// create "delete" item
			SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
			// set item background
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
			// set item width
			deleteItem.setWidth(dp2px(90));
			// set a icon
			deleteItem.setIcon(R.drawable.app_details_cancel_btn_bg);
			// add to menu
			menu.addMenuItem(deleteItem);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_app_manager, container, false);
		adapter = new AppManagerAdapter(getActivity());
		appListView = (SwipeMenuListView) view.findViewById(R.id.app_manager_list);
		appListView.setMenuCreator(menuCreator);
		appListView.setAdapter(adapter);
		appListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				
			}
		});
		appListView.setOnSwipeListener(new OnSwipeListener() {
			
			@Override
			public void onSwipeStart(int position) {
				
			}
			
			@Override
			public void onSwipeEnd(int position) {
				
			}
		});
		return view;
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
