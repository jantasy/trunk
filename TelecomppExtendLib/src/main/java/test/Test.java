package test;

import com.telecompp.ContextUtil;
import com.telecompp.engine.InitEngine;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase{

	public void testInitEnv(){
		ContextUtil.setInstance(getContext());
		assertEquals(0, InitEngine.initEnv(""));
	}
}
