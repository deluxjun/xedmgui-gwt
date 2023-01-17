package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.Canvas;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.log.Log;


/**
 * MainPanel 내 status 에 상태를 기록하는 RPC 콜백 클래스
 * 
 * @author deluxjun
 *
 * @param <T>
 */
public class DefaultAsyncCallbackWithStatus<T> extends AsyncCallbackWithStatus<T> {

	private String headerMessage;
	
	public DefaultAsyncCallbackWithStatus(String commandMessage){
		headerMessage = commandMessage;
		Log.running(getProcessMessage());
	}
	
	public DefaultAsyncCallbackWithStatus() {
		super("dummy");
	}
	
	public void start(String headerMessage) {
		this.headerMessage = headerMessage;
		start();
	}

	@Override
	public void onFailureEvent(Throwable caught) {
		Log.serverError(caught, false);
		return; 						
	}

	@Override
	public String getSuccessMessage() {
		return headerMessage + " : " + I18N.message("client.searchComplete");
	}
	@Override
	public String getProcessMessage() {
		return headerMessage + " : " + I18N.message("client.searchRequest");
	}
	
	
	@Override
	public void onSuccessEvent(T result) {
		Log.debug("successed but you must implement this procedure");
	}
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
}
