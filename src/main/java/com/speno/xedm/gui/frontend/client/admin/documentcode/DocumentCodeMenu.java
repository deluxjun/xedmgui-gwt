package com.speno.xedm.gui.frontend.client.admin.documentcode;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.frontend.client.admin.AdminPanel;

/**
 * DocumentCode Menu
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public class DocumentCodeMenu extends AdminSubMenu{	
	private Button documentType;
	private Button fileType;
	private Button retentionProfile;
	private Button codeManagement;
	private Button lifeCycleProfile;
	private Button states;
	private Button templates;
	private Button filterProfile;


	public DocumentCodeMenu() {
		setMargin(10);
		setMembersMargin(5);

		documentType = new Button(I18N.message("documenttype"));
		documentType.setWidth100();
		documentType.setHeight(25);
		documentType.hide();

		fileType = new Button(I18N.message("filetype"));
		fileType.setWidth100();
		fileType.setHeight(25);
		fileType.hide();

		retentionProfile = new Button(I18N.message("retentionprofile"));
		retentionProfile.setWidth100();
		retentionProfile.setHeight(25);
		retentionProfile.hide();
		
		codeManagement = new Button(I18N.message("codemanagement"));
		codeManagement.setWidth100();
		codeManagement.setHeight(25);
		codeManagement.hide();
		
		lifeCycleProfile = new Button(I18N.message("second.lifecycleprofile"));
		lifeCycleProfile.setWidth100();
		lifeCycleProfile.setHeight(25);
		lifeCycleProfile.hide();
		
		states = new Button(I18N.message("states"));
		states.setWidth100();
		states.setHeight(25);
		states.hide();
		
		templates = new Button(I18N.message("template"));
		templates.setWidth100();
		templates.setHeight(25);
		templates.hide();
		
		filterProfile = new Button(I18N.message("second.filterprofile"));
		filterProfile.setWidth100();
		filterProfile.setHeight(25);
		filterProfile.hide();
	
		initMenus(new Object[]{"documenttype", documentType},
				new Object[]{"filetype", fileType},
				new Object[]{"retention", retentionProfile},
				new Object[]{"codemanagement", codeManagement},
				new Object[]{"lifecycle", lifeCycleProfile},
				new Object[]{"states", states},
				new Object[]{"templates", templates},
				new Object[]{"filterprofile", filterProfile});
		
		

	}

	@Override
	public String getMenuRef() {
		return "admin;documentcode";
	}

	@Override
	public void setContent(String title) {
		VLayout content = null;
		if ("documenttype".equals(title)) {
			content = DocumentTypePanel.get();
		} else if ("filetype".equals(title)) {
			content = FileTypePanel.get();
		} else if ("retention".equals(title)) {
			content = RetentionProfilePanel.get();
		} else if ("codemanagement".equals(title)) {
			content = CodeManagementPanel.get();
		} else if ("lifecycle".equals(title)) {
			content = LifeCycleProfilePanel.get();
		} else if ("states".equals(title)) {
			content = StatesPanel.get();
		} else if ("templates".equals(title)) {
			content = TemplatesPanel.get();
		} else if ("filterprofile".equals(title)) {
			content = FilterProfilePanel.get();
		}
			
		if (content != null)
			AdminPanel.get().setContent(content);
	}


}