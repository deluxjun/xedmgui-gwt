package com.speno.xedm.gui.frontend.client.admin.documentbox;


import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.frontend.client.admin.AdminPanel;

/**
 * Shared Folder Menu
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class DocumentBoxMenu extends AdminSubMenu {
	
	private Button documentBox;
	
//	@Override
//	public void buildMenu(final long parentMenuId) {
//		
//		blnFirstBtn = true;
//		AdminMenuUtil.get().hasPriv(parentMenuId, "documentbox", new AsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long id) {
//				if (id != null) {
//					documentBox.show();
//					if (blnFirstBtn) {
//						documentBox.select();
//						blnFirstBtn = false;
//					}
//				}
//				
//				setContentBySelectedBtn();
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
//	}
//	
//	
//	private void checkPrivAndSet(long parentMenuId, String menuName, final Button btn){
//		AdminMenuUtil.get().hasPriv(parentMenuId, menuName, new AsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long id) {
//				if (id != null) {
//
//				btn.show();
//				if (blnFirstBtn) {
//					btn.select();
//					blnFirstBtn = false;
//				}
//				}
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
//	}
	
	/**
	 *  Shared Folder Menu ����
	 */
	public DocumentBoxMenu() {
		setMargin(10);
		setMembersMargin(5);

		documentBox = new Button(I18N.message("sharedfolder"));
		documentBox.setWidth100();
		documentBox.setHeight(25);
		documentBox.hide();
//		addMember(documentBox);

		initMenus(new Object[]{"documentbox", documentBox});

//		documentBox.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				onDocumentBoxContent();
//			}
//		});


	}
//	
//	/**
//	 * Admin Panel�� Content ����
//	 */
//	protected void onDocumentBoxContent() {
//		AdminPanel.get().setContent(FolderPanel.get());
//	}
//	
//	/**
//	 * ���õ� ��ư�� �ش��ϴ� �г��� �ε���.
//	 */
//	public void setContentBySelectedBtn() {		
//		if(documentBox.isSelected()) {
//			AdminPanel.get().setContent(FolderPanel.get());
//		}
//	}


	@Override
	public String getMenuRef() {
		return "admin;documentbox";
	}
	
	@Override
	public void setContent(String title) {
		VLayout content = null;
		if ("documentbox".equals(title)) {
			content = FolderPanel.get();
		}
			
		if (content != null)
			AdminPanel.get().setContent(content);
	}


}