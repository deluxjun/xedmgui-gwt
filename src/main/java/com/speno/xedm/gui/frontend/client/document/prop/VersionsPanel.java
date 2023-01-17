package com.speno.xedm.gui.frontend.client.document.prop;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SVersion;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.data.VersionsDS;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.popup.VersionDetailsDialog;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * 
 * @author deluxjun
 *
 */
public class VersionsPanel extends DocumentDetailTab implements PagingObserver{
	private VersionsDS dataSource;

	private ListGrid listGrid;
	
	private VLayout container = new VLayout();
	
	private DocumentObserver documentObserver;

	// 페이징
	private PagingToolStrip gridPager;

	private Menu contextMenu;

	public VersionsPanel(final SDocument document) {
		super(document, null);

		container.setWidth100();
		container.setMembersMargin(5);
		addMember(container);

		ListGridField id = new ListGridField("versionId", I18N.message("id"), 80);
		id.setHidden(true);

		ListGridField user = new ListGridField("user", I18N.message("user"), 80);
		ListGridField event = new ListGridField("event", I18N.message("event"), 120);
		ListGridField title = new ListGridField("title", I18N.message("title"), 200);
		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		ListGridField comment = new ListGridField("comment", I18N.message("comment"));
		setField(user, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("user"));
		setField(event, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("event"));
		setField(title, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("title"));
		setField(version, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("version"));
		setField(date, Alignment.CENTER, ListGridFieldType.DATE, true, I18N.message("date"));
		date.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		setField(comment, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("description"));
		
		// 그리드 생성
		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		
		listGrid.setFields(id, user, event, title, version, date, comment);
		gridPager = new PagingToolStrip(listGrid, 10, true, this);
		listGrid.setHeight100();
		listGrid.setBodyOverflow(Overflow.SCROLL);
		
		container.setMembers(listGrid, gridPager);

		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				if(document.getDocType() == SDocument.DOC_LOCKED) return;
				
				ListGridRecord record = event.getRecord();
				if (Session.get().getCurrentFolder().isDownload()
						&& "download".equals(Session.get().getInfo().getConfig("gui.doubleclick")))
					onDownload(document, record);
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				setEnableContextMenu(event.getRecord());
				contextMenu.showContextMenu();
				event.cancel();
			}
		});
		
//		refresh();

	}

	public void setDocumentObserver(DocumentObserver documentObserver) {
		this.documentObserver = documentObserver;
	}

	protected void onDownload(final SDocument document, ListGridRecord record) {
		if (super.download)
			Util.downloadVersion(document.getId(), record.getAttributeAsLong("versionId"), "");
	}


	/**
	 * Prepares the context menu.
	 */
	private void setupContextMenu() {
		contextMenu = new Menu();
		
		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord[] selection = listGrid.getSelectedRecords();
				if (selection == null || selection.length < 1) {
					return;
				}
				onDownload(document, selection[0]);
			}
		});
//		if(!DocumentActionUtil.get().getRights().toString().contains("download")){
//			download.setEnabled(false);
//		}else
			download.setEnabled(super.download);

		MenuItem properties = new MenuItem();
		properties.setTitle(I18N.message("properties"));
		properties.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord[] selection = listGrid.getSelectedRecords();
				if (selection == null || selection.length < 1) {
					return;
				}
				onShowDetails(selection[0]);

			}
		});
		properties.setEnabled(true);

		MenuItem restore = new MenuItem();
		restore.setTitle(I18N.message("restore"));
		restore.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord[] selection = listGrid.getSelectedRecords();
				if (selection == null || selection.length < 1) {
					return;
				}
				onRestore(selection[0]);
			}
		});
		restore.setEnabled(super.update && super.check);

		contextMenu.setItems(download, properties, restore);
	}
	
	// show detail props
	private void onShowDetails(ListGridRecord record) {
		SVersion version = (SVersion)record.getAttributeAsObject("versionObject");
		version.setFolder(document.getFolder());

		VersionDetailsDialog popup = new VersionDetailsDialog(version);
		popup.show();
	}
	
	// restore
	private void onRestore(final ListGridRecord record) {
		SVersion version = (SVersion)record.getAttributeAsObject("versionObject");
		if (version.getVersion().equals(document.getVersion())) {
			SC.say(I18N.message("second.client.cannotRestoreToSameVersion"));
			return;
		}
		
		if (SDocument.DOC_CHECKED_OUT == document.getStatus() ||
				SDocument.DOC_LOCKED == document.getStatus()) {
			SC.say(I18N.message("third.client.cannotRestoreForLockedDoc"));
			return;
		}
		SC.confirm(I18N.message("second.maketorecentversioin"), I18N.message("second.maketorecentversioin"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				try {
					if(value){
						SVersion version = (SVersion)record.getAttributeAsObject("versionObject");
						ServiceUtil.document().restoreVersion(Session.get().getSid(), version, new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								documentObserver.onDocumentSaved(document);
								refresh();
							}
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, true);
							}
						});
					}
			
				} catch (NullPointerException e) {
					return;
				}
			}
		});

	}

	private void executeFetch(final int pageNum, final int pageSize)	{	
		final PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		ServiceUtil.document().pagingVersionsByDocId(Session.get().getSid(), document.getId(), config, new AsyncCallback<PagingResult<SVersion>>() {
			@Override
			public void onSuccess(PagingResult<SVersion> result) {
				int totalLength = result.getTotalLength();
				List<SVersion> data = result.getData();
				
				SVersion[] popVersion = new SVersion[data.size()];
				listGrid.setData(new ListGridRecord[0]);
				for (int i = 0; i < data.size(); i++) {
					popVersion[i] = data.get(i);
				}

				setRecordData(listGrid, popVersion);

				gridPager.setRespPageInfo(totalLength, pageNum);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	// 필드값 채우기
	public void setRecordData(ListGrid grid, SVersion[] version){
		ListGridRecord[] records = new ListGridRecord[version.length];
		for(int i=0; i< version.length; i++){
			records[i] = new ListGridRecord();
			records[i].setAttribute("versionId", version[i].getId());
			records[i].setAttribute("user", version[i].getUsername());   
			records[i].setAttribute("event", I18N.message(version[i].getEvent()));   
			records[i].setAttribute("title", version[i].getTitle());   
			records[i].setAttribute("version", version[i].getVersion());   
			records[i].setAttribute("date", version[i].getVersionDate());   
			records[i].setAttribute("comment", version[i].getComment());   
			records[i].setAttribute("versionObject", version[i]);   
	    }   
		grid.setData(records);
	}
	
	// permission 관련 동작이 아니므로 아무것도 하지 않음.
	// 20130820, junsoo, 버전 다운로드 restore 기능으로 권한관리 하게 되어 주석처리함.
//	@Override
//	protected void updatePermission() {
//		return;
//	}
	
	@Override
	public void refresh(){
		listGrid.setData(new ListGridRecord[0]);

		int pageSize = 30;
		try {
			pageSize = Integer.parseInt(Session.get().getInfo().getConfig("gui.versionPageSize"));
		} catch (Exception e) {
		}
		executeFetch(1, pageSize);
		

		// 20130820, junsoo, setup context menu. 여기서 해야만 권한이 제대로 세팅됨.
		setupContextMenu();

	}

	// 필드 셋팅
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter, String title){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
		glidField.setTitle(title);
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
	
	/**
	 * 20131213na 최근 버전 메뉴의 비활성화 및 잠금상태일 경우 비활성화
	 */
	private void setEnableContextMenu(ListGridRecord record){
		SVersion version = (SVersion)record.getAttributeAsObject("versionObject");
		String compare = "";
		boolean isRecentVersion;
		
		for (int i = 0; i < contextMenu.getItems().length; i++) {
			compare = contextMenu.getItem(i).getTitle();
					
			if(document.getStatus() == SDocument.DOC_LOCKED
					|| document.getStatus() == SDocument.DOC_CHECKED_OUT){
				contextMenu.getItem(i).setEnabled(false);
				continue;
			}
				
			//properties와 restore는 최신 버전이면 비활성화
			if(compare.equals(I18N.message("properties")) || compare.equals(I18N.message("restore"))){
				isRecentVersion =  version.getVersion().equals(document.getVersion());
				contextMenu.getItem(i).setEnabled(!isRecentVersion);
			}
		}
		contextMenu.redraw();
	}
}