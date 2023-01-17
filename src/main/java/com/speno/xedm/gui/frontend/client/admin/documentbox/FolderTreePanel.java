package com.speno.xedm.gui.frontend.client.admin.documentbox;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.Action;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Useful;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.folder.AdminFolderTree;

/**
 * Folder Tree Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class FolderTreePanel extends VLayout  implements RecordObserver  {	
	private static HashMap<String, FolderTreePanel> instanceMap = new HashMap<String, FolderTreePanel>();
	
	private AdminFolderTree tree;
	private DynamicForm form;
	private Window window;
	
	//상위 RecordObserver에게 Callback하기위해 RecordObserver를 맴버로도 가짐.
	private RecordObserver recordObserver;

	// 20140402, junsoo, context menu
	private Action action;

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param isCanDrag
	 * @param isShowRoot
	 * @param isShowAct
	 * @return
	 */
	public static FolderTreePanel get(
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
	public static FolderTreePanel get(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag, 
			final boolean isShowRoot, 
			final boolean isShowAct,
			final String width) {
		if (instanceMap.get(id) == null) {
			new FolderTreePanel(id, ob, isCanDrag, isShowRoot, isShowAct, width);
		}		
		return instanceMap.get(id);
	}
	
	public static FolderTreePanel get(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag, 
			final boolean isShowRoot, 
			final boolean isShowAct,
			final String width,
			final boolean isCanDragOut) {
		if (instanceMap.get(id) == null) {
			new FolderTreePanel(id, ob, isCanDrag, isShowRoot, isShowAct, width, isCanDragOut);
		}		
		return instanceMap.get(id);
	}
	
	/**
	 * Folder Tree Panel 생성
	 * @param id
	 * @param ob
	 * @param isCanDrag
	 * @param isShowRoot
	 * @param isShowAct
	 */
	public FolderTreePanel(
			final String id, 
			final RecordObserver ob, 
			final boolean isCanDrag,
			final boolean isShowRoot,
			final boolean isShowAct) {
		this(id, ob, isCanDrag, isShowRoot, isShowAct, "100%");
	}
	
	/**
	 * Folder Tree Panel 생성
	 * @param id
	 * @param ob
	 * @param isCanDrag
	 * @param isShowRoot
	 * @param isShowAct
	 * @param width
	 */
	public FolderTreePanel(
			final String id, 
			final RecordObserver ob,
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct,
			final String width) {
		this(id, ob, isCanDrag, isShowRoot, isShowAct, width, true);
	}
	
	public FolderTreePanel(
			final String id, 
			final RecordObserver ob,
			final boolean isCanDrag, 
			final boolean isShowRoot,
			final boolean isShowAct,
			final String width,
			final boolean isCanDragOut){
		instanceMap.put(id, this);
		
		this.recordObserver = ob;
		tree = new AdminFolderTree(this);
		tree.setWidth100();
        tree.setBorder("1px solid gray");
        //tree.getDataSource().setCacheAllData(true);
        //tree.getDataSource().setAutoCacheAllData(true);
        
        if(isCanDrag) {
            //tree의 drag 환경 설정
	        tree.setDragDataAction(DragDataAction.COPY);
	        tree.setCanReorderRecords(false);
	        tree.setCanAcceptDroppedRecords(true);
	        tree.setCanDragRecordsOut(isCanDragOut);        
	        tree.setCanRemoveRecords(false);
	        tree.setShowHeader(true);
	        
			// 20130910, junsoo, drop 가능여부 세팅
	        tree.setCanAcceptDroppedRecords(true);
	        tree.setCanAcceptDrop(true);

	        tree.addDragStartHandler(new DragStartHandler() {
				@Override
				public void onDragStart(DragStartEvent ev) {
					if (!(EventHandler.getDragTarget() instanceof AdminFolderTree)){
						ev.cancel();
						return;
					}
					
					TreeNode node = getSelectedTreeNode();
					// root 는 무시
					if (String.valueOf(Constants.ADMIN_FOLDER_ROOT).equals(node.getAttributeAsString("id"))) {
						ev.cancel();
						return;
					}
				}
			});
			
	        tree.addDropHandler(new DropHandler() {
				public void onDrop(final DropEvent event) {
					try {
						// event 취소하고 UI 동기화는 reload를 통해서 함.
						event.cancel();

						ListGrid list = null;
						if (EventHandler.getDragTarget() instanceof AdminFolderTree) {

							final AdminFolderTree folderTree = ((AdminFolderTree)EventHandler.getDragTarget());
							
							final long source = Long.parseLong(folderTree.getDragData()[0].getAttributeAsString("id"));
							final long target = Long.parseLong(folderTree.getDropFolder().getAttributeAsString("id"));
							final long parentSource = Long.parseLong(folderTree.getDragData()[0].getAttributeAsString("parentId"));

							final String sourceName = folderTree.getDragData()[0].getAttributeAsString("name");
							final String targetName = folderTree.getDropFolder().getAttributeAsString("name");
							
							//20131213na source의 parentSource로 이동 불가
							if(target == parentSource){
								SC.warn(I18N.message("error.folder.cannotMoveInsideSameFolder"));
								return;
							}
							if(target > 0){ 
							Useful.askYesNoCancel(I18N.message("move"), I18N.message("moveaskClearYesOrNot", new String[] { sourceName, targetName }), Alignment.LEFT,
									new BooleanCallback() {

										@Override
										public void execute(Boolean value) {
											if (value == null)
												return;
											
//											if (value) {
												ServiceUtil.folder().move(Session.get().getSid(), source, target, !value, 
														new AsyncCallback<Void>() {
															@Override
															public void onFailure(Throwable caught) {
																Log.serverError(caught, true);
															}

															@Override
															public void onSuccess(Void ret) {
																// source, target 모두 갱신
																// yuk 20140304	공백에다가 던질시에 못하게 막아버림
																TreeNode node = folderTree.getTree().find("id", source);
																if (node != null) {
																	node.setAttribute("expand", true);		
																	folderTree.refreshNode(node);
																	folderTree.getFolderDataRpcChild(node, true);
																}
																node = folderTree.getTree().find("id", target);
																if (node != null) {
																	node.setAttribute("expand", true);		
																	folderTree.refreshNode(node);
																	folderTree.getFolderDataRpcChild(node, true);
																}
																redraw();

																Log.infoWithPopup(I18N.message("move"), I18N.message("second.client.successfully"));

															}
														});
//											}

										}
									});
						}
					}} catch (Throwable e) {
					}
				}
			});

        }
        
        if(isShowAct) {
        	//Action 모드에 따른 Left Top Action Panel 생성
        	addMember(createLeftTopHL());
        }
        
        // context menu
		// 20140325, junsoo, DocumentActionUtil 이 너무 복잡하니, 이제 가급적 단독으로 하자!
		action = new Action();
		
		// 서버의 ecm.viewer.url 세팅에 따라서 보기메뉴 표시
		action.createAction("add", "add", "add", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				openAddEditWin("addfolder", getSelectedTreeNode());
			}
		}, true, false);
		
		action.createAction("edit", "edit", "edit", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				openAddEditWin("editfolder", getSelectedTreeNode());
			}
		}, true, false);

		action.createAction("delete", "delete", "delete", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
        		removeCtrol(getSelectedTreeNode());
			}
		}, true, false);
		
        tree.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(final CellContextClickEvent event) {
				action.enable("add", true, "");
				action.enable("edit", true, "");
				action.enable("delete", true, "");

				action.getContextMenu().showContextMenu();

				if (event != null)
					event.cancel();
			}
        });
		
        setMembersMargin(10);
        setHeight100();
        
        addMember(tree);
        setWidth(width);
        
        //폴더 데이타 조회
        tree.getFolderDataRpc(Constants.ADMIN_FOLDER_ROOT, isShowRoot);
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
	
	public void setSecurityProfileId(SFolder folder) {
		//20140426na 
		//tree.getRecordList를 사용하면 트리의 선택 표시가 안되는 문제가 발생함.
		// 따라서 트리를 직접 찾아 수정하는 방식으로 바꿈
//		Record record = tree.getRecordList().find("id", folder.getId());
//		record.setAttribute("profileId", folder.getSecurityProfileId());
//		tree.refreshRow(tree.getRecordIndex(record));			
		
		Tree treeNode = tree.getTree();
		TreeNode node = treeNode.findById(Long.toString(folder.getId()));
		node.setAttribute("profileId", folder.getSecurityProfileId());
	}
	
	/**
	 * Left Top Action Panel 생성
	 * @return
	 */
	protected HLayout createLeftTopHL() {
		Button addButton = new Button(I18N.message("add"));
		addButton.setWidth(80);   
		addButton.setIcon("[SKIN]/actions/add.png");
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				openAddEditWin("addfolder", getSelectedTreeNode());
			}
		});

		Button editButton = new Button(I18N.message("edit"));
		editButton.setWidth(80);
		editButton.setIcon("[SKIN]/actions/edit.png");
		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				openAddEditWin(I18N.message("editfolder"), getSelectedTreeNode());
			}
		});
       
        Button deleteButton = new Button(I18N.message("delete"));
        deleteButton.setWidth(80);
        deleteButton.setIcon("[SKIN]/actions/remove.png");
        deleteButton.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		removeCtrol(getSelectedTreeNode());
        		
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
			SC.say(I18N.message("choosefolderfromtree"));
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
		
		final boolean isAdd = "addfolder".equals(divStr);
		
		if( !isAdd && String.valueOf(Constants.ADMIN_FOLDER_ROOT).equals(treeNode.getAttributeAsString("id"))) {
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
        nameItem.setValue("addfolder".equals(divStr) ? "" : treeNode.getAttribute("name"));
        nameItem.setRequired(true);
        // kimsoeun GS인증용 - 툴팁 다국어화
        nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
//        nameItem.setLength(Constants.MAX_LEN_NAME);
        nameItem.setValidators(new LengthValidator(nameItem, Constants.MAX_LEN_NAME));
        
        final  TextItem descItem = new TextItem();   
        descItem.setTitle(I18N.message("description"));
        descItem.setValue("addfolder".equals(divStr) ? "" : treeNode.getAttribute("description"));
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
                		executeAdd(nameItem.getValueAsString(), descItem.getValueAsString(), treeNode);
                	}
                	else {
                		executeUpdate(nameItem.getValueAsString(), descItem.getValueAsString(), treeNode);
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
	 * 선택된 Tree Node 삭제
	 * @param treeNode
	 */
	private void removeCtrol(final TreeNode treeNode) {
		if(treeNode == null) return;
		
		if("0".equals(treeNode.getAttributeAsString("id"))) {
			SC.say(I18N.message("rootcannotdeleted"));
			return;
		}
		
//		boolean isExpand = Boolean.valueOf(treeNode.getAttributeAsString("expand"));
//		if(isExpand) {
//			SC.say(I18N.message("existnodecannotdeleted"));
//			return;
//		}
		
		//20131216na 2번 확인하는 절차 생략
		executeRemove(Long.parseLong(treeNode.getAttributeAsString("id")));
		
//		SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
//			@Override
//			public void execute(Boolean value) {
//				if(value != null && value) {
//					executeRemove(Long.parseLong(treeNode.getAttributeAsString("id")));
//				}
//				else {
//					return;
//				}
//			}
//		});
	}
	
	/**
	 * Tree Node 생성
	 * @param name
	 * @param description
	 * @param treeNode
	 */
	private void executeAdd(final String name, final String description, final TreeNode treeNode) {		
		long parentId = Long.parseLong(treeNode.getAttributeAsString("id"));
		
		GWT.log("[ FolderTreePanel executeAdd ] parentId["+parentId+"], name["+name+"], description["+description+"]", null);
		
		Session ses = Session.get();				
		if(ses == null) {
			SC.say(I18N.message("invalidsession"));
			return;
		}		
		
		SFolder folder = new SFolder();
		folder.setId(0L);
		folder.setName(name);
		folder.setDescription(description);
		folder.setParentId(parentId);		
		folder.setCreatorId(ses.getUser().getId());
		folder.setType(SFolder.TYPE_SHARED);
		// 20140204, junsoo, 폴더 생성은 상속으로
		folder.setSecurityProfileId(SSecurityProfile.PROFILE_INHERITEDACL);
		
		ServiceUtil.folder().save(Session.get().getSid(), folder, new AsyncCallbackWithStatus<SFolder>() {
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
			public void onSuccessEvent(SFolder result) {
				GWT.log("[ FolderTreePanel executeAdd ] onSuccess. id["+result.getId()+"]", null);				
				tree.onSubFolderServerAdded();
				
				// kimsoeun GS인증용 - 폴더 생성 시 작업 완료 메시지 팝업
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
		final long id = Long.parseLong(treeNode.getAttributeAsString("id"));
		final long parentId = Long.parseLong(treeNode.getAttributeAsString("parentId"));
		final long profileId = Long.parseLong(treeNode.getAttributeAsString("profileId"));
		final boolean parentOrNot = Boolean.valueOf(treeNode.getAttributeAsString("parentOrNot"));
		
		GWT.log("[ FolderTreePanel executeUpdate ] id["+id+"], parentId["+parentId+"], name["+name+"], description["+description+"]", null);
		
		SFolder folder = new SFolder();
		folder.setId(id);
		folder.setName(name);
		folder.setDescription(description);
		folder.setParentId(parentId);
		folder.setType(SFolder.TYPE_SHARED);
		folder.setSecurityProfileId(profileId);
		folder.setParentOrNot(parentOrNot);
		
		treeNode.setAttribute("name", name);
		treeNode.setAttribute("description", description);
		
		ServiceUtil.folder().save(Session.get().getSid(), folder, new AsyncCallbackWithStatus<SFolder>() {
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
			public void onSuccessEvent(SFolder result) {
				GWT.log("[ FolderTreePanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);				
				tree.refreshRow(tree.getRecordIndex(treeNode));
				
				// kimsoeun GS인증용 - 폴더 수정 시 작업 완료 메시지 팝업
				SC.say(I18N.message("operationcompleted"));
				
				window.destroy();
			}
		});
	}
	
	/**
	 * Tree Node 삭제
	 * @param folderId
	 */
	protected void executeRemove(final long folderId)	{
		SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					GWT.log("[ FolderTreePanel executeRemove ] folderId["+folderId+"]", null);
					if(folderId < 0) return;
					
					ServiceUtil.folder().delete(Session.get().getSid(), folderId, new AsyncCallbackWithStatus<Void>() {
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
							GWT.log("[ FolderTreePanel executeRemove ] onSuccess. folderId["+folderId+"]", null);
							tree.onFolderServerRemoved();
							
							// kimsoeun GS인증용 - 폴더 삭제 시 작업 완료 메시지 팝업
							SC.say(I18N.message("operationcompleted"));
						}
					});
				}
			}
		});
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
		if(recordObserver != null) {
			recordObserver.onRecordSelected(record);
		}		
	}
	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}