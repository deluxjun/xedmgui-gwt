package com.speno.xedm.gui.frontend.client.admin.documentbox;

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
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * Document Type GridP Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class DocTypeGridPanel extends VLayout {	
	private static HashMap<String, DocTypeGridPanel> instanceMap = new HashMap<String, DocTypeGridPanel>();
		
	private ListGrid grid; 
	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
	private long folderId;
	
	private boolean editable = true;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isCanDrag
	 * @param width
	 * @return
	 */
	public static DocTypeGridPanel get(
			final String id,
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isCanDrag,
			final String width) {
		if (instanceMap.get(id) == null) {
			new DocTypeGridPanel(id, subTitle, dragSourceGrid, isCanDrag, width);
		}
		return instanceMap.get(id);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 *  DocTypeGridPanel ����
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isCanDrag
	 * @param width
	 */
	public DocTypeGridPanel(
			final String id,
			final String subTitle,
			final ListGrid dragSourceGrid, 
			final boolean isCanDrag, 
			final String width) {		
		instanceMap.put(id, this);
		
		if(subTitle != null) {
			/* Sub Title ���� */
			Label subTitleLable = new Label();
			subTitleLable.setAutoHeight();   
	        subTitleLable.setAlign(Alignment.LEFT);   
	        subTitleLable.setValign(VerticalAlignment.CENTER);
	        subTitleLable.setStyleName("subTitle");
	        subTitleLable.setContents(subTitle);
	        addMember(subTitleLable);
		}
		
		//drag source grid instance ����
		this.dragSourceGrid = dragSourceGrid;
        
		//grid ����
        grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);        
        grid.setSelectionType(SelectionStyle.MULTIPLE);
        grid.setCanResizeFields(true);
                
        if(isCanDrag) {
        	//grid�� drag ȯ�� ����
        	grid.setDragDataAction(DragDataAction.COPY);        
	        grid.setCanReorderRecords(false);
	        grid.setCanAcceptDroppedRecords(true);
	        grid.setCanDragRecordsOut(true);        
	    	grid.setCanRemoveRecords(false);
        }        
        //drag ��尡 �ƴҰ�� CRUDȭ������ ������
        else {
        	grid.setDragDataAction(DragDataAction.MOVE);
        	grid.setCanReorderRecords(true);        	
        	grid.setCanAcceptDroppedRecords(true);
        	grid.setCanDragRecordsOut(true);
			grid.setCanRemoveRecords(true);
        	
        	grid.setCanReorderFields(true);
        	grid.setPreventDuplicates(true);
        	grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        	
        	//record ���� event handler ����--------------------------------------------------------------
    		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
    			@Override
    			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
    				event.cancel();
    				// 20140402, junsoo, folder �Ӽ������ ��� ���� �Ұ��ϵ���
    				if (editable) {
	    				final ListGridRecord record = grid.getRecord( event.getRowNum());
	    				SC.confirm(I18N.message("warning"), I18N.message("willbealldoctypesdeleted"),  new BooleanCallback() {
	    					@Override
	    					public void execute(Boolean value) {
	    						if(value != null && value) {
	    							removeRecordsFromMembers(record);
	    						}
	    					}
    				});
    				}
    			}
    		});
        	
        	/* *******************************************************************************************
             * <addDropHandler �߰� ����> 
             * grid.setPreventDuplicates(true);�� �����ϴ��� Source Grid������ Drag�� �ƴ�
             * ������ ���� �߰��� grid�� ����Ÿ�� dup üũ�� ���� ����.
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
        	
        	if(DocTypeGridPanel.this.dragSourceGrid != null) {
        		grid.addDropHandler(new DropHandler() {
        			@Override
        			public void onDrop(DropEvent event) {
        				if(!isDragByMe) {
        					copyRecordsToMembers(DocTypeGridPanel.this.dragSourceGrid);
        					event.cancel();	
        				}
        			}
                });                
                dragSourceGrid.addDropHandler(new DropHandler() {
        			@Override
        			public void onDrop(DropEvent event) {
        				// 20140402, junsoo, folder �Ӽ������ ��� ���� �Ұ��ϵ���
        				if (editable) {
        					removeRecordsFromMembers();
        				}
        				event.cancel();					
        			}
                });
        	}        	
        }
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField nameField = new ListGridField("name", I18N.message("typename"));
        ListGridField descriptionField = new ListGridField("description");
        ListGridField retentionIdField = new ListGridField("retentionId");
        ListGridField retentionNameField = new ListGridField("retentionName");
        ListGridField retentionPeriodField = new ListGridField("retentionPeriod");
        ListGridField elementClassIdField = new ListGridField("elementClassId");
        ListGridField contentClassIdField = new ListGridField("contentClassId");
        ListGridField userClassIdField = new ListGridField("userClassId");
        ListGridField indexIdField = new ListGridField("indexId");        
        
        idField.setHidden(true);
        descriptionField.setHidden(true);
        retentionIdField.setHidden(true);
        retentionNameField.setHidden(true);
        retentionPeriodField.setHidden(true);
        elementClassIdField.setHidden(true);
        contentClassIdField.setHidden(true);
        userClassIdField.setHidden(true);
        indexIdField.setHidden(true);
        
        grid.setFields(idField, nameField, descriptionField, retentionIdField, retentionNameField, 
        		retentionPeriodField, elementClassIdField, contentClassIdField, userClassIdField, indexIdField);
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        addMember(grid);
        setWidth(width);
                
        if(isCanDrag) {
        	//drag ����� ��� �����ʰ� ���ÿ� ����Ÿ�� ��ȸ��
        	executeFetch();
        }
        else {
        	disable();
        }
	}
	
	/**
	 * grid ��ȯ
	 * @return
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * ��� Doc type ����Ÿ ��ȸ
	 */
	protected void executeFetch() {
		GWT.log("[ DocTypeGridPanel executeFetch ]", null);
		
		ServiceUtil.documentcode().listDocTypeLikeName(Session.get().getSid(), "", false, new AsyncCallbackWithStatus<List<SDocType>>() {
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
			public void onSuccessEvent(List<SDocType> result) {
				grid.setData(new ListGridRecord[0]);
				for (int j = 0; j < result.size(); j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());
					record.setAttribute("retentionId", result.get(j).getRetentionId());					
					record.setAttribute("retentionName", result.get(j).getRetentionName());					
					record.setAttribute("retentionPeriod", result.get(j).getRetentionPeriod());
					record.setAttribute("elementClassId", result.get(j).getElementClassId());
					record.setAttribute("contentClassId", result.get(j).getContentClassId());
					record.setAttribute("userClassId", result.get(j).getUserClassId());
					record.setAttribute("indexId", result.get(j).getIndexId());					
					grid.addData(record);
				}
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
	
	/**
	 * Folder Id�� ���� Doc type ����Ÿ ��ȸ
	 * @param id
	 */
	protected void executeFetch(final long id)	{				
		GWT.log("[ DocTypeGridPanel executeFetch ] id["+id+"]", null);
		this.folderId = id;
		
		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_FOLDER_ROOT == id) {
			reset(id);
			return;
		}
		
		ServiceUtil.folder().listDocTypesInFolder(Session.get().getSid(), "", folderId, new AsyncCallbackWithStatus<SDocType[]>() {
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
			public void onSuccessEvent(SDocType[] result) {				
				reset(id);					
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());
					record.setAttribute("retentionId", result[j].getRetentionId());					
					record.setAttribute("retentionName", result[j].getRetentionName());					
					record.setAttribute("retentionPeriod", result[j].getRetentionPeriod());
					record.setAttribute("elementClassId", result[j].getElementClassId());
					record.setAttribute("contentClassId", result[j].getContentClassId());
					record.setAttribute("userClassId", result[j].getUserClassId());
					record.setAttribute("indexId", result[j].getIndexId());					
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
	
	/**
	 *  Folder Id�� ���� �׸��� Panel Ȱ��ȭ ���� ����
	 * @param id
	 */
	private void reset(final long id) {
		grid.setData(new ListGridRecord[0]);
		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_FOLDER_ROOT == id) {
	     	grid.setEmptyMessage(I18N.message("rootisnotallowedto"));
	     	grid.setEmptyMessageStyle("emptyMsg");
	     	disable();
	     }
		else {
			grid.setEmptyMessage(I18N.message("allpossible"));
			grid.setEmptyMessageStyle("emptyMsg");
	     	enable();
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
		// 20140402, junsoo, folder �Ӽ������ ��� ���� �Ұ��ϵ���
		if (!editable) {
			return;
		}
		
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
		
		long[] docTypes =  new long[temp.size()];
		for(int k=0; k<temp.size(); k++) {
			docTypes[k] = Long.parseLong(temp.get(k).getAttributeAsString("id"));
		}
		
		GWT.log("[ DocTypeGridPanel copyRecordsToMembers ] docTypes.length["+docTypes.length+"]", null);
				
		ServiceUtil.folder().addDocTypesToFolder(Session.get().getSid(), docTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	
	/**
	 * ���õ� Record ����
	 * @param record
	 */
	protected void removeRecordsFromMembers(final ListGridRecord record) {
		GWT.log("[ DocTypeGridPanel removeRecordsFromMembers ] id["+record.getAttributeAsString("id")+"]", null);
		
		long[] docTypes = new long[1];
		docTypes[0] = record.getAttributeAsLong("id");
		
		ServiceUtil.folder().removeDocTypesFromFolder(Session.get().getSid(), docTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	 * ���õ� Records ����
	 */
	protected void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		GWT.log("[ DocTypeGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]", null);
		
		if(removeRecords.length <= 0) return;
				
		long[] docTypes =  new long[removeRecords.length];
		for(int j=0; j<docTypes.length; j++) {
			docTypes[j] = Long.parseLong(removeRecords[j].getAttributeAsString("id"));
		}
		
		ServiceUtil.folder().removeDocTypesFromFolder(Session.get().getSid(), docTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	
	/**
	 * Parents�� Type�� ����ϵ��� ����
	 */
	public void inheritRecordsToMembers() {		
		GWT.log("[ DocTypeGridPanel inheritRecordsToMembers ] folderId["+folderId+"]", null);
				
		ServiceUtil.folder().setDocTypesAsParent(Session.get().getSid(), folderId, new AsyncCallbackWithStatus<SDocType[]>() {
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
			public void onSuccessEvent(SDocType[] result) {
				reset(folderId);					
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());
					record.setAttribute("retentionId", result[j].getRetentionId());					
					record.setAttribute("retentionName", result[j].getRetentionName());					
					record.setAttribute("retentionPeriod", result[j].getRetentionPeriod());
					record.setAttribute("elementClassId", result[j].getElementClassId());
					record.setAttribute("contentClassId", result[j].getContentClassId());
					record.setAttribute("userClassId", result[j].getUserClassId());
					record.setAttribute("indexId", result[j].getIndexId());					
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
}