package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.gui.common.client.I18N;

public class ListGridMultipleItem extends CanvasItem {
	private ListGrid grid = null;
	private String name = "values";
	private int length = 1000;
	private boolean beRequired = false;
	private String lastError = "";
	
	public String getLastError() {
		return lastError;
	}

	ListGridMultipleItem(String name, boolean beRequired) {
		super(name);
		this.name = name;
		this.beRequired = beRequired;
		
		// setHeight("100%");
		// setWidth("100%");
		setEndRow(true);
		setStartRow(true);
		setColSpan("*");
		setShowTitle(false);

		// this is going to be an editable data item
		setShouldSaveValue(true);

		addShowValueHandler(new ShowValueHandler() {
			@Override
			public void onShowValue(ShowValueEvent event) {
				CanvasItem item = (CanvasItem) event.getSource();

				ListGrid grid = (ListGrid) item.getCanvas();
				if (grid == null)
					return;

				grid.deselectAllRecords();
				String value = (String) event.getDisplayValue();
				if (value == null)
					return;

				RecordList recordList = grid.getDataAsRecordList();
				int index = recordList.findIndex(item.getFieldName(), value);
				grid.selectRecord(index);
			}
		});

		setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem item) {
				initGrid();

                // edit pane
                ToolStrip gridEditControls = new ToolStrip();  
                gridEditControls.setWidth100();  
                gridEditControls.setHeight(24);  
                  
                Label label = new Label();  
                label.setPadding(5);
                label.setContents("");
                
                LayoutSpacer spacer = new LayoutSpacer();  
                spacer.setWidth("*");  
                  
//                ToolStripButton editButton = new ToolStripButton();  
//                editButton.setIcon("[SKIN]/actions/edit.png");  
//                editButton.setPrompt(I18N.message("PasteValues"));  
//                editButton.addClickHandler(new ClickHandler() {  
//                      
//                    @Override  
//                    public void onClick(ClickEvent event) {  
//        				InputStringDialog dialog = new InputStringDialog(525,300) {
//        					@Override
//        					public void onOk(String text) {
//        						pasteText(text);
//        					}
//        				};
//        				dialog.draw();
//                    }  
//                });  
                  
                ToolStripButton addButton = new ToolStripButton();  
                addButton.setIcon("[SKIN]/actions/add.png");  
                addButton.setPrompt(I18N.message("AddValue"));  
                addButton.addClickHandler(new ClickHandler() {  
                      
                    @Override  
                    public void onClick(ClickEvent event) {  
                    	 ListGridRecord record = new ListGridRecord();
                    	 //20140304 yuk 처음 등록시 name value를 0으로 생성 -> 에러방지
//                    	 record.setAttribute("name","0");
//                    	 record.setAttribute("value","0");
        				addData(record); 
                    }  
                });  
                  
//                gridEditControls.setMembers(label, spacer, editButton, addButton); 
                gridEditControls.setMembers(label, spacer, addButton); 
                
                grid.setGridComponents(new Object[] {  
                		ListGridComponent.HEADER,  
                        ListGridComponent.BODY,   
                        gridEditControls  
                });  

				grid.addDrawHandler(new DrawHandler() {
					@Override
					public void onDraw(DrawEvent event) {
						ListGrid grid = (ListGrid) event.getSource();
						RecordList data = grid.getDataAsRecordList();
						CanvasItem item = grid.getCanvasItem();
//						String value = (String) item.getValue();
//						String fieldName = item.getFieldName();
//						if (value != null)
//							grid.selectRecord(data.find(fieldName, value));
					}
				});
				
                grid.addEditCompleteHandler(new EditCompleteHandler() {
					@Override
					public void onEditComplete(EditCompleteEvent event) {
			            storeItemValue();
					}
				});

				((CanvasItem) item).setCanvas(grid);
			}
		});
	}
	
	@Override
	public Boolean validate() {
		Record[] records = getData();
		if (beRequired && (records == null || records.length < 1)) {
			lastError = I18N.message("fieldrequired") + "(" + name + ")";
			return false;
		}
		
		if (records != null)
		for (Record r : records) {
			for (String string : r.getAttributes()) {
				if (string == null || string.length() > this.length) {
					lastError = I18N.message("invalidLength") + "(" + name + ")";
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}
	
	private void initGrid() {
		if (grid == null)
			grid = new ListGrid();
		else
			return;
		
		grid.setCanEdit(true);
		grid.setCanRemoveRecords(true);
//		grid.setWidth("*");
//		grid.setHeight("*");
		grid.setLeaveScrollbarGap(false);
		grid.setFields(getGridFields());
		if (getData() != null)
			grid.setData(getData());
		grid.setAutoFetchData(false);
	}

    private Record[] data;  

    public void setGridData(Record[] data) {  
        this.data = data;  
        grid.setData(data);
        storeItemValue();
    }
    
    private void storeItemValue() {
        CanvasItem item = grid.getCanvasItem();
        item.storeValue(grid.getDataAsRecordList());
    }

    public Record[] getData() {  
        RecordList rl = grid.getDataAsRecordList();
        if (rl == null)
        	return null;
        Record[] records = new Record[rl.getLength()];
        for (int i = 0; i < rl.getLength(); i++) {
        	records[i] = rl.get(i);
		}
        data = records;

        return data;  
    }
    
//    public String[] getStringData(){
//        RecordList rl = grid.getDataAsRecordList();
//        
//        this.data = rl.toArray();
//    	String[] arrays = new String[data.length];
//    	for (int i = 0; i < arrays.length; i++) {
//			arrays[i] = data[i].getAttribute(name);
//		}
//    	
//    	return arrays;
//    }
//
//	public void setData(String[] strs) {
//		this.data = new Record[strs.length];
//		for (int i = 0; i < strs.length; i++) {
//			Record record = new Record();
//			record.setAttribute(name, strs[i]);
//			data[i] = record;
//		}
//		
//		initGrid();
//		
//		grid.setData(this.data);
//	}
      
    private ListGridField[] gridFields;  
    public void setGridFields(ListGridField... gridFields) {  
        this.gridFields = gridFields;  
    }  
      
    public ListGridField[] getGridFields() {  
        return gridFields;  
    }
    
    public void addData(ListGridRecord record) {
    	grid.addData(record);
    }
    
    public boolean isNullData(){
    	Record[] records = getData();
    	
    	if (records == null)
			return false;
		for (Record a : records) {
			if(a.getAttribute("name") == null || a.getAttribute("value") == null) return true;
		}
    	return false;
    }
    
//	public void pasteText(String text) {
//		ArrayList<String> fieldNames = new ArrayList<String>();
//
//		fieldNames.add(name);
//
//		TextImportSettings settings = new TextImportSettings();
//		settings.setFieldList(fieldNames.toArray(new String[0]));
//		settings.setFieldSeparator("\t");
//		settings.setEscapingMode(EscapingMode.DOUBLE);
//
//		DataSource dataSource = new DataSource();
//		Record[] records = dataSource.recordsFromText(text, settings);
//		
//		setGridData(records);
//	};
	
//	public void setValidators(Validator) {
//		LengthRangeValidator valid = new com.smartgwt.client.widgets.form.validator.LengthRangeValidator();
//		valid.setMax(10);
//		valid.setErrorMessage("invalid length");
//		valueField.setValidators(valid);
//
//	}
};