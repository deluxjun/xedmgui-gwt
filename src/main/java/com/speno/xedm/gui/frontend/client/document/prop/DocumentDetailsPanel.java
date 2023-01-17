package com.speno.xedm.gui.frontend.client.document.prop;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentSecurityPanel;
import com.speno.xedm.gui.frontend.client.folder.FolderSecurityPanel;


/**
 * 
 * @author deluxjun
 *
 */
public class DocumentDetailsPanel extends VLayout{
	protected SDocument document;

	protected Layout propertiesTabPanel;

	protected Layout extendedPropertiesTabPanel;

	protected Layout versionsTabPanel;

	protected Layout historyTabPanel;

	// TODO:
	protected Layout docSecurityTabPanel;
	
	protected StandardPropertiesPanel propertiesPanel;

	protected ExtendedPropertiesPanel extendedPropertiesPanel;

	protected VersionsPanel versionsPanel;

	protected HistoryPanel historyPanel;

	private DocumentSecurityPanel documentSecurityPanel;
	private FolderSecurityPanel showDocSecurityPanel;
//	private SecurityACLGridPanel securityProfilePanel;
	
	protected HLayout savePanel;
	protected HLayout readonlyPanel;
	private HTMLPane readonly;
	protected TabSet tabSet = new TabSet();

	protected DynamicForm saveForm;

	protected DocumentObserver observer;

	protected Tab propertiesTab;

	protected Tab extendedPropertiesTab;

	protected Tab versionsTab;

	protected Tab historyTab;
	
	// TODO: 문서 level 보안 Tab 추가
	protected Tab docSecurityTab;
	
	protected ChangedHandler changeHandler;

	private int tabNum;
	
	public DocumentDetailsPanel(DocumentObserver observer, int tabNum) {
		super();
		this.observer = observer;
		this.tabNum = tabNum;
		init();
	}

	public DocumentDetailsPanel(DocumentObserver observer) {
		super();
		this.observer = observer;
		init();
	}

	private void init(){

		setHeight100();
		setWidth100();
//		setMembersMargin(10);

		savePanel = new HLayout();
		saveForm = new DynamicForm();
		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		saveButton.setLayoutAlign(VerticalAlignment.CENTER);

		//속성창 닫는 x박스
		/*Img closeImage = ItemFactory.newImgIcon("delete.png");
		closeImage.setHeight("16px");
		closeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// We have to reload the document because the tags may be
				// reverted to the original tags list.
				// This 'if condition' is necessary to know if the close image
				// has been selected into the Documents list panel or into the
				// Search list panel.
//				if (getObserver() instanceof DocumentsPanel)
//					DocumentsPanel.get().onSelectedDocument(document.getId(), false);
//				else if (getObserver() instanceof HitsListPanel)
//					SearchPanel.get().onSelectedDocumentHit(document.getId());
				savePanel.setVisible(false);
			}
		});
		closeImage.setCursor(Cursor.HAND);
		closeImage.setTooltip(I18N.message("close"));
		closeImage.setLayoutAlign(Alignment.RIGHT);
		closeImage.setLayoutAlign(VerticalAlignment.CENTER);*/

		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		HTMLPane spacer2 = new HTMLPane();
		spacer2.setContents("<div>&nbsp;</div>");
		spacer2.setWidth("2%");
		spacer2.setOverflow(Overflow.HIDDEN);

//		TextItem versionComment = ItemFactory.newTextItem("versionCmment", "versioncomment", null);
//		versionComment.setWidth(300);
	//	savePanel.addMember(closeImage);
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(saveButton);
		savePanel.addMember(spacer2);
		savePanel.setHeight(25);
		savePanel.setMembersMargin(10);
		savePanel.setVisible(false);
		savePanel.setStyleName("warn");
		savePanel.setWidth100();

		// read only
		readonlyPanel = new HLayout();
		readonly = new HTMLPane();
		readonly.setContents("<div>" + I18N.message("second.readonly") + "</div>");
		readonly.setWidth("30%");
		readonly.setOverflow(Overflow.HIDDEN);
		readonlyPanel.addMember(readonly);
		readonlyPanel.setHeight(25);
		readonlyPanel.setMembersMargin(10);
		readonlyPanel.setVisible(false);
		readonlyPanel.setStyleName("warn");
		readonlyPanel.setWidth100();

		prepareTabs();
		prepareTabset();
		
		addMember(readonlyPanel);
		addMember(savePanel);
		
		changeHandler = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onModified();
			}
		};
	}
	
	protected void prepareTabs() {
		propertiesTab = new Tab(I18N.message("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);

		extendedPropertiesTab = new Tab(I18N.message("extentionproperties"));
		extendedPropertiesTabPanel = new HLayout();
		extendedPropertiesTabPanel.setWidth100();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTab.setPane(extendedPropertiesTabPanel);

		versionsTab = new Tab(I18N.message("version"));
		versionsTabPanel = new HLayout();
		versionsTabPanel.setWidth100();
		versionsTabPanel.setHeight100();
		versionsTab.setPane(versionsTabPanel);

		historyTab = new Tab(I18N.message("history"));
		historyTabPanel = new HLayout();
		historyTabPanel.setWidth100();
		historyTabPanel.setHeight100();
		historyTab.setPane(historyTabPanel);
		
		docSecurityTab = new Tab(I18N.message("doclevelsecurity"));
		docSecurityTabPanel = new HLayout();
		docSecurityTabPanel.setWidth100();
		docSecurityTabPanel.setHeight100();
		docSecurityTab.setPane(docSecurityTabPanel);
	}

	protected void prepareTabset() {
		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		tabSet.addTab(propertiesTab);
		propertiesTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (propertiesPanel != null) {
					propertiesPanel.onTabSelected();
//					propertiesPanel.setDocument(getDocument());
				}
			}
		});

		tabSet.addTab(extendedPropertiesTab);
		extendedPropertiesTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (extendedPropertiesPanel != null) {
					extendedPropertiesPanel.onTabSelected();
				}
			}
		});


		tabSet.addTab(versionsTab);
		versionsTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (versionsPanel != null) {
					versionsPanel.onTabSelected();
					versionsPanel.setDocument(getDocument());
				}
			}
		});
		

		tabSet.addTab(historyTab);
		historyTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				if (historyPanel != null) {
					historyPanel.onTabSelected();
					historyPanel.setDocument(getDocument());
				}
			}
		});
		// 공유 문서에서만 폴더권한 Tab 보여줌.
		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED ){
			tabSet.addTab(docSecurityTab);
			docSecurityTab.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
				}
			});
		}
			
		addMember(tabSet);
	}

	public DocumentObserver getObserver() {
		return observer;
	}

//	protected void refreshWithInit() {
//		if (savePanel != null)
//			savePanel.setVisible(false);
//
//		/*
//		 * Prepare the standard properties tab
//		 */
//		if (propertiesPanel != null) {
//			propertiesPanel.destroy();
//			if (propertiesTabPanel.contains(propertiesPanel))
//				propertiesTabPanel.removeMember(propertiesPanel);
//		}
//
//		ChangedHandler changeHandler = new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				onModified();
//			}
//		};
//		propertiesPanel = new StandardPropertiesPanel(document, changeHandler, getObserver());
//		propertiesTabPanel.addMember(propertiesPanel);
//
//		/*
//		 * Prepare the extended properties tab
//		 */
//		if (extendedPropertiesPanel != null) {
//			extendedPropertiesPanel.destroy();
//			if (extendedPropertiesTabPanel.contains(extendedPropertiesPanel))
//				extendedPropertiesTabPanel.removeMember(extendedPropertiesPanel);
//		}
//		extendedPropertiesPanel = new ExtendedPropertiesPanel(document, changeHandler);
//		extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);
//
//		/*
//		 * Prepare the versions tab
//		 */
//		if (versionsPanel != null) {
//			versionsPanel.destroy();
//			if (versionsTabPanel.contains(versionsPanel))
//				versionsTabPanel.removeMember(versionsPanel);
//		}
//		versionsPanel = new VersionsPanel(document);
//		versionsTabPanel.addMember(versionsPanel);
//
//		/*
//		 * Prepare the history tab
//		 */
//		if (historyPanel != null) {
//			historyPanel.destroy();
//			if (historyTabPanel.contains(historyPanel))
//				historyTabPanel.removeMember(historyPanel);
//		}
//		historyPanel = new HistoryPanel(document);
//		historyTabPanel.addMember(historyPanel);
//
//		if (tabSet != null && tabSet.getSelectedTab() != null) {
//			Tab selectedTab = tabSet.getSelectedTab();
//			Canvas pane = selectedTab.getPane();
//			((DocumentDetailTab) pane.getChildren()[0]).onTabSelected();
//		}
//	}
	
	protected void refresh() {
		savePanel.setVisible(false);
		
		// 20130820, junsoo, 속성 와 확장속성은 초기에 생성시에만 document setting하고 나머지는 tab 선택할 때마다 리셋.
		if (propertiesPanel == null) {
			propertiesPanel = new StandardPropertiesPanel(document, changeHandler, getObserver(), false);
			propertiesTabPanel.addMember(propertiesPanel);
			propertiesPanel.setDocument(getDocument());
		}

		if (extendedPropertiesPanel == null) {
			extendedPropertiesPanel = new ExtendedPropertiesPanel(document, changeHandler);
			extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);
			extendedPropertiesPanel.setDocument(getDocument());
		}

		/*
		 * Prepare the versions tab
		 */
		if (document.getVersionControl() != null && document.getVersionControl() == 1) {
			if (versionsPanel == null) {
				versionsPanel = new VersionsPanel(document);
				versionsPanel.setDocumentObserver(this.observer);
				versionsTabPanel.addMember(versionsPanel);
			}
			versionsTab.setDisabled(false);
		} else {
			versionsTab.setDisabled(true);
		}

		/*
		 * Prepare the history tab/
		 */
		if (historyPanel == null) {
			historyPanel = new HistoryPanel(document);
			historyTabPanel.addMember(historyPanel);
		}

		/*
		 * Prepare the docSecurity Tab
		 */
		if (documentSecurityPanel == null && 
				(DocumentActionUtil.get().getActivatedMenuType() == 
				DocumentActionUtil.TYPE_SHARED )) {
//			// 문서가 잠금상태 or 체크아웃상태시 폴더의 권한을 보여준다.
//			if(document.getStatus() == SDocument.DOC_LOCKED || document.getStatus() == SDocument.DOC_CHECKED_OUT){
//				setFolderSecurityPanel();
//			}else{
//				// 폴더에 제어 권한이 있을경우 문서보안 창을 보여준다
////				if(DocumentActionUtil.get().isHasfolderControlRight() || tabNum == Constants.MAIN_TAB_SEARCH){
//				if(document.hasPermission(Constants.PERMISSION_CONTROL) || tabNum == Constants.MAIN_TAB_SEARCH){
//					setSecurityPanel();
//				}
//				// 폴더에 제어 권한이 없을경우 폴더 보안상태 창을 모여준다.
//				else{
//					setFolderSecurityPanel();
//				}
//			}

			// 20131205, junsoo, 문서보안이 적용되어 있으면, 그 내용을 보여줘야 함. 보여준 후 제어가능/잠금 체크하여 readonly 체크
			// 문서 보안 적용이 안 되어 있으면, 제어가능/잠금 체크하여 가능하면 문서보안표시(inherited로), 불가하면 폴더보안 표시
			if ((document.getSecurityType() != null && document.getSecurityProfile() >= 0)) {
				if (document.hasPermission(Constants.PERMISSION_CONTROL) &&
						!(document.getStatus() == SDocument.DOC_LOCKED || document.getStatus() == SDocument.DOC_CHECKED_OUT)) {
					setSecurityPanel(true, document.getSecurityProfile(), "");
				} else {
					String message = I18N.message("second.statusLocked");
					if (document.getStatus() == SDocument.DOC_LOCKED || document.getStatus() == SDocument.DOC_CHECKED_OUT) {
						message = I18N.message("second.statusLocked");
					} else {
						message = I18N.message("noDocumentControlRight");
					}
					setSecurityPanel(false, document.getSecurityProfile(), message);
				}

			}else{
				if (document.hasPermission(Constants.PERMISSION_CONTROL) &&
						!(document.getStatus() == SDocument.DOC_LOCKED || document.getStatus() == SDocument.DOC_CHECKED_OUT)) {
					setSecurityPanel(true, SSecurityProfile.PROFILE_INHERITEDACL, "");
				} else {
					String message = I18N.message("second.statusLocked");
					if (document.getStatus() == SDocument.DOC_LOCKED || document.getStatus() == SDocument.DOC_CHECKED_OUT) {
						message = I18N.message("second.statusLocked");
					} else {
						message = I18N.message("noDocumentControlRight");
					}
					setSecurityPanel(false, SSecurityProfile.PROFILE_INHERITEDACL, message);
				}
			}

		}
		// reset all
//		historyPanel.setDocument(null);
//		propertiesPanel.setDocument(null);
//		if (versionsPanel != null)
//		versionsPanel.setDocument(null);
//		extendedPropertiesPanel.setDocument(null);
		
		if (tabSet != null && tabSet.getSelectedTab() != null) {
			Tab selectedTab = tabSet.getSelectedTab();
			Canvas pane = selectedTab.getPane();
			((DocumentDetailTab) pane.getChildren()[0]).setDocument(document);
		}

		// readonly 패널 표시
		readonlyPanel.setVisible(!propertiesPanel.update);

		if (readonlyPanel.isVisible()) {
			String info = "";
			if (document.getStatus() == Constants.DOC_LOCKED)
				info += I18N.message("lock") + " ";
			if (document.getStatus() == Constants.DOC_CHECKED_OUT)
				info += I18N.message("second.statusCheckedout") + " ";
			if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
				info += I18N.message("second.adminSession") + " ";
		
			readonly.setContents("<div>" + I18N.message("second.readonly") + " : " + info + "</div>");
		}
	}

	/**
	 * 문서 보안 창을 보여준다
	 */
	private void setSecurityPanel(){
		documentSecurityPanel = new DocumentSecurityPanel(document);
		docSecurityTabPanel.addMember(documentSecurityPanel);
		if(tabNum == Constants.MAIN_TAB_SEARCH){
			documentSecurityPanel.disable();
		}
	}
	
	/**
	 * 문서 보안 창을 보여준다
	 */
	private void setSecurityPanel(boolean writable, Long initProfileId, String reason){
		documentSecurityPanel = new DocumentSecurityPanel(document);
		docSecurityTabPanel.addMember(documentSecurityPanel);
		if(!writable || tabNum == Constants.MAIN_TAB_SEARCH){
			documentSecurityPanel.disable(reason);
		}
		documentSecurityPanel.setProfileId(initProfileId);
	}
	
	/**
	 * 폴더 보안 창을 보여준다
	 */
	private void setFolderSecurityPanel(){
		showDocSecurityPanel = new FolderSecurityPanel();
		showDocSecurityPanel.setFolder(Session.get().getCurrentFolder());
		if(!DocumentActionUtil.get().isHasfolderControlRight())
			showDocSecurityPanel.setSecurityReason(I18N.message("noFolderControlRight"));
		docSecurityTabPanel.addMember(showDocSecurityPanel);
	}
	
	public SDocument getDocument() {
		return document;
	}

	public void setDocument(SDocument document) {
		this.document = document;
		refresh();
	}

	public void onModified() {
		savePanel.setVisible(true);
		readonlyPanel.setVisible(false);
	}


	private boolean validate() {
		boolean stdValid = propertiesPanel.validate();
		boolean extValid = extendedPropertiesPanel.validate();
		if (!stdValid)
			tabSet.selectTab(0);
		else if (!extValid)
			tabSet.selectTab(1);
		return stdValid && extValid;
	}

	private ReturnHandler savedListener;
	
	public void setSavedListener(ReturnHandler savedListener) {
		this.savedListener = savedListener;
	}
	public void onSave() {
		if (validate()) {
			if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			ServiceUtil.document().save(Session.get().getSid(), document, new AsyncCallback<SDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					saveForm.setValue("versionComment", "");
					Log.serverError(caught, true);
					Waiting.hide();
				}

				@Override
				public void onSuccess(SDocument result) {
					if (observer != null) {
						observer.onDocumentSaved(result);
						Log.infoWithPopup(I18N.message("save"), I18N.message("second.client.successfully"));
						savePanel.setVisible(false);
						
						if (savedListener != null)
							savedListener.onReturn(null);
					}
					Waiting.hide();
				}
			});
		}
	}
}