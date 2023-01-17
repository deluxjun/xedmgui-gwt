package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.services.ECMService;
import com.speno.xedm.gui.common.client.services.ECMServiceAsync;
import com.speno.xedm.gui.common.client.util.PagingConfig;
import com.speno.xedm.gui.common.client.util.PagingResult;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class SFolderTest extends GWTTestCase {

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
	public void testRefined() {
		SFolder folder = new SFolder();
		
		SFolder root = new SFolder(); root.setId(5); root.setParentId(5); root.setName("root");
		SFolder shared = new SFolder(); shared.setId(3); shared.setParentId(5); shared.setName("Shared");
		SFolder workspace = new SFolder(); workspace.setId(4); workspace.setParentId(5); workspace.setName("Workspace");
		SFolder home = new SFolder(); home.setId(111); home.setParentId(4); home.setName("home");
		SFolder folder1 = new SFolder(); folder1.setId(222); folder1.setParentId(111); folder1.setName("folder1");
		
		folder.setPath(new SFolder[]{root, workspace, home, folder1});
		
		SFolder[] path = folder.getRefinedPath();
		String r = folder.getRefinedPathExtended();
		for (SFolder p : path){
			System.out.println(p.getId());
		}
		System.out.println(r);
		
	}

}