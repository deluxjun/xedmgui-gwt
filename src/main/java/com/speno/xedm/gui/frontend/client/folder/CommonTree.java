package com.speno.xedm.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;

/**
 * Tree 공동 모듈
 * 
 * Created : 20130816, junsoo
 * 
 * @author deluxjun
 *
 */
public class CommonTree extends TreeGrid {
	public static final String NODE_ID = "folderId";
	public static final String NODE_NAME = "name";
	public static final String NODE_PARENT = "parent";
	public static final String NODE_TYPE = "type";
	public static final String NODE_SELECTABLE = "selectable";
	public static final String NODE_OBJECT = "object";
	public static final String NODE_EXPANDABLE = "expand";
	
	private TreeNode rootNode; 
	
	private ReturnHandler<TreeNode> openedHandler;
	private ReturnHandler<TreeNode> cellClickedListener;
	
	public CommonTree(){

		//기본 속성
		setWidth("100%");
		setHeight("100%");
		setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		setAutoFitFieldWidths(true);
		setShowAllRecords(true);
		setCanResizeFields(true);
		
		setBorder("0px");
		setBodyStyleName("normal");
		setLoadDataOnDemand(false);   
	    setNodeIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		setFolderIcon(ItemFactory.newImgIcon("folder.png").getSrc());   
		setOpenIconSuffix("");
		setShowOpenIcons(true);   
		setShowDropIcons(false);   
		setClosedIconSuffix("");   
		setShowHeader(false);
		setSelectionType(SelectionStyle.SINGLE);
		
		// 폴더 트리 폴더이름
		ListGridField name = new ListGridField("name");
		setFields(name);
		
		// 폴더 + 클릭시 하위 폴더 가져오기
		addFolderOpenedHandler( new FolderOpenedHandler() {
			@Override
			public void onFolderOpened(FolderOpenedEvent event) {
				if (openedHandler != null)
					openedHandler.onReturn(event.getNode());
//				getFolderDataRpcChild(folderType, Long.parseLong(event.getNode().getAttribute("folderId")), event.getNode(), "folderId", "name", "parent", "type");
			}
		});
		
		// 폴더 클릭시 하위 폴더 가져오고 폴더 오픈
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord rc = event.getRecord();
				TreeNode tn = getTree().findById(rc.getAttribute(NODE_ID));
				
				// listener 호출.
				if (cellClickedListener != null)
					cellClickedListener.onReturn(tn);
				
				boolean isopen = getTree().isOpen(tn);
				
				if(!isopen){
					TreeNode selectedNode = (TreeNode) getSelectedRecord();
					if(selectedNode.getAttributeAsBoolean(NODE_EXPANDABLE))
						openedHandler.onReturn(selectedNode);
					getTree().openFolder(selectedNode);
				}
			}
		});
	}

	public void setOpendHandler(ReturnHandler<TreeNode> openedHandler) {
		this.openedHandler = openedHandler;
	}
	
	public void setCellClickedListener(ReturnHandler<TreeNode> cellClickedListener) {
		this.cellClickedListener = cellClickedListener;
	}

	public void initRoot(SFolder[] roots) {
		// 루트 셋팅
		rootNode = new TreeNode();
		rootNode.setAttribute(NODE_ID, Constants.DOCUMENTS_FOLDERID);
		rootNode.setAttribute(NODE_NAME, "root");

		List<TreeNode> nodeList = new ArrayList<TreeNode>();
		for (int i = 0; i < roots.length; i++) {
			TreeNode node =  new TreeNode();
			node.setAttribute(NODE_ID, roots[i].getId());					
			node.setAttribute(NODE_NAME, roots[i].getName());
			node.setAttribute(NODE_PARENT, Constants.DOCUMENTS_FOLDERID);
			node.setAttribute(NODE_TYPE, roots[i].getType());
			node.setAttribute(NODE_SELECTABLE, roots[i].isSelectable());
			node.setAttribute(NODE_OBJECT, roots[i]);
			
			// 자신에게 딸린 폴더가 있는지 여부
			node.setAttribute(NODE_EXPANDABLE, true);
			node.setIsFolder(true);

			nodeList.add(node);
		}
		
		// 트리 만들기
		Tree dataTree = new Tree();
		dataTree.setModelType(TreeModelType.PARENT);
		dataTree.setIdField(NODE_ID);
		dataTree.setParentIdField(NODE_PARENT);
		dataTree.setNameProperty(NODE_NAME);
		
		dataTree.setAutoOpenRoot(true);
		dataTree.setReportCollisions(false);
		
		dataTree.setShowRoot(false);
		
		// 루트와 트리 셋팅
		dataTree.setRoot(rootNode);
		dataTree.setData(nodeList.toArray(new TreeNode[0]));
		
		setData(dataTree);
		refreshFields();
	}

}