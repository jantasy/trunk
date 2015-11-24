package cn.yjt.oa.app.app.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import cn.yjt.oa.app.R;

public class MentroView extends ViewGroup implements View.OnClickListener {
	private int size1 = 0;
	private int size2;
	private int size1h;
	private int size2h;
	private int size1w;
	private int size2w;
	private int sizeSpace;
	private int mode;
	private int width;
	private int height;
	private int pheight = 0;
	private int pwidth;

	private Map<String, MenuItem> items = new HashMap<String, MentroView.MenuItem>();

	private Map<String, MenuItemView> views = new HashMap<String, MenuItemView>();

	// 移动的位置(目前的确切意思还待考虑)

	public MentroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		BaseAdapter adpAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		adpAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutChildren();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 计算每个子项目的大
		if (pheight == 0) {
			int mHeight = ((View) getParent()).getMeasuredHeight();
			if (mHeight == 0) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			} else {
				pheight = mHeight;
				pwidth = widthMeasureSpec;
			}
		}
		int h = MeasureSpec.makeMeasureSpec(pheight, MeasureSpec.EXACTLY);
		measureChildrenView(widthMeasureSpec, h);
		super.onMeasure(widthMeasureSpec,
				MeasureSpec.makeMeasureSpec(2000/*initHeight()*/, MeasureSpec.EXACTLY));
	}

	public void layoutChildren() {
		int count = getChildCount();
		View tmp;
		for (int i = 0; i < count; i++) {
			tmp = getChildAt(i);
			layoutChildView(tmp);
		}
	}

	private void layoutChildView(View view) {
		if (view == null || !(view instanceof MenuItemView)) {
		}
		MenuItemView info = (MenuItemView) view;
		int l, r, t, b;
		int size1 = MeasureSpec.getSize(this.size1);
		int size2 = MeasureSpec.getSize(this.size2);
		int size1h = MeasureSpec.getSize(this.size1h);
		int size2h = MeasureSpec.getSize(this.size2h);
		l = (info.x + 1) * sizeSpace + info.x * size1;
		// l = 16;
		r = info.size == 1 ? (size1) : size2;
		r += l;// - sizeSpace;
		t = (info.y + 1) * sizeSpace + info.y * size1h;
		b = info.size == 3 ? size2h : (size1h);
		b += t;// - sizeSpace;
		view.layout(l, t, r, b);
	}

	private void measureChildrenView(int widthMeasureSpec, int heightMeasureSpec) {
		// 手动计算自己的高度
		int count = this.getChildCount();
		initSize(widthMeasureSpec, heightMeasureSpec);
		View tem;
		for (int i = 0; i < count; i++) {
			tem = getChildAt(i);
			if (tem instanceof MenuItemView) {
				MenuItemView info = (MenuItemView) tem;
				switch (info.size) {
				case 1:
					tem.measure(size1, size1h);
					break;
				case 2:
					tem.measure(size2h, size1);
					break;
				case 3:
					tem.measure(size2, size2h);
					break;
				default:
					break;
				}
			}

		}
	}

	private void initSize(int widthMeasureSpec, int heightMeasureSpec) {
		mode = MeasureSpec.getMode(widthMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		int ret = width / 30;
		sizeSpace = ret / 3;
		ret = (width - ret) / 2;
		int hret = height - 7 * sizeSpace;
		hret /= 7;
		hret *= 2;
		// 计算出合适的宽度
		size2 = MeasureSpec.makeMeasureSpec(ret, mode);
		size1 = MeasureSpec.makeMeasureSpec((ret - sizeSpace) / 2, mode);
		size2h = MeasureSpec.makeMeasureSpec(hret, mode);
		size1h = MeasureSpec.makeMeasureSpec((hret - sizeSpace) / 2, mode);
	}

	// @Override
	// public void onChildViewAdded(View parent, View child) {
	// super.onChildViewAdded(parent, child);
	//
	// }
	@Override
	public void addView(View child) {
		super.addView(child);
		if (child != null && child instanceof MenuItemView) {
			MenuItemView info = (MenuItemView) child;
			views.put(info.x + "," + info.y, (MenuItemView) child);
			info.setOnClickListener(this);
		}
	}

	/**
	 * 完成两个子控件的交换 v2,为当前正在移动的控件，v1被动交换的子控件
	 */
	protected boolean swapChild(MenuItemView v1, MenuItemView v2) {
		// 大小分别为1,2,3
		// 1,2
		// 1,3
		// 2,3
		boolean retVal = false;
		if (v1.size == v2.size) {
			views.remove(v1.x + "," + v1.y);
			views.remove(v2.x + "," + v2.y);
			// 交换x和y
			v1.x ^= v2.x;
			v2.x ^= v1.x;
			v1.x ^= v2.x;
			v1.y ^= v2.y;
			v2.y ^= v1.y;
			v1.y ^= v2.y;
			//
			retVal = true;
			layoutChildView(v1);
			layoutChildView(v2);
			views.put(v1.x + "," + v1.y, v1);
			views.put(v2.x + "," + v2.y, v2);
		} else {
			// 1,2 2所在的列往下移动放出空间
			// 计算1的位置
			if (v1.size > v2.size) {
				// 执行移动动画
				int x = v1.x, y = v1.y;
				List<MenuItemView> initNewPos = initNewPos(v1.x, v1.y, 1);
				views.remove(v2.x + "," + v2.y);
				v2.x = x;
				v2.y = y;
				views.put(v2.x + "," + v2.y, v2);
				for (MenuItemView view : initNewPos) {
					layoutChildView(view);
				}
				layoutChildView(v2);
			}

			// 1,3
			// 2,3

			// 2,1
			// 3,1
			// 3,2
		}
		Animation ani = AnimationUtils.loadAnimation(getContext(),
				R.anim.menu_flash);
		v2.startAnimation(ani);
		setMeasuredDimension(pwidth,
				MeasureSpec.makeMeasureSpec(initHeight(), MeasureSpec.EXACTLY));
		postInvalidate();
		return retVal;
	}

	/**
	 * 一定底部的菜单项
	 * 
	 * @param x
	 * @param y
	 * @param dy
	 */
	private List<MenuItemView> initNewPos(int x, int y, int dy) {
		//
		List<MenuItemView> list = new ArrayList<MenuItemView>();
		MenuItemView temp;
		while (true) {
			temp = views.get(x + "," + y);
			if (temp == null) {
				temp = views.get((x + 1) + "," + y);
				if (temp == null)
					break;
			}
			list.add(temp);
			int ty = temp.size;
			if (temp.size == 1) {
				temp = views.get((x + 1) + "," + y);
				if (temp != null) {
					list.add(temp);
				}
			}
			y += 1;
			if (ty == 3)
				y++;
		}
		//
		for (MenuItemView view : list) {
			views.remove(view.x + "," + view.y);
			view.y += dy;
		}
		for (MenuItemView view : list) {
			views.put(view.x + "," + view.y, view);
		}
		return list;
	}

	private void initView() {
		// 从本地data/目录下读取，menuconfig.xml 如果没有则使用assert/menuconfig.xml进行初始化菜单
		InputStream min = null;
		XmlPullParser parser;
		Context context = getContext();
		try {
			min = context.getAssets().open("menuitems.xml");
			// 获取所有item
			parser = Xml.newPullParser();
			parser.setInput(min, "UTF-8");
			int event = parser.getEventType();
			String name;
			MenuItem item = new MenuItem();
			Resources resources = context.getResources();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if ("menu".equals(name)) {
						item = new MenuItem();
						item.id = parser.getAttributeValue(0);
					} else if ("bigZoonId".equals(name)) {
						item.bigZoonId = resources.getIdentifier(
								parser.nextText(), "drawable",
								context.getPackageName());
					} else if ("smallZoonId".equals(name)) {
						item.smallZoonId = resources.getIdentifier(
								parser.nextText(), "drawable",
								context.getPackageName());
					} else if ("title".equals(name)) {
						item.title = resources.getIdentifier(parser.nextText(),
								"string", context.getPackageName());
					} else if ("bgcolor".equals(name)) {
						item.bgcolor = resources.getIdentifier(parser.nextText(),
								"color", context.getPackageName());
					}else if("intent".equals(name)){
						item.intent = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if ("menu".equals(name)) {
						items.put(item.id, item);
					}
				default:
					break;
				}
				event = parser.next();
			}
		} catch (Exception e1) {
			throw new RuntimeException("程序安装不完整");
		} finally {
			if (min != null) {
				try {
					min.close();
					min = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 读取位置配置信息
		File mconf = new File(context.getFilesDir(), "menuconfig.xml");
		boolean flag = false;
		if (!mconf.exists()) {
			flag = true;
		} else {
			try {
				min = new FileInputStream(mconf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				flag = true;
			}
		}
		if (flag) {// 读取本地文件出错，使用默认配置文件
			// 本地文件不存在则读取assert目录下内容
			try {
				min = getContext().getResources().getAssets().open("menuconfig.xml");
			} catch (IOException e) {
				// 程序文件不齐全
				e.printStackTrace();
				throw new RuntimeException("app not intact");
			}
		}
		// 开始解析xml获取每个菜单项
		parser = Xml.newPullParser();
		try {
			parser.setInput(min, "UTF-8");

			int event = parser.getEventType();
			MenuItemView view = null;
			while (event != XmlPullParser.END_DOCUMENT) {
				String name;
				String id;
				switch (event) {
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if ("item".equals(name)) {
						id = parser.getAttributeValue(0);
						if (items.containsKey(id)) {
							if ("2".equals(parser.getAttributeValue(1))) {
								view = (MenuItemView) View.inflate(context,
										R.layout.menuitem_strip, null);
							} else {
								view = (MenuItemView) View.inflate(context,
										R.layout.menuitem_box, null);
							}
							view.id = id;
							view.size = Integer.parseInt(parser
									.getAttributeValue(1));
						}
					} else if ("posX".equals(name)) {
						if(view != null)
							view.x = Integer.parseInt(parser.nextText());
					} else if ("posY".equals(name)) {
						if(view != null)
							view.y = Integer.parseInt(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if ("item".equals(name)&&view!=null) {
						//
						MenuItem i = items.get(view.id);
						view.setTitle(i.title);
						view.setBackgroundResource(i.bgcolor);
						view.setIcon(view.size == 3 ? i.bigZoonId
								: i.smallZoonId);
						this.addView(view);
						view = null;
					}
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (min != null) {
				try {
					min.close();
					min = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int[] getCenterPoint(View vw) {
		int[] pos = new int[2];
		vw.getLocationOnScreen(pos);
		pos[0] = pos[0] + vw.getWidth() / 2;
		pos[1] = pos[1] + vw.getHeight() / 2;
		return pos;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof MenuItemView) {
			//
			MenuItemView info = (MenuItemView) v;
			MenuItem item = items.get(info.id);
			if (item!=null&&item.intent!=null){
				try{
					Intent intent = new Intent();
					intent.setClass(getContext(), Class.forName(item.intent));
					getContext().startActivity(intent);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
	}

	private int initHeight() {
		int size1h = MeasureSpec.getSize(this.size1h);
		int height = getChildCount();
		boolean find = false;
		for (; height > 0; height--) {// 获取最下面的高度
			for (int i = 0; i < 4; i++) {
				if (views.containsKey(i + "," + height)) {
					find = true;
					break;
				}
			}
			if (find) {
				break;
			}
		}
		height++;
		return height * (size1h + sizeSpace) + sizeSpace;
	}

	private class MenuItem {
		public String id;
		public int smallZoonId;
		public int bigZoonId;
		public int title;
		public int bgcolor;
		public String intent;
	}
	
	public void serialMenu(){
		//持久化菜单
		File mconf = new File(getContext().getFilesDir(), "menuconfig.xml");
		if(!mconf.exists()){
			try {
				mconf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		XmlSerializer serial = Xml.newSerializer();
		try {
			serial.setOutput(new FileWriter(mconf));
			serial.startDocument("UTF-8", true);
			serial.startTag("", "items");
			MenuItemView view;
			for(String str:views.keySet()){
				view = views.get(str);
				serial.startTag("", "item");
				serial.attribute("", "id", view.id);
				serial.attribute("", "size", view.size+"");
				serial.startTag("", "posX");
				serial.text(view.x+"");
				serial.endTag("", "posX");
				serial.startTag("", "posY");
				serial.text(view.y+"");
				serial.endTag("", "posY");
				serial.endTag("", "item");
			}
			serial.endTag("", "items");
			serial.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
