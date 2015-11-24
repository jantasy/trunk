package cn.yjt.oa.app.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class SubMenuImpl extends MenuImpl implements SubMenu {

	CharSequence mHeaderTitle;
	Drawable mHeaderIcon;
	View mHeaderView;
	Dialog mDialog;
	
	final Context mContext;
	final MenuItem mItem;
	
	public SubMenuImpl(Context context, MenuItem item) {
		super(context);
		mContext = context;
		mItem = item;
	}

	@Override
	public SubMenu setHeaderTitle(int titleRes) {
		return setHeaderTitle(mContext.getText(titleRes));
	}

	@Override
	public SubMenu setHeaderTitle(CharSequence title) {
		mHeaderTitle = title;
		return this;
	}

	@Override
	public SubMenu setHeaderIcon(int iconRes) {
		return setHeaderIcon(mContext.getResources().getDrawable(iconRes));
	}

	@Override
	public SubMenu setHeaderIcon(Drawable icon) {
		mHeaderIcon = icon;
		return this;
	}

	@Override
	public SubMenu setHeaderView(View view) {
		mHeaderView = view;
		return this;
	}

	@Override
	public void clearHeader() {
		mHeaderTitle = null;
		mHeaderIcon = null;
		mHeaderView = null;
	}

	@Override
	public SubMenu setIcon(int iconRes) {
		mItem.setIcon(iconRes);
		return this;
	}

	@Override
	public SubMenu setIcon(Drawable icon) {
		mItem.setIcon(icon);
		return this;
	}

	@Override
	public MenuItem getItem() {
		return mItem;
	}

	@Override
	public void close() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	public void setupDialogHeader(AlertDialog.Builder builder) {
		if (mHeaderView != null) {
			builder.setCustomTitle(mHeaderView);
		} else {
			if (mHeaderTitle != null) {
				builder.setTitle(mHeaderTitle);
			}
			if (mHeaderIcon != null) {
				builder.setIcon(mHeaderIcon);
			}
		}
		
	}
}
