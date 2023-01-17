package com.speno.xedm.gui.frontend.client.folder;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderClosedEvent;
import com.smartgwt.client.widgets.tree.events.FolderClosedHandler;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * AdminTree abstract class
 * 
 * @author 박상기
 * @since 1.0
 */
public abstract class AdminTree extends TreeGrid implements FolderObserver{
	protected RecordObserver recordObserver;
	
	public AdminTree(RecordObserver ob){
		this(ob, I18N.message("name"));
	}
	public AdminTree(RecordObserver ob, String nameTitle){
		this.recordObserver = ob;
		
		setWidth100();
		setHeight100();
		setBorder("0px");
		setBodyStyleName("normal");
		setLoadDataOnDemand(false);
		setOpenIconSuffix("");		
		setShowOpenIcons(true);   
		setShowDropIcons(false);   
		setClosedIconSuffix("");
		setShowHeader(false);		
		
		setSelectionType(SelectionStyle.SINGLE);
		setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		setAutoFitFieldWidths(true);
		//setAutoFetchData(true);
		//setDataSource(GroupsDS.get(String.valueOf(SGroup.TYPE_GROUP)));
		
        ListGridField name = new ListGridField("name", nameTitle);
		setFields(name);
		
		
		addFolderOpenedHandler(new FolderOpenedHandler() {
			@Override
			public void onFolderOpened(FolderOpenedEvent event) {
				GWT.log("[ AdminTree onFolderOpened ]", null);
				getFolderDataRpcChild( event.getNode(), false);
			}
		});
		
		addFolderClosedHandler(new FolderClosedHandler() {
			@Override
			public void onFolderClosed(FolderClosedEvent event) {
				GWT.log("[ AdminTree onFolderClosed ]", null);
				
				TreeNode onNode = event.getNode();
				String id = onNode.getAttributeAsString("id");
				String parentId = onNode.getAttributeAsString("parentId");
				TreeNode selectedNode = (TreeNode)getSelectedRecord();
				
				if(selectedNode == null || !id.equals(selectedNode.getAttributeAsString("id"))) {
					deselectAllRecords();
					selectRecord(onNode);
					if(recordObserver != null) {
						if (recordObserver.isIDLong()) {
							try {
								long lid = Long.parseLong(id);
								long lparentId = Long.parseLong(parentId);
								recordObserver.onRecordSelected(lid, lparentId);
							} catch (Exception e) {
								recordObserver.onRecordSelected(id, parentId);
							}
						} else {
							recordObserver.onRecordSelected(id, parentId);
						}
						recordObserver.onRecordSelected(onNode);
		        	}
					getFolderDataRpcChild(onNode, false);	
				}
			}	
		});
		
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				GWT.log("[ AdminTree onCellClick ]", null);
				
				TreeNode onNode = (TreeNode)event.getRecord();			
				
				
				if(onNode.getAttributeAsBoolean("expand")) {
					if(!getTree().isOpen(onNode) ) {
						getFolderDataRpcChild(onNode, false);
						getTree().openFolder(onNode);
					}	
				}
				
				if(recordObserver != null) {
					String id = onNode.getAttributeAsString("id");
					String parentId = onNode.getAttributeAsString("parentId");
					
					if (recordObserver.isIDLong()) {
						try {
							long lid = Long.parseLong(id);
							long lparentId = Long.parseLong(parentId);
							recordObserver.onRecordSelected(lid, lparentId);
						} catch (Exception e) {
							recordObserver.onRecordSelected(id, parentId);
						}
					} else {
						recordObserver.onRecordSelected(id, parentId);
					}
					
					recordObserver.onRecordSelected(onNode);
            	}
			}
		});
		
		addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				GWT.log("[ AdminTree onCellContextClick ]", null);
				event.cancel();
			}
		});
		
		addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				GWT.log("[ AdminTree onCellDoubleClick ]", null);
				//20130827 남윤성 위임에서 위임자 선택시 위임자의 그룹id 필요함
				TreeNode onNode = (TreeNode)event.getRecord();	
				
				if(onNode.getAttributeAsBoolean("expand")) {
					if(!getTree().isOpen(onNode) ) {
						getFolderDataRpcChild(onNode, false);
						getTree().openFolder(onNode);
					}	
				}
				
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(onNode);
            	}
			}
		});
		
		addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				GWT.log("[ AdminTree onSelectionUpdated ]", null);
				
//				TreeNode onNode = (TreeNode)getSelectedRecord();			
//				
//				if(onNode.getAttributeAsBoolean("expand")) {
//					if(!getTree().isOpen(onNode) ) {
//						getFolderDataRpcChild(onNode, false);
//						getTree().openFolder(onNode);
//					}	
//				}
//				
//				if(recordObserver != null) {
//					String sid = onNode.getAttributeAsString("id");
//					String sparentId = onNode.getAttributeAsString("parentId");
//					if (recordObserver.isIDLong()) {
//						try {
//							long id = Util.getAslong(sid);		
//							long parentId = Util.getAslong(sparentId);
//							recordObserver.onRecordSelected(id, parentId);
//							recordObserver.onRecordSelected(onNode);
//						} catch (Exception e) {
//							recordObserver.onRecordSelected(sid, sparentId);
//							recordObserver.onRecordSelected(onNode);
//						}
//					} else {
//						recordObserver.onRecordSelected(sid, sparentId);
//						recordObserver.onRecordSelected(onNode);
//					}
//					
//            	}
			}
		});
		
		addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				GWT.log("[ AdminTree onRecordDoubleClick ]", null);
				event.cancel();
			}
		});
	}
	
	public String getNameTitle() {
		return getField("name").getTitle();
	}
	
	public void setNameTitle(String title) {
		getField("name").setTitle(title);
	}
	
	public void onSubFolderServerAdded() {
		GWT.log("[ AdminTree onSubFolderServerAdded ]", null);
		final TreeNode selectNode = (TreeNode) getSelectedRecord();
		
		//자식생성에 성공했으므로 expand true로 변경함
		selectNode.setAttribute("expand", true);		
		refreshRow(getRecordIndex(selectNode));
		
		getFolderDataRpcChild(selectNode, true);
	}
	
	public void refreshNode(TreeNode node){
		refreshRow(getRecordIndex(node));
	}
	
	public void onFolderServerRemoved() {
		GWT.log("[ AdminTree onFolderServerRemoved ]", null);
		
		final TreeNode selectNode = (TreeNode) getSelectedRecord();
//		String parentId = selectNode.getAttributeAsString("parentId");
		this.removeData(selectNode);		
		
		
		//20131226na 왜 삭제된 노드의 형제 노드를 찾는지 이유를 모르겠음
		//관리자 공유폴더에서 삭제시 오류발생으로 인해 주석처리
//		TreeNode parentRecord = (TreeNode)(getRecordList().find("id", parentId));
//		String grandParentId = parentRecord.getAttributeAsString("parentId");
//		
//		
//		//삭제된 노드의 부모를 부모로 갖고 있는 노드가 존재하는지 여부 체크
//		Record[] records = getRecordList().findAll("parentId", parentId);
//		if(records == null) {
//			parentRecord.setAttribute("expand", false);
//			refreshRow(getRecordIndex(parentRecord));
//		}
//		
//		this.selectRecord(parentRecord);
//		if(recordObserver != null) {
//			recordObserver.onRecordSelected(Util.getAslong(parentId), Util.getAslong(grandParentId));
//			recordObserver.onRecordSelected(parentRecord);
//    	}
//		getFolderDataRpcChild(parentRecord, true);
	}
	
	protected abstract void getFolderDataRpcChild( final TreeNode selectNode, final boolean isForceOpen);
	
	public abstract void getFolderDataRpc(final Serializable id, final boolean isShowVirtualRoot);

	public String getPath(long id) {
		TreeNode selectedNode = getTree().find("id", Long.toString(id));
		String path = "";
		TreeNode[] parents = getTree().getParents(selectedNode);
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
				path += "/" + parents[i].getName();
		}
		path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
		return path;
	}
	
	@Override
	public void onFolderSaved(SFolder folder) {
		TreeNode selectedNode = getTree().find("id", Long.toString(folder.getId()));
		if (selectedNode != null) {
			selectedNode.setTitle(folder.getName());
			selectedNode.setName(folder.getName());
			getTree().reloadChildren(selectedNode);
		}
		folder.setPathExtended(getPath(folder.getId()));
	}

	@Override
	public void enable() {
		super.enable();
		getTree().setReportCollisions(false);
	}

	@Override
	protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
		if(record == null) return "";
		if ("1".equals(record.getAttribute("type"))) {
			return "font-weight:bold;";
		} else
			return super.getCellCSSText(record, rowNum, colNum);
	}

	@Override
	protected String getIcon(Record record, boolean defaultState) {
		if ("1".equals(record.getAttribute("type"))) {
			setCustomNodeIcon(record, Util.imageUrl("cube_blue16.png"));
		}
		return super.getIcon(record, defaultState);
	}

	@Override
	public void onFolderSelected(SFolder folder) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFolderReload() {
		// TODO Auto-generated method stub		
	}
}