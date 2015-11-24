package cn.yjt.oa.app.find.config;

public class FinderItem {
	private int label;
	private int icon;
	private String intent;
	private String url;

	
	
	public FinderItem(int label, int icon, String intent, String url) {
		super();
		this.label = label;
		this.icon = icon;
		this.intent = intent;
		this.url = url;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "FinderItem [label=" + label + ", icon=" + icon + ", intent="
				+ intent + ", url=" + url + "]";
	}
}
