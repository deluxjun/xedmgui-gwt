package com.speno.xedm.gui.frontend.client.admin;

import java.io.Serializable;
import java.util.HashMap;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.frontend.client.admin.documentcode.DocumentTypeGridPanel;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupGridPanel;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupTreePanel;
import com.speno.xedm.gui.frontend.client.admin.organization.UserGridPanel;

/**
 * ���� �˾�â (DUTY, POSITION, GROUP, USER, DOCTYPE) 
 * @author �ڻ��
 * @since 1.0
 */
public class CommSearchDialog extends Window implements RecordObserver, CellDoubleClickHandler {	
	public final static int DUTY			= SRight.GROUPTYPE_DUTY;	
	public final static int POSITION		= SRight.GROUPTYPE_POSITION;
	public final static int GROUP			= SRight.GROUPTYPE_GROUP;
	public final static int USERGROUP		= SRight.GROUPTYPE_USERGROUP;
	public final static int TYPE			= 90;
	//�׳� SRight.GROUPTYPE_DUTY... ���� Constants�� ������ ������..
	
	/**
	 * ���õ� ������� ���� Handler Interface ���� 
	 * @author �ڻ��
	 * @since 1.0
	 */
	public static interface ResultHandler {
        void onSelected(HashMap<String, String> resultMap);
    }
	
	private ListGrid grid;	
	private ResultHandler handler;
	
	/**
	 * ���õ� ������� ���� handler ���
	 */
	public void addResultHandler(ResultHandler  handler) {
		this.handler = handler;
	}
	
	@Override
	public void onCellDoubleClick(CellDoubleClickEvent event) {
		event.cancel();
		saveData();
	}
	
	public CommSearchDialog(int type) {		
		this(type, "", I18N.message("searchgroup"));
	}
	
	public CommSearchDialog(int type, String name) {
		this(type, "", name);
	}
	
	public CommSearchDialog(int type, Object name) {
		this(type, name, I18N.message("searchgroup"));
	}
	
	/**
	 * type�� ���� ���� �˾�â ����
	 * @param type
	 * @param name
	 * @param title
	 */
	public CommSearchDialog(int type, Object name, String title) {		
		setWidth(483);
        setHeight(550);
		
        setTitle(title);   
        setShowMinimizeButton(false);   
        setIsModal(true);   
        setShowModalMask(true);
        setCanDragResize(true);
        centerInPage();
        
        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();   
			}
		});
        
        VLayout vlayout = new VLayout();
        vlayout.setWidth100();
//        vlayout.setHeight100();
//        vlayout.setWidth(453);
        vlayout.setHeight(480);
        vlayout.setMargin(5);
        
        
        switch(type) {
        	case DUTY:
        		vlayout.addMember(createDutyGrid());
        		break;
        	case POSITION:
        		vlayout.addMember(createPosGrid());
        		break;
        	case GROUP:
        		vlayout.addMember(createGrpGrid());
        		grid.addCellDoubleClickHandler(this);
//        		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
//        			@Override
//        			public void onCellDoubleClick(CellDoubleClickEvent event) {
//        				// TODO Auto-generated method stub
//        				saveData();
//        			}
//        		});
        		break;
        	case USERGROUP:
        		vlayout.addMember(cretateUserVL());
        		break;
        	case TYPE:
        		vlayout.addMember(cretateDocTypeVL());
        		break;
        }
        vlayout.redraw();
        
        addItem(vlayout);
        
        DynamicForm actForm = new DynamicForm();   
        actForm.setHeight(30);   
        actForm.setWidth100();   
        actForm.setPadding(5);
        actForm.setMargin(5);
        actForm.setShowEdges(false);
        actForm.setAlign(Alignment.CENTER);
        actForm.setColWidths("50%", "50%");
        
        ButtonItem btnOk = new ButtonItem();
        btnOk.setAlign(Alignment.RIGHT);
        btnOk.setTitle(I18N.message("ok"));
        btnOk.setWidth(100);
        btnOk.setStartRow(false);
        btnOk.setEndRow(false);
        btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}
        });
        
        ButtonItem btnCancel = new ButtonItem();
        btnCancel.setAlign(Alignment.LEFT);
        btnCancel.setTitle(I18N.message("cancel"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false);
        btnCancel.setEndRow(false);
        btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
        });   
        
        actForm.setItems(btnOk, btnCancel);
        addItem(actForm);
	}

	/**
	 * Duty Grid Panel ����
	 * @return
	 */
	private GroupGridPanel createDutyGrid() {		
		GroupGridPanel panel = new GroupGridPanel("admin.search.duty", SGroup.TYPE_DUTY, null, this, false, "100%");
		grid = panel.getGrid();
		grid.setSelectionType(SelectionStyle.SINGLE);
		return panel;
	}
	
	/**
	 * Position Grid Panel ����
	 * @return
	 */
	private GroupGridPanel createPosGrid() {
		GroupGridPanel panel = new GroupGridPanel("admin.search.pos", SGroup.TYPE_POSITION, null, this, false, "100%");
		grid = panel.getGrid();
		grid.setSelectionType(SelectionStyle.SINGLE);
		return panel;
	}
	
	/**
	 * Group Tree Panel ����
	 * @return
	 */
	private GroupTreePanel createGrpGrid() {
		GroupTreePanel panel = new GroupTreePanel("admin.search.group", null, false, false, false);
		grid = panel.getGroupTree();
		grid.setSelectionType(SelectionStyle.SINGLE);
		return panel;
	}
	
	/**
	 * User Grid Panel ����
	 * @return
	 */
	private UserGridPanel cretateUserVL() {
		UserGridPanel panel = new UserGridPanel("admin.search.user", this, null, true, false, false, "100%");
		grid = panel.getGrid();
		grid.setSelectionType(SelectionStyle.SINGLE);
		return panel;
	}
	
	/**
	 * DocType Grid Panel ����
	 * @return
	 */
	private DocumentTypeGridPanel cretateDocTypeVL() {
		DocumentTypeGridPanel panel = new DocumentTypeGridPanel("admin.search.docType", null, this, false);
		grid = panel.getGrid();
		grid.setSelectionType(SelectionStyle.SINGLE);
		return panel;
	}
	
	/**
	 * ���õ� Record�� HashMap�� �ڵ鷯�� ����
	 * @param record
	 */
	private void commonResultControl(Record record) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("id", record.getAttributeAsString("id"));
		resultMap.put("name", record.getAttributeAsString("name"));
		resultMap.put("description", record.getAttributeAsString("description"));
		handler.onSelected(resultMap);
	}

	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onRecordDoubleClick(Record record) {
		if(handler != null && record != null) {
			commonResultControl(record);
		}
		destroy();
	}

	@Override
	public void onRecordSelected(Record record) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
	
	private void saveData(){
		ListGridRecord record = grid.getSelectedRecord();
		if(handler != null && record != null) {
			/* ROOT�� ����Ʈ�� ������ �ʵ��� �����Ǿ����Ƿ� �Ʒ� ���� ���ʿ�.
			if(CommSearchDialog.this.type == CommSearchDialog.GROUP) {
				if(String.valueOf(Constants.ADMIN_ROOT).equals(record.getAttributeAsString("parentid"))) {
					SC.say("You will not be able to select the root.");
					return;
				}
			}
			*/					
			commonResultControl(record);
		}
		clear();
		//20131210na  tree������ ���� destory() ���� ���� �߻�
//		destroy();
	}
}