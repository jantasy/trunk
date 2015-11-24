package cn.yjt.oa.app.enterprise.operation;

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
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserManagerInfo;

import com.umeng.analytics.MobclickAgent;

public class MemberAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<UserManagerInfo> members;
	private Context context;
	private UserInfo currentUserInfo;
	
	private List<UserManagerInfo> deleteMembers;
	boolean isModify;

	
	public MemberAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		currentUserInfo=AccountManager.getCurrent(context);
		members = new ArrayList<UserManagerInfo>();
		deleteMembers = new ArrayList<UserManagerInfo>();
	}
	

	private OnSearchMemptyListener mListener;
	
	public void setOnSearchMemptyListener(OnSearchMemptyListener listener){
		this.mListener = listener;
	}
	interface OnSearchMemptyListener{
		public void adapterIsEmpty();
	}



	public List<UserManagerInfo> getDeleteMembers() {
		return deleteMembers;
	}

	public void clearDeleteMembers(){
		deleteMembers.clear();
	}

	public void bindData(List<UserManagerInfo> members){
		if(members != null){
			this.members = members;
		}
	}
	
	public void addMember(UserManagerInfo member){
		if(members!=null){
			members.add(member);
			notifyDataSetChanged();
			isModify = true;
		}
	}
	
	public boolean isModify() {
		return isModify;
	}
	
	public void removeMember(UserManagerInfo member){
		if(members!=null){
			members.remove(member);
			notifyDataSetChanged();
			isModify = true;
		}
		if(deleteMembers!=null){
			deleteMembers.add(member);
		}
		if(mListener!=null){
			if(members.size()==0){
				mListener.adapterIsEmpty();
			}
		}
	}
	
	public List<UserManagerInfo> getMembers(){
		return members;
	}
	
	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final UserManagerInfo info = members.get(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.enterprise_memeber_list_item, parent, false);
			holder.member_icon= (ImageView) convertView.findViewById(R.id.member_icon);
			holder.name = (TextView) convertView.findViewById(R.id.tv_member_name);
			holder.phone = (TextView) convertView.findViewById(R.id.tv_member_phone);
			holder.status_desc = (TextView) convertView.findViewById(R.id.status_desc);
			holder.delete = (ImageView) convertView.findViewById(R.id.ib_member_delete);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(currentUserInfo.getIsYjtUser()==1){
			holder.delete.setVisibility(View.GONE);
		}else{
			if(info.getId()==AccountManager.getCurrent(context).getId()){
				holder.delete.setVisibility(View.GONE);
			}else{
				holder.delete.setVisibility(View.VISIBLE);
			}
		}
		
	
		
		if(info.getIsCustAdmin()==1){
			holder.member_icon.setVisibility(View.VISIBLE);
		}else{
			holder.member_icon.setVisibility(View.INVISIBLE);
		}
		if(!TextUtils.isEmpty(info.getName())){
			holder.name.setText(info.getName());
		}else{
			holder.name.setText("匿名");
		}
		holder.phone.setText(info.getPhone());
		holder.status_desc.setText(info.getStatusDesc());
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(context, "enterprise_manage_member_list_clickitemdeletedbtn");
				removeMember(info);
			}
		});
		
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView member_icon;
		TextView name;
		TextView phone;
		TextView status_desc;
		ImageView delete;
	}

}
