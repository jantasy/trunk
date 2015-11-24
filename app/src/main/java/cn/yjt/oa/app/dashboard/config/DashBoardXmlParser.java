package cn.yjt.oa.app.dashboard.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardXmlParser {
	private static final String TAG = "DashBoardConfig";
	
	private static final String PKG_CURRENT = "current";
	
	private Context context;
	
	public DashBoardXmlParser(Context context) {
		this.context = context;
	}
	
	public ArrayList<DashBoardItem> parseStream(InputStream stream) {

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
                    	items.add(parseItem(parser));
                    }else if("include".equals(tag)){
                    	ArrayList<DashBoardItem> include = parseInclude(parser);
                    	if(include != null){
                    		items.addAll(include);
                    	}
                    }
                }
            } while (type != XmlPullParser.END_DOCUMENT);
        } catch (Exception e) {
        	Log.w(TAG, "failed parsing widget settings", e);
        }
        return items;
    }

	private ArrayList<DashBoardItem> parseInclude(XmlPullParser parser) {
		String xml = parser.getAttributeValue(null, "xml");
		try {
			ArrayList<DashBoardItem> interanlItems = parseStream(context.getAssets().open(xml+".xml"));
			return interanlItems;
		} catch (IOException e1) {
			Log.w(TAG, "Failed to load state from assets.", e1);
		}
		return null;
	}

	private DashBoardItem parseItem( XmlPullParser parser) {
		String pkg = parser.getAttributeValue(null, "p");
		String cl = parser.getAttributeValue(null, "c");
		String icon = parser.getAttributeValue(null, "i");
		String title = parser.getAttributeValue(null, "t");
		String description = parser.getAttributeValue(null, "d");
		int sdkVersion = 0;
		if(parser.getAttributeValue(null,"v")!=null){
			sdkVersion = Integer.parseInt(parser.getAttributeValue(null,"v"));
		}
		
		String requirePermission = parser.getAttributeValue(null,"require_permission");
		int id = 0;
		try {
			id = Integer.parseInt(parser.getAttributeValue(null,"id"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int state = DashBoardItem.STATUS_NORMAL;
		try {
			state = Integer.parseInt(parser.getAttributeValue(null,"state"));
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
		item.setSdkVersion(sdkVersion);
		item.setStatus(state);
		title = item.getTitle(context);
		if(id == 0 && !TextUtils.isEmpty(title)){
			item.setId(getIdWithTitle(title));
		}
		
		return item;
	}
	
	private int getIdWithTitle(String title){
		int id = 0;
		if(title.equals("电子公告")){
			id= 1;
    	}else if(title.equals("任务待办")){
    		id= 2;
    	}else if(title.equals("考勤记录")){
    		id= 3;
    	}else if(title.equals("定位考勤")){
    		id= 4;
    	}else if(title.equals("扫码考勤")){
    		id= 5;
    	}else if(title.equals("消费记录")){
    		id= 6;
    	}else if(title.equals("电话会议")){
    		id= 7;
    	}else if(title.equals("企业管理")){
    		id= 8;
    	}else if(title.equals("自助充值")){
    		id= 9;
    	}else if(title.equals("蓝牙考勤")){
    		id = 10;
    	}
		return id;
	}
}
