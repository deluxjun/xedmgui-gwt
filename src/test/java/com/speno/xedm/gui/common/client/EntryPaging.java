package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.HeaderClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.frontend.client.PagingToolbar;
import com.speno.xedm.gui.frontend.client.TestRPCDataSource;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryPaging implements EntryPoint {

	public void onModuleLoad() {

		final ListGrid countryGrid = new ListGrid();  
		countryGrid.setWidth(350); 
		//countryGrid.setHeight(200);  
		countryGrid.setAlternateRecordStyles(true);
		
		
		

		ListGridField indexField = new ListGridField("id", "id", 50);  
		indexField.setShowSelectedIcon(true);
		indexField.setCanToggle(true);
		//indexField.set
		ListGridField nameField = new ListGridField("desc", "desc", 150);
		countryGrid.setFields(indexField, nameField);
		
		countryGrid.setCanResizeFields(true);  
		
		countryGrid.setShowAllRecords(false);
		countryGrid.setSelectionType(SelectionStyle.SIMPLE);  
		countryGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);  
		countryGrid.setDataSource(new TestRPCDataSource());
//		countryGrid.setAutoFetchData(false);
		countryGrid.addHeaderClickHandler(new HeaderClickHandler(){

			public void onHeaderClick(HeaderClickEvent event)
			{
				if (event.getFieldNum()!=0)
				{
					return;
				}
				if (countryGrid.getSelection().length!=0)
				{
					countryGrid.deselectAllRecords();
					event.cancel();
					return;
				}
				Integer[] selected=countryGrid.getVisibleRows();
				
				int[] selects=new int[selected[1]-selected[0]+1];
				for (int i=selected[0]; i<=selected[1]; i++)
				{
					selects[i-selected[0]]=i;
				}
				countryGrid.selectRecords(selects, true);
				event.cancel();
			}});
		 
		PagingToolbar gridPager = new PagingToolbar(countryGrid, 10);
		
		// reload
//		Label reload= new Label();
//		reload.setWidth(40);
//		reload.setStyleName("fakelink");
//		reload.setContents("reload");
//		reload.addClickHandler(new ClickHandler()
//		{
//			public void onClick(ClickEvent event)
//			{
//				TestRPCDataSource.total=TestRPCDataSource.total-10;
//				countryGrid.invalidateCache();
//				countryGrid.fetchData();
//			}
//		});
//		gridPager.addMember(reload);
		gridPager.setDeselect(false);
		
		VLayout v=new VLayout();
		v.setWidth(360);
		v.setHeight100();
		v.addMember(gridPager);
		gridPager.setWidth(350);
		v.addMember(countryGrid);
		v.draw();
		countryGrid.fetchData();
		
	}
}
