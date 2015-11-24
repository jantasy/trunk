package cn.yjt.oa.app.nfctools.operation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

public class NfcTagOperatorConfig {

	private static NfcTagOperatorConfig singleton;
	private Context context;

	private List<NfcTagOperatorGroup> operatorGroups;
	private NfcTagOperatorGroup operatorGroup;
	private NfcTagOperator operator;
	private NfcTagOperation operation;
	private int defaultOperationId;
	private List<NfcTagOperation> nfcTagOperations = new LinkedList<NfcTagOperation>();

	public NfcTagOperatorConfig(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @param context Suggest a application context.
	 * @return
	 */
	public static synchronized NfcTagOperatorConfig getInstance(Context context) {
		if (singleton == null) {
			singleton = new NfcTagOperatorConfig(context);
		}
		return singleton;
	}

	public synchronized List<NfcTagOperatorGroup> getOperatorGroups() {
		List<NfcTagOperatorGroup> groups = new ArrayList<NfcTagOperatorGroup>();
		groups.addAll(operatorGroups);
		Iterator<NfcTagOperatorGroup> iterator = groups.iterator();
		while(iterator.hasNext()){
			NfcTagOperatorGroup group = iterator.next();
			if(group.isHide()){
				iterator.remove();
			}
		}
		return groups;
	}
	
	public synchronized void load(String assetFileName) {
		XmlPullParser xmlParser = Xml.newPullParser();
		InputStream in = null;
		try {
			in = context.getAssets().open(assetFileName);
			xmlParser.setInput(in, "utf-8");
			int tag;
			while ((tag = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (tag) {
				case XmlPullParser.START_TAG:
					onTagStart(xmlParser);
					break;
				case XmlPullParser.END_TAG:
					onTagEnd(xmlParser);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public NfcTagOperation findNfcTagOperationWithOperationId(int operationId){
		System.out.println("findNfcTagOperationWithOperationId:"+Integer.valueOf(operationId));
		for (NfcTagOperation nfcTagOperation : nfcTagOperations) {
			if(operationId == nfcTagOperation.getOperationId()){
				return nfcTagOperation;
			}
		}
		return null;
	}

	public NfcTagOperation findNfcTagOperationWithOperationId(byte[] operationId) {
		return findNfcTagOperationWithOperationId((operationId[0] << 8) + operationId[1]);
	}

	private void onTagStart(XmlPullParser xmlParser) {
		String name = xmlParser.getName();
		if ("groups".equals(name)) {
			onGroupsStart(xmlParser);
		} else if ("group".equals(name)) {
			onGroupStart(xmlParser);
		} else if ("operator".equals(name)) {
			onOperatorStart(xmlParser);
		} else if ("operation".equals(name)) {
			onOperationStart(xmlParser);
		}
	}

	private void onTagEnd(XmlPullParser xmlParser) {
		String name = xmlParser.getName();
		if ("groups".equals(name)) {
			onGroupsEnd(xmlParser);
		} else if ("group".equals(name)) {
			onGroupEnd(xmlParser);
		} else if ("operator".equals(name)) {
			onOperatorEnd(xmlParser);
		} else if ("operation".equals(name)) {
			onOperationEnd(xmlParser);
		}
	}

	private void onGroupsStart(XmlPullParser xmlParser) {
		operatorGroups = new ArrayList<NfcTagOperatorGroup>();
	}

	private void onGroupStart(XmlPullParser xmlParser) {
		operatorGroup = new NfcTagOperatorGroup();
		operatorGroup.setGroupName(xmlParser.getAttributeValue(null, "name"));
		String hide = xmlParser.getAttributeValue(null, "hide");
		if(hide != null){
			operatorGroup.setHide(Boolean.parseBoolean(hide));
		}
	}

	private void onOperatorStart(XmlPullParser xmlParser) {
		operator = new NfcTagOperator();
		operator.setOperatorName(xmlParser.getAttributeValue(null, "name"));
		operator.setIcon(xmlParser.getAttributeValue(null, "icon"));
		defaultOperationId = parseInt(xmlParser.getAttributeValue(null, "default_operation"));
	}

	private void onOperationStart(XmlPullParser xmlParser) {
		int id =  parseInt(xmlParser.getAttributeValue(null, "id"));
		operation = NfcTagOperation.create(id);
		operation.setOperationId(id);
		operation.setOperationName(xmlParser.getAttributeValue(null, "name"));
		operation.setOperatorName(operator.getOperatorName());
		operation.setContext(context);
		operation.setIcon(xmlParser.getAttributeValue(null, "icon"));
		if(id == defaultOperationId){
			operator.setSelectedOperation(operation);
			defaultOperationId = 0;
		}
		operation.setNotificationFormat(xmlParser.getAttributeValue(null, "notification_format"));
		String singleChoose = xmlParser.getAttributeValue(null, "singleChoose");
		boolean isSingleChoose = false;
		try{
			isSingleChoose = Boolean.parseBoolean(singleChoose);
		}finally{
			operation.setSingleChoose(isSingleChoose);
		}
	}

	private void onOperationEnd(XmlPullParser xmlParser) {
		nfcTagOperations.add(operation);
		operator.addNfcTagOperation(operation);
		operation = null;
	}

	private void onOperatorEnd(XmlPullParser xmlParser) {
		operatorGroup.addNfcTagOperator(operator);
		operator = null;
	}

	private void onGroupEnd(XmlPullParser xmlParser) {
		operatorGroups.add(operatorGroup);
		operatorGroup = null;
	}

	private void onGroupsEnd(XmlPullParser xmlParser) {
	}
	
	private static int parseInt(String value){
		if(value != null){
			if(value.startsWith("0x")){//hex
				return Integer.parseInt(value.substring(2), 16);
			}else if(value.startsWith("0")){//octal
				return Integer.parseInt(value.substring(1), 8);
			}else{
				return Integer.parseInt(value);
			}
		}
		return -1;
	}
}
