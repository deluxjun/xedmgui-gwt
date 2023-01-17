package com.speno.xedm.gui.frontend.client.panels;

import com.google.gwt.core.shared.GWT;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.UserObserver;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.frontend.client.clipboard.Clipboard;
import com.speno.xedm.gui.frontend.client.clipboard.ClipboardObserver;
import com.speno.xedm.gui.frontend.client.clipboard.ClipboardWindow;
import com.speno.xedm.gui.frontend.client.document.DashboardPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.folder.EtcMenus;

/**
 * 
 * @author deluxjun
 *
 */
public class StatusBarIcons extends HLayout implements ClipboardObserver, UserObserver {
	private static StatusBarIcons instance;
	
	private Img checkoutImage;
	private Img messageImage;

	private HTMLFlow clipboardSize = new HTMLFlow("0");

	private HTMLFlow lockedCount = new HTMLFlow("0");

	private HTMLFlow checkoutCount = new HTMLFlow("0");

	private HTMLFlow messagesCount = new HTMLFlow("0");


	private StatusBarIcons() {
		Img clipboardImage = ItemFactory.newImgIcon("page_white_paste.png");
		clipboardImage.setHeight("16px");
		clipboardImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Clipboard.getInstance().isEmpty())
					ClipboardWindow.getInstance().show();
			}
		});
		clipboardImage.setCursor(Cursor.HAND);
		clipboardImage.setTooltip(I18N.message("clipboard"));

		// TODO: lock 구현은 추후에..
		Img lockedImage = ItemFactory.newImgIcon("page_white_lock.png");
		if (!GWT.isScript()) {
			lockedImage.setHeight("16px");
			lockedImage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					DashboardPanel.get().selectMenu(Constants.DASHBOARD_HOME, Constants.DASHBOARD_MESSAGE_NOTICE, false);
				}
			});
			lockedImage.setCursor(Cursor.HAND);
			lockedImage.setTooltip(I18N.message("event.lockeddocs"));
		}

		checkoutImage = ItemFactory.newImgIcon("page_edit.png");
		checkoutImage.setHeight("16px");
		checkoutImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO: show locked docs
				onCheckedSearch();
			}
		});
		checkoutImage.setCursor(Cursor.HAND);
		checkoutImage.setTooltip(I18N.message("checkout"));

		messageImage = ItemFactory.newImgIcon("mail.png");
		messageImage.setHeight("16px");
		messageImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DashboardPanel.get().selectMenu(Constants.DASHBOARD_MESSAGE, Constants.DASHBOARD_MESSAGE_RECEIVED, false);
			}
		});
		messageImage.setCursor(Cursor.HAND);
		messageImage.setTooltip(I18N.message("message"));


		clipboardSize.setWidth("20px");
		lockedCount.setWidth("20px");
		checkoutCount.setWidth("20px");
		messagesCount.setWidth("20px");

		addMember(clipboardImage);
		addMember(clipboardSize);
		if (!GWT.isScript()) {
			addMember(lockedImage);
			addMember(lockedCount);
		}
		addMember(checkoutImage);
		addMember(checkoutCount);

		addMember(messageImage);
		addMember(messagesCount);

		// hide
		messageImage.hide();
		messagesCount.hide();

		Clipboard.getInstance().addObserver(this);
		Session.get().getUser().addObserver(this);
		onUserChanged(Session.get().getUser(), null);
	}

	public static StatusBarIcons get() {
		if (instance == null)
			instance = new StatusBarIcons();
		return instance;
	}
	
	private void onCheckedSearch(){
//		MainPanel.get().jump("documents;Etc;checkOutList");
		DocumentsPanel.get().getMenu().selectMenu(Constants.MENU_DOCUMENTS_ETC, EtcMenus.CHECKOUTLIST.getId(), false);
	}
	
	// 20130913, junsoo, hide checkout image
	public void hideCheckout(){
		checkoutImage.hide();
		checkoutCount.hide();
	}
	// 20130913, junsoo, hide message image
	public void showMessage(){
		messageImage.show();
		messagesCount.show();
	}
	

	@Override
	public void onAdd(SDocument entry) {
		clipboardSize.setContents(Integer.toString(Clipboard.getInstance().size()));
	}

	@Override
	public void onRemove(SDocument entry) {
		clipboardSize.setContents(Integer.toString(Clipboard.getInstance().size()));
	}

	@Override
	public void onUserChanged(SUser user, String attribute) {
		checkoutCount.setContents(Integer.toString(user.getCheckedOutDocs()));
		lockedCount.setContents(Integer.toString(user.getLockedDocs()));
		messagesCount.setContents(Integer.toString(user.getUnreadMessages()));
	}

	@Override
	public void onUserAdded(SUser user) {
	}

	@Override
	public void onUserRemoved() {
	}
}