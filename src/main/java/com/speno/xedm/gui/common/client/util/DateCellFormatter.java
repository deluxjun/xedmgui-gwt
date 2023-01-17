package com.speno.xedm.gui.common.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.gui.common.client.I18N;

/**
 * 
 * @author deluxjun
 *
 */
public class DateCellFormatter implements CellFormatter {
	private String format = "";

	@Override
	public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
		if (value == null)
			return null;
		DateTimeFormat formatter = DateTimeFormat.getFormat(format);

		return formatter.format((Date) value);
	}

	public DateCellFormatter(boolean shortFormat) {
		if (shortFormat)
			format = I18N.message("format_dateshort");
		else
			format = I18N.message("format_date");
	}
	
	public DateCellFormatter(String shortFormat) {
		format = shortFormat;
	}
	
}
