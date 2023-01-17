package com.speno.xedm.gui.common.client;

import java.util.List;

import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.services.SystemService;
import com.speno.xedm.gui.common.client.services.SystemServiceAsync;
import com.speno.xedm.gui.common.client.util.SCM;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryTransition implements EntryPoint {
	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private int width = 500;
	private int height = 400;
	private DataTable data;
	private Options options;
	private LineChart pie;
	private VLayout content;

    public void onModuleLoad() {
    	content = new VLayout();
        Canvas canvas = new Canvas();  
        
        
        final Window window = new Window();  
        window.setTitle("Window with footer");  
        window.setWidth(300);  
        window.setHeight(200);  
        window.setCanDragResize(true);  
        window.setShowFooter(true);  
        window.addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				content.setWidth(window.getWidth() - 10);
				content.setHeight(window.getHeight() - 30);

				options.setWidth(content.getWidth()- 10);
				options.setHeight(content.getHeight() - 30);
				pie.draw(data, options);
			}
		});
  
        window.addItem(content);  
        canvas.addChild(window);  
  
		IButton resizeButton = new IButton(I18N.message("resize"));
		resizeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				width += 10;
				height += 10;
				options.setWidth(width);
				options.setHeight(height);
				pie.draw(data, options);
			}
		});

		IButton loginButton = new IButton(I18N.message("login"));
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				service.getMonitorCount("", "ECM_ARCHIVE", "MAIN", new AsyncCallback<List<String[]>>() {
		  			@Override
		  			public void onFailure(Throwable caught) {
		  				SCM.warn(caught);
		  				
		  			}
		  			@Override
		  			public void onSuccess(List<String[]> result) {
		  				String[] r = result.get(1);
		  				
		  				if(data.getNumberOfRows() >= 5) {
		  					data.removeRow(0);
		  				}
		  				
				      System.out.println(r[0] + "," + r[1] + "," + r[2] + "," +r[3]);
				      
				      int d1 = Integer.parseInt(r[1]);
				      int d2 = Integer.parseInt(r[2]);
				      int d3 = Integer.parseInt(r[3]);
				      int d4 = Integer.parseInt(r[4]);
				      
				      int index = data.addRow();
				      data.setValue(index,  0, r[0]);
				      data.setValue(index,  1, d1);
				      data.setValue(index,  2, d2);
				      data.setValue(index,  3, d3);
				      data.setValue(index,  4, d4);
				      
				      drawChart();
		  			}
		  		});

				

			}
		});

//		content.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		content.setWidth100();
		content.setHeight100();
		content.addMember(loginButton);
//		content.addMember(resizeButton);

//    	RootPanel.get().add(content);
        
	    // Create a callback to be called when the visualization API
	    // has been loaded.
	    Runnable onLoadCallback = new Runnable() {
	      public void run() {
//	        Panel panel = content;
	        
	    	data = (DataTable)createTable();
	    	options = createOptions();

	 
	        // Create a pie chart visualization.
	        pie = new LineChart(data, options);

	        content.addMember(pie);
	      }
	    };

	    // Load the visualization api, passing the onLoadCallback to be called
	    // when loading is done.
	    VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);
	    
        canvas.draw();  

	  }
    
		private void drawChart() {

			pie.draw(data, options);
		  }

	  private Options createOptions() {
	    Options options = Options.create();
	    options.setWidth(200);
	    options.setHeight(200);
	    Properties animation = Properties.create();
        animation.set("duration", 1000.0);
        animation.set("easing", "in");
        options.set("animation", animation);

        Properties chartArea = Properties.create();
        chartArea.set("width", "100%");
        chartArea.set("height", "80%");
        options.set("chartArea", chartArea);
	    
        return options;
	  }

	  private AbstractDataTable createTable() {
	    DataTable data = DataTable.create();
	    data.addColumn(ColumnType.STRING, "X");
	    data.addColumn(ColumnType.NUMBER, "Read");
	    data.addColumn(ColumnType.NUMBER, "Write");
	    data.addColumn(ColumnType.NUMBER, "Delete");
	    data.addColumn(ColumnType.NUMBER, "Error");
	    data.addRow();
	    data.setValue(0, 0, "0");
	    data.setValue(0, 1, 0);
	    data.setValue(0, 2, 0);
	    data.setValue(0, 3, 0);
	    data.setValue(0, 4, 0);
	    return data;
	  }
	}