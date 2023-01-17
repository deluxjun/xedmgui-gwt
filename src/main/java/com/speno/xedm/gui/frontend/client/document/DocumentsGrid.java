package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DataCache;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.util.WindowUtils;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;
 
public class DocumentsGrid extends ListGrid{
	// Context Menu 제어용 List
//	private List<String> contextListData = new ArrayList<String>();
	// 선택된 이미지의 elementId 저장
	private String elementId="";
	// 대표 파일의 elementId 저장
	private List<String> elementIds = new ArrayList<String>();
	
	// 현재 선택한 로우값 반환
	private int selectRowNum = 0;
	
	private boolean recordsExpanded;
	// 현재 선택되어있는 row값
//	private int currentLocation = 0;
	// 검색측 Tab 구분용
	private String searchTabName;
	
	public List<ListGridField> fields = new ArrayList<ListGridField>();
	public List<ListGridField> attrFields = new ArrayList<ListGridField>();
	
	// 20140221, junsoo, 화면에 따라 적절한 메뉴가 표시되도록 하기 위해 추가.
	private int currentMenuType;
	
	// 이미지 element 초기화
	public void resetData(){
		elementIds.clear();
	}
	
	public int getCurrentMenuType() {
		return currentMenuType;
	}

	public void setCurrentMenuType(int currentMenuType) {
		this.currentMenuType = currentMenuType;
	}

	public boolean isRecordsExpanded() {
		return recordsExpanded;
	}

	// 20130816, junsoo, expand or collapse records
	public void setRecordsExpanded(boolean recordsExpanded) {
    	for (ListGridRecord record : this.getRecords()) {
			if (recordsExpanded) {
				if (canExpandRecord(record, -1))
					expandRecord(record);
			}
			else {
				if (canExpandRecord(record, -1))
					collapseRecord(record);
			}
    	}
		this.recordsExpanded = recordsExpanded;
	}

	public void resetGridData(){
		resetData();
		setRecords(new ListGridRecord[]{});
	}
	
	private void showContextMenu(int x, int y, Menu menu){
		menu.setTop(y);
		menu.setLeft(x);
		
		menu.show();
	}
	
	// ==================================================================
	// expansion component 의 버튼 동작 모듈
	// ==================================================================
	//여기다!
	private com.smartgwt.client.widgets.events.ClickHandler contentClickHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
		@Override
		public void onClick(final com.smartgwt.client.widgets.events.ClickEvent event) {
			//20140114na search는 오른쪽클릭 비활성화
			if(isSearchTab == true) return;
			bContentButtonPressed = true;
			MyLabel button = (MyLabel)event.getSource();
			
			// 20130807, junsoo, 확장자에 따른 가능한 액션을 서버로 부터 획득
//			Menu menu = setupContextMenu(doc.getId(), content.getElementId());
			// 20140121, junsoo, content 전달
			ItemFactory.setFileMenuByExt(button.getDocument(), button.getContent(), Util.getExtByFileName(button.getContent().getFileName()), new ReturnHandler() {
				@Override
				public void onReturn(Object param) {
					showContextMenu(event.getX(), event.getY(), (Menu)param);
				}
			});
//			showContextMenu(event.getX(), event.getY(), menu);
		}
	};

	private ShowContextMenuHandler contentShowContextMenuHandler = new ShowContextMenuHandler() {
		@Override
		public void onShowContextMenu(final ShowContextMenuEvent event) {
			bContentButtonPressed = true;
			MyLabel button = (MyLabel)event.getSource();
//			Menu menu = setupContextMenu(doc.getId(), content.getElementId());
			// 20140121, junsoo, content 전달
			ItemFactory.setFileMenuByExt(button.getDocument(), button.getContent(), Util.getExtByFileName(button.getContent().getFileName()), new ReturnHandler() {
				@Override
				public void onReturn(Object param) {
					showContextMenu(event.getX(), event.getY(), (Menu)param);
//					btn.setContextMenu((Menu)param);
				}
			});
			setContextMenu(null);
			event.cancel();
		}
	};
	private com.smartgwt.client.widgets.events.ClickHandler contentThumbnailClickHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
		@Override
		public void onClick(final com.smartgwt.client.widgets.events.ClickEvent event) {
			bContentButtonPressed = true;
			MyImg img = (MyImg)event.getSource();
			
			// 20130807, junsoo, 확장자에 따른 가능한 액션을 서버로 부터 획득
//			Menu menu = setupContextMenu(doc.getId(), content.getElementId());
			// 20140121, junsoo, content 전달
			ItemFactory.setFileMenuByExt(img.getDocument(), img.getContent(), Util.getExtByFileName(img.getContent().getFileName()), new ReturnHandler() {
				@Override
				public void onReturn(Object param) {
					showContextMenu(event.getX(), event.getY(), (Menu)param);
				}
			});
//			showContextMenu(event.getX(), event.getY(), menu);
		}
	};
	private ShowContextMenuHandler contentThumbnailShowContextMenuHandler = new ShowContextMenuHandler() {
		@Override
		public void onShowContextMenu(final ShowContextMenuEvent event) {
			bContentButtonPressed = true;
			MyImg img = (MyImg)event.getSource();
			// 20140121, junsoo, content 전달
			ItemFactory.setFileMenuByExt(img.getDocument(), img.getContent(), Util.getExtByFileName(img.getContent().getFileName()), new ReturnHandler() {
				@Override
				public void onReturn(Object param) {
					showContextMenu(event.getX(), event.getY(), (Menu)param);
				}
			});
			setContextMenu(null);
			event.cancel();
		}
	};
	
	private String preparedFileName(SContent content) {
		String fileName = content.getFileName();
    	String str = Util.strCut(fileName, 50, "...") + " (" + Util.setFileSize(content.getFileSize(), true) + ")";

		// 20131206, junsoo, content의 process 상태에 따라 비활성화 시킴
		if (content.getProcessed() == SContent.PROCESS_PROCESSED) {
		} else {
			if (content.getProcessed() == SContent.PROCESS_TO_PROCESS)
				str = "(" + I18N.message("PostJobProcessing") + ") " + str;
			else if (content.getProcessed() == SContent.PROCESS_ERROR)
				str = "(" + I18N.message("PostJobError") + ") " + str;
		}
		
		return str;
	}
	
	public class MyLabel extends Label {
		private SDocument document;
		private SContent content;
		public MyLabel(SDocument document, SContent content) {
			this.document = document;
			this.content = content;

			String fileName = content.getFileName();
        	String str = Util.strCut(fileName, 50, "...") + " (" + Util.setFileSize(content.getFileSize(), true) + ")";

			// 20131206, junsoo, content의 process 상태에 따라 비활성화 시킴
			if (content.getProcessed() == SContent.PROCESS_PROCESSED) {
				
				//addClickHandler(contentClickHandler);
				addShowContextMenuHandler(contentShowContextMenuHandler);
				setStyleName("contentHighlight");
			} else {
				if (content.getProcessed() == SContent.PROCESS_TO_PROCESS)
					str = "(" + I18N.message("PostJobProcessing") + ") " + str;
				else if (content.getProcessed() == SContent.PROCESS_ERROR)
					str = "(" + I18N.message("PostJobError") + ") " + str;
				
				setStyleName("contentNormal");
			}
			
			setHeight(18);
//			setWidth100();
			setAutoFit(true);
			setAlign(Alignment.LEFT);
			setValign(VerticalAlignment.CENTER);
			setWrap(false);
			String icon = content.getIcon().split("\\.")[0];
			setIcon(ItemFactory.newImgIcon(icon + ".png").getSrc());
//			setShowEdges(false);
			setPrompt(fileName);
			
			// 20131226, junsoo
//			addMouseOverHandler(this);
//			addMouseOutHandler(this);
			
			setContents(str);
		}

		public SDocument getDocument() {
			return document;
		}
		public void setDocument(SDocument document) {
			this.document = document;
		}
		public SContent getContent() {
			return content;
		}
		
//		@Override
//		public void onMouseOver(MouseOverEvent event) {
//			((Label)event.getSource()).setStyleName("contentMouseOver");
//		}
//		@Override
//		public void onMouseOut(MouseOutEvent event) {
//			((Label)event.getSource()).setStyleName("contentNormal");
//		}
	};
	
	public class MyImg extends Img {
		private SDocument document;
		private SContent content;
		public MyImg(String title) {
			super(title);
		}
		public SDocument getDocument() {
			return document;
		}
		public void setDocument(SDocument document) {
			this.document = document;
		}
		public SContent getContent() {
			return content;
		}
		public void setContent(SContent content) {
			this.content = content;
			
			// 20131206, junsoo, content의 process 상태에 따라 비활성화 시킴
			if (content.getProcessed() == SContent.PROCESS_PROCESSED) {
				addClickHandler(contentThumbnailClickHandler);
				addShowContextMenuHandler(contentThumbnailShowContextMenuHandler);
			} else {
				setDisabled(true);
			}
		}
	};

    /*
     * 그리드 확장 이벤트
     * 각각의 레코드별로 딸린 파일이 있을경우 이벤트 호출됨
     * 그리드 생성시 그리드의 row 개수 만큼 호출됨.
     */
	private boolean bContentButtonPressed = false;
	private static int CONTENTS_COUNT = 5;
	private Map<Long, List<String[]>> thumbnailInfos = new HashMap<Long, List<String[]>>();
	
    @Override  
    protected Canvas getExpansionComponent(final ListGridRecord record) {
//        VLayout vContentLayout = (VLayout)record.getAttributeAsObject("layout");
//        if (vContentLayout != null)
//        	return vContentLayout;
        
    	VLayout vContentLayout = new VLayout();
//        record.setAttribute("layout", vContentLayout);
    	
        final SDocument doc = (SDocument)record.getAttributeAsObject("document");

        if (doc == null)
        	return vContentLayout;
        
		vContentLayout.setPadding(2);
		HLayout hContentLayout = null; 
        int hContentLayoutCount = 0;
        
        for (int i = 0; i < doc.getContents().length; i++) {
        	final SContent content = doc.getContents()[i];
        	String fileName = content.getFileName();
        	String str = Util.strCut(fileName, 50, "...") + " (" + Util.setFileSize(content.getFileSize(), true) + ")";

//        	// 20120807, junsoo, ShowContextMenuHandler 는 비동기에 의한 contextMenu의 핸들링이 너무 느려 mouse down으로 대체.
//        	btn.addRightMouseDownHandler(new RightMouseDownHandler() {
//				
//				@Override
//				public void onRightMouseDown(final RightMouseDownEvent event) {
//    				bContentButtonPressed = true;
////
////					Menu menu = setupContextMenu(doc.getId(), content.getElementId());
//    				ItemFactory.setFileMenuByExt(doc, content.getElementId(), Util.getExtByFileName(content.getFileName()), new ReturnHandler() {
//    					@Override
//    					public void onReturn(Object param) {
//    						showContextMenu(event.getX(), event.getY(), (Menu)param);
////    						btn.setContextMenu((Menu)param);
//    					}
//    				});
//    				setContextMenu(null);
//    				event.cancel();
//				}
//			});
    		
        	// add it.
        	if (hContentLayoutCount < 1) {
        		hContentLayout = new HLayout(5);
        		hContentLayout.setMembersMargin(5);  
        		hContentLayout.setAutoHeight();
        		hContentLayout.setAutoWidth();
        		vContentLayout.addMember(hContentLayout);
        	}
        	
        	// 메뉴의 선택 상태가 Thumnail일 경우 Thumnail을 보여준다.
    		if(DocumentActionUtil.get().isThumbnail()){
    			final VLayout thumbLayout = new VLayout();
    			thumbLayout.setWidth(75);
//    			thumbLayout.setAutoWidth();
    			thumbLayout.setAutoHeight();
    			thumbLayout.setMargin(2);
    			thumbLayout.setPadding(2);
    			thumbLayout.setDefaultLayoutAlign(Alignment.CENTER);
    			thumbLayout.setMembersMargin(5);
    			thumbLayout.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						thumbLayout.setBackgroundColor("#EEF7FF");
						thumbLayout.setBorder("1px solid #5089DE");
					}
				});
    			thumbLayout.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						thumbLayout.setBackgroundColor(null);
						thumbLayout.setBorder("0px");
					}
				});

    			// 썸네일 이미지 생성
    			MyImg img = new MyImg("");
    			// 썸네일 이미지 보여줌.
    			if(thumbnailInfos.get(doc.getId()) != null
    					&& thumbnailInfos.get(doc.getId()).size() > i 
    					&& !thumbnailInfos.get(doc.getId()).get(i)[1].contains("null")){
    				// url 획득
    				String url = thumbnailInfos.get(doc.getId()).get(i)[1];
    				// url 변경
    				url = url.substring(url.indexOf("/view/")).replaceAll("/view/", "");
    				img.setAppImgDir(Util.thumbnailPrefix());
    				// url 적용
    				img.setSrc(url);
    			}
    			// 썸네일 이미지가 없을 경우 기본 이미지(아이콘)를 보여준다.
    			else{
    				img.setAppImgDir(Util.imagePrefix());
    				img.setSrc(content.getIcon());
    			}
    			img.setDocument(doc);
    			img.setContent(content);
    			img.setHeight(60);
    			img.setWidth(60);
    			
    			final Label label = new Label(content.getFileName());
//    			label.setPadding(2);
//    			label.setAutoFit(true);
    			label.setWidth100();
    			label.setHeight(16);
    			thumbLayout.addMembers(img, label);
    			hContentLayout.addMember(thumbLayout);
    		}
    		// thumnail view 상태가 아닐경우 기본 버튼을 보여줌
    		else{
    			MyLabel btn = new MyLabel(doc, content);
    			//20140522 육용수 확장 파일에 대한 런처 기동 flow
    			btn.addDoubleClickHandler(new DoubleClickHandler() {					
					public void onDoubleClick(DoubleClickEvent event) {
						// TODO Auto-generated method stub						
					 System.out.println("content click");
						DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
						MyLabel btn = (MyLabel)event.getSource();
						final SDocument doc = btn.getDocument();	        
						if(doc.isView())
						{
						if (doc.getContents() == null || doc.getContents().length < 1){
			        		SC.warn("error.haveNotContents");
			        		return;
			        	}
			        	
			        	final SContent content =  btn.getContent();
			        	if(doc.getStatus() == SDocument.DOC_LOCKED || content.getProcessed() == SContent.PROCESS_TO_PROCESS){
			        		SC.warn(I18N.message("error.docLockAndProcesss"));
			        		return;		
			        	}
			        	        	
			        	String ext =  Util.getExtByFileName(content.getFileName());
			        	SFileType filetype = (SFileType)DataCache.get(DataCache.FILEMENU.getId() + ext);
			        	documentCodeService.getFileTypeByName(Session.get().getSid(), ext, new AsyncCallback<SFileType>() {
			        		@Override
							public void onSuccess(SFileType result) {	
			        			try{
			        			String[] viewType = result.getViewer().split(",");
			        			List<String> temp = new ArrayList<String>();
			        			for(String s : viewType)
			        			temp.add(s);
			        			if(result.getLinkViewer() != null && result.getLinkViewer().equals("$LauncherScan$")
			        					&& temp.contains(SFileType.VIEWER_SYSTEM))         					
			        			ScanStarter.view(String.valueOf(doc.getId()));
			        			else if ((result.getLinkViewer() == null || result.getLinkViewer().equals("$Launcher$")) 
			        					&& temp.contains(SFileType.VIEWER_SYSTEM))
			        			Util.open(doc.getId(),content.getElementId(),content.getFileName());	
			        			else if (result.getLinkViewer() != null && temp.contains(SFileType.VIEWER_SYSTEM)){
				        			String URL = GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + 
				        					"&docId=" + doc.getId() + "&elementId=" + content.getElementId() + "&viewerType=1&print=false";
				        			
				        			WindowUtils.openPopupUrl(URL,"Viewer","" );
				        			}
			        			}
			        			catch(Exception e){
			        				SC.warn("error.LauncherFail");
			        			}
							}
							@Override
							public void onFailure(Throwable caught) {						
								Log.serverError(caught, false);
							}
						});
			        	event.cancel();
						}
			        	else
			        	SC.warn(I18N.message("error.permissionView"));
					}
				
					
				});
    			
    			btn.setPadding(2);
//    			btn.setAutoFit(true);  
//    			btn.setIcon(ItemFactory.newImgIcon(content.getIcon() ).getSrc());
//    			btn.setPrompt(fileName);
    			
    			btn.setDocument(doc);
//    			btn.setContent(content);
    			hContentLayout.addMember(btn);
    		}
    		
    		hContentLayoutCount ++;
    		if (hContentLayoutCount >= CONTENTS_COUNT) {
    			hContentLayoutCount = 0;
    		}
		}
        
        
        // 이 canvas를 클릭할 경우, grid event가 제대로 동작하지 않아 추가함.
        vContentLayout.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (bContentButtonPressed)
					return;
				
            	if (!isSelected(record))
            		selectRecord(record);
            	else
            		deselectRecord(record);
			}
		});
        
        return vContentLayout;
    }
    
    // 20130801, junsoo, 첨부파일이 없는 경우, 확장 금지
    @Override
    public boolean canExpandRecord(ListGridRecord record, int rowNum) {
        SDocument doc = (SDocument)record.getAttributeAsObject("document");
        
        if (doc == null || doc.getContents() == null || doc.getContents().length < 2)
        	return false;
        
        return true;
    }
	// ==================================================================
    
//    // 20131218, junsoo, 주 content의 버튼처리
//    @Override
//    protected Canvas createRecordComponent(ListGridRecord record, Integer colNum) {
//    	
//        final String fieldName = this.getFieldName (colNum);
//        
//        if (fieldName.equals ("mainDocIcon")) {
//        	final SDocument doc = (SDocument)record.getAttributeAsObject("document");
//        	
//        	if (doc.getContents() == null || doc.getContents().length < 1)
//        		return null;
//        	
//            MyLabel label = new MyLabel(doc, doc.getContents()[0]);  
//
//            return label;
//        }
//
////    	return super.createRecordComponent(record, colNum);
//        return null;
//    }
    
    
    @Override  
    protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
    	String field = getFieldName(colNum);
    	SDocument document = (SDocument)record.getAttributeAsObject("document");
    	if (document == null || document.getContents() == null || document.getContents().length < 1)
            return super.getCellCSSText(record, rowNum, colNum);  
    	
    	SContent content = document.getContents()[0];
    	
        if (field.equals("mainDoc")) {
			if (content.getProcessed() == SContent.PROCESS_PROCESSED) {
				return "font-weight:bold; color:#287fd6;";
//			} else if (content.getProcessed() == SContent.PROCESS_TO_PROCESS || content.getProcessed() == SContent.PROCESS_ERROR) {
//				return "text-decoration:overline";
            } else {  
                return super.getCellCSSText(record, rowNum, colNum);  
            }  
        } else {  
            return super.getCellCSSText(record, rowNum, colNum);  
        }  
    }  

    // 20130902, taesu, 문서 level 보안 추가
    private Menu setupContextMenu(final Long docId, final String elementId, boolean isSecurity) {
    	DocumentActionUtil.get().setActionParameters(new Object[]{docId, elementId});
    	if (elementId == null || elementId.length() < 1)
    		return DocumentActionUtil.get().getContextMenu(isSecurity);
    	else 
    		return DocumentActionUtil.get().getContextMenu(DocumentActionUtil.TYPE_FILE);
    }

	/**
	 * 리스트 그리드 필드 설정 
	 * align : 정렬
	 * type : 그리드 필드의 타입
	 * filter : 필터가능 여부
	 * title : 필드의 타이틀 설정
	 */
	private void setField(ListGridField glidField, Alignment align, ListGridFieldType type, boolean filter, String title){
		glidField.setAlign(align);
		glidField.setType(type);
		glidField.setCanFilter(filter);
		glidField.setTitle(title);
	}
	
	private boolean isSearchTab;
	private boolean onlyFile = false;
	
	/**
	 * Search Tab에서 사용하는 생성자
	 * Search Tab은 Document Tab과 달리 context menu를 다른 방식으로 Get하기 때문에 만들었음.
	 * */ 
	public DocumentsGrid(boolean isSearchTab, String searchTabName){
		this.isSearchTab = isSearchTab;
		this.searchTabName = searchTabName;		
		initGrid();
		
		
	}

	public DocumentsGrid() {
		initGrid();
		
		
	}
	public DocumentsGrid(boolean onlyFile) {
		this.onlyFile = onlyFile;
		initGrid();
		
	}
	
	/**
	 * Grid 초기화
	 * */
	private void initGrid(){
		
		resetData();
		
		
        // document id
		ListGridField id = new ListGridField("id");
		id.setHidden(true);      
		
		
		// ====================================================
		// 즐겨찾기 전용 필드 
        // target id
		ListGridField targetid = new ListGridField("targetid");
		targetid.setHidden(true);        		
        
		// 문서의 타입을 이미지로 표시한다.
		ListGridField type = new ListGridField("type", I18N.message("type"), 20);
		setField(type, Alignment.CENTER, ListGridFieldType.IMAGE , false, I18N.message("type"));
        type.setImageURLPrefix(Util.imagePrefix());   
        type.setImageURLSuffix(".png");   
        type.setCanEdit(false);   
        type.setRequired(false);
        type.setHeaderTitle("");
		
        // 해당 문서의 폴더 최상위 위치를 표시한다
        // xvarm, mydoc, shared
        ListGridField rootnm = new ListGridField("rootnm", I18N.message("ROOT"), 20);
		setField(rootnm, Alignment.CENTER, ListGridFieldType.IMAGE , false, I18N.message("ROOT"));
		rootnm.setImageURLPrefix(Util.imagePrefix());   
		rootnm.setImageURLSuffix(".png");   
		rootnm.setCanEdit(false);   
		rootnm.setRequired(false);
		rootnm.setHeaderTitle("");
		
		// ====================================================

		// 딸린파일의 수
		ListGridField attachs = new ListGridField("attachs", I18N.message("attachs"), 50);
		setField(attachs, Alignment.CENTER, ListGridFieldType.TEXT, true, I18N.message("attachs"));
//		attachs.addRecordClickHandler(new RecordClickHandler() {
//			@Override
//			public void onRecordClick(RecordClickEvent event) {
//				if(Integer.parseInt(getSelectedRecord().getAttribute("attachs").replace("+", ""))>=1){
//					VLayout showLay = exHLay.get(getEventRow()-1);
//					if(showLay.isVisible())	showLay.hide();
//					else	showLay.show();
//				}
//			}
//		});
		
		// 문서 보안레벨
		ListGridField docSecurity = new ListGridField("docSecurity", I18N.message("docSecurity"), 20);
		setField(docSecurity, Alignment.CENTER, ListGridFieldType.IMAGE, false, "");
		docSecurity.setImageURLPrefix(Util.imagePrefix());   
		docSecurity.setImageURLSuffix(".png");   
		docSecurity.setCanEdit(false);   
		docSecurity.setRequired(false);
		docSecurity.setHeaderTitle("");
		
		// lock 여부를 이미지로 표시한다.
		ListGridField lockyn = new ListGridField("lock", "lock", 20);
		setField(lockyn, Alignment.CENTER, ListGridFieldType.IMAGE , false, "");
        lockyn.setImageURLPrefix(Util.imagePrefix());   
        lockyn.setImageURLSuffix(".png");   
        lockyn.setCanEdit(false);   
        lockyn.setRequired(false);
        lockyn.setHeaderTitle("");
        
//        TODO 락을 건 사람의 툴팁 표시
//        lockyn.setShowHover(true);
//        lockyn.setHoverCustomizer(new HoverCustomizer() {
//			@Override
//			public String hoverHTML(Object value, ListGridRecord record, int rowNum,
//					int colNum) {
//				// TODO Auto-generated method stub
//				if(record.getAttribute("lock") .equals("lock"))
//					return "lock";
//				else
//					return "" ;
//			}
//		});
        
        // 문서 타이틀
		ListGridField titlenm = new ListGridField("titlenm", I18N.message("title"));
		setField(titlenm, Alignment.LEFT, ListGridFieldType.TEXT, false, I18N.message("title"));
		titlenm.setAutoFitWidth(true);
		
		// 문서 타이틀
		ListGridField path = new ListGridField("path", I18N.message("path"));
		setField(path, Alignment.LEFT, ListGridFieldType.TEXT, false, I18N.message("path"));
		path.setAutoFitWidth(true);
		
		// 메인 문서 표시
		ListGridField mainDocIcon = new ListGridField("mainDocIcon", "", 20);
		setField(mainDocIcon, Alignment.LEFT, ListGridFieldType.IMAGE, false, "");
		mainDocIcon.setImageURLPrefix(Util.imagePrefix());   
		mainDocIcon.setImageURLSuffix(".png");  
		mainDocIcon.setHeaderTitle("");
		
		ListGridField mainDoc = new ListGridField("mainDoc", I18N.message("second.mainDoc"));
		setField(mainDoc, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("second.mainDoc"));
		mainDoc.setAutoFitWidth(true);
		mainDoc.setAlign(Alignment.LEFT);	// 20130816, junsoo, 좌로정렬
		
		// 문서타입
		ListGridField doctype = new ListGridField("doctype", I18N.message("doctype"), 100);
		setField(doctype, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("doctype"));
		
		//버전
		ListGridField version = new ListGridField("version", I18N.message("version"),80);
		setField(version, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("version"));
		version.setAutoFitWidth(true);
		
		// 오너
		ListGridField owner = new ListGridField("owner", I18N.message("owner"));
		setField(owner, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("owner"));
		owner.setAutoFitWidth(true);
		
		// 최초 생성일자
		ListGridField created = new ListGridField("created", I18N.message("createddate"), 110);
		setField(created, Alignment.CENTER, ListGridFieldType.DATE, false, I18N.message("createddate"));
		created.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		
		// 최종 수정일자
		ListGridField modified = new ListGridField("modified", I18N.message("modifieddate"), 110);
		setField(modified, Alignment.CENTER, ListGridFieldType.DATE, false, I18N.message("modifieddate"));
		modified.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		
		// retention
		ListGridField retention = new ListGridField("retention", I18N.message("retention"));
		setField(retention, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("retention"));
		retention.setAutoFitWidth(true);
		
		// TemplateId
		ListGridField templateId = new ListGridField("templateId", I18N.message("templateId"), 100);
		setField(templateId, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("templateId"));
		templateId.setHidden(true);
		
		setEmptyMessage(I18N.message("notitemstoshow"));

		
		// DND copy or move?
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.isCtrlKeyDown())
					setDragDataAction(DragDataAction.COPY);
				else
					setDragDataAction(DragDataAction.MOVE);
			}
		});
		
		// My_DOC에서만 D&D 가능하게함.(권한문제)
		// Shared_DOC에서는 메뉴를 통해 이동 및 복사
//		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC){
//			setCanReorderRecords(false);
//			setCanAcceptDroppedRecords(true);
//			setCanDragRecordsOut(true);
//			setCanDrag(true);
//			setCanDrop(true);
//		}
		
		setAutoFitFieldWidths(false);
		
		// 20131218, 아이콘및 파일명 표시를 위해
//		setShowRecordComponents(true);          
//        setShowRecordComponentsByCell(true);
//        setShowRollOver(false);
        
		setShowAllRecords(true);
		setWidth100();
		setHeight100();
		// 20130725, expansion 
		setCanExpandRecords(true);
		
		// 파일관련 정보
		if(onlyFile){
//		fields.add(chk);
			fields.add(attachs);
			fields.add(mainDocIcon);
			fields.add(mainDoc);
			fields.add(owner);
			setCanSelectCells(false);
			setCanRemoveRecords(true);
		}
		// 모든 정보
		else{
			// 여러건이 선택가능하도록 설정
			setSelectionAppearance(SelectionAppearance.CHECKBOX);   
			setSelectionType(SelectionStyle.SIMPLE);  
			
			fields.add(id);
			fields.add(targetid);
			fields.add(rootnm);
			fields.add(type);
//			fields.add(chk);
			fields.add(attachs);
			fields.add(docSecurity);
			fields.add(lockyn);
			fields.add(titlenm);
			fields.add(path);
			fields.add(mainDocIcon);
			fields.add(mainDoc);
			fields.add(doctype);
			fields.add(version);
			fields.add(owner);
			fields.add(created);
			fields.add(modified);
			fields.add(retention);
			fields.add(templateId);
		}
		setFields(fields.toArray(new ListGridField[0]));
		setCanResizeFields(true);
		
		initAction();
	}
	
	/**
	 * Grid Action init
	 */
	
	private void initAction(){
		// 선택 변경이 있을 경우 호출하여 버튼 활성화를 결정함.
		addSelectionChangedHandler(new SelectionChangedHandler() {  
            public void onSelectionChanged(SelectionEvent event) {
           		onRowClick(event);
            }  
        });
		
		// 20130725, junsoo, row click 이벤트
		// 20130805, 레코드가 선택되어 있으면 선택 해제, 선택되어 있지 않으면 선택 (네이버식 동작)
		addRecordClickHandler(new RecordClickHandler() {  
            public void onRecordClick(RecordClickEvent event) {
				if (bContentButtonPressed) {
					bContentButtonPressed = false;
					return;
				}

            	// 확장 부분이거나, 체크박스 자체이면 패스.
            	int num = getFieldNum(event.getField().getName());
            	if (num == 0 || num == 1)
            		return;

            	Record r = event.getRecord();
            	if (!isSelected((ListGridRecord)r))
            		selectRecord(r);
            	else
            		deselectRecord(r);
            	
            	// 20130805, junsoo, 이상하게도 addSelectionChangedHandler 에 걸리지 않아.. 추가함.
            	// 20130905, taesu, 이상하게 addSelectionChangedHandler에 걸림. 주석처리. 
//            	onRowClick();
//            	currentLocation = getEventRow();
            }  
        });  

		// 20130805, junsoo, 레코드에서 우클릭시 동작, 선택되어 있으면 메뉴 표시, 선택되어 있지 않으면, 해당 건만 선택 후 메뉴 표시
		addRowContextClickHandler(new RowContextClickHandler() {
			@Override
			public void onRowContextClick(RowContextClickEvent event) {
				Log.debug("onRowContextClick");
				if (bContentButtonPressed) {
					bContentButtonPressed = false;
					event.cancel();
					return;
				}

				ListGridRecord record = (ListGridRecord)event.getRecord();
				if (!isSelected(record)) {
					deselectAllRecords();
					selectRecord(record);
				}
				
            	onRowClick();
            	
            	// 선택되어있는 Tab이 Search Tab일 경우 Search Tab의 Context Menu를 보여준다.
            	if(!isSearchTab){
            		ListGridRecord[] records = getSelectedRecords();
            		if (records == null || records.length < 1) {
            			Menu menu = setupContextMenu(0L, elementId, false);
            			setContextMenu(menu);
            			return;
            		}
            		Menu menu = setupContextMenu(getSelectedRecord().getAttributeAsLong("id"), elementId, true);
            		setContextMenu(menu);
            	}else{
            		Menu menu;
//            		if(searchTabName.equals("Personal"))
            			menu = DocumentActionUtil.get().getPersonalSearchContextMenu();
//            		else
//            			menu = DocumentActionUtil.get().getSharedSearchContextMenu();
            		setContextMenu(menu);
            	}
            	
//				showContextMenu(event.getX(), event.getY(), menu);
//				event.cancel();

				elementId = null;

			}
		});
		//20140521 육용수 더블클릭시에 문서뷰어 열리게 했음
		addCellDoubleClickHandler(new CellDoubleClickHandler() {			
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				System.out.println("document click");
				final DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
				// TODO Auto-generated method stub
				ListGridRecord record = event.getRecord();
	        	final SDocument doc = (SDocument)record.getAttributeAsObject("document");	    
	        	if(doc.isView()) {
			        	if (doc.getContents() == null || doc.getContents().length < 1){
			        		SC.warn("error.haveNotContents");
			        		return;
			        	}
			        	
			        	final SContent content = doc.getContents()[0];
			        	if(doc.getStatus() == SDocument.DOC_LOCKED || content.getProcessed() == SContent.PROCESS_TO_PROCESS){
			        		SC.warn("error.DocLockorPreocessed");
			        		return;		
			        	}
			        	        	
			        	final String ext =  Util.getExtByFileName(content.getFileName());
			        	SFileType filetype = (SFileType)DataCache.get(DataCache.FILEMENU.getId() + ext);
			        	
			        	final	SContent contents = content;
			        	
			        	documentCodeService.getSDocTypeByDocId(Session.get().getSid(), doc.getId(), new AsyncCallback<SDocType>() {
							@Override
							public void onSuccess(SDocType result) {
								
								if(result.getAttributes().length>0) {
									for (SExtendedAttribute attr : result.getAttributes()) {
										if("bpm".equals(attr.getName())) { 
											if(doc.getStatus()==SDocument.DOC_UNLOCKED) {
												doc.setStatus(SDocument.DOC_CHECKED_OUT); //체크아웃 시킴
												Util.preview(doc.getId(), contents.getElementId(), doc.getFolder().isPrint());
											} else {
												SC.warn(I18N.message("error.alreadyCheckedout"));
											}
											return;
										}
									}
								} else {
									documentCodeService.getFileTypeByName(Session.get().getSid(), ext, new AsyncCallback<SFileType>() {
						        		@Override
										public void onSuccess(SFileType result) {	
						        			try{
							        			String[] viewType = result.getViewer().split(",");
							        			List<String> temp = new ArrayList<String>();
							        			for(String s : viewType)
							        			temp.add(s);
							        			if(result.getLinkViewer() != null && result.getLinkViewer().equals("$LauncherScan$")
							        					&& temp.contains(SFileType.VIEWER_SYSTEM))         					
							        			ScanStarter.view(String.valueOf(doc.getId()));
							        			else if ((result.getLinkViewer() == null || result.getLinkViewer().equals("$Launcher$")) 
							        					&& temp.contains(SFileType.VIEWER_SYSTEM))
							        			Util.open(doc.getId(),content.getElementId(),content.getFileName());	
							        			
//							        			else if(result.getLinkViewer() != null && result.getLinkViewer().equals("$BPM$")) { //BPM 속성 검사
//						        					if(doc.getStatus()==SDocument.DOC_UNLOCKED) {
//														doc.setStatus(SDocument.DOC_CHECKED_OUT); //체크아웃 시킴
//														Util.preview(doc.getId(), contents.getElementId(), doc.getFolder().isPrint());
//													} else {
//														SC.warn(I18N.message("error.alreadyCheckedout"));
//													}
//													return;
//								        		}
							        			
							        			else if (result.getLinkViewer() != null && temp.contains(SFileType.VIEWER_SYSTEM)){
							        			String URL = GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + 
							        					"&docId=" + doc.getId() + "&elementId=" + content.getElementId() + "&viewerType=1&print=false";
							        			
							        			WindowUtils.openPopupUrl(URL,"Viewer","" );
							        			
							        			} 
						        			}
						        			catch(Exception e){
						        				SC.warn("error.LauncherFail");
						        			}
										}
										@Override
										public void onFailure(Throwable caught) {						
											Log.serverError(caught, false);
										}
									});
									
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								SC.warn("error.LauncherFail");
							}
						});
			        	
			        	event.cancel();
	        		}
		    	
	        	else
	        	SC.warn(I18N.message("error.permissionView"));
			}
		});
	
		// 20131227, junsoo, 속도 개선을 위해서 grid 에 content를 표시하고, context menu 이벤트 정의
		addCellContextClickHandler(new CellContextClickHandler() {
			
			@Override
			public void onCellContextClick(final CellContextClickEvent event) {
				Log.debug("onCellContextClick");
				if (bContentButtonPressed) {
					bContentButtonPressed = false;
					return;
				}
		        String fieldName = getFieldName (event.getColNum());
		        
		        if (fieldName.equals ("mainDoc")) {
		        	bContentButtonPressed = true;

		        	ListGridRecord record = event.getRecord();
		        	SDocument doc = (SDocument)record.getAttributeAsObject("document");
		        	if (doc.getContents() == null || doc.getContents().length < 1)
		        		return;
		        	SContent content = doc.getContents()[0];

		        	
//					Menu menu = setupContextMenu(doc.getId(), content.getElementId());
					ItemFactory.setFileMenuByExt(doc, content, Util.getExtByFileName(content.getFileName()), new ReturnHandler() {
						@Override
						public void onReturn(Object param) {
							showContextMenu(event.getX(), event.getY(), (Menu)param);
//							btn.setContextMenu((Menu)param);
						}
					});

					event.cancel();
		        }
			}
		});
		
		// 20130730, junsoo, addRowContextClickHandler 대신 사용함. 해당되지 않는 부분의 contextmenu 표시를 없애기 위해.
		// grid의 빈 곳에서 우클릭할 경우에 대비해서 이 로직이 필요함.
		addRightMouseDownHandler(new RightMouseDownHandler() {
			
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				Log.debug("onRightMouseDown");
				if (bContentButtonPressed) {
					bContentButtonPressed = false;
					return;
				}
//				getDraftRights();
//				ListGridRecord record = (ListGridRecord)event.getSource();
//				if (!isSelected(record)) {
//					deselectAllRecords();
//					selectRecord(record);
//				}
//				
//            	onRowClick();
            	
            	// 선택되어있는 Tab이 Search Tab일 경우 Search Tab의 Context Menu를 보여준다.
            	if(!isSearchTab){
            		ListGridRecord[] records = getSelectedRecords();
            		if (records == null || records.length < 1) {
            			Menu menu = setupContextMenu(0L, elementId, false);
            			setContextMenu(menu);
            			return;
            		}
            		Menu menu = setupContextMenu(getSelectedRecord().getAttributeAsLong("id"), elementId, true);
            		setContextMenu(menu);
            	}else{
            		ListGridRecord[] records = getSelectedRecords();
            		if (records != null && records.length > 0) {
	            		Menu menu;
	//            		if(searchTabName.equals("Personal"))
	            			menu = DocumentActionUtil.get().getPersonalSearchContextMenu();
	//            		else
	//            			menu = DocumentActionUtil.get().getSharedSearchContextMenu();
	            		setContextMenu(menu);
            		} else {
	            		setContextMenu(null);
            		}
            	}

//	        	Menu menu = setupContextMenu(getSelectedRecord().getAttributeAsLong("id"), elementId);
//	        	setContextMenu(menu);

				elementId = null;
				
			}
		});
		
	     // kimsoeun GS인증용 - 그리드 헤더 우클릭 없앰
		setShowHeaderContextMenu(false);
		
		/*
		 * Record 제거 동작
		 */
		addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
			}
		});
		
		/**
		 * 확장 불가능한 문서도 확장 가능하게 보여주고 있기 때문에 확장 가능 정보를 저장하게 하여 
		 * 이벤트 종료시 확장 여부를 재설정 하게 한다.
		 */
		addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				ListGridRecord[] records = DocumentsPanel.get().getDocumentsMenu().getMydocTree().getRecords();
				DocumentsPanel.get().getDocumentsMenu().getMydocTree().getIsFolderInfos().clear();
				List list = DocumentsPanel.get().getDocumentsMenu().getMydocTree().getIsFolderInfos();
				for (ListGridRecord record : records) {
					list.add(record.getAttributeAsBoolean("isFolder"));
					record.setAttribute("isFolder", true);
				}
			}
		});
	}
	
	/**
	 * 20130911, junsoo, 모두 동작가능하도록 변경하여 주석처리함
	 * 현재 위치에 따라서 D&D활성화 유무를 결정한다.
	 * 공유 문서에서는 동작 안되도록 되어있음.
	 */
//	private void controlDrop(){
//		// My_DOC에서만 D&D 가능하게함.(권한문제)
//		// Shared_DOC에서는 메뉴를 통해 이동 및 복사
//		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC){
//			setCanReorderRecords(false);
//			setCanAcceptDroppedRecords(true);
//			setCanDragRecordsOut(true);
//			setCanDrag(true);
//			setCanDrop(true);
//		}else{
//			setCanReorderRecords(false);
//			setCanAcceptDroppedRecords(false);
//			setCanDragRecordsOut(false);
//			setCanDrag(false);
//			setCanDrop(false);
//		}
//	}
	
	public void onRowClick() {
		onRowClick(null);
	}
	
	// 20140219, junsoo, smartgwt 버그 수정용 변수. 전체 선택 해지시 getSelectedRecords가 정상 작동 하지 않음.
	private int multiCalled = 0; 
	
	/**
	 * 20131211 na SelectionEvent를 이용하여 폴더 오류를 없애는 코드
	 * 20130725, junsoo
	    document 선택 처리. observer에게 알려줌. 
	 */
	public void onRowClick(SelectionEvent event) {
		
		// 20140221, 다시한번 actiontype을 설정. 검색에서 우클릭하다 문서에서 우클릭을 바로하면 contextmenu가 정상적이지 않으므로
		if (!isSearchTab)
			DocumentActionUtil.get().setActivatedMenuType(currentMenuType);
		

		// 20140219, junsoo, smartgwt 버그. 전체 선택 해지시 getSelectedRecords가 정상 작동 하지 않음.
		if (event != null && !event.getState()) {
			int nowCount = getSelectedRecords().length;
			if (multiCalled != nowCount)
				multiCalled = nowCount;
			else {
				Session.get().selectDocuments(null);
				return;
			}
		} else {
			multiCalled = 0;
		}
		
		List<SRecordItem> itemList = new ArrayList<SRecordItem>();
//		controlDrop();
		// 20130725, junsoo, add listener
		for (ListGridRecord selected : getSelectedRecords()) {
			SDocument doc = (SDocument)selected.getAttributeAsObject("document");
			if (doc != null) {
				itemList.add(new SRecordItem(doc));
				continue;
			}
			
			// 북마크일 경우가 폴더가 올 수 있으므로..
			SFolder folder = (SFolder)selected.getAttributeAsObject("folder");
			// 20140317, junsoo, 3개 선택후 한개 해지할 때, event.getState() 가 false라서 이 부분이 실행 안됨
//			if (folder != null && event != null && event.getState())
			if (folder != null )
				itemList.add(new SRecordItem(folder));

		}
		
		SRecordItem[] items = null;
		
		if (itemList.size() > 0)
			items = itemList.toArray(new SRecordItem[0]);
		
		Session.get().selectDocuments(items);
		controlDraftMenu(getSelectedRecord());
		selectRowNum = getEventRow();
	}
	
	/**
	 * Grid의 Field에 맞추어 데이터를 세팅한다.
	 * @param record
	 * @param document
	 */
	public void setRecordData(ListGridRecord record, SDocument document) {
		// 아이디 셋팅
		record.setAttribute("id", document.getId());
		// chk
		record.setAttribute("chk", false);
		
		// 폴더 아이디
		record.setAttribute("folderid", document.getFolder().getId());
		
		// 원 경로
		record.setAttribute("path", document.getPathExtended());

		// 딸린 파일 갯수 표시
		if (document.getContents().length < 1)
			record.setAttribute("attachs", "");
		else
			record.setAttribute("attachs", "+" + String.valueOf(document.getContents().length));
		
		// 문서 보안레벨 적용여부 표시
		// 20131205, junsoo, NONE 도 표시 하도록 함.
//		if (document.getSecurityProfile() != null && document.getSecurityProfile() != 0)
		if (document.getSecurityProfile() != null)
			record.setAttribute("docSecurity", "p7m");
		else
			record.setAttribute("docSecurity", "");
		
		// lock여부 셋팅
		if(document.getStatus() == SDocument.DOC_LOCKED)
			record.setAttribute("lock", "lock");
		else if (document.getStatus() == SDocument.DOC_CHECKED_OUT)
			record.setAttribute("lock", "document_out");
		else
			record.setAttribute("lock", "");
		
		SContent[] scontents = document.getContents();
		
		// 데이터 validation
		if(scontents.length != 0){
			record.setAttribute("mainDocIcon", scontents[0].getIcon().split("\\.")[0]);
			String filename = Util.strCut(scontents[0].getFileName(), 50, "...");
//			record.setAttribute("mainDoc", filename+" ("+Util.setFileSize(scontents[0].getFileSize(), true)+")");
			record.setAttribute("mainDoc", preparedFileName(scontents[0]));
			record.setAttribute("fullMainDoc", scontents[0].getFileName());
		}else{
			record.setAttribute("mainDocIcon", "");
			record.setAttribute("mainDoc", I18N.message("second.nofile"));
		}
		if(document.getTemplateId()!=null)
			record.setAttribute("templateId", document.getTemplateId());
		//문서타이틀
		record.setAttribute("titlenm", document.getTitle());
		//문서 타입
		record.setAttribute("doctype", document.getDocTypeName());
		record.setAttribute("doctypeId", document.getDocType());
		//버전
		record.setAttribute("version", document.getVersion());
		//오너
		record.setAttribute("owner", document.getCreateUserName());
		//최초생성일자
		record.setAttribute("created", document.getCreationDate());
		//최종수정일자
		record.setAttribute("modified", document.getLastModified());
		
		//retention
//		if(document.getRetention() != null){
//			Map<String, String> dateMap = getExpireDate(document);
//			String retentionValue = dateMap.get("retentionDate")+" ("+dateMap.get("difference")+")";
//			record.setAttribute("retention", retentionValue);
//		}
		record.setAttribute("retention", Util.getFormattedExpireDate(document.getCreationDate(), document.getExpireDate()));
				
		//딸린파일정보
		record.setAttribute("contents", document.getContents());
		record.setAttribute("templateId", document.getTemplateId());

		record.setAttribute("folder", document.getFolder());
		
		record.setAttribute("folderId", document.getFolder().getId());
		
		// 20130725, junsoo, sdocument 자체를 저장
		record.setAttribute("document", document);

		// 20130906, taesu, 추가
		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED)
			record.setAttribute("rewriteCMD", document.getRewriteCMD());
	}
	
	public void setRecordDataEx(ListGridRecord record, SDocument document) {
		SExtendedAttribute[] attr = document.getAttributes();
		
		for (int i = 0; i < attr.length; i++) {
			if(attr[i].getPriority() != null){
				switch (attr[i].getEditor()) {
				case SExtendedAttribute.EDITOR_LISTBOX:
					//20140113na 코드 저장 방식 변경에 따른 수정
					if(attr[i].getStringValue() != null && attr[i].getDescription() != null){
//					record.setAttribute(attr[i].getName(), attr[i].getValue() + attr[i].getDescription());
						boolean flag = false;
						String strValue = attr[i].getStringValue();
						String[] opition = attr[i].getOptions();
						for (int j = 0; j < opition.length; j++) {
							int blank =  opition[j].indexOf(" ");
							String strTemp  = opition[j].substring(0, blank);
							if(blank > 0 && strTemp.equals(strValue)){
								record.setAttribute(attr[i].getName(), opition[j]);
								flag = true;
							}
					}
					
					if(flag) break;
					
//					if(attr[i].getDescription() != null && attr[i].getName() != null){
//						record.setAttribute(attr[i].getName(), attr[i].getValue() + attr[i].getDescription());
//						break;
					}
				default:
					record.setAttribute(attr[i].getName(), attr[i].getValue());
					break;
				}
			}
//				record.setAttribute("attr"+i, attr[i].getName() +": "+ attr[i].getValue());
		}
	}
	
	/**
	 * 사용 불가능한 메뉴만을 남겨 기안 목록을 만든다 
	 * 최종 남는건 현재 사용 불가능한 문서의 권한이므로 문서의 권한을 넘긴다.
	 * @param record
	 */
	private void controlDraftMenu(ListGridRecord record){
		// 공유 문서에서만 동작하게 함
		if(DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_SHARED)	return;
		
		if(record == null)	return;
		// 모든 권한 획득
		LinkedHashMap<Integer, String> DraftMap = DocumentActionUtil.get().getDraftMap();
		
		// 문서의 권한
		String rewriteCMD = record.getAttribute("rewriteCMD");
		
		if(rewriteCMD !=null){
			// 내 권한
			String rights = DocumentActionUtil.get().getRights().toString();
			
			rewriteCMD = removeMyRightFromDocRight(rights, rewriteCMD);
			rewriteCMD = setDraftTypeByDocStatus(rewriteCMD);
			rewriteCMD = rewriteCMD.replaceAll(",", "").trim();
			
			if(rewriteCMD.length()==0)	controlToolbarDraftButton(false);
			else	controlToolbarDraftButton(true);
			
			DocumentsPanel.get().setDraftRights(rewriteCMD);
		}else
			controlToolbarDraftButton(false);
	}
	
	/**
	 * 툴바의 DraftButton 활성/비활성화를 제어한다. 
	 * @param enable
	 */
	private void controlToolbarDraftButton(boolean enable){
		DocumentActionUtil.get().controlDraftIcon(enable);
	}
	
	/**
	 * 문서의 사용 가능한 기안 목록 중 내가 사용 가능한 기능을 제거해서 사용 불가능한 목록을 만들어 기안 종류 선택에 추가한다.
	 * @param myRight
	 * @param docRight
	 */
	private String removeMyRightFromDocRight(String myRight, String docRight){
		if (myRight.toString().contains("delete")) {
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DELETE), "");
		}
		if (myRight.toString().contains("download")) {
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DOWNLOAD), "");
		}
		if (myRight.toString().contains("check")) {
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKIN), "");
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKOUT), "");
//				disable	("lock");
//				disable	("unlock");
		}
		if (myRight.toString().contains("write")) {
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_REGISTRATION), "");
//				disable	("paste");
		}else{
			if(!docRight.contains(String.valueOf(Constants.DRAFT_TYPE_REGISTRATION)))
				docRight += String.valueOf(Constants.DRAFT_TYPE_REGISTRATION);
		}
		if (myRight.toString().contains("view")) {
		}
		if (myRight.toString().contains("extend")) {
//				disable	("expire");
//				disable	("expire_expire");
//				disable	("expire_restore");
		}
		return docRight;
	}
	
	/**
	 * 현재 문서의 상태에 따라 선택 가능한 Draft Type을 재설정한다. 
	 * @param docRight
	 * @return
	 */
	private String setDraftTypeByDocStatus(String docRight){
		SDocument doc = (SDocument)getSelectedRecord().getAttributeAsObject("document");
		int status = doc.getStatus();
		// 참조 : DocumentsActionUtil
		// unlock 상태일 경우 check_in 불가능
		if(status == SDocument.DOC_UNLOCKED){
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKIN), "");
		}
		if(status == SDocument.DOC_LOCKED){
			docRight = "";	// Lock 상태는 기안 방지
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DELETE), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKIN), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKOUT), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DOWNLOAD), "");
		}
		// checked_out 상태일 경우 delete, check_out 불가능
		else if(status == SDocument.DOC_CHECKED_OUT){
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DELETE), "");
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKOUT), "");
		}
		return docRight;
	}
	
	/***
	 * Search Tab Grid Set Data
	 * @param records
	 * @param gridPager
	 * @param isNew	: gridPager 초기화 여부(새로운 검색시에만 true)
	 */
	public void setSearchGridData(List<SDocument> records, PagingToolStrip gridPager, boolean isNew){
		showSearchGridRecord(gridPager, records, records.size(), isNew);
	}
	
	/**
	 * 첨부 파일을 보여줄때 사용한다.(페이징 처리 안됨)		 
	 * @param data
	 */
	public void setGridData(List<SDocument> data){
		ListGridRecord records[] = new ListGridRecord[data.size()];
		for (int i = 0; i < data.size() ; i++) {
			SDocument document = data.get(i);
			ListGridRecord record = new ListGridRecord();
			setRecordData(record, document);
			setRecordDataEx(record, document);
			records[i]=record;
		}
		setData(records);
	}
	
	/**
	 * Documents Tab Grid Set Data
	 * @param data
	 * @param totalLength
	 * @param pageNum
	 * @param gridPager
	 */
	public void setGridData(List<SDocument> data, long totalLength, int pageNum, PagingToolStrip gridPager){
		if(data.size()>0 && data != null){
			int selectRecordIndex = -1;
			SDocument selectedDocument = null;
			int dataLength = (data.size() >= gridPager.getPageSize())? gridPager.getPageSize() : data.size();
			ListGridRecord records[] = new ListGridRecord[dataLength];
			
			// 20131230na 확장속성 초안 
			fields.removeAll(attrFields);
			attrFields.removeAll(attrFields);
			
			for (int i = 0; i < dataLength; i++){
				final SDocument document = data.get(i);
				SExtendedAttribute[] attr = document.getAttributes();
			
				for (int j = 0; j < attr.length; j++){
					if(attr[j].getPriority() == null) continue;
					if(!hasFieldName(attrFields, attr[j].getName())){
						ListGridField field = new ListGridField(attr[j].getName(), attr[j].getName());
						setField(field, Alignment.CENTER, ListGridFieldType.TEXT, false, attr[j].getName());
						field.setWidth(80);
						field.setAutoFitWidth(true);
						field.setCanEdit(false);   
						field.setRequired(false);
						attrFields.add(field);
					}
				}
			}
			fields.addAll(attrFields);
			
			setFields(fields.toArray(new ListGridField[0]));
			
			// 데이터 세팅
			for (int i = 0; i < dataLength; i++) {
				final SDocument document = data.get(i);
				ListGridRecord record = new ListGridRecord();
				setRecordData(record, document);
				setRecordDataEx(record, document);

				if(DocumentsPanel.get().expandDocid == document.getId())  {
					selectRecordIndex = i;
					selectedDocument = document;
				}
				records[i]=record;
				// 20130730, 특별한 선택이 없으면 첫번째 선택
				// 20130822, junsoo, 로드시 아무것도 선택하지 않도록 함.
//					if (data != null && data.size() > 0)
//						selectedDocument = data.get(0);
			}
			// 데이터 없으면 리턴.
			if (totalLength < 1) {
				Session.get().selectDocuments(null);
				return;
			}
//			DocumentsPanel.get().getListingPanel().getGrid().selectRecord(selectRecordIndex);
			if (selectedDocument != null) {
				SRecordItem item = new SRecordItem(selectedDocument);
				//DocumentsPanel.get().onSelectedDocument(DocumentsPanel.get().expandDocid , "");
				
				Session.get().selectDocuments(new SRecordItem[]{item});
			}
			DocumentsPanel.get().expandDocid = 0;
			
			setData(records);
			setGridPagerInfo(gridPager, data.size(), pageNum);
			
			// 20130816, junsoo, select는 마지막에 해야함.
			// 20130819, junsoo, 무조건 첫번째 행 선택되는 기능 없앰.
			if (selectRecordIndex != -1) {
				selectRecord(selectRecordIndex);
				expandRecord(getRecord(selectRecordIndex));
				
			}
		}else{
			// 검색된 데이터가 없을경우 데이터 초기화
			setData(new ListGridRecord[] {});
			setGridPagerInfo(gridPager, 0 , 1);
//			getDraftRights();
		}
	}

	/**
	 *	GridPager의 정보들을 설정한다, TotalLength 사용 X
	 * */
	public void setGridPagerInfo(PagingToolStrip gridPager, int recordLength, int pageNum){
		if(gridPager == null)	return;

		gridPager.setRespPageInfo(gridPager.isHavingNextPage(), pageNum);
	}
	
	/**
	 *	SearchTab에서 선택되어있는 Tab(Personal, Shared)에 맞추어 데이터를 그리드에 보여주며, GridPager를 세팅한다. 
	 *	@param gridPager
	 *	@param records
	 *	@param recordSize
	 *	@param isNew	: gridPager pageNum 초기화
	 * */
	private void showSearchGridRecord(PagingToolStrip gridPager, List<SDocument> records, int recordSize, boolean isNew){
		// gridPager의 pageSize에 맞게 데이터 제거
		if(recordSize>0 && (recordSize > gridPager.getPageSize())){
			int differ = recordSize - gridPager.getPageSize();
			if(records.size()>gridPager.getPageSize()){
				for(int i=1; i <= differ ; i++){
					records.remove(recordSize-i);
				}
			}
		}
		ListGridRecord listRecord[] = new ListGridRecord[records.size()];
		for (int i = 0; i < records.size(); i++) {
			SDocument document = records.get(i);
			ListGridRecord record = new ListGridRecord();
			setRecordData(record, document);
			listRecord[i]=record;
		}
		setData(listRecord);
		
		if(gridPager!=null){
			// gridPager 초기화 설정
			if(isNew)
				setGridPagerInfo(gridPager, recordSize, 1);
			else
				setGridPagerInfo(gridPager, recordSize, gridPager.getPageNum());
		}
	}
	
	/**
	 * Thumbnail 정보를 획득한다.(Thumbnail 버튼 선택시 동작)
	 */
	public void setThumbnailInfo(){
		thumbnailInfos.clear();
		for (ListGridRecord record : getRecords()) {
			final SDocument doc = (SDocument)record.getAttributeAsObject("document");
			ServiceUtil.document().getThumbnailInfo(Session.get().getSid(), doc, new AsyncCallback<List<Map<String,String>>>() {
				@Override
				public void onSuccess(List<Map<String, String>> result) {
					List<String[]> values = new ArrayList<String[]>();
					for (Map<String, String> map : result) {
						String[] key = map.keySet().toArray(new String[0]);
						
						values.add(new String[]{key[0], map.get(key[0])});
					}
					thumbnailInfos.put(doc.getId(), values);
				}
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught, false);
				}
			});
		}
	}
	
	/*
	 * Getter, Setter
	 */
	public int getSelectRowNum() {
		return selectRowNum;
	}
	public void setSelectRowNum(int selectRowNum) {
		this.selectRowNum = selectRowNum;
	}
	

	
	/**
	 * 20131230na 리스트와 한글 비교
	 * @param list
	 * @param str
	 * @return
	 */
	public boolean hasFieldName(List<ListGridField> list, String str){
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getName().equals(str)) return true;
		}
		return false;
	}
	
	
}
