package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.gui.common.client.services.InfoService;
import com.speno.xedm.gui.common.client.services.InfoServiceAsync;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class InfoServiceTest extends GWTTestCase {

	/**
	 * must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		// Frontend 는 smartclient js도 로드하는데.. isc 스크립트오류로 테스트가 현재 잘 안됨.
//		return "com.speno.xedm.gui.frontend.Frontend";
		return "com.speno.xedm.gui.common.Common";
	}

	/**
	 * this test will send a request to the server using the greetServer method
	 * in GreetingService and verify the response.
	 */
	public void testInfoService() {
		/* create the service that we will test. */
		InfoServiceAsync infoService = (InfoServiceAsync) GWT.create(InfoService.class);

		ServiceDefTarget target = (ServiceDefTarget) infoService;
		target.setServiceEntryPoint(Util.contextPath() + "frontend/info");
//		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "frontend/info");

		delayTestFinish(10000); // timeout

		infoService.getInfo("en", new AsyncCallback<SInfo>() {
			public void onFailure(Throwable error) {
				assertTrue(false);
			}

			public void onSuccess(final SInfo info) {
				assertEquals("this_is_mock", info.getInstallationId());
				assertEquals(3, info.getSupportedGUILanguages().length);
				finishTest();
			}
		});

	}
	
	public void testTrue(){
		assertTrue(true);
	}
}