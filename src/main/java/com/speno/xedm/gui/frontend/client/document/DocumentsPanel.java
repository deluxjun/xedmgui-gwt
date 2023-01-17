package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.Feature;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DefaultCanvasItem;
import com.speno.xedm.gui.common.client.util.InstanceHandler;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.FolderPropertiesWindow;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gui.frontend.client.admin.system.ApprovalPanel;
import com.speno.xedm.gui.frontend.client.clipboard.Clipboard;
import com.speno.xedm.gui.frontend.client.document.popup.ApproveRequestDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentCheckinDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentCheckoutCancelDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentCheckoutDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentLockDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentPropertiesDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentUnlockDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentUploadDialog;
import com.speno.xedm.gui.frontend.client.document.popup.EmailDialog;
import com.speno.xedm.gui.frontend.client.document.popup.FolderSelectorDialog;
import com.speno.xedm.gui.frontend.client.document.popup.MessagePopup;
import com.speno.xedm.gui.frontend.client.document.popup.TemplateSelectorDialog;
import com.speno.xedm.gui.frontend.client.panels.FileDropArea;
import com.speno.xedm.gui.frontend.client.search.NormalSearchItems;

/**
 * Modified: 20130730 : 속성창은 별도 window로 통합, 복잡한 로직 단순화, 메뉴/버튼은
 * DocumentActionUtil로 통합
 * 
 * @author deluxjun
 * 
 */
public class DocumentsPanel extends HLayout implements FolderObserver,
		DocumentObserver {
	protected static DocumentsPanel instance;

	// 문서 리스트 레이아웃
	protected Layout listing = new VLayout();
	// 문서 속성 레이아웃
	// protected Layout details = new VLayout();
	// 트리 레이아웃
	protected VLayout left = new VLayout();
	// 리스트와 속성 레이아웃
	protected VLayout right = new VLayout();

	// 상단 버튼 레이아웃
	protected HLayout middle = new HLayout();

	// 검색 레이아웃
	private VLayout searchLayout = new VLayout();
	// 리스트 canvas
	protected Canvas listingPanel;
	// 문서
	protected SDocument document;
	// 폴더
	protected SFolder folder;

	// 트리와 메뉴 버튼을 셋팅
	// 20130727, junsoo, public -> private으로 변경
	private DocumentsMenu documentsMenu;

	// protected Button btnCheckOutList;
	// protected Button favoriteButton;
	// protected Button trashButton;

	// 경로표시
	protected TrackPanel trackPanel;

	// 즐겨찾기나 대쉬보드에서 선택할 문서 아이디
	public long expandDocid = 0;

	// 검색여부
	public boolean bSearch = false;
	// 반출조회여부
	public boolean bCheckSearch = false;

	// 마지막으로 속성을 조회한 docid 계속해서 속성을 조회하는것을 막음.
	// private long strLastDocId = 0;

	// Search Item 구성 Form
	private DynamicForm searchTrackFormDefault;
	private DynamicForm searchTrackFormExpire;
	private NormalSearchItems searchItemsDefault;
	private NormalSearchItems searchItemsExpire;

	// Search Item 저장 변수
	HashMap<String, FormItem> itemMapDefault;
	HashMap<String, FormItem> itemMapExpire;

	// 폴더내 검색시 true
	private boolean isSearch = false;

	private String draftRights;
	

	

	// Folder내 필터 검색 옵션 저장용 Map 변수
	// private Map<String, SSearchOptions> folderFilter = new HashMap<String,
	// SSearchOptions>();
	public static DocumentsPanel get() {
		if (instance == null) {
			instance = new DocumentsPanel();
		}
		return instance;
	}

	public static DocumentsPanel getNew() {
		instance = null;
		instance = new DocumentsPanel();
		return instance;
	}

	public DocumentsPanel() {
		Session.get().addFolderObserver(this);
		// 20130730, junsoo
		Session.get().addDocumentObserver(this);

		setWidth100();
		setHeight100();
		setShowEdges(false);

		// 리스팅패널 생성
		listingPanel = new Label("");
		listing.setWidth100();
		listing.setHeight100();
		listing.setShowResizeBar(false);
		listing.addMember(listingPanel);

		// 왼쪽 트리 메뉴 생성
		prepareMenu();
		// 제목 생성
		prepareLabel();

		// actions 초기화
		initActions();
		initSearch();
		right.setWidth100();
		// 제목 삽입

		middle.setHeight(28);
		middle.setWidth100();

		// toolbar 추가
		setDocumentActionType(-1);

		// initDefaultLayout();
		right.addMember(trackPanel);
		right.addMember(middle);
		right.addMember(searchLayout);
		right.addMember(listing);
		// right.hide();
		// right.addMember(searchPanel);
		// 속성 삽입
		// right.addMember(details);

		left.setWidth("15%");
		left.setHeight100();
		left.setMembersMargin(5);
		left.setMembers(documentsMenu);
		// left.setMembers(documentsMenu, searchMenu);
		// left.setMembers(documentsMenu, favoriteButton, trashButton,
		// btnCheckOutList);
		left.setShowResizeBar(true);

		setMembers(left, right);
		// addMember(defaultLayout);
		// 폴더정보를 표시하고 포커스를 주기위해 패널을 그린다.
		// 또한 그리드 패널이 먼저 그려지지 않은 상태에서는
		// 리스트에 파일일 확장 명령또한 먹지 않으므로 이러한 명령들에는
		// 항상 해당 레코드 정보를 포함한 그리드나 폴더가 먼저 그려져야 한다.
		draw();
		// Search Form 초기화
		// initSearch();
		// // 폴더 셋팅
		// try{
		// documentsMenu.refresh();
		// // trackPanel.setIcon(ItemFactory.newImgIcon("mydoc.png").getSrc());
		// }catch(Exception ex){
		// SC.warn(ex.getMessage());
		// }
	}

	/**
	 * search Bar 초기화
	 * */
	private void initSearch() {
		searchItemsDefault = new NormalSearchItems(NormalSearchItems.documentt);
		searchItemsExpire = new NormalSearchItems(NormalSearchItems.documentt);
		itemMapDefault = searchItemsDefault.getSearchItemsMap();
		itemMapExpire = searchItemsExpire.getSearchItemsMap();
		initSearchForm();
	}

	/**
	 * Search Form의 Item을 초기화한다.
	 * */
	private void initSearchForm() {
		searchTrackFormDefault = new DynamicForm();
		SearchUtil.initForm(searchTrackFormDefault, 7, 600);
		searchTrackFormDefault.setItems(itemMapDefault.get("title"),
				itemMapDefault.get("docType"),
				itemMapDefault.get("expireDate"),
				itemMapDefault.get("fexpireDate"),
				itemMapDefault.get("commonDateItem"),
				itemMapDefault.get("bexpireDate"),
				itemMapDefault.get("search"), itemMapDefault.get("keyword"),
				itemMapDefault.get("size"), itemMapDefault.get("createDate"),
				itemMapDefault.get("fcreateDate"),
				itemMapDefault.get("commonDateItem"),
				itemMapDefault.get("bcreateDate"),
				itemMapDefault.get("initialize"));
		itemMapDefault.get("keyword").setStartRow(true);
		itemMapDefault.get("fexpireDate").setStartRow(false);
		itemMapDefault.get("search").setStartRow(false);
		itemMapDefault.get("initialize").setStartRow(false);

		searchItemsDefault.setSearchOption(Constants.SEARCH_PLACE_DEFAULT);
		searchTrackFormDefault.hide();
		searchLayout.addMember(searchTrackFormDefault);

		searchTrackFormExpire = new DynamicForm();
		SearchUtil.initForm(searchTrackFormExpire, 7, 600);
		searchTrackFormExpire.setItems(itemMapExpire.get("title"),
				itemMapExpire.get("owner"), itemMapExpire.get("expireDate"),
				itemMapExpire.get("fexpireDate"),
				itemMapExpire.get("commonDateItem"),
				itemMapExpire.get("bexpireDate"), itemMapExpire.get("search"),
				itemMapExpire.get("fileName"), itemMapExpire.get("keyword"),
				itemMapExpire.get("createDate"),
				itemMapExpire.get("fcreateDate"),
				itemMapExpire.get("commonDateItem"),
				itemMapExpire.get("bcreateDate"),
				itemMapExpire.get("initialize"));
		itemMapExpire.get("fileName").setStartRow(true);
		itemMapExpire.get("fexpireDate").setStartRow(false);
		itemMapExpire.get("search").setStartRow(false);
		itemMapExpire.get("initialize").setStartRow(false);

		// itemMapExpire.get("fileName").setColSpan(4);
		searchItemsExpire.setSearchOption(Constants.SEARCH_PLACE_SHAREDTRASH);
		searchTrackFormExpire.hide();
		searchLayout.addMember(searchTrackFormExpire);

		searchLayout.setAutoWidth();
		searchLayout.hide();
	}

	// TODO : init actions
	public void initActions() {
		DocumentActionUtil.get().createAction("add", "actions_upload",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnAddClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);
		
		
		//20140425육용수
		if(Session.get().getInfo().getConfig("gui.lock.scan").equals("false"))
		{
		DocumentActionUtil.get().createAction("doScan", "doScan",
				"scan", false, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {			
						ScanStarter.scan(Session.get().getCurrentFolder());
					}
				}, DocumentActionUtil.TYPE_SHARED);
		
		DocumentActionUtil.get().createAction("viewScan", "viewScan",	"viewscan", true, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)	.getCheckedRecord();
						String id = "";
						for(Record record : selectedGrid)
						{
							if(record != null)
							id = record.getAttribute("id");
						}
						ScanStarter.view(id);
					}
				}, DocumentActionUtil.TYPE_SHARED);
		}
		
		


		DocumentActionUtil.get().createAction(
				"delete",
				"actions_delete",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						// mydoc & shared doc delete
						// if(documentsMenu.sectionIsExpanded(Constants.SECTION_NAME_MYDOC)
						// ||
						// documentsMenu.sectionIsExpanded(Constants.SECTION_NAME_SHARED))
						if (isCurrentFolderType(Constants.FOLDER_TYPE_MYDOC)|| isCurrentFolderType(Constants.FOLDER_TYPE_SHARED))
							onBtnDelClick();
						// xvarm delete 미적용
						// else if
						// (isCurrentFolderType(Constants.FOLDER_TYPE_XVARM))
						// SC.warn(I18N
						// .message("Sorry not ready to delete Xvarm data!"));
						else
							// 북마크 삭제
							onBtnDelBookmarkClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		// 기안은 공유 폴더에서만 사용가능하게 함.(내 폴더의 경우 모든 권한이 있으므로)
		DocumentActionUtil.get().createAction("approve_request", "draft",
				"approval_act", true, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						showApproveRequestPopup();
					}
				}, DocumentActionUtil.TYPE_SHARED, DocumentActionUtil.TYPE_MYDOC);
		

		DocumentActionUtil.get().createAction("delete_bookmark", "delete",
				"actions_delete", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnDelBookmarkClick();
					}
				}, DocumentActionUtil.TYPE_FAVOR);
		
		DocumentActionUtil.get().createAction("download", "actions_download",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						// 문서 다운로드
						onBtndownClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED);

		if (Feature.isEnbaled(Feature.CHECKOUT))
			DocumentActionUtil.get().createAction("checkin", "document_in",
					new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onBtnCarryClick();
						}
					}, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED,
					DocumentActionUtil.TYPE_CHECKED);

		if (Feature.isEnbaled(Feature.CHECKOUT))
			DocumentActionUtil.get().createAction("checkout", "document_out",
					new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onBtnCarryoutClick();
						}
					}, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED);

		if (Feature.isEnbaled(Feature.CHECKOUT))
			DocumentActionUtil.get().createAction("cancelcheckout",
					"document_inout", new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onBtnUnlockClick();
						}
					}, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED,
					DocumentActionUtil.TYPE_CHECKED);

		DocumentActionUtil.get().createAction("favorites", "favorite",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnFavoriteClick();
					}

				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED);

		// 편집 관련
		DocumentActionUtil.get().createAction("copy", "actions_copy",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnCopyClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		DocumentActionUtil.get().createAction("paste", "paste",
				"actions_paste", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnPasteClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		DocumentActionUtil.get().createAction("pasteTo", "second.pasteTo",
				"actions_paste", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnPasteToClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		DocumentActionUtil.get().createAction("move", "move", "actions_cut",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnMoveClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		DocumentActionUtil.get().createAction("reload", "reload", "reload",
				false, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnReloadClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED, DocumentActionUtil.TYPE_XVARM,
				DocumentActionUtil.TYPE_CHECKED, DocumentActionUtil.TYPE_FAVOR,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		// DocumentActionUtil.get().createAction("props", "document-properties",
		// new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// if(details.isVisible()){
		// details.hide();
		// listing.setHeight100();
		// listing.redraw();
		// }else{
		// details.show();
		// }
		// }
		// });
		DocumentActionUtil.get().createAction("lock", "lock",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnLockClick();
					}
				}, DocumentActionUtil.TYPE_SHARED);

		DocumentActionUtil.get().createAction("unlock", "lock_open",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnUnlockClick();
					}
				}, DocumentActionUtil.TYPE_SHARED);

		// 20130820, junsoo, template 문서 기능 제거. (좀 더 설계가 필요함)
		if (false) {
			DocumentActionUtil.get().createAction("downloadtemplate",
					"second.downTemplate", "actions_download",
					new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onTemplateDownload();
						}
					}, DocumentActionUtil.TYPE_SHARED);

			DocumentActionUtil.get().createAction("settemplate",
					"second.setDefaultTemplate", "actions_upload",
					new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onBtnSetTemplateClick();
						}
					}, DocumentActionUtil.TYPE_SHARED);
		}

		// 휴지통 전용 액션
		DocumentActionUtil.get().createAction("restore", "data_into",
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnRestoreClick();
					}
				}, DocumentActionUtil.TYPE_TRASH,
				DocumentActionUtil.TYPE_SHARED_TRASH);

		DocumentActionUtil.get().createAction("expire", "delete",
				"actions_delete", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnExpireClick();
					}
				}, DocumentActionUtil.TYPE_TRASH,
				DocumentActionUtil.TYPE_SHARED_TRASH);

		// 20140207, junsoo, 검색으로 변경하여 기능 제거함
		// DocumentActionUtil.get().createAction("reload_trash", "reload",
		// "reload", false, new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// // for (int i = 0; i < params.length; i++) {
		// // Object object = params[i];
		// //// System.out.println(object);
		// // System.out.println("zzzzzz");
		// // }
		// onBtnReloadTrashClick();
		// }
		// }, DocumentActionUtil.TYPE_TRASH,
		// DocumentActionUtil.TYPE_SHARED_TRASH);

		// 즐겨찾기
		DocumentActionUtil.get().createAction("goto", "client.goto",
				"actions_goto", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnGotoClick();
					}
				}, DocumentActionUtil.TYPE_FAVOR);
		//
		// // 폐기문서함 전용 메뉴
		// 배치로 폐기시키므로 기능 제거함.
		// DocumentActionUtil.get().createAction("expire_expire", "delete",
		// "actions_delete", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onBtnExpireClick();
		// }
		// }, DocumentActionUtil.TYPE_EXPIRED);

		// 폐기문서함 restore
		DocumentActionUtil.get().createAction("expire_restore", "restore",
				"data_into", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnExpireRestoreClick();
					}
				}, DocumentActionUtil.TYPE_EXPIRED);

		// 20130829, junsoo, 이메일 전송
		if (Util.getSetting("setting.email")) {
			DocumentActionUtil.get().createAction("sendAsEmail", "sendAsEmail",
					"email_edit", true, new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onBtnEmailClick();
						}
					}, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED);
		}

		DocumentActionUtil.get().createAction("message", "message", "mail",
				true, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						showMessagePopup();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED);

		// 필터 검색
		DocumentActionUtil.get().createAction("filter", "filter", "filter",
				false, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnFilterClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_EXPIRED,
				DocumentActionUtil.TYPE_FOLDER_SHARED
		// , DocumentActionUtil.TYPE_TRASH ,
		// DocumentActionUtil.TYPE_SHARED_TRASH
				);

		// TODO: XVARM 전용 액션
		// DocumentActionUtil.get().createAction("download_xvarm", "download",
		// "download", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// // 선택된 리스트 얻기
		// ListGridRecord[] selectedGrid = ((DocumentsListPanel)
		// listingPanel).gridXvarm.getSelectedRecords();
		// if (selectedGrid.length < 1) {
		// SC.say(I18N.message("youmustselectdocument"));
		// return;
		// }else if(selectedGrid.length > 1){
		// SC.say(I18N.message("youmustselectonedocument"));
		// return;
		// }
		//
		// WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" +
		// Session.get().getSid() +
		// "&elementId=" + ((DocumentsListPanel)
		// listingPanel).gridXvarm.getSelectedRecord().getAttribute("elementid")
		// );
		// }
		// },DocumentActionUtil.TYPE_XVARM);

		// 다큐먼트 파일선택 전용
		// DocumentActionUtil.get().createAction("view_file", "view",
		// "download", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onFileView((Long) params[0], (String) params[1]);
		// }
		// }, DocumentActionUtil.TYPE_FILE);
		//
		// DocumentActionUtil.get().createAction("open_file",
		// "second.client.openByOS", "open", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// }
		// }, DocumentActionUtil.TYPE_FILE);
		//
		// DocumentActionUtil.get().createAction("download_file", "download",
		// new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onFileDownload((Long) params[0], (String) params[1]);
		// }
		// }, DocumentActionUtil.TYPE_FILE);

		DocumentActionUtil.get().createAction("properties", "properties",
				"cog_go", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnPropertiesClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_CHECKED,
				DocumentActionUtil.TYPE_EXPIRED, DocumentActionUtil.TYPE_TRASH,
				DocumentActionUtil.TYPE_SHARED_TRASH,
				DocumentActionUtil.TYPE_FAVOR,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		// 20130816, junsoo, 보기옵션 구현.
		// 20130906, junsoo, 너무 느려서 기능 삭제함.
		// 확장
		// DocumentActionUtil.get().createAction("list_expand", "expandAllList",
		// "application_side_expand", false, new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onToggleListExpansion();
		// }
		// }, DocumentActionUtil.TYPE_MYDOC, DocumentActionUtil.TYPE_SHARED,
		// DocumentActionUtil.TYPE_CHECKED,
		// DocumentActionUtil.TYPE_EXPIRED, DocumentActionUtil.TYPE_TRASH,
		// DocumentActionUtil.TYPE_SHARED_TRASH,
		// DocumentActionUtil.TYPE_FAVOR);

		// DocumentActionUtil.get().createAction("list_detail", "list_detail",
		// "list_detail", false, new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onListViewChangeClick("detail");
		// }
		// }, DocumentActionUtil.TYPE_MYDOC,
		// DocumentActionUtil.TYPE_SHARED,
		// DocumentActionUtil.TYPE_FOLDER_SHARED);

		// DocumentActionUtil.get().createAction("list_icon", "list_icon", new
		// DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// // onBtnLockClick();
		// }
		// }, DocumentActionUtil.TYPE_MYDOC,
		// DocumentActionUtil.TYPE_SHARED);

		if (Feature.isEnbaled(Feature.THUMBNAIL))
			DocumentActionUtil.get().createAction("list_thumbnail",
					"list_thumbnail", "list_thumbnail", false,
					new DocumentAction() {
						@Override
						protected void doAction(Object[] params) {
							onListViewChangeClick("thumbnail");
						}
					}, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED,
					DocumentActionUtil.TYPE_FOLDER_SHARED);

		// TODO
		// if(!GWT.isScript()){
		// //TODO: 보안관리
		// DocumentActionUtil.get().createAction("security", "security", null,
		// true, new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// setDocSecurityControl();
		// }
		// }, DocumentActionUtil.TYPE_SHARED);
		// }

		DocumentActionUtil.get().createAction("sort", "sort", null, false,
				new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_CHECKED,
				DocumentActionUtil.TYPE_EXPIRED, DocumentActionUtil.TYPE_TRASH,
				DocumentActionUtil.TYPE_SHARED_TRASH);

		// 20140106, junsoo, drag & drop area 추가
		InstanceHandler<DefaultCanvasItem> instanceHandler = new InstanceHandler<DefaultCanvasItem>() {
			@Override
			public DefaultCanvasItem getInstance() {
				DefaultCanvasItem item = new DefaultCanvasItem("DragAndDrop",
						false) {
					@Override
					public Canvas getCanvas() {
						FileDropArea area = new FileDropArea(150, 20);
						area.setUploadCompleteHandler(new ReturnHandler() {
							@Override
							public void onReturn(Object param) {
								refresh();
							}
						});
						return area;
					}
				};
				return item;
			}
		};

		boolean isDragAndDrop = Util.getSetting("setting.toolbar.DragAndDrop");
		if (isDragAndDrop)
			DocumentActionUtil.get().createToolItem("DragAndDrop",
					instanceHandler, DocumentActionUtil.TYPE_MYDOC,
					DocumentActionUtil.TYPE_SHARED);
	}

	private void onListViewChangeClick(String method) {
		// boolean isThumnail = false;
		// if(method.equals("detail")){
		// isThumnail = false;
		// }else if(method.equals("thumbnail")){
		// isThumnail= true;
		// }
		DocumentActionUtil.get().changeThumbnailStatus();
		boolean bThumbnail = DocumentActionUtil.get().isThumbnail();
		if (bThumbnail)
			((DocumentsListPanel) listingPanel).getGrid().setThumbnailInfo();
		((DocumentsListPanel) listingPanel).getGrid().setRecordsExpanded(false);
		((DocumentsListPanel) listingPanel).getGrid().redraw();
	}

	/**
	 * 메시지 전송
	 */
	private void showMessagePopup() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		List<SDocument> list = new ArrayList<SDocument>();
		for (int i = 0; i < selectedGrid.length; i++) {
			list.add((SDocument) selectedGrid[i]
					.getAttributeAsObject("document"));
		}

		MessagePopup msgPopup = new MessagePopup(Session.get().getSid(),
				Constants.MESSAGE_CONTAIN_FILES, 0, list);
		msgPopup.show();
	}

	/**
	 * 기안 승인 및 등록 창을 띄운다
	 * */
	private ApproveRequestDialog approvePopup;

	public ApproveRequestDialog getApprovePopup() {
		return approvePopup;
	}

	private void showApproveRequestPopup() {
		approvePopup = new ApproveRequestDialog();

		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// 선택된 Record가 있을 경우
		if (selectedGrid.length > 0) {
			SDocument doc = (SDocument) selectedGrid[0]
					.getAttributeAsObject("document");

			approvePopup.show(doc, draftRights);
		}
		// 선택된 Record가 없을 경우(등록)
		else {
			approvePopup.show(null, draftRights);
		}
	}

	// 20130829, junsoo, email 전송.
	private void onBtnEmailClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// 선택된 파일이 없어도 메일 전송 가능함.(첨부파일 없이 전송)
		// if (selectedGrid == null || selectedGrid.length < 1) {
		// SC.say(I18N.message("nonecheckeddoc"));
		// return;
		// }

		List<SDocument> list = new ArrayList<SDocument>();
		// 첨부파일 포함 전송
		if (selectedGrid.length > 0) {
			for (int i = 0; i < selectedGrid.length; i++) {
				list.add((SDocument) selectedGrid[i]
						.getAttributeAsObject("document"));
			}
			EmailDialog window = new EmailDialog(list,
					selectedGrid[0].getAttribute("titlenm"));
			window.show();
		}
		// 첨부파일 없이 전송
		else {
			EmailDialog window = new EmailDialog(list,
					I18N.message("sendAsEmail"));
			window.show();
		}
	}

	// // expand all grid's record
	// private void onToggleListExpansion(){
	// if (listingPanel instanceof DocumentsListPanel) {
	// boolean expanded =
	// ((DocumentsListPanel)listingPanel).getGrid().isRecordsExpanded();
	// ((DocumentsListPanel)listingPanel).getGrid().setRecordsExpanded(!expanded);
	// }
	// }

	/**
	 * 선택된 폴더에 해당하는 Search Bar를 보여준다.
	 * */
	private void showSearchForm(int searchPlace) {
		switch (searchPlace) {
		case DocumentActionUtil.TYPE_MYDOC:
		case DocumentActionUtil.TYPE_SHARED:
		case DocumentActionUtil.TYPE_FOLDER_SHARED:
		case DocumentActionUtil.TYPE_FAVOR:
		case DocumentActionUtil.TYPE_CHECKED:
		case DocumentActionUtil.TYPE_APPROVE_ALL:
		case DocumentActionUtil.TYPE_APPROVE_COMPLETE:
		case DocumentActionUtil.TYPE_APPROVE_REQUEST:
		case DocumentActionUtil.TYPE_APPROVE_STANDBY:
			searchItemsDefault.setFolder(folder);
			searchTrackFormDefault.show();
			searchTrackFormExpire.hide();
			searchLayout.hide();
			break;
		case DocumentActionUtil.TYPE_EXPIRED:
		case DocumentActionUtil.TYPE_SHARED_TRASH:
			searchTrackFormDefault.hide();
			searchTrackFormExpire.show();
			searchLayout.show(); // 공유휴지통은 검색을 통해서만 가능하므로 검색바 표시
			break;
		case DocumentActionUtil.TYPE_TRASH:
			searchTrackFormDefault.hide();
			searchTrackFormExpire.show();
			searchLayout.hide();
			break;
		}
	}

	// 20130727, junsoo, goto 해당 위치로.
	private void onBtnGotoClick() {
		// 20130821 taesu, 선택된 문서가 없을경우 동작되지 않게함.
		if (((DocumentsListPanel) listingPanel).getCheckedRecord().length > 0) {
			SDocument[] docs = DocumentActionUtil.get().getCurrentDocuments();
			if (docs != null && docs.length > 0) {
				expandDocid = docs[0].getId();
				documentsMenu.expandFolder(docs[0].getFolder());
				return;
			}

			SFolder[] folders = DocumentActionUtil.get()
					.getCurrentFolderItems();
			if (folders != null && folders.length > 0) {
				expandDocid = folders[0].getId();
				documentsMenu.expandFolder(folders[0]);
				return;
			}
		}
	}

	// private void onFileView(Long docId, String elementId) {
	// Record[] selectedGrid = ((DocumentsListPanel)
	// listingPanel).getCheckedRecord();
	// if (selectedGrid == null) {
	// SC.say(I18N.message("youmustselectdocument"));
	// return;
	// }
	// if (selectedGrid.length != 1)
	// SC.say(I18N.message("youmustselectonedocument"));
	//
	// SDocument doc = (SDocument)
	// selectedGrid[0].getAttributeAsObject("document");
	//
	// // Util.preview(docId, elementId);
	// PreviewPopup view = new PreviewPopup(docId, elementId,
	// doc.getFolder().isPrint());
	// view.show();
	// }

	// /**
	// * 다운로드 페이지를 호출한다.
	// */
	// private void onFileDownload(Long docId, String elementId) {
	// // GWT.log("baseURL : " + GWT.getHostPageBaseURL());
	// Util.download(docId, elementId);
	// // if (elementId == null) {
	// // // 여러건 다운로드
	// // WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" +
	// Session.get().getSid() + "&docId=" + docId);
	// // } else {
	// // // 한건다운로드
	// // WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" +
	// Session.get().getSid() + "&docId=" + docId + "&elementId=" + elementId);
	// // }
	// }

	private void onTemplateDownload() {
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length != 1)
			SC.say(I18N.message("youmustselectonedocument"));

		SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");

		if (doc.getTemplateId() == null)
			Log.warn(I18N.message("second.templateIsNotSetted"));

		Util.downloadTemplate(doc.getTemplateId());
		// WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" +
		// Session.get().getSid() + "&templateId=" + doc.getTemplateId());
	}

	// template 설정
	private void onBtnSetTemplateClick() {
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length != 1)
			SC.say(I18N.message("youmustselectonedocument"));

		final SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");

		TemplateSelectorDialog selector = new TemplateSelectorDialog();
		selector.setReturnHandler(new ReturnHandler<Long>() {
			@Override
			public void onReturn(Long param) {
				// TODO: set!
				ServiceUtil.template().setTemplateDoc(Session.get().getSid(),
						param, doc.getId(), new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								Log.infoWithPopup(
										I18N.message("second.client.command.setTemplate"),
										I18N.message("second.client.successfully"));
							};

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, false);
							}
						});
			}
		});
		selector.show();
	}

	// 버튼 셋팅
	protected void setButton(Button btn, int width, int height) {
		if (width == 0) {
			btn.setWidth(50);
		} else
			btn.setWidth(width);
		btn.setShowRollOver(true);
		btn.setShowDisabled(true);
		btn.setShowDown(true);
	}

	// 메뉴 패널 생성
	protected void prepareMenu() {
		documentsMenu = new DocumentsMenu();		
	}

	// 제목 생성
	protected void prepareLabel() {
		trackPanel = new TrackPanel("", null);
		trackPanel.setHeight(12);
	}

	public void showListPanel() {
		if (!(listingPanel instanceof DocumentsListPanel)) {
			listing.removeMember(listingPanel);
			listingPanel = new DocumentsListPanel();
			listing.addMember(listingPanel);
		}

		((DocumentsListPanel) listingPanel).refresh();
		listing.redraw();
	}

	// approval list 패널 보기 - goodbong
	private void showApprovalPanel(String threadType) {
		if (!(listingPanel instanceof ApprovalPanel)) {
			listing.removeMember(listingPanel);
			listingPanel = ApprovalPanel.get();
			listing.addMember(listingPanel);
		}
		((ApprovalPanel) listingPanel).refresh(threadType);
		listing.redraw();
	}

	// 즐겨찾기
	public void onFavoriteBtnClick() {
		// toolbar 변경
		setDocumentActionType(DocumentActionUtil.TYPE_FAVOR);
		// DocumentsGrid grid = ((DocumentsListPanel) listingPanel).getGrid();
		// grid.fields.removeAll(grid.attrFields);
		// grid.setFields(grid.fields.toArray(new ListGridField[0]));
		
		showListPanel();
	}

	// 휴지통
	public void onTrashBtnClick() {
		// toolbar 재추가
		setDocumentActionType(DocumentActionUtil.TYPE_TRASH);

		showListPanel();
	}

	// 공유 휴지통
	public void onSharedTrashBtnClick() {
		setDocumentActionType(DocumentActionUtil.TYPE_SHARED_TRASH);

		showListPanel();
	}

	// // 메뉴버튼 생성
	// private void prepareMenuButton(){
	// // 즐겨찾기
	// favoriteButton = new Button(I18N.message("favorites"));
	// setButton(favoriteButton, 236, 0);
	// favoriteButton.setWidth100();
	// favoriteButton.addClickHandler(new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// onFavoriteBtnClick();
	// }
	// });
	//
	// // 체크아웃
	// btnCheckOutList = new Button(I18N.message("CheckOutList"));
	// setButton(btnCheckOutList, 236, 0);
	// btnCheckOutList.setWidth100();
	// btnCheckOutList.addClickHandler(new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// showCheckedSearch();
	// }
	// });
	//
	// // 휴지통
	// trashButton = new Button(I18N.message("trash"));
	// setButton(trashButton, 236, 0);
	// trashButton.setWidth100();
	// trashButton.addClickHandler(new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// onTrashBtnClick();
	// }
	// });
	//
	// }

	// 문서등록 패널 생성 띄우기
	private void onBtnAddClick() {
		// final DocumentsUploader uploader = new
		// DocumentsUploader(DocumentsPanel.this);
		// uploader.show();

		final DocumentUploadDialog uploader = new DocumentUploadDialog(this);
		uploader.show();

	}

	// 즐겨찾기 삭제
	private void onBtnDelBookmarkClick() {
		// 선택 리스트 조회
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		SC.confirm(I18N.message("docpanedelmsg"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					try {
						// 문서 아이디 저장
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}

						// 즐겨찾기 삭제
						ServiceUtil.document().deleteBookmarks(
								Session.get().getSid(), docid,
								new AsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										SC.say(I18N
												.message("event.deleted.message"));
										onFavoriteBtnClick();
									}

									@Override
									public void onFailure(Throwable caught) {
										SCM.warn(caught);
									}
								});
					} catch (Throwable ex) {
						SC.warn(ex.getMessage());
						return;
					}
				} else {
					return;
				}
			}
		});
	}

	// 문서 다운로드
	private void onBtndownClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// 두건이상 처리불가
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		} else if (selectedGrid.length > 1) {
			SC.say(I18N.message("just1document"));
			return;
		}

		// 한건만 처리
		// long[] docid = new long[ selectedGrid.length ];
		// for (int i = 0; i < docid.length; i++) {
		// docid[i] =
		// Long.parseLong(selectedGrid[i].getAttribute("id"));
		// }

		long docid = 0;
		docid = selectedGrid[0].getAttributeAsLong("id");
		// } else {
		// if ("folder".equals(selectedGrid[0].getAttribute("type"))) {
		// SC.say(I18N.message("thisbookmarktypeisfolder"));
		// return;
		// }
		// docid = selectedGrid[0].getAttributeAsLong("targetid");
		// }
		Util.downloadAsFrame(docid, null);
		// WindowUtils.openUrl(GWT.getHostPageBaseURL()+ "download?sid=" +
		// Session.get().getSid() + "&docId=" + docid);

	}

	// 즐겨찾기
	private void onBtnFavoriteClick() {
		// 선택된 리스트 얻기
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}

		// 문서 아이디 얻기
		long[] docid = new long[selectedGrid.length];
		for (int i = 0; i < docid.length; i++) {
			docid[i] = Long.parseLong(selectedGrid[i].getAttribute("id"));
		}

		ServiceUtil.document().addBookmarks(Session.get().getSid(), docid, 0,
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						SC.say(I18N.message("addbookmarksuccess"));
						((DocumentsListPanel) listingPanel).getGrid()
								.deselectAllRecords();
					}

					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});
	}

	// reload
	private void onBtnReloadClick() {
		// 20140306, junsoo, 즐겨찾기, 체크아웃리스트는 refresh가 필요함
		int menuType = DocumentActionUtil.get().getActivatedMenuType();
		switch (menuType) {
		case DocumentActionUtil.TYPE_FAVOR:
					
		case DocumentActionUtil.TYPE_CHECKED:
			((DocumentsListPanel) listingPanel).refresh();
			break;

		default:
			break;
		}
		// yuk yong soo 20140317 페이지 리프레시 할때 페이징 사이즈 유지
		
		
		
		// ((DocumentsListPanel) listingPanel).refresh();
		if (folder != null)
			Session.get().setCurrentFolder(folder);
		
		// isSearch = false;
		// if(searchLayout.isVisible()){
		// searchLayout.hide();
		// searchItemsDefault.resetItems(true);
		// }
		// ((DocumentsListPanel) listingPanel).refresh();
	}

	private void redrawList() {
		((DocumentsListPanel) listingPanel).redraw();
	}

	// 붙여넣기
	// 클립보드에 복사된 문서아이디를 사용해서
	// 서비스에 넘겨 해당 폴더로 복사 한다.
	private void onBtnPasteClick() {
		// 선택된 문서 가져오기
		if (Clipboard.getInstance().isEmpty()) {
			SC.warn(I18N.message("noiteminclipboard"));
			return;
		}

		Waiting.show(I18N.message("pleaseWait"));

		final long[] docid = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (SDocument doc : Clipboard.getInstance()) {
			docid[i++] = doc.getId();
		}

		ServiceUtil.folder().paste(Session.get().getSid(), docid,
				Session.get().getCurrentFolder().getId(), Clipboard.COPY,
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						// 20130726, junsoo, 상태바 메시지로 변경.
						Log.info(I18N.message("second.client.actionPaste"),
								I18N.message("pastecomplite"));
						onBtnReloadClick();
						Waiting.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						Waiting.hide();
						Log.serverError(caught, true);
					}
				});
	}

	// 붙여넣기
	// 특정 폴더로 붙혀넣기.
	private void onBtnPasteToClick() {
		// 선택된 문서 가져오기
		if (Clipboard.getInstance().isEmpty()) {
			SC.warn(I18N.message("noiteminclipboard"));
			return;
		}

		final long[] docid = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (SDocument doc : Clipboard.getInstance()) {
			docid[i++] = doc.getId();
		}

		// 20130816, junsoo, 문서 선택 창 변경.
		ReturnHandler<SFolder> returnHandler = new ReturnHandler<SFolder>() {
			@Override
			public void onReturn(final SFolder folder) {
				ServiceUtil.folder().paste(Session.get().getSid(), docid,
						folder.getId(), Clipboard.COPY,
						new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								Log.infoWithPopup(
										I18N.message("second.client.command.pasteTo"),
										I18N.message("second.client.successfully"));
								onReloadRequest(folder);
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, true);
							}
						});

			}
		};

		FolderSelectorDialog folderSelector = FolderSelectorDialog.get();
		folderSelector.setReturnHandler(returnHandler);
		folderSelector.show();
	}

	// 이동
	private void onBtnMoveClick() {
		// 선택된 리스트 얻기
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}

		// 문서 아이디 얻기
		final long[] docids = new long[selectedGrid.length];
		for (int i = 0; i < docids.length; i++) {
			docids[i] = Long.parseLong(selectedGrid[i].getAttribute("id"));
		}

		// 문서 이동 패널 생성
		// final DocumentsMove dmove = new
		// DocumentsMove(Session.get().getSid(),docid, DocumentsPanel.this);
		// dmove.show();

		// 20130816, junsoo, 문서 선택 창 변경.
		ReturnHandler<SFolder> returnHandler = new ReturnHandler<SFolder>() {
			@Override
			public void onReturn(SFolder folder) {
				// 이동
				ServiceUtil.folder().paste(Session.get().getSid(), docids,
						folder.getId(), Clipboard.CUT,
						new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								// 성공표시
								onReloadRequest(Session.get()
										.getCurrentFolder());
								Log.infoWithPopup(I18N.message("move"), I18N
										.message("second.client.successfully"));
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, true);
							}
						});
			}
		};

		FolderSelectorDialog folderSelector = FolderSelectorDialog.get();
		folderSelector.setReturnHandler(returnHandler);
		folderSelector.show();

	}

	// 문서 복원
	private void onBtnRestoreClick() {
		// 선택 리스트
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// 문서 아이디 저장 & 폴더 아이디
		long[] docid = new long[selectedGrid.length];
		long[] folderid = new long[selectedGrid.length];
		for (int i = 0; i < docid.length; i++) {
			docid[i] = Long.parseLong(selectedGrid[i].getAttribute("id"));
			folderid[i] = Long.parseLong(selectedGrid[i]
					.getAttribute("folderid"));
		}

		ServiceUtil.document().restore(Session.get().getSid(), docid, folderid,
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						Log.infoWithPopup(I18N.message("restore"),
								I18N.message("second.client.successfully"));
						showListPanel();
						// onTrashBtnClick();
					}

					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});

	}

	public void onBtnCarryClick() {
		// 선택된 리스트 얻기
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			// Log.warn(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length > 1) {
			// Log.warn(I18N.message("youmustselectonedocument"));
			return;
		}

		SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");

		// 문서정보
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in 패널 생성
						final DocumentCheckinDialog checkinDialog = new DocumentCheckinDialog(
								result);
						checkinDialog.show();

						// // 문서타입을 조회한다.
						// // check in 패널 확장필드를 셋팅하기 위해서임.
						// // System.out.println("==== doctype : " +
						// // String.valueOf(result.getDocType() ));
						// documentCodeService.listXvarmIndexFieldsByDocTypeId(Session.get().getSid(),
						// result.getDocType(),
						// new AsyncCallback<List<String>>() {
						// @Override
						// public void onSuccess(
						// List<String> resultArray) {
						// // 확장필드명 배열
						// String[] strArray = new String[resultArray.size()];
						// for (int i = 0; i < strArray.length; i++) {
						// // 확장 필드명 저장
						// strArray[i] = resultArray.get(i).toString();
						// }
						//
						// // check in 패널 생성
						// final DocumentsCheckin checkinPanel = new
						// DocumentsCheckin(result,
						// DocumentsPanel.this,strArray);
						// checkinPanel.show();
						// }
						//
						// @Override
						// public void onFailure(Throwable caught) {
						// SCM.warn(caught);
						// }
						// });
					}
				});

	}

	// check out
	public void onBtnCarryoutClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}

		if (selectedGrid.length > 1) {
			// Log.warn(I18N.message("youmustselectonedocument"));
			return;
		}

		SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");

		// 문서정보
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in 패널 생성
						final DocumentCheckoutDialog dialog = new DocumentCheckoutDialog(
								result);
						dialog.show();
					}
				});
	}

	// 문서 Lock
	public void onBtnLockClick() {

		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length > 1) {
			// Log.warn(I18N.message("youmustselectonedocument"));
			return;
		}

		SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");

		// 문서정보
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in 패널 생성
						final DocumentLockDialog dialog = new DocumentLockDialog(
								result);
						dialog.show();
					}
				});

	}

	// 문서 속성보기
	public void onBtnPropertiesClick() {
		Record[] selectedGrid;
		selectedGrid = ((DocumentsListPanel) listingPanel).getCheckedRecord();

		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length > 1) {
			// Log.warn(I18N.message("youmustselectonedocument"));
			return;
		}

		if ((SDocument) selectedGrid[0].getAttributeAsObject("document") != null) {
			SDocument doc = (SDocument) selectedGrid[0]
					.getAttributeAsObject("document");
			document = doc;
			// DocumentPropertiesWindow.get().show();

			// 문서정보
			ServiceUtil.document().getByIdWithPermission(
					Session.get().getSid(), doc.getId(),
					new AsyncCallback<SDocument>() {
						@Override
						public void onFailure(Throwable caught) {
							SCM.warn(caught);
						}

						@Override
						public void onSuccess(final SDocument result) {
							boolean readonly = false;
							// 20130820, junsoo, 휴지통/즐겨찾기/폐기함은 속성에서 읽기만 가능!!
							if (DocumentActionUtil.TYPE_TRASH == DocumentActionUtil
									.get().getActivatedMenuType()
									|| DocumentActionUtil.TYPE_SHARED_TRASH == DocumentActionUtil
											.get().getActivatedMenuType()
									|| DocumentActionUtil.TYPE_EXPIRED == DocumentActionUtil
											.get().getActivatedMenuType()
									|| DocumentActionUtil.TYPE_FAVOR == DocumentActionUtil
											.get().getActivatedMenuType()
									|| DocumentActionUtil.TYPE_FOLDER_SHARED == DocumentActionUtil
											.get().getActivatedMenuType())
								readonly = true;

							Log.debug("[DocumentsPanel onBtnPropertiesClick]"
									+ "profile id : "
									+ ((result.getSecurityProfile() == null) ? "null"
											: result.getSecurityProfile()));

							// properties
							final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(
									result, readonly);
							dialog.show();
						}
					});
		}
	}

	// 문서 unlock
	public void onBtnUnlockClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length > 1) {
			// Log.warn(I18N.message("youmustselectonedocument"));
			return;
		}

		SDocument doc = (SDocument) selectedGrid[0]
				.getAttributeAsObject("document");
		final int status = doc.getStatus();

		// 문서정보
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in 패널 생성
						final DocumentUnlockDialog dialog;
						switch (status) {
						case SDocument.DOC_CHECKED_OUT:
							dialog = new DocumentCheckoutCancelDialog(result);
							dialog.show();
							break;
						case SDocument.DOC_LOCKED:
							dialog = new DocumentUnlockDialog(result);
							dialog.show();
						}
					}
				});

	}

	// 문서 복사
	public void onBtnCopyClick() {
		// 문서 복사
		final Record[] selectedGrid;

		// 선택된 문서 가져오기
		selectedGrid = ((DocumentsListPanel) listingPanel).getCheckedRecord();
		if (selectedGrid == null)
			return;

		// 잠긴문서 처리불가
		// 20140106na 후처리 중 문서 복사 불가
		for (Record rc : selectedGrid) {
			if ("lock".equals(rc.getAttribute("lock"))) {
				SC.warn(I18N.message("lockdocumentcantcopy"));
				return;
			}
			if (rc.getAttribute("mainDoc").contains(
					I18N.message("PostJobProcessing"))) {
				SC.warn(I18N.message("youcantcopyprocessingdocument"));
				return;
			}
		}

		// 클립보드 클리어
		Clipboard.getInstance().clear();
		for (int i = 0; i < selectedGrid.length; i++) {
			String id = selectedGrid[i].getAttribute("id");
			SDocument document = new SDocument();
			document.setId(Long.parseLong(id));
			document.setTitle(selectedGrid[i].getAttribute("titlenm"));

			// 클립보드에 아이디 저장
			Clipboard.getInstance().add(document);
			Clipboard.getInstance().setLastAction(Clipboard.COPY);
		}

		// 20130822, junsoo, grid 갱신
		((DocumentsListPanel) listingPanel).refreshSelectedRows();

		// 20130726, junsoo, 확인 창 없애고 상태바에 기록 남기기
		// SC.say(I18N.message("copycomplite"));
		Log.info(I18N.message("second.client.actionCopy"),
				I18N.message("second.client.copyCompleted"));
	}

	// 문서 삭제
	public void onBtnDelClick() {
		// 선택한 문서가 존재하는지 여부
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)	.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// 잠긴문서
		for (Record rc : selectedGrid) {
			if ("lock".equals(rc.getAttribute("lock"))) {
				SC.warn(I18N.message("lockdocumentcantdelete"));
				return;
			}
		}

		SC.confirm(I18N.message("docpanedelmsg"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					try {
						// doc아이디 저장
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}

						// 문서 삭제 서비스
						ServiceUtil.document().delete(Session.get().getSid(),
								docid, new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
										SCM.warn(caught);
									}

									@Override
									public void onSuccess(Void result) {
										DocumentsPanel.this
												.onServiceComplite("success");
										// 폴더 재조회
										Session.get().setCurrentFolder(folder);
									}
								});

					} catch (Throwable ex) {
						SC.warn(ex.getMessage());
					}

				} else {
					return;
				}
			}
		});
	}

	public boolean isCurrentFolderType(int type) {
		if (listingPanel instanceof DocumentsListPanel)
			return (Session.get().getCurrentFolder().getType() == type);

		return false;
	}

	// 문서 폐기
	private void onBtnExpireClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// 20130822, junsoo, 경고메시지 강화
		String message = I18N.message("doexpire");
		
		if (DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED_TRASH)
		message = I18N.message("doexpireWithWarning");

		SC.confirm(message, new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					try {
						// doc아이디 저장
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}
						ServiceUtil.document().expire(Session.get().getSid(),
								docid, new AsyncCallback<Void>() {
									@Override
									public void onSuccess(Void arg0) {
										((DocumentsListPanel) listingPanel)
												.refresh();
										Log.infoWithPopup("OK!",I18N.message("second.client.successfully"));
									}

									@Override
									public void onFailure(Throwable arg0) {
										Log.serverError(arg0, true);
									}
								});

					} catch (Throwable ex) {
						SC.warn(ex.getMessage());
					}

				} else {
					return;
				}
			}
		});
	}

	/**
	 * 검색 버튼 선택시 검색 메뉴 활성화 유무 세팅
	 * */
	private void onBtnFilterClick() {
		if (searchLayout.isVisible()) {
			searchLayout.hide();

			// 2013-11-26 나용준
			// 필터를 숨길 때 필터에 입력되었던 값 제거
			searchItemsDefault.resetItems(true);
		} else
			searchLayout.show();
	}

	// 폐기 문서함 문서 restore
	private void onBtnExpireRestoreClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}
		SC.confirm(I18N.message("doexpireresotre"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					try {
						// doc아이디 저장
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}
						// folderId 저장
						long[] folderId = new long[selectedGrid.length];
						for (int i = 0; i < folderId.length; i++) {
							folderId[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("folderId"));
						}
						ServiceUtil.document().restoreExpired(
								Session.get().getSid(), docid, folderId,
								new AsyncCallbackWithStatus<Void>() {
									@Override
									public String getSuccessMessage() {

										return I18N
												.message("client.expireRestoreComplete");
									}

									@Override
									public String getProcessMessage() {
										return I18N
												.message("client.expireRestoreRequest");
									}

									@Override
									public void onSuccessEvent(Void result) {
										((DocumentsListPanel) listingPanel)
												.refresh();
										Log.infoWithPopup(
												I18N.message("restore"),
												I18N.message("second.client.successfully"));
									}

									@Override
									public void onFailureEvent(Throwable caught) {
										Log.serverError(caught, false);
									}
								});
					} catch (Throwable ex) {
						SC.warn(ex.getMessage());
					}

				} else {
					return;
				}
			}
		});
	}

	// 휴지통 비우기
	// private void onBtnCheckoutClick(){
	// SC.warn("not ready yet!");
	// }
	// 20131210 na 휴지통 새로고침을 공유문서 휴지통과 내 휴지통으로 구분하여 새로고침 추가
	// 재조회
	private void onBtnReloadTrashClick() {
		int menuType = DocumentActionUtil.get().getActivatedMenuType();

		if (menuType == DocumentActionUtil.TYPE_TRASH)
			onTrashBtnClick();
		else if (menuType == DocumentActionUtil.TYPE_SHARED_TRASH)
			onSharedTrashBtnClick();
	}

	// 폴더를 선택해서 폴더정보를 조회
	@Override
	public void onFolderSelected(SFolder folder) {
		Log.debug("[onFolderSelected] " + folder.getId());
		this.folder = folder;
		// 폴더 이동시 Thumbnail 선택상태 기본으로 변경.
		// DocumentActionUtil.get().changeThumbnailStatus(false);

		if (bSearch || bCheckSearch) {
			// setIconTrackPanel();
		}

		bSearch = false;
		bCheckSearch = false;

		try {
			refresh();
		} catch (Exception ex) {
			SC.warn(ex.getMessage());
		}
	}

	public void onSharedFolderSelected(SFolder folder) {
		Log.debug("[onSharedFolderSelected] " + folder.getId());
		this.folder = folder;
		// 폴더 이동시 Thumbnail 선택상태 기본으로 변경.
		// DocumentActionUtil.get().changeThumbnailStatus(false);
		bSearch = false;
		bCheckSearch = false;

		try {
			setDocumentActionType(DocumentActionUtil.TYPE_SHARED);
			showListPanel();
		} catch (Exception ex) {
			SC.warn(ex.getMessage());
		}
	}

	// 폴더에 따른 리스트와 폴더의 속성을 표시
	public void refresh() {
		// 리스트 조회
		updateListingPanel(folder);

		// 속성창이 열려있으면 폴더 속성 갱신
		FolderPropertiesWindow.get().refresh();
	}

	// 20130802, junsoo, 선택된 레코드만 refresh
	public void refreshSelectedRecords() {
		((DocumentsListPanel) listingPanel).updateSelectedRecords();
	}

	// 리스트 조회
	protected void updateListingPanel(SFolder folder) {
		// toolbar 재추가
		if (DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_FOLDER_SHARED)
			setDocumentActionType(getActionType(folder.getType()));
		else
			setDocumentActionType(DocumentActionUtil.TYPE_FOLDER_SHARED);

		// 20140213, junsoo, 검색이 아님을 설정
		setSearch(false);
		showListPanel();

	}

	// top패널에서의 조회
	public void showSearchResult(SSearchOptions searchOptions) {
		bSearch = true;

		// toolbar 재추가
		// setDocumentActionType(DocumentActionUtil.TYPE_SEARCH);

		((DocumentsListPanel) listingPanel).setSearchOptions(searchOptions);
		showListPanel();

	}

	// // TODO: right pannel member control
	// /**
	// * Right Pannel의 member show/hide 처리
	// *
	// * */
	// private void controlRight(boolean showMiddle, boolean showExpireTop) {
	// // 상단 바 컨트롤
	// if (showMiddle) middle.show();
	// else middle.hide();
	//
	// if (showExpireTop) middle.show();
	// else middle.hide();
	// }

	// Expire 문서함 조회
	public void onExpire() {
		setDocumentActionType(DocumentActionUtil.TYPE_EXPIRED);

		showListPanel();
	}

	// 승인 대기함 조회
	public void onApproveStandBy() {
		showApprovalPanel("ToApproval");

		// 20130827, junsoo, approvePanel에서 툴바 초기화 하므로, 툴바
		// 설정(setDocumentActionType 호출)을 나중에 해야함.
		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_STANDBY);
	}

	// 승인 요청함 조회
	public void onApproveRequest() {
		showApprovalPanel("Request");

		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_REQUEST);
	}

	// 승인 완료함 조회
	public void onApproveComplete() {
		showApprovalPanel("Completed");

		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_COMPLETE);
	}

	// LockUserIdAndStatus 조회
	public void showCheckedSearch() {
		bCheckSearch = true;
		// toolbar 재추가
		setDocumentActionType(DocumentActionUtil.TYPE_CHECKED);

		showListPanel();
	}

	// SheardList 조회
	public void showSharedList() {
		// TODO
		setDocumentActionType(DocumentActionUtil.TYPE_FOLDER_SHARED);

		showListPanel();
	}

	// 문서 정보 받아오기 xvarm
	// public void getByIdXvarm(long docId, String strId){
	// ecmService.getById(Session.get().getSid(), strId,
	// Session.get().getCurrentFolder().getName(), new
	// AsyncCallback<SDocument>() {
	//
	// @Override
	// public void onSuccess(final SDocument result) {
	// document = result;
	// // 확장필드명 배열
	// SContent[] scontent = result.getContents();
	// String[] strArray = scontent[0].getFieldNames();
	//
	// // 속성표시
	// // if (!(detailPanel instanceof DocumentDetailsPanel)) {
	// // details.removeMember(detailPanel);
	// // detailPanel = DocumentDetailsPanel.get(DocumentsPanel.this, true,
	// result, strArray, null);
	// // details.addMember(detailPanel);
	// // }
	// //
	// // if (detailPanel instanceof DocumentDetailsPanel) {
	// // ((DocumentDetailsPanel) detailPanel).setDocument(result, true,
	// strArray, null);
	// // details.redraw();
	// // }
	// }
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// SCM.warn(caught);
	// }
	// });
	// }

	// 20130730, session observer로부터 onDocumentSelected가 호출되므로 삭제함.
	// public void onSelectedDocument(long docId, String strId) {
	// if(isCurrentFolderType(Constants.FOLDER_TYPE_XVARM) && bSearch == false
	// && bCheckSearch == false){
	// // getByIdXvarm(docId, strId);
	// }else{
	// // 20130729, junsoo, 권한정보는 처음 조회할 때 부터 가져오도록 함.
	// // if(bSearch || bCheckSearch) {
	// // // 탑패널에서 조회시에는 각문서를 클릭할때만다 문서의
	// // // 권한을 가져와서 버튼을 설정한다.
	// // getByIdPermission(docId, strId);
	// // }else{
	// // if(strLastDocId != docId){
	// // getById(docId, strId);
	// // strLastDocId = docId;
	// // }
	// // }
	// DocumentPropertiesWindow.get().refresh();
	// }
	// }

	@Override
	public void onFolderSaved(SFolder folder) {
		// Nothing to do
	}

	@Override
	public void onFolderReload() {
	}

	@Override
	public void onServiceComplite(String message) {

		redrawList();
	}

	// 옵저버를 통해 문서가 저장되면 호출되는 함수
	// 한건만 갱신
	@Override
	public void onDocumentSaved(SDocument document) {
		// 20130802, junsoo, 문서가 속성창을 통해 저장되면 호출되어 해당 레코드만 refresh 되도록 함.
		Log.debug("DocumentsPanel : onDocumentSaved");

		DocumentsGrid grid = ((DocumentsListPanel) listingPanel).getGrid();
		ListGridRecord records = grid.getSelectedRecord();

		long templateforRecord = 0;
		long templateforDoc = 0;

		if(records == null){
			((DocumentsListPanel) listingPanel).refresh();
			return;
		}
			
		if (records.getAttributeAsLong("templateId") != null)
			templateforRecord = records.getAttributeAsLong("templateId");
		if (document.getTemplateId() != null)
			templateforDoc = document.getTemplateId();

		if (templateforDoc != templateforRecord)
			((DocumentsListPanel) listingPanel).refresh();
		else
			((DocumentsListPanel) listingPanel).updateSelectedRecords();
		// ((DocumentsListPanel) listingPanel).updateSelectedRecord(document);

	}

	// 문서가 선택되면 호출되는 함수
	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		Log.debug("onDocumentSelected");

		// 20130820, junsoo, DocumentPropertiesWindow 더이상 사용하지 않음. modal 로 변경하여
		// DocumentPropertiesDialog 사용함.
		// if (items != null && items.length > 0 && items[0].getType() ==
		// SRecordItem.TYPE_DOCUMENT) {
		// DocumentPropertiesWindow.get().refresh(items[0].getDocument());
		// } else {
		// DocumentPropertiesWindow.get().empty();
		// }
	}

	// 20130809, junsoo, 현재 보고 있는 폴더일 때만 갱신.
	// 리스트 전체 갱신.
	@Override
	public void onReloadRequest(SFolder folder) {
		Log.debug("DocumentsPanel : onReloadRequest");
		// 현재 보고 있는 폴더일 때만 갱신.
		if (folder.getId() == Session.get().getCurrentFolder().getId())
			((DocumentsListPanel) listingPanel).refresh();
	}

	// 20130725, junsoo, 툴바 변경
	private ToolStrip currentToolbar;

	public ToolStrip getCurrentToolbar() {
		return currentToolbar;
	}

	/**
	 * 현재 선택되어있는 폴더의 위치에 따라 Toolbar를 재구성한다.
	 * 
	 * @param Type
	 *            : DocumentActionUtil.*
	 * */
	public void changeToolbar(int type) {
		ToolStrip toolbar = DocumentActionUtil.get().getToolbar(type);
		// 기존과 같은 툴바이면 리턴.
		if (toolbar == null || currentToolbar == toolbar) {
			return;
		}

		if (currentToolbar != null && middle.hasMember(currentToolbar)) {
			middle.removeMember(currentToolbar);
		}

		currentToolbar = toolbar;
		middle.setMembers(currentToolbar);
	}

	public void setFolder(SFolder folder) {
		this.folder = folder;
	}

	/**
	 * Track Panel의 Icon과 Label 값을 변경한다.
	 * 
	 * @param Type
	 *            : DocumentActionUtil.*
	 * */
	public void setTrackPanel(int Type) {
		if (folder != null) {
			if (Type == DocumentActionUtil.TYPE_FOLDER_SHARED) {
				trackPanel.setIcon(ItemFactory.newImgIcon("sharedoc.png").getSrc());
				trackPanel.setTrack(" " + "/" +  folder.getName());
			} else {
				trackPanel.setTrack(" " +  folder.getPathExtended().replace("/root", ""));
				trackPanel.setDescription(folder.getDescription());
			}
		} else {
			trackPanel.setDescription("");
		}

		// 네비게이션 설정
		switch (Type) {
		case DocumentActionUtil.TYPE_MYDOC:
			trackPanel.setTrack(" "
					+ folder.getPathExtended().replace("/root", ""));
			trackPanel.setIcon(ItemFactory.newImgIcon("mydoc.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_SHARED:
			trackPanel.setTrack(" "
					+ folder.getPathExtended().replace("/root", ""));
			trackPanel.setIcon(ItemFactory.newImgIcon("sharedoc.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_XVARM:
			trackPanel.setIcon(ItemFactory.newImgIcon("ecm.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_FAVOR:
			trackPanel.setTrack(I18N.message("favorites"));
			trackPanel.setIcon(ItemFactory.newImgIcon("favorite.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_TRASH:
			trackPanel.setTrack(I18N.message("trash"));
			trackPanel.setIcon(ItemFactory.newImgIcon("garbage.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_SHARED_TRASH:
			trackPanel.setTrack(I18N.message("sharedtrash"));
			trackPanel.setIcon(ItemFactory.newImgIcon("garbage.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_EXPIRED:
			trackPanel.setTrack(I18N.message("second.client.expireddoc"));
			trackPanel.setIcon(ItemFactory.newImgIcon("garbage.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_CHECKED:
			trackPanel.setTrack(I18N.message("CheckOutList"));
			trackPanel.setIcon(ItemFactory.newImgIcon("document_out.png")
					.getSrc());
			break;
		case DocumentActionUtil.TYPE_APPROVE_STANDBY:
			trackPanel.setTrack(I18N.message("second.client.approveStandby"));
			trackPanel.setIcon(ItemFactory.newImgIcon("approval.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_APPROVE_REQUEST:
			trackPanel.setTrack(I18N.message("second.client.approveRequest"));
			trackPanel.setIcon(ItemFactory.newImgIcon("approval.png").getSrc());
			break;
		case DocumentActionUtil.TYPE_APPROVE_COMPLETE:
			trackPanel.setTrack(I18N.message("approveComplete"));
			trackPanel.setIcon(ItemFactory.newImgIcon("approval.png").getSrc());
			break;
		default:
			break;
		}
	}

	/**
	 * 1. Track 변경 2. 현재 Menu Type 변경 3. Toolbar 변경 4. Folder Filter 변경
	 * */
	public void setDocumentActionType(int type) {
		Log.debug("[setDocumentActionType] " + type);
		if (type < 0)
			return;
		// 2013-11-26 나용준
		// setDocumentActionType 변경 시에 필터 초기화 시켜줘야 함.
		searchItemsDefault.resetItems(true);
		searchItemsExpire.resetItems(true);

		setTrackPanel(type);
		DocumentActionUtil.get().setActivatedMenuType(type);
		
		// 20140317, junsoo, menu type 저장
		if (listingPanel instanceof DocumentsListPanel)
			((DocumentsListPanel) listingPanel).getGrid().setCurrentMenuType(type);
		
		changeToolbar(type);
		showSearchForm(type);
	}

	public int getActionType(int folderType) {
		switch (folderType) {
		case 0:
			return DocumentActionUtil.TYPE_SHARED;
		case 1:
			return DocumentActionUtil.TYPE_MYDOC;
		case 2:
			return DocumentActionUtil.TYPE_XVARM;

		default:
			return DocumentActionUtil.TYPE_SHARED;
		}
	}

	/**
	 * 폴더검색 조건을 가지고 있는지 검사
	 * 
	 * @return boolean
	 */
	// public boolean haveFilter(){
	// if(Session.get().getCurrentFolder()!=null)
	// return
	// folderFilter.containsKey(Session.get().getCurrentFolder().getId());
	// else
	// return false;
	// }

	public NormalSearchItems getActivatedSearchItems() {
		if (searchTrackFormExpire.isVisible())
			return searchItemsExpire;

		return searchItemsDefault;
	}

	public NormalSearchItems getSearchItemsDefault() {
		return searchItemsDefault;
	}

	public NormalSearchItems getSearchItemsExpire() {
		return searchItemsExpire;
	}

	// 20130727, junsoo, documentsMenu 획득
	public DocumentsMenu getMenu() {
		return documentsMenu;
	}

	// public Map<String, SSearchOptions> getFolderFilter() {
	// return folderFilter;
	// }

	public SFolder getFolder() {
		return folder;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public SDocument getDocument() {
		return document;
	}

	public void setDocument(SDocument document) {
		this.document = document;
	}

	/**
	 * 필터 검색시 true, 폴더 선택시 false
	 * */
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	public DocumentsListPanel getListingPanel() {
		return (DocumentsListPanel) listingPanel;
	}

	public DocumentsMenu getDocumentsMenu() {
		return documentsMenu;
	}

	public String getDraftRights() {
		return draftRights;
	}

	public void setDraftRights(String draftRights) {
		this.draftRights = draftRights;
	}

}