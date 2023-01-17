package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

/**
 * Code Management Grid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class CodeManagementGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, CodeManagementGridPanel> instanceMap = new HashMap<String, CodeManagementGridPanel>();
	
	private ListGrid grid;
	private PagingToolStrip gridPager;
	private DynamicForm searchForm, form;
	private HLayout actionHL;
	
	private String orderByField = "code";
	private HashMap<String, SortDir> orderDirMap = new HashMap<String, SortDir>();
	private SortDir orderDir = SortDir.ASC;
	private String code="";
	private IsNotChangedValidator validator = new IsNotChangedValidator();
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static CodeManagementGridPanel get(final String id, final String subTitle) {
		if (instanceMap.get(id) == null) {
			new CodeManagementGridPanel(id, subTitle);
		}
		return instanceMap.get(id);
	}
	
	/**
	 * Code Management Grid Panel 생성
	 * @param id
	 * @param subTitle
	 */
	public CodeManagementGridPanel(final String id, final String subTitle) {
		instanceMap.put(id, this);
		
		orderDirMap.put("desc", SortDir.DESC);
		orderDirMap.put("asc", SortDir.ASC);
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		
		/* Sub Title 생성 */
		Label subTitleLabel = new Label();
		subTitleLabel.setAutoHeight();   
		subTitleLabel.setAlign(Alignment.LEFT);   
		subTitleLabel.setValign(VerticalAlignment.CENTER);
		subTitleLabel.setStyleName("subTitle");
		subTitleLabel.setContents(I18N.message("codemanagement"));			
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		//grid.setDataSource(new DocumentCodeDS(DocumentCodeDS.TYPE_RETENTION));
		//grid.setAutoFetchData(true);
		grid.invalidateCache();
		
		ListGridField checkField = new ListGridField("check", I18N.message("check"));
    	checkField.setWidth(40);
    	checkField.setType(ListGridFieldType.BOOLEAN);
    	checkField.setCanEdit(true);
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);
		ListGridField codeField = new ListGridField("code", I18N.message("code"));
		ListGridField nameField = new ListGridField("name", I18N.message("name"));
		ListGridField valueField = new ListGridField("value", I18N.message("value"));
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		descriptionField.setWidth("*");
		
		grid.setFields(checkField, idField, codeField, nameField, valueField, descriptionField);
    	
		//record click event handler 정의--------------------------------------------------------------
		grid.addRecordClickHandler(new RecordClickHandler() {   
            public void onRecordClick(RecordClickEvent event) {
            	recordClickedProcess(event.getRecord());
            }   
        });
		
		//record 삭제 event handler 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				form.reset();
            	form.editRecord(record);
				SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeRemove(Long.parseLong(record.getAttribute("id")));
						}
					}
				});
				event.cancel();
			}
		});
		
		VLayout gridPanel = new VLayout();        
        //gridPanel.setAutoHeight();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);
        
        HLayout titlePanel = new HLayout();        
        //gridPanel.setAutoHeight();
        titlePanel.setWidth100();
        titlePanel.setMembersMargin(0);
        titlePanel.setMembers(subTitleLabel, createSearchForm());
        
        gridPanel.setMembers(titlePanel, grid);
		
        gridPager = new PagingToolStrip(grid, 20, true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        
        grid.setBodyOverflow(Overflow.SCROLL);
        
		VLayout pagerPanel = new VLayout();
		pagerPanel.setHeight100();
		pagerPanel.setHeight("210%");
		pagerPanel.setShowResizeBar(true);
		pagerPanel.setMembersMargin(1);
		pagerPanel.addMember(gridPanel);
		pagerPanel.addMember(gridPager);
		
		VLayout groupVL = new VLayout(5);
		groupVL.setMembers(pagerPanel, createFormVL(), createActHL());
		
        addMember(groupVL);        
        executeFetch();
	}
	
	/**
	 * 상단 검색 Form 생성
	 * @return
	 */
	private DynamicForm createSearchForm() {
		searchForm = new DynamicForm();
		searchForm.setWidth100();
		searchForm.setAlign(Alignment.RIGHT);
			
		searchForm.setMargin(4);
		searchForm.setNumCols(7);
		searchForm.setColWidths("*","1","1","1","1","1","1");
		
		SelectItem sortItem = new SelectItem("sort", I18N.message("sort"));
		//sortItem.setWidth("*");
		sortItem.setValueMap(createSortOpts());
		sortItem.setDefaultValue("code:asc");
		sortItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String val = (String)event.getValue();
				if(val != null) {
					String[] strArry = val.split(":");
					orderByField = strArry[0];
					orderDir = orderDirMap.get(strArry[1]);		
				}
			}
		});
		
		SpacerItem dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		TextItem searchItem = new TextItem("code", I18N.message("code"));
		searchItem.setWrapTitle(false);
		searchItem.setRequired(false);
//		searchItem.setLength(Constants.MAX_LEN_ID);
		searchItem.setValidators(new LengthValidator(searchItem, Constants.MAX_LEN_ID));
		searchItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if("Enter".equals(event.getKeyName())) {
					CodeManagementGridPanel.this.code = getCode();
					executeFetch();
				}	
			}
		});
		
		ButtonItem searchButton = new ButtonItem();        
		searchButton.setTitle(I18N.message("search"));
		searchButton.setAlign(Alignment.RIGHT);		
		searchButton.setStartRow(false);		
		searchButton.setEndRow(true);
		searchButton.setIcon("[SKIN]/actions/search.png");		
		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				CodeManagementGridPanel.this.code = getCode();
				executeFetch();
			}
        });
		
		searchForm.setItems(sortItem, dummyItem, searchItem, dummyItem, searchButton);
		return searchForm;
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {		
		TextItem idItem = new TextItem("id", I18N.message("id"));		
		idItem.disable();
		idItem.setCanEdit(false);
		idItem.setTooltip(I18N.message("generatedbyserver", idItem.getTitle()));
		
		TextItem codeItem = new TextItem("code", I18N.message("code"));
		codeItem.setWrapTitle(false);
		codeItem.setRequired(true);
//		codeItem.setLength(Constants.MAX_LEN_ID);
		codeItem.setValidators(new LengthValidator(codeItem, Constants.MAX_LEN_ID));
		
		TextItem nameItem = new TextItem("name", I18N.message("name"));
		nameItem.setWrapTitle(false);
		nameItem.setRequired(true);	
//		nameItem.setLength(Constants.MAX_LEN_NAME);
		nameItem.setValidators(new LengthValidator(nameItem, Constants.MAX_LEN_NAME));
		
		TextAreaItem descriptionItem = new TextAreaItem("description", I18N.message("description"));
		descriptionItem.setWrapTitle(false);
		//descriptionItem.setRowSpan(4);
		descriptionItem.setHeight("*");
//		descriptionItem.setLength(Constants.MAX_LEN_DESC);
		descriptionItem.setValidators(new LengthValidator(descriptionItem, Constants.MAX_LEN_DESC));
		
		TextItem valueItem = new TextItem("value", I18N.message("value"));
		valueItem.setWrapTitle(false);
		valueItem.setRequired(true);	
//		valueItem.setLength(Constants.MAX_LEN_NAME);
		valueItem.setValidators(new LengthValidator(valueItem, Constants.MAX_LEN_NAME));
		
		form = new DynamicForm();
		form.setWidth100();
		form.setMargin(4);
		/*
		form.setNumCols(4);
		form.setColWidths("1","1","1","*");
		form.setItems(idItem, descriptionItem, codeItem, nameItem, valueItem );
		*/
		form.setHeight100();
		form.setNumCols(2);
//		form.setColWidths("1","*");
		form.setTitleWidth(60);
		form.setItems(idItem, codeItem, nameItem, valueItem, descriptionItem);
		
		form.reset();
    	
    	VLayout formVL = new VLayout(50);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();    	
    	formVL.setHeight100();
    	
    	formVL.setOverflow(Overflow.SCROLL);
    	formVL.addMembers(form);
    	
    	return formVL;
	}
	
	/**
	 * Action Panel 생성
	 * @return
	 */
	private HLayout createActHL() {		
		Button btnRowsRemove = new Button(I18N.message("checkeddel"));
		btnRowsRemove.setIcon("[SKIN]/MultiUploadItem/icon_remove_files.png");		
		btnRowsRemove.setWidth(120);
		btnRowsRemove.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	executeRemove();
            }   
        });
		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	addNew();
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(form.getValue("id") == null) {
            		 if(form.validate()) {
            			 SC.confirm(I18N.message("wanttoadd"),  new BooleanCallback() {
         					@Override
         					public void execute(Boolean value) {
         						if(value != null && value) {
         							executeAdd();
         						}
         					}
         				});
            		 }
            	}
            	else {
            		 if(form.validate()) {
            			 SC.confirm(I18N.message("wanttoupdate"),  new BooleanCallback() {
          					@Override
          					public void execute(Boolean value) {
          						if(value != null && value) {
          							executeUpdate();
          						}
          					}
          				});
            		 }
            	}
            }   
        });
		
		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnRowsRemove, btnAddNew, btnSave);		
		return actionHL;
	}
	
	/**
	 * Add New 버튼의 클릭 이벤트 핸들러
	 */
	private void addNew() {
		form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
    	form.editNewRecord();
    	form.reset();
    	grid.deselectAllRecords();
	}
	
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
    	form.reset();
    	form.editRecord(record);
    	validator.setMap(form);
	}
	
	/**
	 * 검색 Form의 입력한 code값 반환
	 * @return
	 */
	private String getCode() {
		String code = (String)searchForm.getField("code").getValue();
		return (code == null) ? "" : code;		
	}
	
	/**
	 * 1(Default)페이지 조회
	 */
	private void executeFetch() {
		executeFetch(1, gridPager.getPageSize());
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{
		GWT.log("[ CodeManagementGridPanel executeFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"], code["+code+"]", null);
		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		config.setOrderByField(orderByField);
		config.setOrderDir(orderDir);
		
		ServiceUtil.documentcode().pagingCodesByLikeCode(Session.get().getSid(), code, config, new AsyncCallbackWithStatus<PagingResult<SCode>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(PagingResult<SCode> result) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화

				int totalLength = result.getTotalLength();
				List<SCode> data = result.getData();
				
				for (int j = 0; j < data.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", data.get(j).getId());
					record.setAttribute("code", data.get(j).getCode());
					record.setAttribute("name", data.get(j).getName());
					record.setAttribute("value", data.get(j).getValue());
					record.setAttribute("description", data.get(j).getDescription());
					grid.addData(record);
				}	
				
				if (data.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
				else {
					addNew();
				}
				
				GWT.log("[ CodeManagementGridPanel executeFetch ] totalLength["+totalLength+"]", null);					
				gridPager.setRespPageInfo(totalLength, pageNum);
				
			}
		});
	}
	
	private void executeAdd() {
		GWT.log("[ CodeManagementGridPanel executeAdd ]", null);
		
		SCode code = new SCode();
		code.setId(0L);
		code.setCode(form.getValueAsString("code"));
		code.setName(form.getValueAsString("name"));
		code.setValue(form.getValueAsString("value"));
		code.setDescription(form.getValueAsString("description"));		
		
		ServiceUtil.documentcode().saveCode(Session.get().getSid(), code, new AsyncCallbackWithStatus<SCode>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SCode result) {
				GWT.log("[ CodeManagementGridPanel executeAdd ] onSuccess. id["+result.getId()+"]", null);
				validator.setMap(form);
				SC.say(I18N.message("operationcompleted"), new BooleanCallback() {					
					@Override
					public void execute(Boolean value) {
						onPageDataReqeust(gridPager.getPageNum(), gridPager.getPageSize()); //재조회
					}
				});
			}
		});
	}
	
	private void executeUpdate() {
		GWT.log("[ CodeManagementGridPanel executeUpdate ]", null);
		
		SCode code = new SCode();
		code.setId(Long.parseLong(form.getValueAsString("id")));
		code.setCode(form.getValueAsString("code"));
		code.setName(form.getValueAsString("name"));
		code.setValue(form.getValueAsString("value"));
		code.setDescription(form.getValueAsString("description"));
		
		if(validator.check(form)){
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		ServiceUtil.documentcode().saveCode(Session.get().getSid(), code, new AsyncCallbackWithStatus<SCode>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SCode result) {
				GWT.log("[ CodeManagementGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);
				
				Record record = grid.getRecordList().find("id", result.getId());
				record.setAttribute("code",			result.getCode());
				record.setAttribute("name",		result.getName());
				record.setAttribute("value",			result.getValue());
				record.setAttribute("description",	result.getDescription());
				grid.refreshRow(grid.getRecordIndex(record));
				SC.say(I18N.message("operationcompleted"));
				validator.setMap(form);
			}
		});
	}	
	
	private void executeRemove(final long id)
	{
		GWT.log("[ CodeManagementGridPanel executeRemove ] id["+id+"]", null);
		
		long[] ids = new long[1];
		ids[0] = id;
		
		ServiceUtil.documentcode().deleteCode(Session.get().getSid(), ids, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(Void result) {
				GWT.log("[ CodeManagementGridPanel executeRemove ] onSuccess. id["+id+"]", null);
				
				SC.say(I18N.message("operationcompleted"), new BooleanCallback() {					
					@Override
					public void execute(Boolean value) {
						onPageDataReqeust(gridPager.getPageNum(), gridPager.getPageSize()); //재조회
					}
				});
			}
		});
	}
	
	private void executeRemove() {
		GWT.log("[ CodeManagementGridPanel executeRemove ]", null);
				
		RecordList recordList = grid.getRecordList();
		if( recordList.isEmpty() ) {
			SC.say(I18N.message("noitemstodelete"));
			return;
		}		
		
		final List<Long> removeList = new ArrayList<Long>();
		Record record;
		for(int j=0; j<recordList.getLength(); j++) {
			record = recordList.get(j);
			if( record.getAttributeAsBoolean("check") ) {
				removeList.add(Long.parseLong(record.getAttributeAsString("id")));
			}
		}
		
		if(removeList.isEmpty()) {
			SC.say(I18N.message("checkeditemsnotexist"));
			return;
		}
		SC.confirm(I18N.message("checkeddelete", String.valueOf(removeList.size())), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					long[] ids = new long[removeList.size()];
					for(int j=0; j<removeList.size(); j++) {
						ids[j] = removeList.get(j);
					}
					
					ServiceUtil.documentcode().deleteCode(Session.get().getSid(), ids, new AsyncCallbackWithStatus<Void>() {
						@Override
						public String getSuccessMessage() {
							return I18N.message("client.searchComplete");
						}
						@Override
						public String getProcessMessage() {
							return I18N.message("client.searchRequest");
						}
						@Override
						public void onFailureEvent(Throwable caught) {
							SCM.warn(caught);
						}
						@Override
						public void onSuccessEvent(Void result) {
							GWT.log("[ CodeManagementGridPanel executeRemove ] onSuccess.", null);
							
							SC.say(I18N.message("operationcompleted"), new BooleanCallback() {					
								@Override
								public void execute(Boolean value) {
									onPageDataReqeust(gridPager.getPageNum(), gridPager.getPageSize()); //재조회
								}
							});
						}
					});
				}
			}
		});
	}

	@Override
	public void onPageDataReqeust(final int pageNum, final int pageSize) {
		final String code = getCode();
		if(!code.equals(this.code)) {
			SC.confirm(I18N.message("conditionchangedrequery"),  new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					if(value != null && value) {
						CodeManagementGridPanel.this.code = code;
					}
					executeFetch(pageNum, pageSize);
				}
			});
		}
		else {
			executeFetch(pageNum, pageSize);
		}
	}
	
	ListGridField codeField = new ListGridField("code", I18N.message("code"));
	ListGridField nameField = new ListGridField("name", I18N.message("name"));
	ListGridField valueField = new ListGridField("value", I18N.message("value"));
	ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
	
	private LinkedHashMap<String, String> createSortOpts() {
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put("code:asc", I18N.message("code")+"(ASC)");		
		valueMap.put("name:asc", I18N.message("name")+"(ASC)");
		valueMap.put("value:asc", I18N.message("value")+"(ASC)");
		valueMap.put("description:asc", I18N.message("description")+"(ASC)");
		valueMap.put("code:desc", I18N.message("code")+"(DESC)");
		valueMap.put("name:desc", I18N.message("name")+"(DESC)");		
		valueMap.put("value:desc", I18N.message("value")+"(DESC)");		
		valueMap.put("description:desc", I18N.message("description")+"(DESC)");
		return valueMap;
	}
}