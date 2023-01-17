package com.speno.xedm.gui.frontend.client.folder;


import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;

public class FolderDetailsPanel extends VLayout {
	private SFolder folder;
	private Layout propertiesTabPanel;
	private Layout securityTabPanel;
	
	private Tab securityTab;
	private Tab propertiesTab;
	
	private PropertiesPanel propertiesPanel;
	private FolderSecurityPanel securityPanel;
	
	public TabSet tabSet = new TabSet();
	private ListGridRecord[] records;
	private static FolderDetailsPanel instance;

	public static FolderDetailsPanel get(SFolder folder) {
		if (instance == null)
			instance = new FolderDetailsPanel(folder);
		return instance;
	}
	
	public FolderDetailsPanel(SFolder folder) {
		super();
		this.folder = folder;
		
		init();
	}

	public FolderDetailsPanel() {
		super();
		
		init();
	}
	
	private void init(){
		setHeight100();
		setWidth100();
		setMembersMargin(10);

		tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight100();
		
		prepareProperty();
		
		addMember(tabSet);
	}

	private void prepareSecurity(){
		securityTab = new Tab(I18N.message("security"));
		securityTabPanel = new HLayout();
		securityTabPanel.setWidth100();
		securityTabPanel.setHeight100();
		securityTab.setPane(securityTabPanel);
		tabSet.addTab(securityTab);
	}
	
	private void prepareProperty(){
		propertiesTab = new Tab(I18N.message("properties"));
		propertiesTabPanel = new HLayout();
		propertiesTabPanel.setWidth100();
		propertiesTabPanel.setHeight100();
		propertiesTab.setPane(propertiesTabPanel);
		tabSet.addTab(propertiesTab);
	}
	
	private void refreshProperty() {
		try {
			if(propertiesPanel != null)
				if( propertiesTabPanel.contains(propertiesPanel)) propertiesTabPanel.removeMember(propertiesPanel);
				
			propertiesPanel = PropertiesPanel.get(folder);
			propertiesTabPanel.addMember(propertiesPanel);
			propertiesPanel.setFolder(folder);
			
		} catch (Throwable r) {
			SCM.warn(r);
		}
	}
	
	private void refresh() {
		try {
			if(propertiesPanel != null)
				if( propertiesTabPanel.contains(propertiesPanel)) propertiesTabPanel.removeMember(propertiesPanel);
				
			propertiesPanel = PropertiesPanel.get(folder);
			propertiesTabPanel.addMember(propertiesPanel);
			propertiesPanel.setFolder(folder);
			
			if (securityPanel != null)
				if (securityTabPanel.contains(securityPanel)) securityTabPanel.removeMember(securityPanel);

			securityPanel = FolderSecurityPanel.get();
			securityTabPanel.addMember(securityPanel);					
			securityPanel.setFolder(folder);
			
		} catch (Throwable r) {
			SCM.warn(r);
		}
	}

	public SFolder getFolder() {
		return folder;
	}

	public void setFolder(SFolder folder, boolean shared, ListGridRecord[] record) {
		this.folder = folder;
		
		if(!shared){
			refreshProperty();
		}else{
			if(tabSet.getTabs().length == 1) prepareSecurity();
			refresh();
		}
	}

	public void removeTab(){
		if(tabSet.getTabs().length > 1) tabSet.removeTab(tabSet.getTabs().length -1);
	}
	
//	public void execute(){
//		ServiceUtil.security().listRightsByFolderId(Session.get().getSid(), folder.getId(), new AsyncCallback<List<SRight>>() {
//			@Override
//			public void onSuccess(List<SRight> result) {
//				setData(result);
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.serverError(caught, false);
//			}
//		});
//	}
//	private LinkedHashMap<Integer, String> opts;
//	public void setData(List<SRight> result) {
//		opts = new LinkedHashMap<Integer, String>();
//		ServiceUtil.getAllGroupType(new ReturnHandler<LinkedHashMap<Integer,String>>() {
//			@Override
//			public void onReturn(LinkedHashMap<Integer, String> param) {
//				opts = param;
//			}
//		});
//		
//		records = new ListGridRecord[result.size()];
//		
//		records[0] =new ListGridRecord(); 
//		records[0] = setButtonPermission(folder)[0];
//		
//		for (int j = 1; j < result.size(); j++) {
//			records[j] =new ListGridRecord();
//			records[j].setAttribute("groupType",	opts.get(result.get(j).getGroupType()));
//			records[j].setAttribute("name",			result.get(j).getName());
//			records[j].setAttribute("view",			result.get(j).isView());
//			records[j].setAttribute("print",		result.get(j).isPrint());
//			records[j].setAttribute("read",			result.get(j).isRead());
//			records[j].setAttribute("write",		result.get(j).isWrite());
//			records[j].setAttribute("add",			result.get(j).isAdd());
//			records[j].setAttribute("rename",		result.get(j).isRename());
//			records[j].setAttribute("pdelete",		result.get(j).isDelete());					
//			records[j].setAttribute("check",		result.get(j).isCheck());			
//			records[j].setAttribute("download",		result.get(j).isDownload());
//			records[j].setAttribute("extend",		result.get(j).isExtend());
//			records[j].setAttribute("control",		result.get(j).isControl());
//		}
//	}
	
	// 20130729, junsoo
	public void refreshFolder(SFolder folder){
		if (folder != null && this.folder != null && folder.getId() == this.folder.getId())
			return;
			
		boolean shared = false;
		if (DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED)
			shared = true;
		
		this.folder = folder;
//		execute();
		// get record
//		ListGridRecord[] records = setButtonPermission(folder);
		
		setFolder(folder, shared, records);
	}

//	public ListGridRecord[] setButtonPermission(SFolder folder){
//		String[] permis = folder.getPermissions();
//		ListGridRecord record[] = new ListGridRecord[1];
//		record[0] = new ListGridRecord();
//		
//		if(folder.getType() == Constants.FOLDER_TYPE_MYDOC){
//		}else{
//			for(String str : permis){
//				if("add".equals(str)){ 
//					record[0].setAttribute("add", true);
//				}
//			
//				if("delete".equals(str)){
//					record[0].setAttribute("pdelete", true);
//				}
//				
//				if("download".equals(str)){
//					record[0].setAttribute("download", true);
//				}
//	
//				if("check".equals(str)){
//					record[0].setAttribute("check", true);
//				}
//
//				if("view".equals(str)) 		record[0].setAttribute("view", true);
//				if("read".equals(str)) 		record[0].setAttribute("read", true);
//				if("write".equals(str)) 	record[0].setAttribute("write", true);
//				if("rename".equals(str))	record[0].setAttribute("rename", true);
//				if("print".equals(str))		record[0].setAttribute("print", true);
//				if("extend".equals(str))	record[0].setAttribute("extend", true);
//				if("control".equals(str))	record[0].setAttribute("control", true);
//			}
//		}
//		
//		record[0].setAttribute("groupType", I18N.message("myRights"));
//		record[0].setAttribute("name",		Session.get().getUser().getName());
//		
//		return record;
//	}

}