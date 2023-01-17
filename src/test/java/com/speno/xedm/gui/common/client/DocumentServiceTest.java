package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.gui.common.client.services.DocumentService;
import com.speno.xedm.gui.common.client.services.DocumentServiceAsync;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class DocumentServiceTest extends GWTTestCase {

	/**
	 * must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		// Frontend �� smartclient js�� �ε��ϴµ�.. isc ��ũ��Ʈ������ �׽�Ʈ�� ���� �� �ȵ�.
//		return "com.speno.xedm.gui.frontend.Frontend";
		return "com.speno.xedm.gui.common.Common";
	}

	/**
	 * this test will send a request to the server using the greetServer method
	 * in GreetingService and verify the response.
	 */
	public void testSecurityService() {
		/* create the service that we will test. */
		DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

		ServiceDefTarget target = (ServiceDefTarget) service;
		target.setServiceEntryPoint("http://10.1.61.6:8088/xedm/frontend/document");
//		target.setServiceEntryPoint(Util.contextPath() + "frontend/security");

		delayTestFinish(10000); // timeout

		service.cleanUploadedFileFolder("admin", new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				error.printStackTrace();
				assertTrue(false);
			}

			public void onSuccess(Void result) {

				assertTrue(true);
				finishTest();
			}
		});
	}

}