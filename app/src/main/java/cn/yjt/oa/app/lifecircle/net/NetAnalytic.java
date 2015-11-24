package cn.yjt.oa.app.lifecircle.net;

import android.util.Log;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.model.Word;
import cn.yjt.oa.app.lifecircle.net.MyException.JsonException;
import cn.yjt.oa.app.lifecircle.net.MyException.TimeOutException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class NetAnalytic {
    private static final String TAG = "NetAnalytic";

    public ArrayList<Netable> jsonList(String str) throws TimeOutException, JsonException {
        if (responseFilter(str)) {
            try {
                return (ArrayList) new Gson().fromJson(str, new TypeToken<ArrayList<Netable>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsonException();
            }
        }
        throw new TimeOutException();
    }

    public Netable jsonObj(String str) throws TimeOutException, JsonException {
        if (responseFilter(str)) {
            try {
                return (Netable) new Gson().fromJson(str, new TypeToken<Netable>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsonException();
            }
        }
        throw new TimeOutException();
    }

    public Word jsonObj2(String str) throws TimeOutException, JsonException {
        if (responseFilter(str)) {
            try {
                return (Word) new Gson().fromJson(str, new TypeToken<Word>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsonException();
            }
        }
        throw new TimeOutException();
    }

    public void jsonString(String str) throws TimeOutException {
        if (!responseFilter(str)) {
            throw new TimeOutException();
        }
    }

    public boolean responseFilter(String str) {
        boolean z = false;
//        if (!str.equals(RContactStorage.PRIMARY_KEY)) {
            if (str.toLowerCase().equals("netwrong")) {
                Log.d(TAG, "网络异常:yzsd-netwrong");
            } else if (str.toLowerCase().equals("netmissing")) {
                Log.d(TAG, "网络异常:yzsd-netmissing");
            } else if (str.toLowerCase().equals("requestfailed")) {
                Log.d(TAG, "网络异常:requestfailed");
            } else if (str.toLowerCase().equals("no")) {
                Log.d(TAG, "网络异常:no");
            } else {
                z = true;
            }
//        }
        Log.d(TAG, "解析后网络情况:" + z);
        return z;
    }
}
