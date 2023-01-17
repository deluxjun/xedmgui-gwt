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
 * @author 박상기
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
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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
		
		/* Sub Title 생성 */
		currentTitle = subTitle;
		subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(subTitle);
        
        setSearch(true);
        
        //drag source grid instance 설정
        this.dragSourceGrid = dragSourceGrid;
        
        //grid 생성
        grid = new ListGrid();
        grid.setWidth100();
        
        // 팝업창에서 실행시킬 경우 사이즈 수정 및 admin menu의 동작 방지
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
        // kimsoeun GS인증용 - 멀티 세렉트 방지
        grid.setSelectionType(SelectionStyle.SINGLE);
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField userIdField = new ListGridField("username", I18N.message("userid"));
        ListGridField userNameField = new ListGridField("name", I18N.message("uusername"));
        //20130827 남윤성 위임에서 위임자 선택시 위임자의 그룹id 필요함
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
        // 검색바
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

        // 검색
        PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {  
				search(searchText.getValueAsString(), gridPager.getPageNum(), gridPager.getPageSize());
            }   
        });

        // 삭제 버튼
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
         * <addDropHandler 추가 이유> 
         * grid.setPreventDuplicates(true);로 설정하더라도 Source Grid에서의 Drag가 아닌
         * 로직에 의해 추가된 teamGrid의 데이타는 dup 체크를 하지 못함.
         * 
         * <addDragStartHandler, addDragStopHandler 추가 이유>
         * 자기자신의 Record drag & drop 처리
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
        
		//record 삭제 event handler 정의--------------------------------------------------------------
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
        	//Action 모드에 따른 Panel 생성
	        VLayout teamVL = new VLayout(5);
	        teamVL.setWidth100();
	        teamVL.setMembers(grid, createActHL());
	        teamGridPanel.setMembers(teamVL, createTeamIndexBtnsVL(isPopup));
        }
        else {
        	//조회모드로 Panel 생성
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
     	
     	 //20140205na 자동검색 기능 추가
        if(userName != null && !"".equals(userName)){
        	searchText.setValue(userName);
        	search(userName, 1, gridPager.getPageSize());
        }
	}

	/**
	 * 데이타 존재유무 반환
	 * @return
	 */
	public boolean isExistMember() {
		RecordList recordList = grid.getRecordList(); 
		return (recordList != null && !recordList.isEmpty());
	}

	/**
	 * Name에 의한 Team Member 조회
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
//				// 20130911, taesu, 검색 결과가 없을 경우 그리드 출력 메시지 변경
//				if(result == null){
//					grid.setEmptyMessage(I18N.message("notitemstoshow"));
//					return;	
//				}
//				for (int j = 0; j < result.size(); j++) {
//					ListGridRecord r=new ListGridRecord();
//					r.setAttribute("id", result.get(j).getId());
//					r.setAttribute("username", result.get(j).getUserName());
//					r.setAttribute("name",result.get(j).getName());
//					r.setAttribute("groupid",result.get(j).getGroups()[0].getId());//20130823 남윤성 위임에서 위임자 선택시 위임자의 그룹id 필요함
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
						//20140221na 그룹 여러개 보이기
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

	// 20131128, junsoo, 유저 검색
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
						//20140221na 그룹 여러개 보이기
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
	 * 선택된 그룹과 name으로 Team Member 조회. (Root 선택시 초기화 수행함) 
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
	 * Default 초기화
	 */
	private void reset() {
		grid.setData(new ListGridRecord[0]);
		
		// Popup 창에서 선택 가능하도록 수정
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
	 * Team 색인버튼 패널 생성
	 */
	private VLayout createTeamIndexBtnsVL(boolean isPopup) {
		btns = IndexBtn.getBtns();
		
		ClickHandler indexBtnsClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
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
    	
    	//20140318na en이면 보이지 않음.
        boolean isKorea = "ko".equals(Session.get().getUser().getLanguage());
        if(!isKorea) indexPanel.setVisible(false);
        else indexPanel.setVisible(isPopup);
    	
        return indexPanel;
	}
	
	/**
	 * Action Panel 생성
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
	 * 색인 버튼 초기화
	 */
	private void resetBtnState() {
		if(btns != null) {
			for(int i=0; i<btns.length; i++) {
	    		btns[i].setISelected((i==0));
	    	}	
		}
	}
	
	/**
	 * Source Grid로 부터 데이타를 복사해 받음
	 * @return
	 */
	protected void copyRecordsToMembers() {
		copyRecordsToMembers(dragSourceGrid);
	}
	
	/**
	 * Source Grid로 부터 데이타를 복사해 받음
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