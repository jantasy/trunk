package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicsManager extends BaseTag {
	private List<Map<String, Object>> mData;
	// private ArrayList<Uri> fileinfos = new ArrayList<Uri>();
	private ArrayList<Uri> musicinfos = new ArrayList<Uri>();
	private ListView mainListView;
	private SimpleAdapter listAdapter;
	private Button btnCancel;
	private Button btnOk;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_musics_manager);
		initial();
		// initialEvent();
	}

	private void initial() {
		setTitle(R.string.nfc_music_title);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!musicinfos.isEmpty())
					finishWithResult(musicinfos);
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

		Cursor mAudioCursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“
																	// * ”
				null, // 查询条件
				null, // 条件的对应?的参数
				MediaStore.Audio.AudioColumns.TITLE);// 排序方式
		String[] projection = new String[]{MediaStore.Video.Media.TITLE};
        Cursor mVideoCursor = getContentResolver().query(
        		MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, 
                null, 
                null, 
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		// 循环输出歌曲的信息
		// List<Map<String, Object>>
		mData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mAudioCursor.getCount(); i++) {
			mAudioCursor.moveToNext();
			// 找到歌曲标题和总时间对应的列索引
			int indexTitle = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);// 歌名
			int indexARTIST = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);// 艺术家
			// int indexALBUM = mAudioCursor
			// .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);//专辑

			String strTitle = mAudioCursor.getString(indexTitle);
			String strARTIST = mAudioCursor.getString(indexARTIST);
			// String strALBUM = mAudioCursor.getString(indexALBUM);

			HashMap<String, Object> nowMap = new HashMap<String, Object>();
			// nowMap.put("SongName", strTitle + "---" + strARTIST + "---" +
			// strALBUM);
			nowMap.put("SongName", strTitle + "---" + strARTIST);
			String uri_Str = mAudioCursor.getString(1);
			nowMap.put("info", uri_Str);
			mData.add(nowMap);

			// fileinfos.add(Uri.parse(uri_Str));
		}

		// ListAdapter listAdapter = new SimpleCursorAdapter(this,
		// R.layout.nfc_filemanagelistview,
		// mAudioCursor,
		// new String[]{"title", "info", "img"},
		// new int[]{R.id.title, R.id.info, R.id.img}
		// );
        for(int i = 0; i < mVideoCursor.getCount(); i++){   
        	mVideoCursor.moveToNext();
			int indexTitle = mVideoCursor
					.getColumnIndex(MediaStore.Video.VideoColumns.TITLE);// 歌名
			int indexARTIST = mVideoCursor
					.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST);// 艺术家
			String strTitle = mVideoCursor.getString(indexTitle);
			String strARTIST = mVideoCursor.getString(indexARTIST);
			HashMap<String, Object> nowMap = new HashMap<String, Object>();
			nowMap.put("SongName", strTitle + "---" + strARTIST);
			String uri_Str = mVideoCursor.getString(1);
			nowMap.put("info", uri_Str);
			mData.add(nowMap);
        }


		listAdapter = new SimpleAdapter(this, mData,
				R.layout.nfc_musicmanagelistview,
				new String[] { "SongName", "info" }, new int[] { R.id.SongName,
						R.id.info });

		mainListView.setAdapter(listAdapter);
		mainListView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				if (!musicinfos.isEmpty())
					finishWithResult(musicinfos);
				return true;
			}
		});
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				CheckBox cb = (CheckBox) v.findViewById(R.id.ans_cbx_select);
				cb.toggle();
				Boolean bcheck = cb.isChecked();
				if (bcheck) {
					String fileinfo = "file://"
							+ (String) mData.get(position).get("info");
					Uri uri = Uri.parse(fileinfo);
					musicinfos.add(uri);
				} else {
					String fileinfo = "file://"
							+ (String) mData.get(position).get("info");
					musicinfos.remove(Uri.parse(fileinfo));
				}
			}
		});
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (d.getHeight() * 0.8);
		p.width = (int) (d.getWidth() * 0.95);
		getWindow().setAttributes(p);
	}

	// private void initialEvent()
	// {
	// listAdapter.setOnItemLongClickListener(new
	// AdapterView.OnItemLongClickListener() {
	// @Override
	// public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// if (!musicinfos.isEmpty()) finishWithResult(musicinfos);
	// return true;
	// }
	// });
	// }

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// CheckBox cb = (CheckBox)v.findViewById(R.id.ans_cbx_select);
	// cb.toggle();
	// Boolean bcheck = cb.isChecked();
	// if(bcheck){
	// String fileinfo = "file://" + (String) mData.get(position).get("info");
	// Uri uri = Uri.parse(fileinfo);
	// musicinfos.add(uri);
	// }
	// else
	// {
	// String fileinfo = "file://" + (String) mData.get(position).get("info");
	// musicinfos.remove(Uri.parse(fileinfo));
	// }
	// }

	private void finishWithResult(ArrayList<Uri> fileinfos) {
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileinfos);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		Boolean notnull = intent != null && intent.getAction() != null;
//		Boolean b = intent.getType().equals("application/mobileinfo") && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()));
        if (notnull) {
        	super.onNewIntent(intent);
        	setTitle(R.string.nfc_music_title);
        }
        setIntent(intent);
	}

}
