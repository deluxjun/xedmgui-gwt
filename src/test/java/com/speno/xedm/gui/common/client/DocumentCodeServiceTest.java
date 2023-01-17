package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.gui.common.client.services.DocumentCodeService;
import com.speno.xedm.gui.common.client.services.DocumentCodeServiceAsync;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class DocumentCodeServiceTest extends GWTTestCase {

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
	public void testSecurityService() {
		/* create the service that we will test. */
		DocumentCodeServiceAsync service = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8088/xedm/frontend/documentcode");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		service.listXvarmIndexFields("admin", "DOC", new AsyncCallback<List<String>>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(List<String> result) {

				System.out.println(result);
				assertTrue(true);
				finishTest();
			}
		});
	}

}