package cn.yjt.oa.app.enterprise.contact;

import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
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
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;
import cn.yjt.oa.app.utils.BitmapUtils;

public class MachineContactAdapter extends BaseAdapter {
	private Context mContext;
	private List<MachineContactInfo> allContacts=Collections.emptyList();
	private ContentResolver resolver;
	
	public MachineContactAdapter(Context context){
		this.mContext=context;
		resolver=mContext.getContentResolver();
	}

	@Override
	public int getCount() {
		return allContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return allContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactViewHolder holder = null;
		int currType = allContacts.get(position).getViewType();
		if (convertView == null) {
			holder = new ContactViewHolder();
			convertView = initView(currType, holder);
		} else {
			holder = (ContactViewHolder) convertView.getTag();
		}

		if (holder.type != currType) {
			holder = new ContactViewHolder();
			convertView = initView(currType, holder);
		}
		if (position > 0 && holder.divider != null) {
			holder.divider.setTag(allContacts.get(position - 1));
		}
		setView(holder, currType,position);
		return convertView;
	}
	
	
	
	
	public List<MachineContactInfo> getAllContacts() {
		return allContacts;
	}

	public void setAllContacts(List<MachineContactInfo> allContacts) {
		this.allContacts = allContacts;
	}

	private View initView(int type, ContactViewHolder holder) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = null;
		if (type == ContactlistContactInfo.VIEW_TYPE_CONTACT) {
			view = inflater
					.inflate(R.layout.contactlist_contact_item, null);
			holder.contactIcon = (ImageView) view
					.findViewById(R.id.contact_item_icon);
			holder.contactName = (TextView) view
					.findViewById(R.id.contact_item_name);
			holder.checkBox = (CheckBox) view.findViewById(R.id.item_check);
			holder.checkBox.setVisibility(view.VISIBLE);
			holder.divider = view.findViewById(R.id.divider);
		} else {
			view = inflater.inflate(
					R.layout.contactlist_contact_item_index, null);
			holder.contactIndex = (TextView) view
					.findViewById(R.id.contact_item_index);
		}
		holder.type = type;
		view.setTag(holder);
		return view;
	}
	private void setView(ContactViewHolder holder, int currType,
			final int position) {
		if (currType == ContactlistContactInfo.VIEW_TYPE_CONTACT) {
			if(allContacts.get(position).getPhotoid()>0){
				loadImage(allContacts.get(position).getPhotoid(),holder.contactIcon,
						R.drawable.contactlist_contact_icon_default);
			}else{
				holder.contactIcon.setImageResource(R.drawable.contactlist_contact_icon_default);
			}
			
			holder.contactName.setText(allContacts.get(position).getDisplayName() + "("+allContacts.get(position).getNumber()+")");
			
		
			holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						allContacts.get(position).setIsChecked(1);
					}else{
						allContacts.get(position).setIsChecked(0);
					}
				}
			});
			
			if(allContacts.get(position).getIsChecked()==0){
				holder.checkBox.setChecked(false);
			}else{
				holder.checkBox.setChecked(true);
			}
			setDivider(holder);
		} else {
			holder.contactIndex.setText(allContacts.get(position).getDisplayName());

		}

	}
	
	private void loadImage(long photo_id, ImageView imageView, final int dfResId) {
		imageView.setImageResource(dfResId);
		String selection = ContactsContract.Data._ID + " = " + photo_id;

		String[] projection = new String[] { ContactsContract.Data.DATA15 };
		Cursor cur = resolver.query(ContactsContract.Data.CONTENT_URI,
				projection, selection, null, null);
		cur.moveToFirst();
		byte[] contactIcon = cur.getBlob(0);
		if (contactIcon != null) {
			Bitmap contactPhoto = BitmapFactory.decodeByteArray(contactIcon, 0,
					contactIcon.length);
			getHeaderIcon(imageView, contactPhoto);
		} else {
			getHeaderIcon(imageView, getDefaultBitmap(dfResId));
		}

	}
	
	private Bitmap getDefaultBitmap(int resId) {
		Resources res = mContext.getResources();
		if (res == null) {
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
	}
	
	private void getHeaderIcon(ImageView imageView, Bitmap bmp) {
		if (bmp != null) {
			bmp = BitmapUtils.getMachineContactHeaderIcon(mContext, bmp);
			imageView.setImageBitmap(bmp);
			
		}

	}
	
	private void setDivider(ContactViewHolder holder) {
		if (holder.divider.getTag() == null) {
			return;
		}
		MachineContactInfo info = (MachineContactInfo) holder.divider
				.getTag();
		if (info.getViewType() != holder.type) {
			holder.divider.setVisibility(View.GONE);
		} else {
			holder.divider.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	private class ContactViewHolder {
		private int type;
		private TextView contactName, contactIndex;
		private ImageView contactIcon;
		private CheckBox checkBox;
		private View divider;

	}

}
