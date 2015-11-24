package cn.yjt.oa.app.patrol.protocol;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.NFCTagInfo;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;

/**
 * @author 熊岳岳
 */
public class PatrolTagListProtocol {

    private static PatrolTagListProtocol mProtocol;

    private int mStartIndex = 0;
    private final int MAX_COUNT = 10;
    private OnRequestPatrolTagListListener mListener;

    /*-----单例------*/
    private PatrolTagListProtocol() {

    }

    public static PatrolTagListProtocol getInstance() {
        if (mProtocol == null) {
            synchronized (PatrolTagListProtocol.class) {
                mProtocol = new PatrolTagListProtocol();
            }
        }
        return mProtocol;
    }

	/*--------------*/

    public void requestRefreshDatas() {
        mStartIndex = 0;
        PatrolApiHelper.getPatrolTagByPage(new ResponseListener<ListSlice<PatrolTag>>() {
            @Override
            public void onSuccess(ListSlice<PatrolTag> payload) {
                if (mListener != null) {
                    mListener.refreshPatrolPatrolTagListSuccess(payload.getContent());
                }
            }
        }, AccountManager.getCurrent(MainApplication.getAppContext()).getCustId(), mStartIndex, MAX_COUNT);
    }

    public void requestLoadMoreDatas() {
        mStartIndex += MAX_COUNT;
        PatrolApiHelper.getPatrolTagByPage(new ResponseListener<ListSlice<PatrolTag>>() {
            @Override
            public void onSuccess(ListSlice<PatrolTag> payload) {
                if (mListener != null) {
                    mListener.loadMorePatrolPatrolTagListSuccess(payload.getContent());
                }
            }
        }, AccountManager.getCurrent(MainApplication.getAppContext()).getCustId(), mStartIndex, MAX_COUNT);
    }

    public void filterRefreshDatas(String filter) {
        mStartIndex = 0;
        PatrolApiHelper.getPatrolTagByPage(new ResponseListener<ListSlice<PatrolTag>>() {
            @Override
            public void onSuccess(ListSlice<PatrolTag> payload) {
                if (mListener != null) {
                    mListener.refreshPatrolPatrolTagListSuccess(payload.getContent());
                }
            }
        }, AccountManager.getCurrent(MainApplication.getAppContext()).getCustId(), mStartIndex, MAX_COUNT,filter);
    }

    public void filterLoadMoreDatas(String filter) {
        mStartIndex += MAX_COUNT;
        PatrolApiHelper.getPatrolTagByPage(new ResponseListener<ListSlice<PatrolTag>>() {
            @Override
            public void onSuccess(ListSlice<PatrolTag> payload) {
                if (mListener != null) {
                    mListener.loadMorePatrolPatrolTagListSuccess(payload.getContent());
                }
            }
        }, AccountManager.getCurrent(MainApplication.getAppContext()).getCustId(), mStartIndex, MAX_COUNT,filter);
    }
    /*-----回调接口-----*/
    public interface OnRequestPatrolTagListListener {
        public void refreshPatrolPatrolTagListSuccess(List<PatrolTag> datas);
        public void loadMorePatrolPatrolTagListSuccess(List<PatrolTag> datas);
    }

    public void setOnRequestPatrolTagListListener(OnRequestPatrolTagListListener mListener) {
        this.mListener = mListener;
    }
}
