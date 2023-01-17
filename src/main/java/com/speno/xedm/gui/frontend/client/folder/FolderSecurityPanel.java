package com.speno.xedm.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class FolderSecurityPanel extends HLayout {

	private ListGrid list;

	private VLayout container = new VLayout();

	private static FolderSecurityPanel instance;

	private ListGridField groupType;
	private ListGridField aclNmField;
	private ListGridField add;
	private ListGridField view;
	private ListGridField read;
	private ListGridField write;
	private ListGridField rename;
    private ListGridField pdelete;
	private ListGridField check;		
    private ListGridField download;
    //20150509na GS인증을 위한 프린트 제거
//    private ListGridField print;
    private ListGridField extend;
    private ListGridField control;
    
    private Label securityACLName;
    
    
    private ListGridRecord[] records; 
    private LinkedHashMap<Integer, String> opts;
    private SFolder folder;
    
	public static FolderSecurityPanel get() {
		if (instance == null)
			instance = new FolderSecurityPanel();
		else{
			instance.destroy();
			instance = new FolderSecurityPanel();
		}
		
		return instance;
	}
	
	public FolderSecurityPanel() {
		
		container.setMembersMargin(3);
		addMember(container);

		securityACLName = new Label();
		securityACLName.setTitle(I18N.message("securityprofile"));
		securityACLName.setAutoHeight();
		
		groupType = new ListGridField("groupType");
		groupType.setCanEdit(false);
		groupType.setHidden(true);
		
		aclNmField = new ListGridField("name", I18N.message("name"), 150);
		aclNmField.setCanEdit(false);
		
		add = new ListGridField("add", I18N.message("add"), 100);
		add.setType(ListGridFieldType.BOOLEAN);
		add.setCanEdit(false);
		
		view = new ListGridField("view", I18N.message("view"), 100);
		view.setType(ListGridFieldType.BOOLEAN);
		view.setCanEdit(false);
		
		read = new ListGridField("read", I18N.message("read"), 100);
		read.setType(ListGridFieldType.BOOLEAN);
		read.setCanEdit(false);
		
		write = new ListGridField("write", I18N.message("write"), 100);
		write.setType(ListGridFieldType.BOOLEAN);
		write.setCanEdit(false);
		
		rename = new ListGridField("rename", I18N.message("update"), 100);
		rename.setType(ListGridFieldType.BOOLEAN);
		rename.setCanEdit(false);
		
		pdelete = new ListGridField("pdelete", I18N.message("delete"), 100);
		pdelete.setType(ListGridFieldType.BOOLEAN);
		pdelete.setCanEdit(false);
		
		check = new ListGridField("check", I18N.message("checkin") + "/" + I18N.message("checkout"), 100);
		check.setType(ListGridFieldType.BOOLEAN);
		check.setCanEdit(false);		
		
		download = new ListGridField("download", I18N.message("download"), 100);
		download.setType(ListGridFieldType.BOOLEAN);
		download.setCanEdit(false);
		
		//20150509na GS인증을 위한 프린트 제거
//		print = new ListGridField("print", I18N.message("print"), 100);
//		print.setType(ListGridFieldType.BOOLEAN);
//		print.setCanEdit(false);

		extend = new ListGridField("extend", I18N.message("retention"), 100);
		extend.setType(ListGridFieldType.BOOLEAN);
		extend.setCanEdit(false);

		control = new ListGridField("control", I18N.message("control"), 100);
		control.setType(ListGridFieldType.BOOLEAN);
		control.setCanEdit(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		
		list.setGroupStartOpen(GroupStartOpen.ALL);
		list.setGroupByField("groupType");
		
		List<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(groupType);
		fields.add(aclNmField);
		fields.add(add);
		fields.add(view);
		fields.add(read);
		fields.add(write);
		fields.add(rename);
		fields.add(pdelete);
		fields.add(check);
		fields.add(download);
		//20150509na GS인증을 위한 프린트 제거
//		fields.add(print);
		fields.add(extend);
		fields.add(control);
		
		list.setFields(fields.toArray(new ListGridField[0]));
//		if(container.hasMember(container)) container.removeMember(list);
		container.addMember(securityACLName);
		container.addMember(list);
	}
	
	public void setSecurityReason(String reason){
		String content = "";
		content = securityACLName.getContents();
		securityACLName.setContents(content + "  " + reason);
	}
	
	public void execute(SFolder folder){
		this.folder = folder;
		
		ServiceUtil.security().listRightsByFolderId(Session.get().getSid(), folder.getId(), new AsyncCallback<List<SRight>>() {
			@Override
			public void onSuccess(List<SRight> result) {
				setData(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	public void setData(final List<SRight> result) {
		ServiceUtil.security().getSecurityProfile(Session.get().getSid(), folder.getSecurityProfileId(), false, new AsyncCallback<SSecurityProfile>() {
			@Override
			public void onSuccess(SSecurityProfile profile) {
				if(profile == null){
					setSecurityReason("<b>" + I18N.message("securityprofile") + ": </b>" + "Inherited-ACL");
				}else{
					setSecurityReason("<b>" + I18N.message("securityprofile") + ": </b>" + profile.getName());
				}
				
				if(result.get(0).getExpiredday() != null){
					setSecurityReason("&nbsp;&nbsp;&nbsp;<b>" + I18N.message("expirationDate")+ ": </b>" + Util.getFormattedDate(result.get(0).getExpiredday(), false));
				}else{
					setSecurityReason("&nbsp;&nbsp;&nbsp;<b>" + I18N.message("expirationDate")+ ": </b>" + I18N.message("notspecified"));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		opts = new LinkedHashMap<Integer, String>();
		ServiceUtil.getAllGroupType(new ReturnHandler<LinkedHashMap<Integer,String>>() {
			@Override
			public void onReturn(LinkedHashMap<Integer, String> param) {
				opts = param;
			}
		});
		
		records = new ListGridRecord[result.size()+1];
		
		if(result.size() == 0)	return;
		
		records[0] =new ListGridRecord(); 
		records[0] = setButtonPermission(folder)[0];
		
		//20131129 na 권한 관련 리스트 수정
		//records[0]는 내 권한 // recordes[1] 그 이후는 권한 받은 사용자 목록
		for (int j = 0; j < result.size(); j++) {
			records[j+1] =new ListGridRecord();
			records[j+1].setAttribute("groupType",	 I18N.message(opts.get(result.get(j).getGroupType()).toLowerCase()));
			records[j+1].setAttribute("name",			result.get(j).getName());
			records[j+1].setAttribute("view",			result.get(j).isView());
			//20150509na GS인증을 위한 프린트 제거
//			records[j+1].setAttribute("print",		result.get(j).isPrint());
			records[j+1].setAttribute("read",			result.get(j).isRead());
			records[j+1].setAttribute("write",		result.get(j).isWrite());
			records[j+1].setAttribute("add",			result.get(j).isAdd());
			records[j+1].setAttribute("rename",		result.get(j).isRename());
			records[j+1].setAttribute("pdelete",		result.get(j).isDelete());					
			records[j+1].setAttribute("check",		result.get(j).isCheck());			
			records[j+1].setAttribute("download",		result.get(j).isDownload());
			records[j+1].setAttribute("extend",		result.get(j).isExtend());
			records[j+1].setAttribute("control",		result.get(j).isControl());
			
			System.out.println(result.get(j));
		}
		
		list.setRecords(records);
	}
	
	private ListGridRecord[] setButtonPermission(SFolder folder){
		String[] permis = folder.getPermissions();
		ListGridRecord record[] = new ListGridRecord[1];
		record[0] = new ListGridRecord();
		
		if(folder.getType() == Constants.FOLDER_TYPE_MYDOC){
		}else{
			for(String str : permis){
				if("add".equals(str)){ 
					record[0].setAttribute("add", true);
				}
			
				if("delete".equals(str)){
					record[0].setAttribute("pdelete", true);
				}
				
				if("download".equals(str)){
					record[0].setAttribute("download", true);
				}
	
				if("check".equals(str)){
					record[0].setAttribute("check", true);
				}

				if("view".equals(str)) 		record[0].setAttribute("view", true);
				if("read".equals(str)) 		record[0].setAttribute("read", true);
				if("write".equals(str)) 	record[0].setAttribute("write", true);
				if("rename".equals(str))	record[0].setAttribute("rename", true);
				//20150509na GS인증을 위한 프린트 제거
//				if("print".equals(str))		record[0].setAttribute("print", true);
				if("extend".equals(str))	record[0].setAttribute("extend", true);
				if("control".equals(str))	record[0].setAttribute("control", true);
			}
		}
		
		record[0].setAttribute("groupType", I18N.message("myRights"));
		record[0].setAttribute("name",		Session.get().getUser().getName());
		
		return record;
	}
	
	public void refresh(ListGridRecord[] record){
		list.setData(record);
	}
	
	public void setFolder(SFolder folder){
		execute(folder);
	}
	
}