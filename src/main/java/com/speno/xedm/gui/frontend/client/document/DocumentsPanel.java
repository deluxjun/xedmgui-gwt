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
 * Modified: 20130730 : �Ӽ�â�� ���� window�� ����, ������ ���� �ܼ�ȭ, �޴�/��ư��
 * DocumentActionUtil�� ����
 * 
 * @author deluxjun
 * 
 */
public class DocumentsPanel extends HLayout implements FolderObserver,
		DocumentObserver {
	protected static DocumentsPanel instance;

	// ���� ����Ʈ ���̾ƿ�
	protected Layout listing = new VLayout();
	// ���� �Ӽ� ���̾ƿ�
	// protected Layout details = new VLayout();
	// Ʈ�� ���̾ƿ�
	protected VLayout left = new VLayout();
	// ����Ʈ�� �Ӽ� ���̾ƿ�
	protected VLayout right = new VLayout();

	// ��� ��ư ���̾ƿ�
	protected HLayout middle = new HLayout();

	// �˻� ���̾ƿ�
	private VLayout searchLayout = new VLayout();
	// ����Ʈ canvas
	protected Canvas listingPanel;
	// ����
	protected SDocument document;
	// ����
	protected SFolder folder;

	// Ʈ���� �޴� ��ư�� ����
	// 20130727, junsoo, public -> private���� ����
	private DocumentsMenu documentsMenu;

	// protected Button btnCheckOutList;
	// protected Button favoriteButton;
	// protected Button trashButton;

	// ���ǥ��
	protected TrackPanel trackPanel;

	// ���ã�⳪ �뽬���忡�� ������ ���� ���̵�
	public long expandDocid = 0;

	// �˻�����
	public boolean bSearch = false;
	// ������ȸ����
	public boolean bCheckSearch = false;

	// ���������� �Ӽ��� ��ȸ�� docid ����ؼ� �Ӽ��� ��ȸ�ϴ°��� ����.
	// private long strLastDocId = 0;

	// Search Item ���� Form
	private DynamicForm searchTrackFormDefault;
	private DynamicForm searchTrackFormExpire;
	private NormalSearchItems searchItemsDefault;
	private NormalSearchItems searchItemsExpire;

	// Search Item ���� ����
	HashMap<String, FormItem> itemMapDefault;
	HashMap<String, FormItem> itemMapExpire;

	// ������ �˻��� true
	private boolean isSearch = false;

	private String draftRights;
	

	

	// Folder�� ���� �˻� �ɼ� ����� Map ����
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

		// �������г� ����
		listingPanel = new Label("");
		listing.setWidth100();
		listing.setHeight100();
		listing.setShowResizeBar(false);
		listing.addMember(listingPanel);

		// ���� Ʈ�� �޴� ����
		prepareMenu();
		// ���� ����
		prepareLabel();

		// actions �ʱ�ȭ
		initActions();
		initSearch();
		right.setWidth100();
		// ���� ����

		middle.setHeight(28);
		middle.setWidth100();

		// toolbar �߰�
		setDocumentActionType(-1);

		// initDefaultLayout();
		right.addMember(trackPanel);
		right.addMember(middle);
		right.addMember(searchLayout);
		right.addMember(listing);
		// right.hide();
		// right.addMember(searchPanel);
		// �Ӽ� ����
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
		// ���������� ǥ���ϰ� ��Ŀ���� �ֱ����� �г��� �׸���.
		// ���� �׸��� �г��� ���� �׷����� ���� ���¿�����
		// ����Ʈ�� ������ Ȯ�� ��ɶ��� ���� �����Ƿ� �̷��� ��ɵ鿡��
		// �׻� �ش� ���ڵ� ������ ������ �׸��峪 ������ ���� �׷����� �Ѵ�.
		draw();
		// Search Form �ʱ�ȭ
		// initSearch();
		// // ���� ����
		// try{
		// documentsMenu.refresh();
		// // trackPanel.setIcon(ItemFactory.newImgIcon("mydoc.png").getSrc());
		// }catch(Exception ex){
		// SC.warn(ex.getMessage());
		// }
	}

	/**
	 * search Bar �ʱ�ȭ
	 * */
	private void initSearch() {
		searchItemsDefault = new NormalSearchItems(NormalSearchItems.documentt);
		searchItemsExpire = new NormalSearchItems(NormalSearchItems.documentt);
		itemMapDefault = searchItemsDefault.getSearchItemsMap();
		itemMapExpire = searchItemsExpire.getSearchItemsMap();
		initSearchForm();
	}

	/**
	 * Search Form�� Item�� �ʱ�ȭ�Ѵ�.
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
		
		
		//20140425�����
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
						// xvarm delete ������
						// else if
						// (isCurrentFolderType(Constants.FOLDER_TYPE_XVARM))
						// SC.warn(I18N
						// .message("Sorry not ready to delete Xvarm data!"));
						else
							// �ϸ�ũ ����
							onBtnDelBookmarkClick();
					}
				}, DocumentActionUtil.TYPE_MYDOC,
				DocumentActionUtil.TYPE_SHARED,
				DocumentActionUtil.TYPE_FOLDER_SHARED);

		// ����� ���� ���������� ��밡���ϰ� ��.(�� ������ ��� ��� ������ �����Ƿ�)
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
						// ���� �ٿ�ε�
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

		// ���� ����
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

		// 20130820, junsoo, template ���� ��� ����. (�� �� ���谡 �ʿ���)
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

		// ������ ���� �׼�
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

		// 20140207, junsoo, �˻����� �����Ͽ� ��� ������
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

		// ���ã��
		DocumentActionUtil.get().createAction("goto", "client.goto",
				"actions_goto", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnGotoClick();
					}
				}, DocumentActionUtil.TYPE_FAVOR);
		//
		// // ��⹮���� ���� �޴�
		// ��ġ�� ����Ű�Ƿ� ��� ������.
		// DocumentActionUtil.get().createAction("expire_expire", "delete",
		// "actions_delete", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// onBtnExpireClick();
		// }
		// }, DocumentActionUtil.TYPE_EXPIRED);

		// ��⹮���� restore
		DocumentActionUtil.get().createAction("expire_restore", "restore",
				"data_into", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onBtnExpireRestoreClick();
					}
				}, DocumentActionUtil.TYPE_EXPIRED);

		// 20130829, junsoo, �̸��� ����
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

		// ���� �˻�
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

		// TODO: XVARM ���� �׼�
		// DocumentActionUtil.get().createAction("download_xvarm", "download",
		// "download", new DocumentAction() {
		// @Override
		// protected void doAction(Object[] params) {
		// // ���õ� ����Ʈ ���
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

		// ��ť��Ʈ ���ϼ��� ����
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

		// 20130816, junsoo, ����ɼ� ����.
		// 20130906, junsoo, �ʹ� ������ ��� ������.
		// Ȯ��
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
		// //TODO: ���Ȱ���
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

		// 20140106, junsoo, drag & drop area �߰�
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
	 * �޽��� ����
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
	 * ��� ���� �� ��� â�� ����
	 * */
	private ApproveRequestDialog approvePopup;

	public ApproveRequestDialog getApprovePopup() {
		return approvePopup;
	}

	private void showApproveRequestPopup() {
		approvePopup = new ApproveRequestDialog();

		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// ���õ� Record�� ���� ���
		if (selectedGrid.length > 0) {
			SDocument doc = (SDocument) selectedGrid[0]
					.getAttributeAsObject("document");

			approvePopup.show(doc, draftRights);
		}
		// ���õ� Record�� ���� ���(���)
		else {
			approvePopup.show(null, draftRights);
		}
	}

	// 20130829, junsoo, email ����.
	private void onBtnEmailClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// ���õ� ������ ��� ���� ���� ������.(÷������ ���� ����)
		// if (selectedGrid == null || selectedGrid.length < 1) {
		// SC.say(I18N.message("nonecheckeddoc"));
		// return;
		// }

		List<SDocument> list = new ArrayList<SDocument>();
		// ÷������ ���� ����
		if (selectedGrid.length > 0) {
			for (int i = 0; i < selectedGrid.length; i++) {
				list.add((SDocument) selectedGrid[i]
						.getAttributeAsObject("document"));
			}
			EmailDialog window = new EmailDialog(list,
					selectedGrid[0].getAttribute("titlenm"));
			window.show();
		}
		// ÷������ ���� ����
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
	 * ���õ� ������ �ش��ϴ� Search Bar�� �����ش�.
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
			searchLayout.show(); // ������������ �˻��� ���ؼ��� �����ϹǷ� �˻��� ǥ��
			break;
		case DocumentActionUtil.TYPE_TRASH:
			searchTrackFormDefault.hide();
			searchTrackFormExpire.show();
			searchLayout.hide();
			break;
		}
	}

	// 20130727, junsoo, goto �ش� ��ġ��.
	private void onBtnGotoClick() {
		// 20130821 taesu, ���õ� ������ ������� ���۵��� �ʰ���.
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
	// * �ٿ�ε� �������� ȣ���Ѵ�.
	// */
	// private void onFileDownload(Long docId, String elementId) {
	// // GWT.log("baseURL : " + GWT.getHostPageBaseURL());
	// Util.download(docId, elementId);
	// // if (elementId == null) {
	// // // ������ �ٿ�ε�
	// // WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" +
	// Session.get().getSid() + "&docId=" + docId);
	// // } else {
	// // // �ѰǴٿ�ε�
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

	// template ����
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

	// ��ư ����
	protected void setButton(Button btn, int width, int height) {
		if (width == 0) {
			btn.setWidth(50);
		} else
			btn.setWidth(width);
		btn.setShowRollOver(true);
		btn.setShowDisabled(true);
		btn.setShowDown(true);
	}

	// �޴� �г� ����
	protected void prepareMenu() {
		documentsMenu = new DocumentsMenu();		
	}

	// ���� ����
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

	// approval list �г� ���� - goodbong
	private void showApprovalPanel(String threadType) {
		if (!(listingPanel instanceof ApprovalPanel)) {
			listing.removeMember(listingPanel);
			listingPanel = ApprovalPanel.get();
			listing.addMember(listingPanel);
		}
		((ApprovalPanel) listingPanel).refresh(threadType);
		listing.redraw();
	}

	// ���ã��
	public void onFavoriteBtnClick() {
		// toolbar ����
		setDocumentActionType(DocumentActionUtil.TYPE_FAVOR);
		// DocumentsGrid grid = ((DocumentsListPanel) listingPanel).getGrid();
		// grid.fields.removeAll(grid.attrFields);
		// grid.setFields(grid.fields.toArray(new ListGridField[0]));
		
		showListPanel();
	}

	// ������
	public void onTrashBtnClick() {
		// toolbar ���߰�
		setDocumentActionType(DocumentActionUtil.TYPE_TRASH);

		showListPanel();
	}

	// ���� ������
	public void onSharedTrashBtnClick() {
		setDocumentActionType(DocumentActionUtil.TYPE_SHARED_TRASH);

		showListPanel();
	}

	// // �޴���ư ����
	// private void prepareMenuButton(){
	// // ���ã��
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
	// // üũ�ƿ�
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
	// // ������
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

	// ������� �г� ���� ����
	private void onBtnAddClick() {
		// final DocumentsUploader uploader = new
		// DocumentsUploader(DocumentsPanel.this);
		// uploader.show();

		final DocumentUploadDialog uploader = new DocumentUploadDialog(this);
		uploader.show();

	}

	// ���ã�� ����
	private void onBtnDelBookmarkClick() {
		// ���� ����Ʈ ��ȸ
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
						// ���� ���̵� ����
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}

						// ���ã�� ����
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

	// ���� �ٿ�ε�
	private void onBtndownClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();

		// �ΰ��̻� ó���Ұ�
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		} else if (selectedGrid.length > 1) {
			SC.say(I18N.message("just1document"));
			return;
		}

		// �ѰǸ� ó��
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

	// ���ã��
	private void onBtnFavoriteClick() {
		// ���õ� ����Ʈ ���
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}

		// ���� ���̵� ���
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
		// 20140306, junsoo, ���ã��, üũ�ƿ�����Ʈ�� refresh�� �ʿ���
		int menuType = DocumentActionUtil.get().getActivatedMenuType();
		switch (menuType) {
		case DocumentActionUtil.TYPE_FAVOR:
					
		case DocumentActionUtil.TYPE_CHECKED:
			((DocumentsListPanel) listingPanel).refresh();
			break;

		default:
			break;
		}
		// yuk yong soo 20140317 ������ �������� �Ҷ� ����¡ ������ ����
		
		
		
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

	// �ٿ��ֱ�
	// Ŭ�����忡 ����� �������̵� ����ؼ�
	// ���񽺿� �Ѱ� �ش� ������ ���� �Ѵ�.
	private void onBtnPasteClick() {
		// ���õ� ���� ��������
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
						// 20130726, junsoo, ���¹� �޽����� ����.
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

	// �ٿ��ֱ�
	// Ư�� ������ �����ֱ�.
	private void onBtnPasteToClick() {
		// ���õ� ���� ��������
		if (Clipboard.getInstance().isEmpty()) {
			SC.warn(I18N.message("noiteminclipboard"));
			return;
		}

		final long[] docid = new long[Clipboard.getInstance().size()];
		int i = 0;
		for (SDocument doc : Clipboard.getInstance()) {
			docid[i++] = doc.getId();
		}

		// 20130816, junsoo, ���� ���� â ����.
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

	// �̵�
	private void onBtnMoveClick() {
		// ���õ� ����Ʈ ���
		Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}

		// ���� ���̵� ���
		final long[] docids = new long[selectedGrid.length];
		for (int i = 0; i < docids.length; i++) {
			docids[i] = Long.parseLong(selectedGrid[i].getAttribute("id"));
		}

		// ���� �̵� �г� ����
		// final DocumentsMove dmove = new
		// DocumentsMove(Session.get().getSid(),docid, DocumentsPanel.this);
		// dmove.show();

		// 20130816, junsoo, ���� ���� â ����.
		ReturnHandler<SFolder> returnHandler = new ReturnHandler<SFolder>() {
			@Override
			public void onReturn(SFolder folder) {
				// �̵�
				ServiceUtil.folder().paste(Session.get().getSid(), docids,
						folder.getId(), Clipboard.CUT,
						new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								// ����ǥ��
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

	// ���� ����
	private void onBtnRestoreClick() {
		// ���� ����Ʈ
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// ���� ���̵� ���� & ���� ���̵�
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
		// ���õ� ����Ʈ ���
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

		// ��������
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in �г� ����
						final DocumentCheckinDialog checkinDialog = new DocumentCheckinDialog(
								result);
						checkinDialog.show();

						// // ����Ÿ���� ��ȸ�Ѵ�.
						// // check in �г� Ȯ���ʵ带 �����ϱ� ���ؼ���.
						// // System.out.println("==== doctype : " +
						// // String.valueOf(result.getDocType() ));
						// documentCodeService.listXvarmIndexFieldsByDocTypeId(Session.get().getSid(),
						// result.getDocType(),
						// new AsyncCallback<List<String>>() {
						// @Override
						// public void onSuccess(
						// List<String> resultArray) {
						// // Ȯ���ʵ�� �迭
						// String[] strArray = new String[resultArray.size()];
						// for (int i = 0; i < strArray.length; i++) {
						// // Ȯ�� �ʵ�� ����
						// strArray[i] = resultArray.get(i).toString();
						// }
						//
						// // check in �г� ����
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

		// ��������
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in �г� ����
						final DocumentCheckoutDialog dialog = new DocumentCheckoutDialog(
								result);
						dialog.show();
					}
				});
	}

	// ���� Lock
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

		// ��������
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in �г� ����
						final DocumentLockDialog dialog = new DocumentLockDialog(
								result);
						dialog.show();
					}
				});

	}

	// ���� �Ӽ�����
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

			// ��������
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
							// 20130820, junsoo, ������/���ã��/������� �Ӽ����� �б⸸ ����!!
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

	// ���� unlock
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

		// ��������
		ServiceUtil.document().getById(Session.get().getSid(), doc.getId(),
				new AsyncCallback<SDocument>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(final SDocument result) {
						// check in �г� ����
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

	// ���� ����
	public void onBtnCopyClick() {
		// ���� ����
		final Record[] selectedGrid;

		// ���õ� ���� ��������
		selectedGrid = ((DocumentsListPanel) listingPanel).getCheckedRecord();
		if (selectedGrid == null)
			return;

		// ��乮�� ó���Ұ�
		// 20140106na ��ó�� �� ���� ���� �Ұ�
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

		// Ŭ������ Ŭ����
		Clipboard.getInstance().clear();
		for (int i = 0; i < selectedGrid.length; i++) {
			String id = selectedGrid[i].getAttribute("id");
			SDocument document = new SDocument();
			document.setId(Long.parseLong(id));
			document.setTitle(selectedGrid[i].getAttribute("titlenm"));

			// Ŭ�����忡 ���̵� ����
			Clipboard.getInstance().add(document);
			Clipboard.getInstance().setLastAction(Clipboard.COPY);
		}

		// 20130822, junsoo, grid ����
		((DocumentsListPanel) listingPanel).refreshSelectedRows();

		// 20130726, junsoo, Ȯ�� â ���ְ� ���¹ٿ� ��� �����
		// SC.say(I18N.message("copycomplite"));
		Log.info(I18N.message("second.client.actionCopy"),
				I18N.message("second.client.copyCompleted"));
	}

	// ���� ����
	public void onBtnDelClick() {
		// ������ ������ �����ϴ��� ����
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)	.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// ��乮��
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
						// doc���̵� ����
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}

						// ���� ���� ����
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
										// ���� ����ȸ
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

	// ���� ���
	private void onBtnExpireClick() {
		final Record[] selectedGrid = ((DocumentsListPanel) listingPanel)
				.getCheckedRecord();
		if (selectedGrid == null) {
			SC.say(I18N.message("nonecheckeddoc"));
			return;
		}

		// 20130822, junsoo, ���޽��� ��ȭ
		String message = I18N.message("doexpire");
		
		if (DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED_TRASH)
		message = I18N.message("doexpireWithWarning");

		SC.confirm(message, new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value != null && value) {
					try {
						// doc���̵� ����
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
	 * �˻� ��ư ���ý� �˻� �޴� Ȱ��ȭ ���� ����
	 * */
	private void onBtnFilterClick() {
		if (searchLayout.isVisible()) {
			searchLayout.hide();

			// 2013-11-26 ������
			// ���͸� ���� �� ���Ϳ� �ԷµǾ��� �� ����
			searchItemsDefault.resetItems(true);
		} else
			searchLayout.show();
	}

	// ��� ������ ���� restore
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
						// doc���̵� ����
						long[] docid = new long[selectedGrid.length];
						for (int i = 0; i < docid.length; i++) {
							docid[i] = Long.parseLong(selectedGrid[i]
									.getAttribute("id"));
						}
						// folderId ����
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

	// ������ ����
	// private void onBtnCheckoutClick(){
	// SC.warn("not ready yet!");
	// }
	// 20131210 na ������ ���ΰ�ħ�� �������� ������� �� ���������� �����Ͽ� ���ΰ�ħ �߰�
	// ����ȸ
	private void onBtnReloadTrashClick() {
		int menuType = DocumentActionUtil.get().getActivatedMenuType();

		if (menuType == DocumentActionUtil.TYPE_TRASH)
			onTrashBtnClick();
		else if (menuType == DocumentActionUtil.TYPE_SHARED_TRASH)
			onSharedTrashBtnClick();
	}

	// ������ �����ؼ� ���������� ��ȸ
	@Override
	public void onFolderSelected(SFolder folder) {
		Log.debug("[onFolderSelected] " + folder.getId());
		this.folder = folder;
		// ���� �̵��� Thumbnail ���û��� �⺻���� ����.
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
		// ���� �̵��� Thumbnail ���û��� �⺻���� ����.
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

	// ������ ���� ����Ʈ�� ������ �Ӽ��� ǥ��
	public void refresh() {
		// ����Ʈ ��ȸ
		updateListingPanel(folder);

		// �Ӽ�â�� ���������� ���� �Ӽ� ����
		FolderPropertiesWindow.get().refresh();
	}

	// 20130802, junsoo, ���õ� ���ڵ常 refresh
	public void refreshSelectedRecords() {
		((DocumentsListPanel) listingPanel).updateSelectedRecords();
	}

	// ����Ʈ ��ȸ
	protected void updateListingPanel(SFolder folder) {
		// toolbar ���߰�
		if (DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_FOLDER_SHARED)
			setDocumentActionType(getActionType(folder.getType()));
		else
			setDocumentActionType(DocumentActionUtil.TYPE_FOLDER_SHARED);

		// 20140213, junsoo, �˻��� �ƴ��� ����
		setSearch(false);
		showListPanel();

	}

	// top�гο����� ��ȸ
	public void showSearchResult(SSearchOptions searchOptions) {
		bSearch = true;

		// toolbar ���߰�
		// setDocumentActionType(DocumentActionUtil.TYPE_SEARCH);

		((DocumentsListPanel) listingPanel).setSearchOptions(searchOptions);
		showListPanel();

	}

	// // TODO: right pannel member control
	// /**
	// * Right Pannel�� member show/hide ó��
	// *
	// * */
	// private void controlRight(boolean showMiddle, boolean showExpireTop) {
	// // ��� �� ��Ʈ��
	// if (showMiddle) middle.show();
	// else middle.hide();
	//
	// if (showExpireTop) middle.show();
	// else middle.hide();
	// }

	// Expire ������ ��ȸ
	public void onExpire() {
		setDocumentActionType(DocumentActionUtil.TYPE_EXPIRED);

		showListPanel();
	}

	// ���� ����� ��ȸ
	public void onApproveStandBy() {
		showApprovalPanel("ToApproval");

		// 20130827, junsoo, approvePanel���� ���� �ʱ�ȭ �ϹǷ�, ����
		// ����(setDocumentActionType ȣ��)�� ���߿� �ؾ���.
		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_STANDBY);
	}

	// ���� ��û�� ��ȸ
	public void onApproveRequest() {
		showApprovalPanel("Request");

		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_REQUEST);
	}

	// ���� �Ϸ��� ��ȸ
	public void onApproveComplete() {
		showApprovalPanel("Completed");

		setDocumentActionType(DocumentActionUtil.TYPE_APPROVE_COMPLETE);
	}

	// LockUserIdAndStatus ��ȸ
	public void showCheckedSearch() {
		bCheckSearch = true;
		// toolbar ���߰�
		setDocumentActionType(DocumentActionUtil.TYPE_CHECKED);

		showListPanel();
	}

	// SheardList ��ȸ
	public void showSharedList() {
		// TODO
		setDocumentActionType(DocumentActionUtil.TYPE_FOLDER_SHARED);

		showListPanel();
	}

	// ���� ���� �޾ƿ��� xvarm
	// public void getByIdXvarm(long docId, String strId){
	// ecmService.getById(Session.get().getSid(), strId,
	// Session.get().getCurrentFolder().getName(), new
	// AsyncCallback<SDocument>() {
	//
	// @Override
	// public void onSuccess(final SDocument result) {
	// document = result;
	// // Ȯ���ʵ�� �迭
	// SContent[] scontent = result.getContents();
	// String[] strArray = scontent[0].getFieldNames();
	//
	// // �Ӽ�ǥ��
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

	// 20130730, session observer�κ��� onDocumentSelected�� ȣ��ǹǷ� ������.
	// public void onSelectedDocument(long docId, String strId) {
	// if(isCurrentFolderType(Constants.FOLDER_TYPE_XVARM) && bSearch == false
	// && bCheckSearch == false){
	// // getByIdXvarm(docId, strId);
	// }else{
	// // 20130729, junsoo, ���������� ó�� ��ȸ�� �� ���� ���������� ��.
	// // if(bSearch || bCheckSearch) {
	// // // ž�гο��� ��ȸ�ÿ��� �������� Ŭ���Ҷ����� ������
	// // // ������ �����ͼ� ��ư�� �����Ѵ�.
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

	// �������� ���� ������ ����Ǹ� ȣ��Ǵ� �Լ�
	// �ѰǸ� ����
	@Override
	public void onDocumentSaved(SDocument document) {
		// 20130802, junsoo, ������ �Ӽ�â�� ���� ����Ǹ� ȣ��Ǿ� �ش� ���ڵ常 refresh �ǵ��� ��.
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

	// ������ ���õǸ� ȣ��Ǵ� �Լ�
	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		Log.debug("onDocumentSelected");

		// 20130820, junsoo, DocumentPropertiesWindow ���̻� ������� ����. modal �� �����Ͽ�
		// DocumentPropertiesDialog �����.
		// if (items != null && items.length > 0 && items[0].getType() ==
		// SRecordItem.TYPE_DOCUMENT) {
		// DocumentPropertiesWindow.get().refresh(items[0].getDocument());
		// } else {
		// DocumentPropertiesWindow.get().empty();
		// }
	}

	// 20130809, junsoo, ���� ���� �ִ� ������ ���� ����.
	// ����Ʈ ��ü ����.
	@Override
	public void onReloadRequest(SFolder folder) {
		Log.debug("DocumentsPanel : onReloadRequest");
		// ���� ���� �ִ� ������ ���� ����.
		if (folder.getId() == Session.get().getCurrentFolder().getId())
			((DocumentsListPanel) listingPanel).refresh();
	}

	// 20130725, junsoo, ���� ����
	private ToolStrip currentToolbar;

	public ToolStrip getCurrentToolbar() {
		return currentToolbar;
	}

	/**
	 * ���� ���õǾ��ִ� ������ ��ġ�� ���� Toolbar�� �籸���Ѵ�.
	 * 
	 * @param Type
	 *            : DocumentActionUtil.*
	 * */
	public void changeToolbar(int type) {
		ToolStrip toolbar = DocumentActionUtil.get().getToolbar(type);
		// ������ ���� �����̸� ����.
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
	 * Track Panel�� Icon�� Label ���� �����Ѵ�.
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

		// �׺���̼� ����
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
	 * 1. Track ���� 2. ���� Menu Type ���� 3. Toolbar ���� 4. Folder Filter ����
	 * */
	public void setDocumentActionType(int type) {
		Log.debug("[setDocumentActionType] " + type);
		if (type < 0)
			return;
		// 2013-11-26 ������
		// setDocumentActionType ���� �ÿ� ���� �ʱ�ȭ ������� ��.
		searchItemsDefault.resetItems(true);
		searchItemsExpire.resetItems(true);

		setTrackPanel(type);
		DocumentActionUtil.get().setActivatedMenuType(type);
		
		// 20140317, junsoo, menu type ����
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
	 * �����˻� ������ ������ �ִ��� �˻�
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

	// 20130727, junsoo, documentsMenu ȹ��
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
	 * ���� �˻��� true, ���� ���ý� false
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