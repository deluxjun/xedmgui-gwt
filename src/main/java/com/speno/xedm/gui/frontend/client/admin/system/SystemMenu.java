package com.speno.xedm.gui.frontend.client.admin.system;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.frontend.client.admin.AdminPanel;
import com.speno.xedm.gui.frontend.client.admin.system.audit.AuditManagementPanel;


/**
 * System Menu
 * 
 * @author 박상기
 * @since 1.0
 */
public class SystemMenu extends AdminSubMenu {	
	private Button general;
	private Button settings;
	private Button auditmanagement;
	private Button taskmanagement;
	private Button approvalmanagement;
	private Button notice;
	private Button delegation;
	private Button ecmsetting;
	

	public SystemMenu() {
		setMargin(10);
		setMembersMargin(5);

		general = new Button(I18N.message("general"));
		general.setWidth100();
		general.setHeight(25);
		general.hide();
//		addMember(general);
		
		settings = new Button(I18N.message("settings"));
		settings.setWidth100();
		settings.setHeight(25);
		settings.hide();
//		addMember(settings);

		auditmanagement = new Button(I18N.message("audit"));
		auditmanagement.setWidth100();
		auditmanagement.setHeight(25);
		auditmanagement.hide();
//		addMember(auditmanagement);
		
		taskmanagement = new Button(I18N.message("taskmanagement"));
		taskmanagement.setWidth100();
		taskmanagement.setHeight(25);
		taskmanagement.hide();
//		addMember(taskmanagement);
		
		approvalmanagement = new Button(I18N.message("approvalmanagement"));
		approvalmanagement.setWidth100();
		approvalmanagement.setHeight(25);
		approvalmanagement.hide();
		
		notice = new Button(I18N.message("notice"));
		notice.setWidth100();
		notice.setHeight(25);
		notice.hide();
		
		delegation = new Button(I18N.message("delegation"));
		delegation.setWidth100();
		delegation.setHeight(25);
		delegation.hide();

		ecmsetting = new Button(I18N.message("second.ecmsetting"));
		ecmsetting.setWidth100();
		ecmsetting.setHeight(25);
		ecmsetting.hide();

		initMenus(new Object[]{"general", general},
				// TODO : 20130823, junsoo, 좀더 수정한 후 사용 
//				new Object[]{"settings", settings},
				new Object[]{"audit", auditmanagement},
				new Object[]{"taskmanagement", taskmanagement},
				new Object[]{"approvalmanagement", approvalmanagement},
				new Object[]{"notice", notice},
				new Object[]{"delegation", delegation},
				new Object[]{"settings", ecmsetting}
		);

		addInformations();

	}

	private void addInformations() {
		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth(300);
		systemForm.setColWidths(1, "*");

		StaticTextItem productName = ItemFactory.newStaticTextItem2("productName", "", "<b>"
				+ Session.get().getInfo().getProductName() + "</b>");
		productName.setShouldSaveValue(false);
		productName.setWrapTitle(false);

		StaticTextItem version = ItemFactory.newStaticTextItem2("version", "", I18N.message("version") + " "
				+ Session.get().getInfo().getRelease());
		version.setShouldSaveValue(false);

		StaticTextItem vendor = ItemFactory.newStaticTextItem2("vendor", "", "&copy; "
				+ Session.get().getInfo().getVendor());
		vendor.setShouldSaveValue(false);

		DynamicForm supportForm = new DynamicForm();
		supportForm.setAlign(Alignment.LEFT);
		supportForm.setTitleOrientation(TitleOrientation.TOP);
		supportForm.setColWidths(1);
		supportForm.setWrapItemTitles(false);
		supportForm.setMargin(8);
		supportForm.setNumCols(1);

		LinkItem support = new LinkItem();
		support.setName(I18N.message("support"));
		support.setLinkTitle(Session.get().getInfo().getSupport());
		support.setValue("mailto:" + Session.get().getInfo().getSupport() + "?subject="
				+ Session.get().getInfo().getProductName() + " Support - ID("
				+ Session.get().getInfo().getInstallationId() + ")");
		support.setRequired(true);
		support.setShouldSaveValue(false);

		StaticTextItem installationID = ItemFactory.newStaticTextItem("installid", "installid", Session.get().getInfo()
				.getInstallationId());
		installationID.setRequired(true);
		installationID.setShouldSaveValue(false);
		installationID.setWrap(false);
		installationID.setWrapTitle(false);

		systemForm.setItems(productName, version, vendor);
		systemForm.setLayoutAlign(Alignment.CENTER);

		supportForm.setItems(support, installationID);

		addMember(systemForm);
		addMember(supportForm);
	}

	@Override
	public String getMenuRef() {
		return "admin;system";
	}
	
	@Override
	public void setContent(String title) {
		VLayout content = null;
		if ("general".equals(title)) {
			content = GeneralPanel.get();
		} else if ("settings".equals(title)) {
			content = SettingsPanel.get();
		} else if ("audit".equals(title)) {
			content = AuditManagementPanel.get();
		} else if ("taskmanagement".equals(title)) {
			content = TaskManagementPanel.get(null);
		} else if ("approvalmanagement".equals(title)) {	// goodbong
			content = ApprovalManagementPanel.get("All");
//			((ApprovalPanel) content).refresh("All");
		} else if ("notice".equals(title)) {
			content = NoticePanel.get();
		} else if ("delegation".equals(title)) {
			content = DelegationPanel.get();
		} else if ("settings".equals(title)) {
			content = EcmSettingsPanel.get();
		}
			
		if (content != null)
			AdminPanel.get().setContent(content);
	}


}