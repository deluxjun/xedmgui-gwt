package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.speno.xedm.gui.common.client.I18N;

public abstract class InputStringDialog extends Dialog implements com.smartgwt.client.widgets.form.fields.events.ClickHandler {

	private DynamicForm form;

	private TextAreaItem textArea;
	
	public abstract void onOk(String text);

	public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
		String text = (String) this.textArea.getValue();
		onOk(text);
		this.removeItem(this.form);
		this.markForDestroy();
		this.hide();
	};

	public InputStringDialog(String message, int width, int height, int length) {

		final int WIDTH = width;
		final int HEIGHT = height;

		final String GUIDANCE = I18N.message("guidance_inputstring");

		StaticTextItem label = new StaticTextItem();
		label.setName("label");
		label.setShowTitle(false);
		label.setValue(GUIDANCE);

		TextAreaItem area = new TextAreaItem();
		area.setName("textArea");
		area.setShowTitle(false);
		area.setCanEdit(true);
		area.setHeight("*");
		area.setWidth("*");
		if (message != null && message.length() > 0)
			area.setValue(message);
		if (length > 0)
			area.setValidators(new LengthValidator(area, length));
		this.textArea = area;

		ButtonItem button = new ButtonItem();
		button.setName("apply");
		button.setAlign(Alignment.CENTER);
		button.setTitle(I18N.message("apply"));
		button.addClickHandler(this);

		DynamicForm form = new DynamicForm();
		form.setNumCols(1);
		form.setWidth(WIDTH);
		form.setHeight(HEIGHT);
		form.setAutoFocus(true);
		form.setFields(new FormItem[] { label, this.textArea, button });
		this.form = form;

		this.setAutoSize(true);
		this.setShowToolbar(false);
		this.setCanDragReposition(true);
		this.setTitle(I18N.message("description"));
		this.setShowModalMask(true);
		this.setIsModal(true);
		this.addItem(form);
	}

	public InputStringDialog(String message, int width, int height) {
		this(message, width, height, 4000);	// max 4000
	}

	public InputStringDialog(int width, int height) {
		this("", width, height);
	}
};