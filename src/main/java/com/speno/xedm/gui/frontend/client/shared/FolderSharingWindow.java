package com.speno.xedm.gui.frontend.client.shared;

import com.smartgwt.client.widgets.Window;
import com.speno.xedm.gui.common.client.I18N;

/**
 * ���� ����(context Menu����)�� ���Ǵ� Window 
 * @author taesu
 */
public class FolderSharingWindow extends Window{
	public static FolderSharingWindow instance;
	
	public static FolderSharingWindow get(){
		if(instance == null)
			instance = new FolderSharingWindow();
		return instance;
	}
	
	public FolderSharingWindow(){
		setTitle(I18N.message("foldersharing"));

		setWidth(420);
		setHeight(150);
		setMargin(5);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		
		show();
	}
}
