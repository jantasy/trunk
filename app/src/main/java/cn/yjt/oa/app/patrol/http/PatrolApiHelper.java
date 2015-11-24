package cn.yjt.oa.app.patrol.http;

import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.InspectInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.PatrolAttendanceInfo;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.beans.PatrolPointAttendanceInfo;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.RouteUser;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.http.AsyncRequest;
import io.luobo.common.Cancelable;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

/**
 * Created by 熊岳岳 on 2015/9/17.
 */
public class PatrolApiHelper {

    /** 获取巡检点列表的接口 */
    public static final String MODULE_PATROL_POINTS = "custinspect/%s/points";
    /** 新建或者更新巡检点的接口*/
    public static final String MODULE_PATROL_POINTS_NEW="custinspect/%s/points";
    /** 删除巡检点的接口 */
    public static final String MODULE_PATROL_POINTS_DELETE="custinspect/%s/points/%s";


    /** 获取巡检路线列表的接口 */
    public static final String MODULE_PATROL_ROUTE = "custinspect/%s/lines/lists";
    /** 新建或者更新巡检点的路线*/
    public static final String MODULE_PATROL_ROUTE_NEW="custinspect/%s/lines";
    /**获取巡检员*/
    public static final String MODULE_PATROL_PERSON= "custinspect/%s/inspectusers";
    /** 获取巡检路线绑定的巡检点*/
    public static final String MODULE_PATROL_POINT_BIND_ROUTE = "custinspect/%s/lines/%s/points";
    /** 获取巡检路线绑定的巡检人员*/
    public static final String MODULE_PATROL_PERSON_BIND_ROUTE = "custinspect/%s/lines/%s/users";
    /** 获取巡检路线绑定的巡检点*/
    public static final String MODULE_PATROL_POINT_UPDATE = "custinspect/%s/lines/%s/points";
    /** 修改巡检路线绑定的巡检人员*/
    public static final String MODULE_PATROL_PERSON_UPDATE = "custinspect/%s/lines/%s/users";
    /** 删除巡检路线*/
    public static final String MODULE_PATROL_ROUTE_DELETE="custinspect/%s/lines/%s";


    /** 获取巡检标签列表的接口*/
    public static final String MODULE_PATROL_TAG = "custinspect/%s/tags/lists";
    /** 分页获取巡检标签列表的接口*/
    public static final String MODULE_PATROL_TAG_FILTER = "custinspect/%s/tags/common";
    /** 新建巡检标签的接口*/
    public static final String MODULE_PATROL_TAG_NEW = "custinspect/%s/tags";
    /** 删除巡检标签*/
    public static final String MODULE_PATROL_TAG_DELETE="custinspect/%s/tags/%s";

    /**设定简约规则*/
    public static final String MODULE_PATROL_TIMES="custinspect/%s/times";
    /**查询简约规则*/
    public static final String MODULE_PATROL_TIMES_SELECT="custinspect/%s/times";
    /**获取简约型巡检规则的状态*/
    public static final String MODULE_PATROL_TIMES_STASTUS="custinspect/%s/times/status";

    /**查询某段时间的巡检记录*/
    public static final String MODULE_PATROL_RECORD="inspectattendance";
    /**查询某条线路巡检详细记录*/
    public static final String MODULE_PATROL_RECORD_POINT="inspectattendance/%s/attendancepoints";


    /**巡检签到签到接口*/
    public static final String MODULE_PATROL_CHECKIN = "inspectins/attendances";

    /*巡检报表导出*/
    /**巡检汇总表*/
    public static final String MODULE_PATROL_SUMMARY = "inspectattendance/summary";
    /**巡检明细表*/
    public static final String MODULE_PATROL_DETAIL = "inspectattendance/detail";
    /**巡检违纪表*/
    public static final String MODULE_PATROL_LEAK = "inspectattendance/loss";
    /**巡检签到明细表*/
    public static final String MODULE_PATROL_CHECK = "inspectattendance/signin";
    /*上面四个无法单独使用，须在后面拼上下面两个中的其中任意一个*/
    /**下载*/
    public static final String PATROL_DOWNLOAD = "/export";
    /**发送邮箱*/
    public static final String PATROL_SENDMAIL = "/sendmail";

    /*--------------------------------------巡检点--------------------------------------*/
    /** 分页请求巡检点 */
    public static Cancelable getPatrolPointByPage(Listener<Response<ListSlice<PatrolPoint>>> listener,String custId,
                                                  int from, int max) {
        Type responseType = new TypeToken<Response<ListSlice<PatrolPoint>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINTS,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addPageQueryParameter(from, max)
                .build().get();
    }

    /**请求所有巡检点*/
    public static Cancelable getPatrolPoint(Listener<Response<List<PatrolPoint>>> listener,String custId) {
        Type responseType = new TypeToken<Response<List<PatrolPoint>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINTS,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }

    /**  新建或者更新巡检点 */
    public static Cancelable addOrUpdatePatrolPoint(Listener<Response<PatrolPoint>> listener, String custId, PatrolPoint info) {
        Type responseType = new TypeToken<Response<PatrolPoint>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINTS_NEW,custId))
                .setResponseType(responseType)
                .setRequestBody(info)
                .setResponseListener(listener)
                .build().post();
    }

    /**  删除巡检点 */
    public static Cancelable deletePatrolPoint(Listener<Response<Object>> listener,String custId,String pointId) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINTS_DELETE, custId, pointId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().delete();
    }

    /*--------------------------------------巡检路线--------------------------------------*/
    /** 请求所有巡检路线 */
    public static Cancelable getPatrolRoute(Listener<Response<ListSlice<PatrolRoute>>> listener,String custId,
                                            int from, int max) {
        Type responseType = new TypeToken<Response<ListSlice<PatrolRoute>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_ROUTE,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addPageQueryParameter(from, max)
                .build().get();
    }

    /** 请求和路线有关联的巡检点 */
    public static Cancelable getPatrolPointBindRoute(Listener<Response<PatrolRoute>> listener,String custId,String lineId
    ) {
        Type responseType = new TypeToken<Response<PatrolRoute>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINT_BIND_ROUTE,custId,lineId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }

    /** 请求和路线有关联的巡检人员 */
    public static Cancelable getPatrolPerson(Listener<Response<ListSlice<RouteUser>>> listener,String custId
    ) {
        Type responseType = new TypeToken<Response<ListSlice<RouteUser>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_PERSON,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }

    /** 请求和路线有关联的巡检人员 */
    public static Cancelable getPatrolPersonBindRoute(Listener<Response<ListSlice<RouteUser>>> listener,String custId,String lineId
    ) {
        Type responseType = new TypeToken<Response<ListSlice<RouteUser>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_PERSON_BIND_ROUTE,custId,lineId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }

    /**  新建或者更新巡检路线 */
    public static Cancelable addOrUpdatePatrolRoute(Listener<Response<PatrolRoute>> listener,String custId,PatrolRoute info) {
        Type responseType = new TypeToken<Response<PatrolRoute>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_ROUTE_NEW,custId))
                .setResponseType(responseType)
                .setRequestBody(info)
                .setResponseListener(listener)
                .build().post();
    }

    /**  修改和路线有关联的巡检人员 */
    public static Cancelable bindPatrolPoint(Listener<Response<Object>> listener,String custId,String lineId,List<PatrolPoint> infos) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_POINT_UPDATE,custId,lineId))
                .setResponseType(responseType)
                .setRequestBody(infos)
                .setResponseListener(listener)
                .build().put();
    }

    /**  修改和路线有关联的巡检人员 */
    public static Cancelable bindPatrolPerson(Listener<Response<Object>> listener,String custId,String lineId,List<RouteUser> infos) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_PERSON_UPDATE,custId,lineId))
                .setResponseType(responseType)
                .setRequestBody(infos)
                .setResponseListener(listener)
                .build().put();
    }

    /**  删除巡检路线 */
    public static Cancelable deletePatrolRoute(Listener<Response<Object>> listener,String custId,String lineId) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_ROUTE_DELETE, custId, lineId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().delete();
    }


    /*--------------------------------------巡检标签--------------------------------------*/
    /** 分页请求巡检标签 */
    public static Cancelable getPatrolTagByPage(Listener<Response<ListSlice<PatrolTag>>> listener,String custId,
                                                  int from, int max) {
        Type responseType = new TypeToken<Response<ListSlice<PatrolTag>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TAG,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addPageQueryParameter(from, max)
                .build().get();
    }

    /** 分页过滤请求巡检标签 */
    public static Cancelable getPatrolTagByPage(Listener<Response<ListSlice<PatrolTag>>> listener,String custId,
                                                int from, int max,String filter) {
        if(TextUtils.isEmpty(filter)){
            return getPatrolTagByPage(listener,custId,from,max);
        }
        Type responseType = new TypeToken<Response<ListSlice<PatrolTag>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TAG_FILTER,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addPageQueryParameter(from, max)
                .addQueryParameter("filter",filter)
                .build().get();
    }

    /**  新建或者更新巡检点 */
    public static Cancelable addPatrolTag(Listener<Response<PatrolTag>> listener, String custId, PatrolTag info) {
        Type responseType = new TypeToken<Response<PatrolTag>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TAG_NEW,custId))
                .setResponseType(responseType)
                .setRequestBody(info)
                .setResponseListener(listener)
                .build().post();
    }

    /**  删除巡检点 */
    public static Cancelable deletePatrolTag(Listener<Response<Object>> listener,String custId,String tagId) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TAG_DELETE, custId, tagId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().delete();
    }

    /*--------------------------------------巡检时间--------------------------------------*/
    public static Cancelable setPatrolTime(Listener<Response<Object>> listener,String custId,List<AttendanceTime> infos) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TIMES,custId))
                .setResponseType(responseType)
                .setRequestBody(infos)
                .setResponseListener(listener)
                .build().put();
    }

    public static Cancelable getPatrolTime(Listener<Response<ListSlice<AttendanceTime>>> listener,String custId) {
        Type responseType = new TypeToken<Response<ListSlice<AttendanceTime>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TIMES_SELECT,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }


    public static Cancelable getPatrolTimeStatus(Listener<Response<Boolean>> listener,String custId) {
        Type responseType = new TypeToken<Response<Boolean>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TIMES_STASTUS,custId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().get();
    }



    public static Cancelable setPatrolTimeStatus(Listener<Response<Object>> listener,String custId,Boolean enable) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_TIMES,custId) + "/"
                + (enable ? "enable" : "disable"))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build().put();
    }

    /*-------------------------------巡检记录-----------------------------------*/
    public static Cancelable getPatrolRecord(Listener<Response<List<List<PatrolAttendanceInfo>>>> listener,String startDate,String endDate) {
        Type responseType = new TypeToken<Response<List<List<PatrolAttendanceInfo>>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(PatrolApiHelper.MODULE_PATROL_RECORD)
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addQueryParameter("startDate", startDate)
                .addQueryParameter("endDate", endDate)
                .build().get();
    }

    public static Cancelable getPatrolDetailRecord(Listener<Response<List<PatrolPointAttendanceInfo>>> listener,String lineId,String dutyDate,String clientinST) {
        Type responseType = new TypeToken<Response<List<PatrolPointAttendanceInfo>>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(String.format(PatrolApiHelper.MODULE_PATROL_RECORD_POINT,lineId))
                .setResponseType(responseType)
                .setResponseListener(listener)
                .addQueryParameter("dutyDate", dutyDate)
                .addQueryParameter("clientinST",clientinST)
                .build().get();
    }
    /*---------------------------------巡检签到-------------------------------------*/
    public static Cancelable patrolCheckIn(Listener<Response<ICheckInType>> listener,InspectInfo info) {
        Type responseType = new TypeToken<Response<InspectInfo>>() {
        }.getType();
        return new AsyncRequest.Builder().setModule(PatrolApiHelper.MODULE_PATROL_CHECKIN)
                .setResponseType(responseType)
                .setRequestBody(info)
                .setResponseListener(listener)
                .build().post();
    }

    /** 发送巡检报表到邮箱 */
    public static Cancelable emailPatrolExcel(Listener<Response<String>> listener, String email, String url,Date beginDate,Date endDate) {
        Type responseType = new TypeToken<Response<String>>() {
        }.getType();
        return new AsyncRequest.Builder()
                .setModule(url)
                .addQueryParameter("email", email)
                .addDateQueryParameter(beginDate,endDate)
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build()
                .get();
    }
}
