package com.speno.xedm.gui.frontend.client.document.popup;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.prop.ExtendedPropertiesPanel;
import com.speno.xedm.gui.frontend.client.document.prop.StandardPropertiesPanel;


/**
 * ���� ���, ���� üũ��/üũ�ƿ�/Lock/UnLock ���� ���Ǵ� Panel
 * 
 * @author deluxjun
 *
 */
public class DocumentUploadPanel extends VLayout {
	protected SDocument document = new SDocument();

	protected Layout propertiesTabPanel;

	protected Layout extendedPropertiesTabPanel;

	protected StandardPropertiesPanel propertiesPanel;

	protected ExtendedPropertiesPanel extendedPropertiesPanel;

	protected TabSet tabSet = new TabSet();

	protected Tab propertiesTab;

	protected Tab extendedPropertiesTab;
	
	protected DocumentObserver documentObserver;

	// ���� ���δ�
	private MultiUploader multiUploader;
	
	private ReturnHandler<Boolean> canSaveListener;

	// ���ε�����
	private SFolder folder;

	// ÷������ �׸��� �������� ����
	private boolean attachGridRecordEditable = false;

	// ���� �޽����� ����
	private String statusMessage = "";
	private String availableFileTypes = "";

	private DynamicForm dfFolder; 
	private VLayout vScrollLayout;
	
	public void setCanSaveListener(ReturnHandler<Boolean> canSaveListener) {
		this.canSaveListener = canSaveListener;
	}
	
	//20150505na GS������ ���� ���� ��� ����
	public DocumentUploadPanel(SDocument metadata, DocumentObserver observer) {
		super();
		
		this.documentObserver = observer;

		if (metadata != null)
			document = metadata;
		else {
			document.setFolder(Session.get().getCurrentFolder());
		}
		
		this.attachGridRecordEditable = false;

		if (document.getFolder() == null) {
			ServiceUtil.folder().getFolder(Session.get().getSid(), Session.get().getHomeFolderId(), true, true, new AsyncCallback<SFolder>() {
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
				@Override
				public void onSuccess(SFolder result) {
					document.setFolder(result);
					initGUI();
				}
			});
		} else {
			initGUI(true);
		}
	}

	public DocumentUploadPanel(SDocument metadata, DocumentObserver observer, boolean attachGridRecordEditable) {
		super();
		
		this.documentObserver = observer;

		if (metadata != null)
			document = metadata;
		else {
			document.setFolder(Session.get().getCurrentFolder());
		}
		
		this.attachGridRecordEditable = attachGridRecordEditable;

		if (document.getFolder() == null) {
			ServiceUtil.folder().getFolder(Session.get().getSid(), Session.get().getHomeFolderId(), true, true, new AsyncCallback<SFolder>() {
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
				@Override
				public void onSuccess(SFolder result) {
					document.setFolder(result);
					initGUI();
				}
			});
		} else {
			initGUI();
		}
	}
	
	public void initGUI(){
		initGUI(attachGridRecordEditable);
	}
	
	private void initGUI(boolean attach) {
		setHeight100();
		setWidth100();
//		setMembersMargin(10);

		prepareTabset();

		// �űԵ���� ���� ������� ǥ���ϱ� 
		if (document.getId() == 0L){ 
			prepareSecondSection();
		}

		// uploader
		if (attach){ 
			prepareUploadSection();
		}

//		prepareButton();
		
		refresh();
	}
	
	// save button �غ�
//	private HLayout savePanel = new HLayout();
//	protected void prepareButton(){
//		HTMLPane spacer = new HTMLPane();
//		spacer.setContents("<div>&nbsp;</div>");
//		spacer.setWidth("60%");
//		spacer.setOverflow(Overflow.HIDDEN);
//
//		Button saveButton = new Button(I18N.message("save"));
//		saveButton.setAutoFit(true);
//		saveButton.setMargin(2);
//		saveButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				onSave();
//			}
//		});
//		
//		HTMLPane spacer2 = new HTMLPane();
//		spacer2.setContents("<div>&nbsp;</div>");
//		spacer2.setWidth("5%");
//		spacer2.setOverflow(Overflow.HIDDEN);
//
//		DynamicForm saveForm = new DynamicForm();
//		savePanel.addMember(saveForm);
//		savePanel.addMember(spacer);
//		savePanel.addMember(saveButton);
//		savePanel.addMember(spacer2);
//		savePanel.setHeight(25);
//		savePanel.setMembersMargin(10);
//		savePanel.setVisible(false);
//		savePanel.setWidth100();
//
//		
//		addMember(savePanel);
//	}
	
	private void redrawAllowedFileTypes(){
		if (!getAttachGridRecordEditable()) {
			return;
		}

		availableFileTypes = propertiesPanel.getAvailableFileTypes();
		statusMessage = Util.strCut(availableFileTypes, 70, "...");
		refreshStatus();
	}
	
	// ���ε��� Ÿ���������
	private TextItem txtfolder; 

	// ���ε� ���� ����
	private void prepareSecondSection(){
		// 20130806, junsoo, ���� ������ ����� ���� ���� handler����
		final ReturnHandler returnHandler = new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				SFolder selectedFolder = (SFolder)param;
				txtfolder.setValue(selectedFolder.getPathExtended().replaceAll("/root", ""));
				folder = selectedFolder;
				
				// TODO: reload folder�� �ش��ϴ� document type, file type�� ���ε� (����������)
				document.setFolder(folder);
				propertiesPanel.changeFolder(folder);
			}
		};
		
		txtfolder = ItemFactory.newTextItem("folder", I18N.message("folder"), "/");
		txtfolder.setWidth(400);
		txtfolder.setHeight(16);
		txtfolder.setShowTitle(true);
		txtfolder.setCanEdit(false);	// 20140207, junsoo, �����Ұ�
		txtfolder.setAlign(Alignment.LEFT);
		txtfolder.setTextBoxStyle("white");
		
		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_FOLDER_SHARED){
			txtfolder.setValue(document.getFolder().getName());
		}else{
			if(document.getFolder().getPathExtended().contains("blueItalic")) {
				String path = document.getFolder().getPathExtended().replaceAll("/root", "");
				
				if(path.contains("blueItalic")) {
					path = path.replaceAll("<span class='blueItalic'>", "");
					path = path.replaceAll("</span>", "");
				};
				
				txtfolder.setValue(path);
			} else {
				txtfolder.setValue(document.getFolder().getPathExtended().replaceAll("/root", ""));
			}
			
		}
		final ButtonItem btnChgFolder = new ButtonItem();
		btnChgFolder.setStartRow(false);
		btnChgFolder.setHeight(22);
		btnChgFolder.setTitle(I18N.message("changefolder"));
		btnChgFolder.setTooltip(I18N.message("changefolder"));
		btnChgFolder.setIcon(ItemFactory.newImgIcon("folder_edit_Disabled.png").getSrc());
		btnChgFolder.setShowDisabled(true);   
		btnChgFolder.setVAlign(VerticalAlignment.CENTER);
		btnChgFolder.setAlign(Alignment.LEFT);
		btnChgFolder.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				// ���� ���ε� Ʈ�� �˾�
//				final DocumentsUploadPath selectPath = DocumentsUploadPath.get(1);
//				selectPath.show();
				// 20130806, junsoo, return handler �߰��Ͽ� ó�� ����� �� Ŭ�������� �����ϵ��� ��. (�ҽ� ������)

//				final DocumentsUploadPath selectPath = new DocumentsUploadPath(1, returnHandler);
//				selectPath.show();
				
				// 20130816, junsoo, ���� selector ����.
				FolderSelectorDialog folderSelector = FolderSelectorDialog.get();
//				FolderSelectorDialog folderSelector = new FolderSelectorDialog();
				folderSelector.setReturnHandler(returnHandler);
				folderSelector.show();
			}
		});
		
		//20150424na GS������ ���� �۾�
		txtfolder.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FolderSelectorDialog folderSelector = FolderSelectorDialog.get();
				folderSelector.setReturnHandler(returnHandler);
				folderSelector.show();
				btnChgFolder.focusInItem();
			}
		});
		
		dfFolder = new DynamicForm();
		dfFolder.setWidth(540);
		dfFolder.setHeight(15);
		dfFolder.setNumCols(3);
		dfFolder.setColWidths("5", "50", "*");
		dfFolder.setMargin(5);
		dfFolder.setAlign(Alignment.LEFT);
		dfFolder.setShowEdges(false);
		
		dfFolder.setFields(txtfolder, btnChgFolder);
		
		addMember(dfFolder);
	}

	// panel ���� ����� ȣ��
	protected ChangedHandler changeHandler = new ChangedHandler() {
		@Override
		public void onChanged(ChangedEvent event) {
			onModified();
		}
	};
	

	protected void prepareTabset() {
		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();

		propertiesTab = new Tab(I18N.message("properties"));
		propertiesTab.setID("propertiesTab");
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);

		extendedPropertiesTab = new Tab(I18N.message("extentionproperties"));
		extendedPropertiesTab.setID("extendedPropertiesTab");
		extendedPropertiesTabPanel = new HLayout();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTabPanel.setHeight100();
		extendedPropertiesTab.setPane(extendedPropertiesTabPanel);
		
		tabSet.addTab(propertiesTab);
		tabSet.addTab(extendedPropertiesTab);
		addMember(tabSet);
	}
	
	// ���Ͼ��δ��� ���� �г� ��ũ�ѹٸ� ǥ���ϱ� ���ؼ�
	VLayout vlayUpload;
	HTMLPane allowedFileTypesPane = new HTMLPane();
	private HLayout titlePanel;
	// ���� ���δ�
	private void prepareUploadSection(){
		titlePanel = new HLayout();
		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;" + I18N.message("second.uploadFiles") + "</div>");
		spacer.setWidth("20%");
		spacer.setOverflow(Overflow.HIDDEN);
		allowedFileTypesPane.setContents("<div>&nbsp;</div>");
		allowedFileTypesPane.setWidth("80%");
		allowedFileTypesPane.setAlign(Alignment.RIGHT);
		allowedFileTypesPane.setOverflow(Overflow.HIDDEN);
		allowedFileTypesPane.setHoverWidth(350);

		titlePanel.addMember(spacer);
		titlePanel.addMember(allowedFileTypesPane);
		titlePanel.setHeight(25);
		titlePanel.setMembersMargin(10);
		titlePanel.setWidth100();
		
		addMember(titlePanel);

		
		multiUploader = new MultiUploader();

		// ���δ� �̺�Ʈ ����
		multiUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		multiUploader.addOnStatusChangedHandler(onStatusChangedHandler);
		multiUploader.addOnCancelUploadHandler(onCancelUploaderHandler);
		multiUploader.addOnChangeUploadHandler(new IUploader.OnChangeUploaderHandler() {
			@Override
			public void onChange(IUploader uploader) {
//				if (!propertiesPanel.validateFileName(uploader.getBasename())){
//					Log.error(I18N.message("second.validation"), I18N.message("notAllowedFile"), true);
//					uploader.cancel();
//				}
//				else
//					if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
				boolean bInvalid = false;
				for (String fileName : uploader.getFileInput().getFilenames()) {
					if (!propertiesPanel.validateFileName(fileName)){
						bInvalid = true;
						break;
					}
				}
				
				if (bInvalid) {
					Log.error(I18N.message("second.validation"), I18N.message("notAllowedFile"), true);
					uploader.cancel();
				}
				else 
					if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			}
		});
		
		// �ִ� ���� �� ����
		try {
			int maximumFiles = Session.get().getInfo().getIntConfig("gui.uploaderMaxFiles", 10);
			multiUploader.setMaximumFiles(maximumFiles);
		} catch (Exception e) {
			multiUploader.setMaximumFiles(10);
		}
		
		// ���δ� ����
		multiUploader.setStyleName("upload");
		multiUploader.setFileInputPrefix("XEDM");
		multiUploader.setHeight("100%");
//		multiUploader.setWidth("100%");
		multiUploader.reset();
		multiUploader.setEnabled(true);
		
		vlayUpload = new VLayout();
		vlayUpload.addMember(multiUploader);
		vlayUpload.setHeight(50);
//		vlayUpload.setWidth100();
		
		vScrollLayout = new VLayout();
		
//		// ���δ��� ���δ� �г� ����. ��ũ���� ǥ���ϱ� ���ؼ� ���δ� �۾�
		vScrollLayout.setAlign(Alignment.LEFT);
		vScrollLayout.setBorder("1px solid black");
		vScrollLayout.setHeight(100);
		vScrollLayout.setWidth100();
		vScrollLayout.setOverflow(Overflow.AUTO);
		vScrollLayout.scrollToBottom();
		vScrollLayout.setAutoHeight();
		vScrollLayout.addChild(vlayUpload);
		
		addMember(vScrollLayout);
	}
	
	/**
	 * Uploader ���� Item���� ��Ʈ���Ѵ�.
	 * @param isShow
	 */
	public void controlUploader(boolean isShow){
		if(isShow){
			if (dfFolder != null)
				dfFolder.show();
			vScrollLayout.show();
			titlePanel.show();
		}
		else{
			if (dfFolder != null)
				dfFolder.hide();
			vScrollLayout.hide();
			titlePanel.hide();
		}
	}
	
	private void refreshStatus() {
		String strUploadFiles = "(" + multiUploader.getSuccessUploads() + "/" + multiUploader.getMaximumFiles() + ")";
		allowedFileTypesPane.setContents("<div>&nbsp;" + I18N.message("second.allowedFileTypes") + "&nbsp;:&nbsp;" +
				statusMessage + "&nbsp;&nbsp;" + I18N.message("second.uploadedFiles") + "&nbsp;:&nbsp;" + strUploadFiles + "</div>");
		allowedFileTypesPane.setTooltip(availableFileTypes);
		allowedFileTypesPane.redraw();

	}
	
	// ���Ͼ��δ� ���� ������ ���ε��Ѱ�� �߻� �̺�Ʈ
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		@Override
		public void onFinish(IUploader uploader) {
			Waiting.hide();
//			if (uploader.getStatus() == Status.SUCCESS) {
//				String fileInfo = uploader.getServerResponse();
//				String fileName = Util.removeExtend(multiUploader.getFileName());
//				
//				SContent content = new SContent();
//				content.setFileName(uploader.getBasename());
//				content.setFileSize(getFileSize(fileInfo));
//				
//				// 20131223na ���ϻ���� 0�̸� XVarm���� ���ε� �Ұ���
////				if(getFileSize(fileInfo) == 0){
////					SC.warn(I18N.message("youcantuploadfilesize"));
////					uploader.cancel();
////					uploader.reset();
////					return;
////				}
//				// 20131105, na, Ÿ��Ʋ�� ���� ��� ���ϸ����� �߰�
//				if(document.getTitle() == null){
//					document.setTitle(fileName);
//					propertiesPanel.setTitle(fileName);
//				}
//				// add to grid
//				propertiesPanel.addContent(content);
//				vlayUpload.setHeight(multiUploader.getOffsetHeight() + 10);
//				refreshStatus();
//				
//			}
			
			
			if (uploader.getStatus() == Status.SUCCESS) {
				
				for (UploadedInfo info : uploader.getServerMessage().getUploadedInfos()) {

					String fileName = Util.removeExtend(info.getFileName());
					String fileNameWithExt = Util.getFileName(info.getFileName());
					
					SContent content = new SContent();
					content.setFileName(fileNameWithExt);
					content.setFileSize(info.getSize());
					
					// 20131223na ���ϻ���� 0�̸� XVarm���� ���ε� �Ұ���
//					if(getFileSize(fileInfo) == 0){
//						SC.warn(I18N.message("youcantuploadfilesize"));
//						uploader.cancel();
//						uploader.reset();
//						return;
//					}
					// 20131105, na, Ÿ��Ʋ�� ���� ��� ���ϸ����� �߰�
					if(document.getTitle() == null){
						document.setTitle(fileName);
						propertiesPanel.setTitle(fileName);
					}
					// add to grid
					propertiesPanel.addContent(content);
					vlayUpload.setHeight(multiUploader.getOffsetHeight() + 10);
					refreshStatus();
				}
				
				if(uploader.getServerMessage().getUploadedInfos().size() == 0 && uploader.getServerRawResponse() != null){
					com.google.gwt.xml.client.Document doc = XMLParser.parse(uploader.getServerRawResponse());
					com.google.gwt.xml.client.NodeList fileNamenodeList = doc.getElementsByTagName("name");
					com.google.gwt.xml.client.NodeList sizeNodeList = doc.getElementsByTagName("size");
					
					String fileNameWithExt = fileNamenodeList.item(0).toString();
					fileNameWithExt = getNodeText(fileNameWithExt);
					String fileName = Util.removeExtend(fileNameWithExt);
					sizeNodeList.item(0).getNodeValue();
					
					SContent content = new SContent();
					content.setFileName(fileNameWithExt);
					content.setFileSize(getFileSize(sizeNodeList.toString()));
					
					if(document.getTitle() == null){
						document.setTitle(fileName);
						propertiesPanel.setTitle(fileName);
					}
					// add to grid
					propertiesPanel.addContent(content);
					vlayUpload.setHeight(multiUploader.getOffsetHeight() + 10);
					refreshStatus();
				}
			}
			


		}
	};
	
	private String getNodeText(String str){
		int start = str.indexOf(">");
		int end = str.lastIndexOf("<");
		return str.substring(start + 1, end );
	}
	
	// ������ ����� �Ľ��Ѵ�
	private long getFileSize(String data){
		int start = data.indexOf("<size>");
		int end = data.indexOf("</size>");
		return Long.parseLong(data.substring(start+6, end));
	}


	// ���Ͼ��δ� ���� ���ε� �� ������ �Ѱ� ���
	private IUploader.OnCancelUploaderHandler onCancelUploaderHandler = new IUploader.OnCancelUploaderHandler() {
		@Override
		public void onCancel(IUploader uploader) {
			if(uploader.getStatus() == Status.SUCCESS || uploader.getStatus() == Status.CANCELED){
//				propertiesPanel.removeContent(uploader.getBasename());
////				if(rclist.getLength() == 1) uploader.clear();
//				vlayUpload.setHeight(multiUploader.getOffsetHeight() + 10);
//				refreshStatus();
				
				for (String iname : uploader.getServerMessage().getUploadedFileNames()) {
					propertiesPanel.removeContent(iname);
					vlayUpload.setHeight(multiUploader.getOffsetHeight() + 10);
				}
				refreshStatus();
			}
		}
	};
	
	// ���Ͼ��δ� ���º�ȭ�� �߻�
	private IUploader.OnStatusChangedHandler onStatusChangedHandler = new IUploader.OnStatusChangedHandler() {
		@Override
		public void onStatusChanged(IUploader uploader) {
			// �ߺ� ������ �ִ��� ã��
			if(uploader.getStatus() == Status.REPEATED ){
				uploader.reset();
				SC.warn(I18N.message("thisfileisalreadyexists"));
			}
		}
	};

	protected void refresh() {
		if (canSaveListener != null)
			canSaveListener.onReturn(false);
//		savePanel.setVisible(false);
		
		/*
		 * Prepare the standard properties tab
		 */
		if (propertiesPanel == null) {
			propertiesPanel = new StandardPropertiesPanel(document, changeHandler, documentObserver, attachGridRecordEditable);
			propertiesTabPanel.addMember(propertiesPanel);
		}

		/*
		 * Prepare the extended properties tab
		 */
		if (extendedPropertiesPanel == null) {
			extendedPropertiesPanel = new ExtendedPropertiesPanel(document, changeHandler);
			extendedPropertiesTabPanel.addMember(extendedPropertiesPanel);
		}

		// doctype �� ����Ǹ� extendedPropertiesPanel �� template id�� ��������� ��.
		// 20130814, junsoo, setDocument �ϱ� ���� ȣ���ؾ���.
		propertiesPanel.setDocTypeChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				extendedPropertiesPanel.refresh();
			}
		});
		
		propertiesPanel.setStatusChangedListener(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				// ȭ�鿡 ������ ����Ȯ���� ǥ��
				redrawAllowedFileTypes();
				
				// uploader�� ����.
				String[] exts = propertiesPanel.getAvailableFileTypesArray();
				if (multiUploader != null)
					multiUploader.setValidExtensions(exts);
			}
		});
		
		// set document
		propertiesPanel.setDocument(document);
		extendedPropertiesPanel.setDocument(document);
		
		// ȭ�鿡 ������ ����Ȯ���� ǥ��
		redrawAllowedFileTypes();
		
		// �� �Լ��� ȣ���Ͽ�, ��������� �������� �̸� �Ǵ���.
		onModified();
	}
	
	public SDocument getDocument() {
		return document;
	}
	
	public boolean getAttachGridRecordEditable() {
		return attachGridRecordEditable;
	}

	public Tab getPropertiesTab() {
		return propertiesTab;
	}

	public Tab getExtendedPropertiesTab() {
		return extendedPropertiesTab;
	}

	public StandardPropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}

	private void onModified() {
		if (canSaveListener != null)
			canSaveListener.onReturn(false);
//		savePanel.setVisible(false);
		
//		if (multiUploader != null)
//			multiUploader.setEnabled(false);
		
		// TODO: save �ص� �Ǵ��� �Ǵ�. content �ö�� �ִ°� �ִ���.. title �ԷµǾ� �ִ��� ��.
		if (document.getTitle() == null || document.getTitle().length() < 1)
			return;
		
		// ���δ� Ȱ��ȭ
		if (multiUploader != null && document.getDocType() != null)
			multiUploader.setEnabled(true);
		else {
			if (multiUploader != null)
				multiUploader.setEnabled(false);
		}
		
		if (document.getContents() == null || document.getContents().length < 1) {
			return;
		}
		
		if (canSaveListener != null)
			canSaveListener.onReturn(true);
//		savePanel.setVisible(true);
	}

	public boolean validate() {
		boolean stdValid = propertiesPanel.validate();
		boolean extValid = extendedPropertiesPanel.validate();
//		boolean extValid = true;
		if (!stdValid)
			tabSet.selectTab(propertiesTab);
//			tabSet.selectTab(0);
		else if (!extValid)
			tabSet.selectTab(extendedPropertiesTab);
//			tabSet.selectTab(1);
		return stdValid && extValid;
	}
}