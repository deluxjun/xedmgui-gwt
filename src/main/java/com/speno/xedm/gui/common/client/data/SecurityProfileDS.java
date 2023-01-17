package com.speno.xedm.gui.common.client.data;

import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * SecurityProfileDS
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public class SecurityProfileDS extends DataSource {
	
	public SecurityProfileDS() {
		setTitleField("name");
		setRecordXPath("/list/securityprofile");
		
		
		DataSourceTextField idField = new DataSourceTextField("id", I18N.message("id"));
		idField.setPrimaryKey(true);
		
		DataSourceTextField descField = new DataSourceTextField("description", I18N.message("description"));

		setFields(idField, descField);
		setDataURL("data/codes.xml?sid=" + Session.get().getSid() + "&type=securityprofile");
		setClientOnly(true);
	}
}