package cn.yjt.oa.app.dashboard.config;

import java.io.FileOutputStream;
import java.util.Collection;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;
import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardXmlConvertor {

	private static final String PKG_CURRENT = "current";
	private Context context;
	
	public DashBoardXmlConvertor(Context context) {
		super();
		this.context = context;
	}

	public void writeSettngsToStream(FileOutputStream stream, Collection<DashBoardItem> items) throws Exception {
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
        	if(item.getRequirePermission() != null){
        		out.attribute(null, "require_permission", item.getRequirePermission());
        	}
        	if(item.getSdkVersion() > 0){
        		out.attribute(null, "v", String.valueOf(item.getSdkVersion()));
        	}
        	
        	out.attribute(null, "state", String.valueOf(item.getStatus()));
        	
        	out.attribute(null, "id", String.valueOf(item.getId()));
            
            out.endTag(null, "item");
        }

        out.endTag(null, "items");
        out.endDocument();
    }
}
