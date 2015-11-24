package cn.yjt.oa.app.personalcenter;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import cn.yjt.oa.app.beans.UserInfo;

public class SaveUserInfoUtils {
	public static void saveUserInfo(Context context, UserInfo info){
		if (info == null){
			return;
		}
		String loginName = info.getPhone();
		SharedPreferences sp = context.getSharedPreferences(loginName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("address", info.getAddress());
		edit.putString("avatar", info.getAvatar());
		edit.putString("custid", info.getCustId());
		edit.putString("custname", info.getCustName());
		edit.putString("custshortname", info.getCustShortName());
		edit.putString("department", info.getDepartment());
		edit.putString("email", info.getEmail());
		edit.putString("id", info.getId()+"");
		edit.putString("name", info.getName());
		edit.putString("phone", info.getPhone());
		edit.putString("position", info.getPosition());
		edit.putString("registtime", info.getRegisterTime());
		edit.putString("sex", info.getSex()+"");
		edit.putString("tel", info.getTel());
		edit.putString("usercode", info.getUserCode());
		edit.commit();
	}
	
	public static UserInfo getLocalUserInfo(Context context){
		//获得当前的
		UserInfo info = new UserInfo();
		SharedPreferences sp = context.getSharedPreferences("logininfo", Context.MODE_PRIVATE);
		String loginName = sp.getString("login_name", "");
		SharedPreferences loginSp = context.getSharedPreferences(loginName, Context.MODE_PRIVATE);
		Map<String, String> map = (Map<String, String>) loginSp.getAll();
		if (map.size() == 0){
			return info;
		}
		info.setAddress(map.get("address"));
		info.setAvatar(map.get("avatar"));
		info.setCustId(map.get("custid"));
		info.setCustName(map.get("custname"));
		info.setCustShortName(map.get("custshortname"));
		info.setDepartment(map.get("department"));
		info.setEmail(map.get("email"));
		String intid = map.get("id");
		long id = 0;
		if (!TextUtils.isEmpty(intid)){
			id = Long.valueOf(id);
		}
		info.setId(id);
		info.setName(map.get("name"));
		info.setPhone(map.get("phone"));
		info.setPosition(map.get("position"));
		info.setRegisterTime(map.get("registtime"));
		String sexTemp = map.get("sex");
		if (TextUtils.isEmpty(sexTemp)){
			sexTemp = "2";
		}
		info.setSex(Integer.valueOf(sexTemp));
		info.setTel(map.get("tel"));
		info.setUserCode(map.get("usercode"));
		return info;
	}
}
