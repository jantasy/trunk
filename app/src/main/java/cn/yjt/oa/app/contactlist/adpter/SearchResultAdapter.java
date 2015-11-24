package cn.yjt.oa.app.contactlist.adpter;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.utils.MD5Utils;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.contactlist.ContactlistFragment;
import cn.yjt.oa.app.contactlist.data.SearchResultInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.BitmapUtils;

public class SearchResultAdapter extends BaseAdapter {
	private Context mContext;
	private boolean isAttached;
	private List<SearchResultInfo> searchResult = Collections.emptyList();
	
	public SearchResultAdapter(Context context,boolean isAttached){
		this.mContext=context;
		this.isAttached=isAttached;
	}
	@Override
	public int getCount() {
		return searchResult.size();
	}

	@Override
	public Object getItem(int position) {
		return searchResult.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public void setSearchResult(List<SearchResultInfo> searchResult) {
		this.searchResult = searchResult;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contactlist_search_result_item, null);
			holder = new Holder();
			holder.name = (TextView) convertView
					.findViewById(R.id.result_item_name);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.result_item_icon);
			convertView.setTag(holder);
		}
		holder = (Holder) convertView.getTag();
		SearchResultInfo info = searchResult.get(position);
		setResult(holder, info, holder.icon);
		return convertView;
	}

	private void setResult(Holder holder, SearchResultInfo info,
			ImageView imageView) {
		int page = info.currPage;
		if (page == ContactlistFragment.PAGER_ALL) {
			holder.name.setText(info.cContactInfo.getContactInfo()
					.getName());
			loadImage(info.cContactInfo.getContactInfo().getAvatar(),
					imageView, R.drawable.contactlist_contact_icon_default);
		} else if (page == ContactlistFragment.PAGER_GROUPS) {
			holder.name.setText(info.cGroupInfo.getInfo().getName());
			loadImage(info.cGroupInfo.getInfo().getAvatar(), imageView,
					R.drawable.contact_group_icon_default);
		} else if (page == ContactlistFragment.PAGER_PUANLIC_SERVICE) {
			holder.name.setText(info.commonContactInfo.getName());
			loadImage(null, imageView,
					R.drawable.contactlist_contact_icon_default);
		}
	}

	class Holder {
		TextView name;
		ImageView icon;
	}
	
	private void loadImage(String url, final ImageView imageView,
			final int dfResId) {
		// setDefaultImage(imageView, dfResId);
		imageView.setImageResource(dfResId);
		if (!isAttached || TextUtils.isEmpty(url)) {
			return;
		}
		final String tag = MD5Utils.md5(url);
		imageView.setTag(tag);
		AsyncRequest.getBitmap(url, new Listener<Bitmap>() {

			@Override
			public void onErrorResponse(InvocationError invocationerror) {
				onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId), tag);
			}

			@Override
			public void onResponse(Bitmap bitmap) {
				if (bitmap != null) {
					onBitmapLoadFinish(imageView, bitmap, tag);
				} else {
					onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId),
							tag);
				}
			}
		});
	}

	private void onBitmapLoadFinish(ImageView imageView, Bitmap bmp, String tag) {
		if (isAttached && bmp != null) {
			String imageTag = (String) imageView.getTag();
			if (!tag.equals(imageTag)) {
				return;
			}
			bmp = BitmapUtils.getPersonalHeaderIcon(mContext, bmp);
			imageView.setImageBitmap(bmp);
		}

	}


	private Bitmap getDefaultBitmap(int resId) {
		Resources res = mContext.getResources();
		if (!isAttached || res == null) {
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
	}
}
