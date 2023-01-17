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
	// �׸��� �ʵ� ����
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
	}
	
	// �׸��� �׸��� 
	// strArray[] Ȯ�� �ʵ�� �迭
	public DocumentsXvarmGrid(String[] strArray) {
		setShowAllRecords(true);
		
		// üũ�ڽ� ����
//		setSelectionType(SelectionStyle.SIMPLE);   
//     setSelectionAppearance(SelectionAppearance.CHECKBOX);   
        
		setEmptyMessage(I18N.message("notitemstoshow"));
		
//		ListGridField chk = new ListGridField("chk", I18N.message("chk"), 30);
//		chk.setType(ListGridFieldType.BOOLEAN);
//		chk.setCanEdit(true);
//		chk.setShowTitle(false);
		
		// ���� ���̵�
		ListGridField id = new ListGridField("id");
		id.setHidden(true);
		// �����Ӽ� ��ȸ�� �Ķ���� elementid �� ����
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
		
		// Ȯ���ʵ� �׸���
		ListGridField[] exfields = new ListGridField[strArray.length];
		for(int i=0; i< strArray.length; i++){
			exfields[i] = new ListGridField(strArray[i], strArray[i], 100);
			setField(exfields[i], Alignment.CENTER, ListGridFieldType.TEXT, true);
			fields.add(exfields[i]);
		}
		
		// ���ʻ�������
		ListGridField created = new ListGridField("created", I18N.message("createddate"), 110);
		setField(created, Alignment.CENTER, ListGridFieldType.DATE, false);
		created.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		// ������������
//		ListGridField modified = new ListGridField("modified", I18N.message("modifieddate"), 110);
//		setField(modified, Alignment.CENTER, ListGridFieldType.DATE, false);
//		modified.setCellFormatter(new DateCellFormatter(false));
		
		fields.add(created);
//		fields.add(modified);
		
		setFields(fields.toArray(new ListGridField[0]));
		// ����Ʈ���� ���� ���ý� �ϴܿ� ���� �Ӽ� ǥ��
		addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				
				if (getSelectedRecords() != null && getSelectedRecords().length > 1)
					return;
		
				ListGridRecord record = event.getRecord();
				// ������ ���ڵ忡�� �������̵� ���� �ش� �Ӽ��� ��ȸ�Ѵ�.
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

	//���ؽ�Ʈ �޴� ����
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
	 * �˾�â�� ����� activeX�� ȣ���Ѵ�.
	 */
	private void onView(String docid, String elementId){
		WindowUtils.openPopupUrl(GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&elementId=" + elementId, "", "");
	}
	
	/*
	 * �ٿ�ε� �������� ȣ���Ѵ�.
	 */
	private void onDownload(String docid, String elementId){
        WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&elementId=" + elementId);
	}
	
}
