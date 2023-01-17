package com.speno.xedm.gui.common.client.log;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.Useful;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.util.GeneralException;

/**
 * Login 이후에만 사용할 것! 그렇지 않으면, statusbar 위치가 깨짐.
 * 
 * @author deluxjun
 *
 */
public class Log {
	private static Logger logger = Logger.getLogger("");

	private static String BR = "<br>";
	private static String LINE = "<hr width='80%' size='3' align='left'>";

	private Log() {
	}

	public static void serverError(Throwable caught, boolean withPopup) {
		serverError(caught.getMessage(), caught, withPopup);
	}

	public static void serverError(String message, Throwable caught, boolean popup){
		serverError(message, caught, popup, true);
		Waiting.hide();
	}
	
	public static void serverError(String message, Throwable caught, boolean popup, boolean bEvent) {
		//Hide download exceptions that normally are raised on double click.
		if (message == null || message.length() < 1 ) {
			message = caught.getMessage();
		}

		if(message == null || "0".equals(message.trim()))
			return;
					
		GWT.log("Server error: " + message, caught);
	    logger.log(Level.SEVERE, message);

		if(caught instanceof GeneralException) {
			GeneralException e = (GeneralException)caught;

			if (bEvent)
			EventPanel.get().error(I18N.message("servererror"), message + " [" + e.getErrorCode() + "] " + e.getMessage());
			
			// check session is expired
			if(Constants.INVALID_SESSION_ERR01.equals(e.getErrorCode()) || 
					Constants.INVALID_SESSION_ERR02.equals(e.getErrorCode())){
				
				Useful.ask(I18N.message("question"), I18N.message("sessionClosedConfirmExit"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							Session.get().logout();
						}
					}
				});
				
			}else{
				if (popup)
				// kimsoeun GS인증용 - 상세 에러 메시지 지움 
//				SC.warn(message + BR+BR+
//						LINE+BR+
//						 "CODE [" + e.getErrorCode() + "]" + BR +
//						 e.getDetailMessage());
				SC.warn(message);
			}
		}
		else if(caught instanceof StatusCodeException) {
			int code = ((StatusCodeException)caught).getStatusCode();
			String detail = I18N.message("client.errorStatusCodeReturned", Integer.toString(code));
			if (bEvent)
			EventPanel.get().error(I18N.message("servererror"), detail);

			if (popup)
				SC.warn(detail);
		}
		else {
			String detail = I18N.message("genericerror");
			if (bEvent)
			EventPanel.get().error(I18N.message("servererror"), message);
			
			if (popup)
				SC.warn(detail);
		}
	}

	public static void warn(String message, String detail) {
		EventPanel.get().warn(message, detail);
		GWT.log("warn: " + message + ", " + detail, null);
	    logger.log(Level.FINER, message);
	}
	
	public static void warn(String detail) {
		EventPanel.get().warn(I18N.message("warning"), detail);
		GWT.log("warn: " + detail, null);
	    logger.log(Level.FINER, detail);
	}
	
	public static void warnWithPopup(String message, String detail) {
		warn(message, detail);
		SC.warn(message+BR+BR+
				LINE+BR+BR +
				 detail);
	}


	public static void running(String message) {
		EventPanel.get().running(message);
	}

	public static void error(String message, String detail, Throwable caught, boolean withPopup) {
		//Hide download exceptions that normally are raised on double click.
		if("0".equals(message))
			return;
		
		EventPanel.get().error(message, detail);
		GWT.log("error: " + message, caught);
	    logger.log(Level.SEVERE, message);

		if (withPopup)
		SC.warn(message+BR+BR+
				LINE+BR+BR +
				 detail);

	}
	
	public static void error(String message, String detail, boolean withPopup) {
		//Hide download exceptions that normally are raised on double click.
		if("0".equals(message))
			return;
		
		EventPanel.get().error(message, detail);
		GWT.log("error: " + message, null);
	    logger.log(Level.SEVERE, message);
		
		if (withPopup)
		SC.warn(message+BR+BR+
				LINE+BR+BR +
				 detail);

	}

	public static void info(String message, String detail) {
		EventPanel.get().info(message, detail);
		GWT.log("info: " + message, null);
	    logger.log(Level.INFO, message);
	}
	//20140305 yuk yong soo line delete
	public static void infoWithPopup(String message, String detail) {
		info(message, detail);
		SC.say(message+BR+BR + detail);
	}

	
	public static void debug(String message) {
		GWT.log("debug: " + message, null);
	    logger.log(Level.FINEST, message);
	}
}