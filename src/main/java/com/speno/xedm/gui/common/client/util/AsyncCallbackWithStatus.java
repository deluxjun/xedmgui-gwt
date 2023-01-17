package com.speno.xedm.gui.common.client.util;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.speno.xedm.gui.common.client.log.Log;

/**
 * MainPanel �� status �� ���¸� ����ϴ� RPC �ݹ� Ŭ����
 * 
 * @author deluxjun
 *
 * @param <T>
 */
public abstract class AsyncCallbackWithStatus<T> implements AsyncCallback<T> {
	// Proccess�� �������� Canvas
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
	
	// 20130807 taesu, �ε��� ���콺 Ŀ�� �������� ���� �߰�
	public void start(Canvas canvas) {
		Log.running(getProcessMessage());
	}
	/*	
	*	������ ���콺 cursor ��ȭ ��ų��� ���� ����� �Ϸ�� ���� �ٲ�� ������ 
	*	Ŭ���̾�Ʈ������ grid�� �����͸� ���� ���� default cursor�� �ȴ�.
	*	���� ������ default cursor ������ Ŭ���̾�Ʈ ������ ����Ѵ�. 
	*/ 
	@Override
	public void onSuccess(T result) {
		Log.info(getSuccessMessage(), "");
//		MainPanel.get().setStatusMessage(MainPanel.STATUS_INFO, getSuccessMessage());
		onSuccessEvent(result);
	}
	
	@Override
	public void onFailure(Throwable caught) {
		// 20140402, junsoo, ������ eventpanel �� ����
		Log.serverError(caught, false);
		// ������ SCM ���� ���� �޽����� ����Ƿ� ���� callback �Լ��� ȣ��
		onFailureEvent(caught);
	}
	
	public abstract String getSuccessMessage();
	public abstract String getProcessMessage();
	public abstract void onSuccessEvent(T result);
	public abstract void onFailureEvent(Throwable caught);
}
