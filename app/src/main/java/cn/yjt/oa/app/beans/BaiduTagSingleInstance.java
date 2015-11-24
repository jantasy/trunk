package cn.yjt.oa.app.beans;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.MainApplication;

/**
 * 百度推送需要的tag类
 */
public class BaiduTagSingleInstance {
	
	/**sp对象*/
	SharedPreferences sp;

	private static BaiduTagSingleInstance instance;
	
	private BaiduTagSingleInstance() {
		sp = MainApplication.getAppContext().getSharedPreferences("baidu_push", Context.MODE_PRIVATE);
	}
	
	public static BaiduTagSingleInstance getInstance() {
		if(instance == null) {
			synchronized (BaiduTagSingleInstance.class) {
				if(instance == null) {
					instance = new BaiduTagSingleInstance();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获取当前tag的list集合
	 * @return
	 */
	public List<String> getCurrentListTags() {
		List<String> list_tags = new ArrayList<String>();
		if(getUserId() != null){
			list_tags.add(getUserId());
		}
		if(getPhone() != null){
			list_tags.add(getPhone());
		}
		if(getCustId() != null){
			list_tags.add(getCustId());
		}
		return list_tags;
	}
	
	public void clear(){
		sp.edit().clear().commit();
	}
	
	public String getPhone() {
		return sp.getString("phone", null);
	}

	public void setPhone(String phone) {
		sp.edit().putString("phone", phone).commit();
	}

	public String getCustId() {
		return sp.getString("custId", null);
	}
	public void setCustId(String custId) {
		sp.edit().putString("custId", custId).commit();
	}
	public String getUserId() {
		return sp.getString("userId", null);
	}
	public void setUserId(String userId) {
		sp.edit().putString("userId", userId).commit();
	}
	
	
}
