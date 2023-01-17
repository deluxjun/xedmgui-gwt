package com.speno.xedm.gui.common.client.util;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.speno.xedm.gui.common.client.log.Log;

/**
 * MainPanel 내 status 에 상태를 기록하는 RPC 콜백 클래스
 * 
 * @author deluxjun
 *
 * @param <T>
 */
public abstract class AsyncCallbackWithStatus<T> implements AsyncCallback<T> {
	// Proccess가 진행중인 Canvas
	protected Canvas canvas;
	
	public AsyncCallbackWithStatus() {
		Log.running(getProcessMessage());
//		MainPanel.get().setStatusMessage(MainPanel.STATUS_PROCESS, getProcessMessage());
	}
	
	public AsyncCallbackWithStatus(String dummy){
	}

	public void start() {
		Log.running(getProcessMessage());
	}
	
	// 20130807 taesu, 로딩시 마우스 커서 변경으로 인한 추가
	public void start(Canvas canvas) {
		Log.running(getProcessMessage());
	}
	/*	
	*	성공시 마우스 cursor 변화 시킬경우 서버 통신이 완료된 순간 바뀌기 때문에 
	*	클라이언트측에서 grid에 데이터를 쓰는 동안 default cursor가 된다.
	*	따라서 성공시 default cursor 변경은 클라이언트 측에서 사용한다. 
	*/ 
	@Override
	public void onSuccess(T result) {
		Log.info(getSuccessMessage(), "");
//		MainPanel.get().setStatusMessage(MainPanel.STATUS_INFO, getSuccessMessage());
		onSuccessEvent(result);
	}
	
	@Override
	public void onFailure(Throwable caught) {
		// 20140402, junsoo, 오류시 eventpanel 에 남김
		Log.serverError(caught, false);
		// 오류는 SCM 에서 상태 메시지를 남기므로 단지 callback 함수만 호출
		onFailureEvent(caught);
	}
	
	public abstract String getSuccessMessage();
	public abstract String getProcessMessage();
	public abstract void onSuccessEvent(T result);
	public abstract void onFailureEvent(Throwable caught);
}
