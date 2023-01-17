package com.speno.xedm.gui.frontend.client.document.prop;

import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.Constants;

/**
 * 
 * @author deluxjun
 *
 */
public abstract class DocumentDetailTab extends HLayout {
	protected SDocument document;

	protected ChangedHandler changedHandler;

	protected boolean update = false;		// 문서 속성 업데이트 권한
	protected boolean rename = false;		// 문서명 변경 권한
	protected boolean check = false;		// 20130820, junsoo, 문서 체크인/아웃 권한. (버전 복구에 사용됨)
	protected boolean extend = false;		// 보존년한 연장 권한
	protected boolean control = false;		// 설정 권한 (문서보안 설정, doctype, template 변경)
	protected boolean download = false;		// 다운로드 권한
	protected boolean view = false;			// 문서 보기 권한
	
	/**
	 * 
	 * @param document The document this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        document
	 */
	public DocumentDetailTab(SDocument document, ChangedHandler changedHandler) {
		super();
		this.changedHandler = changedHandler;

		this.document = document;

		updatePermission();
	}

	public SDocument getDocument() {
		return document;
	}

	/**
	 * set document
	 * @param document		document to set
	 * @param reset			초기화 할지 여부
	 */
	public void setDocument(SDocument document, boolean reset) {
		
		if (document == null) {
			document = new SDocument();
			document.setId(-1L);
		}

		// 20130814, junsoo, 기존 document 체크 없이 무조건 세팅한 후 refresh 한다. 매번 new로 생성되므로.. 
//		if (reset || (this.document.getId() != document.getId())) {
			this.document = document;

			updatePermission();

			refresh();
//		}
	}
	
	/**
	 * reset 하지 않고 refresh. 즉, 같은 document이면 아무것도 하지 않음.
	 * @param document
	 */
	public void setDocument(SDocument document) {
		setDocument(document, false);
	}
	
	/**
	 * 신규 문서인지 여부.
	 * @return
	 */
	public boolean isNew(){
		return (document == null || document.getId() == 0L);
	}
	

	protected void updatePermission(){
		update = false;
		rename = false;
		check = false;
		extend = false;
		control = false;
		download = false;
		view = false;

		// admin 인 경우에도 서버로 부터 리턴되는 권한 정책을 따른다. document lock 인데도 업데이트 가능하게 되면 안되므로..
//		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
//			update = true;
//			rename = true;
//			extend = true;
//			control = true;
//			download = true;
//			view = true;
//			return;
//		}
		
		// 속성 갱신은 unlock 상태일때만 가능.
		if (document != null && document.getFolder() != null) {
//			if (document.getStatus() == Constants.DOC_UNLOCKED) {
//				update	= document.getFolder().isWrite();
//				rename	= document.getFolder().isRename();
//				check	= document.getFolder().hasPermission(Constants.PERMISSION_CHECK);
//				extend	= document.getFolder().isExtend();
//				control	= document.getFolder().isControl();
//			}
//			download	= document.getFolder().isDownload();
			
			if(document.getSecurityType() == null){
				if (document.getStatus() == Constants.DOC_UNLOCKED) {
					update	= document.getFolder().isWrite();
					rename	= document.getFolder().isRename();
					check	= document.getFolder().hasPermission(Constants.PERMISSION_CHECK);
					extend	= document.getFolder().isExtend();
					control	= document.getFolder().isControl();
				}
				download	= document.getFolder().isDownload();
			}
			else  {
				if (document.getStatus() == Constants.DOC_UNLOCKED) {
					update	= document.hasPermission(Constants.PERMISSION_WRITE);
					rename	= document.hasPermission(Constants.PERMISSION_RENAME);
					check	= document.hasPermission(Constants.PERMISSION_CHECK);
					extend	= document.hasPermission(Constants.PERMISSION_EXTEND);
					control	= document.hasPermission(Constants.PERMISSION_CONTROL);
				}
				download	= document.hasPermission(Constants.PERMISSION_DOWNLOAD);
			}
			view		= document.getFolder().isView();
		}
	}

	public ChangedHandler getChangedHandler() {
		return changedHandler;
	}

	public boolean isUpdate() {
		return update;
	}

	/**
	 * refresh
	 */
	public void refresh() {
		
	}
	
	/**
	 * Place here special logic that will be invoked when the user opens the tab
	 */
	protected void onTabSelected(){
	}
}
