package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.WindowUtils;
import com.speno.xedm.gwt.service.ECMService;
import com.speno.xedm.gwt.service.ECMServiceAsync;

public class DocumentsXvarmGrid extends ListGrid {
	protected ECMServiceAsync ecmService = (ECMServiceAsync) GWT.create(ECMService.class);
	// 그리드 필드 설정
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
	}
	
	// 그리드 그리기 
	// strArray[] 확장 필드명 배열
	public DocumentsXvarmGrid(String[] strArray) {
		setShowAllRecords(true);
		
		// 체크박스 설정
//		setSelectionType(SelectionStyle.SIMPLE);   
//     setSelectionAppearance(SelectionAppearance.CHECKBOX);   
        
		setEmptyMessage(I18N.message("notitemstoshow"));
		
//		ListGridField chk = new ListGridField("chk", I18N.message("chk"), 30);
//		chk.setType(ListGridFieldType.BOOLEAN);
//		chk.setCanEdit(true);
//		chk.setShowTitle(false);
		
		// 문서 아이디
		ListGridField id = new ListGridField("id");
		id.setHidden(true);
		// 문서속성 조회시 파라메터 elementid 와 동일
		ListGridField idstr = new ListGridField("idstr");
		idstr.setHidden(true);        
        
		// elementid
		ListGridField elementid = new ListGridField("elementid", I18N.message("elementid"), 150);
		setField(elementid, Alignment.CENTER, ListGridFieldType.TEXT, true);
		
		// description
		ListGridField description = new ListGridField("description", I18N.message("description"));
		setField(description, Alignment.CENTER, ListGridFieldType.TEXT, true);
		
		List<ListGridField> fields = new ArrayList<ListGridField>();
//		fields.add(chk);
		fields.add(id);
		fields.add(idstr);
		fields.add(elementid);
		fields.add(description);
		
		// 확장필드 그리기
		ListGridField[] exfields = new ListGridField[strArray.length];
		for(int i=0; i< strArray.length; i++){
			exfields[i] = new ListGridField(strArray[i], strArray[i], 100);
			setField(exfields[i], Alignment.CENTER, ListGridFieldType.TEXT, true);
			fields.add(exfields[i]);
		}
		
		// 최초생성일자
		ListGridField created = new ListGridField("created", I18N.message("createddate"), 110);
		setField(created, Alignment.CENTER, ListGridFieldType.DATE, false);
		created.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		// 최종수정일자
//		ListGridField modified = new ListGridField("modified", I18N.message("modifieddate"), 110);
//		setField(modified, Alignment.CENTER, ListGridFieldType.DATE, false);
//		modified.setCellFormatter(new DateCellFormatter(false));
		
		fields.add(created);
//		fields.add(modified);
		
		setFields(fields.toArray(new ListGridField[0]));
		// 리스트에서 문서 선택시 하단에 문서 속성 표시
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				
				if (getSelectedRecords() != null && getSelectedRecords().length > 1)
					return;
		
				ListGridRecord record = event.getRecord();
				// 선택한 레코드에서 문서아이디를 추출 해당 속성을 조회한다.
//				if (record != null)
//					try {
//						DocumentsPanel.get().onSelectedDocument(Long.parseLong(record.getAttribute("id")), record.getAttribute("idstr").toString());
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					} catch (GeneralException e) {
//						e.printStackTrace();
//					}

			}
		});
		
		addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = setupContextMenu(event.getRecord().getAttribute("id"), event.getRecord().getAttribute("elementid"));
				contextMenu.showContextMenu();
				if (event != null)
					event.cancel();
			}
		});
	}

	//컨텍스트 메뉴 생성
	private Menu setupContextMenu(final String docid, final String elementid) {
		Menu contextMenu = new Menu();

		MenuItem view = new MenuItem();
		view.setTitle(I18N.message("view"));
		view.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onView(docid, elementid);
			}
		});

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onDownload(docid, elementid);
			}
		});
		
		contextMenu.setItems(view, download);
		
		return contextMenu;
	}
		
	/*
	 * 팝업창을 띄워서 activeX를 호출한다.
	 */
	private void onView(String docid, String elementId){
		WindowUtils.openPopupUrl(GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&elementId=" + elementId, "", "");
	}
	
	/*
	 * 다운로드 페이지를 호출한다.
	 */
	private void onDownload(String docid, String elementId){
        WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&elementId=" + elementId);
	}
	
}
