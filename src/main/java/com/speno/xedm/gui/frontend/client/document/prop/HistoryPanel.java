package com.speno.xedm.gui.frontend.client.document.prop;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SHistory;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.data.DocumentHistoryDS;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * 
 * @author deluxjun
 *
 */
public class HistoryPanel extends DocumentDetailTab implements PagingObserver  {

	private DocumentHistoryDS dataSource;

	private ListGrid listGrid;
	
	private VLayout container = new VLayout();

	// 페이징
	private PagingToolStrip gridPager;	
	
	public HistoryPanel(final SDocument document) {
		super(document, null);
		
		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);
		
		// 문서 아이디 
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		// 사용자 그룹
		ListGridField user = new ListGridField("user", I18N.message("user"), 80);
		ListGridField event = new ListGridField("event", I18N.message("event"), 120);
		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		ListGridField comment = new ListGridField("comment", I18N.message("comment"), 150);
		ListGridField title = new ListGridField("title", I18N.message("title"), 150);
		ListGridField path = new ListGridField("path", I18N.message("path"), 250);

		setField(user, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("user"));
		setField(event, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("event"));
		setField(version, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("version"));
		setField(date, Alignment.CENTER, ListGridFieldType.DATE, true, I18N.message("date"));
		date.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		setField(comment, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("comment"));
		setField(title, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("title"));
		setField(path, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("path"));
		
		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		listGrid.setFields(user, event, date, comment, version, title, path);

		// 페이징
		int pageSize = 10;
		try {
			pageSize = Session.get().getInfo().getIntConfig("gui.historyPageSize", 10);
		} catch (Exception e) {
		}
		gridPager = new PagingToolStrip(listGrid, pageSize, true, this);
		//totalLength 사용치 않을경우
        //gridPager = new PagingToolStrip(grid, 20, false, this); 
		listGrid.setHeight100();
		listGrid.setBodyOverflow(Overflow.SCROLL);

		container.setMembers(listGrid, gridPager);

		// 20130819, junsoo, 버전 다운로드는 버전탭에서 가능하도록 함.
//		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
//			@Override
//			public void onCellDoubleClick(CellDoubleClickEvent event) {
//				ListGridRecord record = event.getRecord();
//				Window.open(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
//						+ document.getId() + "&versionId=" + record.getAttribute("version") + "&open=true", "_blank",
//						"");
//			}
//		});

//		refresh();
	}
	
	// permission 관련 동작이 아니므로 아무것도 하지 않음.
	@Override
	protected void updatePermission() {
		return;
	}
	
	@Override
	public void refresh(){
		listGrid.setData(new ListGridRecord[0]);
		executeFetch(1, gridPager.getPageSize());
	}

	// 그리드 필드 설정
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter, String title){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
		glidField.setTitle(title);
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{	
		final PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		ServiceUtil.document().pagingHistoryByDocIdAndUserIdAndEvent(Session.get().getSid(), document.getId(), "", "", config, new AsyncCallback<PagingResult<SHistory>>() {
			@Override
			public void onSuccess(PagingResult<SHistory> result) {
				int totalLength = result.getTotalLength();
				List<SHistory> data = result.getData();
				SHistory[] shistory = new SHistory[data.size()]; 
				for (int i = 0; i < data.size(); i++) {
					shistory[i] = data.get(i);
				}
				
				setRecordData(listGrid, shistory);
				
//				listGrid.setData(getNewRecords( shistory));
				gridPager.setRespPageInfo(totalLength, pageNum);
				//totalLength 사용치 않을경우
				//gridPager.setRespPageInfo((data.size() > 0), pageNum); 
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	// 필드값 채우기
	public void setRecordData(ListGrid grid, SHistory[] shistory){
		ListGridRecord[] records = new ListGridRecord[shistory.length];
		for(int i=0; i< shistory.length; i++){
			records[i] = new ListGridRecord();
			records[i].setAttribute("user", shistory[i].getUserName());   
			records[i].setAttribute("event", I18N.message(shistory[i].getEvent()));   
			records[i].setAttribute("title", shistory[i].getTitle());   
			records[i].setAttribute("version", shistory[i].getVersion());   
			records[i].setAttribute("date", shistory[i].getDate());   
			records[i].setAttribute("comment", shistory[i].getComment());
			records[i].setAttribute("path", shistory[i].getPath());
		}
		grid.setData(records);
	}


	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}	

}