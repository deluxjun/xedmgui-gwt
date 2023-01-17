package com.speno.xedm.gui.frontend.client.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SShare;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;

public class PersonalSharedGrid extends ListGrid{
	private int totalLengh = 0; 
	
	public int getTotalLengh() {
		return totalLengh;
	}

	public void setTotalLengh(int totalLengh) {
		this.totalLengh = totalLengh;
	}
	
	public PersonalSharedGrid(){
		initField();
		initAction();
		setCanRemoveRecords(true);
	}
	
	/**
	 * 	액션 초기화
	 * */
	private void initAction(){
		/*
		 * Record 선택 동작
		 * */
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord record = getSelectedRecord();
				setInfoBySelect(record);
			}
		});
		
		/*
		 * Record 제거 동작
		 * */
		addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = getRecord(event.getRowNum());
				SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							removeRecord(record);
						}
					}
				});
				event.cancel();
			}
		});
		
		/*
		 * 마우스 우클릭 동작
		 * */
		addRowContextClickHandler(new RowContextClickHandler() {
			@Override
			public void onRowContextClick(RowContextClickEvent event) {
				ListGridRecord record = event.getRecord();
				selectRecord(record);
				if(record != null){
	            	// 우클릭 액션
	            	Session.get().selectDocuments(new SRecordItem[]{new SRecordItem(new SDocument())});
					Menu menu = DocumentActionUtil.get().getSharingContextMenu();
					setContextMenu(menu);
				}
			}
		});
		addRightMouseDownHandler(new RightMouseDownHandler() {
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				ListGridRecord record = getSelectedRecord();
				selectRecord(record);
				setInfoBySelect(record);
				if(record != null){
					Menu menu = DocumentActionUtil.get().getSharingContextMenu();
					setContextMenu(menu);
				}
			}
		});
	}
	
	/**
	 * 레코드 삭제시 공유 삭제 동작
	 * @param record
	 */
	private void removeRecord(ListGridRecord record){
		long recordId = record.getAttributeAsLong("id");
		//TODO
		ServiceUtil.folder().deleteSharing(Session.get().getSid(), recordId, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				PersonalSharedPanel.get().execute();
				Log.info(I18N.message("successdelete"), I18N.message("successdelete"));
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	/**
	 *	필드 초기화
	 * */
	private void initField(){
		ListGridField id = new ListGridField("id", I18N.message("id"));
		setField(id, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("id"));
		id.setHidden(true);
		
		ListGridField target = new ListGridField("target", I18N.message("target"));
		setField(target, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("target"));
		
		ListGridField folder = new ListGridField("folder", I18N.message("folder"));
		setField(folder, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("folder"));
		
		ListGridField comment = new ListGridField("comment", I18N.message("comment"));
		setField(comment, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("comment"));
		
		ListGridField date = new ListGridField("date", I18N.message("date"));
		setField(date, Alignment.CENTER, ListGridFieldType.DATE, false, I18N.message("date"));
		date.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		
		ListGridField rights = new ListGridField("rights", I18N.message("rights"));
		setField(rights, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("rights"));
		
		List<ListGridField> fields = new ArrayList<ListGridField>();

		fields.add(target);
		fields.add(folder);
		fields.add(comment);
		fields.add(date);
		fields.add(rights);
		
		setFields(fields.toArray(new ListGridField[0]));
	}
	
	/**
	 * Grid Data Set
	 * @param data
	 * @param gridPager
	 */
	public void setGridData(List<SShare> data, PagingToolStrip gridPager, int totalLength){
		if(data.size()>0 && data != null){
			int dataLength = (data.size() >= gridPager.getPageSize())? gridPager.getPageSize() : data.size();
			setTotalLengh(totalLength);
			ListGridRecord records[] = new ListGridRecord[dataLength];
			for(int i=0 ; i < dataLength ; i++){
				SShare share = data.get(i);
				ListGridRecord record = new ListGridRecord();
				setRecordData(record, share);
				records[i]=record;
			}
			setData(records);
		}else{
			setData(new ListGridRecord[]{});
		}
	}
	
	/**
	 * Field에 맞추어 데이터를 Set
	 * @param record
	 * @param share
	 */
	private void setRecordData(ListGridRecord record, SShare share) {
		// 아이디 셋팅
		record.setAttribute("id", share.getId());
		record.setAttribute("target", share.getGroupName());
		record.setAttribute("folder", share.getFolderName());
		record.setAttribute("comment", share.getComment());
		record.setAttribute("date", share.getLastModified());
		record.setAttribute("rights", setRightsData(share));

		record.setAttribute("share", share);
	}
	
	/**
	 * 선택된 레코드의 값으로 FolderSharingPanel의 값을 채운다.
	 * @param record
	 */
	private void setInfoBySelect(ListGridRecord record){
		FolderSharingPanel sharing = PersonalSharedPanel.get().getSharingPanel();
		sharing.setData(record);
	}
	
	/**
	 * 폴더 권한 표시 데이터 파싱(ex : a, b, c, ...)
	 * @param share
	 * @return
	 */
	private String setRightsData(SShare share){
		String right = "";
		if(share.getRead() == 1)			right += ", " + I18N.message("read");
		if(share.getDelete()== 1)			right += ", " + I18N.message("delete");
		if(share.getWrite() == 1)			right += ", " + I18N.message("write");
		if(share.getIncludeSubDir() == 1)	right += ", " + I18N.message("includesubdirectories");

		try{
			right = right.substring(1);
		}catch(Exception e){}
		
		return right;
	}
	
	/**
	 * 리스트 그리드 필드 설정 
	 * align : 정렬
	 * type : 그리드 필드의 타입
	 * filter : 필터가능 여부
	 * title : 필드의 타이틀 설정
	 */
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter, String title){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
		glidField.setTitle(title);
	}
}
