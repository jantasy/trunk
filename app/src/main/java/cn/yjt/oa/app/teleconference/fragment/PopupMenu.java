package cn.yjt.oa.app.teleconference.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class PopupMenu {
	private static String[] itemNames = new String[]{"手动输入","单位通讯录","本机通讯录"};
	private static Integer[] iconIds = new Integer[]{R.drawable.tc_hand_written,R.drawable.tc_yjt_contacts,R.drawable.tc_local_contacts};
	private List<String> items;
	private List<Integer> icons;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	
	@SuppressWarnings("deprecation")
	public PopupMenu(Context context){
		this.context = context;
		items = new ArrayList<String>();
		icons = new ArrayList<Integer>();
		initData();
		View view = LayoutInflater.from(context).inflate(R.layout.popumenu, null);
		listView = (ListView) view.findViewById(R.id.popupmenu_list);
		listView.setAdapter(new PopupMenuAdapter());
		popupWindow = new PopupWindow(view,300,LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
	}
	
	// 设置菜单项点击监听器
	public void setOnItemClickListener(OnItemClickListener listener){
		listView.setOnItemClickListener(listener);
	}
	
	// 批量添加菜单项
	public void addItems(String[] items){
		for(String item:items){
			this.items.add(item);
		}
	}
	public void addIcons(Integer[] icons){
		for(int icon:icons){
			this.icons.add(icon);
		}
	}
	public void initData(){
		addItems(itemNames);
		addIcons(iconIds);
	}
	
	// 下拉式弹出pop菜单 parent右下角
	public void showAsDropDown(View parent){
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		float margin = context.getResources().getDimension(R.dimen.popmenu_y_margin_right);
		int width = DensityUtil.getScreenWidth((Activity)context);
		int x = width-(int)margin+33-300;
		int y = location[1]+parent.getHeight();
		popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY,x,y);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}
	
	// 隐藏菜单
	public void dismiss(){
		popupWindow.dismiss();
	}
	
	public class PopupMenuAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = new TextView(context);
			}
			TextView menu = ((TextView)convertView);
			menu.setPadding(0, 20, 0, 20);
			menu.setText(items.get(position));
			menu.setTextColor(Color.WHITE);
			Drawable icon = context.getResources().getDrawable(icons.get(position));
			icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
			menu.setCompoundDrawables(icon, null, null, null);
			return convertView;
		}
	}
	
}
