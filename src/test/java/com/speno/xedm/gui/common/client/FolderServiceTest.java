package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.services.FolderService;
import com.speno.xedm.gui.common.client.services.FolderServiceAsync;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.util.GeneralException;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class FolderServiceTest extends GWTTestCase {

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
	public void testService() {
		/* create the service that we will test. */
		FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

		ServiceDefTarget target = (ServiceDefTarget) folderService;
		target.setServiceEntryPoint("http://10.1.61.6:8089/xedm/frontend/folder");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/folder");

		delayTestFinish(10000); // timeout

		folderService.getFolder("sid5", "", 1, true, true, new AsyncCallback<SFolder>() {
			public void onFailure(Throwable error) {
				if (error instanceof GeneralException) {
					GeneralException ge = (GeneralException) error;
					ge.printStackTrace();
					System.out.println(ge.getMessage());
					System.out.println(ge.getErrorCode());
					System.out.println(ge.getDetailMessage());
				
				}
				assertTrue(false);
			}

			public void onSuccess(final SFolder folder) {
				assertNotNull(folder.getPath());
				System.out.println(folder.getName());

				finishTest();
			}
		});

	}

}