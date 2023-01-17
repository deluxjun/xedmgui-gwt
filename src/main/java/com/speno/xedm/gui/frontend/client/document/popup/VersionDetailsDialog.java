package com.speno.xedm.gui.frontend.client.document.popup;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SVersion;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * version 세부 정보
 * 
 * @author deluxjun
 *
 */
public class VersionDetailsDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	protected DocumentUploadPanel updatePanel;
	
	protected HLayout savePanel = new HLayout();
	protected ValuesManager vm;
	
	protected Button saveButton;
	
	protected DocumentObserver documentObserver;


	public VersionDetailsDialog(SVersion version) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		
		documentObserver = DocumentsPanel.get();

		setTitle(I18N.message("properties"));
		setWidth(750);
		setHeight(360);
		
		// 모든 동작을 readonly로 설정
		setReadOnly(version);
		
		updatePanel = new DocumentUploadPanel(version, documentObserver, false);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

		layout.setMembersMargin(10);
		layout.setTop(25);
		layout.setMargin(3);
		layout.setWidth100();
		layout.setHeight("99%");
		layout.setMembers(updatePanel);

		setPanel(layout);
	}


	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}
	
	protected void onSave() {
		// do nothing
	}


}