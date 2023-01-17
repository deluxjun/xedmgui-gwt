package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.Date;
import java.util.LinkedHashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

public class ECMStatisticsPanel extends VLayout{
	public static ECMStatisticsPanel instance;
	public static ECMStatisticsPanel get(){
		if(instance == null)
			instance = new ECMStatisticsPanel();
		
		return instance;
	}
	
	// member Items
	private SpacerItem dummyItem;
	private HLayout gridLayout;
//	private SelectItem column1Item;
//	private SelectItem column1Item, column2Item;
	private SelectItem daySelectItem; 
	private GridWidget gridWidget;
	private DynamicForm searchForm;
	private DynamicForm gridForm;
	
	// ECM Statistics type
	private SelectItem ecmStatisticsList;
	// member Value Items 
	private LinkedHashMap<String, String> indexOpts = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> dayOpts = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> ecmStatisticsOps = new LinkedHashMap<String, String>();
	public ECMStatisticsPanel(){
		initMember();
		setMemberValues();
	}
	
	private void initMember(){
		dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		addMember(new TrackPanel(I18N.message("statistics") + " > " + I18N.message("statistics") + " > " + I18N.message("ecmStatistics"), null));
		addMember(createSearchForm());
		
		gridLayout = new HLayout();
		gridLayout.setBorder("1px solid #E1E1E1");
		gridLayout.setHeight100();
		gridLayout.setPadding(5);
//		gridLayout.addMember(selectForm());
		gridLayout.addMember(createGridWidget());
		
		addMember(gridLayout);
	}

	private void setMemberValues(){
		setColumnValue();
	}
	
	private DynamicForm createSearchForm() {		
		//시작일자
		DateItem fromItem = new DateItem("from", I18N.message("from"));
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
		DateItem toItem = new DateItem("to", I18N.message("to"));
		toItem.setWrapTitle(false);
		toItem.setRequired(true);
		toItem.setShowTitle(false);
		
		//index 선택
		daySelectItem = new SelectItem("index", I18N.message("date"));
		daySelectItem.setWrapTitle(false);
		daySelectItem.setRequired(true);
		daySelectItem.setEmptyDisplayValue(I18N.message("all"));
		
		//ECM 통계 구분
		ecmStatisticsList = ItemFactory.newSelectItem("ecmStatisticsList", I18N.message("ecm.statistics.type"));
		ecmStatisticsList.setWrapTitle(false);
		ecmStatisticsList.setRequired(true);
		
		String[] strStatisticsOps = Session.get().getInfo().getConfig("setting.ecm.stat.ecmStatisticsOps").split(",");
		for(int i = 0; i< strStatisticsOps.length;i++){
			ecmStatisticsOps.put(strStatisticsOps[i].trim(), I18N.message("ecm.statistics."+strStatisticsOps[i].trim()));
		}
//		ecmStatisticsOps.put("amount", I18N.message("ecm.statistics.amoumt"));
//		ecmStatisticsOps.put("storage", I18N.message("ecm.statistics.storage"));
//		ecmStatisticsOps.put("document", I18N.message("ecm.statistics.document"));
		ecmStatisticsList.setValueMap(ecmStatisticsOps);
		ecmStatisticsList.setDefaultToFirstOption(true);
		
		//검색버튼
		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));	
		searchButton.setStartRow(false);
		searchButton.setIcon("[SKIN]/actions/search.png");		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				executeGridFetch();
			}
        });
		
		searchForm = new DynamicForm();
		searchForm.setWidth100();
		searchForm.setAutoHeight();		
		searchForm.setNumCols(13);
		searchForm.setColWidths("*","1","1","1","1","1","1","1","1","1","1","1","1");
//		searchForm.setItems(dummyItem, fromItem, dummyItem, commonDateColumn, dummyItem, toItem, dummyItem, searchButton);
		searchForm.setItems(dummyItem, fromItem, dummyItem, commonDateColumn, dummyItem, toItem, dummyItem, daySelectItem, dummyItem, ecmStatisticsList, searchButton);
		
		return searchForm;
	}
	
//	private HLayout selectForm(){
//		HLayout selectLayout = new HLayout();
//		selectLayout.setAutoWidth();
//		
//		//Colum1
//		column1Item = new SelectItem("column1", I18N.message("column1"));
//		column1Item.setWrapTitle(false);
//		column1Item.setRequired(true);
//		column1Item.setEmptyDisplayValue(I18N.message("choosetype"));
//		column1Item.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {				
//				column2Item.clearValue();
//				column2Item.enable();
//			}
//		});
//		
//		//Colum2
//		column2Item = new SelectItem("column2", I18N.message("column2"));
//		column2Item.setWrapTitle(false);
//		column2Item.disable();
//		column2Item.setEmptyDisplayValue(I18N.message("choosetype"));
//		column2Item.addChangeHandler(new ChangeHandler() {
//			@Override
//			public void onChange(ChangeEvent event) {				
//				String col1 = column1Item.getValueAsString();
//				String col2 = (String)event.getValue();
//				
//				if( !(col1.indexOf("regDate")<0) && !(col2.indexOf("regDate")<0) ) {
//					SC.warn(I18N.message("dupmessage"));
//					event.cancel();
//					return;
//				}
//				else if(col2.equals(col1)) {
//					SC.warn(I18N.message("dupmessage"));
//					event.cancel();
//					return;
//				}
//			}
//		});
//		
//		SpacerItem dummyItem = new SpacerItem();
//		
//		ButtonItem searchButton = new ButtonItem();
//		searchButton.setTitle(I18N.message("search"));	
//		searchButton.setIcon("[SKIN]/actions/search.png");
//		searchButton.setAlign(Alignment.RIGHT);
//		searchButton.setStartRow(false);
//		searchButton.setEndRow(true);
//		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
//			@Override
//			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//				executeGridFetch();
//			}
//        });
//				
//		gridForm = new DynamicForm();
//		gridForm.setAutoWidth();
//		gridForm.setAutoHeight();
//		gridForm.setItems(column1Item, column2Item, dummyItem, searchButton);
//		
//		selectLayout.addMember(gridForm);
//		
//		return selectLayout;
//	}
	
	private void setColumnValue(){
		// 1. index정보 가져옴
//		ServiceUtil.security().listEcmMenu(Session.get().getSid(), new AsyncCallbackWithStatus<List<SAdminMenu>>() {
//			@Override
//			public String getSuccessMessage() {
//				return I18N.message("client.searchComplete");
//			}
//			@Override
//			public String getProcessMessage() {
//				return I18N.message("client.searchRequest");
//			}
//			@Override
//			public void onSuccessEvent(List<SAdminMenu> result) {
//				indexOpts.put("", I18N.message("all"));
//				if(result.size() > 0){
//					for (SAdminMenu menu : result) {
//						indexOpts.put(menu.getTitle(), menu.getTitle());
//					}
//				}
//				indexSelectItem.setValueMap(indexOpts);
//			}
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				Log.serverError(caught, false);
//			}
//		});
		
		// 2. date 정보 가져옴
//		ServiceUtil.documentcode().listCodes(Session.get().getSid(), "ECM_STAT_FIELDS", new AsyncCallback<List<SCode>>() {
//			@Override
//			public void onSuccess(List<SCode> result) {				
//				if( result.size() > 0) {
//					for (SCode code : result) {
//						codeOpts.put(code.getValue(), I18N.message(code.getName().replaceAll(" ", "")));						
//					}
//					column1Item.setValueMap(codeOpts);
////					column2Item.setValueMap(codeOpts);
//					
//					setDefaultStatVal();
//				}
//			}			
//			@Override
//			public void onFailure(Throwable caught) {
//				SCM.warn(caught);
//			}
//		});
		LinkedHashMap<String, String> dayOpts = new LinkedHashMap<String, String>();
		dayOpts.put("year", I18N.message("date(year)"));
		dayOpts.put("month", I18N.message("date(month)"));
		dayOpts.put("day", I18N.message("date(day)"));
		
		daySelectItem.setValueMap(dayOpts);
		daySelectItem.setDefaultToFirstOption(true);
	}
	
//	private void setDefaultStatVal() {
//		String[] keySet = new String[codeOpts.size()];
//		codeOpts.keySet().toArray(keySet);
//		
//		column1Item.setValue(keySet[2]);
//		column1Item.enable();
//	
//		column2Item.setValue(keySet[3]);
//		column2Item.enable();
//		
//		Date date = (Date)searchForm.getField("from").getValue();
//		CalendarUtil.addMonthsToDate(date, -1);
//		searchForm.getField("from").setValue(date);
//	}
	
	
	private GridWidget createGridWidget(){
		gridWidget = new GridWidget(false);
		return gridWidget;
	}
	
	private void executeGridFetch(){
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
		
		
		String column = Session.get().getInfo().getConfig("setting.ecm.stat.column");
		String[] fieldNames = new String[column.split(",").length-1];
		
		fieldNames = column.split(",");
		
		String[] fieldValues = fieldNames;
		
		String day = daySelectItem.getValueAsString();
		
		String strEcmStatType = String.valueOf( ecmStatisticsList.getValueAsString()  );
		
		gridWidget.loadECMGrid(fieldNames, fieldValues);
//		gridWidget.executeECMGridFetch(from, to, day);
		gridWidget.executeECMGridFetch(from, to, day, strEcmStatType);
	}
}
