package cn.yjt.oa.app.dashboard.config;

import io.luobo.common.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import android.support.v4.util.AtomicFile;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.DashBoardItem;
import cn.yjt.oa.app.utils.FileUtils;

public class DashBoardHelper {

	static final String DASHBOARD_FILE = "dashboard.xml";
	static final String HIDE_DASHBOARD_FILE = "hide_dashboard.xml";
	static final String SERVER_DASHBOARD_FILE = "server_dashboard.xml";

	public static boolean isYjtDashBoardItem(DashBoardItem boardItem) {
		if (MainApplication.getAppContext().getPackageName()
				.equals(boardItem.getPackageName())) {
			return true;
		}
		return false;
	}

	public static List<DashBoardItem> getDefaultDashBoard() {
		InputStream stream = null;
		try {
			stream = MainApplication.getAppContext().getAssets()
					.open(DASHBOARD_FILE);
			DashBoardXmlParser dashBoardXmlParser = new DashBoardXmlParser(
					MainApplication.getAppContext());
			return dashBoardXmlParser.parseStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	public static List<DashBoardItem> getUserDashBoard() {
		return getDashBoardFromFile(getUserDashBoardFile());
	}

	public static List<DashBoardItem> getUserHideDashBoard() {
		return getDashBoardFromFile(getUserHideDashBoardFile());
	}
	
	public static List<DashBoardItem> getServerDashBoard(){
		return getDashBoardFromFile(getUserServerDashBoardFile());
	}

	public static void save(List<DashBoardItem> displayedItems,
			List<DashBoardItem> hideItems) {
		saveToFile(displayedItems, getUserDashBoardFile());
		saveToFile(hideItems, getUserHideDashBoardFile());
	}
	
	public static void saveServerConfig(List<DashBoardItem> serverConfig){
		saveToFile(serverConfig, getUserServerDashBoardFile());
	}

	private static void saveToFile(List<DashBoardItem> items, File file) {
		if(items == null){
			items = Collections.emptyList();
		}
		DashBoardXmlConvertor boardXmlConvertor = new DashBoardXmlConvertor(
				MainApplication.getAppContext());
		AtomicFile settingFile = new AtomicFile(file);
		FileOutputStream stream = null;
		try {
			stream = settingFile.startWrite();
			boardXmlConvertor.writeSettngsToStream(stream, items);
			settingFile.finishWrite(stream);
		} catch (Exception e) {
			if (stream != null) {
				settingFile.failWrite(stream);
			}
		} 
	}

	private static List<DashBoardItem> getDashBoardFromFile(File file) {
		FileInputStream stream = null;
		try {
			stream = new AtomicFile(file).openRead();
			DashBoardXmlParser dashBoardXmlParser = new DashBoardXmlParser(
					MainApplication.getAppContext());
			return dashBoardXmlParser.parseStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	private static File getUserDashBoardFile() {
		return FileUtils.createFileInUserFolder(DASHBOARD_FILE);
	}

	private static File getUserHideDashBoardFile() {
		File createFileInUserFolder = FileUtils.createFileInUserFolder(HIDE_DASHBOARD_FILE);
		System.out.println("getUserHideDashBoardFile:"+createFileInUserFolder);
		return createFileInUserFolder;
	}
	
	private static File getUserServerDashBoardFile() {
		return FileUtils.createFileInUserFolder(SERVER_DASHBOARD_FILE);
	}
}
