package com.speno.xedm.gui.frontend.client.document.popup;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.folder.CommonTree;

public class FolderSelectorDialog extends Window {
	// mydoc 트리
	protected CommonTree tree;
	protected boolean isSingleton = false;
	private ButtonItem btnOk;
	private boolean bInit = false;
	
	protected static FolderSelectorDialog instance;
	public static FolderSelectorDialog get() {
		if (instance == null) {
			instance = new FolderSelectorDialog();
			instance.isSingleton = true;
		}
		return instance;
	}
	
	private ReturnHandler<SFolder> returnHandler;		// 20130806, junsoo, 결과를 리턴할 handler
	
	private boolean sharedSelectable = false;
	
	public void setSharedSelectable(boolean sharedSelectable) {
		this.sharedSelectable = sharedSelectable;
	}
	
	public void setReturnHandler(ReturnHandler<SFolder> returnHandler) {
		this.returnHandler = returnHandler;
	}
	// 20130806, junsoo, 결과 리턴자 추가
	public FolderSelectorDialog(ReturnHandler<SFolder> returnHandler) {
		setReturnHandler(returnHandler);
		
        prepareBase();
        // 트리 그리기
        prepareTree();
        // 확인
        prepareBtn();
	}

	public FolderSelectorDialog() {
        prepareBase();
        // 트리 그리기
        prepareTree();
        // 확인
        prepareBtn();
	}
	
	private void prepareBase(){
		setWidth(300); 
		setHeight(580);   
        setTitle(I18N.message("selectpath"));   
        setShowMinimizeButton(false);   
		setCanDragResize(true);
		setCanDragReposition(true);
        setIsModal(true);   
        setShowModalMask(true);   
        centerInPage();
        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				close();
			}
		});
	}
	
	private void prepareBtn(){
		DynamicForm dfButton = new DynamicForm();
        dfButton.setHeight(30);  dfButton.setWidth100();   
        //dfButton.setPadding(5); //dfButton.setMargin(5);
        dfButton.setAlign(Alignment.CENTER);
        dfButton.setNumCols(3);
        dfButton.setColWidths("20", "10", "10");
        
        StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "", "");
        dummy.setShowTitle(false);
        
        btnOk = new ButtonItem();
        btnOk.setTitle(I18N.message("ok"));
        btnOk.setWidth(100);
        btnOk.setStartRow(false); btnOk.setEndRow(false);
        btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (tree.getSelectedRecord() == null)
					return;
				SFolder selectedFolder = (SFolder)tree.getSelectedRecord().getAttributeAsObject(CommonTree.NODE_OBJECT);
				selectFolder(tree, selectedFolder);
			}
		});
        
        // 취소
        ButtonItem btnCancel = new ButtonItem();
        btnCancel.setTitle(I18N.message("cancel"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false); btnCancel.setEndRow(false);
        btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
        
        dfButton.setItems(dummy, btnOk, btnCancel);
        
		btnOk.setDisabled(true);

        addItem(dfButton);
	}
	
	// 폴더 경로와 폴더 전달
	public void selectFolder(CommonTree tgrid, SFolder folder) {
		folder.setPathExtended(Util.getPath(tgrid, folder.getId()));

		// 20130806, junsoo, 결과 리턴
		if (returnHandler != null) {
			returnHandler.onReturn(folder);
			closeWindow();
		}
	}
	

	private void closeWindow(){
		if (!isSingleton)
			destroy();
		hide();
	}
	
	// 트리 그리기
	private void prepareTree(){
        VLayout vlayout = new VLayout();
        vlayout.setWidth100();
        vlayout.setHeight("95%");

        tree = new CommonTree();
        // 속성 설정
		tree.setWidth100();
		tree.setHeight100();
		tree.setBodyStyleName("normal");
		tree.setLoadDataOnDemand(false);   
		tree.setNodeIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		tree.setFolderIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		tree.setShowOpenIcons(false);   
		tree.setShowDropIcons(false);   
		tree.setClosedIconSuffix("");   
		tree.setShowHeader(false);
		tree.setMargin(5);
		tree.setShowEdges(true);
		tree.setEdgeSize(3);
    	
    	// 더블 클릭시 ok버튼 클릭과 동일한 처리
		tree.addDoubleClickHandler(new DoubleClickHandler() {
    		@Override
    		public void onDoubleClick(DoubleClickEvent event) {
    			boolean selectable = tree.getSelectedRecord().getAttributeAsBoolean(CommonTree.NODE_SELECTABLE);
    			if (!selectable)
    				return;
    			
				SFolder selectedFolder = (SFolder)tree.getSelectedRecord().getAttributeAsObject(CommonTree.NODE_OBJECT);

    			selectFolder(tree, selectedFolder);
    		}
    	});
		
		// node open 액션 정의
		tree.setOpendHandler(nodeOpenedHandler);
		tree.setCellClickedListener(cellClickedListener);
		
        vlayout.setMargin(5);
        vlayout.addMembers(tree);

        addItem(vlayout);
		
//    	tree.getFolderDataRpc(9, 0, "folderId", "name", "parent", "type", false);
	}

	@Override
	public void show() {
		super.show();
		 
		if (!bInit) {
			SFolder[] roots;
			
			if(Util.getSetting("setting.mydoc")){
				roots = new SFolder[2];
				roots[0] = new SFolder(Session.get().getHomeFolderId(), I18N.message("mydoc"), 0, Constants.FOLDER_TYPE_MYDOC);
				roots[0].setSelectable(true);
				if (Constants.WORKSPACE_DEFAULTID == Session.get().getHomeFolderId())
					roots[0].setPaths("/" + Constants.WORKSPACE_DEFAULTID);
				else
					roots[0].setPaths("/" + Constants.WORKSPACE_DEFAULTID + "/" + Session.get().getHomeFolderId());

				roots[1] = new SFolder(Constants.SHARED_DEFAULTID, I18N.message("shareddoc"), 0, Constants.FOLDER_TYPE_SHARED);
				roots[1].setSelectable(sharedSelectable);
				roots[1].setPaths("/" + Constants.SHARED_DEFAULTID);
			}
			else{
				roots = new SFolder[1];
				roots[0] = new SFolder(Constants.SHARED_DEFAULTID, I18N.message("shareddoc"), 0, Constants.FOLDER_TYPE_SHARED);
				roots[0].setSelectable(sharedSelectable);
			}
			
			tree.initRoot(roots);
		}
		bInit = true;
	}

	/**
	 * 개인 문서함만 보여준다.
	 * 20130819 taesu
	 * */
	public void showPersonal(){
		super.show();
		 
		if (!bInit) {
			SFolder[] roots = new SFolder[1];
			roots[0] = new SFolder(Session.get().getHomeFolderId(), I18N.message("mydoc"), 0, Constants.FOLDER_TYPE_MYDOC);
			roots[0].setSelectable(true);
			tree.initRoot(roots);
		}
		bInit = true;
	}
	@Override
	public void destroy() {
		instance = null;
		super.destroy();
	}
	
    // 트리 노드 선택되었을 때의 액션 정의
    private ReturnHandler<TreeNode> nodeOpenedHandler = new ReturnHandler<TreeNode>() {
    	public void onReturn(final TreeNode node) {
    		long folderId = Long.parseLong(node.getAttribute(CommonTree.NODE_ID));
    		int folderType = Integer.parseInt(node.getAttribute(CommonTree.NODE_TYPE));
    		
    		ServiceUtil.folder().listFolderByTypeAndParentId(Session.get().getSid(), folderType, folderId, new AsyncCallback<List<SFolder>>() {
    			@Override
    			public void onSuccess(List<SFolder> result) {
    				SFolder[] sfolder = new SFolder[result.size()];
    				
    				TreeNode[] returnNode = new TreeNode[result.size()];
    				for(int i=0; i< result.size(); i++){
    					sfolder[i] = result.get(i);
    					returnNode[i] = new TreeNode();
    					returnNode[i].setAttribute(CommonTree.NODE_ID, sfolder[i].getId());
    					returnNode[i].setAttribute(CommonTree.NODE_NAME, sfolder[i].getName());
    					returnNode[i].setAttribute(CommonTree.NODE_PARENT, sfolder[i].getParentId());
    					returnNode[i].setAttribute(CommonTree.NODE_TYPE, sfolder[i].getType());
    					returnNode[i].setAttribute(CommonTree.NODE_SELECTABLE, true);		// 선택가능한지 여부
    					returnNode[i].setAttribute(CommonTree.NODE_OBJECT, sfolder[i]);
    
    					if(sfolder[i].isParentOrNot()){
    						returnNode[i].setAttribute(CommonTree.NODE_EXPANDABLE, true);
    						returnNode[i].setIsFolder(true);
    					}
    					else{
    						returnNode[i].setAttribute(CommonTree.NODE_EXPANDABLE, false);
    						returnNode[i].setIsFolder(false);
    					}
    					// 트리에 삽입
    					if(returnNode[i] != null ) tree.getTree().add(returnNode[i], node);
    			    }
    
    				tree.refreshFields();
    			}
    			
    			@Override
    			public void onFailure(Throwable caught) {
    				Log.serverError(caught, false);
    			}
    		});
    	};
	};
	
	// 트리 선택이 변경되었을 때 호출되는 리스너
	private ReturnHandler<TreeNode> cellClickedListener = new ReturnHandler<TreeNode>() {
		
		@Override
		public void onReturn(TreeNode param) {
			if (param.getAttributeAsBoolean(CommonTree.NODE_SELECTABLE))
				btnOk.setDisabled(false);
			else
				btnOk.setDisabled(true);
		}
	};
}