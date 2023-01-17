package com.speno.xedm.gui.frontend.client.document;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.clipboard.Clipboard;
import com.speno.xedm.gui.frontend.client.folder.NavigatorForDocument;

public class DocumentsMove extends Window implements DocumentObserver{

	// mydoc Ʈ��
	protected NavigatorForDocument MoveMydocTree;
	// shared Ʈ��
	protected NavigatorForDocument MoveShareTree;
	private DocumentObserver observer;
	private long[] docid;
	public DocumentsMove(String sid, long[] docid, final DocumentObserver observer ) {
		this.observer = observer;
		this.docid = docid;
		
        prepareBase();
        // Ʈ�������
        prepareTree();
        // ��ư�����
        prepareBtn();
	}
	
	private void prepareBtn(){
		DynamicForm dfButton = new DynamicForm();   
        dfButton.setHeight(30);  dfButton.setWidth100();   
        dfButton.setPadding(5); dfButton.setMargin(5);
        dfButton.setAlign(Alignment.CENTER);
        dfButton.setNumCols(3);
        dfButton.setColWidths("20", "10", "10");
        
        StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "", "");
        dummy.setShowTitle(false);
        
        ButtonItem btnOk = new ButtonItem();
        btnOk.setTitle(I18N.message("ok"));
        btnOk.setWidth(100);
        btnOk.setStartRow(false); btnOk.setEndRow(false);
        btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String folderid = "";
				// �̵���ų ������ ���̵�
				if(MoveMydocTree.getSelectedRecord() != null){
					folderid = MoveMydocTree.getSelectedRecord().getAttribute("folderId");
				}else if(MoveShareTree.getSelectedRecord() != null){
					folderid = MoveShareTree.getSelectedRecord().getAttribute("folderId");
				}
				
				// �̵�
				ServiceUtil.folder().paste(Session.get().getSid(), docid,Long.parseLong( folderid ), Clipboard.CUT, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						// ����ǥ��
						observer.onServiceComplite(I18N.message("complitemove") );
						destroy();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});
			}
		});
        
        ButtonItem btnCancel = new ButtonItem();
        btnCancel.setTitle(I18N.message("cancel"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false); btnCancel.setEndRow(false);
        btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
        
        dfButton.setItems(dummy, btnOk, btnCancel);
        addItem(dfButton);
	}
	
	private void prepareBase(){
		setWidth(300);   
        setHeight(600);   
        setTitle(I18N.message("movedocuments"));   
        setShowMinimizeButton(false);   
        setIsModal(true);   
        setShowModalMask(true);   
        centerInPage();
        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();   
			}
		});
	}
	
	private void prepareTree(){
        VLayout vlayout = new VLayout();
        vlayout.setWidth100();
        vlayout.setHeight100();
        
        /*
         * �ΰ��� Ʈ���� �����ϰ� ������ Ʈ���� �����ϸ� �ٸ�Ʈ���� deselect ���·� �����.
         * ������ �����ϰ� ��Ʈ ���������� �����´�.
         */
		MoveMydocTree = new NavigatorForDocument(1);
        setTree(MoveMydocTree);
        MoveMydocTree.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				// shared Ʈ�� �𼿷�Ʈ
				MoveShareTree.deselectAllRecords();
			}
		});
        
        // Ʈ�� ������ ����
        ListGridField name = new ListGridField("name");
        MoveMydocTree.setFields(name);
        // Ʈ�� ��Ʈ���� ������ ��������
        MoveMydocTree.getFolderDataRpc(1, 0, "folderId", "name", "parent", "type", false);
   		
        MoveShareTree = new NavigatorForDocument(0);
        setTree(MoveShareTree);
        MoveShareTree.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				// mydoc Ʈ�� �𼿷�Ʈ
				MoveMydocTree.deselectAllRecords();
			}
		});
        
        // Ʈ�� ������ ����
        name = new ListGridField("name");
        MoveShareTree.setFields(name);
        // Ʈ�� ��Ʈ���� ������ ��������
        MoveShareTree.getFolderDataRpc(0, 0, "folderId", "name", "parent", "type", false);
        
        Label lblTitleMydoc = new Label(I18N.message("mydoc"));
        lblTitleMydoc.setHeight(12);
        Label lblTitleShared = new Label(I18N.message("shareddoc"));
        lblTitleShared.setHeight(12);
        
        vlayout.setMargin(5);
        vlayout.addMembers(lblTitleMydoc, MoveMydocTree, lblTitleShared, MoveShareTree);
        addItem(vlayout);
	}
	
	// Ʈ�� ����
	private void setTree(NavigatorForDocument tree){
		tree.setWidth100();
		tree.setHeight(240);
		tree.setBodyStyleName("normal");
		tree.setLoadDataOnDemand(false);   
		tree.setNodeIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		tree.setFolderIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		tree.setShowOpenIcons(false);   
		tree.setShowDropIcons(false);   
		tree.setClosedIconSuffix("");   
		tree.setShowHeader(false);
		tree.setMargin(5);
//		tree.setBorder("1px solid blue");
		tree.setShowEdges(true);
		tree.setEdgeSize(3);
	}

	@Override
	public void onDocumentSaved(SDocument document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceComplite(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReloadRequest(SFolder folder) {
		// TODO Auto-generated method stub
		
	}
	
}