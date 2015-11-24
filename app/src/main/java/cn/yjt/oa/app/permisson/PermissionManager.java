package cn.yjt.oa.app.permisson;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

public class PermissionManager {

	//user permission is a binary number,permission code is the position in the binary number. 
	public static final int PERMISSION_CODE_MEMBER_LIST = 0;
	public static final int PERMISSION_CODE_SEND_NOTICE = 1;
	public static final int PERMISSION_CODE_MODIFY_ENTERPRISE_INFO = 2;
	public static final int PERMISSION_CODE_DEL_NOTICE = 3;
	public static final int PERMISSION_CODE_ATTENDANCE_SETTINGS = 4;
	public static final int PERMISSION_CODE_INDUSTRY_TAGS = 5;
	public static final int PERMISSION_CODE_ATTENDANCE_REPORT = 6;
    public static final int PERMISSION_CODE_INSPECT_CONFIG = 7;
    public static final int PERMISSION_CODE_GET_INSPECT_REPORT = 8;
    public static final int PERMISSION_CODE_INSPECT = 9;

	public static final String PERMISSION_NAME_MEMEBER_LIST = "member_list";
	public static final String PERMISSION_NAME_SEND_NOTICE = "send_notice";
	public static final String PERMISSION_NAME_MODIFY_ENTERPRISE_INFO = "modify_enterprise_info";
	public static final String PERMISSION_NAME_DEL_NOTICE = "del_notice";
	public static final String PERMISSION_NAME_ATTENDANCE_SETTINGS = "attendance_settings";
	public static final String PERMISSION_NAME_INDUSTRY_TAGS = "industry_tags";
	public static final String PERMISSION_NAME_ATTENDANCE_REPORT = "attendance_report";
    public static final String PERMISSION_NAME_INSPECT_CONFIG = "inspect_config";
    public static final String PERMISSION_NAME_GET_INSPECT_REPORT = "get_inspect_report";
    public static final String PERMISSION_NAME_INSPECT = "inspect";

	private static final Map<String, Integer> permissionMap = new HashMap<String, Integer>();

	static {
		permissionMap.put(PERMISSION_NAME_MEMEBER_LIST,
				PERMISSION_CODE_MEMBER_LIST);
		permissionMap.put(PERMISSION_NAME_SEND_NOTICE,
				PERMISSION_CODE_SEND_NOTICE);
		permissionMap.put(PERMISSION_NAME_MODIFY_ENTERPRISE_INFO,
				PERMISSION_CODE_MODIFY_ENTERPRISE_INFO);
		permissionMap.put(PERMISSION_NAME_DEL_NOTICE, PERMISSION_CODE_DEL_NOTICE);
		permissionMap.put(PERMISSION_NAME_ATTENDANCE_SETTINGS, PERMISSION_CODE_ATTENDANCE_SETTINGS);
		permissionMap.put(PERMISSION_NAME_INDUSTRY_TAGS, PERMISSION_CODE_INDUSTRY_TAGS);
		permissionMap.put(PERMISSION_NAME_ATTENDANCE_REPORT, PERMISSION_CODE_ATTENDANCE_REPORT);
        permissionMap.put(PERMISSION_NAME_INSPECT_CONFIG, PERMISSION_CODE_INSPECT_CONFIG);
        permissionMap.put(PERMISSION_NAME_GET_INSPECT_REPORT, PERMISSION_CODE_GET_INSPECT_REPORT);
        permissionMap.put(PERMISSION_NAME_INSPECT, PERMISSION_CODE_INSPECT);

	}

	/**
	 * In this version,just consider permission string's size less than 32.So
	 * don not handle large length permission.
	 * 
	 * @param permission It is a hex string
	 * @param requirePermission Permissions compounded with "|"
	 * @return
	 */
	public static boolean verify(String permission, String requirePermission) {
		if(TextUtils.isEmpty(requirePermission)){
			return true;
		}
		
		if(permission == null ){
			return false;
		}
		int requirePermissionInt = parsePermission(requirePermission);
		return verify(permission, requirePermissionInt);
	}
	
	private static boolean verify(String permission,int requirePermission){
		if(permission == null){
			return false;
		}
		if (permission.length() > 32) {
			permission = permission.substring(permission.length() - 32);
		}
		int permissionInt = Integer.parseInt(permission, 16);
		int requirePermissionInt = requirePermission;
		return (permissionInt & requirePermissionInt) != 0;
	}
	
	/**
	 * 
	 * @param permission hex string
	 * @param requirePermissionCode {@link PermissionManager#PERMISSION_CODE_DEL_NOTICE}
	 * @return
	 */
	public static boolean verifyCode(String permission,int requirePermissionCode){
		if(permission == null){
			return false;
		}
		if (permission.length() > 32) {
			permission = permission.substring(permission.length() - 32);
		}
		int permissionInt = Integer.parseInt(permission, 16);
		int requirePermissionInt = 1 << requirePermissionCode;
		return (permissionInt & requirePermissionInt) != 0;
	}
	

	/**
	 * Permissions can be compounded with "|"
	 * @param permissions
	 * @return 
	 */
	public static int parsePermission(String permissions) {
		if(TextUtils.isEmpty(permissions)){
			return 0;
		}
		String[] permissionArray = permissions.split("\\|");
		int permission = 0;
		for (int i = 0; i < permissionArray.length; i++) {
			String permissionName = permissionArray[i];
			if(permissionMap.containsKey(permissionName)){
				int permissionCode = permissionMap.get(permissionName);
				permission += (1 << permissionCode);
			}
		}
		return permission;
	}

}
