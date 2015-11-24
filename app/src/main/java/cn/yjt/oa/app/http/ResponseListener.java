package cn.yjt.oa.app.http;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import cn.yjt.oa.app.beans.Response;

public abstract class ResponseListener<T> implements Listener<Response<T>> {

	@Override
	public void onErrorResponse(InvocationError error) {
		System.out.println("onErrorResponse:"+error);
		onFinish();
		afterFinish();
	}

	@Override
	public void onResponse(Response<T> response) {
		System.out.println("onResponse:"+response);
		onFinish();
		if(response.getCode() == Response.RESPONSE_OK){
			onSuccess(response.getPayload());
		}else{
			onErrorResponse(response);
		}
		afterFinish();
	}
	
	public abstract void onSuccess(T payload);
	
	public void onErrorResponse(Response<T> response){
		
	}
	
	public void onFinish(){
		
	}
	
	public void afterFinish(){
		
	}


}
