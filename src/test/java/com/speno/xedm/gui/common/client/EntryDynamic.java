package com.speno.xedm.gui.common.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.data.TextImportSettings;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.EscapingMode;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryDynamic implements EntryPoint{
	
    public class ListGridItem extends CanvasItem {
    	private ListGrid grid = null;
    	private String name = "values";
        ListGridItem (String name) {
            super(name);
            this.name = name;
              
//            setHeight("100%");  
//            setWidth("100%");  
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
                      
                    ListGrid grid = (ListGrid)item.getCanvas();  
                    if (grid==null) return;  
                      
                    grid.deselectAllRecords();  
                    String value = (String) event.getDisplayValue();  
                    if (value==null) return;  
                      
                    RecordList recordList = grid.getDataAsRecordList();  
                    int index = recordList.findIndex(item.getFieldName(), value);  
                    grid.selectRecord(index);  
                }  
            });  
              
            setInitHandler(new FormItemInitHandler () {  
                @Override  
                public void onInit(FormItem item) {
                	if (grid == null)
                		grid = new ListGrid();  
                	grid.setCanEdit(true);  
                	grid.setCanRemoveRecords(true);
                    grid.setWidth("*");  
                    grid.setHeight("*");  
                    grid.setLeaveScrollbarGap(false);  
                    grid.setFields(((ListGridItem) item).getGridFields());  
                    if (((ListGridItem)item).getData() != null)
                    	grid.setData(((ListGridItem)item).getData());  
                    grid.setAutoFetchData(false);  
                    
                    // edit pane
                    ToolStrip gridEditControls = new ToolStrip();  
                    gridEditControls.setWidth100();  
                    gridEditControls.setHeight(24);  
                      
                    LayoutSpacer spacer = new LayoutSpacer();  
                    spacer.setWidth("*");  
                      
                    ToolStripButton editButton = new ToolStripButton();  
                    editButton.setIcon("[SKIN]/actions/edit.png");  
                    editButton.setPrompt("Edit selected record");  
                    editButton.addClickHandler(new ClickHandler() {  
                          
                        @Override  
                        public void onClick(ClickEvent event) {  
            				InputStringDialog dialog = new InputStringDialog() {
            					@Override
            					public void onOk(String text) {
            						pasteText(text);
            					}
            				};
            				dialog.draw();
                        }  
                    });  
                      
                    ToolStripButton addButton = new ToolStripButton();  
                    addButton.setIcon("[SKIN]/actions/add.png");  
                    addButton.setPrompt("Remove selected record");  
                    addButton.addClickHandler(new ClickHandler() {  
                          
                        @Override  
                        public void onClick(ClickEvent event) {  
            				ListGridRecord record = new ListGridRecord();
            				record.setAttribute("value", "");
            				multiValueItem.addData(record);

                        }  
                    });  
                      
                    gridEditControls.setMembers(spacer, editButton, addButton); 
                    
                    grid.setGridComponents(new Object[] {  
                    		ListGridComponent.HEADER,  
                            ListGridComponent.BODY,   
                            gridEditControls  
                    });  
                      
                    grid.addDrawHandler(new DrawHandler() {  
                        @Override  
                        public void onDraw(DrawEvent event) {  
                            ListGrid grid = (ListGrid)event.getSource();  
                            RecordList data = grid.getDataAsRecordList();  
                            CanvasItem item = grid.getCanvasItem();  
                            String value = (String)item.getValue();  
                            String fieldName = item.getFieldName();  
                            if (value != null) grid.selectRecord(data.find(fieldName, value));                              
                        }  
                    });
                    
                    grid.addEditCompleteHandler(new EditCompleteHandler() {
						@Override
						public void onEditComplete(EditCompleteEvent event) {
				            storeItemValue();
						}
					});
                    
//                    grid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {  
//                        @Override  
//                        public void onSelectionUpdated(SelectionUpdatedEvent event) {  
//                            ListGrid grid = (ListGrid) event.getSource();  
//                            CanvasItem item = grid.getCanvasItem();  
////                            ListGridRecord record = grid.getSelectedRecord();  
////                            if (record != null) {  
////                                item.storeValue(record.getAttribute(item.getFieldName()));  
////                            } else {  
////                                item.storeValue((com.smartgwt.client.data.Record)null);  
////                            }
//                            RecordList rl = new RecordList(getData());
//                            item.storeValue(rl);
//                        }  
//                    });  
                      
                    ((CanvasItem) item).setCanvas(grid);  
                }  
            });  
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

    };
    
    public ListGridItem newMultiValueItem(String name, String title, int width) {
    	ListGridItem item = new ListGridItem(name) {
    		@Override
    		public Boolean validate() {
                String[] strs = getStringData();
                for (String string : strs) {
					if (string.getBytes().length > 10)
						return Boolean.FALSE;
				}
                return Boolean.TRUE;
    		}
    	}; 

    	item.setName(name);
		item.setTitle(name + " (" + title + ")");
		item.setShowTitle(true);
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");

		ListGridField valueField = new ListGridField(name, title, width);
		
		LengthRangeValidator valid = new com.smartgwt.client.widgets.form.validator.LengthRangeValidator();
		valid.setMax(10);
		valid.setErrorMessage("invalid length");
		valueField.setValidators(valid);

    	item.setGridFields(valueField);
    	
    	item.setValidators(valid);
    	
    	return item;
    }
    
//	private ListGrid countryList;  
	private ListGridItem multiValueItem;  

	public void onModuleLoad() {
		final ValuesManager vm = new ValuesManager();

		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setTitleWidth(100);
		form.setNumCols(2);
		form.setColWidths("1","*");
//		form.setNumCols(4);
		form.setWidth(200);
		form.setHeight(200);
		form.setLeft(200);  
		form.setCanDragResize(true);  
		form.setBorder("3px solid #0083ff");  
          


        multiValueItem = newMultiValueItem("value", "value", 150);  

		ButtonItem pasteBtnItem = new ButtonItem();
		pasteBtnItem.setStartRow(false);
		pasteBtnItem.setHeight(22);
		pasteBtnItem.setTitle("Paste Cells");
//		pasteBtnItem.setIcon(ItemFactory.newImgIcon("folder_edit_Disabled.png").getSrc());
		pasteBtnItem.setAlign(Alignment.RIGHT);
		pasteBtnItem.setColSpan(2);
		pasteBtnItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				InputStringDialog dialog = new InputStringDialog() {
					@Override
					public void onOk(String text) {
						multiValueItem.pasteText(text);
					}
				};
				dialog.draw();
			}
		});
		
		ButtonItem addBtnItem = new ButtonItem();
		addBtnItem.setStartRow(false);
		addBtnItem.setHeight(22);
		addBtnItem.setTitle("Add Cell");
		addBtnItem.setIcon("[SKIN]/actions/add.png");
		addBtnItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("value", "");
				multiValueItem.addData(record);
			}
		});

		ButtonItem okBtnItem = new ButtonItem();
		okBtnItem.setStartRow(false);
		okBtnItem.setHeight(22);
		okBtnItem.setTitle("OK");
//		okBtnItem.setIcon("[SKIN]/actions/add.png");
		okBtnItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				Map<String, Object> values = (Map<String, Object>) vm.getValues();
				if (vm.validate()) {
					System.out.println("true");
				} else {
					System.out.println("false");
				}

				form.
				if (form.validate()) {
					System.out.println("true");
				} else {
					System.out.println("false");
				}

				if (!vm.hasErrors()) {
					for (String name : values.keySet()) {
						Object val = values.get(name);
						System.out.println(val);
						for (Object m : (List)val) {
							System.out.println(((Map)m).get("value"));
						}
					}
				}

			}
		});
        
        form.setFields(multiValueItem, pasteBtnItem, addBtnItem, okBtnItem);

        VLayout layout = new VLayout();  
        layout.addMember(form);  
        layout.draw();  
		RootLayoutPanel.get().add(layout);

	}

	private abstract class InputStringDialog extends Dialog implements com.smartgwt.client.widgets.form.fields.events.ClickHandler {

		private DynamicForm form;

		private TextAreaItem textArea;
		
		public abstract void onOk(String text);

		public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
			String text = (String) this.textArea.getValue();
			onOk(text);
			this.removeItem(this.form);
			this.markForDestroy();
			this.hide();
		};

		public InputStringDialog() {

			final int WIDTH = 525;
			final int HEIGHT = 300;

			final String GUIDANCE = "Press Ctrl-V (Command-V on Mac) or right click (Option-click on Mac) to paste values, then hit \"Apply\"";

			StaticTextItem label = new StaticTextItem();
			label.setName("label");
			label.setShowTitle(false);
			label.setValue(GUIDANCE);

			TextAreaItem area = new TextAreaItem();
			area.setName("textArea");
			area.setShowTitle(false);
			area.setCanEdit(true);
			area.setHeight("*");
			area.setWidth("*");
			this.textArea = area;

			ButtonItem button = new ButtonItem();
			button.setName("apply");
			button.setAlign(Alignment.CENTER);
			button.setTitle("Apply");
			button.addClickHandler(this);

			DynamicForm form = new DynamicForm();
			form.setNumCols(1);
			form.setWidth(WIDTH);
			form.setHeight(HEIGHT);
			form.setAutoFocus(true);
			form.setFields(new FormItem[] { label, this.textArea, button });
			this.form = form;

			this.setAutoSize(true);
			this.setShowToolbar(false);
			this.setCanDragReposition(true);
			this.setTitle("Paste Cells");
			this.setShowModalMask(true);
			this.setIsModal(true);
			this.addItem(form);
		}
	};



}
