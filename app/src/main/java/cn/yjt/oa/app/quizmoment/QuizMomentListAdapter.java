package cn.yjt.oa.app.quizmoment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DocumentInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.widget.TimeLineAdapter;

public class QuizMomentListAdapter extends TimeLineAdapter {
	private Context context;
	private LayoutInflater inflater;
	private int imageWidth;
	private int imageHeight;
	private ImageLoader imageLoader;

	public QuizMomentListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		imageWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 120, context.getResources()
						.getDisplayMetrics());
		imageHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources()
						.getDisplayMetrics());
		imageLoader = MainApplication.getImageLoader();
	}

	/**
	 * 检索出精彩瞬间的时间
	 * 
	 * @param docInfo
	 * @return
	 */
	private Date retrieveTaskDate(DocumentInfo docInfo) {
		Date createDate = null;
		if (!TextUtils.isEmpty(docInfo.getCreateTime())) {
			try {
				createDate = BusinessConstants.parseTime(docInfo
						.getCreateTime());
			} catch (Exception e) {
				createDate = null;
			}
		}
		return createDate;
	}

	/**
	 * 将一条精彩瞬间记录添加到adapter中
	 * 
	 * @param docInfo
	 */
	public void addMoment2Adapter(DocumentInfo docInfo) {
		Date createDate = retrieveTaskDate(docInfo);
		if (createDate == null) {
			return;
		}
		addEntry(createDate, docInfo);
	}

	/**
	 * 添加多条精彩瞬间到adapter中
	 * 
	 * @param infos
	 */
	public void addMoment2Adapter(List<DocumentInfo> infos) {
		if (infos == null) {
			return;
		}
		for (DocumentInfo info : infos) {
			addMoment2Adapter(info);
		}
	}

	@Override
	public View getSectionView(int section, View convertView, ViewGroup parent) {
		View sectionView = convertView;
		if (sectionView == null) {
			sectionView = inflater.inflate(R.layout.message_center_item_title,
					parent, false);
		}
		TextView sectionTitle = (TextView) sectionView
				.findViewById(R.id.title_tv);
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
		if (view == null) {
			view = inflater.inflate(R.layout.moment_list_item, parent, false);
			holder = new ViewHolder();
			holder.img_moment = (ImageView) view.findViewById(R.id.img_moment);
			holder.tv_moment_des = (TextView) view
					.findViewById(R.id.tv_moment_des);
			holder.tv_moment_loc = (TextView) view
					.findViewById(R.id.tv_moment_loc);
			holder.tv_moment_time = (TextView) view
					.findViewById(R.id.tv_moment_time);
			holder.tv_moment_who = (TextView) view
					.findViewById(R.id.tv_moment_who);
			holder.last_line = (ImageView) view.findViewById(R.id.last_line);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final DocumentInfo docInfo = (DocumentInfo) getItem(section, position);
		if (position == getSectionItemCount(section) - 1) {
			holder.last_line.setVisibility(view.GONE);
		} else {
			holder.last_line.setVisibility(view.VISIBLE);
		}

		holder.tv_moment_des.setText(docInfo.getDescription());
		holder.tv_moment_loc.setText(docInfo.getAddress());
		Date createDate = null;
		if (!TextUtils.isEmpty(docInfo.getCreateTime())) {
			try {
				createDate = BusinessConstants.parseTime(docInfo
						.getCreateTime());
			} catch (ParseException e) {
				createDate = null;
			}
		}

		if (createDate != null) {
			holder.tv_moment_time.setText(new SimpleDateFormat("HH:mm")
					.format(createDate));
		} else {
			holder.tv_moment_time.setText(null);
		}
		holder.tv_moment_who.setText(docInfo.getFromUser().getName());
		// final ImageView img_moment = holder.img_moment;
		// TODO 加载远程图片
		if (docInfo.getDownUrl() != null)
			loadImage(docInfo.getDownUrl(), holder.img_moment);
		// AsyncRequest.getInImageView(docInfo.getDownUrl(), holder.img_moment,
		// R.drawable.moment, R.drawable.moment);
		return view;
	}

	private void loadImage(final String url, final ImageView imageView) {
		imageView.setTag(url);
		imageView.setImageResource(R.drawable.default_image);
		imageLoader.get(url, imageWidth, imageHeight,
				new ImageLoaderListener() {

					@Override
					public void onSuccess(
							final cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer container) {
						if (imageView.getTag().equals(container.getUrl())) {
							imageView.post(new Runnable() {

								@Override
								public void run() {
									imageView.setImageBitmap(container
											.getBitmap());
								}
							});
						}
					}

					@Override
					public void onError(
							cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer container) {

					}
				});
		/*
		 * new ImageListener() {
		 * 
		 * @Override public void onErrorResponse(VolleyError arg0) {
		 * 
		 * }
		 * 
		 * @Override public void onResponse(ImageContainer arg0, boolean arg1) {
		 * if(imageUrl==imageView.getTag()){ Bitmap bitmap = arg0.getBitmap();
		 * if(bitmap!=null){ imageView.setImageBitmap(bitmap); } } } }
		 */
	}

	class ViewHolder {
		public ImageView img_moment;
		public TextView tv_moment_who;
		public TextView tv_moment_time;
		public TextView tv_moment_des;
		public TextView tv_moment_loc;
		public ImageView last_line;
	}
}
