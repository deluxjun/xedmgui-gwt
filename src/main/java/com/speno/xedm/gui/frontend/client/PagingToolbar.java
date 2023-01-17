package com.speno.xedm.gui.frontend.client;


import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.SearchForm;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.DrawAreaChangedEvent;
import com.smartgwt.client.widgets.grid.events.DrawAreaChangedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;


public class PagingToolbar extends ToolStrip {

    private int pageSize = 20;
    private int pageNum = 1;
    private int cellHeight = 20;
    private int headerHeight = 22;
    
    private final ListGrid grid;

    private final Label totalLabel;
    private ToolStripButton firtButton;
    private ToolStripButton nextButton;
    private ToolStripButton previousButton;
    private ToolStripButton lastButton;
    private FloatItem pageItem;
    private FloatItem pageSizeItem;
    
	boolean deselect=true;
	
	public boolean isDeselect()
	{
		return deselect;
	}

	public void setDeselect(boolean deselect)
	{
		this.deselect = deselect;
	}

    public int getPageSize() {
            return pageSize;
    }

    public PagingToolbar(ListGrid listGrid, int pageSize) {
    	this(listGrid, pageSize, 20, 22);
    }

    public PagingToolbar(ListGrid listGrid, int pageSize, int cellHeight, int headerHeight) {
            this.grid = listGrid;
            this.pageSize = pageSize;

            grid.getDataSource().getDefaultParams();

            firtButton = new ToolStripButton("<<");
            firtButton.setWidth(30);
            firtButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                            goToPage(1);
                    }
            });

            previousButton = new ToolStripButton("<");
            previousButton.setWidth(30);
            previousButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                            goToPage(pageNum - 1);
                    }
            });

            final SearchForm pageform = new SearchForm();
            pageform.setNumCols(4);
            pageform.setWidth("30%");
            pageform.setAlign(Alignment.LEFT);
            pageItem = new FloatItem();
            // pageItem.setShowTitle(false);
            pageItem.setTitle("page");
            pageItem.setWidth(36);
            pageItem.setDefaultValue(1);
            pageItem.setTextAlign(Alignment.RIGHT);
            pageItem.addKeyDownHandler(new KeyDownHandler() {
                    public void onKeyDown(KeyDownEvent event) {
                            if (event.getKeyName().equals("Enter")) {
                                    try {
                                            goToPage(Integer.parseInt((String) pageItem.getValue()));
                                            pageItem.focusInItem();
                                    } catch (Exception e) {
                                    }
                            }
                    }
            });
            pageSizeItem = new FloatItem();
            pageSizeItem.setTitle("size");
            pageSizeItem.setWidth(32);
            pageSizeItem.setDefaultValue(this.pageSize);
            pageSizeItem.setTextAlign(Alignment.RIGHT);
          /* pageSizeItem.addKeyDownHandler(new KeyDownHandler() {
                    public void onKeyDown(KeyDownEvent event) {
                            if (event.getKeyName().equals("Enter")) {
                                    try {
                                            updatePageSize(Integer.parseInt((String) pageSizeItem.getValue()));
                                            goToPage(1);
                                            pageSizeItem.focusInItem();
                                    } catch (Exception e) {
                                    }
                            }
                    }
            });*/
            pageform.setItems(pageItem, pageSizeItem);

            nextButton = new ToolStripButton(">");
            nextButton.setWidth(30);
            nextButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                            goToPage(pageNum + 1);
                    }
            });

            lastButton = new ToolStripButton(">>");
            lastButton.setWidth(20);
            lastButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                            goToPage(getTotalPages());
                    }
            });

            totalLabel = new Label();
            totalLabel.setWrap(false);
            totalLabel.setWidth("30%");
            totalLabel.setAlign(Alignment.RIGHT);
            setHeight(25);
//            this.setWidth100();
            addSpacer(1);
            addMember(firtButton);
            addSpacer(1);
            addMember(previousButton);
            addSpacer(1);
            addMember(nextButton);
            addSpacer(1);
            addMember(lastButton);
            addSpacer(2);
            addMember(pageform);
            addFill();
            // addSeparator();
            addSpacer(2);
            addMember(totalLabel);
            addSpacer(6);

    		// --------------set grid height
            this.cellHeight = cellHeight;
            this.headerHeight = headerHeight;
            
    		grid.setCellHeight(cellHeight);
    		grid.setHeaderHeight(headerHeight);
    		
    		if(grid.getShowFilterEditor() != null && grid.getShowFilterEditor()) {
    			grid.setHeight(cellHeight * pageSize + headerHeight + grid.getFilterEditorHeight());
    		}
    		else {
    			grid.setHeight(cellHeight * pageSize + headerHeight);
    		}
    		
    		grid.setBodyOverflow(Overflow.HIDDEN);
    		grid.setDataPageSize(pageSize);
    		grid.setDrawAheadRatio(2.0f);
    		
    		grid.addDataArrivedHandler(new DataArrivedHandler()
    		{
    			public void onDataArrived(DataArrivedEvent event)
    			{
    				GWT.log("onDataArrived is called", null);
    				goToPage(pageNum);
    			}
    		});
    		
    		grid.addDrawAreaChangedHandler(new DrawAreaChangedHandler() {
				@Override
				public void onDrawAreaChanged(DrawAreaChangedEvent event) {
					System.out.println("changed");
					PagingToolbar.this.setWidth(grid.getWidth());
				}
			});
    				
//    		grid.addResizedHandler(new ResizedHandler(){
//    			@Override
//    			public void onResized(ResizedEvent event) {
//    				// TODO Auto-generated method stub
//    				
//    			}
//    		});


    }

    public void updatePageSize(int pageSize) {
            if (pageSize > 500) {
                    pageSize = 500;
            }
            if (pageSize < 1) {
                    pageSize = this.pageSize;
            }
            this.pageSize = pageSize;
            pageSizeItem.setValue(pageSize);
            
//    		grid.setHeight(cellHeight * pageSize + headerHeight);
    }

    public void goToPage(int pageNum) {
            // clamp to the end of the possible set of pages
            int pages = getTotalPages();
            if (pageNum > pages)
                    pageNum = pages;
            if (pageNum < 1)
                    pageNum = 1;
            
    		if (pageNum == this.pageNum)
    		{
    			updatePagerControls(pages);
    			return;
    		}

            this.pageNum = pageNum;
            
//            updatePage(pageNum);
            
    		updatePagerControls(pages);
    		if (deselect)
    			grid.deselectAllRecords();
    		int cellHeight = grid.getCellHeight();
    		int rowNum = (pageNum - 1) * pageSize;

    		// here: give extra 2 pixes. This is a hack. Otherwise,
    		// listGrid think the last row from previous page is visible although it
    		// is not visible
    		grid.scrollBodyTo(null, rowNum * cellHeight + 2);

    }

    private int getTotalPages() {
            int total = this.grid.getTotalRows();
            int pages = (int) Math.ceil(((float) total) / ((float) pageSize));
            // never return zero pages
            if (pages == 0)
                    pages = 1;
            return pages;
    }
    
	protected void updatePagerControls(int total)
	{
		// pageText.setValue(pageNum);
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
		if (pageNum == total)
		{
			if (!lastButton.getDisabled())
				lastButton.setDisabled(true);
			if (!nextButton.getDisabled())
				nextButton.setDisabled(true);
		} else
		{
			if (lastButton.getDisabled())
				lastButton.setDisabled(false);
			if (nextButton.getDisabled())
				nextButton.setDisabled(false);
		}
//		if (total >= 4)
//		{
//			if (!pageForm.getDisabled())
//				pageForm.setDisabled(false);
//		} else
//		{
//			if (pageForm.getDisabled())
//				pageForm.setDisabled(true);
//		}
		
		pageItem.setValue(pageNum);

		totalLabel.setContents("(" + pageNum + "/" + total + ")");

	}

//    public void updatePage(int pageNum) {
//            Criteria criteria = new Criteria();
//            criteria.addCriteria("limit", this.pageSize);
//            criteria.addCriteria("offset", (pageNum - 1) * this.pageSize);
//            final int displayPageNum = pageNum;
//            grid.fetchData(criteria, new DSCallback() {
//                    public void execute(DSResponse response, Object rawData,
//                                    DSRequest request) {
//                    	try {
//                            totalCount = response.getAttributeAsInt("totalCount");
//                            int recnum = XMLTools.selectObjects(rawData, "/").size();
//                            if (totalCount > 0) {
//
//                                    totalLabel.setContents("a&nbsp;b&nbsp;" + displayPageNum
//                                                    + "&nbsp;c&nbsp;&nbsp;"
//                                                    + ((displayPageNum - 1) * pageSize + 1)
//                                                    + "&nbsp;c&nbsp;"
//                                                    + ((displayPageNum - 1) * pageSize + recnum)
//                                                    + "&nbsp;d&nbsp;&nbsp;e&nbsp;" +getTotalPages()+"&nbsp;g&nbsp;"+ totalCount
//                                                    + "&nbsp;f&nbsp;");
//                                    pageItem.setValue(displayPageNum);
//                            } else {
//                                    totalLabel.setContents("no data");
//                            }
//                    	} catch (Exception e) {
//							e.printStackTrace();
//						}
//
//                    }
//            });
//    }
}

