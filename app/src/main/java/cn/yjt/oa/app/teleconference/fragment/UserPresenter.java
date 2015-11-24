package cn.yjt.oa.app.teleconference.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.yjt.oa.app.beans.TCItem;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;

public class UserPresenter {
	private IUserView userView;
	
	public UserPresenter(IUserView view){
		userView = view;
	}
	
	public void addUser(Context context,long id){
		if(context != null){
			ContactManager manager = ContactManager.getContactManager(context);
			UserInfo userInfo = manager.getContactInfoById(id);
			if(userInfo != null){
				TCItem item = new TCItem();
				item.setName(userInfo.getName());
				item.setPhone(userInfo.getPhone());
				userView.setUser(item);
			}
		}
	}
	
	public void addUser(Context context,Long[] ids){
		if(context != null){
			ContactManager manager = ContactManager.getContactManager(context);
			List<TCItem> items = new ArrayList<TCItem>();
			for(long id:ids){
				UserInfo userInfo = manager.getContactInfoById(id);
				if(userInfo != null){
					TCItem item = new TCItem();
					item.setName(userInfo.getName());
					item.setPhone(userInfo.getPhone());
					items.add(item);
				}
			}
			userView.setUsers(items);
		}
	}
	public void addParams(String mobile,String ecp_token,String access_token){
		userView.setParams(mobile, ecp_token, access_token);
	}
}
