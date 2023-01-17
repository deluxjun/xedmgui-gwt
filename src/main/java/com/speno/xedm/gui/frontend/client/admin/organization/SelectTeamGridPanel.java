package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.VerticalAlignment;
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
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.document.popup.MessagePopup;

/**
 * SelectTeamGrid Panel
 * 
 * @author ������
 * @since 1.0
 */
public class SelectTeamGridPanel extends VLayout {	
	private static HashMap<String, SelectTeamGridPanel> instanceMap = new HashMap<String, SelectTeamGridPanel>();
	private ListGrid grid;
	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static SelectTeamGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, "100%");		
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
	public static SelectTeamGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new SelectTeamGridPanel(id, subTitle, dragSourceGrid, width);
		}
		return instanceMap.get(id);
	} 
	
	public SelectTeamGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid,"100%");
	}
	
	public SelectTeamGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
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
    	grid.setCanReorderRecords(true);
    	grid.setCanDragRecordsOut(true);
    	grid.setCanAcceptDroppedRecords(true);
    	grid.setDragDataAction(DragDataAction.MOVE);
    	grid.setCanReorderFields(true);
    	grid.setCanRemoveRecords(true);        
    	grid.setPreventDuplicates(true);
    	grid.setDuplicateDragMessage(I18N.message("dupmessage"));
    	setWidth(width);  
    	
        grid.setShowAllRecords(true);
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField userIdField = new ListGridField("username", I18N.message("userid"));
        ListGridField userNameField = new ListGridField("name", I18N.message("uusername"));
        idField.setHidden(true);
        
        grid.setFields(idField, userIdField, userNameField);
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
				if(!isDragByMe && SelectTeamGridPanel.this.dragSourceGrid != null) {
					copyRecordsToMembers(SelectTeamGridPanel.this.dragSourceGrid);
					event.cancel();	
				}
			}
        });
        
		//record ���� event handler ����--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				removeRecordFromMembers(record);
				event.cancel();
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
        
        HLayout selectTeamGridPanel = new HLayout(0);
        
    	//��ȸ���� Panel ����
        selectTeamGridPanel.setMembers(grid);
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, selectTeamGridPanel);
        
        grid.setData(new ListGridRecord[0]); //�׸��� �ʱ�ȭ
        
        if(MessagePopup.get(Session.get().getSid())!=null){
        	if(null != MessagePopup.get(Session.get().getSid()).form.getField("selecttoId").getValue()){
        		String [] selecttoId = (MessagePopup.get(Session.get().getSid()).form.getValueAsString("selecttoId")).split(",");
        		String [] toId = (MessagePopup.get(Session.get().getSid()).form.getValueAsString("toId")).split(",");
        		String [] toNameTemp = (MessagePopup.get(Session.get().getSid()).form.getValueAsString("toNameTemp")).split(",");
        		
        		for (int j = 0; j < toId.length; j++) {
        			ListGridRecord r=new ListGridRecord();
        			r.setAttribute("id", selecttoId[j]);
        			r.setAttribute("username", toId[j]);
        			r.setAttribute("name",toNameTemp[j]);
        			grid.addData(r);
        		}
        	}
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
	 * Source Grid�� ���� ����Ÿ�� ������ ����
	 * @return
	 */
	public void copyRecordsToMembers() {
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
		
		for(int j=0; j<temp.size(); j++) {
			grid.addData((ListGridRecord)temp.get(j));
		}
			
		Log.debug("[ SelectTeamGridPanel copyRecordsToMembers ] userIds.length["+temp.size() +"]");
	}
	
	private void removeRecordFromMembers(final ListGridRecord record) {		
		Log.debug("[ SelectTeamGridPanel removeRecordFromMembers ] id["+record.getAttributeAsString("id")+"]");
				
		String[] userIds =  new String[1];
		userIds[0] = record.getAttributeAsString("id");
		grid.removeData(record);
	}
	
	/**
	 * 
	 * @return
	 */
	public void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		Log.debug("[ SelectTeamGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]");
		
		if(removeRecords.length <= 0) return;
		
		for(int j=0; j<removeRecords.length; j++) {
			grid.removeData(removeRecords[j]);
		}
	}
}