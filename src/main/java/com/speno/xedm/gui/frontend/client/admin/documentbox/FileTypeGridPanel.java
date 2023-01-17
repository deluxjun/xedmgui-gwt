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
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * File Type Grid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class FileTypeGridPanel extends VLayout {	
	private static HashMap<String, FileTypeGridPanel> instanceMap = new HashMap<String, FileTypeGridPanel>();
		
	private ListGrid grid; 
	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;
	private long folderId;
	
	private boolean editable;
	
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isCanDrag
	 * @param width
	 * @return
	 */
	public static FileTypeGridPanel get(
			final String id,
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isCanDrag,
			final String width) {
		if (instanceMap.get(id) == null) {
			new FileTypeGridPanel(id, subTitle, dragSourceGrid, isCanDrag, width);
		}
		return instanceMap.get(id);
	}
	
	/**
	 * File Type Grid Panel 생성
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isCanDrag
	 * @param width
	 */
	public FileTypeGridPanel(
			final String id,
			final String subTitle,
			final ListGrid dragSourceGrid, 
			final boolean isCanDrag, 
			final String width) {		
		instanceMap.put(id, this);
		
		if(subTitle != null) {
			/* Sub Title 생성 */
			Label subTitleLable = new Label();
			subTitleLable.setAutoHeight();   
	        subTitleLable.setAlign(Alignment.LEFT);   
	        subTitleLable.setValign(VerticalAlignment.CENTER);
	        subTitleLable.setStyleName("subTitle");
	        subTitleLable.setContents(subTitle);
	        addMember(subTitleLable);
		}
		
		//drag source grid instance 설정
		this.dragSourceGrid = dragSourceGrid;
        
		//grid 생성
        grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);        
        grid.setSelectionType(SelectionStyle.MULTIPLE);
        grid.setCanResizeFields(true);
        
        if(isCanDrag) {
        	//grid의 drag 환경 설정
        	grid.setDragDataAction(DragDataAction.COPY);        
	        grid.setCanReorderRecords(false);
	        grid.setCanAcceptDroppedRecords(true);
	        grid.setCanDragRecordsOut(true);        
	    	grid.setCanRemoveRecords(false);
        }
        //drag 모드가 아닐경우 CRUD화면으로 설정함
        else {
        	grid.setDragDataAction(DragDataAction.MOVE);
        	grid.setCanReorderRecords(true);        	
        	grid.setCanAcceptDroppedRecords(true);
        	grid.setCanDragRecordsOut(true);

        	grid.setCanRemoveRecords(true);
        	
        	grid.setCanReorderFields(true);
        	grid.setPreventDuplicates(true);
        	grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        	
        	//record 삭제 event handler 정의--------------------------------------------------------------
    		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
    			@Override
    			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
    				// 20140402, junsoo, folder 속성상속일 경우 수정 불가하도록
    				if (editable) {
	    				final ListGridRecord record = grid.getRecord( event.getRowNum());
	    				SC.confirm(I18N.message("warning"), I18N.message("willbeallfiletypesdeleted"),  new BooleanCallback() {
	    					@Override
	    					public void execute(Boolean value) {
	    						if(value != null && value) {
	    							removeRecordsFromMembers(record);
	    						}
	    					}
	    				});
    				}
    				event.cancel();
    			}
    		});
        	
        	/* *******************************************************************************************
             * <addDropHandler 추가 이유> 
             * grid.setPreventDuplicates(true);로 설정하더라도 Source Grid에서의 Drag가 아닌
             * 로직에 의해 추가된 grid의 데이타는 dup 체크를 하지 못함.
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
        	
        	if(FileTypeGridPanel.this.dragSourceGrid != null) {
        		grid.addDropHandler(new DropHandler() {
        			@Override
        			public void onDrop(DropEvent event) {
        				if(!isDragByMe) {
           					copyRecordsToMembers(FileTypeGridPanel.this.dragSourceGrid);
        					event.cancel();	
        				}
        			}
                });
        		
                dragSourceGrid.addDropHandler(new DropHandler() {
        			@Override
        			public void onDrop(DropEvent event) {
        				// 20140402, junsoo, folder 속성상속일 경우 수정 불가하도록
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
        	//drag 모드일 경우 생성됨과 동시에 데이타를 조회함
        	executeFetch();
        }
        else {
        	disable();
        }
	}
	
	/**
	 * 
	 * @return
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	private void executeFetch() {
		GWT.log("[ FileTypeGridPanel executeFetch ]", null);
		
		ServiceUtil.documentcode().listFileTypeLikeName(Session.get().getSid(), "", new AsyncCallbackWithStatus<List<SFileType>>() {
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
			public void onSuccessEvent(List<SFileType> result) {
				grid.setData(new ListGridRecord[0]);
				for (int j = 0; j < result.size(); j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());
					record.setAttribute("viewer", result.get(j).getViewer());					
					record.setAttribute("maxFileSize", result.get(j).getMaxFileSize());					
					record.setAttribute("maxFileSizeCodeName", result.get(j).getMaxFileSizeCodeName());
					record.setAttribute("viewerCodeName", result.get(j).getViewerCodeName());		
					grid.addData(record);
				}
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
	
	protected void executeFetch(final long id)	{				
		GWT.log("[ FileTypeGridPanel executeFetch ] id["+id+"]", null);
		this.folderId = id;
		
		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_FOLDER_ROOT == id) {
			reset(id);
			return;
		}
		ServiceUtil.folder().listFileTypesInFolder(Session.get().getSid(), "", folderId, new AsyncCallbackWithStatus<SFileType[]>() {
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
			public void onSuccessEvent(SFileType[] result) {				
				reset(id);					
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());
					record.setAttribute("viewer", result[j].getViewer());					
					record.setAttribute("maxFileSize", result[j].getMaxFileSize());					
					record.setAttribute("maxFileSizeCodeName", result[j].getMaxFileSizeCodeName());
					record.setAttribute("viewerCodeName", result[j].getViewerCodeName());			
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
	
	public void reset(final long id) {
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
	 * Source Grid로 부터 데이타를 복사해 받음
	 * @return
	 */
	public void copyRecordsToMembers() {
		copyRecordsToMembers(dragSourceGrid);
	}
	
	/**
	 * Source Grid로 부터 데이타를 복사해 받음
	 * @return
	 */
	public void copyRecordsToMembers(ListGrid dragSourceGrid) {
		
		// 20140402, junsoo, folder 속성상속일 경우 수정 불가하도록
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
		
		long[] fileTypes =  new long[temp.size()];
		for(int k=0; k<temp.size(); k++) {
			fileTypes[k] = Long.parseLong(temp.get(k).getAttributeAsString("id"));
		}
		
		GWT.log("[ FileTypeGridPanel copyRecordsToMembers ] fileTypes.length["+fileTypes.length+"]", null);
				
		ServiceUtil.folder().addFileTypesToFolder(Session.get().getSid(), fileTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	
	protected void removeRecordsFromMembers(final ListGridRecord record) {
		GWT.log("[ FileTypeGridPanel removeRecordsFromMembers ] id["+record.getAttributeAsString("id")+"]", null);
		
		long[] fileTypes = new long[1];
		fileTypes[0] = record.getAttributeAsLong("id");
		
		ServiceUtil.folder().removeFileTypesFromFolder(Session.get().getSid(), fileTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	
	public void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		
		GWT.log("[ FileTypeGridPanel removeRecordsFromMembers ] removeRecords.length["+removeRecords.length+"]", null);
		
		if(removeRecords.length <= 0) return;
				
		long[] fileTypes =  new long[removeRecords.length];
		for(int j=0; j<fileTypes.length; j++) {
			fileTypes[j] = Long.parseLong(removeRecords[j].getAttributeAsString("id"));
		}
		
		ServiceUtil.folder().removeFileTypesFromFolder(Session.get().getSid(), fileTypes, folderId, new AsyncCallbackWithStatus<Void>() {
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
	
	public void inheritRecordsToMembers() {		
		GWT.log("[ FileTypeGridPanel inheritRecordsToMembers ] folderId["+folderId+"]", null);
				
		ServiceUtil.folder().setFileTypesAsParent(Session.get().getSid(), folderId, new AsyncCallbackWithStatus<SFileType[]>() {
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
			public void onSuccessEvent(SFileType[] result) {
				reset(folderId);					
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());
					record.setAttribute("viewer", result[j].getViewer());					
					record.setAttribute("maxFileSize", result[j].getMaxFileSize());					
					record.setAttribute("maxFileSizeCodeName", result[j].getMaxFileSizeCodeName());
					record.setAttribute("viewerCodeName", result[j].getViewerCodeName());			
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
				}
			}
		});
	}
}