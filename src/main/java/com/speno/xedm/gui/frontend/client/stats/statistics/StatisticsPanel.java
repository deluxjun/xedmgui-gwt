package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SStatSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog.ResultHandler;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Statistics Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class StatisticsPanel extends VLayout {	
	private static StatisticsPanel instance = null;
	
	private VLayout docInfoVL;
	private DynamicForm searchForm, chartForm, gridForm;	
	private HLayout chartHL;
	private HLayout gridHL;
	
	private SpacerItem dummyItem;
	private ListGrid docInfoGrid;	
	
	private ListGridField userIdField;
	private ListGridField userNameField;
	private ListGridField groupDepartmentField;
	
	private StatisticsWidget lineWidget;
	private GridWidget gridWidget;

	private Tab chartTab;
	
	private SelectItem actionItem;
	private SelectItem xAchseItem, viewItemItem;
	private SelectItem column1Item, column2Item, column3Item, column4Item;
	private LinkedHashMap<String, String> statOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<String, String> statOpts2 = new LinkedHashMap<String, String>() ;
	
	private TabSet tabSet;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static StatisticsPanel get() {
		if (instance == null) {
			instance = new StatisticsPanel();
		}
		return instance;
	}

	/**
	 * Statistics Panel 생성
	 */
	public StatisticsPanel() {
		dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);		
		addMember(new TrackPanel(I18N.message("statistics") + " > " + I18N.message("statistics") + " > " + I18N.message("edmStatistics"), null));
		addMember(createDocInfoVL());
		
		executeGetOptionsAndSet();
	}
	
	/**
	 * 문서정보 패널 생성
	 */
	private VLayout createDocInfoVL() {
		/*
		Label docInfoLabel = new Label();   
		docInfoLabel.setHeight(20);   
		docInfoLabel.setAlign(Alignment.LEFT);   
		docInfoLabel.setValign(VerticalAlignment.BOTTOM);   
		docInfoLabel.setWrap(false);   
		docInfoLabel.setStyleName("subTitle");
		docInfoLabel.setContents(I18N.message("documentinfo"));
		*/
		
		docInfoVL = new VLayout();
		docInfoVL.setMembersMargin(5);
		docInfoVL.setHeight100();
		//docInfoVL.setMembers(docInfoLabel, createSearchForm(), createChartVL(), createGridVL());
//		docInfoVL.setMembers(createSearchForm(), createChartVL(), createGridVL());
		docInfoVL.setMembers(createSearchForm(), createTabSet());
		
		return docInfoVL;
	}
	
	private TabSet createTabSet(){
		tabSet = new TabSet();

		Tab gridTab = new Tab(I18N.message("viewtypegrid"));
		gridTab.setName("gridTab");
		gridTab.setPane(createGridVL());
		
		chartTab = new Tab(I18N.message("viewtypechart"));
		chartTab.setName("chartTab");
		chartTab.setPane(createChartVL());

		if(Session.get().getInfo().getConfig("settings.connect.exNetwork").equals("true")){
			tabSet.setTabs(chartTab, gridTab);
		}else{
			tabSet.setTabs(gridTab);
		}
		
		return tabSet;
	}
	/**
	 * 상단 검색 Form 생성
	 * @return
	 */
	private DynamicForm createSearchForm() {		
		//시작일자
		final DateItem fromItem = new DateItem("from", I18N.message("from"));
		fromItem.setWrapTitle(false);
		fromItem.setRequired(true);
		fromItem.setShowTitle(false);
		
		StaticTextItem commonDateColumn = new StaticTextItem();
		commonDateColumn.setValue("~");
		commonDateColumn.setWidth(7);
		commonDateColumn.setAlign(Alignment.CENTER);
		commonDateColumn.setShowTitle(false);
		commonDateColumn.setStartRow(false);
		commonDateColumn.setEndRow(false);
		
		//종료일자
		final DateItem toItem = new DateItem("to", I18N.message("to"));
		toItem.setWrapTitle(false);
		toItem.setRequired(true);
		toItem.setShowTitle(false);
		
		//event
		actionItem = new SelectItem("event", I18N.message("event"));
		actionItem.setWidth("*");
		actionItem.setWrapTitle(false);
		actionItem.setPickListWidth(56);
		
		ChangedHandler ch = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
					DateItem startDayItem = (DateItem) searchForm.getItem("from");
					DateItem expiredDayItem = (DateItem) searchForm.getItem("to");
					
					//20131216 na 오늘 이전날짜와 비교를 해야 당일도 잠금을 선택할 수 있음.
					Date startDay = startDayItem.getValueAsDate();
					Date expiredDay = expiredDayItem.getValueAsDate();
					Date today = new Date();
					
					if(startDay.after(expiredDay)){
						SC.warn(I18N.message("youcantchooseday"));
						startDayItem.setValue(today);
						expiredDayItem.setValue(today);
					}
				}
		};
		
		//20131204na 이전일자 클릭시 팝업
		fromItem.addChangedHandler(ch);
		toItem.addChangedHandler(ch);
		
        //형식 id
        HiddenItem typeIdItem = new HiddenItem("typeId");
        
        //형식(삭제) = 전체
        PickerIcon typeClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	searchForm.getField("typeId").clearValue();
				searchForm.getField("typeName").clearValue();
            }   
        });
		
        //형식(검색) = 지정
		PickerIcon typeSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	final CommSearchDialog commSearchDialog = new CommSearchDialog(CommSearchDialog.TYPE);
				commSearchDialog.addResultHandler(new ResultHandler() {
					@Override
					public void onSelected(HashMap<String, String> resultMap) {						
						searchForm.getField("typeId").setValue(resultMap.get("id"));
						searchForm.getField("typeName").setValue(resultMap.get("name"));
					}
				});
				commSearchDialog.show();
            }   
        });
		
		//형식명
		TextItem typeNameItem = new TextItem("typeName", I18N.message("type"));
		typeNameItem.setWrapTitle(false);
		typeNameItem.setCanEdit(false);
		typeNameItem.setEmptyDisplayValue(I18N.message("all"));
		typeNameItem.setIcons(typeClearPicker, typeSearchPicker);
		typeNameItem.setDisableIconsOnReadOnly(false);
		typeNameItem.setWidth(100);
		
		//사용자 id
		HiddenItem userIdItem = new HiddenItem("userId");
        
        //사용자(삭제) = 전체
        PickerIcon userClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	searchForm.getField("userId").clearValue();
				searchForm.getField("userName").clearValue();
            }   
        });
		
        //사용자(검색) = 지정
		PickerIcon userSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	final CommSearchDialog commSearchDialog = new CommSearchDialog(CommSearchDialog.USERGROUP, I18N.message("usersgroup"));
				commSearchDialog.addResultHandler(new ResultHandler() {
					@Override
					public void onSelected(HashMap<String, String> resultMap) {						
						searchForm.getField("userId").setValue(resultMap.get("id"));
						searchForm.getField("userName").setValue(resultMap.get("name"));
					}
				});
				commSearchDialog.show();
            }   
        });
		
		//사용자명
		TextItem userNameItem = new TextItem("userName", I18N.message("user"));
		userNameItem.setWrapTitle(false);
		userNameItem.setCanEdit(false);
		userNameItem.setEmptyDisplayValue(I18N.message("all"));
		userNameItem.setIcons(userClearPicker, userSearchPicker);
		userNameItem.setDisableIconsOnReadOnly(false);
		userNameItem.setWidth(100);
		
		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));	
		searchButton.setStartRow(false);
		searchButton.setIcon("[SKIN]/actions/search.png");		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				boolean validSearch = searchForm.validate(); 
				boolean validChart = chartForm.validate();
				boolean validGrid = gridForm.validate();
				if( validSearch && validChart && validGrid) {
					executeChartFetch();
					executeGridFetch();
				}
			}
        });
		
		searchForm = new DynamicForm();
		searchForm.setWidth100();
		searchForm.setAutoHeight();		
		searchForm.setNumCols(19);
		searchForm.setColWidths("*","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1");
		searchForm.setItems(dummyItem, fromItem, dummyItem, commonDateColumn, dummyItem, toItem, dummyItem, actionItem, dummyItem, 
				typeIdItem, typeNameItem, dummyItem, userIdItem, userNameItem, dummyItem, searchButton);
		
		return searchForm;
	}
	
	private DynamicForm createChartForm() {
		
		//X축 기준
		xAchseItem = new SelectItem("x", I18N.message("x-Achse"));
		xAchseItem.setWrapTitle(false);
		xAchseItem.setRequired(true);
		xAchseItem.setEmptyDisplayValue(I18N.message("choosetype"));
		xAchseItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String y = viewItemItem.getValueAsString();
				if( y != null) {
					String x = xAchseItem.getValueAsString();
					if( !(x.indexOf("regDate")<0) && !(y.indexOf("regDate")<0) ) {
						viewItemItem.clearValue();
					}
					else if(x.equals(y)) {
						viewItemItem.clearValue();
					}
				}
			}
		});
        
        //표시항목
      	viewItemItem = new SelectItem("viewitem", I18N.message("viewitem"));
      	viewItemItem.setWrapTitle(false);
      	viewItemItem.setRequired(true);
      	viewItemItem.setEmptyDisplayValue(I18N.message("choosetype"));
      	viewItemItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String x = xAchseItem.getValueAsString();
				if( x != null) {
					String y = viewItemItem.getValueAsString();
					if( !(y.indexOf("regDate")<0) && !(x.indexOf("regDate")<0) ) {
						xAchseItem.clearValue();
					}
					else if(y.equals(x)) {
						xAchseItem.clearValue();
					}
				}
			}
		});
      	
      	SelectItem viewPointItem = new SelectItem("viewpoint", I18N.message("viewpoint"));
      	viewPointItem.setWrapTitle(false);
      	viewPointItem.setRequired(true);
      	viewPointItem.setValueMap(createMaxItemMap());   
      	viewPointItem.setDefaultValue("5");
      	
      	SelectItem itemCountItem = new SelectItem("itemcount", I18N.message("itemcount"));
      	itemCountItem.setWrapTitle(false);
      	itemCountItem.setRequired(true);
      	itemCountItem.setValueMap(createViewPointItemMap());   
      	itemCountItem.setDefaultValue("5");
      	
      	SpacerItem dummyItem = new SpacerItem();
      	
      	ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));
		searchButton.setIcon("[SKIN]/actions/search.png");
		searchButton.setAlign(Alignment.RIGHT);
		searchButton.setStartRow(false);
		searchButton.setEndRow(true);
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				boolean validSearch = searchForm.validate(); 
				boolean validChart = chartForm.validate();
				if( validSearch && validChart) {
					executeChartFetch();
				}
			}
        });
		
      	chartForm = new DynamicForm();
      	chartForm.setAutoWidth();;
      	chartForm.setAutoHeight();
      	
      	//chartForm.setItems(xAchseItem, viewItemItem, viewPointItem, itemCountItem, dummyItem, searchButton);
      	chartForm.setItems(xAchseItem, viewItemItem, itemCountItem, dummyItem, searchButton);
		return chartForm;
	}
	
	private DynamicForm createGridForm() {		
		//Colum1
		column1Item = new SelectItem("column1", I18N.message("column1"));
		column1Item.setWrapTitle(false);
		column1Item.setRequired(true);
		column1Item.setEmptyDisplayValue(I18N.message("choosetype"));
		column1Item.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {				
				column2Item.clearValue();
				column2Item.enable();
				column3Item.clearValue();
				column3Item.disable();
				column4Item.clearValue();
				column4Item.disable();
			}
		});
		
		//Colum2
		column2Item = new SelectItem("column2", I18N.message("column2"));
		column2Item.setWrapTitle(false);
		column2Item.disable();
		column2Item.setEmptyDisplayValue(I18N.message("choosetype"));
		column2Item.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {				
				String col1 = column1Item.getValueAsString();
				String col2 = (String)event.getValue();
				
				if( !(col1.indexOf("regDate")<0) && !(col2.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col2.equals(col1)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				
				if("".equals(col2)) {
					column3Item.clearValue();
					column3Item.disable();
				}
				else {
					column3Item.clearValue();
					column3Item.enable();
				}
				column4Item.clearValue();
				column4Item.disable();
			}
		});
		
		//Colum3
		column3Item = new SelectItem("column3", I18N.message("column3"));
		column3Item.setWrapTitle(false);
		column3Item.disable();
		column3Item.setEmptyDisplayValue(I18N.message("choosetype"));
		column3Item.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {				
				String col1 = column1Item.getValueAsString();
				String col2 = column2Item.getValueAsString();
				String col3 = (String)event.getValue();
				
				if( !(col1.indexOf("regDate")<0) && !(col3.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if( !(col2.indexOf("regDate")<0) && !(col3.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col3.equals(col1)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col3.equals(col2)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				
				if("".equals(col3)) {
					column4Item.clearValue();
					column4Item.disable();
				}
				else {
					column4Item.clearValue();
					column4Item.enable();
				}
			}
		});
				
		//Colum4
		column4Item = new SelectItem("column4", I18N.message("column4"));
		column4Item.setWrapTitle(false);
		column4Item.disable();
		column4Item.setEmptyDisplayValue(I18N.message("choosetype"));
		column4Item.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {				
				String col1 = column1Item.getValueAsString();
				String col2 = column2Item.getValueAsString();
				String col3 = column3Item.getValueAsString();
				String col4 = (String)event.getValue();
				
				if( !(col1.indexOf("regDate")<0) && !(col4.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if( !(col2.indexOf("regDate")<0) && !(col4.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if( !(col3.indexOf("regDate")<0) && !(col4.indexOf("regDate")<0) ) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col4.equals(col1)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col4.equals(col2)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
				else if(col4.equals(col3)) {
					SC.warn(I18N.message("dupmessage"));
					event.cancel();
					return;
				}
			}
		});
		
		SpacerItem dummyItem = new SpacerItem();
		
		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));	
		searchButton.setIcon("[SKIN]/actions/search.png");
		searchButton.setAlign(Alignment.RIGHT);
		searchButton.setStartRow(false);
		searchButton.setEndRow(true);
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				boolean validSearch = searchForm.validate();
				boolean validGrid = gridForm.validate();
				if( validSearch && validGrid) {
					executeGridFetch();
				}
			}
        });
		
      	gridForm = new DynamicForm();
      	gridForm.setAutoWidth();;
      	gridForm.setAutoHeight();
      	gridForm.setItems(column1Item, column2Item, column3Item, column4Item, dummyItem, searchButton);
		return gridForm;
	}
	
	private LinkedHashMap<String, String> createMaxItemMap() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("5",  "5");
		valueMap.put("10",  "10");
		valueMap.put("20",  "20");
		valueMap.put("30",  "30");
		return valueMap;
	}
	
	private LinkedHashMap<String, String> createViewPointItemMap() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("5",  "5");
		valueMap.put("10",  "10");
		valueMap.put("20",  "20");
		return valueMap;
	}
	
	/**
	 * 문서정보 차트 생성 및 반환
	 * @return
	 */
	private VLayout createChartVL() {		
		
		/* Sub Title 생성 */
//		Label subTitle = new Label();   
//		subTitle.setHeight(20);   
//		subTitle.setPadding(10);
//		subTitle.setWrap(false);
//		subTitle.setShowEdges(false);   
//		subTitle.setStyleName("subTitle");
//		subTitle.setContents(I18N.message("viewtypechart"));
		
		chartHL = new HLayout(5);
//		chartHL.addMember(subTitle);
		chartHL.addMember(createChartForm());
		chartHL.addMember(createLineWidget());		
    	
		VLayout chartVL = new VLayout(5);
    	chartVL.setShowResizeBar(false);
    	chartVL.setPadding(10);
    	chartVL.setBorder("1px solid #E1E1E1");
//    	chartVL.addMember(subTitle);
    	chartVL.addMember(chartHL);
    	return chartVL;
    } 	
	
	private VLayout createGridVL() {    	
		
		/* Sub Title 생성 */
//		Label subTitle = new Label();   
//		subTitle.setHeight(20);   
//		subTitle.setPadding(10);
//		subTitle.setWrap(false);
//		subTitle.setShowEdges(false);   
//		subTitle.setStyleName("subTitle");
//		subTitle.setContents(I18N.message("viewtypegrid"));
		
		gridHL = new HLayout(5);
//		gridHL.addMember(subTitle);
		gridHL.addMember(createGridForm());
		gridHL.addMember(createGridWidget());
		
		VLayout gridVL = new VLayout(5);
		gridVL.setShowResizeBar(false);
		gridVL.setPadding(10);
		gridVL.setBorder("1px solid #E1E1E1");
//		gridVL.addMember(subTitle);
		gridVL.addMember(gridHL);
    	return gridVL;
    } 
    
	/**
	 * 문서정보 그리드 생성 및 반환
	 * @return
	 */
    public ListGrid createDocInfoGrid() {
    	//Chart를 바로 반환하면 되겠다.
    	//임의로 HLayer를 반호나 함
    	
    	docInfoGrid = new ListGrid();
        docInfoGrid.setWidth100();
        docInfoGrid.setHeight100();
        docInfoGrid.setShowAllRecords(true);
                
        docInfoGrid.setCanReorderRecords(false);
        docInfoGrid.setCanDragRecordsOut(true);
        docInfoGrid.setCanAcceptDroppedRecords(false);
        docInfoGrid.setDragDataAction(DragDataAction.COPY);

        docInfoGrid.setShowFilterEditor(true);
        docInfoGrid.setFilterOnKeypress(true);
        docInfoGrid.setAutoFetchData(true);
        
        userIdField = new ListGridField("username",  I18N.message("userid"));
        userNameField = new ListGridField("name", I18N.message("uusername"));
        groupDepartmentField = new ListGridField("department",  I18N.message("department"));
        
        docInfoGrid.setFields(userIdField, userNameField, groupDepartmentField);        
        docInfoGrid.setCanResizeFields(true);
        //docInfoGrid.setDataSource(new UsersDS());
        docInfoGrid.setAutoFetchData(true);
    	
    	return docInfoGrid;
    } 
	
	public void executeChartFetch() {
		GWT.log("[ StatisticsPanel executeChartFetch ]", null);
		
		Date fromDate = ((DateItem)(searchForm.getField("from"))).getValueAsDate();
		String strFromYear = String.valueOf(fromDate.getYear()+1900);
		String strFromMonth = String.valueOf(fromDate.getMonth()+1);
		String strFromDate = String.valueOf(fromDate.getDate());
		strFromMonth = strFromMonth.length()<2 ? "0"+strFromMonth : strFromMonth;
		strFromDate = strFromDate.length()<2 ? "0"+strFromDate : strFromDate;
		
		Date toDate = ((DateItem)(searchForm.getField("to"))).getValueAsDate();
		String strToYear = String.valueOf(toDate.getYear()+1900);
		String strToMonth = String.valueOf(toDate.getMonth()+1);
		String strToDate = String.valueOf(toDate.getDate());		
		strToMonth = strToMonth.length()<2 ? "0"+strToMonth : strToMonth;
		strToDate = strToDate.length()<2 ? "0"+strToDate : strToDate;
		
		final int itemCount = Integer.parseInt((String)chartForm.getField("itemcount").getValue());
		//final int viewPoint = Integer.parseInt((String)chartForm.getField("viewpoint").getValue());
		
		SStatSearchOptions statSearchOptions = new SStatSearchOptions();		
		statSearchOptions.setDateFrom(strFromYear+strFromMonth+strFromDate); //from
		statSearchOptions.setDateTo(strToYear+strToMonth+strToDate); //to
		statSearchOptions.setEvent((String)searchForm.getField("event").getValue()); //action
		
		String typeId = (String)searchForm.getField("typeId").getValue();
		if(typeId != null) {
			statSearchOptions.setDocTypeId(Long.parseLong(typeId)); //type
		}
		
		String userId = (String)searchForm.getField("userId").getValue();
		if(userId != null) {
			statSearchOptions.setUserId(userId); //user
		}
		
		statSearchOptions.setMax(itemCount); //max
		//statSearchOptions.setViewPoint(viewPoint);
		
		ServiceUtil.system().listGraphStat(
				Session.get().getSid(),  
				(String)chartForm.getField("x").getValue(), 
				(String)chartForm.getField("viewitem").getValue(),
				statSearchOptions, new AsyncCallback<List<String[]>>() {
  			@Override
  			public void onFailure(Throwable caught) {
  				SCM.warn(caught);
  				
  			}
  			@Override
  			public void onSuccess(List<String[]> result) {
  				if(lineWidget != null) {
  					chartHL.removeMember(lineWidget);
  				}  				
  				chartHL.addMember(createLineWidget(), 1);  				
  				if(result != null && result.size() > 0) {
  					lineWidget.loadChart(null, result);
  				}
  			}
  		});
	}
	
	private void executeGridFetch() {
		Date fromDate = ((DateItem)(searchForm.getField("from"))).getValueAsDate();
		String strFromYear = String.valueOf(fromDate.getYear()+1900);
		String strFromMonth = String.valueOf(fromDate.getMonth()+1);
		String strFromDate = String.valueOf(fromDate.getDate());		
		strFromMonth = strFromMonth.length()<2 ? "0"+strFromMonth : strFromMonth;
		strFromDate = strFromDate.length()<2 ? "0"+strFromDate : strFromDate;
		
		Date toDate = ((DateItem)(searchForm.getField("to"))).getValueAsDate();
		String strToYear = String.valueOf(toDate.getYear()+1900);
		String strToMonth = String.valueOf(toDate.getMonth()+1);
		String strToDate = String.valueOf(toDate.getDate());		
		strToMonth = strToMonth.length()<2 ? "0"+strToMonth : strToMonth;
		strToDate = strToDate.length()<2 ? "0"+strToDate : strToDate;		
		
		String from = strFromYear+strFromMonth+strFromDate;
		String to = strToYear+strToMonth+strToDate;
		String action = (String)searchForm.getField("event").getValue();
		String typeId = (String)searchForm.getField("typeId").getValue();
		String userId = (String)searchForm.getField("userId").getValue();		
		
		String[] fieldNames = new String[4];
		fieldNames[0] = (String)gridForm.getField("column1").getDisplayValue();
		fieldNames[1] = (String)gridForm.getField("column2").getDisplayValue();
		fieldNames[2] = (String)gridForm.getField("column3").getDisplayValue();		
		fieldNames[3] = (String)gridForm.getField("column4").getDisplayValue();
		
		String[] fieldValues = new String[4];
		fieldValues[0] = (String)gridForm.getField("column1").getValue();
		fieldValues[1] = (String)gridForm.getField("column2").getValue();
		fieldValues[2] = (String)gridForm.getField("column3").getValue();		
		fieldValues[3] = (String)gridForm.getField("column4").getValue();
		
		gridWidget.loadGrid(fieldNames, fieldValues);
		gridWidget.executeGridFetch(from, to, action, typeId, userId);
	}
	
	private StatisticsWidget createLineWidget() {
		lineWidget = new StatisticsWidget();
		return lineWidget;
	}
	
	private GridWidget createGridWidget() {
		gridWidget = new GridWidget();
		return gridWidget;
	}
	
	private void executeGetOptionsAndSet() {
		//ACTIONS
		ServiceUtil.documentcode().listCodes(Session.get().getSid(), "ACTIONS", new AsyncCallback<List<SCode>>() {
			@Override
			public void onSuccess(List<SCode> result) {
				final LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>() ;
				opts.put("", I18N.message("all"));
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						opts.put(result.get(j).getValue(), I18N.message("event."+result.get(j).getName().replaceAll(" ", "")));
					}
				}
				actionItem.setValueMap(opts);
				actionItem.setDefaultValue("");
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
		
		//STAT_FIELDS
		ServiceUtil.documentcode().listCodes(Session.get().getSid(), "STAT_FIELDS", new AsyncCallback<List<SCode>>() {
			@Override
			public void onSuccess(List<SCode> result) {				
				if( result.size() > 0) {
					statOpts2.put("", I18N.message("notspecified"));
					for(int j=0; j<result.size(); j++) {
						statOpts.put(result.get(j).getValue(), I18N.message(result.get(j).getName().replaceAll(" ", "")));
						statOpts2.put(result.get(j).getValue(), I18N.message(result.get(j).getName().replaceAll(" ", "")));
					}
				}
				xAchseItem.setValueMap(statOpts);
				viewItemItem.setValueMap(statOpts);
				column1Item.setValueMap(statOpts);
				column2Item.setValueMap(statOpts2);
				column3Item.setValueMap(statOpts2);
				column4Item.setValueMap(statOpts2);
				
				setDefaultStatVal();
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void setDefaultStatVal() {
		String[] keySet = new String[statOpts.size()];
		statOpts.keySet().toArray(keySet);
		
		if(statOpts.size() >= 3) {
			xAchseItem.setValue(keySet[2]);
			column1Item.setValue(keySet[2]);
			column1Item.enable();
		}
		if(statOpts.size() >= 4) {
			viewItemItem.setValue(keySet[3]);
			column2Item.setValue(keySet[3]);
			column2Item.enable();
		}
		if(statOpts.size() >= 5) {
			column3Item.setValue(keySet[4]);
			column3Item.enable();
		}
		if(statOpts.size() >= 6) {
			column4Item.setValue(keySet[5]);
			column4Item.enable();
		}
		
		Date date = (Date)searchForm.getField("from").getValue();
		CalendarUtil.addMonthsToDate(date, -1);
		searchForm.getField("from").setValue(date);
	}
}
