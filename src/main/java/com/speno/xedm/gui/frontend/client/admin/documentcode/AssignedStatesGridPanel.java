package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SState;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.LifeCycleService;
import com.speno.xedm.gwt.service.LifeCycleServiceAsync;

/**
 * AssignedStatesGrid Panel
 * 
 * @author ������
 * @since 1.0
 */
public class AssignedStatesGridPanel extends VLayout {	
	private static HashMap<String, AssignedStatesGridPanel> instanceMap = new HashMap<String, AssignedStatesGridPanel>();
	LifeCycleServiceAsync service = (LifeCycleServiceAsync) GWT.create(LifeCycleService.class);
	
	private ListGrid grid;
	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
//	private long groupId = Constants.INVALID_LONG;
	private long profileId  = 0L;
	private  List<ListGridRecord> temp;
	private boolean stateDefault = true;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static AssignedStatesGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, false, "100%");		
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
	public static AssignedStatesGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isAction, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new AssignedStatesGridPanel(id, subTitle, dragSourceGrid, isAction, width);
		}
		return instanceMap.get(id);
	} 
	
	public AssignedStatesGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, false, "100%");
	}
	
	public AssignedStatesGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final String width) {		
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
        grid.setHeight100();
        grid.setShowAllRecords(true);
        
        grid.setCanReorderRecords(true);
        grid.setCanDragRecordsOut(true);
        grid.setCanAcceptDroppedRecords(true);
        grid.setDragDataAction(DragDataAction.MOVE);
        
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        grid.setCanReorderFields(true);
        grid.setCanRemoveRecords(true);        
        grid.setPreventDuplicates(true);
        grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField namdField = new ListGridField("name", I18N.message("name"));
        ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
        ListGridField conditionField = new ListGridField("condition", I18N.message("second.condition"));
        ListGridField targeteField = new ListGridField("target", I18N.message("target"));
        ListGridField targetPathField = new ListGridField("path", I18N.message("target"));
        
        idField.setHidden(true);
        targeteField.setHidden(true);
                
        grid.setFields(idField, namdField, descriptionField, conditionField, targeteField, targetPathField);
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
				if(!isDragByMe && AssignedStatesGridPanel.this.dragSourceGrid != null) {
					copyRecordsToMembers(AssignedStatesGridPanel.this.dragSourceGrid);
					event.cancel();	
				}
			}
        });
        
		//record ���� event handler ����--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
				SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							final ListGridRecord record = grid.getRecord( event.getRowNum());
							removeRecordFromMembers(record);
						}
					}
				});
//				event.cancel();
			}
		});   
        
        dragSourceGrid.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				removeRecordsFromMembers();
				event.cancel();					
			}
        });
        
        HLayout teamGridPanel = new HLayout(0);
        
        if(isAction) {
        	//Action ��忡 ���� Panel ����
	        VLayout teamVL = new VLayout(5);
	        teamVL.setWidth100();
//	        teamVL.setMembers(grid, createActHL());
	        teamVL.setMembers(grid);
	        teamGridPanel.setMembers(teamVL);//createTeamIndexBtnsVL()����
        }
        else {
        	//��ȸ���� Panel ����
	        teamGridPanel.setMembers(grid);//createTeamIndexBtnsVL()����
        }
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, teamGridPanel);
        if(isAction) {
        	setHeight(width);
            setWidth100();
        }else{
        	setHeight100();
            setWidth(width);
        } 
        
        disable();
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
	 * �׸��� ����
	 */
	protected void setGrid(Record record) {
		Log.debug("[ AssignedStatesGridPanel setGrid ]"+ record.getAttributeAsString("id") + record.getAttributeAsString("description"));
		
		
		profileId = Long.parseLong(record.getAttributeAsString("id"));
		service.getAssignedStates(Session.get().getSid(), profileId, new AsyncCallbackWithStatus<SState[]>() {
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
			public void onSuccessEvent(SState[] result) {
				reset();
				grid.setData(new ListGridRecord[0]); 
				for (int j = 0; j < result.length; j++) {
					ListGridRecord r=new ListGridRecord();
					r.setAttribute("id", result[j].getId());
					r.setAttribute("name", result[j].getName());
					r.setAttribute("description", result[j].getDescription());
					r.setAttribute("condition", result[j].getCondition());
					String targets = "";
					String paths = "";
					if(result[j].getTarget() != null){
						for(int i= 0; i<result[j].getTarget().length; i++){
							targets += "," +result[j].getTarget()[i];
							paths += "," +result[j].getPath()[i];
						}
					}
					r.setAttribute("target","".equals(targets)?"":targets.substring(1));
					
					r.setAttribute("path","".equals(paths)?"":paths.substring(1));
					grid.addData(r);
				}
				setStatesGrid();
			}
		});
	}
	
	/**
	 * States ���� Assigned States ���ڵ� ����
	 * @return
	 */
	private void setStatesGrid() {
		if(stateDefault){
			temp = new ArrayList<ListGridRecord>();	
			final ListGridRecord[] senderRecords = dragSourceGrid.getRecords();
			for(int j=0; j<senderRecords.length; j++) {
				temp.add(senderRecords[j]);
			}
		}
		stateDefault = false;
		
		//states ����Ÿ �ʱ�ȭ
		dragSourceGrid.setData(new ListGridRecord[0]); 
		for(int i=0; i<temp.size(); i++){
			dragSourceGrid.addData((ListGridRecord)temp.get(i));
		}
		
		ListGridRecord[] gridRecords = grid.getRecords();
		
		for(int k=0; k<gridRecords.length; k++){
			//states ������ ����
			RecordList rclist  = dragSourceGrid.getDataAsRecordList();
			Record rc = rclist.find("id", String.valueOf(gridRecords[k].getAttribute("id")));
			if(rc != null){
				dragSourceGrid.removeData(rc);
			}
		}
	}
	
	/**
	 * Default �ʱ�ȭ
	 */
	private void reset() {
		grid.setData(new ListGridRecord[0]);
//		if(Constants.ADMIN_ROOT == groupId || Constants.ADMIN_GROUP_ROOT == groupId) {
//	     	grid.setEmptyMessage(I18N.message("rootisnotallowedto"));
//	     	disable();
//	     }
//		else {
			grid.setEmptyMessage(I18N.message("droprowshere"));
	     	enable();
//		}	
	}
	
	/**
	 * Action Panel ����
	 * @return HLayout
	 */
//	private HLayout createActHL() {		
//		Button btnSave = new Button(I18N.message("save"));
//		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
//		btnSave.addClickHandler(new ClickHandler() {   
//            public void onClick(ClickEvent event) {
//            	System.out.println("Save Button clicked");
//            }   
//        });
//		
//		HLayout actHL = new HLayout(10);
//		actHL.setAutoWidth();
//		actHL.setAutoHeight();
//		actHL.setMembers(btnSave);		
//		return actHL;
//	}
		
	/**
	 * Source Grid�� ���� ����Ÿ�� ������ ���� (��ư Ŭ����)
	 * @return
	 */
	protected void copyRecordsToMembers() {
		copyRecordsToMembers(dragSourceGrid);
	}
	
	/**
	 * Source Grid�� ���� ����Ÿ�� ������ ���� (�巡�׽�)
	 * @return
	 */
	private void copyRecordsToMembers(ListGrid dragSourceGrid) {
		
		RecordList recordList = grid.getRecordList();
		
		final List<ListGridRecord> temp = new ArrayList<ListGridRecord>();		
		final ListGridRecord[] senderRecords = dragSourceGrid.getSelectedRecords();
		boolean isTarget = true;
		for(int j=0; j<senderRecords.length; j++) {	
			if((senderRecords[j].getAttribute("target") == null || senderRecords[j].getAttribute("target").equals(""))){
				isTarget = false;
				continue;
				}
			if(recordList.find("id", senderRecords[j].getAttribute("id")) == null) {
				temp.add(senderRecords[j]);
			}
		}
		if(!isTarget){
			SC.warn(I18N.message("second.notargetmessage"));
			return;
		}
					
		long[] states =  new long[temp.size()];
		for(int k=0; k<temp.size(); k++) {
			states[k] = Long.parseLong(temp.get(k).getAttributeAsString("id"));
		}
		
		Log.debug("[ AssignedStatesGridPanel copyRecordsToMembers ] userIds.length["+states.length+"]");
				
		service.addStates(Session.get().getSid(), profileId, states, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
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
				setStatesGrid();
			}
		});
	}
	
	/**
	 * ����
	 * @return
	 */
	private void removeRecordFromMembers(final ListGridRecord record) {		
		Log.debug("[ AssignedStatesGridPanel removeRecordFromMembers ] id["+record.getAttributeAsString("id")+"]");
				
		
		SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					long[] stateid =  new long[1];
					
					stateid[0] = Long.parseLong(record.getAttributeAsString("id"));
					service.removeStates(Session.get().getSid(), profileId, stateid, new AsyncCallbackWithStatus<Void>() {
						@Override
						public String getSuccessMessage() {
							return I18N.message("operationcompleted");
						}
						@Override
						public String getProcessMessage() {
							return null;
						}
						@Override
						public void onFailureEvent(Throwable caught) {
							SCM.warn(caught);
						}			
						@Override
						public void onSuccessEvent(Void result) {
//							grid.removeData(record);
							setStatesGrid();
						}
					});
				}
			}
		});
	}
	
	/**
	 *ȭ��ǥŬ���� �Ǵ� �巡��
	 * @return
	 */
	protected void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		Log.debug("[ AssignedStatesGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]");
		
		if(removeRecords.length <= 0) return;
				
		long[] states =  new long[removeRecords.length];
		for(int j=0; j<states.length; j++) {
			states[j] = Long.parseLong(removeRecords[j].getAttributeAsString("id"));
		}
		
		service.removeStates(Session.get().getSid(), profileId, states, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
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
				setStatesGrid();
			}
		});
	}
	
	
}