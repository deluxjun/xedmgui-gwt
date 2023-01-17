package com.speno.xedm.gui.frontend.client.clipboard;

import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ClipboardWindow extends Window {

	private static ClipboardWindow instance = new ClipboardWindow();

	private ListGrid grid = new ListGrid();

	public ClipboardWindow() {
		super();

		HeaderControl trash = new HeaderControl(HeaderControl.TRASH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				@SuppressWarnings("deprecation")
				ListGridRecord[] selection = grid.getSelection();
				if (selection == null || selection.length == 0) {
					Clipboard.getInstance().clear();
				} else {
					for (ListGridRecord record : selection) {
						SDocument doc = new SDocument();
						doc.setId(Long.parseLong(record.getAttribute("id")));
						Clipboard.getInstance().remove(doc);
					}
				}

				refresh();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, trash, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("clipboard"));
		setWidth(255);
		setHeight(200);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();
		grid.setCanReorderFields(false);
		grid.setCanFreezeFields(false);
		grid.setCanGroupBy(false);

		ListGridField id = new ListGridField("id");
		// kimsoeun GS인증용 - 클립 보드에 빈 컬럼 없애기
		//id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.message("title"), 200);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");

		// kimsoeun GS인증용 - 클립 보드에 빈 컬럼 없애기 (icon 주석 처리)
		grid.setFields(new ListGridField[] { id, /*icon,*/ title });
		grid.setCanResizeFields(true);
		addItem(grid);

		refresh();
	}

	private void refresh() {
		grid.setData(Clipboard.getInstance().getRecords());
	}

	public static ClipboardWindow getInstance() {
		return instance;
	}

	@Override
	public void show() {
		refresh();
		super.show();
	}

}