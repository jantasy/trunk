package cn.yjt.oa.app.utils;

import cn.yjt.oa.app.beans.OperaStatistics;

/**
 * Created by xiong on 2015/10/9.
 */
public class OperaEventUtils {

    public static void recordOperation(String operaEventNo){
        try{
            new OperaStatistics(operaEventNo).save();
//            ToastUtils.shortToastShow(operaEventNo+"操作被记录");
        }catch(Exception e){

        }

    }

    public static void recordOperation(String operaEventNo,long userId,String custId){
        try{
            new OperaStatistics(operaEventNo,userId,custId).save();
        }catch(Exception e){

        }
    }
}
