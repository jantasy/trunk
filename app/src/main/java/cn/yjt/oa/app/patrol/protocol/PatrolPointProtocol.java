package cn.yjt.oa.app.patrol.protocol;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.LogUtils;


/**
 * 
 * @author 熊岳岳
 * 
 */
public class PatrolPointProtocol {

    private final String TAG = "PatrolPointProtocol";

	private static PatrolPointProtocol mProtocol;

	/**分页查询起始页*/
	private int mStartIndex = 0;
	/**分页查询每次查询的数量*/
	private final int MAX_COUNT = 10;
	
	/**查询成功的回调接口*/
	private OnRequestPatrolPointListener mListener;

	/*-----单例------*/
	private PatrolPointProtocol() {

	}

	public static PatrolPointProtocol getInstance() {
		if (mProtocol == null) {
			synchronized (PatrolPointProtocol.class) {
				mProtocol = new PatrolPointProtocol();
			}
		}
		return mProtocol;
	}

	/*--------------*/

	public void requestRefreshDatas(Context context) {
        mStartIndex = 0;
        String custId = AccountManager.getCurrent(context).getCustId();
        PatrolApiHelper.getPatrolPointByPage(new ResponseListener<ListSlice<PatrolPoint>>() {
            @Override
            public void onSuccess(ListSlice<PatrolPoint> payload) {
                LogUtils.i(TAG, payload.getContent().toString());
                if (mListener != null) {
                    mListener.refreshPatrolPointSuccess(payload.getContent());
                }
            }
        },custId,mStartIndex,MAX_COUNT);
	}

	public void requestLoadMoreDatas(Context context) {
        mStartIndex+=MAX_COUNT;
        String custId = AccountManager.getCurrent(context).getCustId();
        PatrolApiHelper.getPatrolPointByPage(new ResponseListener<ListSlice<PatrolPoint>>() {
            @Override
            public void onSuccess(ListSlice<PatrolPoint> payload) {
                LogUtils.i(TAG, payload.getContent().toString());
                if (mListener != null) {
                    mListener.loadMorePatrolPointSuccess(payload.getContent());
                }
            }
        },custId,mStartIndex,MAX_COUNT);
	}

	/*-----回调接口-----*/
	public interface OnRequestPatrolPointListener {
		
		public void refreshPatrolPointSuccess(List<PatrolPoint> datas);
		public void loadMorePatrolPointSuccess(List<PatrolPoint> datas);
	}

	public void setOnRequestPatrolPointListener(OnRequestPatrolPointListener mListener) {
		this.mListener = mListener;
	}
}
