package com.chinatelecom.nfc.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailAdActivity;
//import com.chinatelecom.nfc.DetailCouponActivity;
import com.chinatelecom.nfc.DetailMeetingActivity;
//import com.chinatelecom.nfc.DetailMovieActivity;
//import com.chinatelecom.nfc.DetailParkActivity;
import com.chinatelecom.nfc.DetailTextActivity;
import com.chinatelecom.nfc.DetailWebActivity;
//import com.chinatelecom.nfc.LotteryManageActivity02;
import com.chinatelecom.nfc.MainActivity;
import com.chinatelecom.nfc.NameCardManageActivity;
import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Environment.EnvironmentManageListView.EnvirManageAdapter.ViewHolder;
import com.chinatelecom.nfc.Utils.Constant;

public class EnvironmentManageListView {
	private final static int DIVIDER = -1;
	private ListView listView;
	private EnvirManageAdapter adapter;
	private List<Map<String, Object>> datas;
	private Context context;
//	private View view;

	public EnvironmentManageListView(Context context, ListView view) {
		this.context = context;
//		this.view = view;

		listView = view;
		datas = getDatas();
		adapter = new EnvirManageAdapter(context);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		final Context _context = context;
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ViewHolder vHollder = (ViewHolder) view.getTag();
				String dataType = (String)vHollder.dataType.getText().toString();
//				int envirType = -1;
//				if(strId != null && strId.length()>0){
//					envirType = Integer.parseInt(vHollder.dataType.getText().toString());
//				}
				
				if(!dataType.equals(String.valueOf(DIVIDER))){
					Intent intent = new Intent(_context,MainActivity.class);
					intent.putExtra(Constant.MYDATA_DATATYPE, dataType);
					StringBuilder readOrWrite = new StringBuilder();
					readOrWrite.append(MyDataTable.TAG_WRITETAG).append(",").append(MyDataTable.TAG_WRITE_FAVORITE);
					intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
					intent.putExtra(Constant.TITLE, vHollder.title.getText().toString());
					_context.startActivity(intent);
				}
				
//				switch (envirType) {
//				case MyDataTable.MEETTING:
//					break;
//				case MyDataTable.COUPON:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailCouponActivity.class);
//					_context.startActivity(intent);
//					break;
//				case MyDataTable.LOTTERY:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, LotteryManageActivity02.class);
//					_context.startActivity(intent);
////					break;
//				case MyDataTable.AD:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailAdActivity.class);
//					_context.startActivity(intent);
//					break;
//				case MyDataTable.MYMODECUSTOM:
////					intent.putExtra("envirType", envirType);
////					intent.setClass(_context, EditEnvironmentActivity.class);
////					_context.startActivity(intent);
//					break;
//				case MyDataTable.NAMECARD:
//					intent.setClass(_context, NameCardManageActivity.class);
//					intent.setAction(Intent.ACTION_INSERT);
//					_context.startActivity(intent);
//					break;
//				case MyDataTable.MOVIETICKET:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailMovieActivity.class);
//					_context.startActivity(intent);
//					break;
//				case MyDataTable.PARKTICKET:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailParkActivity.class);
//					_context.startActivity(intent);
//					break;
//					
//				case MyDataTable.TEXT:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailTextActivity.class);
//					_context.startActivity(intent);
//					break;
//					
//				case MyDataTable.WEB:
//					intent.putExtra(Constant.MYDATA_DATATYPE, envirType);
//					intent.setClass(_context, DetailWebActivity.class);
//					_context.startActivity(intent);
//					break;
//
//				default:
//					break;
//				}
				
			}
		});

		
	}

	
	
	
	
	// adapter
	public class EnvirManageAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context context;

		// private List<Map<String, Object>> envirDatas;

		public EnvirManageAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return datas.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.nfc_item_image_text, null);
				
				holder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rlItem);
				holder.llDivider = (LinearLayout) convertView.findViewById(R.id.llDivider);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				holder.dataType = (TextView) convertView.findViewById(R.id.hideId);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.ivArrow = (ImageView) convertView.findViewById(R.id.ivArrow);
				convertView.setTag(holder);
				 
			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			String _tvTitle = (String)datas.get(position).get("tvTitle");
			if (_tvTitle != null && _tvTitle.length()>0) {
				
				holder.llDivider.setVisibility(View.VISIBLE);
				holder.tvTitle.setText((String)datas.get(position).get("tvTitle"));
				holder.dataType.setText(String.valueOf(datas.get(position).get("hideId")));
				holder.rlItem.setVisibility(View.GONE);
				convertView.setBackgroundDrawable(null);
				
			}else{
				holder.llDivider.setVisibility(View.GONE);
				holder.rlItem.setVisibility(View.VISIBLE);
//				convertView.setBackgroundResource(R.drawable.nfc_item_listview);
				holder.dataType.setText(String.valueOf( datas.get(position).get("hideId")));
				holder.icon.setBackgroundResource((Integer) datas.get(position).get("icon"));
				holder.title.setText(String.valueOf(datas.get(position).get("title")));
			}
			
//			if (datas.size() == 1) {
//	            convertView.setBackgroundResource(R.drawable.nfc_circle_list_single);
//	        } else if (datas.size() > 1) {
//	            if (position == 0) {
//	                convertView.setBackgroundResource(R.drawable.nfc_circle_list_top);
//	            } else if (position == (datas.size() - 1)) {
//	                convertView.setBackgroundResource(R.drawable.nfc_circle_list_bottom);
//	            } else {
//	                convertView.setBackgroundResource(R.drawable.nfc_circle_list_middle);
//	            }
//	        }
			return convertView;
		}

		/**
		 * listview中点击按键弹出对话框
		 */
		public void showInfo(final int location) {
			Builder builder = new Builder(context);
			builder.setMessage(R.string.nfc_delete);
			builder.setTitle(R.string.nfc_title_prompt);
			builder.setPositiveButton(R.string.nfc_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// // 删除DB上的数据
							// List<Integer> ids = new ArrayList<Integer>();
							// Integer id = Integer.parseInt((String)
							// envirDatas.get(
							// location).get("hideId"));
							// ids.add(id);
							// EnvironmentsDao.delete(context, ids);
							// envirDatas.remove(location);
							// EnvirAdapter.this.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(R.string.nfc_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}

		final class ViewHolder {
			public RelativeLayout rlItem;
			public LinearLayout llDivider;
			public TextView tvTitle;
			public TextView dataType;
			public ImageView icon;
			public TextView title;
			public ImageView ivArrow;
		}

	}
	
	private List<Map<String, Object>> getDatas(){
		String tagTitle[] = context.getResources().getStringArray(R.array.nfc_new_tag_title);
		List<Map<String, Object>> d = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hideId",DIVIDER);
		map.put("tvTitle",context.getResources().getString(R.string.nfc_make_tag));
		d.add(map);
		
		map = new HashMap<String, Object>();
		map.put("hideId",MyDataTable.NAMECARD );
		map.put("title",tagTitle[0]);
		map.put("icon", R.drawable.nfc_icon_card);
		d.add(map);
		
		map = new HashMap<String, Object>();
		map.put("hideId",MyDataTable.MEETTING );
		map.put("title",tagTitle[1]);
		map.put("icon", R.drawable.nfc_icon_meeting );
		d.add(map);
		
		//ashley 0923 删除无用资源
//		map = new HashMap<String, Object>();
//		map.put("hideId",MyDataTable.COUPON );
//		map.put("title",tagTitle[2]);
//		map.put("icon", R.drawable.nfc_coupon );
//		d.add(map);
//		
//		map = new HashMap<String, Object>();
//		map.put("hideId",MyDataTable.LOTTERY );
//		map.put("title",tagTitle[3]);
//		map.put("icon", R.drawable.nfc_lottery );
//		d.add(map);
//		
//		
//		map = new HashMap<String, Object>();
//		map.put("hideId",MyDataTable.AD );
//		map.put("title",tagTitle[4]);
//		map.put("icon", R.drawable.nfc_ad );
//		d.add(map);
		
		map = new HashMap<String, Object>();
		map.put("hideId",MyDataTable.TEXT );
		map.put("title",tagTitle[5]);
		map.put("icon", R.drawable.nfc_text );
		d.add(map);
		
		map = new HashMap<String, Object>();
		map.put("hideId",MyDataTable.WEB );
		map.put("title",tagTitle[6]);
		map.put("icon", R.drawable.nfc_web );
		d.add(map);
		
		map = new HashMap<String, Object>();
		map.put("hideId",DIVIDER);
		map.put("tvTitle",context.getResources().getString(R.string.nfc_wait_update));
		d.add(map);
		
		//ashley 0923 删除无用资源
//		map = new HashMap<String, Object>();
//		map.put("hideId",MyDataTable.MOVIETICKET );
//		map.put("title",tagTitle[7]);
//		map.put("icon", R.drawable.nfc_movieticket );
//		d.add(map);
//		
//		map = new HashMap<String, Object>();
//		map.put("hideId",MyDataTable.PARKTICKET );
//		map.put("title",tagTitle[8]);
//		map.put("icon", R.drawable.nfc_parkticket );
//		d.add(map);
		return d;
		
	}
	
}
