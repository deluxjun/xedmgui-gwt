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
	// Context Menu ����� List
//	private List<String> contextListData = new ArrayList<String>();
	// ���õ� �̹����� elementId ����
	private String elementId="";
	// ��ǥ ������ elementId ����
	private List<String> elementIds = new ArrayList<String>();
	
	// ���� ������ �ο찪 ��ȯ
	private int selectRowNum = 0;
	
	private boolean recordsExpanded;
	// ���� ���õǾ��ִ� row��
//	private int currentLocation = 0;
	// �˻��� Tab ���п�
	private String searchTabName;
	
	public List<ListGridField> fields = new ArrayList<ListGridField>();
	public List<ListGridField> attrFields = new ArrayList<ListGridField>();
	
	// 20140221, junsoo, ȭ�鿡 ���� ������ �޴��� ǥ�õǵ��� �ϱ� ���� �߰�.
	private int currentMenuType;
	
	// �̹��� element �ʱ�ȭ
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
	// expansion component �� ��ư ���� ���
	// ==================================================================
	//�����!
	private com.smartgwt.client.widgets.events.ClickHandler contentClickHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
		@Override
		public void onClick(final com.smartgwt.client.widgets.events.ClickEvent event) {
			//20140114na search�� ������Ŭ�� ��Ȱ��ȭ
			if(isSearchTab == true) return;
			bContentButtonPressed = true;
			MyLabel button = (MyLabel)event.getSource();
			
			// 20130807, junsoo, Ȯ���ڿ� ���� ������ �׼��� ������ ���� ȹ��
//			Menu menu = setupContextMenu(doc.getId(), content.getElementId());
			// 20140121, junsoo, content ����
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
			// 20140121, junsoo, content ����
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
			
			// 20130807, junsoo, Ȯ���ڿ� ���� ������ �׼��� ������ ���� ȹ��
//			Menu menu = setupContextMenu(doc.getId(), content.getElementId());
			// 20140121, junsoo, content ����
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
			// 20140121, junsoo, content ����
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

		// 20131206, junsoo, content�� process ���¿� ���� ��Ȱ��ȭ ��Ŵ
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

			// 20131206, junsoo, content�� process ���¿� ���� ��Ȱ��ȭ ��Ŵ
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
			
			// 20131206, junsoo, content�� process ���¿� ���� ��Ȱ��ȭ ��Ŵ
			if (content.getProcessed() == SContent.PROCESS_PROCESSED) {
				addClickHandler(contentThumbnailClickHandler);
				addShowContextMenuHandler(contentThumbnailShowContextMenuHandler);
			} else {
				setDisabled(true);
			}
		}
	};

    /*
     * �׸��� Ȯ�� �̺�Ʈ
     * ������ ���ڵ庰�� ���� ������ ������� �̺�Ʈ ȣ���
     * �׸��� ������ �׸����� row ���� ��ŭ ȣ���.
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

//        	// 20120807, junsoo, ShowContextMenuHandler �� �񵿱⿡ ���� contextMenu�� �ڵ鸵�� �ʹ� ���� mouse down���� ��ü.
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
        	
        	// �޴��� ���� ���°� Thumnail�� ��� Thumnail�� �����ش�.
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

    			// ����� �̹��� ����
    			MyImg img = new MyImg("");
    			// ����� �̹��� ������.
    			if(thumbnailInfos.get(doc.getId()) != null
    					&& thumbnailInfos.get(doc.getId()).size() > i 
    					&& !thumbnailInfos.get(doc.getId()).get(i)[1].contains("null")){
    				// url ȹ��
    				String url = thumbnailInfos.get(doc.getId()).get(i)[1];
    				// url ����
    				url = url.substring(url.indexOf("/view/")).replaceAll("/view/", "");
    				img.setAppImgDir(Util.thumbnailPrefix());
    				// url ����
    				img.setSrc(url);
    			}
    			// ����� �̹����� ���� ��� �⺻ �̹���(������)�� �����ش�.
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
    		// thumnail view ���°� �ƴҰ�� �⺻ ��ư�� ������
    		else{
    			MyLabel btn = new MyLabel(doc, content);
    			//20140522 ����� Ȯ�� ���Ͽ� ���� ��ó �⵿ flow
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
        
        
        // �� canvas�� Ŭ���� ���, grid event�� ����� �������� �ʾ� �߰���.
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
    
    // 20130801, junsoo, ÷�������� ���� ���, Ȯ�� ����
    @Override
    public boolean canExpandRecord(ListGridRecord record, int rowNum) {
        SDocument doc = (SDocument)record.getAttributeAsObject("document");
        
        if (doc == null || doc.getContents() == null || doc.getContents().length < 2)
        	return false;
        
        return true;
    }
	// ==================================================================
    
//    // 20131218, junsoo, �� content�� ��ưó��
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

    // 20130902, taesu, ���� level ���� �߰�
    private Menu setupContextMenu(final Long docId, final String elementId, boolean isSecurity) {
    	DocumentActionUtil.get().setActionParameters(new Object[]{docId, elementId});
    	if (elementId == null || elementId.length() < 1)
    		return DocumentActionUtil.get().getContextMenu(isSecurity);
    	else 
    		return DocumentActionUtil.get().getContextMenu(DocumentActionUtil.TYPE_FILE);
    }

	/**
	 * ����Ʈ �׸��� �ʵ� ���� 
	 * align : ����
	 * type : �׸��� �ʵ��� Ÿ��
	 * filter : ���Ͱ��� ����
	 * title : �ʵ��� Ÿ��Ʋ ����
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
	 * Search Tab���� ����ϴ� ������
	 * Search Tab�� Document Tab�� �޸� context menu�� �ٸ� ������� Get�ϱ� ������ �������.
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
	 * Grid �ʱ�ȭ
	 * */
	private void initGrid(){
		
		resetData();
		
		
        // document id
		ListGridField id = new ListGridField("id");
		id.setHidden(true);      
		
		
		// ====================================================
		// ���ã�� ���� �ʵ� 
        // target id
		ListGridField targetid = new ListGridField("targetid");
		targetid.setHidden(true);        		
        
		// ������ Ÿ���� �̹����� ǥ���Ѵ�.
		ListGridField type = new ListGridField("type", I18N.message("type"), 20);
		setField(type, Alignment.CENTER, ListGridFieldType.IMAGE , false, I18N.message("type"));
        type.setImageURLPrefix(Util.imagePrefix());   
        type.setImageURLSuffix(".png");   
        type.setCanEdit(false);   
        type.setRequired(false);
        type.setHeaderTitle("");
		
        // �ش� ������ ���� �ֻ��� ��ġ�� ǥ���Ѵ�
        // xvarm, mydoc, shared
        ListGridField rootnm = new ListGridField("rootnm", I18N.message("ROOT"), 20);
		setField(rootnm, Alignment.CENTER, ListGridFieldType.IMAGE , false, I18N.message("ROOT"));
		rootnm.setImageURLPrefix(Util.imagePrefix());   
		rootnm.setImageURLSuffix(".png");   
		rootnm.setCanEdit(false);   
		rootnm.setRequired(false);
		rootnm.setHeaderTitle("");
		
		// ====================================================

		// ���������� ��
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
		
		// ���� ���ȷ���
		ListGridField docSecurity = new ListGridField("docSecurity", I18N.message("docSecurity"), 20);
		setField(docSecurity, Alignment.CENTER, ListGridFieldType.IMAGE, false, "");
		docSecurity.setImageURLPrefix(Util.imagePrefix());   
		docSecurity.setImageURLSuffix(".png");   
		docSecurity.setCanEdit(false);   
		docSecurity.setRequired(false);
		docSecurity.setHeaderTitle("");
		
		// lock ���θ� �̹����� ǥ���Ѵ�.
		ListGridField lockyn = new ListGridField("lock", "lock", 20);
		setField(lockyn, Alignment.CENTER, ListGridFieldType.IMAGE , false, "");
        lockyn.setImageURLPrefix(Util.imagePrefix());   
        lockyn.setImageURLSuffix(".png");   
        lockyn.setCanEdit(false);   
        lockyn.setRequired(false);
        lockyn.setHeaderTitle("");
        
//        TODO ���� �� ����� ���� ǥ��
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
        
        // ���� Ÿ��Ʋ
		ListGridField titlenm = new ListGridField("titlenm", I18N.message("title"));
		setField(titlenm, Alignment.LEFT, ListGridFieldType.TEXT, false, I18N.message("title"));
		titlenm.setAutoFitWidth(true);
		
		// ���� Ÿ��Ʋ
		ListGridField path = new ListGridField("path", I18N.message("path"));
		setField(path, Alignment.LEFT, ListGridFieldType.TEXT, false, I18N.message("path"));
		path.setAutoFitWidth(true);
		
		// ���� ���� ǥ��
		ListGridField mainDocIcon = new ListGridField("mainDocIcon", "", 20);
		setField(mainDocIcon, Alignment.LEFT, ListGridFieldType.IMAGE, false, "");
		mainDocIcon.setImageURLPrefix(Util.imagePrefix());   
		mainDocIcon.setImageURLSuffix(".png");  
		mainDocIcon.setHeaderTitle("");
		
		ListGridField mainDoc = new ListGridField("mainDoc", I18N.message("second.mainDoc"));
		setField(mainDoc, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("second.mainDoc"));
		mainDoc.setAutoFitWidth(true);
		mainDoc.setAlign(Alignment.LEFT);	// 20130816, junsoo, �·�����
		
		// ����Ÿ��
		ListGridField doctype = new ListGridField("doctype", I18N.message("doctype"), 100);
		setField(doctype, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("doctype"));
		
		//����
		ListGridField version = new ListGridField("version", I18N.message("version"),80);
		setField(version, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("version"));
		version.setAutoFitWidth(true);
		
		// ����
		ListGridField owner = new ListGridField("owner", I18N.message("owner"));
		setField(owner, Alignment.CENTER, ListGridFieldType.TEXT, false, I18N.message("owner"));
		owner.setAutoFitWidth(true);
		
		// ���� ��������
		ListGridField created = new ListGridField("created", I18N.message("createddate"), 110);
		setField(created, Alignment.CENTER, ListGridFieldType.DATE, false, I18N.message("createddate"));
		created.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		
		// ���� ��������
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
		
		// My_DOC������ D&D �����ϰ���.(���ѹ���)
		// Shared_DOC������ �޴��� ���� �̵� �� ����
//		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC){
//			setCanReorderRecords(false);
//			setCanAcceptDroppedRecords(true);
//			setCanDragRecordsOut(true);
//			setCanDrag(true);
//			setCanDrop(true);
//		}
		
		setAutoFitFieldWidths(false);
		
		// 20131218, �����ܹ� ���ϸ� ǥ�ø� ����
//		setShowRecordComponents(true);          
//        setShowRecordComponentsByCell(true);
//        setShowRollOver(false);
        
		setShowAllRecords(true);
		setWidth100();
		setHeight100();
		// 20130725, expansion 
		setCanExpandRecords(true);
		
		// ���ϰ��� ����
		if(onlyFile){
//		fields.add(chk);
			fields.add(attachs);
			fields.add(mainDocIcon);
			fields.add(mainDoc);
			fields.add(owner);
			setCanSelectCells(false);
			setCanRemoveRecords(true);
		}
		// ��� ����
		else{
			// �������� ���ð����ϵ��� ����
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
		// ���� ������ ���� ��� ȣ���Ͽ� ��ư Ȱ��ȭ�� ������.
		addSelectionChangedHandler(new SelectionChangedHandler() {  
            public void onSelectionChanged(SelectionEvent event) {
           		onRowClick(event);
            }  
        });
		
		// 20130725, junsoo, row click �̺�Ʈ
		// 20130805, ���ڵ尡 ���õǾ� ������ ���� ����, ���õǾ� ���� ������ ���� (���̹��� ����)
		addRecordClickHandler(new RecordClickHandler() {  
            public void onRecordClick(RecordClickEvent event) {
				if (bContentButtonPressed) {
					bContentButtonPressed = false;
					return;
				}

            	// Ȯ�� �κ��̰ų�, üũ�ڽ� ��ü�̸� �н�.
            	int num = getFieldNum(event.getField().getName());
            	if (num == 0 || num == 1)
            		return;

            	Record r = event.getRecord();
            	if (!isSelected((ListGridRecord)r))
            		selectRecord(r);
            	else
            		deselectRecord(r);
            	
            	// 20130805, junsoo, �̻��ϰԵ� addSelectionChangedHandler �� �ɸ��� �ʾ�.. �߰���.
            	// 20130905, taesu, �̻��ϰ� addSelectionChangedHandler�� �ɸ�. �ּ�ó��. 
//            	onRowClick();
//            	currentLocation = getEventRow();
            }  
        });  

		// 20130805, junsoo, ���ڵ忡�� ��Ŭ���� ����, ���õǾ� ������ �޴� ǥ��, ���õǾ� ���� ������, �ش� �Ǹ� ���� �� �޴� ǥ��
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
            	
            	// ���õǾ��ִ� Tab�� Search Tab�� ��� Search Tab�� Context Menu�� �����ش�.
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
		//20140521 ����� ����Ŭ���ÿ� ������� ������ ����
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
												doc.setStatus(SDocument.DOC_CHECKED_OUT); //üũ�ƿ� ��Ŵ
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
							        			
//							        			else if(result.getLinkViewer() != null && result.getLinkViewer().equals("$BPM$")) { //BPM �Ӽ� �˻�
//						        					if(doc.getStatus()==SDocument.DOC_UNLOCKED) {
//														doc.setStatus(SDocument.DOC_CHECKED_OUT); //üũ�ƿ� ��Ŵ
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
	
		// 20131227, junsoo, �ӵ� ������ ���ؼ� grid �� content�� ǥ���ϰ�, context menu �̺�Ʈ ����
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
		
		// 20130730, junsoo, addRowContextClickHandler ��� �����. �ش���� �ʴ� �κ��� contextmenu ǥ�ø� ���ֱ� ����.
		// grid�� �� ������ ��Ŭ���� ��쿡 ����ؼ� �� ������ �ʿ���.
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
            	
            	// ���õǾ��ִ� Tab�� Search Tab�� ��� Search Tab�� Context Menu�� �����ش�.
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
		
	     // kimsoeun GS������ - �׸��� ��� ��Ŭ�� ����
		setShowHeaderContextMenu(false);
		
		/*
		 * Record ���� ����
		 */
		addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
			}
		});
		
		/**
		 * Ȯ�� �Ұ����� ������ Ȯ�� �����ϰ� �����ְ� �ֱ� ������ Ȯ�� ���� ������ �����ϰ� �Ͽ� 
		 * �̺�Ʈ ����� Ȯ�� ���θ� �缳�� �ϰ� �Ѵ�.
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
	 * 20130911, junsoo, ��� ���۰����ϵ��� �����Ͽ� �ּ�ó����
	 * ���� ��ġ�� ���� D&DȰ��ȭ ������ �����Ѵ�.
	 * ���� ���������� ���� �ȵǵ��� �Ǿ�����.
	 */
//	private void controlDrop(){
//		// My_DOC������ D&D �����ϰ���.(���ѹ���)
//		// Shared_DOC������ �޴��� ���� �̵� �� ����
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
	
	// 20140219, junsoo, smartgwt ���� ������ ����. ��ü ���� ������ getSelectedRecords�� ���� �۵� ���� ����.
	private int multiCalled = 0; 
	
	/**
	 * 20131211 na SelectionEvent�� �̿��Ͽ� ���� ������ ���ִ� �ڵ�
	 * 20130725, junsoo
	    document ���� ó��. observer���� �˷���. 
	 */
	public void onRowClick(SelectionEvent event) {
		
		// 20140221, �ٽ��ѹ� actiontype�� ����. �˻����� ��Ŭ���ϴ� �������� ��Ŭ���� �ٷ��ϸ� contextmenu�� ���������� �����Ƿ�
		if (!isSearchTab)
			DocumentActionUtil.get().setActivatedMenuType(currentMenuType);
		

		// 20140219, junsoo, smartgwt ����. ��ü ���� ������ getSelectedRecords�� ���� �۵� ���� ����.
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
			
			// �ϸ�ũ�� ��찡 ������ �� �� �����Ƿ�..
			SFolder folder = (SFolder)selected.getAttributeAsObject("folder");
			// 20140317, junsoo, 3�� ������ �Ѱ� ������ ��, event.getState() �� false�� �� �κ��� ���� �ȵ�
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
	 * Grid�� Field�� ���߾� �����͸� �����Ѵ�.
	 * @param record
	 * @param document
	 */
	public void setRecordData(ListGridRecord record, SDocument document) {
		// ���̵� ����
		record.setAttribute("id", document.getId());
		// chk
		record.setAttribute("chk", false);
		
		// ���� ���̵�
		record.setAttribute("folderid", document.getFolder().getId());
		
		// �� ���
		record.setAttribute("path", document.getPathExtended());

		// ���� ���� ���� ǥ��
		if (document.getContents().length < 1)
			record.setAttribute("attachs", "");
		else
			record.setAttribute("attachs", "+" + String.valueOf(document.getContents().length));
		
		// ���� ���ȷ��� ���뿩�� ǥ��
		// 20131205, junsoo, NONE �� ǥ�� �ϵ��� ��.
//		if (document.getSecurityProfile() != null && document.getSecurityProfile() != 0)
		if (document.getSecurityProfile() != null)
			record.setAttribute("docSecurity", "p7m");
		else
			record.setAttribute("docSecurity", "");
		
		// lock���� ����
		if(document.getStatus() == SDocument.DOC_LOCKED)
			record.setAttribute("lock", "lock");
		else if (document.getStatus() == SDocument.DOC_CHECKED_OUT)
			record.setAttribute("lock", "document_out");
		else
			record.setAttribute("lock", "");
		
		SContent[] scontents = document.getContents();
		
		// ������ validation
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
		//����Ÿ��Ʋ
		record.setAttribute("titlenm", document.getTitle());
		//���� Ÿ��
		record.setAttribute("doctype", document.getDocTypeName());
		record.setAttribute("doctypeId", document.getDocType());
		//����
		record.setAttribute("version", document.getVersion());
		//����
		record.setAttribute("owner", document.getCreateUserName());
		//���ʻ�������
		record.setAttribute("created", document.getCreationDate());
		//������������
		record.setAttribute("modified", document.getLastModified());
		
		//retention
//		if(document.getRetention() != null){
//			Map<String, String> dateMap = getExpireDate(document);
//			String retentionValue = dateMap.get("retentionDate")+" ("+dateMap.get("difference")+")";
//			record.setAttribute("retention", retentionValue);
//		}
		record.setAttribute("retention", Util.getFormattedExpireDate(document.getCreationDate(), document.getExpireDate()));
				
		//������������
		record.setAttribute("contents", document.getContents());
		record.setAttribute("templateId", document.getTemplateId());

		record.setAttribute("folder", document.getFolder());
		
		record.setAttribute("folderId", document.getFolder().getId());
		
		// 20130725, junsoo, sdocument ��ü�� ����
		record.setAttribute("document", document);

		// 20130906, taesu, �߰�
		if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED)
			record.setAttribute("rewriteCMD", document.getRewriteCMD());
	}
	
	public void setRecordDataEx(ListGridRecord record, SDocument document) {
		SExtendedAttribute[] attr = document.getAttributes();
		
		for (int i = 0; i < attr.length; i++) {
			if(attr[i].getPriority() != null){
				switch (attr[i].getEditor()) {
				case SExtendedAttribute.EDITOR_LISTBOX:
					//20140113na �ڵ� ���� ��� ���濡 ���� ����
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
	 * ��� �Ұ����� �޴����� ���� ��� ����� ����� 
	 * ���� ���°� ���� ��� �Ұ����� ������ �����̹Ƿ� ������ ������ �ѱ��.
	 * @param record
	 */
	private void controlDraftMenu(ListGridRecord record){
		// ���� ���������� �����ϰ� ��
		if(DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_SHARED)	return;
		
		if(record == null)	return;
		// ��� ���� ȹ��
		LinkedHashMap<Integer, String> DraftMap = DocumentActionUtil.get().getDraftMap();
		
		// ������ ����
		String rewriteCMD = record.getAttribute("rewriteCMD");
		
		if(rewriteCMD !=null){
			// �� ����
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
	 * ������ DraftButton Ȱ��/��Ȱ��ȭ�� �����Ѵ�. 
	 * @param enable
	 */
	private void controlToolbarDraftButton(boolean enable){
		DocumentActionUtil.get().controlDraftIcon(enable);
	}
	
	/**
	 * ������ ��� ������ ��� ��� �� ���� ��� ������ ����� �����ؼ� ��� �Ұ����� ����� ����� ��� ���� ���ÿ� �߰��Ѵ�.
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
	 * ���� ������ ���¿� ���� ���� ������ Draft Type�� �缳���Ѵ�. 
	 * @param docRight
	 * @return
	 */
	private String setDraftTypeByDocStatus(String docRight){
		SDocument doc = (SDocument)getSelectedRecord().getAttributeAsObject("document");
		int status = doc.getStatus();
		// ���� : DocumentsActionUtil
		// unlock ������ ��� check_in �Ұ���
		if(status == SDocument.DOC_UNLOCKED){
			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKIN), "");
		}
		if(status == SDocument.DOC_LOCKED){
			docRight = "";	// Lock ���´� ��� ����
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DELETE), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKIN), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_CHECKOUT), "");
//			docRight = docRight.replaceAll(String.valueOf(Constants.DRAFT_TYPE_DOWNLOAD), "");
		}
		// checked_out ������ ��� delete, check_out �Ұ���
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
	 * @param isNew	: gridPager �ʱ�ȭ ����(���ο� �˻��ÿ��� true)
	 */
	public void setSearchGridData(List<SDocument> records, PagingToolStrip gridPager, boolean isNew){
		showSearchGridRecord(gridPager, records, records.size(), isNew);
	}
	
	/**
	 * ÷�� ������ �����ٶ� ����Ѵ�.(����¡ ó�� �ȵ�)		 
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
			
			// 20131230na Ȯ��Ӽ� �ʾ� 
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
			
			// ������ ����
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
				// 20130730, Ư���� ������ ������ ù��° ����
				// 20130822, junsoo, �ε�� �ƹ��͵� �������� �ʵ��� ��.
//					if (data != null && data.size() > 0)
//						selectedDocument = data.get(0);
			}
			// ������ ������ ����.
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
			
			// 20130816, junsoo, select�� �������� �ؾ���.
			// 20130819, junsoo, ������ ù��° �� ���õǴ� ��� ����.
			if (selectRecordIndex != -1) {
				selectRecord(selectRecordIndex);
				expandRecord(getRecord(selectRecordIndex));
				
			}
		}else{
			// �˻��� �����Ͱ� ������� ������ �ʱ�ȭ
			setData(new ListGridRecord[] {});
			setGridPagerInfo(gridPager, 0 , 1);
//			getDraftRights();
		}
	}

	/**
	 *	GridPager�� �������� �����Ѵ�, TotalLength ��� X
	 * */
	public void setGridPagerInfo(PagingToolStrip gridPager, int recordLength, int pageNum){
		if(gridPager == null)	return;

		gridPager.setRespPageInfo(gridPager.isHavingNextPage(), pageNum);
	}
	
	/**
	 *	SearchTab���� ���õǾ��ִ� Tab(Personal, Shared)�� ���߾� �����͸� �׸��忡 �����ָ�, GridPager�� �����Ѵ�. 
	 *	@param gridPager
	 *	@param records
	 *	@param recordSize
	 *	@param isNew	: gridPager pageNum �ʱ�ȭ
	 * */
	private void showSearchGridRecord(PagingToolStrip gridPager, List<SDocument> records, int recordSize, boolean isNew){
		// gridPager�� pageSize�� �°� ������ ����
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
			// gridPager �ʱ�ȭ ����
			if(isNew)
				setGridPagerInfo(gridPager, recordSize, 1);
			else
				setGridPagerInfo(gridPager, recordSize, gridPager.getPageNum());
		}
	}
	
	/**
	 * Thumbnail ������ ȹ���Ѵ�.(Thumbnail ��ư ���ý� ����)
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
	 * 20131230na ����Ʈ�� �ѱ� ��
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
