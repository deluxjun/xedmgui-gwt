package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;

/**
 * DepartmentGrid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class DepartmentGridPanel extends VLayout {	
	private static HashMap<String, DepartmentGridPanel> instanceMap = new HashMap<String, DepartmentGridPanel>();
	
	public ListGrid grid;
	private ListGrid dragSourceGrid;
	private boolean isDragByMe = false;

	private int find_num;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static DepartmentGridPanel get(final String id, final String subTitle, ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, "100%");
	}
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param width
	 * @return
	 */
	public static DepartmentGridPanel get(final String id, final String subTitle, ListGrid dragSourceGrid, final String width) {
		if (instanceMap.get(id) == null) {
			new DepartmentGridPanel(id, subTitle, dragSourceGrid, "100%");
		}
		return instanceMap.get(id);
	} 
	
	public DepartmentGridPanel(final String id, final String subTitle, ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, "100%");
	}
	public DepartmentGridPanel(final String id, final String subTitle, ListGrid dragSrcGrid,  final String width) {
		instanceMap.put(id, this);
		
		if(subTitle != null) {
			Label subTitleLable = new Label();
	        subTitleLable.setAutoHeight();   
	        subTitleLable.setAlign(Alignment.LEFT);   
	        subTitleLable.setValign(VerticalAlignment.CENTER);
	        subTitleLable.setStyleName("subTitleLable2");
	        subTitleLable.setContents(subTitle);	        
	        addMember(subTitleLable);
		}
        
        this.dragSourceGrid = dragSrcGrid;
        
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
        ListGridField idPathField = new ListGridField("IDPath");
        idPathField.setHidden(true);
        ListGridField pathField = new ListGridField("path", I18N.message("usersgroup"));//20130905 남윤성 usersgroup 으로 변경 고정
        idField.setHidden(true);
        
        grid.setFields(idField, idPathField, pathField);
        grid.setCanResizeFields(true);
        
        /* *******************************************************************************************
         * <addDropHandler 추가 이유> 
         * grid.setPreventDuplicates(true);로 설정하더라도 Source Grid에서의 Drag가 아닌
         * 로직에 의해 추가된 grid의 데이타는 dup 체크를 하지 못함.
         * 
         * <addDragStartHandler, addDragStopHandler 추가 이유>
         * 자기자신의 Record drag & drop 방지
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
				if(!isDragByMe && dragSourceGrid != null) {
					copyRecordsToMembers();
					event.cancel();	
				}
			}
        });
        
        grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				event.cancel();
				// 20131206, junsoo, 모두 삭제 가능. 저장할 때만 체크하도록 수정
//				if(grid.getRecords().length-1 == 0){
//					SC.warn(I18N.message("usermustbelongtogroup"));
//					return;
//				}
				
				SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							removeRecordsFromMembers();
						}
					}
				});
			}
		});
        
        dragSourceGrid.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				removeRecordsFromMembers();
				event.cancel();					
			}
        });
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        addMember(grid);
        setWidth(width);
	}
	
	/**
	 * 그리드 초기화
	 */
	protected void resetGrid() {
		grid.setData(new ListGridRecord[0]); //그리드 초기화
//		grid.getField("path").setTitle(I18N.message("usersgroup"));//20130905 남윤성 path 에서 usersgroup 으로 변경되서 grid 세팅시 usersgroup으로 고정 
	}
	
	/**
	 * 그리드 초기화
	 */
	public void resetGrid1() {
		grid.setData(new ListGridRecord[0]); //그리드 초기화
	}
	
	/**
	 * 그리드 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * 그리드 레코드 리스트 반환
	 * @return
	 */
	public RecordList getRecordList() {
		return grid.getRecordList();
	}
	
	/**
	 * 그리드 데이타 추가
	 */
	public void addData(SGroup[] groups) {
		if(groups != null) {
			for(int j=0; j<groups.length; j++) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("id", groups[j].getId());
				record.setAttribute("IDPath", groups[j].getIDPath());
				record.setAttribute("path", groups[j].getPath());
				grid.addData(record);
			}
		}
	}
	
	protected SGroup[] getData() {
		RecordList recordList = grid.getRecordList();
		SGroup[] groups = new SGroup[recordList.getLength()];
		for(int j=0; j<groups.length; j++) {
			groups[j] = new SGroup();			
			groups[j].setId(recordList.get(j).getAttributeAsString("id"));			
			groups[j].setIDPath(recordList.get(j).getAttributeAsString("IDPath"));
			groups[j].setPath(recordList.get(j).getAttributeAsString("path"));
		}
		return groups;
	}
	
	/**
	 * 
	 * @return
	 */
    
	public void copyRecordsToMembers() {
		RecordList recordList = grid.getRecordList();
		final ListGridRecord senderRecord = dragSourceGrid.getSelectedRecord();
		
		/*-------------------------------------------------------------------------------------------
		 * Constants.ADMIN_ROOT 가 Root이나 Tree 특성상 Constants.ADMIN_ROOT
		 * 를 parentid로 가지고 있는것을 Root화 하여 처리함.
		 *------------------------------------------------------------------------------------------*/
		if(String.valueOf(Constants.ADMIN_ROOT).equals(senderRecord.getAttributeAsString("parentid"))) {
			SC.say(I18N.message("cannotcopyroottousergroup"));
			return;
		}
		
		Map<String, String> targetAllMap = new HashMap<String, String>();
		Map<String, String> targetEndMap = new HashMap<String, String>();
		List<String> srcNodesList = new ArrayList<String>();
		
		Record record;
		String pathStr;
		String[] nodeArr;
		
		//targetAllMap, targetEndMap 추출--------------------------------------
		for(int j=0; j<recordList.getLength(); j++) {
			record = recordList.get(j);
			//20140409 yys IDPath값이null인 경우가 존재함 path가 다르고 같은 이름의 부서라도 path자체가 같을수는 없음.
			pathStr = record.getAttributeAsString("path");
			
			nodeArr = pathStr.split(">");
			
			for(int k=0; k<nodeArr.length; k++) {
				targetAllMap.put(nodeArr[k].trim(), nodeArr[k].trim());
			}
			targetEndMap.put(nodeArr[nodeArr.length-1].trim(), nodeArr[nodeArr.length-1].trim());
		}
		
		//srcNodesList 추출------------------------------------------------------
		TreeNode node = ((TreeGrid)dragSourceGrid).getSelectedRecord();
		pathStr = node.getAttribute("path");
		nodeArr = pathStr.split(">");
		for(int k=0; k<nodeArr.length; k++) {
			srcNodesList.add(nodeArr[k].trim());
		}		
		
		//targetEndMap이 srcNodesSet에 있는지 조사--------------------------
		for(int j=0; j<srcNodesList.size(); j++) {
			if(targetEndMap.get(srcNodesList.get(j)) != null) {
				SC.warn(I18N.message("dupmessage"));
				return;
			}
		}		
				
		//dragSourceGrid의 최하위 id가 targetAllMap에 있는지 조사-----------
		for(int j=0; j<targetAllMap.size(); j++) {
			if(targetAllMap.get(senderRecord.getAttributeAsString("name")) != null) {
				SC.warn(I18N.message("dupmessage"));
				return;
			}
		}
		grid.addData(senderRecord);
	}
	
	/**
	 * 
	 * @return
	 */
	public void removeRecordsFromMembers() {
		final ListGridRecord[] removeRecords = grid.getSelectedRecords();
		for(int j=0; j<removeRecords.length; j++) {
			grid.removeData(removeRecords[j]);
		}
	}
}
