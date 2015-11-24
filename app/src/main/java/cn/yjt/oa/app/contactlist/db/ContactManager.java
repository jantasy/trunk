package cn.yjt.oa.app.contactlist.db;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.annotation.Table;

import u.aly.de;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.db.ContactDBHelper.DB;
import cn.yjt.oa.app.contactlist.db.GlobalContactManager.GlobalContact;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.LogUtils;

/**联系人管理类*/
public class ContactManager {

	private static final String TAG = "ContactManager";

	/**本类对象（单例）*/
	private static ContactManager manager = null;
	/**sqlite数据库对象*/
	private SQLiteDatabase database;
	/**数据库操作用的ContentValue对象*/
	private ContentValues values;

	/**常量Split分隔符*/
	private static final String SPLIT = ",";
	/**是否正在加载*/
	private boolean isLoading = false;

	/**私有化构造方法*/
	private ContactManager(Context context) {
		ContactDBHelper helper = new ContactDBHelper(context);
		database = helper.getWritableDatabase();
		values = new ContentValues();
	}

	/**公有的构造方法*/
	public ContactManager(Context context, ContactDBHelper contactDBHelper) {
		database = contactDBHelper.getWritableDatabase();
		values = new ContentValues();
	}

	/**获取本类对象*/
	public static ContactManager getContactManager(Context con) {
		if (manager == null) {
			manager = new ContactManager(con.getApplicationContext());
		}
		return manager;
	}

	/**销毁本类对象，同时关掉数据库*/
	public static void destoryContactManager() {
		if (manager != null) {
			manager.database.close();
		}
		manager = null;
	}

	/**添加联系人的数据库*/
	public void addContacts(List<ContactInfo> list) {
		for (ContactInfo info : list) {
			addContact(info);
		}
	}

	/**添加联系人到数据库*/
	public void addContact(ContactInfo info) {

		LogUtils.i(TAG, "addContact:" + info);

		values.clear();
		values.put(DB.ADDRESS, info.getAddress());
		values.put(DB.AVATAR, info.getAvatar());
		values.put(DB.CUSTID, info.getCustId());
		values.put(DB.CUSTNAME, info.getCustName());
		values.put(DB.DEPARTMENT, info.getDepartment());
		values.put(DB.EMAIL, info.getEmail());
		values.put(DB.NAME, info.getName());
		values.put(DB.PHONE, info.getPhone());
		values.put(DB.POSITION, info.getPosition());
		values.put(DB.REGISTERTIME, info.getRegisterTime());
		values.put(DB.SEX, info.getSex());
		values.put(DB.TEL, info.getTel());
		values.put(DB.USER_CODE, info.getUserCode());
		//判断该联系人是否注册
		boolean isRegistered = info.isRegister();
		if (isRegistered) {
			values.put(DB.USERID, info.getUserId());
			addRegisteredContact(values, info.getUserId());
			boolean isInUnregistered = isContactExist(info.getPhone());
			if (isInUnregistered) {
				deleteUnregisteredContcat(info.getPhone());
			}
		} else {
			String phone = info.getPhone();
			if (!TextUtils.isEmpty(phone)) {
				addUnregisteredContact(values, phone);
			}
		}
	}

	/**添加已注册联系人到数据库*/
	private void addRegisteredContact(ContentValues values, long id) {
		boolean exist = isContactExist(id);
		if (exist) {
			updateContact(values, id);
		} else {
			insertContact(values);
		}
	}

	/**添加未注册的联系人到数据库*/
	private void addUnregisteredContact(ContentValues values, String phone) {
		boolean exist = isContactExist(phone);
		if (exist) {
			updateUnregisterContact(values, phone);
		} else {
			insertUnregisterContact(values);
		}
	}

	public void addGrouops(List<GroupInfo> infos) {
		for (GroupInfo info : infos) {
			addGroup(info);
		}

	}

	public void addGroup(GroupInfo info) {
		values.clear();
		values.put(DB.GROUP_ID, info.getId());
		values.put(DB.NAME, info.getName());
		values.put(DB.DESCRIPTION, info.getDescription());
		values.put(DB.UPDATE_TIME, info.getUpdateTime());
		values.put(DB.CREATE_TIME, info.getCreateTime());
		values.put(DB.AVATAR, info.getAvatar());
		String userids = "";
		for (UserSimpleInfo sInfo : info.getUsers()) {
			userids += sInfo.getId() + ",";
		}
		if (userids.length() > 0) {
			userids = userids.substring(0, userids.length() - 1);
		}
		values.put(DB.GROUP_CONTACTS, userids);
		boolean exist = isGroupExist(info.getId());
		try {
			beginTransaction();
			if (exist) {
				updateGroup(values, info.getId());
			} else {
				insertGroup(values);
			}
			setTransactionSuccessful();
		} catch (Exception e) {
			log("add group failed ! table name : " + DB.TABLE_GROUP, e);
		} finally {
			endTransaction();
		}
		addGroupContactsToContact(info.getUsers());
	}

	private void addGroupContactsToContact(UserSimpleInfo[] infos) {
		if (infos == null || infos.length == 0) {
			return;
		}
		List<Long> unexistIds = new ArrayList<Long>();
		for (UserSimpleInfo info : infos) {
			long id = info.getId();
			if (isContactExist(id)) {
				continue;
			} else {
				unexistIds.add(id);
			}
		}
		getContactsFromServer(unexistIds, new Listener<Response<ContactInfo>>() {

			@Override
			public void onResponse(Response<ContactInfo> response) {
				if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
					ContactInfo info = response.getPayload();
					info.setRegister(true);
					addContact(info);
				}

			}

			@Override
			public void onErrorResponse(InvocationError error) {

			}
		});
	}

	public void addPublicService(List<CommonContactInfo> list) {
		for (CommonContactInfo commonContactInfo : list) {
			addPublicService(commonContactInfo);
		}
	}

	public void addPublicService(CommonContactInfo info) {
		values.clear();
		// values.put(DB.ID, info.getId());
		values.put(DB.NAME, info.getName());
		values.put(DB.DESCRIPTION, info.getDescription());
		values.put(DB.PHONE, info.getPhone1());
		values.put(DB.PHONE2, info.getPhone2());
		values.put(DB.PHONE3, info.getPhone3());
		values.put(DB.URL, info.getUrl());
		values.put(DB.CREATE_TIME, info.getCreateTime());
		boolean exist = isPublicServiceExist(info.getId());
		try {
			beginTransaction();
			if (exist) {
				updatePublicService(values, info.getId());
			} else {
				insertPublicService(values);
			}
			setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			endTransaction();
		}

	}

	public List<ContactInfo> loadAllLocalContacts(boolean onlyRegistered) {
		List<ContactInfo> list = new ArrayList<ContactInfo>();
		list.addAll(getAllLocalRegisteredContacts());
		if (!onlyRegistered) {
			List<ContactInfo> allLocalUnregisteredContacts = getAllLocalUnregisteredContacts();
			list.addAll(allLocalUnregisteredContacts);
		}
		return list;
	}

	public ContactInfo getContactInfoById(long id) {
		String sql = "SELECT * FROM " + DB.TABLE_CONTACT + " where " + DB.USERID + " = " + id;
		List<ContactInfo> list = getContacts(sql, true);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public GlobalContact getContactByPhone(String phone) {
		if (TextUtils.isEmpty(phone)) {
			return null;
		}
		String sql = "SELECT " + DB.NAME + " , " + DB.CUSTNAME + " , " + DB.DEPARTMENT + " , " + DB.POSITION + " , "
				+ DB.USERID + " FROM " + DB.TABLE_CONTACT + " WHERE " + DB.PHONE + "=? OR " + DB.TEL + "=?";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] { phone, phone });
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					GlobalContact contact = new GlobalContact();
					contact.name = cursor.getString(0);
					contact.custName = cursor.getString(1);
					contact.department = cursor.getString(2);
					contact.position = cursor.getString(3);
					contact.phone = phone;
					contact.userId = cursor.getLong(4);
					return contact;
				}
			} finally {
				cursor.close();
			}
		}

		return null;
	}

	public <T> void getContactFromServerById(long id, Listener<T> listener) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		getContactsFromServer(ids, listener);
	}

	private List<ContactInfo> getAllLocalRegisteredContacts() {
		String sql = "SELECT * FROM " + DB.TABLE_CONTACT;
		//		while(isLoading){
		//			
		//		}
		return getContacts(sql, true);
	}

	private List<ContactInfo> getAllLocalUnregisteredContacts() {
		String sql = "SELECT * FROM " + DB.TABLE_CONTACT_UNREGISTER;
		//		while(isLoading){
		//		
		//		}
		return getContacts(sql, false);
	}

	private List<ContactInfo> getContacts(String sql, boolean isregistered) {
		List<ContactInfo> list = new ArrayList<ContactInfo>();
		Cursor cursor = null;
		try {
			cursor = getDatabase().rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContactInfo info = new ContactInfo();
					info.setAddress(cursor.getString(cursor.getColumnIndex(DB.ADDRESS)));
					info.setAvatar(cursor.getString(cursor.getColumnIndex(DB.AVATAR)));
					info.setCustId(cursor.getString(cursor.getColumnIndex(DB.CUSTID)));
					info.setCustName(cursor.getString(cursor.getColumnIndex(DB.CUSTNAME)));
					info.setDepartment(cursor.getString(cursor.getColumnIndex(DB.DEPARTMENT)));
					info.setEmail(cursor.getString(cursor.getColumnIndex(DB.EMAIL)));
					info.setName(cursor.getString(cursor.getColumnIndex(DB.NAME)));
					info.setPhone(cursor.getString(cursor.getColumnIndex(DB.PHONE)));
					info.setPosition(cursor.getString(cursor.getColumnIndex(DB.POSITION)));
					info.setRegister(isregistered);
					info.setRegisterTime(isregistered ? cursor.getString(cursor.getColumnIndex(DB.REGISTERTIME)) : "");
					info.setSex(cursor.getInt(cursor.getColumnIndex(DB.SEX)));
					info.setTel(cursor.getString(cursor.getColumnIndex(DB.TEL)));
					info.setUserCode(cursor.getString(cursor.getColumnIndex(DB.USER_CODE)));
					if (isregistered) {
						long long1 = cursor.getLong(cursor.getColumnIndex(DB.USERID));
						info.setUserId(long1);
						info.setUserId(long1);
					}
					list.add(info);
				}
			}
		} catch (Exception e) {
			log("select table failed!", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	private <T> void getContactsFromServer(List<Long> ids, Listener<T> listener) {
		for (Long id : ids) {
			doRequestContacts(AsyncRequest.MODULE_CONTACTS_ALL, String.valueOf(id), listener);
		}
	}

	public <T> void getallRegisterContactsFromServerAsync(Listener<T> listener) {
		doRequestContacts(AsyncRequest.MODULE_CONTACTS_ALL, null, listener);
	}

	public <T> void getallUnregisterContactsFromServerAsync(Listener<T> listener) {
		doRequestContacts(AsyncRequest.MODULE_CONTACTS_EXTERNAL, null, listener);
	}

	private <T> void doRequestContacts(String module, String moduleItem, Listener<T> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(module);
		builder.setModuleItem(moduleItem);
		TypeToken<Response<List<ContactInfo>>> typeToken = new TypeToken<Response<List<ContactInfo>>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.setResponseListener(listener);
		builder.build().get();
	}

	private void updateContact(ContentValues values, long id) {
		String whereClause = DB.USERID + "=?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		update(DB.TABLE_CONTACT, values, whereClause, whereArgs);
	}

	private void updateUnregisterContact(ContentValues values, String phone) {
		String whereClause = DB.PHONE + "=?";
		String[] whereArgs = new String[] { phone };
		update(DB.TABLE_CONTACT_UNREGISTER, values, whereClause, whereArgs);
	}

	public List<GroupInfo> loadAllLocalGroups() {
		return getGroups();
	}

	public <T> void getAllGroupsFromServerAsync(Listener<T> listener) {
		doRequestGroups(listener);
	}

	private <T> void doRequestGroups(Listener<T> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_GROUPS);
		builder.setResponseListener(listener);
		TypeToken<Response<List<GroupInfo>>> typeToken = new TypeToken<Response<List<GroupInfo>>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.build().get();
	}

	private List<GroupInfo> getGroups() {
		String sql = " SELECT * FROM " + DB.TABLE_GROUP;
		List<GroupInfo> list = new ArrayList<GroupInfo>();
		Cursor cursor = null;
		try {
			cursor = getCursor(sql);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					GroupInfo info = new GroupInfo();
					info.setId(cursor.getLong(cursor.getColumnIndex(DB.GROUP_ID)));
					info.setDescription(cursor.getString(cursor.getColumnIndex(DB.DESCRIPTION)));
					info.setName(cursor.getString(cursor.getColumnIndex(DB.NAME)));
					info.setAvatar(cursor.getString(cursor.getColumnIndex(DB.AVATAR)));
					info.setCreateTime(cursor.getString(cursor.getColumnIndex(DB.CREATE_TIME)));
					info.setUpdateTime(cursor.getString(cursor.getColumnIndex(DB.UPDATE_TIME)));
					String ids = cursor.getString(cursor.getColumnIndex(DB.GROUP_CONTACTS));
					info.setUsers(getUserSimpleInfos(ids));
					list.add(info);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	public GroupInfo getGroupInfo(long id) {
		GroupInfo info = new GroupInfo();
		String sql = " SELECT * FROM " + DB.TABLE_GROUP + " where " + DB.GROUP_ID + "=" + id;
		Cursor cursor = null;
		try {
			cursor = getDatabase().rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					info.setId(cursor.getLong(cursor.getColumnIndex(DB.GROUP_ID)));
					info.setName(cursor.getString(cursor.getColumnIndex(DB.NAME)));
					info.setDescription(cursor.getString(cursor.getColumnIndex(DB.DESCRIPTION)));
					String ids = cursor.getString(cursor.getColumnIndex(DB.GROUP_CONTACTS));
					info.setUsers(getUserSimpleInfos(ids));
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return info;
	}

	private UserSimpleInfo[] getUserSimpleInfos(String ids) {
		List<UserSimpleInfo> infos = new ArrayList<UserSimpleInfo>();
		String[] temp = ids.split(SPLIT);
		for (String id : temp) {
			if (TextUtils.isEmpty(id)) {
				continue;
			}
			UserSimpleInfo info = new UserSimpleInfo(Long.parseLong(id), "");
			infos.add(info);
		}
		return infos.toArray(new UserSimpleInfo[infos.size()]);
	}

	public List<ContactInfo> getGroupMembersInfo(UserSimpleInfo[] infos) {
		List<ContactInfo> list = new ArrayList<ContactInfo>();
		if (infos != null) {

			for (UserSimpleInfo info : infos) {
				String sql = "SELECT * FROM " + DB.TABLE_CONTACT + " where " + DB.USERID + "=" + info.getId();
				list.addAll(getContacts(sql, true));
			}
		}
		return list;
	}

	public void createGroup(GroupInfo info, Listener<Response<GroupInfo>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_GROUPS);
		builder.setRequestBody(info);
		builder.setResponseType(new TypeToken<Response<GroupInfo>>() {
		}.getType());
		builder.setResponseListener(listener);
		builder.build().post();
	}

	public void updateGroupInfo(GroupInfo info, Listener<Response<Object>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_GROUPS);
		builder.setModuleItem(String.valueOf(info.getId()));
		builder.setRequestBody(info);
		builder.setResponseType(new TypeToken<Response<Object>>() {
		}.getType());
		builder.setResponseListener(listener);
		builder.build().put();
	}

	public List<CommonContactInfo> getAllPublicService() {
		String sql = "SELECT * FROM " + DB.TABLE_PUBLIC_SERVICE;
		return getPublicService(sql);
	}

	private List<CommonContactInfo> getPublicService(String sql) {
		List<CommonContactInfo> list = new ArrayList<CommonContactInfo>();
		Cursor cursor = null;
		try {
			cursor = getCursor(sql);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					CommonContactInfo info = new CommonContactInfo();
					info.setId(cursor.getLong(cursor.getColumnIndex(DB.ID)));
					info.setName(cursor.getString(cursor.getColumnIndex(DB.NAME)));
					info.setDescription(cursor.getString(cursor.getColumnIndex(DB.DESCRIPTION)));
					info.setPhone1(cursor.getString(cursor.getColumnIndex(DB.PHONE)));
					info.setPhone2(cursor.getString(cursor.getColumnIndex(DB.PHONE2)));
					info.setPhone3(cursor.getString(cursor.getColumnIndex(DB.PHONE3)));
					info.setCreateTime(cursor.getString(cursor.getColumnIndex(DB.CREATE_TIME)));
					info.setUrl(cursor.getString(cursor.getColumnIndex(DB.URL)));
					list.add(info);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	public void getAllPublicServiceFromServerAsyn(Listener<Response<List<CommonContactInfo>>> listener) {
		doRequestPublicService(listener);
	}

	private <T> void doRequestPublicService(Listener<T> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CONTACTS_COMMON);
		builder.setResponseListener(listener);
		TypeToken<Response<List<CommonContactInfo>>> token = new TypeToken<Response<List<CommonContactInfo>>>() {
		};
		builder.setResponseType(token.getType());
		builder.build().get();
	}

	private void updateGroup(ContentValues values, long id) {
		String whereClause = DB.GROUP_ID + "=?";

		String[] whereArgs = new String[] { String.valueOf(id) };
		update(DB.TABLE_GROUP, values, whereClause, whereArgs);
	}

	private void updatePublicService(ContentValues values, long id) {
		String whereClause = DB.ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		update(DB.TABLE_PUBLIC_SERVICE, values, whereClause, whereArgs);
	}

	private SQLiteDatabase getDatabase() {
		if (database == null || !database.isOpen()) {
			ContactDBHelper helper = new ContactDBHelper(MainApplication.getAppContext());
			database = helper.getWritableDatabase();
		}
		return database;
	}

	private void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		try {
			beginTransaction();
			getDatabase().update(table, values, whereClause, whereArgs);
			setTransactionSuccessful();
		} catch (Exception e) {
			log("update contact failed ! table name : " + table, e);
		} finally {
			endTransaction();
		}
	}

	private void insertContact(ContentValues values) {
		insert(DB.TABLE_CONTACT, values);
	}

	private void insertUnregisterContact(ContentValues values) {
		insert(DB.TABLE_CONTACT_UNREGISTER, values);
	}

	private void insertGroup(ContentValues values) {
		insert(DB.TABLE_GROUP, values);
	}

	private void insertPublicService(ContentValues values) {
		insert(DB.TABLE_PUBLIC_SERVICE, values);
	}

	private void insert(String table, ContentValues values) {

		try {
			getDatabase().insert(table, null, values);
		} catch (Exception e) {
			log("insert contact failed ! table name : " + table, e);
		}
	}

	private boolean isContactExist(long id) {
		String sql = "SELECT " + DB.USERID + " FROM " + DB.TABLE_CONTACT + " WHERE " + DB.USERID + " = " + id;

		return isExist(sql);
	}

	private boolean isContactExist(String phone) {
		String sql = "SELECT " + DB.PHONE + " FROM " + DB.TABLE_CONTACT_UNREGISTER + " WHERE " + DB.PHONE + " = '"
				+ phone + "'";
		return isExist(sql);
	}

	private boolean isGroupExist(long id) {
		String sql = "SELECT " + DB.GROUP_ID + " FROM " + DB.TABLE_GROUP + " WHERE " + DB.GROUP_ID + " = " + id;
		return isExist(sql);
	}

	private boolean isPublicServiceExist(long id) {
		String sql = "SELECT " + DB.USERID + " FROM " + DB.TABLE_PUBLIC_SERVICE + " WHERE " + DB.USERID + " = " + id;
		return isExist(sql);
	}

	private boolean isExist(String sql) {
		boolean exist = false;
		Cursor cursor = null;
		try {
			cursor = getCursor(sql);
			if (cursor.getCount() > 0) {
				exist = true;
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}

		}
		return exist;
	}

	public void deleteAllRegisteredContacts() {
		delete(DB.TABLE_CONTACT, null, null);
	}

	public void deleteAllDept() {
		delete(DB.TABLE_DEPT, null, null);
	}

	public void deleteAllDeptUser() {
		delete(DB.TABLE_DEPT_USER, null, null);
	}

	public void deleteRegisteredContcat(long id) {
		String whereClause = DB.USERID + " =?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		delete(DB.TABLE_CONTACT, whereClause, whereArgs);
	}

	public void deleteAllUnregisteredContacts() {
		getDatabase().delete(DB.TABLE_CONTACT_UNREGISTER, null, null);
	}

	public void deleteUnregisteredContcat(String phone) {
		String whereClause = DB.PHONE + " =?";
		String[] whereArgs = new String[] { phone };
		delete(DB.TABLE_CONTACT, whereClause, whereArgs);
	}

	public void deleteAllGroups() {
		getDatabase().delete(DB.TABLE_GROUP, null, null);
	}

	public void deleteGroup(long id) {
		String whereClause = DB.GROUP_ID + " =?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		delete(DB.TABLE_GROUP, whereClause, whereArgs);
	}

	public void deleteAllPublicService() {
		delete(DB.TABLE_PUBLIC_SERVICE, null, null);
	}

	private void delete(String table, String whereClause, String[] whereArgs) {
		if (getDatabase().isOpen()) {
			try {
				getDatabase().delete(table, whereClause, whereArgs);
			} catch (Exception e) {
			}
		}
	}

	private Cursor getCursor(String sql) {
		return getDatabase().rawQuery(sql, null);
	}

	private Cursor getCursor(String sql, String[] selectionArgs) {
		return getDatabase().rawQuery(sql, selectionArgs);
	}

	private void beginTransaction() {
		getDatabase().beginTransaction();
	}

	private void endTransaction() {
		try {
			getDatabase().endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTransactionSuccessful() {
		getDatabase().setTransactionSuccessful();
	}

	private void log(String msg, Exception e) {
		Log.w(TAG, msg, e);
	}

	/**将组织架构的数据保存到数据库*/
	public void addDepts(List<DeptDetailInfo> deptDetailInfos) {
		beginTransaction();
		try {
			delete(DB.TABLE_DEFT_USER_UNREGISTER, null, null);
			for (DeptDetailInfo deptDetailInfo : deptDetailInfos) {
				insertDeptDetailInfo(deptDetailInfo, 0);
			}
			setTransactionSuccessful();
		} catch (Exception e) {

		} finally {
			endTransaction();
		}
	}

	private void insertDeptDetailInfo(DeptDetailInfo deptDetailInfo, long parentId) {

		ContentValues values = new ContentValues();
		values.put(DB.ID, deptDetailInfo.getId());
		values.put(DB.NAME, deptDetailInfo.getName());
		values.put(DB.PARENT_ID, parentId);
		values.put(DB.ORDER_INDEX, deptDetailInfo.getOrderIndex());
		insert(DB.TABLE_DEPT, values);
		values.clear();

		List<DeptDetailInfo> children = deptDetailInfo.getChildren();
		if (children != null) {
			for (DeptDetailInfo child : children) {
				insertDeptDetailInfo(child, deptDetailInfo.getId());
			}
		}

		List<DeptDetailUserInfo> members = deptDetailInfo.getMembers();
		if (members != null) {
			for (DeptDetailUserInfo member : members) {

				if (member.getStatus() == 3) {
					insertDeptUserDetailInfo(member, deptDetailInfo.getId());
				} else {
					insertUnregisterDetailInfo(member, deptDetailInfo.getId());
				}
			}
		}
	}

	private void insertUnregisterDetailInfo(DeptDetailUserInfo deptDetailUserInfo, long deptId) {
		ContentValues values = new ContentValues();
		values.put(DB.USERID, deptDetailUserInfo.getUserId());
		values.put(DB.POSITION, deptDetailUserInfo.getPosition());
		values.put(DB.DEPT_ID, deptId);
		values.put(DB.NAME, deptDetailUserInfo.getName());
		values.put(DB.EMAIL, deptDetailUserInfo.getEmail());
		values.put(DB.TEL, deptDetailUserInfo.getTel());
		values.put(DB.PHONE, deptDetailUserInfo.getPhone());
		values.put(DB.SEX, deptDetailUserInfo.getSex());
		values.put(DB.ORDER_INDEX, deptDetailUserInfo.getOrderIndex());
		values.put(DB.STATUS, deptDetailUserInfo.getStatus());
		values.put(DB.STATUS_DESC, deptDetailUserInfo.getStatusDesc());

		insert(DB.TABLE_DEFT_USER_UNREGISTER, values);
		values.clear();
	}

	private void insertDeptUserDetailInfo(DeptDetailUserInfo deptDetailUserInfo, long deptId) {
		ContentValues values = new ContentValues();
		values.put(DB.USERID, deptDetailUserInfo.getUserId());
		values.put(DB.POSITION, deptDetailUserInfo.getPosition());
		values.put(DB.DEPT_ID, deptId);
		values.put(DB.ORDER_INDEX, deptDetailUserInfo.getOrderIndex());
		values.put(DB.STATUS, deptDetailUserInfo.getStatus());
		values.put(DB.STATUS_DESC, deptDetailUserInfo.getStatusDesc());
		Log.e("abc",
				deptDetailUserInfo.getName() + "=" + deptDetailUserInfo.getOrderIndex() + "="
						+ deptDetailUserInfo.getStatus() + "=" + deptDetailUserInfo.getStatusDesc());
		insert(DB.TABLE_DEPT_USER, values);
		values.clear();
	}

	public List<DeptDetailInfo> getDeptDetailInfos() {
		Cursor cursor = getCursor("SELECT " + DB.ID + " , " + DB.NAME + " FROM " + DB.TABLE_DEPT + " WHERE "
				+ DB.PARENT_ID + "=0 " + "ORDER BY " +DB.ORDER_INDEX +" ASC");
		if (cursor != null) {
			try {
				List<DeptDetailInfo> deptDetailInfos = new ArrayList<DeptDetailInfo>();
				while (cursor.moveToNext()) {
					deptDetailInfos.add(getDeptDetailInfo(cursor, 0));
				}
				return deptDetailInfos;
			} finally {
				cursor.close();
			}
		}
		return null;
	}

	public DeptDetailInfo getDeptDetailInfo(Cursor cursor, long parentId) {
		long id = cursor.getLong(cursor.getColumnIndex(DB.ID));
		String name = cursor.getString(cursor.getColumnIndex(DB.NAME));
		DeptDetailInfo deptDetailInfo = new DeptDetailInfo();
		deptDetailInfo.setId(id);
		deptDetailInfo.setName(name);
		deptDetailInfo.setParentId(parentId);
		String sql = "SELECT " + DB.ID + " , " + DB.NAME + " FROM " + DB.TABLE_DEPT + " WHERE " + DB.PARENT_ID + "=? ORDER BY " +DB.ORDER_INDEX +" ASC";
		System.out.println(sql + id);

		Cursor childrenCursor = getCursor(sql, new String[] { String.valueOf(id) });
		if (childrenCursor != null) {
			try {
				List<DeptDetailInfo> deptDetailInfos = new ArrayList<DeptDetailInfo>();
				while (childrenCursor.moveToNext()) {
					deptDetailInfos.add(getDeptDetailInfo(childrenCursor, id));
				}
				deptDetailInfo.setChildren(deptDetailInfos);

			} finally {
				childrenCursor.close();
			}
		}
		Cursor membersCursor = getCursor("SELECT dept_user." + DB.ID + " , contact." + DB.USERID + " , contact."
				+ DB.NAME + " , contact." + DB.AVATAR + " , contact." + DB.PHONE + " , contact." + DB.TEL
				+ " , contact." + DB.EMAIL + " , contact." + DB.SEX + " , dept_user." + DB.POSITION + " , dept_user."
				+ DB.ORDER_INDEX + " , dept_user." + DB.STATUS + " , dept_user." + DB.STATUS_DESC + " FROM "
				+ DB.TABLE_DEPT_USER + " dept_user , " + DB.TABLE_CONTACT + " contact WHERE " + "contact." + DB.USERID
				+ "=dept_user." + DB.USERID + " AND " + "dept_user." + DB.DEPT_ID + "=?",
				new String[] { String.valueOf(id) });

		if (membersCursor != null) {
			try {
				List<DeptDetailUserInfo> deptDetailUserInfos = new ArrayList<DeptDetailUserInfo>();
				while (membersCursor.moveToNext()) {
					deptDetailUserInfos.add(getDeptDetailUserInfo(membersCursor, id));
				}
				deptDetailInfo.setMembers(deptDetailUserInfos);
				deptDetailInfo.sortMembers();
			} finally {
				membersCursor.close();
			}
		}

		Cursor unRegiestMembersCursor = getCursor("SELECT " + DB.ID + " , " + DB.USERID + " , " + DB.NAME + " , "
				+ DB.PHONE + " , " + DB.TEL + " , " + DB.EMAIL + " , " + DB.SEX + " , " + DB.POSITION + " , "
				+ DB.ORDER_INDEX + " , " + DB.STATUS + " , " + DB.STATUS_DESC + " FROM "
				+ DB.TABLE_DEFT_USER_UNREGISTER + " WHERE " + DB.DEPT_ID + "=?", new String[] { String.valueOf(id) });

		if (unRegiestMembersCursor != null) {
			try {
				List<DeptDetailUserInfo> deptDetailUserInfos = new ArrayList<DeptDetailUserInfo>();
				while (unRegiestMembersCursor.moveToNext()) {
					deptDetailUserInfos.add(getDeptDetailUserInfo(unRegiestMembersCursor, id));
				}
				if (deptDetailInfo.getMembers().size() > 0) {
					deptDetailInfo.getMembers().addAll(deptDetailUserInfos);
				} else {
					deptDetailInfo.setMembers(deptDetailUserInfos);
				}
			} finally {
				unRegiestMembersCursor.close();
			}
		}

		return deptDetailInfo;
	}

	private DeptDetailUserInfo getDeptDetailUserInfo(Cursor cursor, long parentId) {
		DeptDetailUserInfo deptDetailUserInfo = new DeptDetailUserInfo();
		deptDetailUserInfo.setUserId(cursor.getLong(cursor.getColumnIndex(DB.USERID)));
		deptDetailUserInfo.setName(cursor.getString(cursor.getColumnIndex(DB.NAME)));

		deptDetailUserInfo.setPosition(cursor.getString(cursor.getColumnIndex(DB.POSITION)));
		deptDetailUserInfo.setParentId(parentId);
		deptDetailUserInfo.setPhone(cursor.getString(cursor.getColumnIndex(DB.PHONE)));
		deptDetailUserInfo.setTel(cursor.getString(cursor.getColumnIndex(DB.TEL)));
		deptDetailUserInfo.setEmail(cursor.getString(cursor.getColumnIndex(DB.EMAIL)));
		deptDetailUserInfo.setSex(cursor.getInt(cursor.getColumnIndex(DB.SEX)));

		//TODO:
		deptDetailUserInfo.setOrderIndex(cursor.getInt(cursor.getColumnIndex(DB.ORDER_INDEX)));
		deptDetailUserInfo.setStatus(cursor.getInt(cursor.getColumnIndex(DB.STATUS)));
		deptDetailUserInfo.setStatusDesc(cursor.getString(cursor.getColumnIndex(DB.STATUS_DESC)));
		if (cursor.getInt(cursor.getColumnIndex(DB.STATUS)) == 3) {
			deptDetailUserInfo.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(DB.AVATAR)));
		} else {
			deptDetailUserInfo.setAvatar(null);
		}
		Log.e("abc", deptDetailUserInfo.toString() + "+++");
		return deptDetailUserInfo;
	}

	public void deleteDept() {
		delete(DB.TABLE_DEPT, null, null);
		delete(DB.TABLE_DEPT_USER, null, null);
	}

	public List<String> getUserDepartments(long userId) {
		System.out.println("getUserDepartments userid:" + userId);
		Cursor cursor = getCursor("SELECT " + DB.PARENT_ID + " FROM " + DB.TABLE_DEPT_USER + " WHERE " + DB.USERID
				+ "=?", new String[] { String.valueOf(userId) });
		List<String> deptNames = new ArrayList<String>();
		if (cursor != null) {
			try {
				while (cursor.moveToNext()) {
					int deptId = cursor.getInt(0);
					String deptName = getParent(deptId);
					if (!TextUtils.isEmpty(deptName)) {
						deptNames.add(deptName);
					}
				}
			} finally {
				cursor.close();
			}
		}
		return deptNames;
	}

	private String getParent(int deptId) {
		Cursor cursor = getCursor("SELECT " + DB.NAME + " , " + DB.PARENT_ID + " FROM " + DB.TABLE_DEPT + " WHERE "
				+ DB.ID + "=?", new String[] { String.valueOf(deptId) });
		try {
			if (cursor != null && cursor.moveToFirst()) {
				String name = cursor.getString(0);
				String parentName = null;
				try {
					int parentId = cursor.getInt(1);
					if (parentId != 0) {
						parentName = getParent(parentId);
					}
				} finally {
				}

				if (!TextUtils.isEmpty(parentName)) {
					return parentName + "/" + name;
				} else {
					return name;
				}

			}
		} finally {
			cursor.close();
		}
		return null;
	}

	public static ContactManager createContactManagerWithDBHelper(Context context, ContactDBHelper contactDBHelper) {
		ContactManager contactManager = new ContactManager(context, contactDBHelper);
		return contactManager;
	}

	public void release() {
		if (this != manager) {
			database.close();
			database = null;
		}
	}

	
	/** 根据用户的id删除联系人 */
	public void deleteContactById(long id){
		
	}
	
	/** 更新联系人 */
	public void updateContact(ContactInfo contactInfo){
		
	}
}
