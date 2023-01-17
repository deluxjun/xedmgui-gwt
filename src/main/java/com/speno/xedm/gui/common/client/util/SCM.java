package com.speno.xedm.gui.common.client.util;

import com.google.gwt.user.client.rpc.StatusCodeException;
import com.smartgwt.client.util.SC;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.util.GeneralException;

/**
 * Old log.. don't use from 2013/07/18
 * 
 * @author deluxjun
 *
 */
public class SCM extends SC {	
	private static String BR = "<br>";
	private static String LINE = "<hr width='80%' size='3' align='left'>";
	
	public static void warn(Throwable caught) {
		caught.printStackTrace();

//		Log.serverError(caught.getMessage(), caught, false);
		
		if(caught instanceof GeneralException) {
			GeneralException e = (GeneralException)caught;
			
			// check session is expired
			if(Constants.INVALID_SESSION_ERR01.equals(e.getErrorCode()) || 
					Constants.INVALID_SESSION_ERR02.equals(e.getErrorCode())){
				
				//20150509na GS������ ������ ����Ǹ� �ݵ�� �α׾ƿ��Ǵ� �ɷ� ����
//				Useful.ask(I18N.message("question"), I18N.message("sessionClosedConfirmExit"), new BooleanCallback() {
//					@Override
//					public void execute(Boolean value) {
//						if (value) {
							Session.get().requestLogout();
//						}
//					}
//				});
				
			}else{
//				MainPanel.get().setStatusMessage(MainPanel.STATUS_ERROR, e.getMessage());
//				warn(I18N.message(e.getMessage())+BR+BR+
//						LINE+BR+
//						 "[" + e.getErrorCode() + "]" + BR +
//						 I18N.message(e.getDetailMessage()));
				// kimsoeun GS������ - �� ���� �޽��� ���� 
				warn(I18N.message(e.getMessage()));
			}
		}
		else if(caught instanceof StatusCodeException) {
			int code = ((StatusCodeException)caught).getStatusCode();
			String message = I18N.message("client.errorStatusCodeReturned", Integer.toString(code));
//			MainPanel.get().setStatusMessage(MainPanel.STATUS_ERROR, message);
			warn(message);
		}
		else {
			String message = I18N.message("genericerror");
//			MainPanel.get().setStatusMessage(MainPanel.STATUS_ERROR, message);
			// kimsoeun GS������ - �� ���� �޽��� ���� 
//			warn(message + BR + BR + caught.getMessage());
			warn(message);
		}
	}
	
	public static void warning(String message) {
		// set status
//		Log.warn(message, "");
//		MainPanel.get().setStatusMessage(MainPanel.STATUS_ERROR, message);
		
		warn(message);
	}

}
