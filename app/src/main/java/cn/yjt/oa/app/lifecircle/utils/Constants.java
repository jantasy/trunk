package cn.yjt.oa.app.lifecircle.utils;

import java.util.ArrayList;
import android.content.Context;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.lifecircle.model.Netable;

public class Constants { 
	
	public static String PRO_IP = "";   
//	public static String YJT_IP = "112.124.45.47:9912";  
    public static String PRO_NAME;
    public static Netable areaInfo = new Netable();
    public static int business;
    public static String city;
    public static ArrayList<ArrayList<Netable>> cityList = new ArrayList();
    public static int defaultOrderPress;
    public static int defaultPress = 100;
    public static ArrayList<Netable> juliList = new ArrayList();
    public static Netable juliNetable = new Netable();
    public static double latitude;
    public static String locationCity;
    public static String locationProvince;
    public static double longitude;    
    public static ArrayList<Netable> proList = new ArrayList();
    public static int pushcount;
    public static int typeFrom;
    public static Netable typeInfo = new Netable();
    public static String addrStr;	
    public static Netable area = new Netable();
    public static Netable firstTypes = new Netable();
    public static boolean isLocationCurrentCityEqueals = false;
    public static int FLAG = 0;
    
    public static Netable getArea() {
        return area;
    }

    public static Netable getFirstTypes() {
        return firstTypes;
    }

    public static void setArea(Netable netable) {
        area = netable;
    }

    public static void setFirstTypes(Netable netable) {
        firstTypes = netable;
    }
    
    public static ArrayList<Netable> getProList() {
        return proList;
    }

    public static void setProList(ArrayList<Netable> arrayList) {
        proList = arrayList;
    }

    
    public static String choose(Context context, String str) {
        String simCityName = null;
        String[] stringArray = context.getResources().getStringArray(R.array.fullname);
        
        int i;
        for(i = 0; i < stringArray.length; i++) {
            if (str.equals(stringArray[i])) {
                break;
            }
        }

        if (i < stringArray.length) {
        	simCityName = context.getResources().getStringArray(R.array.simplename)[i];
        } else {
        	simCityName = str;
        }
        return simCityName;
    }
	
}