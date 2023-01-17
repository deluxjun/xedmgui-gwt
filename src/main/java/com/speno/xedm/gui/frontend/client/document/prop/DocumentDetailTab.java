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

	protected boolean update = false;		// ���� �Ӽ� ������Ʈ ����
	protected boolean rename = false;		// ������ ���� ����
	protected boolean check = false;		// 20130820, junsoo, ���� üũ��/�ƿ� ����. (���� ������ ����)
	protected boolean extend = false;		// �������� ���� ����
	protected boolean control = false;		// ���� ���� (�������� ����, doctype, template ����)
	protected boolean download = false;		// �ٿ�ε� ����
	protected boolean view = false;			// ���� ���� ����
	
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
	 * @param reset			�ʱ�ȭ ���� ����
	 */
	public void setDocument(SDocument document, boolean reset) {
		
		if (document == null) {
			document = new SDocument();
			document.setId(-1L);
		}

		// 20130814, junsoo, ���� document üũ ���� ������ ������ �� refresh �Ѵ�. �Ź� new�� �����ǹǷ�.. 
//		if (reset || (this.document.getId() != document.getId())) {
			this.document = document;

			updatePermission();

			refresh();
//		}
	}
	
	/**
	 * reset ���� �ʰ� refresh. ��, ���� document�̸� �ƹ��͵� ���� ����.
	 * @param document
	 */
	public void setDocument(SDocument document) {
		setDocument(document, false);
	}
	
	/**
	 * �ű� �������� ����.
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

		// admin �� ��쿡�� ������ ���� ���ϵǴ� ���� ��å�� ������. document lock �ε��� ������Ʈ �����ϰ� �Ǹ� �ȵǹǷ�..
//		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
//			update = true;
//			rename = true;
//			extend = true;
//			control = true;
//			download = true;
//			view = true;
//			return;
//		}
		
		// �Ӽ� ������ unlock �����϶��� ����.
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
