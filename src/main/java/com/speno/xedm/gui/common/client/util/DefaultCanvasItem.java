package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.gui.common.client.I18N;

public class DefaultCanvasItem extends com.smartgwt.client.widgets.form.fields.CanvasItem {
	private Canvas canvas = null;
	private String name = "canvas";
	private boolean beRequired = false;
	private String lastError = "";
	
	public String getLastError() {
		return lastError;
	}

	public DefaultCanvasItem(String name, boolean beRequired) {
		super(name);
		this.name = name;
		this.beRequired = beRequired;

		setEndRow(true);
		setStartRow(true);
		setColSpan("*");
		setShowTitle(false);

		// this is going to be an editable data item
//		setShouldSaveValue(true);

		addShowValueHandler(new ShowValueHandler() {
			@Override
			public void onShowValue(ShowValueEvent event) {
				DefaultCanvasItem item = (DefaultCanvasItem) event.getSource();

				// TODO:
			}
		});

		setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem item) {
				initCanvas();

				((DefaultCanvasItem) item).setCanvas(canvas);
			}
		});
	}
	
	@Override
	public void hide() {
		if (canvas != null)
			canvas.hide();
		super.hide();
	}
	
	@Override
	public void show() {
		if (canvas != null)
			canvas.show();
		super.show();
	}
	
	@Override
	public Boolean validate() {
		if (beRequired) {
			lastError = I18N.message("fieldrequired") + "(" + name + ")";
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	private void initCanvas() {
		canvas = getCanvas();
	}
	
	// 상속받아 구현할 것
	public Canvas getCanvas() {
		return new HLayout();
	}

};