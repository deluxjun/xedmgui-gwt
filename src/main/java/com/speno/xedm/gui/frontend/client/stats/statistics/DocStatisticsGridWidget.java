package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocType;
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
 * 
 * @author deluxjun
 *
 */
public class DocStatisticsGridWidget extends VLayout implements PagingObserver {
	private Label emptyLabel;
	private ListGrid grid;
	private PagingToolStrip gridPager;	
	private String from, to, path, typeId, userId;
	private String[] fieldValues;
	
	private boolean doPaging = true;
	
	public DocStatisticsGridWidget() {
		super();
		init();
	}
	
	public DocStatisticsGridWidget(boolean doPaging) {
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
	

	public void loadGrid(String[] fieldNames, String[] fieldValues) {
		this.fieldValues = fieldValues;
		
		grid = new ListGrid();
        grid.setWidth100();
        grid.setHeight100();
        grid.setShowAllRecords(true);
        grid.setCanResizeFields(true);
        
        // no grouping!
//        grid.setGroupStartOpen(GroupStartOpen.ALL);
//		grid.setGroupByField("col0");
		
        ListGridField col0Field = new ListGridField("col0");
//        col0Field.setHidden(true);
        ListGridField col1Field = new ListGridField("col1");
        ListGridField col2Field = new ListGridField("col2");
        ListGridField col3Field = new ListGridField("col3");
        ListGridField countField = new ListGridField("totalCount",  I18N.message("count")); //Fix
        ListGridField sizeField = new ListGridField("totalSize",  I18N.message("size")); //Fix
        sizeField.setAlign(Alignment.RIGHT);

        grid.setFields(col0Field, col1Field, col2Field, col3Field, countField, sizeField);
        
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
	 * @param path
	 * @param typeId
	 * @param userId
	 */
	public void executeGridFetch(String from, String to, String path, String typeId, String userId) {
		this.from = from;
		this.to = to;
		this.path = path;
		this.typeId = typeId;
		this.userId = userId;
		
		executeFetch(1, gridPager.getPageSize());
	}
		
	private void executeFetch(final int pageNum, final int pageSize)	{				
		GWT.log("[ GridWidget executeGridFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"], " +
				"from["+from+"], to["+to+"], path["+path+"], typeId["+typeId+"], userId["+userId+"]", null);		
				
		SStatSearchOptions statSearchOptions = new SStatSearchOptions();
		statSearchOptions.setDateFrom(from); //from
		statSearchOptions.setDateTo(to); //to
		statSearchOptions.setFolder(path); //path
		if(typeId != null) {
			statSearchOptions.setDocTypeId(Long.parseLong(typeId)); //type
		}
		if(userId != null) {
			statSearchOptions.setUserId(userId); //user
		}
		statSearchOptions.setFields(fieldValues); //column info		
		statSearchOptions.setConfig(PagingToolStrip.getPagingConfig(pageNum, pageSize)); //페이징
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.system().pagingDocStat(Session.get().getSid(), statSearchOptions, new AsyncCallback<PagingResult<String[]>>() {
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
					record.setAttribute("totalCount", columns[columns.length-2]); //count
					String strSize = "";
					try {
						strSize = Util.getFormattedFileSize(Long.parseLong(columns[columns.length-1]), false);
					} catch (Exception e) {}
					record.setAttribute("totalSize", strSize); //size
					
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
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}
}
