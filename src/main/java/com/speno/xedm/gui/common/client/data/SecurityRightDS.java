package com.speno.xedm.gui.common.client.data;

import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * SecurityRightDS
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public class SecurityRightDS extends DataSource {
	
	public SecurityRightDS(final long profileId) {
		setTitleField("name");
		setRecordXPath("/list/right");
		
		DataSourceTextField entityIdField = new DataSourceTextField("entityId", I18N.message("id"));
		entityIdField.setPrimaryKey(true);
		entityIdField.setHidden(true);
		
		DataSourceTextField nameField = new DataSourceTextField("entityName", I18N.message("name"));
		DataSourceBooleanField readField = new DataSourceBooleanField("read", I18N.message("read"));
		DataSourceBooleanField wiriteField = new DataSourceBooleanField("write", I18N.message("write"));
		DataSourceBooleanField addField = new DataSourceBooleanField("add", I18N.message("add"));
		DataSourceBooleanField deleteField = new DataSourceBooleanField("pdelete", I18N.message("delete"));
		DataSourceBooleanField updateField = new DataSourceBooleanField("rename", I18N.message("update"));
		DataSourceBooleanField downloadField = new DataSourceBooleanField("download", I18N.message("download"));
		DataSourceBooleanField viewField = new DataSourceBooleanField("view", I18N.message("view"));
		DataSourceBooleanField printField = new DataSourceBooleanField("print", I18N.message("print"));
		DataSourceBooleanField checkoutField = new DataSourceBooleanField("check", I18N.message("checkout"));
		
		DataSourceIntegerField typeField = new DataSourceIntegerField("type", I18N.message("type"));
		typeField.setHidden(true);

		setFields(typeField, entityIdField, nameField, readField, wiriteField, addField, deleteField, updateField, downloadField, viewField, printField, checkoutField );
		setDataURL("data/rights.xml?sid=" + Session.get().getSid() + "&folderId=" + profileId+"&type="+SRight.TYPE_SECURITYPROFILE);
		setClientOnly(true);
	}	
}