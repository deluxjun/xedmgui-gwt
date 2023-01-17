package com.speno.xedm.gui.common.client.log;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.core.service.serials.SEvent;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;

/**
 * 
 * @author deluxjun
 *
 */
public class EventsWindow extends Window {

	private static EventsWindow instance = new EventsWindow();

	private ListGrid grid;

	public EventsWindow() {
		super();

		HeaderControl trash = new HeaderControl(HeaderControl.TRASH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.setData(new ListGridRecord[0]);
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, trash, HeaderControls.CLOSE_BUTTON);
		setWidth("50%");
		setHeight(200);
		setTitle(I18N.message("lastevents"));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		grid = new ListGrid() {

			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (SEvent.ERROR.equals(record.getAttribute("severity")))
					return "color: #EF4A4A";
				if (SEvent.WARNING.equals(record.getAttribute("severity")))
					return "color: #FF8723";
				else
					return "color: #577ED0";
			}
		};
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setWidth100();
		grid.setHeight100();
		grid.setCanReorderFields(false);
		grid.setCanFreezeFields(false);
		grid.setCanGroupBy(false);

		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);

		ListGridField message = new ListGridField("message", I18N.message("message"), 200);
		message.setCanSort(false);

		ListGridField detail = new ListGridField("detail", I18N.message("detail"), 300);
		detail.setCanSort(false);

		ListGridField severityLabel = new ListGridField("severityLabel", I18N.message("severity"), 70);

		grid.setFields(date, severityLabel, message, detail);
		grid.setCanResizeFields(true);
		addItem(grid);
	}

	public void addEvent(SEvent event) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("date", event.getDate());
		record.setAttribute("message", event.getMessage());
		record.setAttribute("detail", event.getDetail());
		record.setAttribute("severity", event.getSeverity());
		record.setAttribute("severityLabel", I18N.message(event.getSeverity()));
		grid.addData(record);
		grid.sort("date", SortDirection.DESCENDING);
	}

	public static EventsWindow get() {
		return instance;
	}
}