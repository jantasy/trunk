package cn.yjt.oa.app.contactlist.adpter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.widget.XNode;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.task.TaskPublishingActivity;

public class ContactsStructAdapter extends BaseAdapter {

	private List<XNode> data = new ArrayList<XNode>();

	private Context context;
	private LayoutInflater inflater;

	public ContactsStructAdapter(Context context) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	
	public Context getContext() {
		return context;
	}

	public void setData(List<? extends XNode> data) {
		this.data.clear();
		this.data.addAll(data);
	}

	public List<XNode> getData() {
		return data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		Object item = getItem(position);
		if (item instanceof DeptDetailInfo) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XNode item = (XNode) getItem(position);
		Holder holder = new Holder();
		if (convertView == null) {
			if (item instanceof DeptDetailInfo) {
				convertView = inflater.inflate(R.layout.item_contacts_struct, parent, false);
			}else{
				convertView = inflater.inflate(R.layout.dept_contact_item, parent, false);
				setupUserViewHolder(convertView, holder );
			}
		}
		if (item instanceof DeptDetailInfo) {
			setupDeptView(position, convertView, item);
		} else {
			holder = (Holder) convertView.getTag();
			setupUserItemView(convertView, holder, (DeptDetailUserInfo) item );
		}
		return convertView;
	}

	private void setupDeptView(int position, View convertView, XNode item) {
		TextView deptName = (TextView) convertView
				.findViewById(R.id.dept_title);
		TextView childrenCount = (TextView) convertView.findViewById(R.id.dept_children_count);
		deptName.setText(item.getXTitle());
		List<XNode> xChildren = item.getXChildren();
		if(xChildren != null){
			childrenCount.setText(String.valueOf(xChildren.size()));
		}else{
			childrenCount.setText(String.valueOf(0));
		}
	}
	
	private void setupUserItemView(View convertView, Holder holder,
			DeptDetailUserInfo userInfo ) {
		holder.listener.userInfo = userInfo;
		
		//---改：
		holder.contactName.setText(userInfo.getName());
		holder.itemPhone.setText(userInfo.getPhone());
		String position = userInfo.getPosition();
		if(!TextUtils.isEmpty(position)){
			holder.itemPosition.setVisibility(View.VISIBLE);
			holder.itemPosition.setText(position);
		}else{
			holder.itemPosition.setVisibility(View.GONE);
		}

		holder.detailName.setText(userInfo.getXTitle());
		loadImage(userInfo.getAvatar(), holder.icon, R.drawable.contactlist_contact_icon_default);
		int sexDrawable = -1;
		if(userInfo.getSex() == UserInfo.SEX_MALE){
			sexDrawable = R.drawable.male_uncheck;
		}else if(userInfo.getSex() == UserInfo.SEX_FEMALE){
			sexDrawable = R.drawable.female_uncheck;
		}
		holder.detailSex.setImageResource(sexDrawable);
		holder.detailPhone.setText(userInfo.getPhone());
		holder.detailEmail.setText(userInfo.getEmail());
		holder.detailTel.setText(userInfo.getTel());
		//TODO:
		if(userInfo.getStatus()!=3){
			holder.btnAt.setVisibility(View.GONE);
			holder.isRegiest.setText(userInfo.getStatusDesc());
			holder.isRegiest.setVisibility(View.VISIBLE);
		}else{
			holder.btnAt.setVisibility(View.VISIBLE);
			holder.isRegiest.setText("");
			holder.isRegiest.setVisibility(View.GONE);
		}
		final View layoutContactDetail = convertView.findViewById(R.id.layout_contact_detail);
		layoutContactDetail.setVisibility(View.GONE);
		convertView.findViewById(R.id.layout_contact_item).setPadding(getContext().getResources().getDimensionPixelSize(R.dimen.dept_padding_left_type)*1, 0, 0, 0);
		convertView.findViewById(R.id.layout_contact_item).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layoutContactDetail.setVisibility(layoutContactDetail.getVisibility() == View.GONE?View.VISIBLE:View.GONE);
			}
		});
		
	}
	

	private void loadImage(String url, final ImageView icon,
			final int dfResId) {
		icon.setImageResource(dfResId);
		if (TextUtils.isEmpty(url)) {
			return;
		}
		icon.setTag(url);
		MainApplication.getHeadImageLoader().get(url, new ImageLoaderListener() {
			
			@Override
			public void onSuccess(ImageContainer container) {
				if(container.getUrl().equals(icon.getTag())){
					icon.setImageBitmap(container.getBitmap());
				}
			}
			
			@Override
			public void onError(ImageContainer container) {
				if(container.getUrl()!= null && container.getUrl().equals(icon.getTag())){
					icon.setImageResource(dfResId);
				}
				
			}
		});
	}

	private void setupUserViewHolder(View convertView, Holder holder) {
		holder.contactName = (TextView) convertView
				.findViewById(R.id.contact_item_name);
		holder.divider = convertView.findViewById(R.id.divider);
		holder.itemPhone = (TextView) convertView.findViewById(R.id.contact_item_phone);
		holder.itemPosition = (TextView) convertView.findViewById(R.id.contact_item_position);
		holder.btnPhone = (ImageView) convertView.findViewById(R.id.btn_contact_item_phone);
		holder.btnAt = (ImageView) convertView.findViewById(R.id.btn_contact_item_at);
		holder.detailName = (TextView) convertView.findViewById(R.id.detail_username);
		holder.detailSex = (ImageView) convertView.findViewById(R.id.detail_sex);
		holder.detailPhone = (TextView) convertView.findViewById(R.id.detail_phone);
		holder.detailEmail = (TextView) convertView.findViewById(R.id.detail_email);
		holder.detailTel = (TextView) convertView.findViewById(R.id.detail_tel);
		holder.icon  = (ImageView) convertView.findViewById(R.id.detail_user_icon);
		holder.isRegiest = (TextView) convertView.findViewById(R.id.tv_contact_isregiest);
		holder.listener = new ItemClickListener();
		holder.btnPhone.setOnClickListener(holder.listener);
		holder.btnAt.setOnClickListener(holder.listener);
		holder.detailPhone.setOnClickListener(holder.listener);
		holder.detailTel.setOnClickListener(holder.listener);
		convertView.setTag(holder);
	}

	class Holder {
		TextView contactName;
		TextView deptTitle;
		TextView itemPhone;
		TextView itemPosition;
		ImageView icon;
		ImageView btnPhone;
		ImageView btnAt;
		TextView isRegiest;
		ImageView flagIcon;
		TextView detailName;
		ImageView detailSex;
		TextView detailPhone;
		TextView detailEmail;
		TextView detailTel;
		View divider;
		ItemClickListener listener;
	}
	
	private class ItemClickListener implements OnClickListener{

		private DeptDetailUserInfo userInfo;
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_contact_item_phone:
				startCall();
				 /*记录操作 0612*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_DAIL_STRUCT);
				break;

			case R.id.detail_phone:
				sendMsg();
				/*记录操作 0613*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_SEND_MESSAGE_STRUCT);
				break;

			case R.id.btn_contact_item_at:
				sendTask();
				/*记录操作 0614*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_AT_STRUCT);
				break;

			case R.id.detail_tel:
				callTel();
				/*记录操作 0612*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_DAIL_STRUCT);
				break;

			default:
				break;
			}
		}
		private void sendMsg() {
			String num = userInfo.getPhone();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.sendMsg(getContext(), num);
			}
		}
		
		private void callTel() {
			String num = userInfo.getTel();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.startCall(getContext(), num);
			}
		}
		
		private void sendTask() {
			TaskPublishingActivity.launchWithUserInfo(getContext(), userInfo);
		}

		private void startCall() {
			String num = userInfo.getPhone();
			if (!TextUtils.isEmpty(num)) {
				ContactlistUtils.startCall(getContext(), num);
			}
		}

	}


}
