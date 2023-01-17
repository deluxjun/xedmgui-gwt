package com.speno.xedm.gui.frontend.client.folder;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.HomePanel;

public class PropertiesPanel extends FolderDetailTab {
	private DynamicForm form = new DynamicForm();
	private ValuesManager vm = new ValuesManager();
	protected String strPath;
	
	private static PropertiesPanel instance;

	private StaticTextItem path; 
	private StaticTextItem name;
	private StaticTextItem description;
	private TextItem usedSpace;
	private TextItem totalSpace;
	private TextItem ownerUser;
	private TextItem folderId;
	private CheckboxItem isHomeUsable;
	private HLayout actHL;
			
	private Label lblProperties;
	
	public static PropertiesPanel get(SFolder folder) {
		if (instance == null)
			instance = new PropertiesPanel(folder);
		return instance;
	}
	
	public PropertiesPanel(SFolder folder) {
		super(folder);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
//		refresh();
	}
	
	private HLayout createGroupControl() {
		return null;
	}
	
	private void refresh() {
		vm = new ValuesManager();
		
		if (form != null)
			form.destroy();

		if (contains(form))
			removeChild(form);
		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		
		path = ItemFactory.newStaticTextItem("path", "path", folder.getPathExtended().replaceAll("/root", ""));
		path.setAlign(Alignment.LEFT);   
		path.setWrap(false);
		path.setCellStyle("propertyTitle");
		
		String[] split = folder.getPathExtended().split("/");
		name = ItemFactory.newStaticTextItem("foldername", I18N.message("foldername"), split[split.length -1] );
		name.setCanEdit(false);
		
		description = ItemFactory.newStaticTextItem("description", I18N.message("description"), folder.getDescription());
		description.setWidth(200);
		description.setCanEdit(false);
		
		// soeun 최대 용량, 현재 사용량 추가
		totalSpace = new TextItem("totalSpace",I18N.message("totalSpace"));
		usedSpace = new TextItem("usedSpace",I18N.message("usedSpace"));
		ownerUser = new TextItem("",I18N.message("uusername"));
		
		folderId = new TextItem("id",I18N.message("folderId"));
		folderId.hide();
		
		ButtonItem btnUpdateTotalSpace = new ButtonItem("updateTotalSpace", I18N.message("updateTotalSpace"));
		btnUpdateTotalSpace.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		
//		if(!I18N.message("noValue").equals(totalSpace.getValue().toString())) {
			btnUpdateTotalSpace.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(
						com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					
					long uSpace = I18N.message("noValue").equals(usedSpace.getValue().toString()) ? 0L : Long.parseLong(usedSpace.getValue().toString());
					long tSpace = I18N.message("noValue").equals(totalSpace.getValue().toString()) ? 0L : Long.parseLong(totalSpace.getValue().toString());
					
					// TODO Auto-generated method stub
					ServiceUtil.folder().updateSpace(Session.get().getSid(), 
							Long.parseLong(folderId.getValue().toString()), uSpace, tSpace, new AsyncCallback<Void>() {
						
						
						@Override
						public void onFailure(Throwable caught) {
							SCM.warn(caught);
						}
	
						@Override
						public void onSuccess(Void result) {
							SC.say(I18N.message("savecompleted"));
							
							if(folder.getType() == SFolder.TYPE_SHARED){
								DocumentsPanel.get().getMenu().getFolderDataRpc(DocumentsPanel.get().getMenu().SharedocTree, Constants.FOLDER_TYPE_SHARED);
							}
								
							if(folder.getType() == SFolder.TYPE_WORKSPACE) {
								DocumentsPanel.get().getMenu().getFolderDataRpc(DocumentsPanel.get().getMenu().MydocTree, Constants.FOLDER_TYPE_MYDOC);
							}							
						}
					});
				}
			});
//		} else {
//			SC.say(I18N.message("totalSpace.cannotSave"));
//		}
		
		ButtonItem btnUpdateUser = new ButtonItem("updateOwnerUser", I18N.message("updateOwnerUser"));
		btnUpdateUser.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		
		
		// soeun
		folderId.setValue(folder.getId());
		
		isHomeUsable = new CheckboxItem("homeUsable",I18N.message("homeUsable"));
		isHomeUsable.setCanEdit(false);
		if(folder.getType() == 1) {
			ServiceUtil.security().getUserbyFolder(Session.get().getSid(), folder.getId(), new AsyncCallback<SUser>() {
	
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					SCM.warn(caught);
				}
	
				@Override
				public void onSuccess(SUser result) {
					if(result!=null) {
						// TODO Auto-generated method stub
						isHomeUsable.setValue(result.isHomeUsable());
						ownerUser.setValue(result.getUserName());
					} else {
//						isHomeUsable.setValue(false);
						//ownerUser.setValue(I18N.message("noValue"));
						
					}
				}
			});
		}
		
		btnUpdateUser.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				// TODO Auto-generated method stub
				
				if(ownerUser.getValue() == null) {
					SC.say(I18N.message("youhavetoentername"));
					return;
				}
				
				long uSpace = I18N.message("noValue").equals(usedSpace.getValue().toString()) ? 0L : Long.parseLong(usedSpace.getValue().toString());
				long tSpace = I18N.message("noValue").equals(totalSpace.getValue().toString()) ? 0L : Long.parseLong(totalSpace.getValue().toString());
				
				ServiceUtil.folder().updateSpace(Session.get().getSid(), 
						folder.getId(), uSpace, tSpace, isHomeUsable.getValueAsBoolean(), ownerUser.getValueAsString(),  new AsyncCallback<Void>() {
					
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
					
					@Override
					public void onSuccess(Void result) {
						SC.say(I18N.message("savecompleted"));
					}
				});
			}
		});
		
		// 20150607, junsoo, set usable
		if (folder.getEnabled() != null)
			isHomeUsable.setValue(folder.getEnabled());
		else
			isHomeUsable.setValue(true);
		
		if(folder.getTotalSpace() != null) {
			totalSpace.setValue(folder.getTotalSpace());
			usedSpace.setValue(folder.getUsedSpace());
			
			// 내 문서 && 사용자일때 
			// 전체 사용량 수정 못하게, 버튼 두개 다 안보이게
			if(folder.getType() == 1 && Session.get().getHomeFolderId() != 4 && !Session.get().getUser().isManager()) {
				totalSpace.setCanEdit(false);
				ownerUser.hide();
				btnUpdateTotalSpace.hide();
				btnUpdateUser.hide();
			}
			
//			// 내 문서 && 매니저일때
			if(folder.getType() == 1 && Session.get().getUser().isManager()) {
				// 매니저 본인의 Home 폴더 일때
				if(folder.getId() == Session.get().getHomeFolderId()) {
					totalSpace.setCanEdit(false);
					btnUpdateTotalSpace.hide();
					ownerUser.hide();
					btnUpdateUser.hide();
				}
			}
			
			// 관리자일때 
			if(Session.get().getHomeFolderId() == 4) {
				// 자기 폴더 속성에 정보들 싹 다 안보이게
				if(folder.getId() == 4){
					btnUpdateTotalSpace.hide();
					btnUpdateUser.hide();
					usedSpace.hide();
					totalSpace.hide();
					ownerUser.hide();
					isHomeUsable.hide();
				}
			}
			
			// 공유 문서
			if(folder.getType() == 0) {
				ownerUser.hide();
				btnUpdateUser.hide();
				isHomeUsable.hide();
			}
			
			usedSpace.setCanEdit(false);
		} else {
			btnUpdateTotalSpace.hide();
			btnUpdateUser.hide();
			usedSpace.hide();
			totalSpace.hide();
			ownerUser.hide();
			isHomeUsable.hide();
		}
		
		form.setItems(path, name, description, usedSpace, totalSpace, btnUpdateTotalSpace, ownerUser, btnUpdateUser, isHomeUsable, folderId);
		
		lblProperties = ItemFactory.newLabelWithIcon(Util.imageUrl("folder.png"), I18N.message("properties"), true, 30, 10);
		lblProperties.setAlign(Alignment.LEFT);   

//		addMember(form);
		
		setMembers(lblProperties, form);
		
	}

	boolean validate() {
		vm.validate();
		if (folder.getId() != Constants.DOCUMENTS_FOLDERID) {
			folder.setName(vm.getValueAsString("name").replaceAll("/", ""));
			folder.setDescription(vm.getValueAsString("description"));
		}
		return !vm.hasErrors();
	}
	
	public void setFolder(SFolder folder){
		this.folder = folder;
		refresh();
	}

}