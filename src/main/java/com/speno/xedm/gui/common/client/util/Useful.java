package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.speno.xedm.gui.common.client.I18N;

/**
 * 
 * @author deluxjun
 *
 */
public class Useful {

	/**
	 * Show a dialog to confirm a operation.
	 */
	public static void askYesNoCancel(String title, String message, Alignment alignment, final BooleanCallback callback) {
		final Window dialog = new Window();
		dialog.setAutoCenter(true);
		dialog.setIsModal(true);
		dialog.setShowModalMask(true);
		dialog.setShowHeader(true);
		dialog.setWidth(330);
		dialog.setAutoSize(true);
		dialog.setAlign(Alignment.CENTER);
		dialog.setAlign(VerticalAlignment.CENTER);

		dialog.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		dialog.setCanDragResize(false);
		dialog.setCanDrag(true);
		dialog.centerInPage();
		dialog.setTitle(title);

		VStack container = new VStack();
		container.setWidth("100%");
		container.setHeight("100%");
		container.setMembersMargin(8);
		container.setMargin(3);
		container.setAlign(Alignment.CENTER);
		container.setDefaultLayoutAlign(Alignment.CENTER);

		DynamicForm textForm = new DynamicForm();
		textForm.setTitleOrientation(TitleOrientation.TOP);
		textForm.setAlign(alignment);
		textForm.setNumCols(1);
		
		StaticTextItem text = new StaticTextItem();
		text.setShowTitle(false);
		text.setValue(message);
		text.setShouldSaveValue(false);
		text.setWrapTitle(false);
		text.setAlign(alignment);
		textForm.setFields(text);

		IButton yes = new IButton(I18N.message("yes"));
		yes.setWidth(70);
		yes.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(Boolean.TRUE);
				dialog.destroy();
			}
		});

		IButton no = new IButton(I18N.message("no"));
		no.setWidth(70);
		no.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(Boolean.FALSE);
				dialog.destroy();
			}
		});
		
		IButton cancel = new IButton(I18N.message("cancel"));
		cancel.setWidth(70);
		cancel.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(null);
				dialog.destroy();
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(10);
		buttons.setWidth100();
		buttons.setHeight(20);
		buttons.addMember(yes);
		buttons.addMember(no);
		buttons.addMember(cancel);
		buttons.setAlign(Alignment.CENTER);

		container.addMember(textForm);
		container.addMember(buttons);

		dialog.addItem(container);
		dialog.show();
	}
	
	/**
	 * Show a dialog to confirm a operation.
	 */
	public static void ask(String title, String message, final BooleanCallback callback) {
		final Window dialog = new Window();
		dialog.setAutoCenter(true);
		dialog.setIsModal(true);
		dialog.setShowModalMask(true);
		dialog.setShowHeader(true);
		dialog.setWidth(330);
		dialog.setHeight(120);
		dialog.setAlign(Alignment.CENTER);
		dialog.setAlign(VerticalAlignment.CENTER);

		dialog.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		dialog.setCanDragResize(false);
		dialog.setCanDrag(true);
		dialog.centerInPage();
		dialog.setTitle(title);

		VStack container = new VStack();
		container.setWidth("100%");
		container.setMembersMargin(8);
		container.setMargin(3);
		container.setAlign(Alignment.CENTER);
		container.setDefaultLayoutAlign(Alignment.CENTER);

		DynamicForm textForm = new DynamicForm();
		textForm.setTitleOrientation(TitleOrientation.TOP);
		textForm.setAlign(Alignment.CENTER);
		textForm.setNumCols(1);
		
		StaticTextItem text = new StaticTextItem();
		text.setShowTitle(false);
		text.setValue(message);
		text.setShouldSaveValue(false);
		text.setWrapTitle(false);
		text.setAlign(Alignment.CENTER);
		textForm.setFields(text);

		IButton yes = new IButton(I18N.message("yes"));
		yes.setWidth(70);
		yes.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(true);
				dialog.destroy();
			}
		});

		IButton no = new IButton(I18N.message("no"));
		no.setWidth(70);
		no.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(false);
				dialog.destroy();
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(10);
		buttons.setWidth100();
		buttons.setHeight(20);
		buttons.addMember(yes);
		buttons.addMember(no);
		buttons.setAlign(Alignment.CENTER);

		container.addMember(textForm);
		container.addMember(buttons);

		dialog.addItem(container);
		dialog.show();
	}

	/**
	 * Show a dialog asking for a value to complete a operation.
	 */
	public static void askforValue(String title, String message, String defaultValue, String width,
			ValueCallback callback) {
		TextItem textItem = ItemFactory.newTextItem("value", message, defaultValue);
		textItem.setWrapTitle(false);
		textItem.setWidth("100%");
		askforValue(title, message, defaultValue, width, textItem, callback);
	}

	/**
	 * Show a dialog asking for a value to complete a operation. The privided
	 * form item will be used.
	 */
	public static void askforValue(String title, String message, String defaultValue, String width, FormItem item,
			final ValueCallback callback) {
		final Window dialog = new Window();
		dialog.setAutoCenter(true);
		dialog.setIsModal(true);
		dialog.setShowModalMask(true);
		dialog.setShowHeader(true);
		dialog.setWidth(330);
		dialog.setHeight(140);
		dialog.setAlign(Alignment.CENTER);
		dialog.setAlign(VerticalAlignment.CENTER);

		dialog.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		dialog.setCanDragResize(false);
		dialog.setCanDrag(true);
		dialog.centerInPage();
		dialog.setTitle(title);
		if (width != null)
			dialog.setWidth(width);

		VStack container = new VStack();
		container.setWidth("100%");
		container.setMembersMargin(8);
		container.setMargin(3);
		container.setAlign(Alignment.CENTER);
		container.setDefaultLayoutAlign(Alignment.CENTER);

		final DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		item.setWrapTitle(false);
		item.setWidth("100%");
		item.setName("value");
		item.setTitle(I18N.message(message));
		item.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase())) {
					if (callback != null)
						callback.execute(form.getValue("value").toString());
					dialog.destroy();
				}
			}
		});

		form.setFields(item);

		IButton ok = new IButton(I18N.message("ok"));
		ok.setWidth(70);
		ok.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(form.getValue("value").toString());
				dialog.destroy();
			}
		});

		IButton cancel = new IButton(I18N.message("cancel"));
		cancel.setWidth(70);
		cancel.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (callback != null)
					callback.execute(null);
				dialog.destroy();
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(10);
		buttons.setWidth100();
		buttons.setHeight(20);
		buttons.addMember(ok);
		buttons.addMember(cancel);
		buttons.setAlign(Alignment.CENTER);

		container.addMember(form);
		container.addMember(buttons);

		dialog.addItem(container);
		dialog.show();

		if (defaultValue != null) {
			item.setValue(defaultValue);
			if (item instanceof TextItem) {
				((TextItem) item).selectValue();
				if (defaultValue.length() > 0)
					((TextItem) item).setSelectionRange(0, defaultValue.length());
			} else {
				form.focusInItem(item);
				form.setAutoFocus(true);
				form.focus();
			}
		}
	}
}
