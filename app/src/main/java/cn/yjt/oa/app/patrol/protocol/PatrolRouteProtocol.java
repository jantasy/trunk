package cn.yjt.oa.app.patrol.protocol;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.ApiAdapter;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;


/**
 * @author 熊岳岳
 */
public class PatrolRouteProtocol {

    private static PatrolRouteProtocol mProtocol;

    private int mStartIndex = 0;
    private final int MAX_COUNT = 10;
    private OnRequestPatrolRouteListener mListener;

    /*-----单例------*/
    private PatrolRouteProtocol() {

    }

    public static PatrolRouteProtocol getInstance() {
        if (mProtocol == null) {
            synchronized (PatrolRouteProtocol.class) {
                mProtocol = new PatrolRouteProtocol();
            }
        }
        return mProtocol;
    }

	/*--------------*/

    public void requestRefreshDatas(final Context context) {
        mStartIndex = 0;
        String custId = AccountManager.getCurrent(context).getCustId();
        PatrolApiHelper.getPatrolRoute(new ResponseListener<ListSlice<PatrolRoute>>() {
            @Override
            public void onSuccess(ListSlice<PatrolRoute> payload) {
                if (mListener != null) {
                    mListener.refreshPatrolRouteSuccess(payload.getContent());
                }
            }
        }, custId,mStartIndex,MAX_COUNT);
    }

    public void requestLoadMoreDatas(Context context) {
        mStartIndex += MAX_COUNT;
        String custId = AccountManager.getCurrent(context).getCustId();
        PatrolApiHelper.getPatrolRoute(new ResponseListener<ListSlice<PatrolRoute>>() {
            @Override
            public void onSuccess(ListSlice<PatrolRoute> payload) {
                if (mListener != null) {
                    mListener.loadMorePatrolRouteSuccess(payload.getContent());
                }
            }
        }, custId,mStartIndex,MAX_COUNT);
    }

    /*-----回调接口-----*/
    public interface OnRequestPatrolRouteListener {

        /** 刷新成功 */
        public void refreshPatrolRouteSuccess(List<PatrolRoute> datas);

        /** 加载更多成功 */
        public void loadMorePatrolRouteSuccess(List<PatrolRoute> datas);
    }

    public void setOnRequestPatrolRouteListener(OnRequestPatrolRouteListener mListener) {
        this.mListener = mListener;
    }
}
