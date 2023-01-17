package com.speno.xedm.gui.common.client.window;

import com.smartgwt.client.widgets.Canvas;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.folder.FolderDetailsPanel;

public class FolderPropertiesWindow extends BaseWindow {

	private static FolderPropertiesWindow instance = null;
	
	private SFolder folder = null;
	
	public FolderPropertiesWindow(Canvas item) {
		super(item);
	}

	@Override
	public void refresh() {
		if (panel == null)
			return;

		// 단지 공유폴더 에서만 동작
		// soeun 내 문서에서도 동작하도록
//		if (DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_SHARED)
//			return;

		if (folder == null)
			folder = Session.get().getCurrentFolder();

		// 보여지고 있으며, 최소화상태가 아닌 경우에만 갱신
		if (getActivated()) {
			// session 의 folder로 갱신
			((FolderDetailsPanel)panel).refreshFolder(folder);
			folder = null;
		}
	}

	public static FolderPropertiesWindow get() {
		if (instance == null)
			instance = new FolderPropertiesWindow(null);
		return instance;
	}

	public void show(SFolder folder) {
		if (panel == null) {
			panel = new FolderDetailsPanel();
			addItem(panel);
		}
		
		this.folder = folder;
		
		super.show();
	}
}