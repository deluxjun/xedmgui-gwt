package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.MonitoringObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Monitoring Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class MonitoringPanel extends VLayout {	
	private static MonitoringPanel instance = null;	
	private static final int maxWindowCnt = 9;
	
	private static final int LEFT		= 0;
	private static final int TOP		= 25;	
	private static final int WIDTH	= 280;	
	private static final int HIDELEFT = LEFT-WIDTH-30;
	
	private static final int MAX_S	= 3600;	//3600초(=60분)
	private static final int MAX_M	= 1440;	//1440분(=24시간)
	private static final int MAX_T	= 720;		//720시간(=30일)
	private static final int MAX_D	= 365;		//365일(=1년)
	
	private static final int SM	= 60;	//60S	to 1M
	private static final int MT	= 60;	//60M	to 1T
	private static final int TD		= 24;	//24T	to 1D	
	
	private final static String[] ENABLED_ICON = new String[2];
	private final static String[] STATUS_ICON = new String[2];
	
	static { 
		ENABLED_ICON[0] = ItemFactory.newImgIcon("bullet_green.png").getSrc();
		ENABLED_ICON[1] = ItemFactory.newImgIcon("bullet_red.png").getSrc();
		
		STATUS_ICON[0] = ItemFactory.newImgIcon("idle_task.gif").getSrc(); 
		STATUS_ICON[1] = ItemFactory.newImgIcon("running_task.gif").getSrc();
	}
	
	private VLayout docInfoVL;
	private SpacerItem dummyItem;	
	private ButtonItem addButton, startStopButton, removeAllButton, sortButton;
	private Timer timer;
	
	//x-Achse Options Map
	private LinkedHashMap<String, String> xAchseopts = new LinkedHashMap<String, String>();
	
	/*
	 * (모니터링이 수행된적이 있는) 활성화된 x-Achse를 담고 있는 Map
	 * window의 monitoring 버튼을 클릭하면 reqXAchses에 x-Achse값 put되고 이후 해당 window을 close하더라도
	 * 서버로 부터 계속해서 x-Achse 데이타를 받아오게 됨. 
	 */
	private LinkedHashMap<String, String> reqXAchses = new LinkedHashMap<String, String>();
	
	/*
	 * (reqXAchses Map의) x-Achse 마다 적재를 수행한 (초, 분, 시간) Count Map.
	 * 각 x-Achse의 적재한 초 Count가 SM에	도달하면 해당 x-Achse의 초 Count는 초기화(0)되며 상위 dataM에 dataS의 평균을 내어 적재함.
	 * 각 x-Achse의 적재한 분 Count가 MT에	도달하면 해당 x-Achse의 분 Count는 초기화(0)되며 상위 dataT에 dataM의 평균을 내어 적재함.
	 * 각 x-Achse의 적재한 시 Count가 TD에	도달하면 해당 x-Achse의 시 Count는 초기화(0)되며 상위 dataD에 dataT의 평균을 내어 적재함.
	 */
	private LinkedHashMap<String, Integer[]> addedCnt = new LinkedHashMap<String, Integer[]>();
	
	/*
	 * (reqXAchses Map의) x-Achse의 column정보.
	 * 각 x-Achse의 String[0]은 X축 데이타의 값을 의미하며 String[1]부터~ 각 라인의 명(Name)을 의미함
	 */
	private LinkedHashMap<String, String[]> columnInfo = new LinkedHashMap<String, String[]>();
	
	/*
	 * (reqXAchses Map의) 각 x-Achse의 초, 분, 시, 일 Data 리스트.
	 * 각 x-Achse의 초(dataS), 각 x-Achse의 분(dataM), 각 x-Achse의 시(dataT), 각 x-Achse의 일(dataD)
	 */
	private LinkedHashMap<String, ArrayList<String[]>> dataS = new LinkedHashMap<String, ArrayList<String[]>>();
	private LinkedHashMap<String, ArrayList<String[]>> dataM = new LinkedHashMap<String, ArrayList<String[]>>();
	private LinkedHashMap<String, ArrayList<String[]>> dataT = new LinkedHashMap<String, ArrayList<String[]>>();
	private LinkedHashMap<String, ArrayList<String[]>> dataD = new LinkedHashMap<String, ArrayList<String[]>>();
	
	private RadioGroupItem dbStatisticsItem;
	private Canvas body;
	private Label emptyLabel;
	
	/*
	 * window의 (window id is key) 설정폼, 헤더폼, 그리고 Widget(Line Chart)
	 */
	private HashMap<String, DynamicForm> forms = new HashMap<String, DynamicForm>();
	private HashMap<String, DynamicForm> headerForms = new HashMap<String, DynamicForm>();
	private HashMap<String, MonitoringWidget> widgets = new HashMap<String, MonitoringWidget>();
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static MonitoringPanel get() {
		if (instance == null) {
			instance = new MonitoringPanel();
		}
		return instance;
	}

	/**
	 * Monitoring Panel 생성
	 */
	public MonitoringPanel() {
		addMember(new TrackPanel(I18N.message("statistics")+" > "+ I18N.message("statistics")+" > "+ I18N.message("monitoring"), null));
		dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		setWidth100();
		setMembersMargin(10);
		addMember(createMainVL());
//		setMembers(createMainVL());
		
		executeGetOptionsAndSet();
		executeGetDBStatistics();
	}
	
	/**
	 * Term type에 따른 마지막 수신 데이타를 반환함.
	 * 
	 * @param xAchse : x-Achse의 값
	 * @param termType : refresh term time. (Second, Minute, Time ,Day)
	 * @return result :  해당 gXname의 마지막 수신데이타 (columns + rows) 
	 */
	public List<String[]> getLastData(String xAchse, String termType) {
		List<String[]> gXData = null;
		if(MonitoringObserver.TERM_TYPE_S.equals(termType)) {
			gXData = dataS.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_M.equals(termType)) {
			gXData = dataM.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_T.equals(termType)) {
			gXData = dataT.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_D.equals(termType)) {
			gXData = dataD.get(xAchse);
		}
		
		String[] columns = columnInfo.get(xAchse);
		String[] rows = (gXData == null) ? null : gXData.get(gXData.size()-1);
		
		List<String[]> result = new ArrayList<String[]>();
		result.add(0, columns);
		result.add(1, rows);		
		return result;
	}
	
	/**
	 * Term type에 따른 수신되어 적재된 구간 데이타를 반환함.
	 * 구간은 적재된 데이타와 viewPoint를 비교하여 작은값이 구간이 되며
	 * Columns은 최초 한번 add됨
	 * 
	 * @param xAchse : x-Achse의 값
	 * @param viewPoint : 반환할 data의 구간
	 * @param termType : refresh term time. (Second, Minute, Time ,Day)
	 * @return
	 */
	public List<String[]> getTermDatas(String xAchse, int viewPoint, String termType) {
		List<String[]> gXData = null;
		if(MonitoringObserver.TERM_TYPE_S.equals(termType)) {
			gXData = dataS.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_M.equals(termType)) {
			gXData = dataM.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_T.equals(termType)) {
			gXData = dataT.get(xAchse);
		}
		else if(MonitoringObserver.TERM_TYPE_D.equals(termType)) {
			gXData = dataD.get(xAchse);
		}
		
		String[] columns = columnInfo.get(xAchse);
		
		List<String[]> result = new ArrayList<String[]>();
		result.add(columns);
		
		if(gXData != null) {
			int startRows = viewPoint < gXData.size() ?  gXData.size()-viewPoint : 0; 
			for(int j=startRows; j<gXData.size(); j++) {
				result.add(gXData.get(j));		
			}
		}
		return result;
	}
	
	
	/**
	 * Monitoring Panel Main 생성
	 * @return
	 */
	private VLayout createMainVL() {
		/*
		Label subLabel = new Label();   
		subLabel.setHeight(20);   
		subLabel.setAlign(Alignment.LEFT);   
		subLabel.setValign(VerticalAlignment.BOTTOM);   
		subLabel.setWrap(false);   
		subLabel.setStyleName("subTitle");
		subLabel.setContents(I18N.message("monitoring"));
		*/
		
		docInfoVL = new VLayout();
		docInfoVL.setMembersMargin(5);
		docInfoVL.setHeight100();
		docInfoVL.setMembers( createDBStatiActionForm(), createStartStopActionForm(), createBodyTL());
		return docInfoVL;
	}
	
	/**
	 * Monitoring Panel의 Body Panel 생성
	 * @return
	 */
	private Canvas createBodyTL() {
    	emptyLabel = new Label(I18N.message("notitemstoshow"));
		emptyLabel.setWidth100();
		emptyLabel.setHeight100();
		emptyLabel.setAlign(Alignment.CENTER);
		emptyLabel.setValign(VerticalAlignment.CENTER);
		
		body = new Canvas();
    	body.setBorder("3px solid #E1E1E1");		
		body.addChild(emptyLabel);
		return body;
	}
	
	/**
	 * 우측 상단 DB Statistics Action Form 생성
	 * @return
	 */
	private DynamicForm createDBStatiActionForm() {		
		dbStatisticsItem = new RadioGroupItem ("from", I18N.message("dbstatistics"));
		dbStatisticsItem.setWrapTitle(false);
		dbStatisticsItem.setVertical(false);
		dbStatisticsItem.setStartRow(false);
		dbStatisticsItem.setEndRow(false);
		dbStatisticsItem.setWidth(130);
		dbStatisticsItem.setValueMap(createDbStatisticsOpts());
		
		ButtonItem applyButton = new ButtonItem();
		applyButton.setTitle(I18N.message("apply"));
		applyButton.setIcon("[SKIN]/actions/accept.png");
		applyButton.setWidth(80);
		applyButton.setStartRow(false);
		applyButton.setEndRow(true);
		applyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				SC.confirm(I18N.message("applyondb"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeSetDBStatistics();							
						}
					}
				});
			}
        });
		
		DynamicForm searchForm = new DynamicForm();
		searchForm.setNumCols(4);
		searchForm.setColWidths("*","1","1","1");
		searchForm.setItems(dbStatisticsItem, dummyItem, applyButton);
		
		return searchForm;
	}
	
	/**
	 * 좌측 상단 Action( Add Monitor, Start, Remove All, Sort) Form 생성
	 * @return
	 */
	private DynamicForm createStartStopActionForm() {
		addButton = new ButtonItem();
		addButton.setTitle(I18N.message("addmonitor"));
		addButton.setIcon("[SKIN]/RecordEditor/add.png");
		addButton.setWidth(100);
		addButton.disable();
		addButton.setStartRow(true);
		addButton.setEndRow(false);
		addButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				addMonitor();
			}
        });
		
		startStopButton = new ButtonItem();
		startStopButton.setTitle(I18N.message("start"));		
		startStopButton.setIcon(ENABLED_ICON[1]);
		startStopButton.setWidth(100);
		startStopButton.disable();
		startStopButton.setStartRow(false);
		startStopButton.setEndRow(false);
		startStopButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if(timer == null) {
					startTimer();
				}
				else {
					SC.confirm(I18N.message("allstopmonitor"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {								
								stopTimer();
							}
						}
					});					
				}
			}
        });
		
		removeAllButton = new ButtonItem();
		removeAllButton.setTitle(I18N.message("removeall"));
		removeAllButton.setIcon(ItemFactory.newImgIcon("delete.png").getSrc());
		removeAllButton.setWidth(100);
		removeAllButton.disable();
		removeAllButton.setStartRow(false);
		removeAllButton.setEndRow(false);
		removeAllButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				SC.confirm(I18N.message("removeallmonitor"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							removeAllMonitor();
						}
					}
				});
			}
        });
		
		sortButton = new ButtonItem();
		sortButton.setTitle(I18N.message("sort"));
		sortButton.setIcon("[SKIN]/DynamicForm/ColorPicker_icon.png");
		sortButton.setWidth(100);
		sortButton.disable();
		sortButton.setStartRow(false);
		sortButton.setEndRow(false);
		sortButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {			
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				SC.confirm(I18N.message("sort4x3"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							int wIndex = 0;
							int hIndex = 0;
							final Canvas[] charts = body.getChildren();
							if(charts != null) {
								for(int i=0; i<charts.length; i++) {
									if(charts[i] instanceof Window) {
										Window window = (Window)charts[i];
										window.setLeft(MonitoringWidget.WIDTH * (wIndex%3));
										window.setTop(MonitoringWidget.HEIGHT * hIndex);
										
										if(window.getMinimized()) {
											window.resizeTo(MonitoringWidget.WIDTH, window.getHeight());
										}
										else {
											
											window.resizeTo(MonitoringWidget.WIDTH, MonitoringWidget.HEIGHT);
										}
										
										++wIndex;										
										if(wIndex%3 == 0) {
											++hIndex;
										}
									}
								}
							}
						}
					}
				});
			}
        });
		
		DynamicForm actionForm = new DynamicForm();
		actionForm.setNumCols(4);
		actionForm.setColWidths("1","1","1","*");
		actionForm.setItems(addButton, startStopButton, removeAllButton, sortButton);
		return actionForm;
	}
	
	/**
	 * (각) 모니터링 Window 생성
	 * @param left
	 * @param top
	 * @return
	 */	
	private Window createChartWin(int left, int top) {		
		final MonitoringWidget widget = new MonitoringWidget(this);
		final Window window = new Window();
		
		final ClickHandler settingsClickHandler = new ClickHandler() {   
            public void onClick(ClickEvent event) {            	
            	DynamicForm form = forms.get(window.getID());
            	if(form.getLeft() == LEFT) {
            		form.animateMove(HIDELEFT, TOP);
            	}
            	else if(form.getLeft() == HIDELEFT) {
            		form.animateMove(LEFT, TOP);
            	}
            	else {
            		form.animateMove(LEFT, TOP);
            	}
            }   
        };
		
        final ClickHandler minusClickHandler = new ClickHandler() {   
            public void onClick(ClickEvent event) {     
            	int[] reSize = getResizePer(window.getWidth(), window.getHeight(), false);            	
            	window.resizeTo(reSize[0], reSize[1]);
            }   
        };  
        
        final ClickHandler plusClickHandler = new ClickHandler() {   
            public void onClick(ClickEvent event) {            	
            	int[] reSize = getResizePer(window.getWidth(), window.getHeight(), true);            	
            	window.resizeTo(reSize[0], reSize[1]);
            }   
        };  
        
        final DynamicForm xAchseForm = new DynamicForm();
        xAchseForm.setLayoutAlign(Alignment.CENTER);   
        xAchseForm.setLayoutAlign(VerticalAlignment.CENTER);
        xAchseForm.setNumCols(2);
        xAchseForm.setAutoWidth();
        
        final SelectItem xAchseItem = new SelectItem("x", I18N.message("x-Achse"));
        xAchseItem.setShowTitle(false);
		xAchseItem.setRequired(true);
		xAchseItem.setEmptyDisplayValue(I18N.message("choosetype"));		
		xAchseItem.setValueMap(xAchseopts);
		xAchseItem.setDefaultValue("");
		
		final ButtonItem monitoringButton = new ButtonItem("monitoring");
      	monitoringButton.setTitle(I18N.message("monitoring"));
      	monitoringButton.setIcon(STATUS_ICON[0]);
      	monitoringButton.setWidth(100);
      	monitoringButton.setStartRow(false);
      	monitoringButton.setEndRow(false);
		monitoringButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(widget.getState()) {
					widget.stop();
					monitoringButton.setTitle(I18N.message("monitoring"));
					monitoringButton.setIcon(STATUS_ICON[0]);
				}
				else {
					DynamicForm chartForm = forms.get(window.getID());					
					boolean validXAchse = xAchseForm.validate(); 
					boolean validChart = chartForm.validate();
					
					if(validChart) {
						chartForm.animateMove(HIDELEFT, TOP);					
					}
					else {
						chartForm.animateMove(LEFT, TOP);
					}
					
					if(validXAchse && validChart) {						
						reqXAchses.put((String)xAchseItem.getDisplayValue(), (String)xAchseItem.getValueAsString());
						
						final SelectItem termItem = (SelectItem)chartForm.getItem("term");
						final SelectItem viewPointItem = (SelectItem)chartForm.getItem("viewpoint");
						final RadioGroupItem graphtypeItem = (RadioGroupItem)chartForm.getItem("graphtype");						
						
						widget.start(
								window.getWidth()-10,
								window.getHeight()-30,
								xAchseItem.getDisplayValue(),
								xAchseItem.getValueAsString(),
								termItem.getValueAsString(),
								Integer.valueOf(viewPointItem.getValueAsString()),
								(Boolean)graphtypeItem.getValue());
						
						monitoringButton.setTitle(I18N.message("stop"));
						monitoringButton.setIcon(STATUS_ICON[1]);
						
						if(timer == null) {
							startTimer();
						}
					}
				}
			}
        });
		
		xAchseForm.setFields(xAchseItem, monitoringButton);
        
        HeaderControl settings	= new HeaderControl(HeaderControl.SETTINGS,	settingsClickHandler);
        HeaderControl minus	= new HeaderControl(HeaderControl.MINUS,		minusClickHandler);
        HeaderControl plus 	= new HeaderControl(HeaderControl.PLUS, 		plusClickHandler);
        
        window.setLeft(left);
        window.setTop(top);
		window.setTitle(I18N.message("x-Achse"));
		window.setWidth(MonitoringWidget.WIDTH);
		window.setHeight(MonitoringWidget.HEIGHT);
		window.setMinWidth(MonitoringWidget.WIDTH);
		window.setMinHeight(MonitoringWidget.HEIGHT);		
		window.setOverflow(Overflow.HIDDEN);
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		window.setAnimateMinimize(true);
		window.addItem(widget);
		
		window.setHeaderControls(
				HeaderControls.HEADER_LABEL,
				xAchseForm,
				settings,
				minus, 
				plus, 
				HeaderControls.MAXIMIZE_BUTTON,
				HeaderControls.MINIMIZE_BUTTON, 
				HeaderControls.CLOSE_BUTTON);
		
		window.addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				SC.confirm(I18N.message("windowclose"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							widget.remove();
			            	window.removeItem(widget);
			            	widget.destroy();
			            	body.removeChild(window);
			            	window.destroy();
			            	
			            	forms.remove(window.getID());
							headerForms.remove(window.getID());
							widgets.remove(window.getID());
						}
					}
				});
			}
		});
		
		window.addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				int width = ((Window)event.getSource()).getWidth();
				int height = ((Window)event.getSource()).getHeight();				
				widget.setWidthHeight(width- 10, height - 30);
			}
		});
		
		headerForms.put(window.getID(), xAchseForm);
		widgets.put(window.getID(), widget);
		
		createChartForm(window, widget);
    	return window;
    }
	
	/**
	 * (각) 모니터링 Window의 설정 Form 생성
	 * @param window
	 * @param widget
	 * @return
	 */
	private String createChartForm(final Window window, final MonitoringWidget widget) {
		
		final SelectItem termItem = new SelectItem("term", I18N.message("term"));
		termItem.setWrapTitle(false);
		termItem.setRequired(true);
		termItem.setValueMap(createTermItemOpts());   
		termItem.setDefaultValue(MonitoringObserver.TERM_TYPE_S);
      	
      	final SelectItem viewPointItem = new SelectItem("viewpoint", I18N.message("viewpoint"));
      	viewPointItem.setWrapTitle(false);
      	viewPointItem.setRequired(true);
      	viewPointItem.setValueMap(createViewPointItemOpts(MonitoringObserver.TERM_TYPE_S));   
      	viewPointItem.setDefaultToFirstOption(true);
      	
      	final RadioGroupItem graphtypeItem = new RadioGroupItem ("graphtype", I18N.message("graphtype"));
      	graphtypeItem.setWrapTitle(false);
      	graphtypeItem.setVertical(false);
      	graphtypeItem.setValueMap(createGraphTypeOpts());
      	graphtypeItem.setDefaultValue(false);
		
		final DynamicForm chartForm = new DynamicForm();
      	chartForm.setAutoHeight();      	
      	chartForm.setItems(termItem, viewPointItem, graphtypeItem);
      	
      	chartForm.setParentElement(window);   
      	chartForm.setShowEdges(true);   
      	chartForm.setBackgroundColor("#ffffd0");   
      	chartForm.setPadding(5);   
      	chartForm.setWidth(WIDTH);   
      	chartForm.setTop(TOP);
      	chartForm.setLeft(LEFT-WIDTH);   
      	chartForm.setAlign(Alignment.CENTER);   
      	chartForm.setAnimateTime(200);
		
		termItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if(chartForm.validate()) {
					LinkedHashMap<String, String> opts = createViewPointItemOpts((String)event.getValue());
					viewPointItem.setValueMap(opts);
					viewPointItem.setValue(opts.keySet().iterator().next());
				}
			}
		});
      	
      	viewPointItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				chartForm.validate();
			}
		});
		
		graphtypeItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				chartForm.validate();
			}
		});		
      	
      	forms.put(window.getID(), chartForm);
		return widget.getID();
	}
	
	/**
	 * 좌측 상단의 Add Monitor 버튼의 클릭 이벤트 처리.
	 * (모니터링 Window 생성)
	 */
	private void addMonitor() {
		if (body.contains(emptyLabel)) {
			body.removeChild(emptyLabel);
		}
		
		if(widgets.size() >= maxWindowCnt) {
			SC.say(I18N.message("maxchartnum", String.valueOf(maxWindowCnt)));
			return;
		}
		
		int left = 30 * (widgets.size() % 10);
		int top = 30 * (widgets.size() % 10);
		
		body.addChild(createChartWin(left, top));
	}

	/**
	 * (각) 모니터링 Window의 상단 -, + 버튼 처리
	 * @param width
	 * @param height
	 * @param isPlus
	 * @return
	 */
	private int[] getResizePer(int width, int height, boolean isPlus) {
		int div = (isPlus) ? 1 : -1;
		int wRate = MonitoringWidget.WIDTH/2;
		int hRate = MonitoringWidget.HEIGHT/2;
				
		int wPer = width/wRate+div;
		int hPer = height/hRate+div;
		
		wPer = (wPer < 2) ? 2 : (wPer > 6) ? 6 : wPer;
		hPer = (hPer < 2) ? 2 : (hPer > 6) ? 6 : hPer;
		
		int[] result = new int[2];
		result[0] = (isPlus) ? (int)Math.floor(wRate * wPer) : (int)Math.ceil(wRate * wPer);
		result[1] = (isPlus) ? (int)Math.ceil(hRate * hPer) : (int)Math.floor(hRate * hPer);		
		return result;
	}	
    
	/**
	 * 우측 상단 DB Statistics 상태 조회
	 */
    private void executeGetDBStatistics() {
    	ServiceUtil.system().isDBStatisticsEnabled(Session.get().getSid(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				dbStatisticsItem.setValue(result);
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
    }
    
    /**
     * 타이머 Start
     */
	private void startTimer() {
		if(timer != null) return;
		
		timer = new Timer() {
			public void run() {
				executeFetch();
			}
		};

		timer.scheduleRepeating(1000);
		
		startStopButton.setTitle(I18N.message("stop"));		
		startStopButton.setIcon(ENABLED_ICON[0]);
	}
    
    /**
     * 타이머 Stop.
     */
	private void stopTimer() {
		if( timer != null) {
			timer.cancel();
			timer = null;
		}
			
		startStopButton.setTitle(I18N.message("start"));		
		startStopButton.setIcon(ENABLED_ICON[1]);
		
		stopAllMonitor();
	}
	
	/**
	 * (모든) 모니터링 Window의 Stop.
	 * (실제 수행하는 것은 아이콘을 바꾸는것 뿐)
	 */
	private void stopAllMonitor() {
		final String[] charts = widgets.keySet().toArray(new String[0]);
		if(charts != null) {
			for(int i=0; i<charts.length; i++) {
				MonitoringWidget widget = widgets.get(charts[i]);
				widget.stop();
				
				ButtonItem monitoringButton = (ButtonItem)((headerForms.get(charts[i]).getField("monitoring")));
				monitoringButton.setTitle(I18N.message("monitoring"));
				monitoringButton.setIcon(STATUS_ICON[0]);
			}
		}
	}
	
	/**
	 *  (모든) 모니터링 Window의 Remove.
	 */
	private void removeAllMonitor() {		
		final Canvas[] charts = body.getChildren();
		if(charts != null) {
			for(int i=0; i<charts.length; i++) {
				if(charts[i] instanceof Window) {
					Window window = (Window)charts[i];
					MonitoringWidget widget = widgets.get(window.getID());										
					widget.remove();
					
					window.removeItem(widget);
					widget.depeer();
					body.removeChild(window);
					window.destroy();
				}
			}
		}
		
		forms = new HashMap<String, DynamicForm>();
		headerForms = new HashMap<String, DynamicForm>();
		widgets = new HashMap<String, MonitoringWidget>();
		
		body.addChild(emptyLabel);
	}
	
	/**
	 * 모니터링 데이타 조회
	 */
	private void executeFetch() {
		
		final String[] names = reqXAchses.keySet().toArray(new String[0]);
		final String[] values = new String[names.length];
		
		for(int i=0; i<names.length; i++) {
			values[i] = reqXAchses.get(names[i]);
		}
			
		ServiceUtil.system().getMonitorCount(Session.get().getSid(), names, values, new AsyncCallback<List<List<String[]>>>() {
  			@Override
  			public void onFailure(Throwable caught) {
  				stopTimer();
  				SCM.warn(caught);
  			}
  			@Override
  			public void onSuccess(List<List<String[]>> result) {  				
  				for(int j=0; j<names.length; j++) {
  					setRecords(names[j], result.get(j));
  				}
  				
  				//debug(names, "dataS", dataS);
  				//debug(names, "dataM", dataM);
  				//debug(names, "dataT", dataT);
  				//debug(names, "dataD", dataD);
  			}
  		});
	}
	
	
//	private void debug(String[] names,  String dataNm, LinkedHashMap<String, ArrayList<String[]>> dataX) {
//		for(int i=0; i<names.length; i++) {
//			ArrayList<String[]> temp = dataX.get(names[i]);
//			System.out.println("-------------------------------------------------------------------------------");
//			System.out.println(names[i]);
//			System.out.println("<"+dataNm+".get("+i+") count:"+temp.size()+">");
//			for(int j=0; j<temp.size(); j++) {
//				String[] ttt = temp.get(j);
//				System.out.println("    <"+dataNm+".get("+i+").get("+j+") count:"+ttt.length+">");
//				for(int u=0; u<ttt.length; u++) {
//					System.out.println("        <"+dataNm+".get("+i+").get("+j+")["+u+"] : ["+ttt[u]+"]>");
//				}  						
//			}
//		}
//	}
	
	/**
	 * 수신한 모니터링 데이타로 부터 Record 설정
	 * 
	 * (reqXAchses Map의) x-Achse
	 * 
	 * @param xName :  x-Achse
	 * @param recordInfo
	 */
	private void setRecords(String xAchse, List<String[]> recordInfo) {
			setColumns( xAchse, recordInfo.get(0));
			setRows( xAchse, recordInfo.get(1));
	}
	
	/**
	 * xAchse에 해당하는 dataS, dataM, dataT, dataD 설정
	 * @param xAchse
	 * @param columns
	 */
	private void setColumns(String xAchse, String[] columns) {		
		if(columnInfo.get(xAchse) == null) {					
			columnInfo.put(xAchse, columns);
			GWT.log("[ MonitoringPanel setColumns ] columnInfo.put("+xAchse+", length["+columns.length+"]");
		}
	}
	
	/**
	 * xAchse에 해당하는 columnInfo 설정
	 * @param xAchse
	 * @param rows
	 */
	private void setRows(String xAchse, String[] rows) {
		if(dataS.get(xAchse) == null)	{ dataS.put(xAchse, new ArrayList<String[]>()); }
		if(dataM.get(xAchse) == null)	{ dataM.put(xAchse, new ArrayList<String[]>()); }
		if(dataT.get(xAchse) == null)	{ dataT.put(xAchse, new ArrayList<String[]>()); }
		if(dataD.get(xAchse) == null)	{ dataD.put(xAchse, new ArrayList<String[]>()); }
		
		Integer[] iAddedSMT = addedCnt.get(xAchse);
		if(iAddedSMT == null) {
			iAddedSMT = new Integer[3];
			iAddedSMT[0] = new Integer(0);
			iAddedSMT[1] = new Integer(0);
			iAddedSMT[2] = new Integer(0);
		}
		
		//매초 수신한 데이타 적재
		capUpperStack(rows, dataS.get(xAchse), MAX_S, MonitoringObserver.TERM_TYPE_S, xAchse);
		
		//SM(60초)개의 dataS의 평균값을 dataM에 적재
		if(++iAddedSMT[0] == SM) {
			iAddedSMT[0] = 0;
			capUpperStack(dataS.get(xAchse), SM, dataM.get(xAchse), MAX_M, MonitoringObserver.TERM_TYPE_M, xAchse);
			
			//MT(60분)개의 dataM의 평균값을 dataT에 적재
			if(++iAddedSMT[1] == MT) {
				iAddedSMT[1] = 0;
				capUpperStack(dataM.get(xAchse), MT, dataT.get(xAchse), MAX_T, MonitoringObserver.TERM_TYPE_T, xAchse);
				
				//TD(24시간)개의 dataT의 평균값을 dataD에 적재
				if(++iAddedSMT[2] == TD) {
					iAddedSMT[2] = 0;
					capUpperStack(dataT.get(xAchse), TD, dataD.get(xAchse), MAX_D, MonitoringObserver.TERM_TYPE_D, xAchse);
				}
			}
		}
		addedCnt.put(xAchse, iAddedSMT);
	}
	
	/**
	 * 매초 수신한 데이타를 dataS에 적재
	 * 
	 * @param rows
	 * @param trgList
	 * @param trgMaxSize
	 * @param termType
	 * @param xAchse
	 */
	private void capUpperStack(String[] rows, ArrayList<String[]> trgList, int trgMaxSize, String termType, String xAchse) {
		//최대 허용개수에 도달했을 경우
		if(trgList.size() == trgMaxSize) {
			//첫번재값 제거
			trgList.remove(0);
		}
		//rows 추가
		trgList.add(rows);
		
		//적재되었음을 통보함
		onCapUpperStack(termType, xAchse);
	}
	
	/**
	 * srcList의 평균 데이타를 trgList에 적재 
	 * 
	 * @param srcList
	 * @param srcPerCnt
	 * @param trgList
	 * @param trgMaxSize
	 * @param termType
	 * @param xAchse
	 */
	private void capUpperStack(ArrayList<String[]> srcList, int srcPerCnt, ArrayList<String[]> trgList, int trgMaxSize, String termType, String xAchse) {
		
		//스트링배열 개수
		int valueCnt = srcList.get(0).length;
		
		String[] newRow = new String[valueCnt];
		
		//라인차트의 순수 라인 개수. (String[0]는 X축(=시간) 값이므로 제거함(-1))
		int lineCnt = valueCnt-1;
		
		//각 라인별 합계를 담은 double 변수. 
		double[] temp = new double[lineCnt];
		for(int k=0; k<lineCnt; k++) {
			temp[k] = 0d;
		}
		
		//합산할 시작인덱스
		int fromIndex = srcList.size()-srcPerCnt;
		
		for(int j=0; j<srcPerCnt; j++) {
			String[] sec = srcList.get(j+fromIndex);
			
			for(int k=0; k<lineCnt; k++) { 
				//X축(=시간) 값은 포함하지 않기위해 k+1
				temp[k] += Double.parseDouble(sec[k+1]);
			}
		}
		
		//newRow정의
		newRow[0] = "";
		for(int k=0; k<lineCnt; k++) {
			newRow[k+1] = String.valueOf(temp[k] / srcPerCnt);
		}
		
		//trgList 가 최대 허용개수에 도달했을 경우
		if(trgList.size() == trgMaxSize) {
			//첫번재값 제거
			trgList.remove(0);
		}		
		//trgList값 추가
		trgList.add(newRow);
		
		//적재되었음을 통보함
		onCapUpperStack(termType, xAchse);
	}
	
	/**
	 * 적재되었음을 각 MonitoringWidget에 통보함
	 * @param termType
	 * @param xAchse
	 */
	private void onCapUpperStack(String termType, String xAchse) {
		/*
		final Canvas[] charts = body.getChildren();
		if(charts != null) {
			for(int i=0; i<charts.length; i++) {
				if(charts[i] instanceof Window) {
					Window window = (Window)charts[i];
					MonitoringWidget widget = widgets.get(window.getID());
					System.out.println("widget.onCapUpperStack("+termType+");");
					widget.onCapUpperStack(termType);
				}
			}
		}
		*/
		
		final String[] charts = widgets.keySet().toArray(new String[0]);
		if(charts != null) {
			for(int i=0; i<charts.length; i++) {
				MonitoringWidget widget = widgets.get(charts[i]);
				widget.onCapUpperStack(termType, xAchse);
			}
		}
	}
    
	/**
	 * 우측 상단 DB Statistics 상태 Update
	 */
    private void executeSetDBStatistics() {
    	ServiceUtil.system().setDBStatisticsEnabled(Session.get().getSid(), (Boolean)dbStatisticsItem.getValue(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				SC.say(I18N.message("operationcompleted"));
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
    }
	
    /**
     * x-Achse Options 조회
     * (조회 성공시 좌측 상단 Action( Add Monitor, Start, Remove All, Sort)  버튼 활성화 됨)
     */
	private void executeGetOptionsAndSet() {
		ServiceUtil.system().listMonitor(Session.get().getSid(), new AsyncCallback<List<String[]>>() {
			@Override
			public void onSuccess(List<String[]> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						xAchseopts.put((result.get(j)[1]==null || "".equals(result.get(j)[1])) ? 
								result.get(j)[0] : result.get(j)[1],
								I18N.message(result.get(j)[0]));
					}
				}
				addButton.enable();
				startStopButton.enable();
				removeAllButton.enable();
				sortButton.enable();
				
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}

	/**
	 * 우측 상단 DB Statistics Options 생성
	 * @return
	 */
	private LinkedHashMap<Boolean, String> createDbStatisticsOpts() {
		LinkedHashMap<Boolean, String> valueMap = new LinkedHashMap<Boolean, String>();
		valueMap.put(true, I18N.message("enabled"));
		valueMap.put(false, I18N.message("disabled"));
		return valueMap;
	}
	
	/**
	 *  (모니터링 Window의) Graph Type Options 생성
	 * @return
	 */
	private LinkedHashMap<Boolean, String> createGraphTypeOpts() {
		LinkedHashMap<Boolean, String> valueMap = new LinkedHashMap<Boolean, String>();
		valueMap.put(false, I18N.message("increasemax"));
		valueMap.put(true, I18N.message("dynamic"));
		return valueMap;
	}
	
	/**
	 * (모니터링 Window의) Term Options 생성
	 * @return
	 */
	private LinkedHashMap<String, String> createTermItemOpts() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(MonitoringObserver.TERM_TYPE_S, "Second");
		valueMap.put(MonitoringObserver.TERM_TYPE_M, "Minute");
		valueMap.put(MonitoringObserver.TERM_TYPE_T, "Time");
		valueMap.put(MonitoringObserver.TERM_TYPE_D, "Day");
		return valueMap;
	}
	
	/**
	 * (모니터링 Window의) View Point Options 생성
	 * @param term
	 * @return
	 */
	private LinkedHashMap<String, String> createViewPointItemOpts(String term) {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		if(MonitoringObserver.TERM_TYPE_S.equals(term)) {
			valueMap.put("30", "30 seconds"); //30초
			valueMap.put("60", "1 minute"); //1분
			valueMap.put("600", "10 minutes"); //10분
			valueMap.put("1800", "30 minutes"); //30분
		}
		else if(MonitoringObserver.TERM_TYPE_M.equals(term)) {
			valueMap.put("30",	"30 minutes"); //30분
			valueMap.put("60",	"60 minutes"); //1시간
			valueMap.put("720",	"12 hours"); //12시간
			valueMap.put("1440", "24 hours"); //24시간
		}
		else if(MonitoringObserver.TERM_TYPE_T.equals(term)) {
			valueMap.put("24",	"24 times"); //24시간
			valueMap.put("168",	"7 days"); //7일
			valueMap.put("720",	"30 days"); //30일
		}
		else if(MonitoringObserver.TERM_TYPE_D.equals(term)) {
			valueMap.put("30",	"30 days"); //30일
			valueMap.put("365",	"1 year"); //1년
		}
		return valueMap;
	}
}