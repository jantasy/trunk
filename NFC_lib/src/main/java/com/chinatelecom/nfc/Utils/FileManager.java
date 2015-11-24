package com.chinatelecom.nfc.Utils;
//package com.chinatelecom.Utils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.ListActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.automotiveSafety.R;
//
///**
// * 文件管理
// * 
// * @author ycliuc
// * 
// */
//public class FileManager extends ListActivity {
//	private List<String> items = null;
//	private List<String> paths = null;
//	private String rootPath;
//	private String fDir = "/";
//	private String fName = "";
//	private TextView mPath;
//
//	@Override
//	protected void onCreate(Bundle icicle) {
//		super.onCreate(icicle);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.file_select);
//
//		rootPath = CommonUtil.getSdCardAbsolutePath()+"/AutomotiveSafety";
//
//		mPath = (TextView) findViewById(R.id.mPath);
//		Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
//		buttonConfirm.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				Intent data = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putString("fileName", fName);
//				bundle.putString("fileDir", fDir);
//				data.putExtras(bundle);
//				setResult(Activity.RESULT_OK, data);
//				finish();
//
//			}
//		});
//		Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
//		buttonCancle.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		getFileDir(rootPath);
//	}
//
//	private void getFileDir(String filePath) {
//		mPath.setText(filePath);
//		items = new ArrayList<String>();
//		paths = new ArrayList<String>();
//		File f = new File(filePath);
//		if (!f.exists()) {
//			f.mkdir();
//		}
//		if (f != null) {
//			File[] files = f.listFiles();
//			sortFiles(files);// 排序
//			if (!filePath.equals(rootPath)) {
//				items.add("b1");
//				paths.add(rootPath);
//				items.add("b2");
//				paths.add(f.getParent());
//			}
//			for (int i = 0; i < files.length; i++) {
//				File file = files[i];
//				items.add(file.getName());
//				paths.add(file.getPath());
//			}
//
//			setListAdapter(new MyAdapter(this, items, paths));
//		} else {
//			Toast.makeText(this, "目录不正确，请输入正确的目录!", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 排序文件列表
//	 * 
//	 */
//	private void sortFiles(File[] files) {
//		Arrays.sort(files, new Comparator<File>() {
//			public int compare(File file1, File file2) {
//				if (file1.isDirectory() && file2.isDirectory())
//					return 1;
//				if (file2.isDirectory())
//					return 1;
//				return -1;
//			}
//		});
//	}
//
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		File file = new File(paths.get(position));
//		
//		if (CommonUtil.externalStorageState()) {
//			if (file.isDirectory()) {
//				fDir = paths.get(position);
//				getFileDir(paths.get(position));
//			} else {
//				fDir = file.getParent();
//				fName = file.getName();
//			}
//		} else {
//			Toast.makeText(this, R.string.filePermission_no, Toast.LENGTH_LONG).show();
//		}
//	}
//}
