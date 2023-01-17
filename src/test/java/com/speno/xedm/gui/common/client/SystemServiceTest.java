package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SHistory;
import com.speno.xedm.core.service.serials.SHistorySearchOptions;
import com.speno.xedm.core.service.serials.SStatSearchOptions;
import com.speno.xedm.gui.common.client.services.SystemService;
import com.speno.xedm.gui.common.client.services.SystemServiceAsync;
import com.speno.xedm.gui.common.client.util.PagingResult;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class SystemServiceTest extends GWTTestCase {

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
		SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8089/xedm/frontend/system");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		SStatSearchOptions o = new SStatSearchOptions();
		o.setDateFrom("2013");
		o.setMax(10);

		service.listGraphStat("admin", "event", "SUBSTRING(regDate,1,6)", o, new AsyncCallback<List<String[]>>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(List<String[]> result) {
				System.out.println("total : " + result.size());
				for (String[] strings : result) {
					String mer = "";
					for(String s : strings){
						mer += s + ",";
					}
					System.out.println(mer);
				}

				assertTrue(true);
				finishTest();
			}
		});
	}
	
	public void testHistory() {
		/* create the service that we will test. */
		final SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8089/xedm/frontend/system");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		final SHistorySearchOptions o = new SHistorySearchOptions();
//		o.setDateFrom("2013");
//		o.setMax(10);

		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				service.pagingHistory("DEVMODE", o, new AsyncCallback<PagingResult<SHistory>>() {
					public void onFailure(Throwable error) {
						error.printStackTrace();
						assertTrue(false);
					}

					public void onSuccess(PagingResult<SHistory> result) {
						assertTrue(true);
						finishTest();
					}
				});
			}
		};

		for (int i = 0; i < 10; i++) {
			new Thread(r).start();
		}
	}

}