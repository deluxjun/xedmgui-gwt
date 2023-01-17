package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.MonitoringObserver;

/**
 * Monitoring Widget
 * @author 박상기
 * @since 1.0
 */
public class MonitoringWidget extends VLayout implements MonitoringObserver {
	public static final int WIDTH = 400;
	public static final int HEIGHT = 200;
	
	private String xAchseName;
	private String xAchseValue;
	private String termType; 
	private int viewPoint;
	private boolean isDynamic;
	 
	private Label emptyLabel;
	private DataTable data;
	private Options options;
	private LineChart chart;;
	
	private HashMap<Integer, Double> preVal = new HashMap<Integer, Double>();
	private double max = 5d;
	private MonitoringPanel parentWin;
	
	private boolean isFirst = true;
	private boolean isMonitoring = false;
	
	/**
	 * Monitoring Widget 생성
	 * @param parentWin
	 */
	public MonitoringWidget(final MonitoringPanel parentWin) {
		super();
		this.parentWin = parentWin;
		
		setWidth100();
		setHeight100();
		
		setBorder("1px solid #E1E1E1");
		setOverflow(Overflow.HIDDEN);
		
		emptyLabel = new Label(I18N.message("clickandchartdrawn", I18N.message("search")));
		emptyLabel.setWidth100();
		emptyLabel.setHeight100();
		emptyLabel.setAlign(Alignment.CENTER);
		emptyLabel.setValign(VerticalAlignment.CENTER);
		
		addMember(emptyLabel);
	}
	
	/**
	 * Widget 초기화
	 * @param width
	 * @param height
	 */
	private void init(int width, int height) {
		remove();
		
  		options = Options.create();	
  		options.setWidth(width);
  		options.setHeight(height);
  		
	    Properties animation = Properties.create();
        animation.set("duration", 500d);
        animation.set("easing", "in");
        
        Properties chartArea = Properties.create();
        chartArea.set("width", "60%");
        chartArea.set("height", "60%");
        options.set("animation", animation);
        options.set("chartArea", chartArea);
        
        if(isDynamic) {
			Properties vAxis = Properties.create();;
	        options.set("vAxis", vAxis);
		}
		else {
			Properties vAxis = Properties.create();
	        vAxis.set("maxValue", this.max);
	        vAxis.set("minValue", 0d);
	        options.set("vAxis", vAxis);
		}
        
        /*
        Options viewWindowOption = Options.create();
        viewWindowOption.set("max",10000d);
        Properties hAxis = Properties.create();
        hAxis.set("viewWindowMode", "explicit");
        hAxis.set("viewWindow",viewWindowOption);
        options.set("hAxis", hAxis);
        */
	}
	
	/**
	 * Widget의 width와 height를 실시간으로 변경
	 * @param width
	 * @param height
	 */
	public void setWidthHeight(int width, int height) {
		options.setWidth(width);
		options.setHeight(height);
		if(chart != null) {
			chart.draw(data, options);
		}
	}
	
	/**
	 * Widget Remove
	 */
	public void remove() {	
		chart = null;
		data = DataTable.create();
		preVal = new HashMap<Integer, Double>();
		
		setMembers(emptyLabel);
	}
	
	/**
	 * Widget Stop
	 */
	public void stop() {
		isFirst = true;
		isMonitoring = false;	
	}
	
	/**
	 * Widget Start
	 * @param width
	 * @param height
	 * @param xAchseName
	 * @param xAchseValue
	 * @param termType
	 * @param viewPoint
	 * @param isDynamic
	 */
	public void start(
			int width,
			int height,
			String xAchseName,
			String xAchseValue,
			String termType, 
			int viewPoint, 
			boolean isDynamic) {
		this.xAchseName = xAchseName;
		this.xAchseValue = xAchseValue;
		this.termType = termType; 
		this.viewPoint = viewPoint;
		this.isDynamic = isDynamic;
		this.isMonitoring = true;
		
		init(width, height);
	}
	
	/**
	 * Widget State 반환
	 * @return
	 */
	public boolean getState() {
		return isMonitoring;
	}
	
	@Override
	public void onCapUpperStack(String term, String gXname) {
		if(!isMonitoring) return;
		if(!xAchseName.equals(gXname)) return;
		
		/* 부하가 많이 걸려서 일단 주석 처리함.
		if(isFirst) {
			isFirst = false;			
			List<String[]> records = parentWin.getTermDatas(xAchseName, viewPoint, termType);			
			List<String[]> result = new ArrayList<String[]>();			
			for(int j=1; j<records.size(); j++) {
				result = new ArrayList<String[]>();
				result.add(records.get(0));
				result.add(records.get(j));			
				addListData(result);
			}			
		} else {
			if(termType.equals(term)) {
				addListData(parentWin.getLastData(xAchseName, termType));
			}
		}
		*/
		
		if(termType.equals(term)) {
			addListData(parentWin.getLastData(xAchseName, termType));
		}
	}
	
	/**
	 * Widget에 Data 추가
	 * @param result
	 */
	private void addListData(List<String[]> result) {
		String[] columns = result.get(0);
		String[] rows = result.get(1);
		
		/*
		System.out.println("-------------------------------------------------------------------------------");
		for(int i=0; i<columns.length; i++) {
			System.out.println("MonitoringWidget addListData columns["+i+"]:"+columns[i]);
		}
		
		for(int i=0; i<rows.length; i++) {
			System.out.println("MonitoringWidget addListData rows["+i+"]:"+rows[i]);
		}
		System.out.println("-------------------------------------------------------------------------------");
		*/
		
		setColumns(columns);
		setRows(rows);
	}
	
	/**
	 * Widget의 Columns Info 설정
	 * @param columns
	 */
	
	private void setColumns(String[] columns) {
		
		/*
		 * columns은 최소 2개가 넘어와야 함.
		 */
		if (data.getNumberOfColumns() < 1) {
			if(2 > columns.length) {
				//X축 Display Value Type
				data.addColumn(ColumnType.STRING);
				
				//데이타 Type이 존재하지 않을 경우 최소 한개를 NUMBER Type으로 Add함
				data.addColumn(ColumnType.NUMBER);
			}
			else {
				for(int j=0; j<columns.length; j++) {
		  			data.addColumn((j==0) ? ColumnType.STRING : ColumnType.NUMBER, columns[j]);
		  		}
			}
		}
		
		if (data.getNumberOfColumns() < 1) {
			for(int j=0; j<columns.length; j++) {
	  			data.addColumn((j==0) ? ColumnType.STRING : ColumnType.NUMBER, columns[j]);
	  		}
		}
	}
	
	/**
	 * Widget의 Rows Info 설정
	 * @param rows
	 */
	private void setRows(String[] rows) {		
		if (contains(emptyLabel)) {
			removeMember(emptyLabel);
		}
		
		while(data.getNumberOfRows() >= viewPoint) {
			data.removeRow(0);
		}
		
		int rowNum = data.addRow();
		
		//X축 Display Value
		Date date = new Date();
		data.setValue(rowNum, 0, date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
		
		//데이타가 존재하지 않을 경우 Default "0" 처리함
		if(2 > rows.length) {
			data.setValue(rowNum, 1, getValue(rowNum, 1, "0"));
		}
		else {
			for(int m=1; m<rows.length; m++) {			
				data.setValue(rowNum, m, getValue(rowNum, m, rows[m]));
			}
		}
		
		if(chart == null) {
			chart = new LineChart(data, options);
			addMember(chart);
		}
		chart.draw(data, options);
	}
	
	/**
	 * Widget의 실 value 추출
	 * @param rowNum
	 * @param colNum
	 * @param rowVal
	 * @return
	 */
	private double getValue(int rowNum, int colNum, String rowVal) {		
		double dRowVal = Double.parseDouble(rowVal);
		
		if(this.xAchseValue == "CONNECTION") {
			return dRowVal;
		}
		else if(rowNum == 0){
			preVal.put(colNum,  dRowVal);
			return 0;
		}
		else {
			//현재값 - 이전값
			double result = dRowVal - preVal.get(Integer.valueOf(colNum));
			preVal.put(colNum,  dRowVal);
			
			//Max값 재설정
			if(this.max < result) {
				this.max =  result;
			}
			
			return result;
		}
	}
	
	/**
	 * 무조건 처음부터 모니터링 할 경우 0부터 시작, pause 시 1term skip.
	 * @param isReCondition
	 * @param rowNum
	 * @param colNum
	 * @param rowVal
	 * @return
	 *
	private double getValueBack(boolean isReCondition, int rowNum, int colNum, String rowVal) {		
		if(rowVal == null) {
			rowVal = "0";
		}
		
		double dRowVal = Double.parseDouble(rowVal);
		
		if(this.value == "CONNECTION") {
			return dRowVal;
		}		
		else if(rowNum == 0){
			preVal.put(colNum,  dRowVal);
			return 0;
		}
		else {
			
			if(isReCondition) {
				//GWT.log("<"+isReCondition+">iRowVal:"+dRowVal+", preVal:"+preVal.get(Integer.valueOf(colNum))+", refresh:"+refresh+", persec:"+persec+", result:"+data.getValueInt(rowNum-1, colNum));
				preVal.put(colNum,  dRowVal);
				return data.getValueInt(rowNum-1, colNum);
			}
			
			// 식 = ((현재값 - 이전값) / refresh) * 초당처리량
			double result = ((dRowVal - preVal.get(Integer.valueOf(colNum))) / Integer.parseInt(this.refresh)) *  Integer.parseInt(this.persec);
			//GWT.log("<"+isReCondition+">iRowVal:"+dRowVal+", preVal:"+preVal.get(Integer.valueOf(colNum))+", refresh:"+refresh+", persec:"+persec+", result:"+result);
			preVal.put(colNum,  dRowVal);
			
			//Max값 재설정
			if(this.max < result) {
				this.max =  result;
			}
			
			return result;
		}
	}
	*/
}
