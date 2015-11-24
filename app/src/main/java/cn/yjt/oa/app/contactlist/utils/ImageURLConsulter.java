package cn.yjt.oa.app.contactlist.utils;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.BitmapUtils;

public class ImageURLConsulter {
    private static ImageURLConsulter sInstance;
    
    private Context mContext;
    private ExecutorService mThreadPool;
    private ContactManager mContactManager;
    
    private ImageURLConsulter(Context context) {
        mContext = context;
        mThreadPool = Executors.newFixedThreadPool(2);
        mContactManager = ContactManager.getContactManager(context);
    }
    
    public static ImageURLConsulter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImageURLConsulter(context);
        }
        return sInstance;
    }
    
    public void consultImageUrl(long userId, final ImageView imageView) {
        imageView.setTag(userId);
        imageView.setImageResource(R.drawable.contactlist_contact_icon_default);
        mThreadPool.submit(new ImageURLConsultTask(userId, imageView));
    }
    
    private class ImageURLConsultTask implements Callable<Object> {
        private ImageView imageView;
        private long userId;
        public ImageURLConsultTask(long userId, ImageView imageView) {
            this.imageView = imageView;
            this.userId = userId;
        }

        @Override
        public Object call() throws Exception {
            ContactInfo info = mContactManager.getContactInfoById(userId);
            if (info == null) {
                getContactInfoFromServer(userId);
            } else {
                if (info.getAvatar() != null) {
                    AsyncRequest.getBitmap(info.getAvatar(), new Listener<Bitmap>() {

                        @Override
                        public void onErrorResponse(
                                InvocationError invocationerror) {
                        
                        }

                        @Override
                        public void onResponse(Bitmap arg0) {
                            switchBitmap(arg0);
                        }
                        
                    });
                }
            }
            
            return new Object();
        }
        
        private void getContactInfoFromServer(final long id) {
            mContactManager.getContactFromServerById(id,
                    new Listener<Response<ContactInfo>>() {

                        @Override
                        public void onErrorResponse(InvocationError error) {
                        
                        }

                        @Override
                        public void onResponse(Response<ContactInfo> response) {
                            ContactInfo info = response.getPayload();
                            if (response.getCode() == AsyncRequest.ERROR_CODE_OK && info.getAvatar() != null) {
                                AsyncRequest.getBitmap(info.getAvatar(), new Listener<Bitmap>() {

                                    @Override
                                    public void onErrorResponse(
                                            InvocationError invocationerror) {
                                    
                                    }

                                    @Override
                                    public void onResponse(Bitmap arg0) {
                                        switchBitmap(arg0);
                                    }
                                    
                                });
                            }
                        }
                    });
        }
        
        private void switchBitmap(Bitmap bitmap) {
            long idTag = (Long)imageView.getTag();
            if (idTag == userId) {
                Bitmap circleBitmap = BitmapUtils.getPersonalHeaderIcon(mContext, bitmap);
                imageView.setImageBitmap(circleBitmap);
            }
        }
        
    }
}
