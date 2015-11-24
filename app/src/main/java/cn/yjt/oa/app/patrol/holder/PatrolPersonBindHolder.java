package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.RouteUser;
import cn.yjt.oa.app.imageloader.ImageLoader;


/**
 * 巡检点的holder
 *
 * @author 熊岳岳
 * @since 20150906
 */
public class PatrolPersonBindHolder extends YjtBaseHolder<RouteUser> {

    private final String TAG = "PatrolPersonBindHolder";

    public TextView mTvName;
    public CheckBox mCbSelected;
    public ImageView mIvIcon;

    public PatrolPersonBindHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.item_attendance_member, null);
        mTvName = (TextView) mConvertView.findViewById(R.id.attendance_member_name);
        mIvIcon = (ImageView) mConvertView.findViewById(R.id.attendance_member_icon);
        mCbSelected = (CheckBox) mConvertView.findViewById(R.id.attendance_member_checkbox);
        return mConvertView;
    }

    @Override
    public void refreshView(int position,  RouteUser info) {
        mTvName.setText(info.getName());
        if (info.isSelected()) {
            mCbSelected.setChecked(true);
        }else{
            mCbSelected.setChecked(false);
        }
        mIvIcon.setTag(info.getAvatar());
        mIvIcon.setImageResource(R.drawable.contactlist_contact_icon_default);
    }

    public void refreshViewWithScrolling(int position,  RouteUser info,boolean isScrolling) {
        refreshView(position,info);
        if (!isScrolling) {
//				LogUtils.e(TAG, "位置：" + areaUser.getName());
            MainApplication.getHeadImageLoader().get(info.getAvatar(), new ImageLoader.ImageLoaderListener() {

                @Override
                public void onSuccess(ImageLoader.ImageContainer container) {
                    if (TextUtils.equals(mIvIcon.getTag().toString(), container.getUrl())) {
                        mIvIcon.setImageBitmap(container.getBitmap());
                    }
                }

                @Override
                public void onError(ImageLoader.ImageContainer container) {
                    if (TextUtils.equals(mIvIcon.getTag().toString(), container.getUrl())) {
                        mIvIcon.setImageResource(R.drawable.contactlist_contact_icon_default);
                    }
                }
            });
        }
    }
}
