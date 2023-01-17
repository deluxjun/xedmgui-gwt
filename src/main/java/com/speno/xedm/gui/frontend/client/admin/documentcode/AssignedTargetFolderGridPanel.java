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
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.LifeCycleService;
import com.speno.xedm.gwt.service.LifeCycleServiceAsync;

/**
 * AssignedTargetFolderGrid Panel
 * 
 * @author
 * @since 1.0
 */
public class AssignedTargetFolderGridPanel extends VLayout {	
	private static HashMap<String, AssignedTargetFolderGridPanel> instanceMap = new HashMap<String, AssignedTargetFolderGridPanel>();
	
	private LifeCycleServiceAsync lifeService = (LifeCycleServiceAsync) GWT.create(LifeCycleService.class);
	
	private ListGrid grid;
	private ListGrid dragSourceGrid;
	private ListGrid statesGrid;
	private DynamicForm statesForm ;
	private String comaTargets;
	private String comaPaths;
	private boolean isDragByMe = false;
//	private long groupId = Constants.INVALID_LONG;
	
	/**
	 * 
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static AssignedTargetFolderGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, false, "100%");		
	}
	
	/**
	 * 
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isAction
	 * @param width
	 * @return
	 */
	public static AssignedTargetFolderGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isAction, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new AssignedTargetFolderGridPanel(id, subTitle, dragSourceGrid, isAction, width);
		}
		return instanceMap.get(id);
	} 
	
	public AssignedTargetFolderGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, false, "100%");
	}
	
	public AssignedTargetFolderGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final String width) {		
		instanceMap.put(id, this);
		
		/* Sub Title  */
		Label subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(subTitle);
        
        //drag source grid instance 
        this.dragSourceGrid = dragSourceGrid;
        
        //grid 
        grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);
        
        grid.setCanReorderRecords(true);
        grid.setCanDragRecordsOut(true);
        grid.setCanAcceptDroppedRecords(true);
        grid.setDragDataAction(DragDataAction.MOVE);
        
        grid.setEmptyMessage(I18N.message("droprowshererequired"));
        grid.setCanReorderFields(true);
        grid.setCanRemoveRecords(true);        
        grid.setPreventDuplicates(true);
        grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        idField.setHidden(true);
        ListGridField targetField = new ListGridField("target", I18N.message("target"));
        targetField.setHidden(true);
        ListGridField pathField = new ListGridField("path", I18N.message("target"));
        
        pathField.setWidth("*");
        
        grid.setFields(idField, targetField, pathField);
        grid.setCanResizeFields(true);
        
        /* *******************************************************************************************
         * <addDropHandler > 
         * grid.setPreventDuplicates(true);
         * 
         * 
         * <addDragStartHandler, addDragStopHandler >
         * 
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
				if(!isDragByMe && AssignedTargetFolderGridPanel.this.dragSourceGrid != null) {
					copyRecordsToMembers(AssignedTargetFolderGridPanel.this.dragSourceGrid);
					event.cancel();	
				}
			}
        });
        
		//record event handler--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				removeRecordFromMembers(record,event.getRowNum());
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
        	//Action 
	        VLayout teamVL = new VLayout(5);
	        teamVL.setWidth100();
//	        teamVL.setMembers(grid, createActHL());
	        teamVL.setMembers(grid);
	        teamGridPanel.setMembers(teamVL);
        }
        else {
        	//
	        teamGridPanel.setMembers(grid);
        }
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, teamGridPanel);
        setWidth(width);  
        
        disable();
	}
	

	public ListGrid getGrid(){
		return grid;
	}
	
	/**
	 * Default 
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
	 * Action Panel
	 * @return HLayout
	 * 2013-11-22
	 * 
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
	 * Source Grid
	 * @return
	 */
	protected void copyRecordsToMembers() {
		copyRecordsToMembers(dragSourceGrid);
	}
	
	/**
	 * Source Grid
	 * @return
	 */
	private void copyRecordsToMembers(ListGrid dragSourceGrid) {
		
		
		final List<ListGridRecord> temp = new ArrayList<ListGridRecord>();		
		final ListGridRecord addRecord = new ListGridRecord();
		final ListGridRecord[] senderRecords = dragSourceGrid.getSelectedRecords();
		RecordList rclist = grid.getDataAsRecordList();
		long stateId = 0L;
		comaTargets = "";
		comaPaths = "";
		
		int rccnt = rclist.getLength();
		
		if(rccnt !=0){//
			for(int i=0; i< rccnt; i++){
				Record rc = rclist.get(i);			
				stateId = Long.parseLong(rc.getAttributeAsString("id"));
				comaTargets += ","+ rc.getAttribute("target");
				comaPaths += ","+ rc.getAttribute("path");
				
			}
		}else{			
			ListGridRecord selectedRecord = statesGrid.getSelectedRecord();
			stateId = Long.parseLong(selectedRecord.getAttributeAsString("id"));
			comaTargets = "";
			comaPaths = "";
		}
		for(int j=0; j<senderRecords.length; j++) {	//
			if(rclist.find("target", senderRecords[j].getAttribute("id")) == null) {
				comaTargets += ","+ senderRecords[j].getAttribute("id");
				comaPaths += ","+ senderRecords[j].getAttribute("paths").replace("/Shared", "");
				
				addRecord.setAttribute("id", stateId);				
				addRecord.setAttribute("target", senderRecords[j].getAttribute("id"));
				addRecord.setAttribute("path", senderRecords[j].getAttribute("paths").replace("/Shared", ""));
				temp.add(addRecord);
			}
		}
		comaTargets = comaTargets.substring(1);
		comaPaths = comaPaths.substring(1);
		long [] targets = changeStringToArray(comaTargets,",");	
		
		lifeService.saveStateTarget(Session.get().getSid(), stateId, targets, new AsyncCallbackWithStatus<Void>() {
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
				
				//statesGrid 
				final int selectedRowNum = statesGrid.getRecordIndex(statesGrid.getSelectedRecord());				
				ListGridRecord selectedRecord = statesGrid.getSelectedRecord();
				
				selectedRecord.setAttribute("target", comaTargets);
				selectedRecord.setAttribute("path", comaPaths);
				
				statesGrid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				statesGrid.selectSingleRecord(selectedRowNum);
				statesGrid.scrollToRow(selectedRowNum);
				
				statesForm.getItem("id").setTooltip(I18N.message("fieldisreadonly", statesForm.getItem("id").getTitle()));
				statesForm.reset();
				statesForm.editRecord(selectedRecord);
			}
		});
	}
	
	/**
	 * 
	 */
	private void removeRecordFromMembers(final ListGridRecord record,int rowNum) {		
		Log.debug("[ AssignedTargetFolderGridPanel removeRecordFromMembers ] id["+record.getAttributeAsString("id")+"]");
		
		long stateId = 0L;
		comaTargets = "";
		comaPaths = "";
		RecordList rclist = grid.getDataAsRecordList();
		int rccnt = rclist.getLength();
		if(rccnt == 1)return;//
		long [] targets = new long[rccnt==1?1:rccnt - 1];
		
		int j=0;
		for(int i=0; i< rccnt; i++){
			Record rc = rclist.get(i);			
			stateId = Long.parseLong(rc.getAttributeAsString("id"));
			if(rowNum != i){
				targets[j] = Long.parseLong(rc.getAttributeAsString("target"));
				comaTargets += ","+ rc.getAttribute("target");
				comaPaths += ","+ rc.getAttribute("path");
				j++;
			}
			
			if(rccnt == 1){//
				targets = null;
				comaTargets = "";
				comaPaths = "";
			}
		}
		
		lifeService.saveStateTarget(Session.get().getSid(), stateId, targets, new AsyncCallbackWithStatus<Void>() {
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
//				grid.removeData(record);
				
				//statesGrid
				final int selectedRowNum = statesGrid.getRecordIndex(statesGrid.getSelectedRecord());				
				ListGridRecord selectedRecord = statesGrid.getSelectedRecord();
				
				selectedRecord.setAttribute("target", (""==comaTargets?"":comaTargets.substring(1)));
				selectedRecord.setAttribute("path", (""==comaPaths?"":comaPaths.substring(1)));
				
				statesGrid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				statesGrid.selectSingleRecord(selectedRowNum);
				statesGrid.scrollToRow(selectedRowNum);
				
				statesForm.getField("target").setValue((""==comaTargets?"":comaTargets.substring(1)));
				statesForm.getField("path").setValue((""==comaPaths?"":comaPaths.substring(1)));
				
				statesForm.getItem("id").setTooltip(I18N.message("fieldisreadonly", statesForm.getItem("id").getTitle()));
				statesForm.reset();
				statesForm.editRecord(selectedRecord);
			}
		});
	}
	
	/**
	 * 
	 */
	protected void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		int src = removeRecords.length;
		
		Log.debug("[ AssignedTargetFolderGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]");
		
		long stateId = 0L;
		comaTargets = "";
		comaPaths = "";
		RecordList rclist = grid.getDataAsRecordList();
		int rccnt = rclist.getLength();
		
		if(rccnt == src)return;//
		
		for(int i=0; i< rccnt; i++){
			boolean hasData = false;
			Record rc = rclist.get(i);			
			stateId = Long.parseLong(rc.getAttributeAsString("id"));
			for(int r=0; r< removeRecords.length; r++){
				int p = grid.getRecordIndex(removeRecords[r]);
				if(p == i){//
					hasData = true;
					break;
				}
			}
			if(!hasData){
				comaTargets += ","+ rc.getAttribute("target");
				comaPaths += ","+ rc.getAttribute("path");
			}
			
		}
		comaTargets = "".equals(comaTargets)?comaTargets:comaTargets.substring(1);
		comaPaths = "".equals(comaPaths)?comaPaths:comaPaths.substring(1);

		long [] targets;
		if("".equals(comaTargets)){
			targets = null;
			comaTargets = "";
			comaPaths = "";
		}else{
			targets = changeStringToArray(comaTargets,",");	
		}
		
		lifeService.saveStateTarget(Session.get().getSid(), stateId, targets, new AsyncCallbackWithStatus<Void>() {
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
				
				//statesGrid
				final int selectedRowNum = statesGrid.getRecordIndex(statesGrid.getSelectedRecord());				
				ListGridRecord selectedRecord = statesGrid.getSelectedRecord();
				
				selectedRecord.setAttribute("target", comaTargets);
				selectedRecord.setAttribute("path", comaPaths);
				
				statesGrid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				statesGrid.selectSingleRecord(selectedRowNum);
				statesGrid.scrollToRow(selectedRowNum);
				
				statesForm.getField("target").setValue(comaTargets);
				statesForm.getField("path").setValue(comaPaths);
				
				statesForm.getItem("id").setTooltip(I18N.message("fieldisreadonly", statesForm.getItem("id").getTitle()));
				statesForm.reset();
				statesForm.editRecord(selectedRecord);
			}
		});
	}
	
	/**
	 * 
	 */
	protected void setGrid(Record record,ListGrid orgStatesGrid, DynamicForm orgStatesFrom) {
		Log.debug("[ AssignedTargetFolderGridPanel executeFetch ]");
		grid.setData(new ListGridRecord[0]); //assignedTargetFolderGridP 
		if(record.getAttribute("target") != null){
			statesGrid = orgStatesGrid;
			statesForm = orgStatesFrom;
			String[] arrTarget =  record.getAttribute("target").split(",");
			String[] arrPath =  record.getAttribute("path").split(",");
			reset();
			if(!(arrTarget.length ==1 && "".equals(arrTarget[0]))){
				for (int j = 0; j < arrTarget.length; j++) {					
					ListGridRecord setrecord=new ListGridRecord();
					setrecord.setAttribute("id", record.getAttribute("id"));
					setrecord.setAttribute("target", arrTarget[j]);
					setrecord.setAttribute("path", arrPath[j]);
					grid.addData(setrecord);
				}	
				if (arrTarget.length > 0) {
					grid.selectSingleRecord(0);
				}	
			}
		}
		
	}
	
	/**
	 * 
	 */
	private long[] changeStringToArray(String target, String delimiter) {
		String [] sArray = target.split(delimiter);
		long[] lArray = new long[sArray.length];
		
		for(int i=0; i<sArray.length; i++){
			lArray[i] = Long.parseLong(sArray[i]);
		}
		return lArray;
	}
}