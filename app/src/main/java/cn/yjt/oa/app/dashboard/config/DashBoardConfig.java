package cn.yjt.oa.app.dashboard.config;

import io.luobo.common.json.TypeToken;
import io.luobo.common.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.util.AtomicFile;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import cn.yjt.oa.app.beans.DashBoardItem;
import cn.yjt.oa.app.permisson.PermissionManager;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.utils.SharedUtils;

import com.google.gson.Gson;

/**
 * 
 * @author qinzhe
 *
 * save into xml
 * <items>
 * 		<item p="cn.yjt.oa.app" c=".MainActivity" i="cn.yjt.oa.app:drawable/icon" t="翼机通" />
 * </items>
 */
public class DashBoardConfig {
	
	private static DashBoardConfig sInstance;
	private static final String DASHBOARD_FILE = "dashboard.xml";
	private static final String HIDE_DASHBOARD_FILE = "hide_dashboard.xml";
	private static final String TAG = "DashBoardConfig";
	
	private static final String PKG_CURRENT = "current";
	
	private Context context;
	private ArrayList<DashBoardItem> internalDashboardItems = new ArrayList<DashBoardItem>();
	
	private DashBoardConfig(Context context) {
		this.context = context;
	}
	
	private static DashBoardConfig getInstance(Context context) {
		if (sInstance == null)
			sInstance = new DashBoardConfig(context);
		
		return sInstance;
	}

	public synchronized static List<DashBoardItem> load(Context context,String userPermission) {
		List<DashBoardItem> loadSettings = getInstance(context).loadDashBoard();
		filtDashBoardItemWithPermission(userPermission, loadSettings);
		return loadSettings;
	}
	public synchronized static List<DashBoardItem> load(Context context,String userPermission,List<DashBoardItem> dashConfig) {
		List<DashBoardItem> loadSettings=null;
		if(dashConfig!=null && dashConfig.size()>0){
			loadSettings = getInstance(context).loadDashBoard(dashConfig);
		}else{
			loadSettings = getInstance(context).loadDashBoard();
		}
		 
		filtDashBoardItemWithPermission(userPermission, loadSettings);
		return loadSettings;
	}
	
	public synchronized static void save(Context context, Collection<DashBoardItem> items,String userPermission) {
		DashBoardConfig boardConfig = getInstance(context);
		boardConfig.saveDashItemsToFile(items,boardConfig.getDashBoardFile());
		ArrayList<DashBoardItem> hide = getInternalHide(context,items, boardConfig);
		filtDashBoardItemWithPermission(userPermission, hide);
		boardConfig.saveDashItemsToFile(hide,boardConfig.getHideDashBoardFile());
	}

	
	private static ArrayList<DashBoardItem> getInternalHide(Context context,
			Collection<DashBoardItem> items, DashBoardConfig boardConfig) {
		List<DashBoardItem> dashConfig = null;
		ArrayList<DashBoardItem> internal = boardConfig.getInternal();
		String dashConfigStr = SharedUtils.getDashBoardConfig(context).getString(Config.DASH_CONFIG, null);
		if(!TextUtils.isEmpty(dashConfigStr)){
			dashConfig=new Gson().fromJson(dashConfigStr,  new TypeToken<List<DashBoardItem>>(){}.getType());
		}
		ArrayList<DashBoardItem> hide = new ArrayList<DashBoardItem>();
		for (DashBoardItem dashBoardItem : internal) {
			if(!items.contains(dashBoardItem)){
				if(dashConfig!=null &&dashConfig.contains(dashBoardItem) && dashConfig.get(dashConfig.indexOf(dashBoardItem)).getStatus()==0){
					hide.add(dashBoardItem);
				}else if(dashConfig==null || dashConfig.size()==1){
					hide.add(dashBoardItem);
				}
				
			}
		}
		return hide;
	}
	private static ArrayList<DashBoardItem> getInternalHide(Collection<DashBoardItem> items, DashBoardConfig boardConfig) {
		ArrayList<DashBoardItem> internal = boardConfig.getInternal();
		ArrayList<DashBoardItem> hide = new ArrayList<DashBoardItem>();
		for (DashBoardItem dashBoardItem : internal) {
			if(!items.contains(dashBoardItem)){
				hide.add(dashBoardItem);
			}
		}
		return hide;
	}
	

	private static void filtDashBoardItemWithPermission(String userPermission,
			Collection<DashBoardItem> dashBoardItem) {
		Iterator<DashBoardItem> iterator = dashBoardItem.iterator();
		while(iterator.hasNext()){
			DashBoardItem boardItem = iterator.next();
			boolean isVerify = PermissionManager.verify(userPermission, boardItem.getRequirePermission());
			if(!isVerify){
				iterator.remove();
			}
		}
	}
	
	public static ArrayList<DashBoardItem> getHidedDashboardItem(Context context, Collection<DashBoardItem> showing,String userPermission) {
		ArrayList<DashBoardItem> hided = new ArrayList<DashBoardItem>();
		ArrayList<DashBoardItem> internal = getInstance(context).getInternal();
		List<DashBoardItem> dashConfig = null;
		String dashConfigStr = SharedUtils.getDashBoardConfig(context).getString(Config.DASH_CONFIG, null);
		if (!TextUtils.isEmpty(dashConfigStr)) {
			dashConfig = new Gson().fromJson(dashConfigStr,new TypeToken<List<DashBoardItem>>() {}.getType());
		}
		filtDashBoardItemWithPermission(userPermission, internal);
		for (DashBoardItem item : internal) {
			if (!showing.contains(item)) {
				if (dashConfig!=null && dashConfig.contains(item)&& dashConfig.get(dashConfig.indexOf(item)).getStatus() == 0) {
					hided.add(item);
				}else if(dashConfig==null || dashConfig.size()==1){
					hided.add(item);
				}

			}
		}
		
		return hided;
	}
	
	private File getDashBoardFile() {
		return FileUtils.createFileInUserFolder(DASHBOARD_FILE);
	}
	private File getHideDashBoardFile() {
		return FileUtils.createFileInUserFolder(HIDE_DASHBOARD_FILE);
	}
	
	private AtomicFile getSettingFile(File file) {
		return new AtomicFile(file);
	}
	
	private ArrayList<DashBoardItem> getInternal() {
		synchronized (internalDashboardItems) {
			if (internalDashboardItems.isEmpty()) {
				try {
					InputStream stream = context.getAssets().open("dashboard_internal_all.xml");
					internalDashboardItems = readSettngsFromStream(stream);
				} catch (IOException e1) {
					Log.w(TAG, "Failed to load state from assets.", e1);
				}
			}
		}
		
		return internalDashboardItems;
	}
	
	private boolean isInternal(DashBoardItem boardItem){
		if(context.getPackageName().equals(boardItem.getPackageName())){
			return true;
		}
		return false;
	}
	
	//Load items in dashboard,every user has a different dashboard.These items is loaded from user folder in /sdcard/yijitong/. 
	//When there exist a old item witch has been deleted this version,it will be deleted from user dashboard data.
	//When there exist a new item witch user dashboard doesn'th contain, it will be add to user dashboard data.
	private List<DashBoardItem> loadDashBoard() {
		try {
			List<DashBoardItem> loadDashBoardItems = loadDashBoardItems(getDashBoardFile());
			synchronizeInternalAndUserSettings(loadDashBoardItems);
			return loadDashBoardItems;
		} catch (FileNotFoundException e) {
			Log.d(TAG, "load state from file error, now read from assets");
			return getDashConfig();
		}
	}
	private List<DashBoardItem> loadDashBoard(List<DashBoardItem> dashConfig) {
		try {
			List<DashBoardItem> loadDashBoardItems = loadDashBoardItems(getDashBoardFile());
			synchronizeInternalAndUserSettings(loadDashBoardItems,dashConfig);
			return loadDashBoardItems;
		} catch (FileNotFoundException e) {
			Log.d(TAG, "load state from file error, now read from assets");
			synchronizeInternal(getInternal(), dashConfig);
			return dashConfig;
		}
	}
	
	//Load items from a xml file.
	private List<DashBoardItem> loadDashBoardItems(File file)
			throws FileNotFoundException {
		FileInputStream stream = getSettingFile(file).openRead();
		try {
			return readSettngsFromStream(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}

	}
	
	// get dashborad items from /assets/dashboard.xml/
	private ArrayList<DashBoardItem> getDashConfig(){
		try {
			InputStream stream = context.getAssets().open("dashboard.xml");
			return readSettngsFromStream(stream);
		} catch (IOException e1) {
			Log.w(TAG, "Failed to load state from assets.", e1);
		}
		return null;
	}
	
	private void synchronizeInternalAndUserSettings(List<DashBoardItem> boardItems){
		ArrayList<DashBoardItem> internal = getInternal();
		ArrayList<DashBoardItem> dashConfig = getDashConfig();
		Iterator<DashBoardItem> iterator = boardItems.iterator();
		List<DashBoardItem> hide = Collections.emptyList();
		try {
			hide = loadDashBoardItems(getHideDashBoardFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//remove deleted item
		while(iterator.hasNext()){
			DashBoardItem next = iterator.next();
			if(isInternal(next)&&!internal.contains(next)){
				iterator.remove();
			}
		}
		
		//add new item. Witch is new? The item witch is not contained by user dashboard data or hide data.
		for (DashBoardItem dashBoardItem : dashConfig) {
			if (!hide.contains(dashBoardItem) && !boardItems.contains(dashBoardItem)) {
				boardItems.add(dashBoardItem);
			}
		}
	}
	private void synchronizeInternalAndUserSettings(List<DashBoardItem> boardItems,List<DashBoardItem> dashConfig){
		ArrayList<DashBoardItem> internal = getInternal();
		dashConfig=synchronizeInternal(internal, dashConfig);
		SharedPreferences sp = SharedUtils.getDashBoardConfig(context);
		sp.edit().putString(Config.DASH_CONFIG, new Gson().toJson(dashConfig)).commit();
		
//		ArrayList<DashBoardItem> dashConfig = getDashConfig();
		Iterator<DashBoardItem> iterator = boardItems.iterator();
		List<DashBoardItem> hide = Collections.emptyList();
		try {
			hide = loadDashBoardItems(getHideDashBoardFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//remove deleted item
		while(iterator.hasNext()){
			DashBoardItem next = iterator.next();
			if(isInternal(next)&&!internal.contains(next)){
				iterator.remove();
			}
		}
		for (DashBoardItem dashBoardItem : dashConfig) {
			if (!hide.contains(dashBoardItem) && !boardItems.contains(dashBoardItem)&& dashBoardItem.getStatus()==0) {
				boardItems.add(dashBoardItem);
			}else if(boardItems.contains(dashBoardItem) && dashBoardItem.getStatus()==1){
				boardItems.remove(dashBoardItem);
			}
		}
		
	}

	private List<DashBoardItem> synchronizeInternal(List<DashBoardItem> internal,List<DashBoardItem> dashConfig) {
		dashConfig.add(0, internal.get(0));
		for (DashBoardItem boardItem : internal) {
			for (int i = 0; i < dashConfig.size(); i++) {
				if (boardItem.getId() == dashConfig.get(i).getId()) {
					dashConfig.get(i).setPackageName(boardItem.getPackageName());
					dashConfig.get(i).setClassName(boardItem.getClassName());
					dashConfig.get(i).setTitle(boardItem.getTitle());
					dashConfig.get(i).setIconResUri(boardItem.getIconResUri());
					dashConfig.get(i).setInstalled(boardItem.isInstalled());
					dashConfig.get(i).setDescription(boardItem.getDescription());
					dashConfig.get(i).setRequirePermission(boardItem.getRequirePermission());
					continue;
				}
			}
		}
		return dashConfig;
	}
	
	private void saveDashItemsToFile(Collection<DashBoardItem> items, File file) {
		AtomicFile settingFile = getSettingFile(file);
		FileOutputStream stream;
        try {
            stream = settingFile.startWrite();
            try {
            	writeSettngsToStream(stream, items);
            	settingFile.finishWrite(stream);
            } catch (Exception e) {
            	settingFile.failWrite(stream);
                Log.w(TAG, "Failed to save state, restoring backup.", e);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed open state file for write: " + e);
        }
    }
	
	
	
	private ArrayList<DashBoardItem> readSettngsFromStream(InputStream stream) {

		ArrayList<DashBoardItem> items = new ArrayList<DashBoardItem>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, null);

            int type;
            do {
                type = parser.next();
                if (type == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    if ("item".equals(tag)) {
                        String pkg = parser.getAttributeValue(null, "p");
                        String cl = parser.getAttributeValue(null, "c");
                        String icon = parser.getAttributeValue(null, "i");
                        String title = parser.getAttributeValue(null, "t");
                        String description = parser.getAttributeValue(null, "d");
                        String requirePermission = parser.getAttributeValue(null,"require_permission");
                        int id = 0;
						try {
							id = Integer.parseInt(parser.getAttributeValue(null,"id"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
                        
                        if (PKG_CURRENT.equals(pkg)) {
                        	pkg = context.getPackageName();
                        }
                        if (!TextUtils.isEmpty(cl) && cl.charAt(0) == '.') {
                        	cl = pkg+cl;
                        }
                        boolean installed = false;
                        final PackageManager packageManager = context.getPackageManager();
                        try {
                        	packageManager.getPackageInfo(pkg, 0);
                        	installed = true;
                        } catch (PackageManager.NameNotFoundException e) {
                        	Log.d(TAG, "pkg "+pkg+" not installed");
                        }
                        
                        DashBoardItem item = new DashBoardItem();
                        item.setPackageName(pkg);
                        item.setClassName(cl);
                        item.setTitle(title);
                        item.setIconResUri(icon);
                        item.setInstalled(installed);
                        item.setDescription(description);
                        item.setRequirePermission(requirePermission);
                        item.setId(id);
                        title = item.getTitle(context);
                        if(id == 0 && !TextUtils.isEmpty(title)){
                        	if(title.equals("电子公告")){
                        		item.setId(1);
                        	}else if(title.equals("任务待办")){
                        		item.setId(2);
                        	}else if(title.equals("考勤记录")){
                        		item.setId(3);
                        	}else if(title.equals("定位考勤")){
                        		item.setId(4);
                        	}else if(title.equals("扫码考勤")){
                        		item.setId(5);
                        	}else if(title.equals("消费记录")){
                        		item.setId(6);
                        	}else if(title.equals("电话会议")){
                        		item.setId(7);
                        	}else if(title.equals("企业管理")){
                        		item.setId(8);
                        	}else if(title.equals("自助充值")){
                        		item.setId(9);
                        	}
                        	
                        }
                        
                        items.add(item);
                    }else if("include".equals(tag)){
                    	String xml = parser.getAttributeValue(null, "xml");
                    	try {
            				ArrayList<DashBoardItem> interanlItems = readSettngsFromStream(context.getAssets().open(xml+".xml"));
            				items.addAll(interanlItems);
            			} catch (IOException e1) {
            				Log.w(TAG, "Failed to load state from assets.", e1);
            			}
                    }
                }
            } while (type != XmlPullParser.END_DOCUMENT);
        } catch (Exception e) {
        	Log.w(TAG, "failed parsing widget settings", e);
        }
        
        return items;
    }
	
	private void writeSettngsToStream(FileOutputStream stream, Collection<DashBoardItem> items) throws Exception {
		XmlSerializer out = Xml.newSerializer();
        out.setOutput(stream, "utf-8");
        out.startDocument(null, true);

        
        out.startTag(null, "items");
        
        for (DashBoardItem item:items) {
        	out.startTag(null, "item");
        	
        	if (item.getPackageName() != null) {
        		String currentPkg = context.getPackageName();
        		if (currentPkg.equals(item.getPackageName()))
        			out.attribute(null, "p", PKG_CURRENT);
        		else
        			out.attribute(null, "p", item.getPackageName());
        	}
        	if (item.getClassName() != null)
        		out.attribute(null, "c", item.getClassName());
        	if (item.getIconResUri() != null)
        		out.attribute(null, "i", item.getIconResUri());
        	if (item.getTitle() != null)
        		out.attribute(null, "t", item.getTitle());
        	if (item.getDescription() != null)
        		out.attribute(null, "d", item.getDescription());
        	/*
        	 *  require_permission="member_list|send_notice|modify_enterprise_info"
        	 */
        	if(item.getRequirePermission() != null){
        		out.attribute(null, "require_permission", item.getRequirePermission());
        	}
        	
        	out.attribute(null, "id", String.valueOf(item.getId()));
            
            out.endTag(null, "item");
        }

        out.endTag(null, "items");
        out.endDocument();
    }
}
