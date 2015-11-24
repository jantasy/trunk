package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.menu.MenuItemImpl;

public class MenuView extends SimpleTableView implements View.OnClickListener {

	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MenuView(Context context) {
		super(context);
	}
	
	public interface MenuAdapter {
		View getView(MenuItem menuItem, View convertView);
	}
	
	private Menu menu;
	private MenuAdapter menuAdapter = new MenuAdapter() {
		
		@Override
		public View getView(MenuItem menuItem, View convertView) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menuview_default_item, null);
			}
			
			TextView textview = (TextView) convertView.findViewById(R.id.textview);
			textview.setText(menuItem.getTitle());
			textview.setSelected(menuItem.isChecked());
			
			View iconview = convertView.findViewById(R.id.iconview);
			iconview.setSelected(menuItem.isChecked());
			return convertView;
		}
	};
	
	public void setMenuAdapter(MenuAdapter adapter) {
		menuAdapter = adapter;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
		setupChildren();
	}
	
	private void setupChildren() {
		int menuSize = menu.size();
		int childCount = getChildCount();
		for (int i=0; i<Math.min(menuSize, childCount); ++i) {
			MenuItem menuItem = menu.getItem(i);
			menuAdapter.getView(menuItem, getChildAt(i));
		}
		
		if (menuSize > childCount) {
			for (int i=childCount; i<menuSize; ++i) {
				MenuItem menuItem = menu.getItem(i);
				View v = menuAdapter.getView(menuItem, null);
				addView(v);
				v.setOnClickListener(this);
			}
		} else if (menuSize < childCount) {
			removeViews(menuSize, childCount-menuSize);
		}
	}

	@Override
	public void onClick(View v) {
		int index = indexOfChild(v);
		MenuItem menuItem = menu.getItem(index);
		MenuItemImpl.performMenuItemAction(menuItem);
	}
}
