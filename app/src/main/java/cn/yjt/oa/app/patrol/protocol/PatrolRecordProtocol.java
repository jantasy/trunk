package cn.yjt.oa.app.patrol.protocol;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.bean.PatrolRecord;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.LogUtils;


/**
 * 
 * @author 熊岳岳
 * 
 */
public class PatrolRecordProtocol {

    private final String TAG = "PatrolPointProtocol";

	private static PatrolRecordProtocol mProtocol;

	/**分页查询起始页*/
	private int mStartIndex = 0;
	/**分页查询每次查询的数量*/
	private final int MAX_COUNT = 15;

	/**查询成功的回调接口*/
	private OnRequestPatrolRecordListener mListener;

	/*-----单例------*/
	private PatrolRecordProtocol() {

	}

	public static PatrolRecordProtocol getInstance() {
		if (mProtocol == null) {
			synchronized (PatrolRecordProtocol.class) {
				mProtocol = new PatrolRecordProtocol();
			}
		}
		return mProtocol;
	}

	/*--------------*/

	public void requestRefreshDatas(Context context) {

        List<PatrolRecord> list = new ArrayList<>();an
        for(int i=0;i<10;i++){
            PatrolRecord info = new PatrolRecord();
            info.setName("大厦内巡检"+i);
            info.setPlanStartTime("2015-9-23 6：00");
            info.setCompPointCount(10);
            info.setUncompPointCount(10);
            info.setStatus("已完成");
            list.add(info);
        }
        if (mListener != null) {
            mListener.refreshPatrolRecordSuccess(list);
        }
	}

	public void requestLoadMoreDatas(Context context) {
        List<PatrolRecord> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            PatrolRecord info = new PatrolRecord();
            info.setName("大厦内巡检"+i);
            info.setPlanStartTime("2015-9-23 6：00");
            info.setCompPointCount(10);
            info.setUncompPointCount(10);
            info.setStatus("已完成");
            list.add(info);
        }
        if (mListener != null) {
            mListener.loadMorePatrolRecordSuccess(list);
        }
	}

	/*-----回调接口-----*/
	public interface OnRequestPatrolRecordListener {
		
		public void refreshPatrolRecordSuccess(List<PatrolRecord> datas);
		public void loadMorePatrolRecordSuccess(List<PatrolRecord> datas);
	}

	public void setOnRequestPatrolPointListener(OnRequestPatrolRecordListener mListener) {
		this.mListener = mListener;
	}
}
