package cn.yjt.oa.app.contactlist.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.account.AccountManager;

public class GlobalContactManager {
	
	private static final String CONTACT_DB_REGULAR = "name\\d+";
	private static final String GLOBAL_CONTACT_PREFERENCE_NAME = "global_contact";
	private static final String KEY_YJT_USERS = "yjt_users";
	
	private List<ContactManager> contactManagers = new ArrayList<ContactManager>();
	private List<ContactManager> yjtUserContactManagers = new ArrayList<ContactManager>();
	private ContactManager currentContactManager;
	
	private Context context;

	public GlobalContactManager(Context context) {
		this.context = context;
		File file = context.getDatabasePath("none");
		File databaseDir = file.getParentFile();
		File[] listFiles = databaseDir.listFiles();
		List<String> yjtUserIds = getYjtUserIds(context);
		for (int i = 0; i < listFiles.length; i++) {
			File dbFile = listFiles[i];
			String name = dbFile.getName();
			if(name.matches(CONTACT_DB_REGULAR)){
				ContactManager contactManager = ContactManager.createContactManagerWithDBHelper(context, new ContactDBHelper(context,name));
				String userId = name.substring(4);
				if(yjtUserIds.contains(userId)){
					yjtUserContactManagers.add(contactManager);
				}else if(AccountManager.getCurrent(context).getId() != Long.parseLong(userId)){
					contactManagers.add(contactManager);
				}else{
					//current user contactManager
					currentContactManager = ContactManager.getContactManager(context);
				}
			}
		}
		if(currentContactManager == null){
			currentContactManager = ContactManager.getContactManager(context);
		}
	}
	
	private static List<String> getYjtUserIds(Context context){
		SharedPreferences sp = context.getSharedPreferences(GLOBAL_CONTACT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		List<String> yjtUsers = new ArrayList<String>();
		if(sp.contains(KEY_YJT_USERS)){
			String userStr = sp.getString(KEY_YJT_USERS, "");
			String[] users = userStr.split(",");
			List<String> userList = Arrays.asList(users);
			yjtUsers.addAll(userList);
		}
		return yjtUsers;
	}
	
	public GlobalContact queryContactWithPhone(String phone){
		GlobalContact globalContact = queryContactWithPhoneInYJTUserContactManager(phone);
		if(globalContact != null){
			return globalContact;
		}
		globalContact = currentContactManager.getContactByPhone(phone);
		if(globalContact != null){
			return globalContact;
		}
		globalContact = queryContactWithPhoneInOtherContactManager(phone);
		
		return globalContact;
		
	}
	
	private GlobalContact queryContactWithPhoneInYJTUserContactManager(String phone){
		return queryContactWithPhoneInContactManagers(phone, yjtUserContactManagers);
	}
	
	private GlobalContact queryContactWithPhoneInOtherContactManager(String phone){
		return queryContactWithPhoneInContactManagers(phone, contactManagers);
	}
	
	private GlobalContact queryContactWithPhoneInContactManagers(String phone,List<ContactManager> contactManagers ){
		if(!contactManagers.isEmpty()){
			for (ContactManager contactManager : contactManagers) {
				GlobalContact globalContact = contactManager.getContactByPhone(phone);
				if(globalContact != null){
					return globalContact;
				}
			}
		}
		return null;
	}
	
	public void release(){
		List<ContactManager> contactManagers = this.contactManagers;
		for (ContactManager contactManager : contactManagers) {
			contactManager.release();
		}
		contactManagers.clear();
		this.contactManagers = null;
		
		List<ContactManager> yjtUserContactManagers = this.yjtUserContactManagers;
		for (ContactManager contactManager : yjtUserContactManagers) {
			contactManager.release();
		}
		yjtUserContactManagers.clear();
		this.yjtUserContactManagers = null;
		this.currentContactManager = null;
		this.context = null;
		
	}
	
	public static void addYjtUserId(Context context,long userId){
		SharedPreferences sp = context.getSharedPreferences(GLOBAL_CONTACT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		List<String> yjtUserIds = getYjtUserIds(context);
		
		if(!yjtUserIds.contains(String.valueOf(userId))){
			yjtUserIds.add(String.valueOf(userId));
		}
		sp.edit().putString(KEY_YJT_USERS, buildYjtUserIdString(yjtUserIds)).commit();
	}
	
	private static String buildYjtUserIdString(List<String> yjtUserIds){
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < yjtUserIds.size(); i++) {
			builder.append(yjtUserIds.get(i));
			if(i != yjtUserIds.size() - 1){
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	
	public static class GlobalContact{
		public String name;
		public String phone;
		public String custName;
		public String department;
		public String position;
		public long userId;
	}
}
