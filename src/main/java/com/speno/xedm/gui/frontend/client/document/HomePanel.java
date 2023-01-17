package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.popup.MessagePopup;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gwt.service.FolderService;
import com.speno.xedm.gwt.service.FolderServiceAsync;
import com.speno.xedm.gwt.service.MessageService;
import com.speno.xedm.gwt.service.MessageServiceAsync;
import com.speno.xedm.util.GeneralException;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

public class HomePanel extends HLayout implements PagingObserver{
	private static HashMap<String, HomePanel> instanceMap = new HashMap<String, HomePanel>();
	private static HashMap<Integer, String> instancePortletSelectMap;
	private	VLayout vlaylist = new VLayout(); 
	private static int porletCnt = 0;
	private String [] arrUserData = null;
	
	protected MessageServiceAsync messageService = (MessageServiceAsync) GWT.create(MessageService.class);
	
	//메세지 그리드
	public ListGrid grid;
	//페이징
	private PagingToolStrip gridPager;	
		
	public static HomePanel get(final String id, final String subTitle) {
		if (instanceMap.get(id) == null) {
			instanceMap.put(id, new HomePanel(id, subTitle));
		}
		return instanceMap.get(id);
	}
	
	public HomePanel(final String id, final String subTitle) {
		vlaylist.setWidth100();
		vlaylist.setHeight100();
		vlaylist.setShowEdges(false);
		
		
        // add portal!
        final PortalLayout portalLayout = new PortalLayout(2);  //PortalLayout 1로고정
        portalLayout.setWidth100();  
        portalLayout.setHeight100();  
  
        // create portlets...  
        porletCnt = 0;
        instancePortletSelectMap = new HashMap<Integer, String>();
        String userData = Session.get().getUser().getUserData();
        if(null == userData){
        	// 20140505, junsoo, 초기 데쉬보드 윈도우 기본 설정이 가능하도록 함.
    		userData = Session.get().getInfo().getConfig("gui.dashboard.windows");
        }
        
        if (userData == null || userData.length() < 1) {

    		for (int i = 1; i <= 3; i++) {
	            Portlet portlet = new Portlet("");  
	            portalLayout.addPortlet(portlet);  
	        }
        }else{
        	arrUserData = userData.split(",");
        	if (arrUserData != null)
        	for (int i = 0; i < arrUserData.length; i++) {
	            Portlet portlet = new Portlet(arrUserData[i]);  
	            portalLayout.addPortlet(portlet);  
	        }
        }
		
		// add button
        final DynamicForm form = new DynamicForm();  
        form.setAutoWidth();  
        form.setNumCols(3);  
  
        final SelectItem selectItem = new SelectItem();  
        selectItem.setValueMap("1","2","3");  
        selectItem.setTitle(I18N.message("Columns"));
        selectItem.setShowTitle(true);  
        selectItem.setWrapTitle(false);
  
        selectItem.setDefaultValue("2");  
        selectItem.addChangeHandler(new ChangeHandler() {  
            public void onChange(ChangeEvent event) {
            	int size = Integer.parseInt((String)event.getValue());
            	
                Canvas[] canvases = portalLayout.getMembers();  
                int numMembers = canvases.length;  
                
                
                if (size > numMembers) {
                	// add
                	for (int i = 0; i < size-numMembers; i++) {
                		portalLayout.addMember(new PortalColumn());
    				}
                }
                else { 
                	// remove
                	for (int i = 0; i < numMembers-size; i++) {
                        Canvas lastMember = canvases[numMembers - 1];  
                        portalLayout.removeMember(lastMember);  
                	}
                }  
            }  
        });  
        
        final ButtonItem addPortlet = new ButtonItem("addwindow", I18N.message("AddWindow"));  
        addPortlet.setIcon(ItemFactory.newImgIcon("add.png").getSrc());  
        addPortlet.setAutoFit(true);  
  
        addPortlet.setStartRow(false);  
        addPortlet.setEndRow(false);  
        addPortlet.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {  
            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {  
  
                final Portlet newPortlet = new Portlet("");  
  
                newPortlet.setVisible(false);  
                PortalColumn column = portalLayout.addPortlet(newPortlet);  
  
                // also insert a blank spacer element, which will trigger the built-in  
                //  animateMembers layout animation  
                final LayoutSpacer placeHolder = new LayoutSpacer();  
                placeHolder.setRect(newPortlet.getRect());  
                column.addMember(placeHolder, 0); // add to top  
  
                // create an outline around the clicked button  
                final Canvas outline = new Canvas();  
                outline.setLeft(form.getAbsoluteLeft() + addPortlet.getLeft());  
                outline.setTop(form.getAbsoluteTop());  
                outline.setWidth(addPortlet.getWidth());  
                outline.setHeight(addPortlet.getHeight());  
                outline.setBorder("2px solid #8289A6");  
                outline.draw();  
                outline.bringToFront();  
  
                outline.animateRect(newPortlet.getPageLeft(), newPortlet.getPageTop(),  
                        newPortlet.getVisibleWidth(), newPortlet.getViewportHeight(),  
                        new AnimationCallback() {  
                            public void execute(boolean earlyFinish) {  
                                // callback at end of animation - destroy placeholder and outline; show the new portlet  
                                placeHolder.destroy();  
                                outline.destroy();  
                                newPortlet.show();  
                            }  
                        }, 750);  
            }  
        });  
  
        
        form.setItems(addPortlet);  //selectItem 삭제
  
        vlaylist.addMember(form);

        vlaylist.addMember(portalLayout);
        
		addMember(vlaylist);
		
		try{
			refreshDash();
		}catch(Exception ex){
			SC.warn(ex.getMessage());
		}
	}
	
	// 버튼 셋팅
	protected void setButton(Button btn, int width, int height){
		if(width == 0){ btn.setWidth(50); }
		else btn.setWidth(width);
		btn.setShowRollOver(true);   
		btn.setShowDisabled(true);   
		btn.setShowDown(true);
	}
	
	public void refreshDash() {
		clearPane();
	}
	
	// 초기화
	private void clearPane(){
		if(grid != null && vlaylist.hasMember(grid)){vlaylist.removeChild(grid); grid = null;}
        if(gridPager != null && vlaylist.hasMember(gridPager)){vlaylist.removeChild(gridPager); gridPager = null;}
        
	}

	public void refreshMessage(){
		clearPane();
		
		// 그리드및 페이징 생성
		grid = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("subject")) {
					if ("false".equals(record.getAttributeAsString("read"))) {
						return "font-weight:bold;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};

		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, 10, true, HomePanel.this);
		//totalLength 사용치 않을경우
        //gridPager = new PagingToolStrip(grid, 20, false, this); 
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        gridPager.setMaxPageSize(200);
        
        grid.setHeight100();
        grid.setShowAllRecords(true);  
        grid.setAutoFitFieldWidths(true);
        
        // 문서아이디
		ListGridField id = new ListGridField("id");
		id.setHidden(true);        
        
		// 타이틀
		ListGridField titlenm = new ListGridField("titlenm", I18N.message("title"));
		setField(titlenm, Alignment.LEFT, ListGridFieldType.TEXT, true, I18N.message("title"));
		
		// 최종수정일자
		ListGridField modified = new ListGridField("modified", I18N.message("modifieddate"));
		setField(modified, Alignment.CENTER, ListGridFieldType.DATE, false, I18N.message("modifieddate"));
		modified.setCellFormatter(new DateCellFormatter(false));		
		
		// message
		ListGridField message = new ListGridField("message", I18N.message("message"));
		setField(message, Alignment.LEFT, ListGridFieldType.TEXT, false, I18N.message("message"));	
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		
		List<ListGridField> fields = new ArrayList<ListGridField>();

		fields.add(id);
		fields.add(titlenm);
		fields.add(modified);
		fields.add(message);
			
		grid.setFields(fields.toArray(new ListGridField[0]));
		
        vlaylist.addMember(grid);
        vlaylist.addMember(gridPager);
        
        executeFetch(1, 10);
	}

	// 필드설정
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter, String title){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
		glidField.setTitle(title);
	}
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{		//수정
		final PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		try {
			messageService.pagingMessages(Session.get().getSid(), config, new AsyncCallback<PagingResult<SMessage>>() {
				@Override
				public void onSuccess(PagingResult<SMessage> result) {
					int totalLength = result.getTotalLength();
					List<SMessage> data = result.getData();
					ListGridRecord records[] = new ListGridRecord[data.size()];
					
					// 그리드 초기화
					grid.setData(new ListGridRecord[0]);
					
					for (int i = 0; i < data.size(); i++) {
						SMessage message = data.get(i);
						ListGridRecord record = new ListGridRecord();
						
						// 문서 아이디
						record.setAttribute("id", message.getId());
						// 문서 타이틀
						record.setAttribute("titlenm", message.getSubject());
						// 최종수정일자
						record.setAttribute("modified", message.getLastNotified());
						// 딸린파일정보
						record.setAttribute("message", message.getMessage());
						
						records[i]=record;
					}
					
					// 그리드에 데이터 셋팅
					grid.setData(records);
					gridPager.setRespPageInfo(totalLength, pageNum);
					//totalLength 사용치 않을경우
					//gridPager.setRespPageInfo((data.size() > 0), pageNum); 
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
			});
		} catch (GeneralException e) {
//			e.printStackTrace();
		}
		
	}
	
	/**
	 * 저장
	 */
	private void executeUpdateUserData() {
		Log.debug("[ HomePanel executeUpdateUserData ]");
		
		Set st = instancePortletSelectMap.keySet();
    	Iterator it = st.iterator();
    	String userData = "";
    	while(it.hasNext()){
    		userData += ","+instancePortletSelectMap.get(it.next());
    	}
    	userData = "".equals(userData)?"":userData.substring(1);
		
    	ServiceUtil.security().saveUserData(Session.get().getSid(), userData, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}

			@Override
			public void onSuccess(Void result) {
				Log.debug("[ HomePanel executeUpdateUserData ] onSuccess. ");
			}
		});
	}	

    /** 
     * Portlet class definition 
     */  
    private class Portlet extends Window {  
    	private DataSource dataSource;
    	private ListGrid list;
    	    	
        public Portlet(String SelectItem) {
  			porletCnt++;
  			
            setShowShadow(false);
            
            setHeight(250);
            // enable predefined component animation  
            setAnimateMinimize(true);  
  
            // Window is draggable with "outline" appearance by default.  
            // "target" is the solid appearance.  
            setDragAppearance(DragAppearance.OUTLINE);  
            setCanDrop(true);  
            
            DynamicForm systemSelector = new DynamicForm();  
            systemSelector.setWidth(75);  
            systemSelector.setNumCols(1);  
            systemSelector.setLayoutAlign(Alignment.CENTER);  
            
            final HiddenItem idItem = new HiddenItem("id");
            idItem.setValue(porletCnt);
            
            final SelectItem selectItem = new SelectItem();  
            selectItem.setHeight(19);  
            selectItem.setName("selectFont");  
            selectItem.setWidth(120); 
            selectItem.addChangedHandler(new ChangedHandler() {
				@Override
				public void onChanged(ChangedEvent event) {
					instancePortletSelectMap.remove((Integer)idItem.getValue());
					instancePortletSelectMap.put((Integer) idItem.getValue(), selectItem.getValueAsString());  
					executeUpdateUserData();
				}
            });
            selectItem.setShowTitle(false);  
            
            
            
    		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
    		valueMap.put(Constants.PORTAL_NOTICE, I18N.message(Constants.PORTAL_NOTICE));
    		valueMap.put(Constants.PORTAL_MESSAGES, I18N.message(Constants.PORTAL_MESSAGES));
    		valueMap.put(Constants.PORTAL_CHECKOUT, I18N.message(Constants.PORTAL_CHECKOUT));
    		valueMap.put(Constants.PORTAL_LOCKED, I18N.message(Constants.PORTAL_LOCKED));
    		valueMap.put(Constants.PORTAL_RECENT, I18N.message(Constants.PORTAL_RECENT));

            selectItem.setValueMap(valueMap);
            
            // set random !
            Set<String> keyset = valueMap.keySet();
            String[] keys = keyset.toArray(new String[0]);
            String defaultValue = keys[Random.nextInt(keys.length-1)];
            if("".equals(SelectItem)){
            	setList(defaultValue);
            }else{
            	setList(SelectItem);
            }
            
            selectItem.setDefaultValue("".equals(SelectItem)?defaultValue:SelectItem);  
            selectItem.addChangedHandler(new ChangedHandler() {  
                public void onChanged(ChangedEvent event) {  
                	setList((String)selectItem.getValue());
                }  
            });  
            systemSelector.setItems(selectItem, idItem);  
  
            // customize the appearance and order of the controls in the window header  
            setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL, systemSelector, HeaderControls.CLOSE_BUTTON);  
  
            // show either a shadow, or translucency, when dragging a portlet  
            // (could do both at the same time, but these are not visually compatible effects)  
            // setShowDragShadow(true);  
            setDragOpacity(30);  
  
            // these settings enable the portlet to autosize its height only to fit its contents  
            // (since width is determined from the containing layout, not the portlet contents)  
            setVPolicy(LayoutPolicy.FILL);  
            setOverflow(Overflow.VISIBLE);
            
			addCloseClickHandler(new CloseClickHandler(){
				@Override
				public void onCloseClick(CloseClickEvent event) {
					instancePortletSelectMap.remove(idItem.getValue());
					executeUpdateUserData();
					destroy();
				}
            });
			instancePortletSelectMap.put(porletCnt, selectItem.getValueAsString());  
        }  
        
        public void setList(String type){
        	if (list != null) removeItem(list);

       		list = new ListGrid();
        	
    		setTitle(I18N.message(type));
    		
    		list.setEmptyMessage(I18N.message("notitemstoshow"));
    		list.setCanFreezeFields(true);
    		list.setAutoFetchData(true);
    		list.setShowHeader(true);
    		list.setCanSelectAll(false);
    		list.setSelectionType(SelectionStyle.SINGLE);
    		list.setHeight100();
    		list.setBorder("0px");
//    		if (color != null)
//    			list.setBodyBackgroundColor(color);

    		// set data source
    		if (Constants.PORTAL_NOTICE.equals(type))
    			dataSource = new DashboardNoticeDS(list);
    		else if (Constants.PORTAL_MESSAGES.equals(type))
    			dataSource = new DashboardMessageDS(list);
    		else if (Constants.PORTAL_CHECKOUT.equals(type))
    			dataSource = new DashboardCheckedDS(list, SDocument.DOC_CHECKED_OUT);
    		else if (Constants.PORTAL_LOCKED.equals(type))
    			dataSource = new DashboardLockedDS(list, SDocument.DOC_LOCKED);
    		else if (Constants.PORTAL_RECENT.equals(type))
    			dataSource = new DashboardRecentDS(list);
    		
    		if (dataSource == null){
    			list = null;
    			return;
    		}
    		
    		list.setDataSource(dataSource);
    		
//    		ListGridField docid = new ListGridField("id");
//    		docid.setHidden(true);        
//    		
//    		ListGridField subject = new ListGridField("subject");
//    		subject.setHidden(true);        
//    		
//    		ListGridField sent = new ListGridField("sent", I18N.message("sent"), 150);
//    		sent.setAlign(Alignment.CENTER);
//    		sent.setType(ListGridFieldType.DATE);
//    		sent.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
//    		sent.setCanFilter(false);
//    		
//    		ListGridField text = new ListGridField("text", I18N.message("content"));
//    		text.setAlign(Alignment.CENTER);
//    		text.setType(ListGridFieldType.TEXT);
//    		text.setCanFilter(false);
//    		
//    		List<ListGridField> fields = new ArrayList<ListGridField>();
//    		fields.add(docid);
//    		fields.add(subject);
//    		fields.add(sent);
//    		fields.add(text);
//    		
//    		list.setFields(fields.toArray(new ListGridField[0]));
    		
    		// Count the total of events and the total of unchecked events
    		list.addDataArrivedHandler(new DataArrivedHandler() {
    			@Override
    			public void onDataArrived(DataArrivedEvent event) {
    			}
    		});
    		
    		addItem(list);
        }
    }  
  
    /** 
     * PortalColumn class definition 
     */  
    private class PortalColumn extends VStack {  
  
        public PortalColumn() {  
  
            // leave some space between portlets  
            setMembersMargin(6);  
  
            // enable predefined component animation  
            setAnimateMembers(true);  
            setAnimateMemberTime(300);  
  
            // enable drop handling  
            setCanAcceptDrop(true);  
  
            // change appearance of drag placeholder and drop indicator  
            setDropLineThickness(4);  
  
            Canvas dropLineProperties = new Canvas();  
            dropLineProperties.setBackgroundColor("aqua");  
            setDropLineProperties(dropLineProperties);  
  
            setShowDragPlaceHolder(true);  
  
            Canvas placeHolderProperties = new Canvas();  
            placeHolderProperties.setBorder("2px solid #8289A6");  
            setPlaceHolderProperties(placeHolderProperties);  
        }  
    }  
  
    /** 
     * PortalLayout class definition 
     */  
    private class PortalLayout extends HLayout {  
        public PortalLayout(int numColumns) {  
            setMembersMargin(6);  
            for (int i = 0; i < numColumns; i++) {  
                addMember(new PortalColumn());  
            }  
        }  
        public PortalColumn addPortlet(Portlet portlet) {  
            // find the column with the fewest portlets  
            int fewestPortlets = Integer.MAX_VALUE;  
            PortalColumn fewestPortletsColumn = null;  
            for (int i = 0; i < getMembers().length; i++) {  
                int numPortlets = ((PortalColumn) getMember(i)).getMembers().length;  
                if (numPortlets < fewestPortlets) {  
                    fewestPortlets = numPortlets;  
                    fewestPortletsColumn = (PortalColumn) getMember(i);  
                }  
            }  
            fewestPortletsColumn.addMember(portlet);  
            return fewestPortletsColumn;  
        }  
    } 
    
    private class DashboardNoticeDS extends DataSource {

    	public DashboardNoticeDS(ListGrid list) {
    		// set fields
    		ListGridField docid = new ListGridField("id");
    		docid.setHidden(true); 
    		
    		 	
    		ListGridField fsubject = new ListGridField("subject",I18N.message("subject"));
    		fsubject.setHidden(false);        
    		
    		ListGridField ftext = new ListGridField("text", I18N.message("content"));
    		ftext.setAlign(Alignment.LEFT);
    		ftext.setType(ListGridFieldType.TEXT);
    		ftext.setCanFilter(false);
    		
    		ListGridField fsent = new ListGridField("sent", I18N.message("sent"), 150);
    		fsent.setAlign(Alignment.LEFT);
    		fsent.setType(ListGridFieldType.DATE);
    		fsent.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		fsent.setCanFilter(false);
    		
    		
    		
    		List<ListGridField> ffields = new ArrayList<ListGridField>();
    		ffields.add(docid);
    		ffields.add(fsubject);
    		ffields.add(ftext);
    		ffields.add(fsent);
    		
    		
    		
    		list.setFields(ffields.toArray(new ListGridField[0]));
    		
    		setTitleField("subject");
    		
    		
    		setRecordXPath("/list/message");
    		DataSourceTextField id = new DataSourceTextField("id");
    		DataSourceTextField subject = new DataSourceTextField("subject");
    		DataSourceDateTimeField sent = new DataSourceDateTimeField("sent");
    		DataSourceTextField text = new DataSourceTextField("text");
    		
    		id.setPrimaryKey(true);
    		id.setHidden(true);
    		id.setRequired(true);
    		
    		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    		fields.add(id);
    		fields.add(subject);
    		fields.add(sent);
    		fields.add(text);
    		setFields(fields.toArray(new DataSourceField[0]));
    		
//    		System.out.println("data/messages.xml?sid=" + Session.get().getSid() + "&recipient=SYSTEM");
    		setDataURL("data/messages.xml?sid=" + Session.get().getSid() + "&recipient=SYSTEM");
    		setClientOnly(true);
    		
    		

//    		 13-11-26 나용준 수정
//    		 대쉬보드 사용자 홈에서 공지사항을 더블 클릭하면 메시지로 이동
    		list.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
    			@Override
    			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
    				DashboardPanel.get().menu.setContent("dash_messages");
    			}   
            });
    	}
    }
    
    private class DashboardMessageDS extends DataSource {

    	public DashboardMessageDS(ListGrid list) {
    		ListGridField docid = new ListGridField("id");
    		docid.setHidden(true);       
    		
//    		ListGridField subjectField = new ListGridField("subject", I18N.message("title"));
//    		subjectField.setCanFilter(true);
//    		subjectField.setAlign(Alignment.LEFT);
//    		subjectField.setType(ListGridFieldType.TEXT);
//    		subjectField.setCanFilter(false);
    		ListGridField subjectField = new ListGridField("subject",I18N.message("subject"));
    		subjectField.setHidden(false);       
    		
    		ListGridField ftext = new ListGridField("text", I18N.message("content"));
    		ftext.setAlign(Alignment.LEFT);
    		ftext.setType(ListGridFieldType.TEXT);
    		ftext.setCanFilter(false);
    		
//    		ListGridField ffrom = new ListGridField("from", I18N.message("from"), 60);
//    		ffrom.setAlign(Alignment.CENTER);
//    		ffrom.setType(ListGridFieldType.TEXT);
//    		ffrom.setCanFilter(false);
//    		
//    		ListGridField attach = new ListGridField("attach", I18N.message("attachfile"), 10);
//    		attach.setAlign(Alignment.CENTER);
//    		attach.setType(ListGridFieldType.TEXT);
//    		attach.setCanFilter(false);
    		
    		
    		ListGridField fsent = new ListGridField("sent", I18N.message("sent"), 150);
    		fsent.setAlign(Alignment.LEFT);
    		fsent.setType(ListGridFieldType.DATE);
    		fsent.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		fsent.setCanFilter(false);    		
//    		ListGridField ftext = new ListGridField("text", I18N.message("content"));
//    		ftext.setAlign(Alignment.CENTER);
//    		ftext.setType(ListGridFieldType.TEXT);
//    		ftext.setCanFilter(false);

    		list.setFields(docid, subjectField, ftext, fsent );

    		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
				@Override
				public void onCellDoubleClick(CellDoubleClickEvent event) {
					ListGridRecord rc = event.getRecord();
					MessagePopup messagePopup = new MessagePopup(Session.get().getSid(), Constants.MESSAGE_VIEW, Long.parseLong(rc.getAttributeAsString("id")));
					messagePopup.show();
				}
			});
    		
    		setTitleField("subject");
    		
    		setRecordXPath("/list/message");
    		DataSourceTextField id = new DataSourceTextField("id");
    		DataSourceTextField subject = new DataSourceTextField("subject");
    		DataSourceDateTimeField sent = new DataSourceDateTimeField("sent");
    		DataSourceTextField from = new DataSourceTextField("from");
    		DataSourceTextField text = new DataSourceTextField("text");
    		DataSourceTextField read = new DataSourceTextField("read");
    		DataSourceTextField attachs = new DataSourceTextField("attach");
    		
    		id.setPrimaryKey(true);
    		id.setHidden(true);
    		id.setRequired(true);
    		
    		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    		fields.add(id);
    		fields.add(subject);
    		fields.add(sent);
    		fields.add(from);
    		fields.add(attachs);
    		fields.add(text);
    		fields.add(read);
    		setFields(fields.toArray(new DataSourceField[0]));
    		
    		setDataURL("data/messages.xml?sid=" + Session.get().getSid());
    		setClientOnly(true);
    	}
    }
    
    private class DashboardLockedDS extends DataSource {
    	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

    	public DashboardLockedDS(ListGrid list, int status) {

    		ListGridField fid = new ListGridField("id");
    		fid.setHidden(true);        
            
    		ListGridField ffolderId = new ListGridField("folderId");
    		ffolderId.setHidden(true);        
            
    		ListGridField ftitle = new ListGridField("title", I18N.message("title"));
    		ftitle.setAlign(Alignment.LEFT);
    		ftitle.setType(ListGridFieldType.TEXT);
    		ftitle.setCanFilter(false);
    		
    		ListGridField fattachs = new ListGridField("attachs", I18N.message("attachs"), 45);
    		fattachs.setAlign(Alignment.CENTER);
    		fattachs.setType(ListGridFieldType.TEXT);
    		fattachs.setCanFilter(false);
    		
    		ListGridField fversion = new ListGridField("version", I18N.message("version"), 50);
    		fversion.setAlign(Alignment.CENTER);
    		fversion.setType(ListGridFieldType.TEXT);
    		fversion.setCanFilter(false);
    		
    		ListGridField flast = new ListGridField("lastModified", I18N.message("lastModified"), 130);
    		flast.setAlign(Alignment.CENTER);
    		flast.setType(ListGridFieldType.DATE);
    		flast.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		flast.setCanFilter(false);
    		
    		List<ListGridField> ffields = new ArrayList<ListGridField>();
    		ffields.add(fid);
    		ffields.add(ffolderId);
    		ffields.add(ftitle);
    		ffields.add(fattachs);
    		ffields.add(fversion);
    		ffields.add(flast);
    		
    		list.setFields(ffields.toArray(new ListGridField[0]));
    		
    		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
    			@Override
    			public void onCellDoubleClick(CellDoubleClickEvent event) {
    			 	final ListGridRecord rc = event.getRecord();
//    				GWT.log("expand folder id : " + rc.getAttribute("folderId"));
    				service.getFolder(Session.get().getSid(), Long.parseLong( rc.getAttribute("folderId") ), true, false, new AsyncCallback<SFolder>() {
    					@Override
    					public void onFailure(Throwable caught) {
    						SCM.warn(caught);
    					}

    					@Override
    					public void onSuccess(SFolder result) {
    						MainPanel.get().tabSet.selectTab(Constants.MENU_DOCUMENTS);
    						DocumentsPanel.get().expandDocid = Long.parseLong(rc.getAttribute("id"));
    						DocumentsPanel.get().getMenu().expandFolder(result);
    					}
    					
    				});
    				
    			}
    		});
    		
    		setTitleField("title");
    		
    		setRecordXPath("/list/document");
    		DataSourceTextField id = new DataSourceTextField("id");
    		DataSourceTextField folderId = new DataSourceTextField("folderId");
    		DataSourceTextField title = new DataSourceTextField("title");
    		DataSourceTextField attachs = new DataSourceTextField("attachs");
    		DataSourceTextField version = new DataSourceTextField("version");
    		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
    		
    		id.setPrimaryKey(true);
    		id.setHidden(true);
    		id.setRequired(true);
    		
    		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    		fields.add(id);
    		fields.add(folderId);
    		fields.add(title);
    		fields.add(attachs);
    		fields.add(version);
    		fields.add(lastModified);
    		
    		setFields(fields.toArray(new DataSourceField[0]));
    		System.out.println("data/documents.xml?sid=" + Session.get().getSid() + "&status=" + status + "&max=10");
    		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&status=" + status + "&max=10");
    		setClientOnly(true);
    	}

    }
    
    private class DashboardCheckedDS extends DataSource {
    	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

    	public DashboardCheckedDS(ListGrid list, int status) {
    		
    		ListGridField fid = new ListGridField("id");
    		fid.setHidden(true);        
            
    		ListGridField ffolderId = new ListGridField("folderId");
    		ffolderId.setHidden(true);        
            
    		ListGridField ftitle = new ListGridField("title", I18N.message("title"));
    		ftitle.setAlign(Alignment.LEFT);
    		ftitle.setType(ListGridFieldType.TEXT);
    		ftitle.setCanFilter(false);
    		
    		ListGridField fattachs = new ListGridField("attachs", I18N.message("attachs"), 45);
    		fattachs.setAlign(Alignment.CENTER);
    		fattachs.setType(ListGridFieldType.TEXT);
    		fattachs.setCanFilter(false);
    		
    		ListGridField fversion = new ListGridField("version", I18N.message("version"), 50);
    		fversion.setAlign(Alignment.CENTER);
    		fversion.setType(ListGridFieldType.TEXT);
    		fversion.setCanFilter(false);
    		
    		ListGridField flast = new ListGridField("lastModified", I18N.message("lastModified"), 130);
    		flast.setAlign(Alignment.CENTER);
    		flast.setType(ListGridFieldType.DATE);
    		flast.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		flast.setCanFilter(false);
    		
    		List<ListGridField> ffields = new ArrayList<ListGridField>();
    		ffields.add(fid);
    		ffields.add(ffolderId);
    		ffields.add(ftitle);
    		ffields.add(fattachs);
    		ffields.add(fversion);
    		ffields.add(flast);
    		
    		list.setFields(ffields.toArray(new ListGridField[0]));
    		
    		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
    			@Override
    			public void onCellDoubleClick(CellDoubleClickEvent event) {
    			 	final ListGridRecord rc = event.getRecord();
//    				GWT.log("expand folder id : " + rc.getAttribute("folderId"));
    				service.getFolder(Session.get().getSid(), Long.parseLong( rc.getAttribute("folderId") ), true, false, new AsyncCallback<SFolder>() {
    					@Override
    					public void onFailure(Throwable caught) {
    						SCM.warn(caught);
    					}

    					@Override
    					public void onSuccess(SFolder result) {
    						MainPanel.get().tabSet.selectTab(Constants.MENU_DOCUMENTS);
    						
//    						System.out.println("expanddocid : " + DocumentsPanel.get().expandDocid);
//    						System.out.println("getId : " + rc.getAttribute("id"));
    						
    						DocumentsPanel.get().expandDocid = Long.parseLong(rc.getAttribute("id"));
    						DocumentsPanel.get().getMenu().expandFolder(result);
    					}
    					
    				});
    			}
    		});
    		
    		setTitleField("title");
    		
    		setRecordXPath("/list/document");
    		DataSourceTextField id = new DataSourceTextField("id");
    		DataSourceTextField folderId = new DataSourceTextField("folderId");
    		DataSourceTextField title = new DataSourceTextField("title");
    		DataSourceTextField attachs = new DataSourceTextField("attachs");
    		DataSourceTextField version = new DataSourceTextField("version");
    		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
    		
    		id.setPrimaryKey(true);
    		id.setHidden(true);
    		id.setRequired(true);
    		
    		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    		fields.add(id);
    		fields.add(folderId);
    		fields.add(title);
    		fields.add(attachs);
    		fields.add(version);
    		fields.add(lastModified);
    		
    		setFields(fields.toArray(new DataSourceField[0]));

    		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&status=" + status + "&max=10");
    		setClientOnly(true);
    	}

    }
    
    private class DashboardRecentDS extends DataSource {
    	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

    	public DashboardRecentDS(ListGrid list) {

    		ListGridField fid = new ListGridField("id");
    		fid.setHidden(true);        
            
    		ListGridField ffolderId = new ListGridField("folderId");
    		ffolderId.setHidden(true);        
            
    		ListGridField ftitle = new ListGridField("title", I18N.message("title"));
    		ftitle.setAlign(Alignment.LEFT);
    		ftitle.setType(ListGridFieldType.TEXT);
    		ftitle.setCanFilter(false);
    		
    		ListGridField fattachs = new ListGridField("attachs", I18N.message("attachs"), 45);
    		fattachs.setAlign(Alignment.CENTER);
    		fattachs.setType(ListGridFieldType.TEXT);
    		fattachs.setCanFilter(false);
    		
    		ListGridField fversion = new ListGridField("version", I18N.message("version"), 50);
    		fversion.setAlign(Alignment.CENTER);
    		fversion.setType(ListGridFieldType.TEXT);
    		fversion.setCanFilter(false);
    		
    		ListGridField flast = new ListGridField("lastModified", I18N.message("lastModified"), 130);
    		flast.setAlign(Alignment.CENTER);
    		flast.setType(ListGridFieldType.DATE);
    		flast.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		flast.setCanFilter(false);
    		
    		List<ListGridField> ffields = new ArrayList<ListGridField>();
    		ffields.add(fid);
    		ffields.add(ffolderId);
    		ffields.add(ftitle);
    		ffields.add(fattachs);
    		ffields.add(fversion);
    		ffields.add(flast);
    		
    		list.setFields(ffields.toArray(new ListGridField[0]));
    		
    		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
    			@Override
    			public void onCellDoubleClick(CellDoubleClickEvent event) {
    			 	final ListGridRecord rc = event.getRecord();
//    				GWT.log("expand folder id : " + rc.getAttribute("folderId"));
    				service.getFolder(Session.get().getSid(), Long.parseLong( rc.getAttribute("folderId") ), true, false, new AsyncCallback<SFolder>() {
    					@Override
    					public void onFailure(Throwable caught) {
    						SCM.warn(caught);
    					}

    					@Override
    					public void onSuccess(SFolder result) {
    						MainPanel.get().tabSet.selectTab(Constants.MENU_DOCUMENTS);
    						DocumentsPanel.get().expandDocid = Long.parseLong(rc.getAttribute("id"));
    						DocumentsPanel.get().getMenu().expandFolder(result);
    					}
    					
    				});
    				
    			}
    		});
    		
    		setTitleField("title");
    		
    		setRecordXPath("/list/document");
    		DataSourceTextField id = new DataSourceTextField("id");
    		DataSourceTextField folderId = new DataSourceTextField("folderId");
    		DataSourceTextField title = new DataSourceTextField("title");
    		DataSourceTextField attachs = new DataSourceTextField("attachs");
    		DataSourceTextField version = new DataSourceTextField("version");
    		DataSourceDateTimeField lastModified = new DataSourceDateTimeField("lastModified");
    		
    		id.setPrimaryKey(true);
    		id.setHidden(true);
    		id.setRequired(true);
    		
    		List<DataSourceField> fields = new ArrayList<DataSourceField>();
    		fields.add(id);
    		fields.add(folderId);
    		fields.add(title);
    		fields.add(attachs);
    		fields.add(version);
    		fields.add(lastModified);
    		
    		setFields(fields.toArray(new DataSourceField[0]));
//    		System.out.println("data/documents.xml?sid=" + Session.get().getSid() + "&status=" + status + "&max=10");
    		setDataURL("data/documents.xml?sid=" + Session.get().getSid() + "&type=recent&max=10");
    		setClientOnly(true);
    	}

    }
}