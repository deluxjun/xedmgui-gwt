package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SStatEcmStatisticsOptions;
import com.speno.xedm.core.service.serials.SStatSearchOptions;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.util.paging.PagingResult;

/**
 * Grid Widget
 * 
 * @author 박상기
 * @since 1.0
 */
public class GridWidget extends VLayout implements PagingObserver {
	private Label emptyLabel;
	private ListGrid grid;
	private PagingToolStrip gridPager;	
	private String from, to, action, typeId, userId;
	private String day;
	private String[] fieldValues;
	private String ecmStatType;
	
	private boolean doPaging = true;
	
	public GridWidget() {
		super();
		init();
	}
	
	public GridWidget(boolean doPaging) {
		super();
		this.doPaging = doPaging;
		init();
	}
	
	private void init(){
		setWidth100();
		setHeight100();
		setBorder("1px solid #E1E1E1");
		emptyLabel = new Label(I18N.message("clickandgriddrawn", I18N.message("search")));
		emptyLabel.setWidth100();
		emptyLabel.setHeight100();
		emptyLabel.setAlign(Alignment.CENTER);
		emptyLabel.setValign(VerticalAlignment.CENTER);
		addMember(emptyLabel);
	}
	
	public void loadECMGrid(String[] fieldNames, String[] fieldValues) {
		String[] filedNamesTemp = new String[fieldNames.length];
		this.fieldValues = fieldValues;
		
		for (int i = 0; i < fieldNames.length; i++) {
			filedNamesTemp[i]=I18N.message(fieldNames[i].toLowerCase());
//			System.out.println(filedNamesTemp[i]);
		}
		
		grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);
        grid.setCanResizeFields(true);
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        
		grid.setGroupStartOpen(GroupStartOpen.ALL);
//		grid.setGroupByField(fieldNames[0]);
		//DB에서 읽어온 이름 변경
		
       	List<ListGridField> fields = new ArrayList<ListGridField>();
		for (int j=0; j<fieldValues.length; j++) {
			fields.add(new ListGridField(fieldNames[j], filedNamesTemp[j]));
		}
       	
//		grid.hideField(fieldNames[0]);
		
		
//		ListGridField countField = new ListGridField("count",  I18N.message("count")); //Fix
//		fields.add(countField);
		grid.setFields(fields.toArray(new ListGridField[0]));
		
		//All fields Hidden, After data binding, show field 
		//모든 필드를 숨기고, 데이터 바인딩 할때 값이 있는 필드만 보여줌.
		for(int i = 0; i<fields.size();i++){
//			System.out.println("fields("+i+")::"+fields.get(i).getName()+"::Hidden");
			grid.hideField(fields.get(i).getName());
		}
		
		if(doPaging){
			gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), false, this);
			gridPager.setDeselect(false);
			gridPager.setIsAutoHeightGrid(false);
			
			setMembers(grid, gridPager);
		}else{
			setMembers(grid);
		}
	}
	
	public void loadGrid(String[] fieldNames, String[] fieldValues) {
		this.fieldValues = fieldValues;
		
		grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);
        grid.setCanResizeFields(true);
        
        grid.setGroupStartOpen(GroupStartOpen.ALL);
		grid.setGroupByField("col0");
		
        ListGridField col0Field = new ListGridField("col0");
        col0Field.setHidden(true);
        ListGridField col1Field = new ListGridField("col1");
        ListGridField col2Field = new ListGridField("col2");
        ListGridField col3Field = new ListGridField("col3");
        ListGridField col4Field = new ListGridField("col4",  I18N.message("count")); //Fix
        grid.setFields(col0Field, col1Field, col2Field, col3Field, col4Field);
        
		for (int j=0; j<fieldValues.length; j++) {
			if(fieldValues[j] == null) {
				grid.hideField("col"+j);
			}
			else {
				grid.getField("col"+j).setTitle(fieldNames[j]);
			}
		}
        
		if(doPaging){
			gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), false, this);
			gridPager.setDeselect(false);
			gridPager.setIsAutoHeightGrid(false);
			
			setMembers(grid, gridPager);
		}else{
			setMembers(grid);
		}
	}
	
	/**
	 * 1(Default)페이지 조회
	 * @param from
	 * @param to
	 * @param action
	 * @param typeId
	 * @param userId
	 */
	public void executeGridFetch(String from, String to, String action, String typeId, String userId) {
		this.from = from;
		this.to = to;
		this.action = action;
		this.typeId = typeId;
		this.userId = userId;
		
		executeFetch(1, gridPager.getPageSize());
	}
	
//	public void executeECMGridFetch(String from, String to, String day) {
//		this.from = from;
//		this.to = to;
//		this.day = day;
//		
//		executeECMFetch();
//	}
	
	public void executeECMGridFetch(String from, String to, String day, String ecmStatType) {
		this.from = from;
		this.to = to;
		this.day = day;
		this.ecmStatType=ecmStatType;
		
		executeECMFetch();
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		GWT.log("[ GridWidget executeGridFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"], " +
				"from["+from+"], to["+to+"], action["+action+"], typeId["+typeId+"], userId["+userId+"]", null);		
				
		SStatSearchOptions statSearchOptions = new SStatSearchOptions();
		statSearchOptions.setDateFrom(from); //from
		statSearchOptions.setDateTo(to); //to
		statSearchOptions.setEvent(action); //action
		if(typeId != null) {
			statSearchOptions.setDocTypeId(Long.parseLong(typeId)); //type
		}
		if(userId != null) {
			statSearchOptions.setUserId(userId); //user
		}
		statSearchOptions.setFields(fieldValues); //column info		
		statSearchOptions.setConfig(PagingToolStrip.getPagingConfig(pageNum, pageSize)); //페이징
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.system().pagingStat(Session.get().getSid(), statSearchOptions, new AsyncCallback<PagingResult<String[]>>() {
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(PagingResult<String[]> result) {
				Waiting.hide();
				changeDocType();
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
				List<String[]> data = result.getData();				
				for (int j = 0; j < data.size(); j++) {					
					ListGridRecord record = new ListGridRecord();
					String[] columns = data.get(j);
					for (int k=0; k < columns.length-1; k++) {
						record.setAttribute("col"+k, I18N.message(columns[k]));
					}					
					record.setAttribute("col4", columns[columns.length-1]); //count
					
					grid.addData(record);
				}
				
				if (data.size() > 0) {
					grid.selectSingleRecord(0);
				}
				
				GWT.log("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]", null);
				gridPager.setRespPageInfo((data.size() > 0), pageNum);
			}
		});
	}
	
	private Map<Long, String> docTypes = new HashMap<Long, String>();
	
	private void changeDocType(){
		if(typeId != null){
			ServiceUtil.getAvailableSDocTypes(null, new ReturnHandler<SDocType[]>() {
				@Override
				public void onReturn(SDocType[] param) {
					for (SDocType docType : param) {
						docTypes.put(docType.getId(), docType.getName());
					}
				}
			});
		}
	}
	private void executeECMFetch()	{				
		GWT.log("[ GridWidget executeECMGridFetch ] from["+from+"], to["+to+"], day["+day+"], ecmStatType["+ecmStatType+"]", null);		
				
		SStatSearchOptions statSearchOptions = new SStatSearchOptions();
		statSearchOptions.setDateFrom(from); //from
		statSearchOptions.setDateTo(to); //to
		statSearchOptions.setIndex(day);
		
		//20131223 hongcheol Add
		SStatEcmStatisticsOptions statEcmOptions = new SStatEcmStatisticsOptions();
		statEcmOptions.setType(ecmStatType);
		
		
//		ServiceUtil.system().listECMStat(Session.get().getSid(), statSearchOptions, new AsyncCallback<List<String[]>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				SCM.warn(caught);
//			}
//			@Override
//			public void onSuccess(List<String[]> result) {
//				grid.setData(new ListGridRecord[0]); //그리드 초기화
//				
//				for (int j = 0; j < result.size(); j++) {					
//					ListGridRecord record = new ListGridRecord();
//					String[] columns = result.get(j);
//					for (int k = 0 ;  k < columns.length-1 ; k++) {
//						record.setAttribute(fieldValues[k], columns[k]);
//					}					
//					record.setAttribute("count", columns[columns.length-1]); //count
//					
//					grid.addData(record);
//				}
//				
//				if (result.size() > 0) {
//					grid.selectSingleRecord(0);
//				}
//			}
//		});
		
		//20131223 hongcheol Add
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.system().listECMStat(Session.get().getSid(), statSearchOptions, statEcmOptions, new AsyncCallback<List<String[][]>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Waiting.hide();
				SCM.warn(caught);
			}

			@Override
			public void onSuccess(List<String[][]> result) {
				// TODO Auto-generated method stub
				//오류로 인한 result == null 이면 메소드 종료
				Waiting.hide();
				if(result== null) return ;
				if(result.size()>0){
					String[][] temp = new String[result.get(0).length][2];
					temp = result.get(0);
					String[] fields = new String[temp.length];
					
					//Hidden Field -> Shown
					for(int i =0;i<temp.length;i++){
	//					System.out.println(i+"::"+temp[i][0]+"::Show");
						grid.showField(temp[i][0]);
					}
				}
				
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
//				System.out.println("result : "+result.size());
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record = new ListGridRecord();
//					System.out.println(result.get(j));
					String[][] col = result.get(j);
					for (int k = 0 ;  k < col.length ; k++) {
//						System.out.println("col["+k+"][0], col["+k+"][1] :: "+col[k][0]+", "+ col[k][1] );
						//row[k][0] : columnLable , row[k][1] : value
						record.setAttribute(col[k][0], col[k][1]);
					}					
					
					grid.addData(record);
				}
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
				}
				
//				GWT.log("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]", null);
//				gridPager.setRespPageInfo((data.size() > 0), pageNum);
			}
			
		});
	} 
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}
}
