package com.speno.xedm.gui.frontend.client.document.popup;

import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.frontend.client.admin.system.ApprovalManagementPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.prop.DocumentDetailsPanel;
import com.speno.xedm.gui.frontend.client.search.SearchPanel;

/**
 * check in dialog
 * 
 * @author deluxjun
 *
 */
public class DocumentPropertiesDialog extends BaseWindow {
	private DocumentDetailsPanel panel;

//	private ValuesManager vm;
	
	private boolean readOnly = false;
	private int mainTab = Constants.MAIN_TAB_DOCUMENT;
	
	
	public DocumentPropertiesDialog(SDocument document, boolean bReadOnly) {
		super(null);
		this.readOnly = bReadOnly;
		init(document);
	}
	public DocumentPropertiesDialog(SDocument document, boolean bReadOnly, int mainTab) {
		super(null);
		this.readOnly = bReadOnly;
		this.mainTab = mainTab;
		init(document);
	}
	public DocumentPropertiesDialog(SDocument document) {
		super(null);
		init(document);
	}
	
	public void init(SDocument document) {
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setTitle(I18N.message("properties"));
		setHeight(370);
		
		// 모든 동작을 readonly로 설정
		if (this.readOnly)
			setReadOnly(document);
		
		if(mainTab == Constants.MAIN_TAB_SEARCH)
			panel = new DocumentDetailsPanel(SearchPanel.get(), mainTab);
		else if(mainTab == Constants.MAIN_TAB_ADMIN)
			panel = new DocumentDetailsPanel(ApprovalManagementPanel.get("All"));
		else if(mainTab == Constants.MAIN_TAB_DOCUMENT)
			panel = new DocumentDetailsPanel(DocumentsPanel.get());
		
		
		panel.setWidth100();
		panel.setHeight("100%");
		panel.setShowResizeBar(false);
		panel.setDocument(document);
		panel.setSavedListener(new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				destroy();
			}
		});

		setPanel(panel);
	}


	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}

}