package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.services.ECMService;
import com.speno.xedm.gui.common.client.services.ECMServiceAsync;
import com.speno.xedm.gui.common.client.util.PagingConfig;
import com.speno.xedm.gui.common.client.util.PagingResult;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class ECMServiceTest extends GWTTestCase {

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
		ECMServiceAsync service = (ECMServiceAsync) GWT.create(ECMService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8088/xedm/frontend/ecm");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		PagingConfig config = new PagingConfig();
		config.setLimit(10);
//		config.getOrderByField();
		service.pagingDocuments("admin", "DOC", config, new AsyncCallback<PagingResult<SDocument>>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(PagingResult<SDocument> result) {
				System.out.println("total : " + result.getTotalLength());
				List<SDocument> list = result.getData();
				for (SDocument doc : list) {
					System.out.println(doc.getId());
				}
				assertTrue(true);
				finishTest();
			}
		});
	}

}