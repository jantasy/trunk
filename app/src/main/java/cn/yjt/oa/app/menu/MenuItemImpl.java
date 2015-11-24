package cn.yjt.oa.app.menu;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import cn.yjt.oa.app.component.AlertDialogBuilder;

public class MenuItemImpl implements MenuItem {

	int mItemId;
	int mGroupId;
	int mOrderId;
	int mAddPosition;
	CharSequence mTitle;
	CharSequence mTitleCondensed;
	Drawable mIcon;
	Intent mIntent;
	boolean mCheckable;
	boolean mChecked;
	boolean mVisible;
	boolean mEnabled;
	boolean mCheckExclusive;
	boolean mNew;
	
	OnMenuItemClickListener mOnMenuItemClickListener;
	
	Resources mResource;
	Context mContext;
	MenuImpl mMenu;
	SubMenuImpl mSubMenu;
	
	public MenuItemImpl(Context context, MenuImpl menu) {
		mContext = context;
		mResource = context.getResources();
		mMenu = menu;
	}
	
	public boolean isNew() {
		return mNew;
	}

	public void setNew(boolean mNew) {
		this.mNew = mNew;
	}

	@Override
	public int getItemId() {
		return mItemId;
	}

	@Override
	public int getGroupId() {
		return mGroupId;
	}

	@Override
	public int getOrder() {
		return mOrderId;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		mTitle = title;
		return this;
	}

	@Override
	public MenuItem setTitle(int title) {
		mTitle = mResource.getText(title);
		return this;
	}

	@Override
	public CharSequence getTitle() {
		return mTitle;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		mTitleCondensed = title;
		return this;
	}

	@Override
	public CharSequence getTitleCondensed() {
		return mTitleCondensed;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		mIcon = icon;
		return this;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		if (iconRes != 0)
			mIcon = mResource.getDrawable(iconRes);
		return this;
	}

	@Override
	public Drawable getIcon() {
		return mIcon;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		mIntent = intent;
		return this;
	}

	@Override
	public Intent getIntent() {
		return mIntent;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		return this;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		return this;
	}

	@Override
	public char getNumericShortcut() {
		return 0;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		return this;
	}

	@Override
	public char getAlphabeticShortcut() {
		return 0;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		mCheckable = checkable;
		return this;
	}

	@Override
	public boolean isCheckable() {
		return mCheckable;
	}
	
	public MenuItem setCheckExclusive(boolean exclusive) {
		mCheckExclusive = exclusive;
		return this;
	}

	public boolean isCheckExclusive() {
		return mCheckExclusive;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		if (mChecked != checked) {
			mMenu.checkItem(this, checked);
		}
		
		return this;
	}
	
	MenuItem setCheckedInternal(boolean checked) {
		mChecked = checked;
		return this;
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		mVisible = visible;
		return this;
	}

	@Override
	public boolean isVisible() {
		return mVisible;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		mEnabled = enabled;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return mEnabled;
	}

	@Override
	public boolean hasSubMenu() {
		return mSubMenu != null;
	}

	@Override
	public SubMenu getSubMenu() {
		return mSubMenu;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(
			OnMenuItemClickListener menuItemClickListener) {
		mOnMenuItemClickListener = menuItemClickListener;
		return this;
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		return null;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
	}

	@Override
	public MenuItem setShowAsActionFlags(int actionEnum) {
		return null;
	}

	@Override
	public MenuItem setActionView(View view) {
		return this;
	}

	@Override
	public MenuItem setActionView(int resId) {
		return this;
	}

	@Override
	public View getActionView() {
		return null;
	}

	@Override
	public MenuItem setActionProvider(ActionProvider actionProvider) {
		return this;
	}

	@Override
	public ActionProvider getActionProvider() {
		return null;
	}

	@Override
	public boolean expandActionView() {
		return false;
	}

	@Override
	public boolean collapseActionView() {
		return false;
	}

	@Override
	public boolean isActionViewExpanded() {
		return false;
	}

	@Override
	public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
		return this;
	}
	
	public boolean performAction() {
		boolean result = false;
		if (mSubMenu != null) {
			result = true;
			AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(mContext);
			mSubMenu.setupDialogHeader(builder);
			ArrayList<CharSequence> itemTexts = new ArrayList<CharSequence>();
			final ArrayList<MenuItem> items = new ArrayList<MenuItem>();
			int selectedItem = 0;
			for (int i=0, j=mSubMenu.size(); i<j; ++i) {
				MenuItem item = mSubMenu.getItem(i);
				if (item.isVisible()) {
					itemTexts.add(item.getTitle());
					items.add(item);
					if (item.isChecked()) {
						selectedItem = items.size()-1;
					}
				}
				
			}
			CharSequence[] texts = new CharSequence[itemTexts.size()];
			builder.setSingleChoiceItems(itemTexts.toArray(texts), selectedItem, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MenuItem item = items.get(which);
					performMenuItemAction(item);
					dialog.dismiss();
				}
			});
			mSubMenu.mDialog = builder.show();
			mMenu.dispatchSubMenuOpen(mSubMenu);
		}
		if (!result && mOnMenuItemClickListener != null) {
			result = mOnMenuItemClickListener.onMenuItemClick(this);
		}
		if (!result && mIntent != null) {
			mContext.startActivity(mIntent);
			result = true;
		}
		if (!result) {
			result = mMenu.dispatchMenuItemSelected(mMenu, this);
		}
		
		if (result)
			mMenu.close();
		return result;
	}
	
	public void setSubMenu(SubMenu subMenu) {
		mSubMenu = (SubMenuImpl) subMenu;
	}

	public static boolean performMenuItemAction(MenuItem item) {
		if (item instanceof MenuItemImpl) {
			return ((MenuItemImpl) item).performAction();
		}
		return false;
	}
}
