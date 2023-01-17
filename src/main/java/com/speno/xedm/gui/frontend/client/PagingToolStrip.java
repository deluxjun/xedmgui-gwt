package com.speno.xedm.gui.frontend.client;


import java.util.LinkedHashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.SearchForm;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.frontend.client.document.DocumentsListPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

public class PagingToolStrip extends ToolStrip {

    public  int pageSize = 20;
    private int pageNum = 1;
    private int cellHeight = 20;
    private int headerHeight = 22;
    
    private final ListGrid grid;

    // 20130820 taesu, 총 페이지 수 제거..
//    private final Label totalLabel = new Label();
    private ToolStripButton firtButton;
    private ToolStripButton nextButton;
    private ToolStripButton previousButton;
    // 마지막 페이지로 이동 제거
//    private ToolStripButton lastButton;
    
    private SelectItem securityProfileItem;
    private LinkedHashMap<Integer, String> opts;
    
    private FloatItem pageItem;
    private SelectItem pageSizeItem;
    
	boolean deselect=true;
	
	//----------------------------------------------
	private float totalCount = 0;
	private boolean isAutoHeightGrid = false;	
	private int maxPageSize = 500;	
	private PagingObserver observer;
	private PagingObserverOrderBy observerOrderBy;
	private boolean usingTotalCount = true;
	
	private boolean havingNextPage = false;
	//----------------------------------------------
	
	private String orderBy;
	private SortDir orderDir;
	private boolean doOrder;
	
	//시간 확인을 위한 변수 :: 육용수 20140306
	//long use_time = System.currentTimeMillis();
	
	public boolean isHavingNextPage() {
		if (totalCount == PagingResult.NOLIMIT_TOTAL)
			return havingNextPage;
		else {
			if (pageNum >= totalCount)
				return false;
			else
				return true;
		}
			
	}

	public void setHavingNextPage(boolean havingNextPage) {
		this.havingNextPage = havingNextPage;
	}

	public SortDir getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(SortDir orderDir) {
		this.orderDir = orderDir;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isDeselect()
	{
		return deselect;
	}

	public void setDeselect(boolean deselect)
	{
		this.deselect = deselect;
	}

	
	public boolean getIsAutoHeightGrid() {
		return isAutoHeightGrid;
	}
	
	public void setIsAutoHeightGrid(boolean isAutoHeightGrid) {
		this.isAutoHeightGrid = isAutoHeightGrid;
	}
	
    public int getPageSize() {
    	try {
    		pageSize = Integer.parseInt((String) pageSizeItem.getValue());
        }
    	catch (Exception e) {}
        return pageSize;
    }
    
	public void setPageNum(int pageNum) {
		pageItem.setValue(pageNum);
		this.pageNum = pageNum;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		pageSizeItem.setValue(pageSize);
	}

	public void setMaxPageSize(int  maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
	
    public int geMaxPageSize() {
        return maxPageSize;
    }
    
    public int getPageNum() { 
    	try {
    		pageNum = Integer.parseInt((String) pageItem.getValue());
        }
    	catch (Exception e) {
        }
    	return pageNum;
    }
    
    public PagingToolStrip(ListGrid listGrid, int pageSize, boolean usingTotalCount, PagingObserver observer ) {
    	this(listGrid, pageSize, usingTotalCount, observer, 20, 22);
    }
    public PagingToolStrip(ListGrid listGrid, int pageSize, boolean usingTotalCount, PagingObserverOrderBy observer, boolean doOrder) {
    	this(listGrid, pageSize, usingTotalCount, observer, 20, 22, doOrder);
    	this.doOrder = doOrder;
    	this.pageSizeItem.setValue(DocumentsListPanel.pagingSize);
    
    	
    }
    public PagingToolStrip(ListGrid listGrid, int pageSize, boolean usingTotalCount, PagingObserver observer, int cellHeight, int headerHeight) {
    	this.grid = listGrid;
    	this.pageSize = pageSize;
    	this.usingTotalCount = usingTotalCount;
    	this.observer = observer;
    	
		// --------------set grid height
        this.cellHeight = cellHeight;
        this.headerHeight = headerHeight;
        
    	init();	
    }
    public PagingToolStrip(ListGrid listGrid, int pageSize, boolean usingTotalCount, PagingObserverOrderBy observer, int cellHeight, int headerHeight, boolean doOrder) {
    	this.grid = listGrid;
    	this.pageSize = pageSize;
    	this.usingTotalCount = usingTotalCount;
    	this.observerOrderBy = observer;
    	
		// --------------set grid height
        this.cellHeight = cellHeight;
        this.headerHeight = headerHeight;
        
    	init();
    }
    
    //육용수 1.5초마다 버튼이 동작되도록 막음 20140304
    private void init(){
    	final long interval = 1500;
        firtButton = new ToolStripButton("<<");
        firtButton.setWidth(30);
        firtButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	//if(System.currentTimeMillis() - use_time > interval){
            		goToPage(1);
            		//use_time = System.currentTimeMillis();
            	            	
            	//else
            //SC.warn(I18N.message("notallowdrift "));
            }
        });
        firtButton.setDisabled(true);

        previousButton = new ToolStripButton("<");
        previousButton.setWidth(30);
        previousButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	//if(System.currentTimeMillis() - use_time > interval){
                goToPage(pageNum - 1);
                //use_time = System.currentTimeMillis();
            	
            	//else
            	//	SC.warn(I18N.message("notallowdrift "));
            }
        });
        previousButton.setDisabled(true);
        
        nextButton = new ToolStripButton(">");
        nextButton.setWidth(30);
        nextButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	//if(System.currentTimeMillis() - use_time > interval){
                goToPage(pageNum + 1);
               // use_time = System.currentTimeMillis();
            
              // 	else
               //		SC.warn(I18N.message("notallowdrift "));
            }
        });
        nextButton.setDisabled(true);

//        if( usingTotalCount ) {
//	        lastButton = new ToolStripButton(">>");
//	        lastButton.setWidth(20);
//	        lastButton.addClickHandler(new ClickHandler() {
//	            public void onClick(ClickEvent event) {
//	                goToPage(getTotalPages());
//	            }
//	        });
//        }
        
        final SearchForm pageform = new SearchForm();
        pageform.setNumCols(4);
        pageform.setWidth("30%");
        pageform.setAlign(Alignment.LEFT);

        pageItem = new FloatItem();
        pageItem.setTitle(I18N.message("page"));
        pageItem.setWidth(36);
        pageItem.setDefaultValue(1);
        pageItem.setTextAlign(Alignment.RIGHT);
        pageItem.setWrapTitle(false);
        pageItem.setLength(Session.get().getInfo().getIntConfig("gui.page.fieldsize", 5));
//        pageItem.setValidators(new LengthValidator(pageItem, Session.get().getInfo().getIntConfig("gui.page.fieldsize", 5)));
        pageItem.setKeyPressFilter("[0-9.]");
        
        
        /*pageItem.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
            	try {
            		setPageNum(Integer.parseInt((String) pageItem.getValue()));
                if (event.getKeyName().equals("Enter")) {
                        goToPage(pageNum);
                        pageItem.focusInItem();
                    }
                }
            	catch (Exception e) {
                }
                
            }
        });*/
        
        pageSizeItem = new SelectItem();
        pageSizeItem.setTitle(I18N.message("size"));
        pageSizeItem.setWidth(50);
        pageSizeItem.setType("combobox");
		//pageSizeItem.setValueMap(opts);
        pageSizeItem.setDefaultValue(pageSize);
        pageSizeItem.setValueMap("10", "20", "30", "40", "50");
      //  pageSizeItem.setMultipleValueSeparator();
        pageSizeItem.setTextAlign(Alignment.RIGHT);
        pageSizeItem.setWrapTitle(false);
        pageSizeItem.setRequired(true);
        pageSizeItem.addChangedHandler(new ChangedHandler() {			
			@Override
			public void onChanged(ChangedEvent event) {
				// TODO Auto-generated method stub
				updatePageSize(Integer.parseInt((String) pageSizeItem.getValue()));
				goToPage(1);
				// goToPage(pageNum);
                 pageSizeItem.focusInItem();
			}
		});
//        pageSizeItem.setLength(Session.get().getInfo().getIntConfig("gui.pagesize.fieldsize", 10));
        
        //pageSizeItem.setValidators(new LengthValidator(pageSizeItem, Session.get().getInfo().getIntConfig("gui.pagesize.fieldsize", 10)));
        //-------pageSizeItem.setKeyPressFilter("[0-9.]");

        //        pageSizeItem.setDisabled(true);
        /*pageSizeItem.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                try {
                	updatePageSize(Integer.parseInt((String) pageSizeItem.getValue()));
                	if (event.getKeyName().equals("Enter")) {
	                    goToPage(pageNum);
	                    pageSizeItem.focusInItem();
                	}} catch (Exception e) {
                }
            }
        });*/
        pageform.setItems(pageItem, pageSizeItem);
        //pageSizeItem.setDisabled(true);
        pageItem.setDisabled(true);

        setHeight(25);
        //this.setWidth100();
        addSpacer(1);
        addMember(firtButton);
        addSpacer(1);
        addMember(previousButton);
        addSpacer(1);
        addMember(nextButton);
//        if( usingTotalCount ) {
//        	addSpacer(1);
//	        addMember(lastButton);
//        }
        addSpacer(2);
        addMember(pageform);
        addFill();
        //addSeparator();
        if( usingTotalCount ) {
	        addSpacer(2);
	        // Total Paging값 제거.
//	        totalLabel.setWrap(false);
//	        totalLabel.setWidth("30%");
//	        totalLabel.setAlign(Alignment.RIGHT);
//	        addMember(totalLabel);
	        addSpacer(6);
        }
        
		// --------------set grid height
//        this.cellHeight = cellHeight;
//        this.headerHeight = headerHeight;
        
		grid.setCellHeight(cellHeight);
		grid.setHeaderHeight(headerHeight);
		
		if(grid.getShowFilterEditor() != null && grid.getShowFilterEditor()) {
			grid.setHeight(cellHeight * pageSize + headerHeight + grid.getFilterEditorHeight());
		}
		else {
			grid.setHeight(cellHeight * pageSize + headerHeight);
		}
		
//		grid.setBodyOverflow(Overflow.HIDDEN);
		grid.setDataPageSize(pageSize);
		grid.setDrawAheadRatio(2.0f);		
		
		
    }

    public void updatePageSize(int pageSize) {
    	if (pageSize > maxPageSize) {
    		pageSize = maxPageSize;
    	}
    	if (pageSize < 1) {
    		pageSize = this.pageSize;
    	}
    	
        this.pageSize = pageSize;
        DocumentsListPanel.pagingSize = this.pageSize;
        
        pageSizeItem.setValue(pageSize);
        
        if(isAutoHeightGrid) {
        	if(grid.getShowFilterEditor() != null && grid.getShowFilterEditor()) {
    			grid.setHeight(cellHeight * pageSize + headerHeight + grid.getFilterEditorHeight());
    		}
    		else {
    			grid.setHeight(cellHeight * pageSize + headerHeight);
    		}
        }
    }    


    public void goToPage(int pageNum) {
    	if (pageNum < 1) {
            pageNum = 1;
    	}
    	
//    	if( usingTotalCount ) {
//    		int totalPages = getTotalPages();
//    		if (pageNum > totalPages) {
//    			pageNum = totalPages;
//    		}
//    	}
    	
    	setPageNum(pageNum);
    	
    	if(doOrder){
    		if(observerOrderBy != null)
    			observerOrderBy.onPageDataReqeust(pageNum, pageSize, orderBy+"/"+orderDir);
    	}else{
    		if(observer != null) {
    			observer.onPageDataReqeust(pageNum, pageSize);
    		}
    	}
    }
    
    /**
     * TotalCount를 사용하는 그리드에서의 Response 함수로 사용할 것.
     * @param totalCount
     * @param pageNum
     */
    public void setRespPageInfo(float totalCount, int pageNum) {
    	this.totalCount = totalCount;
    	
    	if (totalCount == PagingResult.NOLIMIT_TOTAL) {
    		setRespPageInfo(isHavingNextPage(), pageNum);
    		return;
    	}
    	
    	int totalPages = getTotalPages();    	 
    	this.pageNum = pageNum;            
		updatePagerControls(totalPages);
		if (deselect) {
			grid.deselectAllRecords();
		}
		int cellHeight = grid.getCellHeight();
		int rowNum = (pageNum - 1) * pageSize;
		// here: give extra 2 pixes. This is a hack. Otherwise,
		// listGrid think the last row from previous page is visible although it
		// is not visible
		grid.scrollBodyTo(null, rowNum * cellHeight + 2);		
    }
    
    /**
     * TotalCount를 사용하지 않는 그리드에서의 Response 함수로 사용할 것.
     * @param dataSize
     * @param pageNum
     */
    public void setRespPageInfo(boolean isExistData, int pageNum) {    	
    	this.pageNum = pageNum;            
		updatePagerControls(isExistData);
		
		if (deselect) {
			grid.deselectAllRecords();
		}
		int cellHeight = grid.getCellHeight();
		int rowNum = (pageNum - 1) * pageSize;

		// here: give extra 2 pixes. This is a hack. Otherwise,
		// listGrid think the last row from previous page is visible although it
		// is not visible
		grid.scrollBodyTo(null, rowNum * cellHeight + 2);
    }
    
    // 20140220, junsoo, 페이징 공통화
    public void updatePageStatus(boolean hasNext, int totalCount, int pageNum) {
    	setHavingNextPage(hasNext);
    	setRespPageInfo(totalCount, pageNum);
    }
    
    private int getTotalPages() {
            int totalPages = (int) Math.ceil(((float) totalCount) / ((float) pageSize));
            if (totalPages == 0)
            	totalPages = 1;
            return totalPages;
    }
    
	protected void updatePagerControls(int total)
	{
		if (pageNum == 1)
		{
			if (!firtButton.getDisabled())
				firtButton.setDisabled(true);
			if (!previousButton.getDisabled())
				previousButton.setDisabled(true);
		} else
		{
			if (firtButton.getDisabled())
				firtButton.setDisabled(false);
			if (previousButton.getDisabled())
				previousButton.setDisabled(false);
		}
		if (pageNum >= total)
		{
//			if (!lastButton.getDisabled())
//				lastButton.setDisabled(true);
			if (!nextButton.getDisabled())
				nextButton.setDisabled(true);
		} else
		{
//			if (lastButton.getDisabled())
//				lastButton.setDisabled(false);
			if (nextButton.getDisabled())
				nextButton.setDisabled(false);
		}		
		if (total == 0){
//			if (!lastButton.getDisabled())
//				lastButton.setDisabled(true);
			if (!nextButton.getDisabled())
				nextButton.setDisabled(true);
			if (!firtButton.getDisabled())
				firtButton.setDisabled(true);
			if (!previousButton.getDisabled())
				previousButton.setDisabled(true);
		}
		pageItem.setValue(pageNum);
//		totalLabel.setContents("(" + pageNum + "/" + total + ")");
	}
	
	public void updatePagerControls(boolean isExistData)
	{
		if(isExistData){
			if (nextButton.getDisabled())
				nextButton.setDisabled(false);
		} else
		{
			if (!nextButton.getDisabled())
				nextButton.setDisabled(true);
		}
		if (pageNum == 1)
		{
			if (!firtButton.getDisabled())
				firtButton.setDisabled(true);
			if (!previousButton.getDisabled())
				previousButton.setDisabled(true);
		} else
		{
			if (firtButton.getDisabled())
				firtButton.setDisabled(false);
			if (previousButton.getDisabled())
				previousButton.setDisabled(false);
		}
//		if (!isExistData)
//		{
//			if (!nextButton.getDisabled())
//				nextButton.setDisabled(true);
//		} else
//		{
//			if (nextButton.getDisabled())
//				nextButton.setDisabled(false);
//		}		
		pageItem.setValue(pageNum);
	}
	
	
	public static PagingConfig getPagingConfig(int pageNum, int pageSize) {
		int totalLength = 0;
		int startRow = (pageNum - 1) * pageSize + 1;		
		return new PagingConfig(totalLength, startRow, pageSize);
	}
	public static PagingConfig getPagingConfig(int pageNum, int pageSize, String orderBy, SortDir orderDir) {
		int totalLength = 0;
		int startRow = (pageNum - 1) * pageSize + 1;	
		return new PagingConfig(totalLength, startRow, pageSize, orderBy, orderDir);
	}
}

