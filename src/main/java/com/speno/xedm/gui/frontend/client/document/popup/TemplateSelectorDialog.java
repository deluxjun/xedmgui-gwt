package com.speno.xedm.gui.frontend.client.document.popup;


import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

public class TemplateSelectorDialog extends Window {
	// mydoc 트리
	private ButtonItem btnOk;
	
	private ReturnHandler<Long> returnHandler;		// 20130806, junsoo, 결과를 리턴할 handler
	
	public void setReturnHandler(ReturnHandler<Long> returnHandler) {
		this.returnHandler = returnHandler;
	}
	// 20130806, junsoo, 결과 리턴자 추가
	public TemplateSelectorDialog(ReturnHandler<Long> returnHandler) {
		setReturnHandler(returnHandler);
		
        prepareBase();
        // 트리 그리기
        prepareBody();
        // 확인
        prepareBtn();
	}

	public TemplateSelectorDialog() {
        prepareBase();
        // 트리 그리기
        prepareBody();
        // 확인
        prepareBtn();
	}
	
	private void prepareBase(){
		setWidth(300); 
		setHeight(580);   
        setTitle(I18N.message("second.selectTemplate"));   
        setShowMinimizeButton(false);   
		setCanDragResize(true);
		setCanDragReposition(true);
        setIsModal(true);   
        setShowModalMask(true);   
        centerInPage();
        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				close();
			}
		});
	}
	
	private void prepareBtn(){
		DynamicForm dfButton = new DynamicForm();
        dfButton.setHeight(30);  dfButton.setWidth100();   
        //dfButton.setPadding(5); //dfButton.setMargin(5);
        dfButton.setAlign(Alignment.CENTER);
        dfButton.setNumCols(3);
        dfButton.setColWidths("20", "10", "10");
        
        StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "", "");
        dummy.setShowTitle(false);
        
        btnOk = new ButtonItem();
        btnOk.setTitle(I18N.message("ok"));
        btnOk.setWidth(100);
        btnOk.setStartRow(false); btnOk.setEndRow(false);
        btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord record = grid.getSelectedRecord();
				try {
					Long id = Long.parseLong(record.getAttribute("id"));
					if (returnHandler != null)
						returnHandler.onReturn(id);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        // 취소
        ButtonItem btnCancel = new ButtonItem();
        btnCancel.setTitle(I18N.message("cancel"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false); btnCancel.setEndRow(false);
        btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeWindow();
			}
		});
        
        dfButton.setItems(dummy, btnOk, btnCancel);
        
		btnOk.setDisabled(true);

        addItem(dfButton);
	}

	private void closeWindow(){
		destroy();
	}
	
	private ListGrid grid;
	// 트리 그리기
	private void prepareBody(){
        VLayout vlayout = new VLayout();
        vlayout.setWidth100();
        vlayout.setHeight("95%");

		grid = new ListGrid();
		grid.setWidth100();		
		grid.setHeight100();
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setCanFreezeFields(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField idField = new ListGridField("id", I18N.message("id"), 80);
		idField.setHidden(true);	// 20130816, junsoo, hide id
		ListGridField nameField = new ListGridField("name", I18N.message("name"), 150);
		nameField.setWidth("*");
		
		grid.setFields(idField, nameField);
		
		grid.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				if (grid.getSelectedRecord() != null){
					btnOk.setDisabled(false);
				}
			}
		});
    	
        vlayout.setMargin(5);
        vlayout.addMembers(grid);

        addItem(vlayout);
	}

	@Override
	public void show() {
		super.show();
		
		ServiceUtil.getAllTemplates(new ReturnHandler<STemplate[]>() {
			@Override
			public void onReturn(STemplate[] param) {
				
				List<ListGridRecord> recordList = new ArrayList<ListGridRecord>();
				for (STemplate template : param) {
					ListGridRecord record = new ListGridRecord();
					record.setAttribute("id", template.getId());
					record.setAttribute("name", template.getName());
					recordList.add(record);
				}
//				for (String id : param.keySet()) {
//					String name = param.get(id);
//					
//					ListGridRecord record = new ListGridRecord();
//					record.setAttribute("id", id);
//					record.setAttribute("name", name);
//					recordList.add(record);
//				}
			
				grid.setRecords(recordList.toArray(new ListGridRecord[0]));
			}
		});
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}

}