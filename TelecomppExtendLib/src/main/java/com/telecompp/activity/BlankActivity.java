package com.telecompp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.telecompp.ContextUtil;
import com.telecompp.engine.PaymentEngine;

public class BlankActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PaymentEngine.getInstance(this, null).pay(
				ContextUtil.getPhoneNum());
	}
}
