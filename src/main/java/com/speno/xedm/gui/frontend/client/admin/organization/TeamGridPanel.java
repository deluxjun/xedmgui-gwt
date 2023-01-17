package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SUser;
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
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * TeamGrid Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class TeamGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, TeamGridPanel> instanceMap = new HashMap<String, TeamGridPanel>();

	private ListGrid grid;
	private PagingToolStrip gridPager;

	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
	private String groupId = "";
	private VLayout indexPanel;
	private IndexBtn[] btns;
	
	private Label subTitleLable;
	private String currentTitle;
	private boolean isSearch = true;
	
	private String id = "";
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static TeamGridPanel get(
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
	public static TeamGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isPopup,
			boolean isAction, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new TeamGridPanel(id, subTitle, dragSourceGrid, isPopup, isAction, width);
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
	public static TeamGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isPopup,
			boolean isAction, 
			final String width, 
			final String userName) {
		if (instanceMap.get(id) == null) {
			new TeamGridPanel(id, subTitle, dragSourceGrid, isPopup, isAction, width, userName);
		}
		return instanceMap.get(id);
	}
	
	public TeamGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, false, false, "100%");
	}
	
	public TeamGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final boolean isPopup,
			final String width) {		
		this(id, subTitle, dragSourceGrid, isAction, isPopup, width, null);
	}
	
	public TeamGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final boolean isPopup,
			final String width,
			final String userName){
		instanceMap.put(id, this);
		
		/* Sub Title ���� */
		currentTitle = subTitle;
		subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(subTitle);
        
        setSearch(true);
        
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
        	
        	grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
				@Override
				public void onCellDoubleClick(CellDoubleClickEvent event) {
//					event.cancel();
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {
								removeRecordsFromMembers();
							}
						}
					});
				}
			});

        }
        grid.setShowAllRecords(true);
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        // kimsoeun GS������ - ��Ƽ ����Ʈ ����
        grid.setSelectionType(SelectionStyle.SINGLE);
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField userIdField = new ListGridField("username", I18N.message("userid"));
        ListGridField userNameField = new ListGridField("name", I18N.message("uusername"));
        //20130827 ������ ���ӿ��� ������ ���ý� �������� �׷�id �ʿ���
        ListGridField groupIdField = new ListGridField("groupid", I18N.message("groupid"));
        ListGridField departmentField = new ListGridField("department", I18N.message("department"));
        ListGridField emailField = new ListGridField("email", I18N.message("email"));
        
        final DynamicForm dfTop = new DynamicForm();   
//		dfTop.setWidth(250);
        dfTop.setNumCols(2);
        dfTop.setAlign(Alignment.RIGHT);
        dfTop.setShowEdges(false);
        dfTop.setAutoFocus(true);	// 20140218, junsoo, auto focusing
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
//        PickerIcon clearPicker = new PickerIcon(PickerIcon.REFRESH, new FormItemClickHandler() {   
//        	public void onFormItemClick(FormItemIconClickEvent event) { 
//            	 dfTop.getField("userSearch").clearValue();
//            }   
//        });
        
        searchText.setIcons(searchPicker);
        dfTop.setItems(searchText);
        dfTop.setVisible(isPopup);
        
        emailField.setHidden(true);
        groupIdField.setHidden(true);
        idField.setHidden(true);
//        departmentField.setHidden(!isPopup);
        
        grid.setFields(idField, userIdField, userNameField, groupIdField, emailField, departmentField);
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
				if(!isDragByMe && TeamGridPanel.this.dragSourceGrid != null) {
					copyRecordsToMembers(TeamGridPanel.this.dragSourceGrid);
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
	        teamGridPanel.setMembers(teamVL, createTeamIndexBtnsVL(isPopup));
        }
        else {
        	//��ȸ���� Panel ����
	        teamGridPanel.setMembers(grid, createTeamIndexBtnsVL(isPopup));
        }

        gridPager = new PagingToolStrip(grid, 20, true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);

        VLayout userGridPagerPanel = new VLayout();
        userGridPagerPanel.setHeight100();
//        userGridPagerPanel.setShowResizeBar(isShowAct);
        userGridPagerPanel.setMembersMargin(1);
        userGridPagerPanel.addMember(teamGridPanel); 
        userGridPagerPanel.addMember(gridPager);
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, dfTop, userGridPagerPanel);
        
//        disable();
//     	indexPanel.disable();
     	
     	 //20140205na �ڵ��˻� ��� �߰�
        if(userName != null && !"".equals(userName)){
        	searchText.setValue(userName);
        	search(userName, 1, gridPager.getPageSize());
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
//	private void executeFetch(final String name)	{				
//		Log.debug("[ TeamGridPanel executeFetch ] name["+name+"]");		
//		ServiceUtil.security().listUsersByNameAndGroupId(Session.get().getSid(), name, groupId, new AsyncCallbackWithStatus<List<SUser>>() {
//			@Override
//			public String getSuccessMessage() {
//				return I18N.message("client.searchComplete");
//			}
//			@Override
//			public String getProcessMessage() {
//				return I18N.message("client.searchRequest");
//			}
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				SCM.warn(caught);
//			}
//			@Override
//			public void onSuccessEvent(List<SUser> result) {
//				reset();
//				// 20130911, taesu, �˻� ����� ���� ��� �׸��� ��� �޽��� ����
//				if(result == null){
//					grid.setEmptyMessage(I18N.message("notitemstoshow"));
//					return;	
//				}
//				for (int j = 0; j < result.size(); j++) {
//					ListGridRecord r=new ListGridRecord();
//					r.setAttribute("id", result.get(j).getId());
//					r.setAttribute("username", result.get(j).getUserName());
//					r.setAttribute("name",result.get(j).getName());
//					r.setAttribute("groupid",result.get(j).getGroups()[0].getId());//20130823 ������ ���ӿ��� ������ ���ý� �������� �׷�id �ʿ���
//					r.setAttribute("email",result.get(j).getEmail());
//					grid.addData(r);
//				}
//			}
//		});
//	}
	
	private void executeFetch(final String name, final int pageNum, final int pageSize)	{		
		Log.debug("[ TeamGridPanel executeFetch ] name["+name+"] " + pageNum +"," + pageSize);
		currentName = name;
		searchText = "";
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		
		ServiceUtil.security().pagingUsersInGroupId(Session.get().getSid(), name, groupId, config, new AsyncCallbackWithStatus<PagingResult<SUser>>() {
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
			public void onSuccessEvent(PagingResult<SUser> result) {
				reset();
				
				if(result == null){
					grid.setEmptyMessage(I18N.message("notitemstoshow"));
					return;	
				}
				
				int totalLength = result.getTotalLength();
				
				SUser user;			
				SGroup duty;
				SGroup position;
				
				List<SUser> data = result.getData();					
				for (int j = 0; j < data.size(); j++) {
					user = data.get(j);			
					duty = user.getDuty();
					position = user.getPosition();
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", user.getId());
					record.setAttribute("name", user.getName());
					record.setAttribute("username", user.getUserName());
					if (user.getUserGroup() != null && user.getGroupLength() != 0){
						record.setAttribute("groupid", user.getUserGroup().getId());
						record.setAttribute("departmentid", user.getDepartmentId());
						SGroup[] groups =  user.getGroups();
						if(groups.length > 1){
						//20140221na �׷� ������ ���̱�
							String departments = groups[0].getName() ;
							for (int i = 1; i < groups.length; i++) {
								departments = departments + ", " + groups[i].getName() ; 
							}
							record.setAttribute("department", departments);
						}
						else record.setAttribute("department", user.getDepartment());
					}
					record.setAttribute("email", user.getEmail());
					record.setAttribute("description", user.getDescription());
					record.setAttribute("creationdate", user.getCreationDate());
					record.setAttribute("dutyid", (duty != null) ? duty.getId() : null);
					record.setAttribute("dutyname", (duty != null) ? duty.getName() : null);
					record.setAttribute("positionid", (position != null) ? position.getId() : null);
					record.setAttribute("positionname", (position != null) ? position.getName() : null);					
					grid.addData(record);			
				}	
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);	
				gridPager.setRespPageInfo(totalLength, pageNum);
				setSearch(false);
			}
		});
	}

	// 20131128, junsoo, ���� �˻�
	private void search(String idOrName, final int pageNum, final int pageSize)	{
		if (idOrName == null)
			idOrName = "";
		Log.debug("[ TeamGridPanel search ] idOrName ["+idOrName+"], " + pageNum + "," + pageSize);
		searchText = idOrName;
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		ServiceUtil.security().pagingUsersByIdOrName(idOrName, config, new AsyncCallbackWithStatus<PagingResult<SUser>>() {
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
			public void onSuccessEvent(PagingResult<SUser> result) {
				reset();

				if(result == null){
					grid.setEmptyMessage(I18N.message("notitemstoshow"));
					return;	
				}
				int totalLength = result.getTotalLength();
				
				SUser user;			
				SGroup duty;
				SGroup position;
				
				List<SUser> data = result.getData();
				for (int j = 0; j < data.size(); j++) {
					
					user = data.get(j);			
					duty = user.getDuty();
					position = user.getPosition();
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", user.getId());
					record.setAttribute("name", user.getName());
					record.setAttribute("username", user.getUserName());
					
					if (user.getUserGroup() != null && user.getGroupLength() != 0) {
						record.setAttribute("groupid", user.getUserGroup().getId());
						record.setAttribute("departmentid", user.getDepartmentId());
						SGroup[] groups =  user.getGroups();
						if(groups.length > 1){
						//20140221na �׷� ������ ���̱�
							String departments = groups[0].getName() ;
							for (int i = 1; i < groups.length; i++) {
								departments = departments + ", " + groups[i].getName() ; 
							}
							record.setAttribute("department", departments);
						}
						else record.setAttribute("department", user.getDepartment());
					}
					record.setAttribute("groupname", user.getUserGroupName());
					record.setAttribute("email", user.getEmail());
					record.setAttribute("description", user.getDescription());
					record.setAttribute("creationdate", user.getCreationDate());
					record.setAttribute("dutyid", (duty != null) ? duty.getId() : null);
					record.setAttribute("dutyname", (duty != null) ? duty.getName() : null);
					record.setAttribute("positionid", (position != null) ? position.getId() : null);
					record.setAttribute("positionname", (position != null) ? position.getName() : null);					
					grid.addData(record);					
				}	
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);				
				gridPager.setRespPageInfo(totalLength, pageNum);
				setSearch(true);
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
        executeFetch(name, 1, gridPager.getPageSize());
	}
	
	/**
	 * Default �ʱ�ȭ
	 */
	private void reset() {
		grid.setData(new ListGridRecord[0]);
		
		// Popup â���� ���� �����ϵ��� ����
		if(groupId.equals(Long.toString(Constants.ADMIN_ROOT))) {
	     	grid.setEmptyMessage(I18N.message("rootisnotallowedto"));
//	     	disable();
//	     	indexPanel.disable();
	     }
		else {
			grid.setEmptyMessage(I18N.message("notitemstoshow"));
	     	enable();
	     	indexPanel.enable();
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
				if (isSearch())
					search(btn.getIValue(), 1, gridPager.getPageSize());
				else
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
    	
    	//20140318na en�̸� ������ ����.
        boolean isKorea = "ko".equals(Session.get().getUser().getLanguage());
        if(!isKorea) indexPanel.setVisible(false);
        else indexPanel.setVisible(isPopup);
    	
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
		
		GWT.log("[ TeamGridPanel copyRecordsToMembers ] userIds.length["+userIds.length+"]", null);
				
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
		GWT.log("[ TeamGridPanel removeRecordFromMembers ] id["+record.getAttributeAsString("id")+"]", null);
				
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
	public void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		GWT.log("[ TeamGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]", null);
		
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
	
	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
		if (isSearch) {
			subTitleLable.setContents(currentTitle + " (" + I18N.message("all") + ")");
		} else {
			subTitleLable.setContents(currentTitle + " (" + I18N.message("searchgroup") + ")");
		}
	}

	private String searchText = "";
	private String currentName = "";
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		if (searchText != null && searchText.length() > 0)
			search(searchText, pageNum, pageSize);
		else
			executeFetch(currentName, pageNum, pageSize);
	}
}