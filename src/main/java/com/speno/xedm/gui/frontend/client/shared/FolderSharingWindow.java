package com.speno.xedm.gui.frontend.client.shared;

import com.smartgwt.client.widgets.Window;
import com.speno.xedm.gui.common.client.I18N;

/**
 * 폴더 공유(context Menu선택)시 사용되는 Window 
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
