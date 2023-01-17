package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
//import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SScheduling;
import com.speno.xedm.core.service.serials.STask;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * TaskManagement Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class TaskManagementPanel extends VLayout {	
	private static TaskManagementPanel instance = null;
	
	private int perSec = 60;
	private Timer timer; 
	private ListGrid grid;
	private ListGrid grid1;
	private DynamicForm propertyForm;
	private TextItem nameItem, commandItem;
	private SelectItem typeItem, simpleItem;
	private FloatItem intervalItem, delayItem;
	private TextItem secondsItem, minutesItem, hoursItem, dayOfMonthItem, monthItem, dayOfWeekItem;
	
	private final static String[] ENABLED_ICON;
	private final static String[] STATUS_ICON;
	private final static String[] ACTION_ICON;
	
	// kimsoeun GS인증용 - 변경사항 있는지 유효성 검사
	private List oldPropertyForm = new ArrayList();
	private IsNotChangedValidator isNotChangedValidator = new IsNotChangedValidator();
	
	static {
		ENABLED_ICON = new String[2];
//		ENABLED_ICON[0] = ItemFactory.newImgIcon("bullet_green.png").getSrc();
//		ENABLED_ICON[1] = ItemFactory.newImgIcon("bullet_red.png").getSrc();
		ENABLED_ICON[0] = "bullet_green";
		ENABLED_ICON[1] = "bullet_red";
		
		STATUS_ICON = new String[3];
		STATUS_ICON[0] = new String("idle_task"); 
		STATUS_ICON[1] = new String("running_task");
		STATUS_ICON[2] = new String("stopping_task"); //임시
		
		ACTION_ICON = new String[3];
//		ACTION_ICON[0] = "[SKIN]/actions/forward.png"; 
//		ACTION_ICON[1] = ItemFactory.newImgIcon("stop.png").getSrc();
//		ACTION_ICON[2] = ItemFactory.newImgIcon("stopping.gif").getSrc(); //임시
		ACTION_ICON[0] = "forward.png"; 
		ACTION_ICON[1] = "stop.png";
		ACTION_ICON[2] = "stopping_task.gif";
	}
	
	private final static int PROPERTY_TAB = 0;
	private final static int HISTORY_TAB = 1;
	
	private int currentTab = PROPERTY_TAB;

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param subTitle
	 * @return
	 */
	public static TaskManagementPanel get(final String subTitle) {
//		if (instance == null) {
//			instance = new TaskManagementPanel(subTitle);
//		}
//		return instance;
		
		// 20140220, junsoo, 매번 갱신으로 변경. task 화면의 자동 갱신 기능이 다른화면에서도 백그라운드로 작동하는데, 이걸 막을 방법이 딱히 생각이 안나서.
		if (instance != null) {
			// 20140318, junsoo, reset timer
			instance.resetTimer();
			instance.destroy();
		}
		instance = new TaskManagementPanel(subTitle);

		return instance;
	}
	
	public TaskManagementPanel(final String subTitle) {
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		
		VLayout topVL = new VLayout();
		topVL.setWidth100();
		topVL.setAutoHeight();
		topVL.addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("system")+" > "+ I18N.message("taskmanagement"), null));
		
		if(subTitle != null) {
			/* Sub Title 생성 */
			Label subTitleLabel = new Label();
			subTitleLabel.setAutoHeight();   
			subTitleLabel.setAlign(Alignment.LEFT);   
			subTitleLabel.setValign(VerticalAlignment.CENTER);
			subTitleLabel.setStyleName("subTitle");
			subTitleLabel.setContents(I18N.message("taskmanagement"));		
			topVL.addMember(subTitleLabel);
		}
		topVL.addMember(createSearchForm());		
		
		VLayout gridVL = new VLayout();
		gridVL.setWidth100();
		gridVL.setHeight100();
		gridVL.setShowResizeBar(true);
		gridVL.addMember(createGrid());
		
		addMember(topVL);
		addMember(gridVL);
		addMember(createTabSetVL());
		
		//onChangedTypedItem();
		onChangedSimpleItem();
		
		executeFetch(false);
	}
	
	/**
	 * 상단 검색 Form 생성
	 * @return
	 */
	private DynamicForm createSearchForm() {		
		SelectItem persecItem = new SelectItem("persec", I18N.message("persec"));
      	persecItem.setWrapTitle(false);
      	persecItem.setRequired(true);
      	persecItem.setValueMap(createPersecItemOpts());   
      	persecItem.setDefaultValue(String.valueOf(perSec));
      	persecItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				resetTimer();
				perSec = Integer.parseInt((String)(event.getValue()));
				executeFetch(false);
			}
      	});
      	
      	SpacerItem dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		final ButtonItem refreshButton = new ButtonItem();
		refreshButton.setTitle(I18N.message("refresh"));
		refreshButton.setIcon("[SKIN]/actions/refresh.png");
		refreshButton.setWidth(80);
		refreshButton.setStartRow(false);
		refreshButton.setEndRow(true);
		refreshButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				resetTimer();
				executeFetch(false);
			}
        });
		
		DynamicForm searchForm = new DynamicForm();
		searchForm.setNumCols(4);
		searchForm.setColWidths("*","1","1","1");
		searchForm.setItems(persecItem, dummyItem, refreshButton);
		
		return searchForm;
	}
	
	private ListGrid createGrid() {
//		grid = new ListGrid() {
//            @Override
//            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
//            	if ("active".equals(this.getFieldName(colNum))) {            		
//                	final boolean enabled = record.getAttributeAsBoolean("active_boolean");
//                	final String staus = record.getAttributeAsString("status");
//                	
//                    ImgButton btn = new ImgButton();
//                    btn.setShowDown(false);
//                    btn.setShowRollOver(false);
//                    btn.setLayoutAlign(Alignment.CENTER);
//                    btn.setSrc(enabled ? ENABLED_ICON[0] : ENABLED_ICON[1]);
//                    btn.setPrompt(enabled ? record.getAttributeAsString("name")+" Active" : record.getAttributeAsString("name")+" Deactive");
//                    btn.setHeight(16);
//                    btn.setWidth(16);
//                    btn.addClickHandler(new ClickHandler() {
//                    	@Override
//                        public void onClick(ClickEvent event) {                    		
//                    		grid.selectSingleRecord(record);
//                    		recordClickedProcess(record);
//                    		
//            				final String name = record.getAttributeAsString("name");
//            				final String msg = enabled ? I18N.message("wanttodeactive") : I18N.message("wanttoactive");
//            				
//            				if(STATUS_ICON[0].equals(staus)) {
//            					SC.confirm( msg, new BooleanCallback() {
//                					@Override
//                					public void execute(Boolean value) {
//                						if(value != null && value) {
//                							executeActiveDeActive(name, enabled);
//                						}
//                					}
//                				});
//            				}
//            				else {
//            					SC.say(I18N.message("cantstoptask"));
//            				}
//            				event.cancel();
//                        }
//                    });
//                    
//                    
//                    
//                    HLayout recordCanvas = new HLayout();
//                    recordCanvas.setHeight(16);
//                    recordCanvas.setWidth(16);
//                    recordCanvas.setAlign(Alignment.CENTER);
//                    recordCanvas.addMember(btn);
//                    return recordCanvas;
//                }
//            	
//                if ("startstop".equals(this.getFieldName(colNum))) {
//                	final boolean enabled = record.getAttributeAsBoolean("active_boolean");    
//                	final String staus = record.getAttributeAsString("status");
//                	
//                    ImgButton btn = new ImgButton();
//                    btn.setShowDown(false);
//                    btn.setShowRollOver(false);
//                    btn.setLayoutAlign(Alignment.CENTER);
//                    
//                    btn.setSrc(
//                    		STATUS_ICON[0].equals(staus) ? 
//                    				ACTION_ICON[0] : STATUS_ICON[1].equals(staus) ? 
//                    						ACTION_ICON[1] : ACTION_ICON[2]);
//                    
//                    btn.setPrompt(
//                    		STATUS_ICON[0].equals(staus) ? 
//                    				record.getAttributeAsString("name")+" Start" : STATUS_ICON[1].equals(staus) ? 
//                    						record.getAttributeAsString("name")+" Stop" : "Stopping "+record.getAttributeAsString("name"));
//                    
//                    if(STATUS_ICON[2].equals(staus)) {
//                    	btn.disable();
//                	}
//                    
//                    if(!enabled && STATUS_ICON[0].equals(staus)) {
//                    	btn.disable();
//                    }
//                    
//                    btn.setHeight(16);
//                    btn.setWidth(16);
//                    
//                    btn.addClickHandler(new ClickHandler() {
//                    	@Override
//                        public void onClick(ClickEvent event) {
//                    		
//                    		grid.selectSingleRecord(record);
//                    		recordClickedProcess(record);
//                    		
//            				final String name = record.getAttributeAsString("name");
//            				final String status = record.getAttributeAsString("status");
//            				String msg = STATUS_ICON[0].equals(status) ? I18N.message("wanttostart") : I18N.message("wanttostop");
//            				
//            				SC.confirm( msg, new BooleanCallback() {
//            					@Override
//            					public void execute(Boolean value) {
//            						if(value != null && value) {
//            							executeStartStop(name, STATUS_ICON[0].equals(status));
//            						}
//            					}
//            				});
//            				event.cancel();
//                        }
//                    });
//                    
//                    HLayout recordCanvas = new HLayout();
//                    recordCanvas.setHeight(16);
//                    recordCanvas.setWidth(16);
//                    recordCanvas.setAlign(Alignment.CENTER);
//                    recordCanvas.addMember(btn);
//                    return recordCanvas;
//                } else {
//                    return null;
//                }
//
//            }
//        };
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		grid.setBodyOverflow(Overflow.SCROLL);
		
		grid.setShowRecordComponents(true);           
		grid.setShowRecordComponentsByCell(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		//record click event handler 정의--------------------------------------------------------------
		grid.addRecordClickHandler(new RecordClickHandler() {   
            public void onRecordClick(RecordClickEvent event) {
            	recordClickedProcess(event.getRecord());
            }   
        });
		
		//record 삭제 event handler 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				propertyForm.reset();
				propertyForm.editRecord(record);
				
				String status = record.getAttributeAsString("status");
    			if( !STATUS_ICON[0].equals(status) ) {
    				SC.say(I18N.message("cantsave"));
    				return;
    			}
				
				SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeRemove(record.getAttributeAsString("name"));
						}
					}
				});
				event.cancel();
			}
		});   
				
		ListGridField enabledField = new ListGridField("active_boolean", I18N.message("active"));
//		ListGridField activeField = new ListGridField("active", I18N.message("active"), 40);		
		
		// 20140221, junsoo
		ListGridField activeField = new ListGridField("active", I18N.message("active"), 40);
		activeField.setAlign(Alignment.CENTER);
		activeField.setType(ListGridFieldType.IMAGE);
		activeField.setImageURLPrefix(Util.imagePrefix());   
		activeField.setImageURLSuffix(".png");   
		activeField.setCanEdit(false);   
		activeField.setRequired(false);
		activeField.setHeaderTitle("");
		activeField.addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				GWT.log("active clicked");

				ListGridRecord record = event.getRecord();
				final boolean enabled = record.getAttributeAsBoolean("active_boolean");
            	final String staus = record.getAttributeAsString("status");

        		grid.selectSingleRecord(record);
        		recordClickedProcess(record);
        		
				final String name = record.getAttributeAsString("name");
				final String msg = enabled ? I18N.message("wanttodeactive") : I18N.message("wanttoactive");
				
				if(STATUS_ICON[0].equals(staus)) {
					SC.confirm( msg, new BooleanCallback() {
    					@Override
    					public void execute(Boolean value) {
    						if(value != null && value) {
    							executeActiveDeActive(name, enabled);
    						}
    					}
    				});
				}
				else {
					SC.say(I18N.message("cantstoptask"));
				}
				event.cancel();

			}
		});
		
		ListGridField statusField = new ListGridField("status", I18N.message("status"), 40);
		ListGridField nameField = new ListGridField("name", I18N.message("task"));
		ListGridField previousFireTimeField = new ListGridField("previousFireTime", I18N.message("lasteststart"), 130);
		ListGridField nextFireTimeField = new ListGridField("nextFireTime", I18N.message("nextstart"), 130);
		ListGridField schedulingLabelField = new ListGridField("schedulingLabel", I18N.message("scheduling"));
		ListGridField completionPercentageField = new ListGridField("completionPercentage", I18N.message("progress"), 110);
		
//		ListGridField startstopField = new ListGridField("startstop", I18N.message("startstop"), 80);
		ListGridField startstopField = new ListGridField("startstop", I18N.message("startstop"), 80);
		startstopField.setAlign(Alignment.CENTER);
		startstopField.setType(ListGridFieldType.IMAGE);
		startstopField.setImageURLPrefix(Util.imagePrefix());   
//		startstopField.setImageURLSuffix(".png");   
		startstopField.setCanEdit(false);   
		startstopField.setRequired(false);
		startstopField.setHeaderTitle("");
		startstopField.addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				GWT.log("startstop clicked");

				final ListGridRecord record = event.getRecord();

            	final boolean enabled = record.getAttributeAsBoolean("active_boolean");    
            	final String staus = record.getAttributeAsString("status");
//            	
//                ImgButton btn = new ImgButton();
//                btn.setShowDown(false);
//                btn.setShowRollOver(false);
//                btn.setLayoutAlign(Alignment.CENTER);
//                
//                btn.setSrc(
//                		STATUS_ICON[0].equals(staus) ? 
//                				ACTION_ICON[0] : STATUS_ICON[1].equals(staus) ? 
//                						ACTION_ICON[1] : ACTION_ICON[2]);
//                
//                btn.setPrompt(
//                		STATUS_ICON[0].equals(staus) ? 
//                				record.getAttributeAsString("name")+" Start" : STATUS_ICON[1].equals(staus) ? 
//                						record.getAttributeAsString("name")+" Stop" : "Stopping "+record.getAttributeAsString("name"));
//                
            	if(STATUS_ICON[2].equals(staus)) {
            		event.cancel();
            		return;
            	}
            	if(!enabled && STATUS_ICON[0].equals(staus)) {
            		event.cancel();
            		return;
            	}
				
        		grid.selectSingleRecord(record);
        		recordClickedProcess(record);
        		
				final String name = record.getAttributeAsString("name");
				final String status = record.getAttributeAsString("status");
				String msg = STATUS_ICON[0].equals(status) ? I18N.message("wanttostart") : I18N.message("wanttostop");
				
				SC.confirm( msg, new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							// 중지 상태이면 실행중으로 아이콘 변경
							if (STATUS_ICON[0].equals(status))
								record.setAttribute("startstop", ACTION_ICON[1]);
							// 실행중이면 중지중으로 아이콘 변경
							else if (STATUS_ICON[1].equals(status))
								record.setAttribute("startstop", ACTION_ICON[2]);
							
							int rownum = grid.getRecordIndex(record);
							if (rownum >= 0)
								grid.refreshRow(grid.getRecordIndex(record));
								
							executeStartStop(name, STATUS_ICON[0].equals(status));
						}
					}
				});
				event.cancel();

			}
		});
		
		ListGridField lastReturnField = new ListGridField("report", I18N.message("result"));
		
		ListGridField typeField = new ListGridField("type");
		ListGridField commandField = new ListGridField("command");
		ListGridField simpleField = new ListGridField("simple");
		ListGridField intervalField = new ListGridField("interval");
		ListGridField delayField = new ListGridField("delay");
		ListGridField secondsField = new ListGridField("seconds");
		ListGridField minutesField = new ListGridField("minutes");
		ListGridField hoursField = new ListGridField("hours");
		ListGridField dayOfMonthField = new ListGridField("dayOfMonth");
		ListGridField monthlField = new ListGridField("month");
		ListGridField dayofweekField = new ListGridField("dayofweek");
		
		enabledField.setHidden(true);
		typeField.setHidden(true);
		commandField.setHidden(true);
		simpleField.setHidden(true);
		intervalField.setHidden(true);
		delayField.setHidden(true);
		secondsField.setHidden(true);
		minutesField.setHidden(true);
		hoursField.setHidden(true);
		dayOfMonthField.setHidden(true);
		monthlField.setHidden(true);
		dayofweekField.setHidden(true);		
		
		enabledField.setAlign(Alignment.CENTER);
		activeField.setAlign(Alignment.CENTER);
		
		statusField.setAlign(Alignment.CENTER);
		statusField.setType(ListGridFieldType.IMAGE);
		statusField.setImageURLPrefix(Util.imagePrefix());
		statusField.setImageURLSuffix(".gif");
				
		previousFireTimeField.setType(ListGridFieldType.DATE);
		previousFireTimeField.setAlign(Alignment.CENTER);
		previousFireTimeField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		
		nextFireTimeField.setType(ListGridFieldType.DATE);
		nextFireTimeField.setAlign(Alignment.CENTER);
		nextFireTimeField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		
		completionPercentageField.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				try {
					int score = record.getAttributeAsInt("completionPercentageField");
					int red = 100 - score > 0 ? 100 - score : 0;
					String strCount = "<p>items : " + record.getAttributeAsLong("processCount") + "</p>";
					String image = "<img src='" + Util.imageUrl("dotblue.gif") + "' style='width: " + score
							+ "px; height: 8px' title='" + score + "%'/>" + "<img src='"
							+ Util.imageUrl("dotgrey.gif") + "' style='width: " + red + "px; height: 8px' title='"
							+ score + "%'/>";

					return image + strCount;
					
				} catch (Throwable e) {
					return "";
				}
			}
		});
		
		startstopField.setAlign(Alignment.CENTER);
		
		grid.setFields(enabledField, activeField, statusField, nameField, previousFireTimeField, nextFireTimeField, schedulingLabelField, completionPercentageField, startstopField, lastReturnField);
		return grid;
	}
	
	private VLayout createTabSetVL() {
		final Tab propertyTab = new Tab(I18N.message("properties"));
		propertyTab.setPane(createPropertiesVL());
		
		final DynamicForm HistoryForm = new DynamicForm();
		
		final Tab HistoriesTab = new Tab(I18N.message("histories"));
		HistoriesTab.setPane(createHistoryVL());
		
		final TabSet tabset = new TabSet();
		tabset.setTabs(propertyTab, HistoriesTab);
		tabset.addTabSelectedHandler(new TabSelectedHandler() {
			
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				Tab tab = ((TabSet)event.getSource()).getSelectedTab();
				if(tab.getTitle().equals(I18N.message("properties"))) {
					currentTab = PROPERTY_TAB;
				}
				if(tab.getTitle().equals(I18N.message("histories"))) {
					currentTab = HISTORY_TAB;

					ListGridRecord record = grid.getSelectedRecord();
					executeTaskHistory(record.getAttributeAsString("name"));
				}
			}
		});
				
        VLayout tabSetVL = new VLayout(5);
        tabSetVL.setWidth100();
        tabSetVL.setHeight("90%");
        tabSetVL.addMember(tabset);
		return tabSetVL;
	}
	
	private VLayout createHistoryVL(){
		
		// 20140210, junsoo, 삭제기능 제거
//		final ButtonItem deleteButton = new ButtonItem();
//		deleteButton.setTitle(I18N.message("delete"));
//		deleteButton.setIcon("[SKIN]/actions/remove.png");
//		deleteButton.setWidth(80);
//		deleteButton.setStartRow(false);
//		deleteButton.setEndRow(true);
//		deleteButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
//			@Override
//			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//				deleteAllTaskHistory();
//			}
//        });
		
		SpacerItem dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		// 20140210, junsoo, 삭제기능 제거
//		DynamicForm deleteForm = new DynamicForm();
//		deleteForm.setNumCols(2);
//		deleteForm.setColWidths("*","1");
//		deleteForm.setItems(dummyItem, deleteButton);
		
		grid1 = new ListGrid();
		grid1.setMargin(2);
		grid1.setWidth100();
		grid1.setHeight100();		
		grid1.setShowAllRecords(true);
		grid1.setEmptyMessage(I18N.message("notitemstoshow"));
	        
		grid1.setCanFreezeFields(true);
		grid1.setCanRemoveRecords(false);
		
		ListGridField startField = new ListGridField("start", I18N.message("start")+I18N.message("hours"));
		ListGridField endField = new ListGridField("end", I18N.message("close")+I18N.message("hours"));
		ListGridField resultField = new ListGridField("result", I18N.message("result"));
		grid1.setFields(startField, endField, resultField);
		grid1.sort("start", SortDirection.DESCENDING);
		
		VLayout gridVL = new VLayout();
		gridVL.setHeight100();
		gridVL.setWidth100();
		gridVL.setMembers(grid1);
		
		return gridVL;
	}
	
	private VLayout createPropertiesVL() {	
		
        //Properties-------------------------------------------------------------------------------------------------------
        nameItem = new TextItem("name", I18N.message("taskname"));
        typeItem = new SelectItem("type",I18N.message("type"));
        //commandItem = new TextItem("command");
        commandItem = new TextItem("command", I18N.message("classcommand"));
        simpleItem = new SelectItem("simple", I18N.message("schedulemode"));
        intervalItem = new FloatItem("interval", I18N.message("intervaltimesec"));
        delayItem = new FloatItem("delay", I18N.message("startdelaytimesec"));        
        secondsItem = new TextItem("seconds", I18N.message("seconds"));
        minutesItem = new TextItem("minutes", I18N.message("minutes"));
        hoursItem = new TextItem("hours", I18N.message("hours"));
        dayOfMonthItem = new TextItem("dayOfMonth", I18N.message("dayofmonth"));
        monthItem = new TextItem("month", I18N.message("month"));
        dayOfWeekItem = new TextItem("dayOfWeek", I18N.message("dayofweek"));
        //------------------------------------------------------------------------------------------------------------------
        
        nameItem.setRequired(true);
        nameItem.disable();
        
        typeItem.setRequired(true);
        typeItem.setValueMap(createTypeItemOpts());   
        typeItem.setDefaultValue(STask.TYPE_CLASS);
        typeItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				//onChangedTypedItem();
			}
        });
        // kimsoeun GS인증용 - 유형 항목 숨김
        typeItem.hide();
        
        commandItem.setRequired(true);
        commandItem.setWrapTitle(false);
        // kimsoeun GS인증용 - 클래스/명령 항목 숨김
        commandItem.hide();
        
        simpleItem.setRequired(true);
        simpleItem.setValueMap(createSimpleItemOpts());
        simpleItem.setDefaultValue("true");        
        simpleItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				onChangedSimpleItem(	);
			}
        });
       
        intervalItem.setRequired(true);
        delayItem.setRequired(true);
        secondsItem.setRequired(true);
        minutesItem.setRequired(true);
        hoursItem.setRequired(true);
        dayOfMonthItem.setRequired(true);
        monthItem.setRequired(true);
        dayOfWeekItem.setRequired(true);
        
        // kimsoeun GS인증용 - 툴팁 다국어화
        simpleItem.setRequiredMessage(I18N.message("fieldisrequired"));
        intervalItem.setRequiredMessage(I18N.message("fieldisrequired"));
        delayItem.setRequiredMessage(I18N.message("fieldisrequired"));
        secondsItem.setRequiredMessage(I18N.message("fieldisrequired"));
        minutesItem.setRequiredMessage(I18N.message("fieldisrequired"));
        hoursItem.setRequiredMessage(I18N.message("fieldisrequired"));
        dayOfMonthItem.setRequiredMessage(I18N.message("fieldisrequired"));
        monthItem.setRequiredMessage(I18N.message("fieldisrequired"));
        dayOfWeekItem.setRequiredMessage(I18N.message("fieldisrequired"));
        
        nameItem.setValidators(new LengthValidator(nameItem, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
//        commandItem.setLength(Session.get().getInfo().getIntConfig("gui.command.fieldsize", 255));
        commandItem.setValidators(new LengthValidator(commandItem, Session.get().getInfo().getIntConfig("gui.command.fieldsize", 255)));
        secondsItem.setLength(20);
        secondsItem.setTooltip(I18N.message("cron.second"));
        secondsItem.setHoverWidth(150);
//        secondsItem.setValidators(new LengthValidator(secondsItem,2));
        minutesItem.setLength(20);
        minutesItem.setTooltip(I18N.message("cron.minute"));
        minutesItem.setHoverWidth(150);
//        minutesItem.setValidators(new LengthValidator(minutesItem,2));
        hoursItem.setLength(20);
        hoursItem.setTooltip(I18N.message("cron.hour"));
        hoursItem.setHoverWidth(150);
//        hoursItem.setValidators(new LengthValidator(hoursItem,2));
        dayOfMonthItem.setLength(20);
        dayOfMonthItem.setTooltip(I18N.message("cron.day"));
        dayOfMonthItem.setHoverWidth(180);
//        dayOfMonthItem.setValidators(new LengthValidator(dayOfMonthItem,2));
        monthItem.setLength(20);
        monthItem.setTooltip(I18N.message("cron.month"));
        monthItem.setHoverWidth(180);
//        monthItem.setValidators(new LengthValidator(monthItem,2));
        dayOfWeekItem.setLength(20);
        dayOfWeekItem.setTooltip(I18N.message("cron.dayweek"));
        dayOfWeekItem.setHoverWidth(180);
//        dayOfWeekItem.setValidators(new LengthValidator(dayOfWeekItem,2));
        
        simpleItem.setStartRow(true);
        simpleItem.setEndRow(true);
        simpleItem.setHoverWidth(200);
        
        delayItem.setStartRow(false);
        delayItem.setEndRow(true);
        delayItem.setKeyPressFilter("[0-9.]");
        delayItem.setValidators(new LengthValidator(delayItem,30));
        
        intervalItem.setKeyPressFilter("[0-9.]");
        intervalItem.setValidators(new LengthValidator(intervalItem,30));
        
        propertyForm = new DynamicForm();
               
        propertyForm.setWrapItemTitles(false);
		propertyForm.setNumCols(6);
		
		propertyForm.setColWidths("120","1","120","1","120","*");
		
		propertyForm.setFields(
				nameItem, typeItem, commandItem,
				simpleItem,
				intervalItem, delayItem,
				secondsItem, minutesItem, hoursItem,
				dayOfMonthItem, monthItem, dayOfWeekItem);
        
		VLayout propertiesVL = new VLayout(10);
        propertiesVL.setWidth100();
        propertiesVL.setHeight100();
        propertiesVL.addMember(propertyForm);
        propertiesVL.addMember(createPropertiesTabPanel());
		return propertiesVL;
	}
	
	private HLayout createPropertiesTabPanel() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {            	
            	addNew();
            }   
        });
		// kimsoeun GS인증용 - 신규 추가 버튼 숨김
		btnAddNew.hide();
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(propertyForm.validate()) {
            		RecordList recordlist = grid.getRecordList();
            		final Record record = recordlist.find("name", propertyForm.getValueAsString("name"));
            		
            		if(!propertyForm.getField("name").isDisabled()) { //Add New 상태
            			if( record != null) {
            				
            				String status = record.getAttributeAsString("status");
                			if( !STATUS_ICON[0].equals(status) ) {
                				SC.say(I18N.message("cantsave"));
                				return;
                			}
            				
            				
        					SC.confirm(I18N.message("itemoverwrite"),  new BooleanCallback() {
        						@Override
        						public void execute(Boolean value) {
        							if(value != null && value) {
        								executeSave();
        							}
        						}
        					});
            			}
            			else {
            				SC.confirm(I18N.message("wanttosave"),  new BooleanCallback() {
            					@Override
            					public void execute(Boolean value) {
            						if(value != null && value) {
            							executeSave();
            						}
            					}
            				});
            			}
        			}
            		else {
            			
            			if( record != null) {
            				String status = record.getAttributeAsString("status");
                			if( !STATUS_ICON[0].equals(status) ) {
                				SC.say(I18N.message("cantsave"));
                				return;
                			}
            			}
            			
            			// kimsoeun GS인증용 - 변경사항 여부 확인
            			int changed = isNotChangedValidator.check(propertyForm, oldPropertyForm);
            			if(changed==propertyForm.getFields().length) {
            				SC.say(I18N.message("nothingchanged"));
            				return;
            			}
            			
            			SC.confirm(I18N.message("wanttosave"),  new BooleanCallback() {
        					@Override
        					public void execute(Boolean value) {
        						if(value != null && value) {
        							executeSave();
        						}
        					}
        				});
            		}
            	}
			}
        });
		
		Button btnCancel = new Button(I18N.message("cancel"));
		btnCancel.setIcon(ItemFactory.newImgIcon("generic.png").getSrc());
		btnCancel.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
    			if(!propertyForm.getField("name").isDisabled()) { //Add New 상태
    				addNew();
    			}
    			else {
    				String name = (String)propertyForm.getField("name").getValue();
    				ListGridRecord record = grid.getRecord(grid.getRecordList().findIndex("name", name));
    				
    				if(record != null) {
    					recordClickedProcess(record);
    				}
    				else {
    					addNew();
    				}
    				
    			}
            }   
        });
		
		HLayout actionHL = new HLayout(10);
		actionHL.setAutoHeight();
		actionHL.setMembers(btnAddNew, btnSave, btnCancel);		
		return actionHL;
	}
	
	private void setTimer(int sec) {
		// 20140318, junsoo, reset timer
		resetTimer();
		
		timer = new Timer() {
			public void run() {
				executeFetchByTimer();
			}
		};
		timer.scheduleRepeating(sec * 1000);
	}
	
	private void resetTimer() {
		if( timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	/*
	private void onChangedTypedItem() {
		//commandItem.clearValue();
		commandItem.setTitle(typeItem.getDisplayValue());
		commandItem.redraw();
	}
	*/
	
	private void onChangedSimpleItem() {		
		Boolean value = Boolean.parseBoolean(simpleItem.getValueAsString());
		
		intervalItem.setRequired(value);
		delayItem.setRequired(value);
		
		secondsItem.setRequired(!value);
		minutesItem.setRequired(!value);
		hoursItem.setRequired(!value);
		dayOfMonthItem.setRequired(!value);
		monthItem.setRequired(!value);
		dayOfWeekItem.setRequired(!value);
		
		if(value) {
			simpleItem.setTooltip("");
			intervalItem.show();
			delayItem.show();
			
			secondsItem.hide();
			minutesItem.hide();
			hoursItem.hide();
			dayOfMonthItem.hide();
			monthItem.hide();
			dayOfWeekItem.hide();
		}
		else {
			simpleItem.setTooltip(I18N.message("cron"));
			intervalItem.hide();
			delayItem.hide();
			
			secondsItem.show();
			minutesItem.show();
			hoursItem.show();
			dayOfMonthItem.show();
			monthItem.show();
			dayOfWeekItem.show();
		}
	}
	
	private void refreshTask(final String name) {
		ServiceUtil.system().getTaskByName(Session.get().getSid(), name, I18N.getLocale(), new AsyncCallback<STask>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, true);
			}
			@Override
			public void onSuccess(STask result) {
				ListGridRecord[] records = grid.getRecords();
				for (ListGridRecord record : records) {
					if (name.equals(record.getAttribute("name"))){
						SScheduling scheduling = result.getScheduling();
						
						record.setAttribute("active_boolean", scheduling.isEnabled());
						
						if (scheduling.isEnabled())
							record.setAttribute("active", ENABLED_ICON[0]);
						else
							record.setAttribute("active", ENABLED_ICON[1]);
						
						record.setAttribute("status", STATUS_ICON[result.getStatus()]);
						
						record.setAttribute("startstop", ACTION_ICON[result.getStatus()]);

						record.setAttribute("name", result.getName());
						record.setAttribute("previousFireTime", scheduling.getPreviousFireTime());
						record.setAttribute("nextFireTime", scheduling.getNextFireTime());
						record.setAttribute("schedulingLabel", result.getSchedulingLabel());
						record.setAttribute("completionPercentageField", result.getCompletionPercentage());
						record.setAttribute("size", result.getSize());
						record.setAttribute("progress", result.getProgress());
						record.setAttribute("processCount", result.getProcessCount());
						record.setAttribute("report",  result.getExitMessage());
						
						record.setAttribute("type",  result.getType());
						record.setAttribute("command",  result.getCommand());
						record.setAttribute("simple",  String.valueOf(scheduling.isSimple()));
						record.setAttribute("interval",  scheduling.getInterval());
						record.setAttribute("delay",  scheduling.getDelay());
						record.setAttribute("seconds",  scheduling.getSeconds());
						record.setAttribute("minutes",  scheduling.getMinutes());
						record.setAttribute("hours",  scheduling.getHours());
						record.setAttribute("dayOfMonth",  scheduling.getDayOfMonth());
						record.setAttribute("month",  scheduling.getMonth());
						record.setAttribute("dayOfWeek",  scheduling.getDayOfWeek());
						
						grid.refreshRow(grid.getRecordIndex(record));
					}
				}
			}			
		});
	}
	
    private void executeFetch(final boolean iscorrecFocus) {
    	resetTimer();
    	
    	ServiceUtil.system().loadTasks(Session.get().getSid(), I18N.getLocale(), new AsyncCallbackWithStatus<STask[]>() {
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
			public void onSuccessEvent(STask[] result) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
				SScheduling scheduling;
				for (int j = 0; j < result.length; j++) {
					scheduling = result[j].getScheduling();
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("active_boolean", scheduling.isEnabled());

					if (scheduling.isEnabled())
						record.setAttribute("active", ENABLED_ICON[0]);
					else
						record.setAttribute("active", ENABLED_ICON[1]);

					record.setAttribute("status", STATUS_ICON[result[j].getStatus()]);
					
					record.setAttribute("startstop", ACTION_ICON[result[j].getStatus()]);
					
					record.setAttribute("name", result[j].getName());
					record.setAttribute("previousFireTime", scheduling.getPreviousFireTime());
					record.setAttribute("nextFireTime", scheduling.getNextFireTime());
					record.setAttribute("schedulingLabel", result[j].getSchedulingLabel());
					record.setAttribute("completionPercentageField", result[j].getCompletionPercentage());
					record.setAttribute("size", result[j].getSize());
					record.setAttribute("progress", result[j].getProgress());
					record.setAttribute("processCount", result[j].getProcessCount());
					record.setAttribute("report",  result[j].getExitMessage());
					
					record.setAttribute("type",  result[j].getType());
					record.setAttribute("command",  result[j].getCommand());
					record.setAttribute("simple",  String.valueOf(scheduling.isSimple()));
					record.setAttribute("interval",  scheduling.getInterval());
					record.setAttribute("delay",  scheduling.getDelay());
					record.setAttribute("seconds",  scheduling.getSeconds());
					record.setAttribute("minutes",  scheduling.getMinutes());
					record.setAttribute("hours",  scheduling.getHours());
					record.setAttribute("dayOfMonth",  scheduling.getDayOfMonth());
					record.setAttribute("month",  scheduling.getMonth());
					record.setAttribute("dayOfWeek",  scheduling.getDayOfWeek());
					
					grid.addData(record);
				}
				
				if(iscorrecFocus) {					
					RecordList recordlist = grid.getRecordList();
					String name = (String)propertyForm.getField("name").getValue();
					int rowNum = recordlist.findIndex("name", name);
					if(rowNum >= 0) {
						grid.selectSingleRecord(rowNum);
						recordClickedProcess(grid.getRecord(rowNum));
					}
				}
				else {
			    	addNew();
					if (result.length > 0) {
						grid.selectSingleRecord(0);
						recordClickedProcess(grid.getRecord(0));
					}
				}
				
				setTimer(perSec);
				
				GWT.log("[ TaskManagementPanel executeFetch ] result.length["+result.length+"]", null);
			}
		});
    }
    
    private void executeFetchByTimer() {
    	ServiceUtil.system().loadTasks(Session.get().getSid(), I18N.getLocale(), new AsyncCallbackWithStatus<STask[]>() {
    		@Override
			public String getSuccessMessage() {
				return "";
			}
			@Override
			public String getProcessMessage() {
				return "";
			}
    		@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
				resetTimer();
			}
			@Override
			public void onSuccessEvent(STask[] result) {
				RecordList recordlist = grid.getRecordList();
				if (recordlist == null)
					return;
				
				SScheduling scheduling;
				for (int j = 0; j < result.length; j++) {
					scheduling = result[j].getScheduling();
					
					ListGridRecord record = new ListGridRecord();					
					record.setAttribute("active_boolean", scheduling.isEnabled());					
					if (scheduling.isEnabled())
						record.setAttribute("active", ENABLED_ICON[0]);
					else
						record.setAttribute("active", ENABLED_ICON[1]);
					record.setAttribute("status", STATUS_ICON[result[j].getStatus()]);

					record.setAttribute("startstop", ACTION_ICON[result[j].getStatus()]);

					record.setAttribute("name", result[j].getName());
					record.setAttribute("previousFireTime", scheduling.getPreviousFireTime());
					record.setAttribute("nextFireTime", scheduling.getNextFireTime());
					record.setAttribute("schedulingLabel", result[j].getSchedulingLabel());
					record.setAttribute("completionPercentageField", result[j].getCompletionPercentage());
					record.setAttribute("size", result[j].getSize());
					record.setAttribute("progress", result[j].getProgress());
					record.setAttribute("processCount", result[j].getProcessCount());
					record.setAttribute("report",  result[j].getExitMessage());
					
					record.setAttribute("type",  result[j].getType());
					record.setAttribute("command",  result[j].getCommand());
					record.setAttribute("simple",  String.valueOf(scheduling.isSimple()));
					record.setAttribute("interval",  scheduling.getInterval());
					record.setAttribute("delay",  scheduling.getDelay());
					record.setAttribute("seconds",  scheduling.getSeconds());
					record.setAttribute("minutes",  scheduling.getMinutes());
					record.setAttribute("hours",  scheduling.getHours());
					record.setAttribute("dayOfMonth",  scheduling.getDayOfMonth());
					record.setAttribute("month",  scheduling.getMonth());
					record.setAttribute("dayOfWeek",  scheduling.getDayOfWeek());
					
					int rowNum = recordlist.findIndex("name", result[j].getName());
					
					if(rowNum < 0) {
						grid.addData(record);
					}
					else {
						recordlist.set(rowNum, record);						
					}
				}
				
				String name = (String)propertyForm.getField("name").getValue();
				int rowNum = recordlist.findIndex("name", name);
				
				if(rowNum >= 0) {
					grid.selectSingleRecord(rowNum);
				}
				
			//	GWT.log("[ TaskManagementPanel executeFetchByTimer ] result.length["+result.length+"]", null);
			}
		});
    }
    
    private void executeSave() {
		GWT.log("[ TaskManagementPanel executeSave ]", null);
		
		final String name = propertyForm.getValueAsString("name");
		final boolean simple = Boolean.valueOf(propertyForm.getValueAsString("simple"));
		
		SScheduling scheduling = new SScheduling();
		
		if(simple) {
			scheduling.setSimple(			simple);
			scheduling.setInterval(			Long.parseLong(propertyForm.getValueAsString("interval")));
			scheduling.setDelay(			Long.parseLong(propertyForm.getValueAsString("delay")));
		}
		else {
			scheduling.setSimple(			simple);
			scheduling.setSeconds(			propertyForm.getValueAsString("seconds"));
			scheduling.setMinutes(			propertyForm.getValueAsString("minutes"));
			scheduling.setHours(			propertyForm.getValueAsString("hours"));
			scheduling.setDayOfMonth(	propertyForm.getValueAsString("dayOfMonth"));
			scheduling.setMonth(			propertyForm.getValueAsString("month"));
			scheduling.setDayOfWeek(		propertyForm.getValueAsString("dayOfWeek"));
		}
		
		STask task = new STask();
		task.setName(		name);
		task.setType(			propertyForm.getValueAsString("type"));
		task.setCommand(	propertyForm.getValueAsString("command"));
		task.setScheduling(	scheduling);
		
		ServiceUtil.system().saveTask(Session.get().getSid(), task, I18N.getLocale(), new AsyncCallbackWithStatus<STask>() {
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
			public void onSuccessEvent(STask result) {	
				GWT.log("[ TaskManagementPanel executeSave ] onSuccess. name["+result.getName()+"]", null);
//				SC.say(I18N.message("operationcompleted"));
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
//				executeFetch(true);
				refreshTask(name);
				
				// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
				isNotChangedValidator.setList(propertyForm, oldPropertyForm);
			}
		});
	}
    
    private void executeActiveDeActive(final String name, final Boolean isActive) {
    	GWT.log("[ TaskManagementPanel executeActiveDeActive ] isActive["+isActive+"]", null);
    	
    	if(isActive) {
    		ServiceUtil.system().disableTask(Session.get().getSid(), name, new AsyncCallbackWithStatus<Boolean>() {		
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
    			public void onSuccessEvent(Boolean result) {
    				GWT.log("[ TaskManagementPanel executeActiveDeActive ] disableTask result["+result+"]", null);
//    				SC.say(I18N.message("operationresult", result ? " Completed" : " Failure"));
//    				executeFetch(true);
    				refreshTask(name);
//    				executeTaskHistory(name);

    			}
    		});
    	}
    	else {
    		ServiceUtil.system().enableTask(Session.get().getSid(), name, new AsyncCallbackWithStatus<Boolean>() {		
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
    			public void onSuccessEvent(Boolean result) {
    				GWT.log("[ TaskManagementPanel executeActiveDeActive ] enableTask result["+result+"]", null);
    				//SC.say(I18N.message("operationresult", result ? " Completed" : " Failure"));
//    				executeFetch(true);
    				refreshTask(name);
//    				executeTaskHistory(name);
    			}
    		});
    	}
    }
    
    private void executeStartStop(final String name, final Boolean isStart) {
    	GWT.log("[ TaskManagementPanel executeStartStop ] isStart["+isStart+"]", null);
    	
    	if(isStart) {
    		ServiceUtil.system().startTask(name, new AsyncCallbackWithStatus<Boolean>() {		
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
    			public void onSuccessEvent(Boolean result) {
    				GWT.log("[ TaskManagementPanel executeStartStop ] startTask result["+result+"]", null);
//    				SC.say(I18N.message("operationresult", result ? " Completed" : " Failure"));
//    				executeFetch(true);
//    				refreshTask(name);
    				
    				refreshTaskWithIdleTime(name, 2000);
    			}
    		});
    	}
    	else {
    		ServiceUtil.system().stopTask(name, new AsyncCallbackWithStatus<Boolean>() {		
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
    			public void onSuccessEvent(Boolean result) {
    				GWT.log("[ TaskManagementPanel executeStartStop ] stopTask result["+result+"]", null);
//    				SC.say(I18N.message("operationresult", result ? " Completed" : " Failure"));
//    				executeFetch(true);
//    				refreshTask(name);

    				refreshTaskWithIdleTime(name, 2000);
    			}
    		});
    	}
    }
    
    private void refreshTaskWithIdleTime(final String name, int time) {
		Timer timer = new Timer() {
			public void run() {
				refreshTask(name);
				
				if (currentTab == HISTORY_TAB)
					executeTaskHistory(name);

				this.cancel();
			}
		};
		timer.scheduleRepeating(time);
    }
    
	private void executeRemove(final String name)	{
		GWT.log("[ TaskManagementPanel executeRemove ] name["+name+"]", null);

		SC.confirm(I18N.message("docpanedelmsg"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					STask task = new STask();
					task.setName(name);
					
					ServiceUtil.system().removeTask(Session.get().getSid(), task, new AsyncCallbackWithStatus<Void>() {		
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
							GWT.log("[ TaskManagementPanel executeRemove ] onSuccess.", null);
							SC.say(I18N.message("operationcompleted"));
							executeFetch(false);
						}
					});
				} else {
					return;
				}
			}
		});
	}
    
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		
		propertyForm.reset();
		propertyForm.editRecord(record);
		
		// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
		isNotChangedValidator.setList(propertyForm, oldPropertyForm);
		
		//onChangedTypedItem();
		onChangedSimpleItem();
		nameItem.disable();
		
		if (currentTab == HISTORY_TAB)
			executeTaskHistory(record.getAttributeAsString("name"));
	}
	
	private void executeTaskHistory(final String name)	{
		GWT.log("[ TaskManagementPanel executeTaskHistory ] name["+name+"]", null);

		grid1.setData(new ListGridRecord[0]); //그리드 초기화

		ServiceUtil.system().listTaskHistory(Session.get().getSid(), name, new AsyncCallbackWithStatus<List<Map<String,String>>>() {		
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
			public void onSuccessEvent(List<Map<String,String>> result) {
				GWT.log("[ TaskManagementPanel executeTaskHistory ] onSuccess.", null);
				
				Map<String,String> resultMap;
				for (int j = 0; j < result.size(); j++) {
//					for (int j = result.size()-1; j > -1; j--) {
					resultMap = result.get(j);
					
					ListGridRecord record = new ListGridRecord();					
					record.setAttribute("start", resultMap.get("start"));		
					record.setAttribute("duration", resultMap.get("duration"));
					record.setAttribute("result", I18N.message(resultMap.get("result")));
					
					grid1.addData(record);
				}
			}
		});

	}
	
//	private void deleteAllTaskHistory()	{
//		String name = grid.getSelectedRecord().getAttributeAsString("name").replaceAll(" ", "");
//		GWT.log("[ TaskManagementPanel deleteAllTaskHistory ] name["+name+"]", null);
//		
//		ServiceUtil.security().deleteAllTaskHistory(Session.get().getSid(), name, new AsyncCallbackWithStatus<Void>() {		
//			@Override
//			public String getSuccessMessage() {
//				return I18N.message("operationcompleted");
//			}
//			@Override
//			public String getProcessMessage() {
//				return null;
//			}
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				SCM.warn(caught);
//			}
//			@Override
//			public void onSuccessEvent(Void result) {
//				GWT.log("[ TaskManagementPanel deleteAllTaskHistory ] onSuccess.", null);
//				grid1.setData(new ListGridRecord[0]); //그리드 초기화
//				SC.say(I18N.message("operationcompleted"));
//			}
//		});
//	}
	
	/**
	 * Add New 버튼의 클릭 이벤트 핸들러
	 */
	private void addNew() {
		propertyForm.editNewRecord();
		propertyForm.reset();
    	grid.deselectAllRecords();
    	nameItem.enable();
    	
    	//onChangedTypedItem();
		onChangedSimpleItem();
	}
	
	private LinkedHashMap<String, String> createPersecItemOpts() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("1", "1");
		valueMap.put("2", "2");
		valueMap.put("3", "3");
		valueMap.put("4", "4");
		valueMap.put("5", "5");
		valueMap.put("10", "10");
		valueMap.put("30", "30");
		valueMap.put("60", "60");
		return valueMap;
	}
	
	private LinkedHashMap<String, String> createTypeItemOpts() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(STask.TYPE_CLASS, "Class");
		valueMap.put(STask.TYPE_SHELL, "Executable Command");
		return valueMap;
	}
	
	private LinkedHashMap<String, String> createSimpleItemOpts() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("true", "Simple");
		valueMap.put("false", "Cron");
		return valueMap;
	}
}