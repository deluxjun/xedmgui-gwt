package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.List;
import java.util.Map;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.Util;

public class RepositoriesGridPanel extends VLayout{
	private List<Map<String,Object>> dataList;
	private ListGrid grid;
	
	public RepositoriesGridPanel(List<Map<String,Object>> result){
		this.dataList = result;
		initMain();
		initGrid();
		setGridData();
	}
	
	private void initMain(){
		setHeight100();
		setWidth100();
	}
	
	private void initGrid(){
		grid = new ListGrid();
		
		ListGridField archiveId = new ListGridField("archiveId", I18N.message("archive"));
		ListGridField maxSpace 	= new ListGridField("maxSpace", I18N.message("maxspace"));
		ListGridField spaceUsed = new ListGridField("spaceUsed", I18N.message("spaceUsed"));
		ListGridField spaceLeft = new ListGridField("spaceLeft", I18N.message("spaceleft"));
		ListGridField spaceLeftPercent = new ListGridField("spaceLeftPercent", I18N.message("spaceLeftPercent"));
		
		grid.setFields(archiveId, maxSpace, spaceUsed, spaceLeft, spaceLeftPercent);
		addMember(grid);
	}
	
	private void setGridData(){
		double maxSpace = 0.0;
		double spaceLeft = 0.0;
		double spaceUsed = 0.0;
		double percentValue = 0.0;
		
		for (Map<String, Object> data : dataList) {
			maxSpace = Double.parseDouble(((String) data.get("maxSpace")).replaceAll("\"", ""));
			spaceLeft = Double.parseDouble(((String) data.get("spaceLeft")).replaceAll("\"", ""));
			spaceUsed = maxSpace - spaceLeft;
			percentValue = (spaceUsed * 100 / maxSpace);//+ Math.round((spaceUsed * 100 % maxSpace));
			
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("archiveId", data.get("name"));
			record.setAttribute("maxSpace", Util.formatSizeW7(maxSpace));
			record.setAttribute("spaceUsed", Util.formatSizeW7(spaceLeft));
			record.setAttribute("spaceLeft", Util.formatSizeW7(spaceUsed));

			record.setAttribute("spaceLeftPercent", Math.round(percentValue * Math.pow(10, 2)) / Math.pow(10, 2) +"%");

			grid.addData(record);
		}
	}
}
