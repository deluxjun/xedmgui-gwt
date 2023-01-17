package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;

/**
 * Statistics Widget
 * @author 박상기
 * @since 1.0
 */
public class StatisticsWidget extends VLayout {
	private Label emptyLabel;
	
	/**
	 * Statistics Widget 생성
	 */
	public StatisticsWidget() {
		super();
		setWidth100();
		setHeight100();
		setBorder("1px solid #E1E1E1");
		emptyLabel = new Label(I18N.message("clickandchartdrawn", I18N.message("search")));
		emptyLabel.setWidth100();
		emptyLabel.setHeight100();
		emptyLabel.setAlign(Alignment.CENTER);
		emptyLabel.setValign(VerticalAlignment.CENTER);
		addMember(emptyLabel);
	}
	
	public void loadChart(final String title, final List<String[]> result) {
		
		if(result == null || result.size() < 2) {
			emptyLabel.setContents(I18N.message("notitemstoshow"));
			return;
		}
		
		;
		
  		Options options = Options.create();
  		options.setWidth(Window.getClientWidth()-510);
  		options.setHeight(250);
  		
  		options.setSmoothLine(false);
  		//options.setTitleX("x title");
  		//options.setTitleY("Y Title");
  		
  		if(title != null) {
  			options.setTitle(title);
  	  		options.setTitleFontSize(7L);
  		}
  		options.setMin(0);
  		options.setAxisFontSize(10);
  		
  		DataTable data = DataTable.create();
  		
  		/* --------------------------------------------------------------
  		 * List의 첫번째 배열은 column정보
  		 * 배열의 첫번재 항목은 x축 data, 그외는 line data
  		 *-------------------------------------------------------------- */
  		String[] names = result.get(0);
  		for(int j=0; j<names.length; j++) {
  			data.addColumn((j==0) ? ColumnType.STRING : ColumnType.NUMBER, I18N.message(names[j]));
  		}
  		result.remove(0);
  		
		data.addRows(result.size());
		String[] datas = null;		
		long max = 0L;
		for(int k=0; k<result.size(); k++) {
			datas = result.get(k);
			for(int m=0; m<datas.length; m++) {
				if(m == 0) {
					data.setValue(k, m, datas[m]);
				}
				else {
					if(datas[m] == null) {
						datas[m] = "0";
					}
					
					long val = Long.parseLong(datas[m]);
					if(val > max) {
						max = val;
					}
					data.setValue(k, m, val);
				}
			}
		}	
		options.setMax(max);
		if(emptyLabel != null) {
			this.removeMember(emptyLabel);
		}
		addMember(new LineChart(data, options));
	}
}
