package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.HashMap;

import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
//import com.google.gwt.visualization.client.visualizations.PieChart;
//import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;

/**
 * Pie Widget
 * @author 박상기
 * @since 1.0
 */
public class PieWidget extends VLayout {
	/**
	 * Pie Widget 생성
	 * @param title
	 * @param map
	 */
	public PieWidget(final String title, final HashMap<String, Object> map) {
		super();		    	
		
		setAutoWidth();
		setAutoHeight();
		
		// Defining the options
		PieOptions options = PieOptions.create();
  		options.setWidth(300);
  		options.setHeight(200);
  		options.set3D(true);
  		options.setTitle(title);
//  		options.setTitleFontSize(18L);

  		// Setup the table with data
  		DataTable data = DataTable.create();
  		data.addColumn(ColumnType.STRING, "name");
  		data.addColumn(ColumnType.NUMBER, "value");
  		data.addRows(map.size());
  		
  		data.setValue(0, 0,  I18N.message("maxspace"));
  		data.setValue(0, 1, Long.parseLong((String)map.get("maxSpace")));
  		
  		data.setValue(1, 0, I18N.message("spaceleft"));
  		data.setValue(1, 1, Long.parseLong((String)map.get("spaceLeft")));
  		
  		// Create a pie chart visualization.
  		PieChart pie = new PieChart(data, options);
  		addMember(pie);		
	}
}
