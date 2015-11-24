package com.chinatelecom.nfc.Environment;//package com.chinatelecom.nfc.Environment;
//
//import java.util.List;
//import java.util.Map;
//
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.chinatelecom.nfc.R;
//import com.chinatelecom.nfc.DB.Dao.MyDataDao;
//import com.chinatelecom.nfc.DB.Provider.MyDataTable;
//import com.chinatelecom.nfc.Environment.EnvironmentListView.EnvirAdapter.SettingViewHolder;
//
//public class EnvironmentListView {
//
//	private ListView listEnvironment;
//	private EnvirAdapter envirAdapter;
//	private List<Map<String, Object>> envirDatas;
//	private Button btnAddEnvironments;
//	private Context context;
//	private View view;
//
//	public EnvironmentListView(Context context, View view) {
//		this.context = context;
//		this.view = view;
//
//		listEnvironment = (ListView) view.findViewById(R.id.listSetting);
//		envirDatas = MyDataDao.getMyDatasFromDB(context,null,String.valueOf(MyDataTable.TAG_READFROMNFC));
//		envirAdapter = new EnvirAdapter(context);
//
////		btnAddEnvironments = new Button(context);
////		btnAddEnvironments.setBackgroundResource(R.drawable.add_btn);
////		btnAddEnvironments.setGravity(Gravity.CENTER);
////
////		final Context _context = context;
////		btnAddEnvironments.setOnClickListener(new OnClickListener() {
////			@Override
////			public void onClick(View v) {
////				// TODO Auto-generated method stub
//////
//////				Intent i = new Intent();
//////				i.setClass(_context, AddEnvironmentActivity.class);
//////				((MainActivity) _context).startActivityForResult(i,
//////						Constant.REQUESTCODE_ADD_ENVIROMENT);
////				
////				MyUtil.showMessage("btnAddEnvironments click!!", _context);
////			}
////		});
////
////		listEnvironment.addFooterView(btnAddEnvironments);
//		listEnvironment.setAdapter(envirAdapter);
//		listEnvironment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//		listEnvironment.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				for (int i = 0; i < listEnvironment.getChildCount(); i++) {
//					RelativeLayout rl = (RelativeLayout) listEnvironment
//							.getChildAt(i);// 获得子级
//					rl.findViewById(R.id.view_btn).setVisibility(View.GONE);
//				}
//			}
//		});
//		listEnvironment
//				.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//					@Override
//					public boolean onItemLongClick(AdapterView<?> parent,
//							View view, int position, long id) {
//						// TODO Auto-generated method stub
//						SettingViewHolder vHollder = (SettingViewHolder) view
//								.getTag();
//						vHollder.viewBtn.setVisibility(View.VISIBLE);
//						return true;
//					}
//				});
//	}
//
//	public void update() {
//		// TODO Auto-generated method stub
//		envirDatas = MyDataDao.getMyDatasFromDB(context,null,String.valueOf(MyDataTable.TAG_READFROMNFC));
//		notifyDataSetChanged();
//	}
//	public void notifyDataSetChanged() {
//		// TODO Auto-generated method stub
//		envirAdapter.notifyDataSetChanged();
//	}
//	
//	
//	
//	
//	
//	
//	
//	//adapter
//	class EnvirAdapter extends BaseAdapter {
//
//		private LayoutInflater mInflater;
//		private Context context;
////		private List<Map<String, Object>> envirDatas;
//
//		public EnvirAdapter(Context context) {
//			this.mInflater = LayoutInflater.from(context);
//			this.context = context;
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return envirDatas.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return envirDatas.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			// TODO Auto-generated method stub
//			return arg0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			final int location = position;
//			SettingViewHolder holder = null;
//			if (convertView == null) {
//
//				holder = new SettingViewHolder();
//
//				convertView = mInflater.inflate(R.layout.setting_item, null);
//				holder.hideId = (TextView) convertView
//						.findViewById(R.id.hideId);
//				holder.img = (ImageView) convertView.findViewById(R.id.img);
//				holder.title = (TextView) convertView.findViewById(R.id.title);
//				holder.info = (TextView) convertView.findViewById(R.id.info);
//				holder.viewBtn = (Button) convertView
//						.findViewById(R.id.view_btn);
//				convertView.setTag(holder);
//
//			} else {
//
//				holder = (SettingViewHolder) convertView.getTag();
//			}
//
//			holder.hideId.setText(String.valueOf( envirDatas.get(position).get("hideId")));
//			holder.img.setBackgroundResource((Integer) envirDatas.get(position).get(
//					"img"));
//			holder.title.setText((String) envirDatas.get(position).get("title"));
//			holder.info.setText((String) envirDatas.get(position).get("info"));
//
//			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					showInfo(location);
//				}
//			});
//
//			return convertView;
//		}
//
//		/**
//		 * listview中点击按键弹出对话框
//		 */
//		public void showInfo(final int location) {
//			AlertDialog.Builder builder = new Builder(context);
//			builder.setMessage(R.string.delete);
//			builder.setTitle(R.string.title_prompt);
//			builder.setPositiveButton(R.string.ok,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
////							// 删除DB上的数据
////							List<Integer> ids = new ArrayList<Integer>();
////							Integer id = Integer.parseInt((String) envirDatas.get(
////									location).get("hideId"));
////							ids.add(id);
////							EnvironmentsDao.delete(context, ids);
////							envirDatas.remove(location);
////							EnvirAdapter.this.notifyDataSetChanged();
//							dialog.dismiss();
//						}
//					});
//			builder.setNegativeButton(R.string.cancel,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//						}
//					});
//			builder.create().show();
//		}
//
//		public final class SettingViewHolder {
//			public TextView hideId;
//			public ImageView img;
//			public TextView title;
//			public TextView info;
//			public Button viewBtn;
//		}
//
//	}
//}
