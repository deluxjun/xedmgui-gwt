package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryVisual implements EntryPoint {
//    private SimpleLayoutPanel layoutPanel;
//    private PieChart pieChart;
//
//    /**
//     * This is the entry point method.
//     */
//    @Override
//    public void onModuleLoad() {
//            Window.enableScrolling(false);
//            Window.setMargin("0px");
//
//            RootLayoutPanel.get().add(getSimpleLayoutPanel());
//
//            // Create the API Loader
//            ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
//            chartLoader.loadApi(new Runnable() {
//
//                    @Override
//                    public void run() {
//                            getSimpleLayoutPanel().setWidget(getPieChart());
//                            drawPieChart();
//                    }
//            });
//    }
//
//    private SimpleLayoutPanel getSimpleLayoutPanel() {
//            if (layoutPanel == null) {
//                    layoutPanel = new SimpleLayoutPanel();
//            }
//            return layoutPanel;
//    }
//
//    private Widget getPieChart() {
//            if (pieChart == null) {
//                    pieChart = new PieChart();
//            }
//            return pieChart;
//    }
//
//    private void drawPieChart() {
//            // Prepare the data
//            DataTable dataTable = DataTable.create();
//            dataTable.addColumn(ColumnType.STRING, "Name");
//            dataTable.addColumn(ColumnType.NUMBER, "Donuts eaten");
//            dataTable.addRows(4);
//            dataTable.setValue(0, 0, "Michael");
//            dataTable.setValue(1, 0, "Elisa");
//            dataTable.setValue(2, 0, "Robert");
//            dataTable.setValue(3, 0, "John");
//            dataTable.setValue(0, 1, 5);
//            dataTable.setValue(1, 1, 7);
//            dataTable.setValue(2, 1, 3);
//            dataTable.setValue(3, 1, 2);
//
//            // Draw the chart
//            pieChart.draw(dataTable);
//    }


    public void onModuleLoad() {
        
	    // Create a callback to be called when the visualization API
	    // has been loaded.
	    Runnable onLoadCallback = new Runnable() {
	      public void run() {
	        Panel panel = RootPanel.get();
	 
	        // Create a pie chart visualization.
	        PieChart pie = new PieChart(createTable(), createOptions());

	        pie.addSelectHandler(createSelectHandler(pie));
	        panel.add(pie);
	      }
	    };

	    // Load the visualization api, passing the onLoadCallback to be called
	    // when loading is done.
	    VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);
	  }

	  private Options createOptions() {
	    Options options = Options.create();
	    options.setWidth(400);
	    options.setHeight(240);
	    options.set3D(true);
	    options.setTitle("My Daily Activities");
	    return options;
	  }

	  private SelectHandler createSelectHandler(final PieChart chart) {
	    return new SelectHandler() {
	      @Override
	      public void onSelect(SelectEvent event) {
	        String message = "";
	        
	        // May be multiple selections.
	        JsArray<Selection> selections = chart.getSelections();

	        for (int i = 0; i < selections.length(); i++) {
	          // add a new line for each selection
	          message += i == 0 ? "" : "\n";
	          
	          Selection selection = selections.get(i);

	          if (selection.isCell()) {
	            // isCell() returns true if a cell has been selected.
	            
	            // getRow() returns the row number of the selected cell.
	            int row = selection.getRow();
	            // getColumn() returns the column number of the selected cell.
	            int column = selection.getColumn();
	            message += "cell " + row + ":" + column + " selected";
	          } else if (selection.isRow()) {
	            // isRow() returns true if an entire row has been selected.
	            
	            // getRow() returns the row number of the selected row.
	            int row = selection.getRow();
	            message += "row " + row + " selected";
	          } else {
	            // unreachable
	            message += "Pie chart selections should be either row selections or cell selections.";
	            message += "  Other visualizations support column selections as well.";
	          }
	        }
	        
	        Window.alert(message);
	      }
	    };
	  }

	  private AbstractDataTable createTable() {
	    DataTable data = DataTable.create();
	    data.addColumn(ColumnType.STRING, "Task");
	    data.addColumn(ColumnType.NUMBER, "Hours per Day");
	    data.addRows(2);
	    data.setValue(0, 0, "Work");
	    data.setValue(0, 1, 14);
	    data.setValue(1, 0, "Sleep");
	    data.setValue(1, 1, 10);
	    return data;
	  }
	}