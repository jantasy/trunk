package cn.yjt.oa.app.contactlist.adpter;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.ContactlistFragment;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.BitmapUtils;

public class ContactAllAdapter extends BaseAdapter {
	private Context mContext;
	private boolean isAttached;
	private int model;
	private List<UserSimpleInfo> checkedList = new ArrayList<UserSimpleInfo>();
	private List<ContactlistContactInfo> allContacts = new ArrayList<ContactlistContactInfo>();
	private ContactsPublicServiceAdapter contactsServiceAdapter;
	
	public ContactAllAdapter(Context context,boolean isAttached,int model){
		this.mContext=context;
		this.isAttached=isAttached;
		this.model=model;
	}
	
	public void setContactsServiceAdapter(
			ContactsPublicServiceAdapter contactsServiceAdapter) {
		this.contactsServiceAdapter = contactsServiceAdapter;
	}

	@Override
	public int getCount() {
		if(contactsServiceAdapter != null){
			return contactsServiceAdapter.getCount() + allContacts.size();
		}
		return allContacts.size();
	}

	@Override
	public Object getItem(int position) {
		int count = 0;
		if(contactsServiceAdapter != null){
			count = contactsServiceAdapter.getCount();
			if(position < count){
				return contactsServiceAdapter.getItem(position);
			}
		}
		return allContacts.get(position - count);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		ContactlistContactInfo item = (ContactlistContactInfo) getItem(position);
		int type = item.getViewType();
		if (type == ContactlistContactInfo.VIEW_TYPE_INDEX) {
			return false;
		}
		return super.isEnabled(position);
	}

	public List<UserSimpleInfo> getCheckedList() {
		return checkedList;
	}


	@Override
	public int getViewTypeCount() {
		if(contactsServiceAdapter != null){
			return contactsServiceAdapter.getViewTypeCount() + 2;
		}
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(contactsServiceAdapter != null){
			if(position < contactsServiceAdapter.getCount()){
				return contactsServiceAdapter.getItemViewType(position);
			}
		}
		ContactlistContactInfo contactInfo = (ContactlistContactInfo) getItem(position);
		return contactInfo.getViewType();
	}

	public void setAllContacts(List<ContactlistContactInfo> allContacts) {
		this.allContacts = getContacts(allContacts);
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if(contactsServiceAdapter != null){
			contactsServiceAdapter.notifyDataSetChanged();
		}
	}
	
	private List<ContactlistContactInfo> getContacts(List<ContactlistContactInfo> allContacts){
		List<ContactlistContactInfo> contacts = new ArrayList<ContactlistContactInfo>();
		for (ContactlistContactInfo contactlistContactInfo : allContacts) {
			if(contactlistContactInfo.getCommonContactInfo() == null){
				contacts.add(contactlistContactInfo);
			}
		}
		return contacts;
	}
	
	
	public List<ContactlistContactInfo> getAllContacts() {
		return allContacts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(contactsServiceAdapter != null){
			if(position < contactsServiceAdapter.getCount()){
				return contactsServiceAdapter.getView(position, convertView, parent);
			}
		}
		System.out.println("contactalladapter.getView."+position);
		ContactlistContactInfo info = (ContactlistContactInfo) getItem(position);
		Holder holder = null;
		int currType = info.getViewType();
		if (convertView == null) {
			holder = new Holder();
			convertView = getView(currType, holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		if (position > 0 && holder.divider != null) {
			holder.divider.setTag(getItem(position - 1));
		}
		setView(holder, currType, info);
		return convertView;
	}

	private View getView(int type, Holder holder) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = null;
		if (type == ContactlistContactInfo.VIEW_TYPE_CONTACT) {
			view = inflater.inflate(R.layout.contactlist_contact_item, null);
			holder.contactIcon = (ImageView) view
					.findViewById(R.id.contact_item_icon);
			holder.contactName = (TextView) view
					.findViewById(R.id.contact_item_name);
			holder.checkBox = (CheckBox) view.findViewById(R.id.item_check);
			holder.divider = view.findViewById(R.id.divider);
		} else {
			view = inflater.inflate(R.layout.contactlist_contact_item_index,
					null);
			holder.contactIndex = (TextView) view
					.findViewById(R.id.contact_item_index);
		}
		holder.type = type;
		view.setTag(holder);
		return view;
	}

	private void setView(Holder holder, int currType,
			ContactlistContactInfo info) {
		if (currType == ContactlistContactInfo.VIEW_TYPE_CONTACT) {
			loadImage(info.getContactInfo().getAvatar(), holder.contactIcon,
					R.drawable.contactlist_contact_icon_default);
			holder.contactName.setText(info.getContactInfo().getName());
			if (!info.getContactInfo().isRegister()) {
			} else {
				if (!isMulitChoice()) {
					holder.checkBox.setVisibility(View.GONE);
				} else {
					holder.checkBox.setVisibility(View.VISIBLE);
					holder.checkBox.setOnCheckedChangeListener(changeListener);
					holder.checkBox.setTag(info);
					holder.checkBox.setChecked(info.isChecked());
					setCheckedBg((View) holder.contactName.getParent(),
							info.isChecked());
				}

			}
			setDivider(holder);
		} else {
			if (info.getContactInfo().isRegister()) {
				holder.contactIndex.setText(info.getContactInfo().getName());
			} else {
				holder.contactIndex
						.setText(R.string.contactlist_contact_unregister);
			}

		}

	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			ContactlistContactInfo info = (ContactlistContactInfo) buttonView
					.getTag();
			addChekedData(info, isChecked);
			info.setChecked(isChecked);
			setCheckedBg((View) buttonView.getParent(), isChecked);
		}
	};

	public boolean isMulitChoice() {
		return model == ContactlistFragment.CONTACTLIST_STRAT_MODEL_MULITCHOICE;
	}

	private void setCheckedBg(View view, boolean checked) {
		if (checked) {
			view.setBackgroundColor(getItemCheckedColorBg());
		} else {
			view.setBackgroundColor(getItemUnCheckedColorBg());
		}
	}

	private int getItemCheckedColorBg() {
		return Color.parseColor("#ffe2eaf8");
	}

	private int getItemUnCheckedColorBg() {
		return Color.parseColor("#fff2f6fc");
	}

	private void setDivider(Holder holder) {
		if (holder.divider.getTag() == null) {
			return;
		}
		ContactlistContactInfo info = (ContactlistContactInfo) holder.divider
				.getTag();
		if (info.getViewType() != holder.type) {
			holder.divider.setVisibility(View.GONE);
		} else {
			holder.divider.setVisibility(View.VISIBLE);
		}
	}

	public static class Holder {
		int type;
		TextView contactName, contactIndex;
		ImageView contactIcon;
		CheckBox checkBox;
		View divider;
	}

	private void addChekedData(ContactlistContactInfo inf, boolean checked) {
		UserSimpleInfo info = new UserSimpleInfo(inf);
		if (checked && !checkedList.contains(info)) {
			checkedList.add(info);
		} else if (!checked && checkedList.contains(info)) {
			checkedList.remove(info);
		}
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

	private Bitmap getDefaultBitmap(int resId) {
		Resources res = mContext.getResources();
		if (!isAttached || res == null) {
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
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

}
