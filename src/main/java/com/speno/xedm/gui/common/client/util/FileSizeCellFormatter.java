package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * 
 * @author deluxjun
 *
 */
public class FileSizeCellFormatter implements CellFormatter {
	@Override
	public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
		if (value == null)
			return null;
		if (value instanceof Long)
			return Util.formatSizeKB(((Long) value).doubleValue());
		else if (value instanceof Integer)
			return Util.formatSizeKB(((Integer) value).doubleValue());
		else
			return Util.formatSizeKB(0L);
	}
}
