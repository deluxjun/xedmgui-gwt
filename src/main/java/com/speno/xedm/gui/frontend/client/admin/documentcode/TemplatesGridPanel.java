package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.HashMap;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * TemplatesGrid Panel
 * @author 남윤성
 * @since 1.0
 */
public class TemplatesGridPanel extends VLayout{
	private static HashMap<String, TemplatesGridPanel> instanceMap = new HashMap<String, TemplatesGridPanel>();
	
	private ListGrid grid;
	private DynamicForm form;
	
	private TemplatesExtGridPanel extPanel; 
	private RecordObserver recordObserver;	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static TemplatesGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isCanDrag, final boolean isAction, final TemplatesExtGridPanel panel, final String width) {
		if (instanceMap.get(id) == null) {
			new TemplatesGridPanel(id, subTitle, ob, isCanDrag, isAction, panel, width);
		}
		return instanceMap.get(id);
	}
	
	public TemplatesGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isCanDrag, final boolean isAction, final TemplatesExtGridPanel panel, final String width) {
		instanceMap.put(id, this);	
		
		this.recordObserver = ob;
		this.extPanel = panel;
		
		if(subTitle != null) {
			/* Sub Title 생성 */
			Label subTitleLable = new Label();
			subTitleLable.setAutoHeight();   
	        subTitleLable.setAlign(Alignment.LEFT);   
	        subTitleLable.setValign(VerticalAlignment.CENTER);
	        subTitleLable.setStyleName("subTitle");
	        subTitleLable.setContents(subTitle);
	        addMember(subTitleLable);
		}
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(isAction);
		grid.invalidateCache();
		
		
		grid.setSelectionType(isAction ? SelectionStyle.SINGLE : SelectionStyle.MULTIPLE);        
        grid.setCanReorderRecords(!isAction);
        grid.setCanAcceptDroppedRecords(!isAction);
        grid.setCanDragRecordsOut(!isAction);  
    	
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("id"));
		ListGridField typeNmField = new ListGridField("name", I18N.message("name"));
		ListGridField descField = new ListGridField("description", I18N.message("description"));
		typeIdField.setHidden(true);
		
		grid.setFields(typeIdField, typeNmField,descField);
		
        //record dbclick event handler 정의------------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(event.getRecord());
				}
			}   
        });
		
		if(isAction) {			
			//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {   
	            public void onRecordClick(RecordClickEvent event) {
	            	recordClickedProcess(event.getRecord());
	            	if(recordObserver != null) {				
						recordObserver.onRecordSelected(grid.getSelectedRecord());
	        		}
	            }   
	            
	        });
			
			//record 삭제 event handler 정의--------------------------------------------------------------
			grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							final ListGridRecord record = grid.getRecord( event.getRowNum());
							extPanel.getForm1().reset();
							extPanel.getForm1().editRecord(record);
							if(value != null && value) {
								executeRemove(Long.parseLong(record.getAttribute("id")));
							}
						}
					});
					event.cancel();
				}
			});		
			
		}
		
		VLayout docTypeVL = new VLayout(5);
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		docTypeVL.addMember(grid);
        addMember(docTypeVL);
        setHeight("50%");
        setWidth100();
        
        executeFetch();
	}
	
	/**
	 * grid 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * form 가져오기
	 */
	public DynamicForm getForm() {
		return form;
	}
	
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		if(form != null) {
			form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
	    	form.reset();
	    	form.editRecord(record);
		}
	}
	
	private void executeFetch()	{				
		Log.debug("[ TemplatesGridPanel executeFetch ]");
		
		ServiceUtil.template().getTemplates(Session.get().getSid(), new AsyncCallbackWithStatus<STemplate[]>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(STemplate[] result) {
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());	
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
					if(recordObserver != null) {				
						recordObserver.onRecordSelected(grid.getSelectedRecord());
	        		}
				}
				Log.debug("TemplatesGridPanel executeFetch ] result.size()["+result.length +"]");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}			
		});
	}
	
	private void executeRemove(final long id)
	{
		Log.debug("[ TemplatesGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		ServiceUtil.template().delete(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onSuccessEvent(Void result) {
				Log.debug("[ TemplatesGridPanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				extPanel.getForm1().editNewRecord();
				extPanel.getForm1().reset();
				extPanel.resetGridForm2("all");
				SC.say(I18N.message("operationcompleted"));
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
}