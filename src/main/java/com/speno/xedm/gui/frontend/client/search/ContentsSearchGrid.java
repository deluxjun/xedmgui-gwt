package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.Action;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;

public class ContentsSearchGrid extends ListGrid{
	private ContentsListPanel parent;
	private Action action;
	
	public void setAction(Action action) {
		this.action = action;
	}

	public ContentsSearchGrid(final ContentsListPanel parent){
		this.parent = parent;
		// 마우스 우크릭 동작
		addRightMouseDownHandler(new RightMouseDownHandler() {
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				String elementId = getSelectedRecord().getAttribute("ELEMENTID");
				parent.setElementId(elementId);
				
//				List<SRecordItem> itemList = new ArrayList<SRecordItem>();
//				SRecordItem[] items = null;
//				if (itemList.size() > 0)
//					items = itemList.toArray(new SRecordItem[0]);
//				Session.get().selectDocuments(items);
				
				updateAction(elementId);
				Menu menu = action.getContextMenu();
				
				setContextMenu(menu);
			}
		});
		// Grid Data 선택 동작
		addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				parent.setElementId(getSelectedRecord().getAttribute("ELEMENTID"));
			}
		});
	}
	
	/**
	 * 필드 구성
	 */
	public void setField(String[] fields){
		List<ListGridField> listFields = new ArrayList<ListGridField>();
		
//		// ECM 검색 기본 필드값(elementId, indexId) 설정
//		ListGridField elementIdField = new ListGridField("ELEMENTID", I18N.message("elementId"));
		ListGridField elementIdField = getDefaultField("ELEMENTID", "elementId", Alignment.LEFT, 150);
		listFields.add(elementIdField);
		
		// 필드 동적 생성
		for (String field : fields) {
			if(field != null && !field.equals("ELEMENTID")){
				ListGridField gridField = getDefaultField(field, field, Alignment.LEFT, 150);
				listFields.add(gridField);
			}
		}
		this.setFields(listFields.toArray(new ListGridField[0]));
		this.redraw();
	}
	
	private ListGridField getDefaultField(String name, String title, Alignment align, int width){
		ListGridField f = new ListGridField(name, I18N.message(title));
		if (width > 0)
			f.setWidth(width);
		else
			f.setAutoFitWidth(true);
		f.setAlign(align);
		f.setType(ListGridFieldType.TEXT);
		return f;
	}

	
	/**
	 * 그리드에 데이터를 Set한다.
	 * @param result
	 * @param gridPager
	 */
	public void setGridData(List<Map<String, String>> result, PagingToolStrip gridPager){
		// 최초 검색시 값들 초기화
		this.setData(new ListGridRecord[]{});
		parent.setElementId(null);
		// 검색 결과값 Set
		if(result.size() > 0 && result != null){
			int dataLength = (result.size() >= gridPager.getPageSize())? gridPager.getPageSize() : result.size();
			ListGridRecord records[] = new ListGridRecord[dataLength];
			for (int i = 0; i < dataLength; i++) {
				Map<String,String> rows = result.get(i);
				records[i] = new ListGridRecord();
				for(String colName : rows.keySet()) {
					records[i].setAttribute(colName, rows.get(colName));
				}
			}
			setData(records);
			setGridPagerInfo(gridPager, result.size(), gridPager.getPageNum());
		}else{
			setData(new ListGridRecord[]{});
			setGridPagerInfo(gridPager, 0 , 1);
		}
	}
	
	/**
	 *	GridPager의 정보들을 설정한다, TotalLength 사용 X
	 * */
	public void setGridPagerInfo(PagingToolStrip gridPager, int recordLength, int pageNum){
		if(gridPager == null)	return;
	
		if(recordLength >= gridPager.getPageSize()){
			gridPager.setRespPageInfo(true, pageNum);
		}else{
			gridPager.setRespPageInfo(false, pageNum);
		}
	}
	
	public void resetGridData(){
		setRecords(new ListGridRecord[]{});
	}
	
	// update command possibility
	private void updateAction(String id) {
		action.enable("show", true, "");
		action.enable("download", true, "");
	}
}
