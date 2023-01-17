package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.gui.common.client.services.SecurityService;
import com.speno.xedm.gui.common.client.services.SecurityServiceAsync;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class SecurityServiceTest extends GWTTestCase {

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
		SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

		ServiceDefTarget target = (ServiceDefTarget) securityService;
		target.setServiceEntryPoint("http://10.1.61.6:8088/xedm/frontend/security");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		securityService.login("admin", new AsyncCallback<SSession>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(final SSession session) {
				assertTrue(session.isLoggedIn());
				assertEquals("admin", session.getUser().getUserName());
				assertEquals(3, session.getInfo().getSupportedGUILanguages().length);

				finishTest();
			}
		});
//
//		securityService.pagingUsers(new PagingLoadConfigBean(), "d", new AsyncCallback<PagingLoadResult<SUser>>() {
//			public void onFailure(Throwable error) {
//				error.printStackTrace();
//				assertTrue(false);
//			}
//
//			public void onSuccess(final PagingLoadResult<SUser> user) {
//				System.out.println("Sucess");
//
//				finishTest();
//			}
//		});
	}

}