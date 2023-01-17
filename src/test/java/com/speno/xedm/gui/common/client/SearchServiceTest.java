package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SResult;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.services.SearchService;
import com.speno.xedm.gui.common.client.services.SearchServiceAsync;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class SearchServiceTest extends GWTTestCase {

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
		SearchServiceAsync service = (SearchServiceAsync) GWT.create(SearchService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8088/xedm/frontend/search");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		SSearchOptions opt = new SSearchOptions();

		opt.setType(SSearchOptions.TYPE_FULLTEXT);

//		opt.setLanguage("ko");
		opt.setExpression("ECM");
//		opt.setFields(new String[] { "content", "title" });
//		opt.setExpressionLanguage("ko");
//		opt.setType(SearchOptions.TYPE_FULLTEXT);
//		opt.setUserId(2);
		
		service.search("DEVMODE", opt, new AsyncCallback<SResult>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(SResult result) {
				assertTrue(true);
				finishTest();
			}
		});
	}

}