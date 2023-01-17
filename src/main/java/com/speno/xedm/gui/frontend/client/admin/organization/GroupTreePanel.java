package com.speno.xedm.gui.frontend.client.admin.organization;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.folder.AdminGroupTree;

/**
 * GroupTree Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class GroupTreePanel extends VLayout  implements RecordObserver  {	
	private static HashMap<String, GroupTreePanel> instanceMap = new HashMap<String, GroupTreePanel>();
	
	public AdminGroupTree tree;
	private DynamicForm form;
	private Window window;
	private SUser user;
	
	//상위 RecordObserver에게 Callback하기위해 RecordObserver를 맴버로도 가짐.
	private final RecordObserver recordObserver;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param isCanDrag
	 * @param isShowRoot
	 * @param isShowAct
	 * @return
	 */
	public static GroupTreePanel get(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct) {
		return get(id, ob, isCanDrag, isShowRoot, isShowAct, "100%");
		
	}
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param isCanDrag
	 * @param isShowRoot
	 * @param isShowAct
	 * @param width
	 * @return
	 */
	public static GroupTreePanel get(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag, 
			final boolean isShowRoot, 
			final boolean isShowAct,
			final String width) {
		if (instanceMap.get(id) == null) {
			new GroupTreePanel(id, ob, isCanDrag, isShowRoot, isShowAct, width);
		}		
		return instanceMap.get(id);
	}
	
	public GroupTreePanel(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct) {
		this(id, ob, isCanDrag, isShowRoot, isShowAct, "100%");
	}
	
	public GroupTreePanel(
			final String id, 
			final RecordObserver ob,
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct,
			final String width,
			final String group){
		this(id,ob,isCanDrag,isShowRoot,isShowAct,width);
		
	}

	public GroupTreePanel(
			final String id, 
			final RecordObserver ob,
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct,
			final String width) {
		
		instanceMap.put(id, this);
		
		this.recordObserver = ob;
		tree = new AdminGroupTree(this);
//		tree.setShowFilterEditor(true);
		tree.setWidth100();
        tree.setBorder("1px solid gray");
        //tree.getDataSource().setCacheAllData(true);
        //tree.getDataSource().setAutoCacheAllData(true);
        
        if(isCanDrag) {
        	//tree의 drag 환경 설정
	        tree.setDragDataAction(DragDataAction.COPY);
	        tree.setCanReorderRecords(false);
	        tree.setCanAcceptDroppedRecords(true);
	        tree.setCanDragRecordsOut(true);        
	        tree.setCanRemoveRecords(false);
	        tree.setShowHeader(true);
        }
        
		/* Sub Title 생성 */
		Label subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("group"));

        addMember(subTitleLable);
        if(isShowAct) {
        	addMember(createLeftTopHL());
        }
        
        setMembersMargin(2);
        setHeight100();
        
        addMember(tree);
        setWidth(width);
          
        tree.getFolderDataRpc(SGroup.ROOTID, isShowRoot);      
	}
	
	/**
	 * 선택된 Tree의 Group Id 반환
	 * @return
	 */
	public String getSelectedGrpId() {
		ListGridRecord record = tree.getSelectedRecord();
		return record.getAttribute("id");
	}
	
	/**
	 * 선택된 Tree의 Group Name 반환
	 * @return
	 */
	public String getSelectedGrpNm() {
		ListGridRecord record = tree.getSelectedRecord();
		return record.getAttribute("name");
	}
	
	/**
	 * Tree 반환
	 * @return
	 */
	public ListGrid getGroupTree() {
		return tree;
	}
	
	/**
	 *
	 * @return
	 */
	private HLayout createLeftTopHL() {
		SInfo info = com.speno.xedm.gui.common.client.Session.get().getInfo();
		String temp = info.getConfig("gui.lock.hrinterface");
		
		Button addButton = new Button(I18N.message("add"));
		if(temp.equals("true"))
		addButton.disable();
		addButton.setWidth(80);
		addButton.setIcon("[SKIN]/actions/add.png");   
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				openAddEditWin("addgroup", getSelectedTreeNode());
			}
		});

		Button editButton = new Button(I18N.message("edit"));
		if(temp.equals("true"))
		editButton.disable();
		editButton.setWidth(80);
		editButton.setIcon("[SKIN]/actions/edit.png");
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// kimsoeun GS인증용 - 최상위 그룹 편집 못하게
				if("@@@0".equals(getSelectedTreeNode().getAttribute("id"))) {
					SC.say(I18N.message("cantEditRootGroup"));				
    				event.cancel();
        			return;
				}
				openAddEditWin("editgroup", getSelectedTreeNode());
			}
		});
       
        Button deleteButton = new Button(I18N.message("delete"));
		if(temp.equals("true"))
		deleteButton.disable();
        deleteButton.setWidth(80);
        deleteButton.setIcon("[SKIN]/actions/remove.png");
        deleteButton.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		// kimsoeun GS인증용 - 최상위 그룹 편집 못하게
        		if("@@@0".equals(getSelectedTreeNode().getAttribute("id"))) {
					SC.say(I18N.message("cantEditRootGroup"));				
    				event.cancel();
        			return;
				}
        		final TreeNode treeNode = getSelectedTreeNode();
        		
        		if(treeNode == null) {
        			event.cancel();
        			return;
        		}
        		
        		if("0".equals(treeNode.getAttributeAsString("id"))) {
        			SC.say(I18N.message("rootcannotdeleted"));
        			event.cancel();
        			return;
        		}
        		
        		boolean isExpand = Boolean.valueOf(treeNode.getAttributeAsString("expand"));
        		if(isExpand) {
        			SC.say(I18N.message("existnodecannotdeleted"));
        			event.cancel();
        			return;
        		}
        		
        		//2013.05.06 Member 있으면 삭제 안되게.
        		if(recordObserver != null) {
        			if(recordObserver.isExistMember() ) {
        				SC.say(I18N.message("existmembercannotdeleted"));				
        				event.cancel();
            			return;
        			}
        		}
        		
        		SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
        			@Override
        			public void execute(Boolean value) {
        				if(value != null && value) {
        					executeRemove(treeNode);
        				}
        			}
        		});
        		event.cancel();
        	}
        });
        
        HLayout topButtonsPanel = new HLayout();
        topButtonsPanel.setMembersMargin(5);
        topButtonsPanel.setHeight(15);
        topButtonsPanel.setMembers(addButton, editButton, deleteButton);
        return topButtonsPanel;
	}

	
	/**
	 * 선택된 Tree Node 반환
	 * @return
	 */
	private TreeNode getSelectedTreeNode() {
		final TreeNode treeNode = (TreeNode)tree.getSelectedRecord();
		if(treeNode == null) {
			SC.say(I18N.message("choosegroupfromtree"));
		}
		return treeNode;
	}
	
	/**
	 * Add 및 Edit Window Open
	 * @param divStr
	 * @param treeNode
	 */
	private void openAddEditWin(String divStr, final TreeNode treeNode) {		
		if(treeNode == null) return;
		
		final boolean isAdd = "addgroup".equals(divStr);
		
		if( !isAdd && "0".equals(treeNode.getAttributeAsString("id"))) {
			SC.say(I18N.message("rootcannotedited"));
			return;
		}
		
		window = new Window();   
        window.setWidth(320);   
        window.setHeight(170);   
        window.setTitle(I18N.message(divStr));
        window.setShowMinimizeButton(false);   
        window.setIsModal(true);   
        window.setShowModalMask(true);   
        window.centerInPage();   
        window.addCloseClickHandler(new CloseClickHandler() {   
            public void onCloseClick(CloseClickEvent event) {   
                //buttonTouchThis.setTitle("Touch This");   
                window.destroy();   
            }   
        });
        
        form = new DynamicForm();
        form.setHeight100();   
        form.setWidth100();   
        form.setPadding(5);   
        form.setLayoutAlign(VerticalAlignment.BOTTOM);   
       
        TextItem parentIdItem = new TextItem();
        if(isAdd) {
        	parentIdItem.setTitle(I18N.message("parentname"));
            parentIdItem.setValue(treeNode.getAttribute("name"));
            parentIdItem.setTooltip(I18N.message("createunderchosen", treeNode.getAttribute("name")));
        }
        else {
        	parentIdItem.setTitle(I18N.message("id"));
            parentIdItem.setValue(treeNode.getAttribute("id"));
            parentIdItem.setTooltip(I18N.message("fieldisreadonly", parentIdItem.getTitle()));
        }
        parentIdItem.setRequired(true);
        parentIdItem.disable();
        
        final TextItem nameItem = new TextItem();
        nameItem.setTitle(I18N.message("name"));
        nameItem.setValue("addgroup".equals(divStr) ? "" : treeNode.getAttribute("name"));
        nameItem.setRequired(true);
        // kimsoeun GS인증용 - 툴팁 다국어화
        nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
//        nameItem.setLength(Constants.MAX_LEN_NAME);
        nameItem.setValidators(new LengthValidator(nameItem, Constants.MAX_LEN_NAME));
    
        final  TextItem descItem = new TextItem();   
        descItem.setTitle(I18N.message("description"));
        descItem.setValue("addgroup".equals(divStr) ? "" : treeNode.getAttribute("description"));
//        descItem.setLength(Constants.MAX_LEN_DESC);
        descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_DESC));
        
        form.setFields(parentIdItem, nameItem, descItem);        
        
        Button okButton = new Button(I18N.message("save"));
        okButton.setIcon(ItemFactory.newImgIcon("accept.png").getSrc());
        okButton.setAutoFit(true);
        
        okButton.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
                if(form.validate()) {
                	if(isAdd) {
                		if(isUniqName(nameItem.getValueAsString(),  treeNode)) {
                			executeAdd(nameItem.getValueAsString(), descItem.getValueAsString(), treeNode);
                		}
                	}
                	else {
                		if(isUniqName(nameItem.getValueAsString(), tree.getTree().getParent(treeNode), treeNode)) {
                			executeUpdate(nameItem.getValueAsString(), descItem.getValueAsString(), treeNode);
                		}
                	}
                }
            }
        });
        
        Button cancelButton = new Button(I18N.message("cancel"));
        cancelButton.setAutoFit(true);
        cancelButton.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	window.destroy();   
            }   
        });
        
        HLayout bottom = new HLayout(10);
        bottom.setAlign(Alignment.CENTER);
        bottom.setMembers(okButton, cancelButton);
        
        VLayout layout = new VLayout(10);
        layout.setPadding(10);
        layout.setMembers(form, bottom);   
        layout.draw();
        
        window.addItem(layout);   
        window.show(); 
        nameItem.focusInItem();
	}
	
	/** 
	 * 트리의 이름이 유니크한지 비교하는 함수
	 * 
	 * @param name: 입력한 Text
	 * @param treeNode: 자신의 노드
	 * @return
	 */
	private boolean isUniqName(final String name, final TreeNode treeNode) {		
		return isUniqName(name, treeNode, null);
	}
	
	/**
	 * 20131203na 그룹 트리 변경시 자기 자신 제외 못하는 현상 제거한 isUniqName
	 * @param name
	 * @param treeNode: 추가의 경우 현재노드, 변경의 노드는 부모노드
	 * @param selfNode: 자신의 노드
	 * @return
	 */
	private boolean isUniqName(final String name, final TreeNode treeNode, final TreeNode selfNode) {		
		final TreeNode[] nodes = tree.getTree().getChildren(treeNode);
    	for(int j=0; j<nodes.length; j++) {
    		if(nodes[j].getAttributeAsString("name").equals(name)) {
    			//20131203na selfNode 비교 추가
    			if(selfNode != null){
    				String nodeName = nodes[j].getAttributeAsString("name");
    				if(selfNode.getName().equals(nodeName))
    					return true;
    			}
    			SC.warn(I18N.message("dupmessage"));
    			return false;
    		}
    	}
    	return true;
	}
	
	/**
	 * Tree Node 생성
	 * @param name
	 * @param description
	 * @param treeNode
	 */
	private void executeAdd(final String name, final String description, final TreeNode treeNode) {		
		String parentId = treeNode.getAttributeAsString("id");
		
		GWT.log("[ GroupTreePanel executeAdd ] parentId["+parentId+"], name["+name+"], description["+description+"]", null);
		
		SGroup group = new SGroup();
		group.setType(SGroup.TYPE_GROUP);
		group.setId("");
		group.setName(name);
		group.setDescription(description);
		group.setParentId(parentId);
		
		ServiceUtil.security().saveGroup(Session.get().getSid(), group, new AsyncCallbackWithStatus<SGroup>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SGroup result) {
				GWT.log("[ GroupTreePanel executeAdd ] onSuccess. id["+result.getId()+"]", null);
				
				tree.onSubFolderServerAdded();
				
				// kimsoeun GS인증용 - 그룹 생성 시 작업 완료 메시지 팝업
				SC.say(I18N.message("operationcompleted"));
				
				window.destroy();
			}
		});
	}
	
	/**
	 * Tree Node 수정
	 * @param name
	 * @param description
	 * @param treeNode
	 */
	private void executeUpdate(final String name, final String description, final TreeNode treeNode) {		
		final String id = treeNode.getAttributeAsString("id");
		final int type =Integer.parseInt(treeNode.getAttributeAsString("type"));
		final String parentId = treeNode.getAttributeAsString("parentId");
		final String path = treeNode.getAttributeAsString("path");
		final String iDPath = treeNode.getAttributeAsString("IDPath");
		final boolean parentOrNot = Boolean.valueOf(treeNode.getAttributeAsString("parentOrNot"));
		
		GWT.log("[ GroupTreePanel executeUpdate ] id["+id+"], parentId["+parentId+"], name["+name+"], description["+description+"], path["+path+"], IDPath["+iDPath+"]", null);
		
		SGroup group = new SGroup();
		group.setId(id);
		group.setName(name);
		group.setDescription(description);
		group.setType(type);
		group.setParentId(parentId);
		group.setPath(path);
		group.setIDPath(iDPath);
		group.setParentOrNot(parentOrNot);
		
		treeNode.setAttribute("name", name);
		treeNode.setAttribute("description", description);
		
		ServiceUtil.security().saveGroup(Session.get().getSid(), group, new AsyncCallbackWithStatus<SGroup>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SGroup result) {
				GWT.log("[ GroupTreePanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);				
				tree.refreshRow(tree.getRecordIndex(treeNode));
				
				// kimsoeun GS인증용 - 그룹 수정 시 작업 완료 메시지 팝업
				SC.say(I18N.message("operationcompleted"));
				
				window.destroy();
			}
		});
	}
	
	/**
	 * Tree Node 삭제
	 * @param groupId
	 */
	private void executeRemove(final TreeNode treeNode)	{
		final String groupId = treeNode.getAttributeAsString("id");
		final TreeNode parent = tree.getTree().getParent(treeNode);
		
		GWT.log("[ GroupTreePanel executeRemove ] groupId["+groupId+"]", null);
		if(groupId == null || groupId.length() < 1) return;
		
		ServiceUtil.security().deleteGroup(Session.get().getSid(), groupId, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(Void result) {
				GWT.log("[ GroupTreePanel executeRemove ] onSuccess. groupId["+groupId+"]", null);
				
				tree.onFolderServerRemoved();
				if (parent != null) {
					TreeNode[] nodes = tree.getTree().getChildren(parent);
					if (nodes == null || nodes.length < 1) {
						parent.setAttribute("expand", false);
					}
				}
				
				// kimsoeun GS인증용 - 그룹 삭제 시 작업 완료 메시지 팝업
				SC.say(I18N.message("operationcompleted"));
				
			}
		});
	}

	/**
	 * 트리 초기화(초기 트리로 변경)
	 * */
	public void reset(){
		tree.getTree().closeAll();
	}
	
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		if(recordObserver != null) {
			recordObserver.onRecordSelected(id, parentId);
		}
	}

	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onRecordDoubleClick(Record record) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onRecordSelected(Record record) {
		// TODO Auto-generated method stub		
	}
	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return false;
	}
}
