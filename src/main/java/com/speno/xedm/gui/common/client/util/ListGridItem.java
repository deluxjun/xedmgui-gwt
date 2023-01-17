package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.data.TextImportSettings;
import com.smartgwt.client.types.EscapingMode;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.gui.common.client.I18N;

public class ListGridItem extends CanvasItem {
	private ListGrid grid = null;
	private String name = "values";
	private int length = 1000;
	private boolean beRequired = false;
	private String lastError = "";
	
	public String getLastError() {
		return lastError;
	}

	ListGridItem(String name, final int length, boolean beRequired) {
		super(name);
		this.name = name;
		this.length = length;
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
                label.setContents("(" + Integer.toString(length) + ")");
                
                LayoutSpacer spacer = new LayoutSpacer();  
                spacer.setWidth("*");  
                  
                ToolStripButton editButton = new ToolStripButton();  
                editButton.setIcon("[SKIN]/actions/edit.png");  
                editButton.setPrompt(I18N.message("PasteValues"));  
                editButton.addClickHandler(new ClickHandler() {  
                      
                    @Override  
                    public void onClick(ClickEvent event) {  
        				InputStringDialog dialog = new InputStringDialog(525,300) {
        					@Override
        					public void onOk(String text) {
        						pasteText(text);
//        						ListGridField[] fields = grid.getFields();
//        						for (ListGridField f : fields) {
//									f.
//								}
        					}
        				};
        				dialog.draw();
                    }  
                });  
                  
                ToolStripButton addButton = new ToolStripButton();  
                addButton.setIcon("[SKIN]/actions/add.png");  
                addButton.setPrompt(I18N.message("AddValue"));  
                addButton.addClickHandler(new ClickHandler() {  
                      
                    @Override  
                    public void onClick(ClickEvent event) {  
        				ListGridRecord record = new ListGridRecord();
        				record.setAttribute("value", "");
        				addData(record);

                    }  
                });  
                  
                gridEditControls.setMembers(label, spacer, editButton, addButton); 
                
                grid.setGridComponents(new Object[] {  
                		ListGridComponent.HEADER,  
                        ListGridComponent.BODY,   
                        gridEditControls  
                });  

				grid.addDrawHandler(new DrawHandler() {
					@Override
					public void onDraw(DrawEvent event) {
						try {
							ListGrid grid = (ListGrid) event.getSource();
							RecordList data = grid.getDataAsRecordList();
							CanvasItem item = grid.getCanvasItem();
							String value = item.getValue().toString();
							String fieldName = item.getFieldName();
							if (value != null)
								grid.selectRecord(data.find(fieldName, value));
						} catch (Exception e) {
						}
					}
				});
				
                grid.addEditCompleteHandler(new EditCompleteHandler() {
					@Override
					public void onEditComplete(EditCompleteEvent event) {
			            storeItemValue();
					}
				});

//				grid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
//					@Override
//					public void onSelectionUpdated(SelectionUpdatedEvent event) {
//						ListGrid grid = (ListGrid) event.getSource();
//						CanvasItem item = grid.getCanvasItem();
//						ListGridRecord record = grid.getSelectedRecord();
//						if (record != null) {
//							item.storeValue(record.getAttribute(item
//									.getFieldName()));
//						} else {
//							item.storeValue((com.smartgwt.client.data.Record) null);
//						}
//					}
//				});

				((CanvasItem) item).setCanvas(grid);
			}
		});
	}
	
	@Override
	public Boolean validate() {
		storeItemValue();
		String[] strs = getStringData();
		if (beRequired && (strs == null || strs.length < 1)) {
			lastError = I18N.message("fieldrequired") + "(" + name + ")";
			return false;
		}
		
		for (String string : strs) {
			if (string == null || string.getBytes().length > this.length) {
				lastError = I18N.message("invalidLength") + "(" + name + ")";
				return Boolean.FALSE;
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
//		CanvasItem item = grid.getCanvasItem();
//		item.storeValue(grid.getDataAsRecordList());
    	if (grid.getDataAsRecordList() != null && grid.getDataAsRecordList().getLength() > 0)
    		storeValue(grid.getDataAsRecordList());
    }

    public Record[] getData() {  
        return data;  
    }
    
    public String[] getStringData(){
        RecordList rl = grid.getDataAsRecordList();
        
        this.data = rl.toArray();
    	String[] arrays = new String[data.length];
    	for (int i = 0; i < arrays.length; i++) {
			arrays[i] = data[i].getAttribute(name);
		}
    	
    	return arrays;
    }

	public void setData(String[] strs) {
		this.data = new Record[strs.length];
		for (int i = 0; i < strs.length; i++) {
			Record record = new Record();
			record.setAttribute(name, strs[i]);
			data[i] = record;
		}
		
		initGrid();
		
		grid.setData(this.data);
//		storeItemValue();
	}
      
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
    
	public void pasteText(String text) {
		ArrayList<String> fieldNames = new ArrayList<String>();

		fieldNames.add(name);

		TextImportSettings settings = new TextImportSettings();
		settings.setFieldList(fieldNames.toArray(new String[0]));
		settings.setFieldSeparator("\t");
		settings.setEscapingMode(EscapingMode.DOUBLE);

		DataSource dataSource = new DataSource();
		Record[] records = dataSource.recordsFromText(text, settings);
		
		setGridData(records);
	};
	
//	public void setValidators(Validator) {
//		LengthRangeValidator valid = new com.smartgwt.client.widgets.form.validator.LengthRangeValidator();
//		valid.setMax(10);
//		valid.setErrorMessage("invalid length");
//		valueField.setValidators(valid);
//
//	}
	public ListGrid getGrid(){
		return grid;
	}
	
	
	//20140220na 아이템 삭제해도 저장여부 패널이 활성화가 되어야함.
	@Override
	public HandlerRegistration addChangedHandler(final ChangedHandler handler) {
		if (grid == null)
			return super.addChangedHandler(handler);
		
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				handler.onChanged(null);
			}
		});
		return super.addChangedHandler(handler);
	}
};