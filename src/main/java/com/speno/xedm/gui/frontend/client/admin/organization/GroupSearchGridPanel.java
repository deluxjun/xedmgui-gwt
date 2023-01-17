package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * GroupSearchGrid Panel
 * 
 * @author ������
 * @since 1.0
 */
public class GroupSearchGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, GroupSearchGridPanel> instanceMap = new HashMap<String, GroupSearchGridPanel>();

	private ListGrid grid;
	private PagingToolStrip gridPager;

	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
	private String groupId = "";
	private VLayout indexPanel;
	private IndexBtn[] btns;
	
	private String id = "";
	
	private String searchText = "";
	private String currentName = "";
	private int type = 0;
	private final static int TYPE_SEARCH = 0;
	private final static int TYPE_EXECUTE = 1;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static GroupSearchGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, false, false, "100%");		
	}
	
	public ListGrid getGrid() {
		return grid;
	}

	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isAction
	 * @param width
	 * @return
	 */
	public static GroupSearchGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isPopup,
			boolean isAction, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new GroupSearchGridPanel(id, subTitle, dragSourceGrid, isPopup, isAction, width);
		}
		return instanceMap.get(id);
	} 
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isAction
	 * @param width
	 * @param userName
	 * @return
	 */	
	public static GroupSearchGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isPopup,
			boolean isAction, 
			final String width, 
			final String userName) {
		if (instanceMap.get(id) == null) {
			new GroupSearchGridPanel(id, subTitle, dragSourceGrid, isPopup, isAction, width, userName);
		}
		return instanceMap.get(id);
	}
	
	public GroupSearchGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, false, false, "100%");
	}
	
	public GroupSearchGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final boolean isPopup,
			final String width) {		
		this(id, subTitle, dragSourceGrid, isAction, isPopup, width, null);
	}
	
	public GroupSearchGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final boolean isPopup,
			final String width,
			final Object name){
		instanceMap.put(id, this);
		
		/* Sub Title ���� */
		Label subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(subTitle);
        
        //drag source grid instance ����
        this.dragSourceGrid = dragSourceGrid;
        
        //grid ����
        grid = new ListGrid();
        grid.setWidth100();
        
        // �˾�â���� �����ų ��� ������ ���� �� admin menu�� ���� ����
        if(isPopup){
        	grid.setHeight(360);
        	setWidth(250);
        	grid.setCanDragRecordsOut(true);
        	grid.setDragDataAction(DragDataAction.MOVE);
        }
        else{
        	grid.setHeight100();
        	grid.setCanReorderRecords(true);
        	grid.setCanDragRecordsOut(true);
        	grid.setCanAcceptDroppedRecords(true);
        	grid.setDragDataAction(DragDataAction.MOVE);
        	grid.setCanReorderFields(true);
        	grid.setCanRemoveRecords(true);        
        	grid.setPreventDuplicates(true);
        	grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        	setWidth(width);  

        }
        grid.setShowAllRecords(true);
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        // kimsoeun GS������ - ��Ƽ����Ʈ ����
        grid.setSelectionType(SelectionStyle.SINGLE);
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField userIdField = new ListGridField("name", I18N.message("group"));
        ListGridField userNameField = new ListGridField("path", I18N.message("path"));
        //20130827 ������ ���ӿ��� ������ ���ý� �������� �׷�id �ʿ���
        ListGridField groupIdField = new ListGridField("groupid", I18N.message("groupid"));
        ListGridField emailField = new ListGridField("email", I18N.message("email"));
        
        final DynamicForm dfTop = new DynamicForm();   
//		dfTop.setWidth(250);
        dfTop.setNumCols(2);
        dfTop.setAlign(Alignment.RIGHT);
        dfTop.setShowEdges(false);
        // =================================================
        // �˻���
        final TextItem searchText = new TextItem("userSearch", I18N.message("UserIdORUserName"));
        searchText.setWidth(230);
        searchText.setShowTitle(false);
        searchText.setWrapTitle(false);
        searchText.setCanEdit(true);
        searchText.setDisableIconsOnReadOnly(false);
//        searchText.setLength(Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255));
        searchText.setValidators(new LengthValidator(searchText, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
        searchText.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getKeyName().equals("Enter")){
					search(searchText.getValueAsString(), 1, gridPager.getPageSize());
				}
			}
		});

        // �˻�
        PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {  
				search(searchText.getValueAsString(), gridPager.getPageNum(), gridPager.getPageSize());
            }   
        });

        // ���� ��ư
        PickerIcon clearPicker = new PickerIcon(PickerIcon.REFRESH, new FormItemClickHandler() {   
        	public void onFormItemClick(FormItemIconClickEvent event) { 
            	 dfTop.getField("userSearch").clearValue();
            }   
        });
        
        searchText.setIcons(searchPicker, clearPicker);
        dfTop.setItems(searchText);
        dfTop.setVisible(isPopup);
        
        emailField.setHidden(true);
        groupIdField.setHidden(true);
        idField.setHidden(true);
        
        grid.setFields(idField, userIdField, userNameField, groupIdField, emailField);
        grid.setCanResizeFields(true);
        
        /* *******************************************************************************************
         * <addDropHandler �߰� ����> 
         * grid.setPreventDuplicates(true);�� �����ϴ��� Source Grid������ Drag�� �ƴ�
         * ������ ���� �߰��� teamGrid�� ����Ÿ�� dup üũ�� ���� ����.
         * 
         * <addDragStartHandler, addDragStopHandler �߰� ����>
         * �ڱ��ڽ��� Record drag & drop ó��
         * *******************************************************************************************/
        
        grid.addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				isDragByMe = true;
			}
        });        
        grid.addDragStopHandler(new DragStopHandler() {
			@Override
			public void onDragStop(DragStopEvent event) {
				isDragByMe = false;
			}
        });
        grid.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				if(!isDragByMe && GroupSearchGridPanel.this.dragSourceGrid != null) {
					copyRecordsToMembers(GroupSearchGridPanel.this.dragSourceGrid);
					event.cancel();	
				}
			}
        });
        
		//record ���� event handler ����--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
				event.cancel();
				SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							ListGridRecord record = grid.getRecord( event.getRowNum());
							removeRecordFromMembers(record);
						}
					}
				});
			}
		});

		if(dragSourceGrid != null){
	        dragSourceGrid.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					removeRecordsFromMembers();
					event.cancel();					
				}
	        });
        }
        
        HLayout teamGridPanel = new HLayout(0);
        
        if(isAction) {
        	//Action ��忡 ���� Panel ����
	        VLayout teamVL = new VLayout(5);
	        teamVL.setWidth100();
	        teamVL.setMembers(grid, createActHL());
	        teamGridPanel.setMembers(teamVL);
        }
        else {
        	//��ȸ���� Panel ����
	        teamGridPanel.setMembers(grid);
        }

        gridPager = new PagingToolStrip(grid, 20, true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);

        VLayout groupGridPagerPanel = new VLayout();
        groupGridPagerPanel.setHeight100();
//        userGridPagerPanel.setShowResizeBar(isShowAct);
        groupGridPagerPanel.setMembersMargin(1);
        groupGridPagerPanel.addMember(teamGridPanel); 
        groupGridPagerPanel.addMember(gridPager);
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, dfTop, groupGridPagerPanel);
        
//        disable();
//     	indexPanel.disable();
     	
     	 //20140205na �ڵ��˻� ��� �߰�
        if(name != null && !"".equals(name)){
        	searchText.setValue(name);
        	search(name.toString(), 1, gridPager.getPageSize());
        }
	}

	/**
	 * ����Ÿ �������� ��ȯ
	 * @return
	 */
	public boolean isExistMember() {
		RecordList recordList = grid.getRecordList(); 
		return (recordList != null && !recordList.isEmpty());
	}

	/**
	 * Name�� ���� Team Member ��ȸ
	 * @param name
	 */
	
	private void executeFetch(final String name, final int pageNum, final int pageSize)	{		
		Log.debug("[ GroupSearchGridPanel executeFetch ] name["+name+"] " + pageNum +"," + pageSize);
		currentName = name;
		searchText = "";
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		type = TYPE_EXECUTE;
		
		ServiceUtil.security().pagingGroupsById(name, CommSearchDialog.GROUP, config, new AsyncCallback<PagingResult<SGroup>>() {
			
			@Override
			public void onSuccess(PagingResult<SGroup> result) {
				// TODO Auto-generated method stub
				reset();

				if(result == null){
					grid.setEmptyMessage(I18N.message("notitemstoshow"));
					return;	
				}
				int totalLength = result.getTotalLength();
				
				SGroup group;
				List<SGroup> data = result.getData();
				for (int j = 0; j < data.size(); j++) {
					group =  data.get(j);
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", group.getId());
					record.setAttribute("name", group.getName());
					record.setAttribute("path", group.getPath());	
					grid.addData(record);					
				}	
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);				
				gridPager.setRespPageInfo(totalLength, pageNum);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				SCM.warn(caught);
			}
		});
	}

	// 20131128, junsoo, ���� �˻�
	private void search(String name, final int pageNum, final int pageSize)	{
		if (name == null)
			name = "";
		Log.debug("[ GroupSearchGridPanel search ] name ["+name+"], " + pageNum + "," + pageSize);
		searchText = name;
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		type = TYPE_SEARCH; 
		
		ServiceUtil.security().pagingGroupsByName(name, CommSearchDialog.GROUP, config, new AsyncCallback<PagingResult<SGroup>>() {
			
			@Override
			public void onSuccess(PagingResult<SGroup> result) {
				// TODO Auto-generated method stub
				reset();

				if(result == null){
					grid.setEmptyMessage(I18N.message("notitemstoshow"));
					return;	
				}
				int totalLength = result.getTotalLength();
				
				SGroup group;
				List<SGroup> data = result.getData();
				for (int j = 0; j < data.size(); j++) {
					group =  data.get(j);
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", group.getId());
					record.setAttribute("name", group.getName());
					record.setAttribute("path", group.getPath());
					grid.addData(record);					
				}
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);				
				gridPager.setRespPageInfo(totalLength, pageNum);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * ���õ� �׷�� name���� Team Member ��ȸ. (Root ���ý� �ʱ�ȭ ������) 
	 * @param id
	 * @param name
	 */
	public void executeFetch(final String id, final String name)	{
		Log.debug("[ TeamGridPanel executeFetch ] id["+id+"] name["+name+"]");
		this.groupId = id;
//		if(Constants.ADMIN_ROOT == id) {
		if(id.equals(Long.toString(Constants.ADMIN_ROOT))) {
			reset();
			return;
		}
//		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_GROUP_ROOT == id) {
//			reset();
//			return;
//		}
		
		resetBtnState();		
        executeFetch(id, 1, gridPager.getPageSize());
	}
	
	/**
	 * Default �ʱ�ȭ
	 */
	private void reset() {
		grid.setData(new ListGridRecord[0]);
		
		// Popup â���� ���� �����ϵ��� ����
		if(groupId.equals(Long.toString(Constants.ADMIN_ROOT))) {
	     	grid.setEmptyMessage(I18N.message("rootisnotallowedto"));
	     	disable();
//	     	indexPanel.disable();
	     }
		else {
			grid.setEmptyMessage(I18N.message("notitemstoshow"));
	     	enable();
//	     	indexPanel.enable();
		}	
//		if(Constants.ADMIN_ROOT == groupId || Constants.ADMIN_GROUP_ROOT == groupId) {
//	     	grid.setEmptyMessage(I18N.message("rootisnotallowedto"));
//	     	disable();
//	     	indexPanel.disable();
//	     }
//		else {
//			grid.setEmptyMessage(I18N.message("droprowshere"));
//	     	enable();
//	     	indexPanel.enable();
//		}	
	}
	
	/**
	 * Team ���ι�ư �г� ����
	 */
	private VLayout createTeamIndexBtnsVL(boolean isPopup) {
		btns = IndexBtn.getBtns();
		
		ClickHandler indexBtnsClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.setData(new ListGridRecord[0]); //�׸��� �ʱ�ȭ
				
				IndexBtn btn = (IndexBtn)event.getSource();				
				for(int i=0; i<btns.length; i++) {
		    		btns[i].setISelected(btn.getID().equals(btns[i].getID()));
		    	}				
				executeFetch(btn.getIValue(), 1, gridPager.getPageSize());
				
//				gridPager.goToPage(1);
			}
		};
		
		indexPanel = new VLayout(1);
		indexPanel.setAutoWidth();
		indexPanel.setAutoHeight();
    	for(int i=0; i<btns.length; i++) {
    		btns[i].setAddClickHandler(indexBtnsClickHandler);
    		indexPanel.addMember(btns[i]);
    	}
    	indexPanel.setVisible(isPopup);
        return indexPanel;
	}
	
	/**
	 * Action Panel ����
	 * @return HLayout
	 */
	private HLayout createActHL() {		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	// TODO Auto-generated method stub
//            	System.out.println("Save Button clicked");
            }   
        });
		
		HLayout actHL = new HLayout(10);
		actHL.setAutoWidth();
		actHL.setAutoHeight();
		actHL.setMembers(btnSave);		
		return actHL;
	}
	
	/**
	 * ���� ��ư �ʱ�ȭ
	 */
	private void resetBtnState() {
		if(btns != null) {
			for(int i=0; i<btns.length; i++) {
	    		btns[i].setISelected((i==0));
	    	}	
		}
	}
	
	/**
	 * Source Grid�� ���� ����Ÿ�� ������ ����
	 * @return
	 */
	protected void copyRecordsToMembers() {
		copyRecordsToMembers(dragSourceGrid);
	}
	
	/**
	 * Source Grid�� ���� ����Ÿ�� ������ ����
	 * @return
	 */
	private void copyRecordsToMembers(ListGrid dragSourceGrid) {
		
		RecordList recordList = grid.getRecordList();
		
		final List<ListGridRecord> temp = new ArrayList<ListGridRecord>();		
		final ListGridRecord[] senderRecords = dragSourceGrid.getSelectedRecords();
		for(int j=0; j<senderRecords.length; j++) {			
			if(recordList.find("id", senderRecords[j].getAttribute("id")) == null) {
				temp.add(senderRecords[j]);
			}
		}
		
		if(temp.size() <= 0) {
			if(temp.size() < senderRecords.length) {
				SC.warn(I18N.message("dupmessage"));
			}
			return;
		}
		
		String[] userIds =  new String[temp.size()];
		for(int k=0; k<temp.size(); k++) {
			userIds[k] = temp.get(k).getAttributeAsString("id");
		}
		
		GWT.log("[ GroupSearchGridPanel copyRecordsToMembers ] userIds.length["+userIds.length+"]", null);
				
		ServiceUtil.security().addUsersToGroup(Session.get().getSid(), groupId, userIds, new AsyncCallbackWithStatus<Void>() {
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
				for(int j=0; j<temp.size(); j++) {
					grid.addData((ListGridRecord)temp.get(j));
				}
				
				if(temp.size() < senderRecords.length) {
					SC.warn(I18N.message("dupmessage"));
				}
			}
		});
	}
	
	private void removeRecordFromMembers(final ListGridRecord record) {		
		GWT.log("[ GroupSearchGridPanel removeRecordFromMembers ] id["+record.getAttributeAsString("id")+"]", null);
				
		String[] userIds =  new String[1];
		userIds[0] = record.getAttributeAsString("id");
		
		ServiceUtil.security().removeFromGroup(Session.get().getSid(), groupId, userIds, new AsyncCallbackWithStatus<Void>() {
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
				grid.removeData(record);
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	protected void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		GWT.log("[ GroupSearchGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]", null);
		
		if(removeRecords.length <= 0) return;
				
		String[] userIds =  new String[removeRecords.length];
		for(int j=0; j<userIds.length; j++) {
			userIds[j] = removeRecords[j].getAttributeAsString("id");
		}
		
		ServiceUtil.security().removeFromGroup(Session.get().getSid(), groupId, userIds, new AsyncCallbackWithStatus<Void>() {
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
				for(int j=0; j<removeRecords.length; j++) {
					grid.removeData(removeRecords[j]);
				}
			}
		});
	}
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		if(type == TYPE_SEARCH)
			search(searchText, pageNum, pageSize);
		else if(type == TYPE_EXECUTE)
			executeFetch(currentName, pageNum, pageSize);
	}
}