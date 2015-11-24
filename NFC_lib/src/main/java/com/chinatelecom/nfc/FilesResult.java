package com.chinatelecom.nfc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

public class FilesResult extends BaseTag {
	public final static String  INTENT_LOTTERY_DIR = "LOTTERY_DIR";
	private List<Map<String, Object>> mData;
	private String mDir ;
	private ArrayList<Uri> fileinfos = new ArrayList<Uri>();
	private ArrayList<String> isDir = new ArrayList<String>();
	
	private ListView mainListView;
	private MyAdapter adapter;
	private Button btnCancel;
	private Button btnOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_musics_manager);
		if (MyUtil.externalStorageState()){
			if (getIntent().getBooleanExtra(INTENT_LOTTERY_DIR, false)) {
				mDir = Constant.LOTTERY_DIR;
				if(MyUtil.externalStorageState()){
					MyUtil.makeDir(Constant.LOTTERY_DIR);
				}
			} else {
				mDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			}
			initial();
			initialEvent();
		}else{
			MyUtil.showMessage(R.string.nfc_msg_no_sdcard, this);
		}
	}
	
	
	private void initial(){
		setTitle(R.string.nfc_fileresult);
	
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!fileinfos.isEmpty()) 
					finishWithResult(fileinfos);
				finish();
			}
		});
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mainListView = (ListView) findViewById(R.id.mainListView);
		
//		Intent intent = this.getIntent();
//		Uri uri = intent.getData();
//		mDir = uri.getPath();
		File dirs = new File(Const.ShareDIR);
		if (!dirs.exists()) dirs.mkdirs();
		mDir = Const.ShareDIR;
		mData = getData();
		adapter = new MyAdapter(this);
		mainListView.setAdapter(adapter);

		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (d.getHeight() * 0.8);
		p.width = (int) (d.getWidth() * 0.95);
		getWindow().setAttributes(p);
	}
	
	private void initialEvent()
	{
		mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (!fileinfos.isEmpty()) finishWithResult(fileinfos);
			return true;
			}
		});
		
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d("MyListView4-click", (String) mData.get(position).get("info"));
				//如果是目录 点击 继续递归   查看该路径下的所有文件和文件夹，或者返回上一目录
				
				//如果是文件点击后 将该文件路径回传给上一页面
				TextView tv = (TextView)v.findViewById(R.id.info);
				String curParh = (String) tv.getText();
				startActivity(openFile(curParh));
			}
		});
	}
	
	
	//获取当前路径下的所有文件夹和文件
	private List<Map<String, Object>> getData() {
		fileinfos.clear();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		File[] files = f.listFiles();
		sortFiles1(files);
		sortFiles2(files);
		
		if (!mDir.equals(Const.ShareDIR)) {
			map = new HashMap<String, Object>();
			map.put("title", getResources().getString(R.string.nfc_back_to));
			map.put("info", f.getParent());
			map.put("img", R.drawable.nfc_backto);
			list.add(map);
			isDir.add(getResources().getString(R.string.nfc_back_to));
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())//是否是目录
				{
					map.put("img", R.drawable.nfc_ex_folder);
					isDir.add(files[i].getName());
				}
				else
					map.put("img", R.drawable.nfc_ex_doc);
				list.add(map);
			}
		}
		return list;
	}

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		Log.d("MyListView4-click", (String) mData.get(position).get("info"));
//		//如果是目录 点击 继续递归   查看该路径下的所有文件和文件夹，或者返回上一目录
//		if ((Integer) mData.get(position).get("img") == R.drawable.nfc_ex_folder) {
//			mDir = (String) mData.get(position).get("info");
//			mData = getData();
//			MyAdapter adapter = new MyAdapter(this);
//			setListAdapter(adapter);
//			
//			
//		} else {
//			//如果是文件点击后 将该文件路径回传给上一页面
//			CheckBox cb = (CheckBox)v.findViewById(R.id.ans_cbx_select);
//			cb.toggle();
//			Boolean bcheck = cb.isChecked();
//			Boolean bDir = ((Integer) mData.get(position).get("img") != R.drawable.nfc_ex_folder);
//			if (bDir)
//			{
//				if(bcheck){
//					String fileinfo = "file:///mnt" + (String) mData.get(position).get("info");
//					Uri uri = Uri.parse(fileinfo);
//					fileinfos.add(uri);
//				}
//				else
//				{
//					String fileinfo = "file:///mnt" + (String) mData.get(position).get("info");
//					fileinfos.remove(Uri.parse(fileinfo));
//				}
//			}			
//		}
//	}


	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
		public CheckBox checkbox;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.nfc_filesresultlistview, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			
			return convertView;
		}
	}

	//HashMap<Integer, Boolean> state = new HashMap<Integer,Boolean>();
	
	private Boolean UrisContainUri(ArrayList<Uri> Uris, String Uri)
	{
		Object[] UrisObj = Uris.toArray();
		for (int i = 0 ; i< UrisObj.length ; i++)
		{
			if (UrisObj[i].toString().contains(Uri)) return true;
		}
		return false;
	}

	
	
	
	private void finishWithResult(ArrayList<Uri> fileinfos) {
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileinfos);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	
	
	private void sortFiles1(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}
		});
	}
	
	private void sortFiles2(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && file2.isDirectory())
					return 1;
				if (file2.isDirectory())
					return 1;
				return -1;
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Intent openFile(String filePath){

		File file = new File(filePath);
		if(!file.exists()) return null;
		/* 取得扩展名 */
		String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase(); 
		/* 依扩展名的类型决定MimeType */
		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
				end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
			return getAudioFileIntent(filePath);
		}else if(end.equals("3gp")||end.equals("mp4")){
			return getAudioFileIntent(filePath);
		}else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
				end.equals("jpeg")||end.equals("bmp")){
			return getImageFileIntent(filePath);
		}else if(end.equals("apk")){
			return getApkFileIntent(filePath);
		}else if(end.equals("ppt")){
			return getPptFileIntent(filePath);
		}else if(end.equals("xls")){
			return getExcelFileIntent(filePath);
		}else if(end.equals("doc")){
			return getWordFileIntent(filePath);
		}else if(end.equals("pdf")){
			return getPdfFileIntent(filePath);
		}else if(end.equals("chm")){
			return getChmFileIntent(filePath);
		}else if(end.equals("txt")){
			return getTextFileIntent(filePath,false);
		}else{
			return getAllIntent(filePath);
		}
	}
	
	//Android获取一个用于打开APK文件的intent
	public static Intent getAllIntent( String param ) {

		Intent intent = new Intent();  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri,"*/*"); 
		return intent;
	}
	//Android获取一个用于打开APK文件的intent
	public static Intent getApkFileIntent( String param ) {

		Intent intent = new Intent();  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri,"application/vnd.android.package-archive"); 
		return intent;
	}

	//Android获取一个用于打开VIDEO文件的intent
	public static Intent getVideoFileIntent( String param ) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	//Android获取一个用于打开AUDIO文件的intent
	public static Intent getAudioFileIntent( String param ){

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	//Android获取一个用于打开Html文件的intent   
	public static Intent getHtmlFileIntent( String param ){

		Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	//Android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent( String param ) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	//Android获取一个用于打开PPT文件的intent   
	public static Intent getPptFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");   
		return intent;   
	}   

	//Android获取一个用于打开Excel文件的intent   
	public static Intent getExcelFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/vnd.ms-excel");   
		return intent;   
	}   

	//Android获取一个用于打开Word文件的intent   
	public static Intent getWordFileIntent( String param ){  

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/msword");   
		return intent;   
	}   

	//Android获取一个用于打开CHM文件的intent   
	public static Intent getChmFileIntent( String param ){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/x-chm");   
		return intent;   
	}   

	//Android获取一个用于打开文本文件的intent   
	public static Intent getTextFileIntent( String param, boolean paramBoolean){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		if (paramBoolean){   
			Uri uri1 = Uri.parse(param );   
			intent.setDataAndType(uri1, "text/plain");   
		}else{   
			Uri uri2 = Uri.fromFile(new File(param ));   
			intent.setDataAndType(uri2, "text/plain");   
		}   
		return intent;   
	}  
	//Android获取一个用于打开PDF文件的intent   
	public static Intent getPdfFileIntent( String param ){   

		Intent intent = new Intent("android.intent.action.VIEW");   
		intent.addCategory("android.intent.category.DEFAULT");   
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		Uri uri = Uri.fromFile(new File(param ));   
		intent.setDataAndType(uri, "application/pdf");   
		return intent;   
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		Boolean notnull = intent != null && intent.getAction() != null;
//		Boolean b = intent.getType().equals("application/mobileinfo") && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()));
        if (notnull) {
        	super.onNewIntent(intent);
        	setTitle(R.string.nfc_fileresult);
        }
        setIntent(intent);
	}
}

