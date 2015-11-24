package cn.yjt.oa.app.find.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

public class FinderConfig {
	
	private static final String SCHEME_RESOURCE = "res://";

	private static FinderConfig instance;
	
	private Context context;
	private List<List<FinderItem>> groups;
	
	public FinderConfig(Context context) {
		this.context = context;
	}
	
	public static FinderConfig getInstance(Context context){
		if(instance == null){
			instance = new FinderConfig(context);
		}
		return instance;
	}

	public void load() {
		XmlPullParser xmlParser = Xml.newPullParser();
		List<List<FinderItem>> groups = null;
		List<FinderItem> group = null;
		FinderItem item = null;
		try {
			InputStream in = context.getAssets().open("finder.xml");
			xmlParser.setInput(in, "utf-8");
			int tag;
			while ((tag = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
				String name = xmlParser.getName();
				switch (tag) {
				case XmlPullParser.START_TAG:
					if (isGroupsTag(name)) {
						groups = createGroups();
					} else if (isGroupTag(name)) {
						group = createGroup();
					} else if (isItemTag(name)) {
						item = createItem(xmlParser);
					}
					break;
				case XmlPullParser.END_TAG:
					if (isGroupsTag(name)) {
						this.groups = groups;
					} else if (isGroupTag(name)) {
						addGroupToGroups(groups, group);
					} else if (isItemTag(name)) {
						addItemToGroup(group, item);
					}
					break;

				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
	public List<List<FinderItem>> getGroups() {
		return groups;
	}

	private boolean isItemTag(String tag) {
		return "item".equals(tag);
	}

	private boolean isGroupTag(String tag) {
		return "group".equals(tag);
	}

	private boolean isGroupsTag(String tag) {
		return "groups".equals(tag);
	}

	private List<List<FinderItem>> createGroups() {
		return new ArrayList<List<FinderItem>>();
	}

	private List<FinderItem> createGroup() {
		return new ArrayList<FinderItem>();
	}
	
	private void addItemToGroup(List<FinderItem> group,FinderItem item){
		group.add(item);
	}
	
	private void addGroupToGroups(List<List<FinderItem>> groups,List<FinderItem> group){
		groups.add(group);
	}

	private FinderItem createItem(XmlPullParser parser){
		String label = parser.getAttributeValue(null, "label");
		String icon = parser.getAttributeValue(null, "icon");
		String intent = parser.getAttributeValue(null, "intent");
		String url = parser.getAttributeValue(null, "url");
		
		return new FinderItem(getResourceId(label), getResourceId(icon), intent, url);
	}

	private int getResourceId(String res){
		if(res.startsWith(SCHEME_RESOURCE)){
			String name = res.substring(res.lastIndexOf("/")+1);
			String type = res.substring(SCHEME_RESOURCE.length(), res.lastIndexOf("/"));
			return context.getResources().getIdentifier(name, type, context.getPackageName());
		}
		return -1;
	}
}
