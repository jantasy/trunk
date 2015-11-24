package cn.yjt.oa.app.documentcenter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.DocumentInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.widget.TimeLineAdapter;

public class DocumentCenterListAdapter extends TimeLineAdapter {

	private Context context;
    private LayoutInflater inflater;
    
    
    public DocumentCenterListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * 检索出文档的时间
     * @param docInfo
     * @return
     */
    private Date retrieveTaskDate(DocumentInfo docInfo){
    	Date createDate = null;
    	if(!TextUtils.isEmpty(docInfo.getCreateTime())){
    		try {
				createDate = BusinessConstants.parseTime(docInfo.getCreateTime());
			} catch (Exception e) {
				createDate = null;
			}
    	}
    	return createDate;
    }
    /**
     * 将一条文档记录添加到adapter中
     * @param docInfo
     */
    public void addMoment2Adapter(DocumentInfo docInfo){
    	Date createDate  = retrieveTaskDate(docInfo);
    	if(createDate == null){
    		return;
    	}
    	addEntry(createDate,docInfo);
    }
    /**
     * 添加多条文档到adapter中
     * @param infos
     */
    public void addMoment2Adapter(List<DocumentInfo> infos){
    	if(infos == null){
    		return;
    	}
    	for(DocumentInfo info : infos){
    		addMoment2Adapter(info);
    	}
    }
    
    

	@Override
	public View getSectionView(int section, View convertView, ViewGroup parent) {
		View sectionView = convertView;
		if(sectionView == null){
			sectionView = inflater.inflate(R.layout.message_center_item_title, parent, false);
		}
		TextView sectionTitle = (TextView)sectionView.findViewById(R.id.title_tv);
        Date date = getSectionDate(section);
        Calendar sectionTime = Calendar.getInstance();
        sectionTime.setTimeInMillis(date.getTime());
        
        Calendar now = Calendar.getInstance();
        
        if (now.get(Calendar.DATE) == sectionTime.get(Calendar.DATE)) {
            sectionTitle.setText("今天");
        } else if (now.get(Calendar.DATE) == sectionTime.get(Calendar.DATE) + 1) {
            sectionTitle.setText("昨天");
        } else {
            sectionTitle.setText(BusinessConstants.getDate(date));
        }
        
        return sectionView;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if(view == null){
			view = inflater.inflate(R.layout.document_center_list_item, parent,false);
			holder = new ViewHolder();
			holder.document_centre_item = (LinearLayout) view.findViewById(R.id.document_centre_item);
			holder.img_moment = (ImageView) view.findViewById(R.id.img_moment);
			holder.tv_moment_des = (TextView) view.findViewById(R.id.tv_moment_des);
			holder.tv_moment_size = (TextView) view.findViewById(R.id.tv_moment_size);
			holder.tv_moment_time = (TextView) view.findViewById(R.id.tv_moment_time);
			holder.tv_moment_who = (TextView) view.findViewById(R.id.tv_moment_who);
			holder.last_line = (ImageView) view.findViewById(R.id.last_line);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		final DocumentInfo docInfo = (DocumentInfo)getItem(section, position);
		
		if(position==getSectionItemCount(section)-1){
			holder.last_line.setVisibility(view.GONE);
			
		}else{
			holder.last_line.setVisibility(view.VISIBLE);
		}
		holder.tv_moment_des.setText(docInfo.getDescription());
		double size = docInfo.getSize();
		LogUtils.i("====docInfo.getName() = " + docInfo.getName());
		holder.tv_moment_size.setText(FileUtils.sizeFromBToString(size));
		Date createDate = null;
        if (!TextUtils.isEmpty(docInfo.getCreateTime())) {
            try {
            	createDate = BusinessConstants.parseTime(docInfo.getCreateTime());
            } catch (ParseException e) {
            	createDate = null;
            }
        }
        
        if (createDate != null) {
            holder.tv_moment_time.setText(BusinessConstants.getTime(createDate));
        } else {
            holder.tv_moment_time.setText(null);
        }
        UserSimpleInfo usi = docInfo.getFromUser();
        if (usi != null) {
        	holder.tv_moment_who.setText(usi.getName());
        } else {
        	holder.tv_moment_who.setText("");
        }
        
   
        
        final double sizeTemp = docInfo.getSize();
        
        holder.document_centre_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO:下载页面
				String url = docInfo.getDownUrl();
				if (TextUtils.isEmpty(url)) {
					Toast.makeText(context, "对不起，此文件不能下载", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(context,
						DocumentCenterDownloadActivity.class);
				intent.putExtra("DocumentDownloadUri", url);
				intent.putExtra("DocumentDownloadName", docInfo.getName());
				intent.putExtra("DocumentDownloadSize", sizeTemp);
				context.startActivity(intent);
			}
		});
//		final ImageView img_moment = holder.img_moment;
		//TODO 加载远程图片
        //ImageURLConsulter.getInstance(context).consultImageUrl(userId, imageView);
        return view;
	}

	
	class ViewHolder{
		public LinearLayout document_centre_item;
		public ImageView img_moment;
		public TextView tv_moment_who;
		public TextView tv_moment_time;
		public TextView tv_moment_des;
		public TextView tv_moment_size;
		public ImageView last_line;
	}
}
