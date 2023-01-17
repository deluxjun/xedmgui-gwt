package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.List;

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
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SHit;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;

public class FoldersSearchGrid extends ListGrid{
	private FoldersListPanel parent;

	public FoldersSearchGrid(final FoldersListPanel parent){
		this.parent = parent;

		// 마우스 우크릭 동작
		addRightMouseDownHandler(new RightMouseDownHandler() {
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				// observer에게 선택되었음을 알려줘야, DocumentActionUtil update가 호출된다.
				List<SRecordItem> itemList = new ArrayList<SRecordItem>();
				SRecordItem[] items = null;
				if (itemList.size() > 0)
					items = itemList.toArray(new SRecordItem[0]);
				Session.get().selectDocuments(items);

				Menu menu = DocumentActionUtil.get().getContextMenu(DocumentActionUtil.TYPE_SEARCH_FOLDERS);
				setContextMenu(menu);
			}
		});
		// Grid Data 선택 동작
		addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				
			}
		});
		
		
		List<ListGridField> listFields = new ArrayList<ListGridField>();
		
//		ListGridField fieldFolder = new ListGridField("folder", I18N.message("folder"));
//		fieldFolder.setHidden(true);
//		listFields.add(fieldFolder);
		
		listFields.add(getDefaultField("titlenm", "title", Alignment.LEFT, 200));
		listFields.add(getDefaultField("description", "description", Alignment.LEFT, 300));
		listFields.add(getDefaultField("path", "path", Alignment.LEFT, 300));
		ListGridField created = getDefaultField("created", "createddate", Alignment.LEFT, 0);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		listFields.add(created);

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
	public void setGridData(List<SHit> result, PagingToolStrip gridPager){
		// 최초 검색시 값들 초기화
		this.setData(new ListGridRecord[]{});

		// 검색 결과값 Set
		if(result.size() > 0 && result != null){
			List<ListGridRecord> records = new ArrayList<ListGridRecord>();

			for (SHit hit : result) {
				ListGridRecord record = new ListGridRecord();

				SFolder folder = hit.getFolder();

				// TODO: 값 세팅
				record.setAttribute("folder", folder);
				record.setAttribute("titlenm", folder.getName());
				record.setAttribute("description", folder.getDescription());
				record.setAttribute("path", folder.getPaths());
				record.setAttribute("created", folder.getCreation());
				
				records.add(record);
			}
			setData(records.toArray(new ListGridRecord[0]));
			
			setGridPagerInfo(gridPager, result.size(), gridPager.getPageNum());
		}else{
			setData(new ListGridRecord[]{});
			setGridPagerInfo(gridPager, 0 , 1);
		}
		
		redraw();
	}
	
	/**
	 *	GridPager의 정보들을 설정한다, TotalLength 사용 X
	 * */
	private void setGridPagerInfo(PagingToolStrip gridPager, int recordLength, int pageNum){
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
}
