package cn.yjt.oa.app.contactlist.adpter;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.utils.MD5Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.ContactlistFragment;
import cn.yjt.oa.app.contactlist.GroupDetailActivity;
import cn.yjt.oa.app.contactlist.data.ContactlistGroupInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.BitmapUtils;

public class GroupAdapter extends BaseExpandableListAdapter {
	
	private Context mContext;
	private boolean isAttached;
	private int model;
	private OnClickListener clickListener;
	private List<ContactlistGroupInfo> groupInfos = Collections.emptyList();
	private List<GroupInfo> checkedGroupList = new ArrayList<GroupInfo>();

	public GroupAdapter(Context context, boolean isAttached, int model,OnClickListener clickListener) {
		this.mContext = context;
		this.isAttached = isAttached;
		this.model = model;
		this.clickListener=clickListener;
		
	}


	@Override
	public int getGroupCount() {
		return groupInfos.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupInfos.get(groupPosition).getUsers().length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupInfos.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupInfos.get(groupPosition).getUsers()[childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	



	public void setGroupInfos(List<ContactlistGroupInfo> groupInfos) {
		this.groupInfos = groupInfos;
	}


	public List<GroupInfo> getCheckedGroupList() {
		return checkedGroupList;
	}




	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		 GroupHolder groupHolder =null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contactlist_group_group_item, null);
			groupHolder=new GroupHolder();
			convertView.setTag(groupHolder);
		}
		groupHolder=(GroupHolder) convertView.getTag();
		final ContactlistGroupInfo info = groupInfos.get(groupPosition);

		groupHolder.groupName = (TextView) convertView
				.findViewById(R.id.group_item_group_name);
		groupHolder.groupName.setText(info.getInfo().getName());
		groupHolder.childCount = (TextView) convertView
				.findViewById(R.id.group_item_child_count);
		String childCountStr = mContext.getResources().getString(
				R.string.contactlist_group_members, info.getUsers().length);
		groupHolder.childCount.setText(childCountStr);

		// 点击右侧的向下的箭头展开expandableListview
		// ImageView group_item_expandable_show = (ImageView) convertView
		// .findViewById(R.id.group_item_expandable_show);
		groupHolder.view = convertView
				.findViewById(R.id.group_item_expandable_show_parent);
		groupHolder.checkBox = (CheckBox) convertView
				.findViewById(R.id.group_item_check);
		if (!isMulitChoice()) {
			groupHolder.view.setOnClickListener(clickListener);
			groupHolder.view.setTag(groupPosition);
			groupHolder.view.setVisibility(View.VISIBLE);
			groupHolder.checkBox.setVisibility(View.GONE);
		} else {
			groupHolder.checkBox.setVisibility(View.VISIBLE);
			groupHolder.view.setVisibility(View.GONE);
			groupHolder.checkBox.setChecked(info.isChecked());
			groupHolder.checkBox.setTag(info);
			groupHolder.checkBox.setOnCheckedChangeListener(changeListener);
		}
		final CheckBox checkBoxTemp = groupHolder.checkBox;
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMulitChoice()) {
					checkBoxTemp.toggle();
				} else {
					intentToGroupDetailActivity(info);
				}
			}
		});
		groupHolder.groupIcon = (ImageView) convertView.findViewById(R.id.group_icon);
		final ImageView IconTemp = groupHolder.groupIcon;
//		final Bitmap defaultHeadTemp=groupHolder.defaultHead;
		// loadImage(info.getInfo().getAvatar(), icon,
		// R.drawable.contact_group_icon_default);
		IconTemp.setImageResource(R.drawable.contact_group_icon_default);
		IconTemp.setTag(info.getInfo().getAvatar());
		MainApplication.getHeadImageLoader().get(info.getInfo().getAvatar(),
				new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						if (container.getUrl().equals(IconTemp.getTag())) {
							IconTemp.setImageBitmap(container.getBitmap());
						}
					}

					@Override
					public void onError(ImageContainer container) {
						IconTemp.setImageResource(R.drawable.contact_group_icon_default);
					}
				});
		return convertView;
	}


	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contactlist_group_child_item, null);
			holder = new Holder();
			convertView.setTag(holder);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.child_item_icon);
			holder.name = (TextView) convertView.findViewById(R.id.child_name);
		}
		holder = (Holder) convertView.getTag();
		ContactInfo info = groupInfos.get(groupPosition).getUsers()[childPosition];
		loadImage(info.getAvatar(), holder.icon,
				R.drawable.contactlist_contact_icon_default);
		holder.name.setText(info.getName());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			ContactlistGroupInfo info = (ContactlistGroupInfo) buttonView
					.getTag();
			addChekedData(info, isChecked);
		}
	};

	private class Holder {
		ImageView icon, phone, msg, infos;
		TextView name;
	}
	private class GroupHolder {
		private ImageView groupIcon;
		private TextView groupName,childCount;
		private View view;
		private CheckBox checkBox;
//		private Bitmap defaultHead;
//		public GroupHolder(){
//			Bitmap defaultBm = BitmapFactory.decodeResource(
//					mContext.getResources(), R.drawable.contact_group_icon_default);
//			defaultHead = BitmapUtils.getPersonalHeaderIcon(mContext, defaultBm);
//		}
	}

	
	private void intentToGroupDetailActivity(ContactlistGroupInfo info) {
		ContactlistFragment.isIntentToGroupDetail = true;
		Intent intent = new Intent();
		if (info != null) {
			intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, info.getInfo()
					.getId());
		}
		intent.setClass(mContext, GroupDetailActivity.class);
		((Activity)mContext).startActivityForResult(intent, ContactlistFragment.UPDATE_GROUP_NEED_REFRESH);
	}

	public boolean isMulitChoice() {
		return model == ContactlistFragment.CONTACTLIST_STRAT_MODEL_MULITCHOICE;
	}

	private void addChekedData(ContactlistGroupInfo info, boolean checked) {
		GroupInfo groupInfo = info.getInfo();
		if (checked && !checkedGroupList.contains(groupInfo)) {
			List<UserSimpleInfo> userSimpleInfoFromGroup = getUserSimpleInfoFromGroup(
					info, groupInfo);
			groupInfo
					.setUsers(userSimpleInfoFromGroup
							.toArray(new UserSimpleInfo[userSimpleInfoFromGroup
									.size()]));
			checkedGroupList.add(groupInfo);
		} else if (!checked && checkedGroupList.contains(groupInfo)) {
			checkedGroupList.remove(groupInfo);
		}
	}

	private List<UserSimpleInfo> getUserSimpleInfoFromGroup(
			ContactlistGroupInfo info, GroupInfo group) {
		List<UserSimpleInfo> list = new ArrayList<UserSimpleInfo>();
		ContactInfo[] infos = info.getUsers();
		UserSimpleInfo[] users = group.getUsers();
		if (infos.length == users.length) {
			for (int i = 0; i < infos.length; i++) {
				UserSimpleInfo user = new UserSimpleInfo();
				user.setId(infos[i].getUserId());
				user.setName(infos[i].getName());
				user.setIcon(infos[i].getAvatar());
				list.add(user);
			}
		}
		return list;
	}

	private void loadImage(String url, final ImageView imageView,
			final int dfResId) {
		// setDefaultImage(imageView, dfResId);
		imageView.setImageResource(dfResId);
		if (!isAttached || TextUtils.isEmpty(url)) {
			return;
		}
		final String tag = MD5Utils.md5(url);
		imageView.setTag(tag);
		AsyncRequest.getBitmap(url, new Listener<Bitmap>() {

			@Override
			public void onErrorResponse(InvocationError invocationerror) {
				onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId), tag);
			}

			@Override
			public void onResponse(Bitmap bitmap) {
				if (bitmap != null) {
					onBitmapLoadFinish(imageView, bitmap, tag);
				} else {
					onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId),
							tag);
				}
			}
		});
	}

	private void onBitmapLoadFinish(ImageView imageView, Bitmap bmp, String tag) {
		if (isAttached && bmp != null) {
			String imageTag = (String) imageView.getTag();
			if (!tag.equals(imageTag)) {
				return;
			}
			bmp = BitmapUtils.getPersonalHeaderIcon(mContext, bmp);
			imageView.setImageBitmap(bmp);
		}

	}

	private Bitmap getDefaultBitmap(int resId) {
		Resources res = mContext.getResources();
		if (!isAttached || res == null) {
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
	}
}
