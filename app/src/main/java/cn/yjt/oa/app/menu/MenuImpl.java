package cn.yjt.oa.app.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class MenuImpl implements Menu {

	Context mContext;
	PackageManager mPm;
	Callback mCallback;
	
	SparseArray<MenuItemImpl> mItems = new SparseArray<MenuItemImpl>();
	ArrayList<SubMenuImpl> mSubMenus = new ArrayList<SubMenuImpl>();
	TreeSet<MenuItemImpl> mOrderedItems = new TreeSet<MenuItemImpl>(
		new Comparator<MenuItemImpl>() {

			@Override
			public int compare(MenuItemImpl lhs, MenuItemImpl rhs) {
				if (lhs.getOrder()==rhs.getOrder())
					return lhs.mAddPosition-rhs.mAddPosition;
				return lhs.getOrder()-rhs.getOrder();
			}
	});
	HashMap<Integer, MenuItemGroup> mGroups = 
			new HashMap<Integer, MenuItemGroup>();
	
	public interface Callback {
		void onOpenSubMenu(SubMenu subMenu);
		void onCloseMenu();
		boolean onMenuItemSelected(MenuItem menuItem);
	}
	
	class MenuItemGroup {
		ArrayList<MenuItemImpl> mChildItems = new ArrayList<MenuItemImpl>();
		boolean mCheckable;
		boolean mCheckExclusive;
		boolean mVisible = true;
		boolean mEnabled = true;
		
		public void add(MenuItemImpl item) {
			mChildItems.add(item);
			item.setCheckable(mCheckable);
			item.setCheckExclusive(mCheckExclusive);
			item.setVisible(mVisible);
			item.setEnabled(mEnabled);
		}
		
		public void setCheckable(boolean checkable, boolean exclusive) {
			mCheckable = checkable;
			mCheckExclusive = exclusive;
			for (MenuItemImpl item:mChildItems) {
				item.setCheckable(checkable);
				item.setCheckExclusive(exclusive);
			}
		}
		
		public void setVisible(boolean visible) {
			mVisible = visible;
			for (MenuItemImpl item:mChildItems) {
				item.setVisible(visible);
			}
		}

		public void setEnabled(boolean enabled) {
			mEnabled = enabled;
			for (MenuItemImpl item:mChildItems) {
				item.setEnabled(enabled);
			}
		}
		
		public void setChecked(MenuItemImpl item, boolean checked) {
			if (checked && mCheckExclusive) {
				for (MenuItemImpl child:mChildItems) {
					child.setCheckedInternal(child==item);
				}
			} else {
				item.setCheckedInternal(checked);
			}
		}
		
	}
	
	public MenuImpl(Context context) {
		mContext = context;
		mPm = context.getPackageManager();
	}
	
	private MenuItem addItem(MenuItemImpl item) {
		mItems.put(item.mItemId, item);
		item.mAddPosition = mOrderedItems.size();
		mOrderedItems.add(item);
		MenuItemGroup group = mGroups.get(item.mGroupId);
		if (group == null) {
			group = new MenuItemGroup();
			mGroups.put(item.mGroupId, group);
		}
		group.add(item);
		return item;
	}
	
	@Override
	public MenuItem add(CharSequence title) {
		MenuItemImpl item = new MenuItemImpl(mContext, this);
		item.setTitle(title);
		return addItem(item);
	}

	@Override
	public MenuItem add(int titleRes) {
		MenuItemImpl item = new MenuItemImpl(mContext, this);
		item.setTitle(titleRes);
		return addItem(item);
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		MenuItemImpl item = new MenuItemImpl(mContext, this);
		item.mGroupId = groupId;
		item.mItemId = itemId;
		item.mOrderId = order;
		item.setTitle(title);
		return addItem(item);
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		MenuItemImpl item = new MenuItemImpl(mContext, this);
		item.mGroupId = groupId;
		item.mItemId = itemId;
		item.mOrderId = order;
		item.setTitle(titleRes);
		return addItem(item);
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		return addSubMenu(0, 0, 0, title);
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
		return addSubMenu(mContext.getText(titleRes));
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order,
			CharSequence title) {
		MenuItemImpl item = new MenuItemImpl(mContext, this);
		item.mGroupId = groupId;
		item.mItemId = itemId;
		item.mOrderId = order;
		item.setTitle(title);
		SubMenuImpl subMenu = new SubMenuImpl(mContext, item);
		subMenu.setHeaderTitle(title);
		subMenu.setCallBack(mCallback);
		item.setSubMenu(subMenu);
		mSubMenus.add(subMenu);
		addItem(item);
		return subMenu;
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		return addSubMenu(groupId, itemId, order, mContext.getText(titleRes));
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order,
			ComponentName caller, Intent[] specifics, Intent intent, int flags,
			MenuItem[] outSpecificItems) {
		List<ResolveInfo> rs = mPm.queryIntentActivityOptions(caller, specifics, intent, flags);
		int count = 0;
		for (ResolveInfo r:rs) {
			ActivityInfo ai = r.activityInfo;
			if (ai != null) {
				Intent i = new Intent(intent);
				i.setClassName(ai.packageName, ai.name);
				add(groupId, itemId, order, ai.loadLabel(mPm))
					.setIcon(ai.loadIcon(mPm));
				++count;
			}
		}
		return count;
	}

	@Override
	public void removeItem(int id) {
		MenuItem item = mItems.get(id);
		if (item != null) {
			mItems.remove(id);
			if (item.hasSubMenu()) {
				mSubMenus.remove(item.getSubMenu());
			}
		}
	}

	@Override
	public void removeGroup(int groupId) {
		MenuItemGroup group =  mGroups.remove(groupId);
		if (group != null) {
			ArrayList<Integer> keysToRemove = new ArrayList<Integer>();
			for (int i=0, j=mItems.size(); i<j; ++i) {
				if (mItems.valueAt(i).getGroupId() == groupId) {
					int key = mItems.keyAt(i);
					keysToRemove.add(key);
				}
			}
			for (Integer key:keysToRemove) {
				mItems.remove(key);
			}
		}
	}

	@Override
	public void clear() {
		mGroups.clear();
		mItems.clear();
		mOrderedItems.clear();
		mSubMenus.clear();
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable,
			boolean exclusive) {
		MenuItemGroup menuGroup = mGroups.get(group);
		menuGroup.setCheckable(checkable, exclusive);
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
		MenuItemGroup menuGroup = mGroups.get(group);
		menuGroup.setVisible(visible);
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		MenuItemGroup menuGroup = mGroups.get(group);
		menuGroup.setEnabled(enabled);
	}

	@Override
	public boolean hasVisibleItems() {
		for (int i=0, j=mItems.size(); i<j; ++i) {
			if (mItems.valueAt(i).isVisible()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MenuItem findItem(int id) {
		MenuItem item =  mItems.get(id);
		if (item != null)
			return item;
		else {
			for (SubMenu subMenu:mSubMenus) {
				item = subMenu.findItem(id);
				if (item != null)
					return item;
			}
		}
		
		return null;
	}

	@Override
	public int size() {
		return mItems.size();
	}

	@Override
	public MenuItem getItem(int index) {
		return mItems.valueAt(index);
	}

	@Override
	public void close() {
		if (mCallback != null)
			mCallback.onCloseMenu();
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		return false;
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		MenuItemImpl item = mItems.get(id);
		if (item != null) {
			return item.performAction();
		}
		return false;
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
	}
	
	public void checkItem(MenuItemImpl item, boolean checked) {
		MenuItemGroup group = mGroups.get(item.mGroupId);
		if (group != null) {
			group.setChecked(item, checked);
		} else {
			item.setCheckedInternal(checked);
		}
	}
	
	public ArrayList<MenuItem> getOrderedVisibleMenuItems() {
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		for (MenuItemImpl item:mOrderedItems) {
			if (item.isVisible())
				items.add(item);
		}
		
		return items.size()>0?items:null;
	}

	public void setCallBack(Callback callback) {
		mCallback = callback;
		for (SubMenuImpl submenu : mSubMenus) {
			submenu.setCallBack(callback);
		}
	}
	
	boolean dispatchMenuItemSelected(MenuImpl menu, MenuItem item) {
        return mCallback != null && mCallback.onMenuItemSelected(item);
    }
	
	void dispatchSubMenuOpen(SubMenu subMenu) {
		if (mCallback != null)
			mCallback.onOpenSubMenu(subMenu);
	}
}
