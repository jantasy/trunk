package cn.yjt.oa.app.contactlist.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;

public class ContactsPublicServiceAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ContactlistContactInfo contactInfo = new ContactlistContactInfo(ContactlistContactInfo.VIEW_TYPE_PUBLIC_SERVICE);
	
	public ContactsPublicServiceAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return contactInfo;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		return ContactlistContactInfo.VIEW_TYPE_PUBLIC_SERVICE;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_public_service, parent,false);
		}
		return convertView;
	}
	

}
