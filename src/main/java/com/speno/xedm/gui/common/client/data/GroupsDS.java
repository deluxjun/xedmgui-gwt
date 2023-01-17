package com.speno.xedm.gui.common.client.data;

import java.util.HashMap;

import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class GroupsDS extends DataSource {
	
	private static final String PREFIX = "GroupsDS";
	
	private static HashMap<String, GroupsDS> instanceMap = new HashMap<String, GroupsDS>();
	
	public static GroupsDS get(final String id) {
		if (instanceMap.get(id) == null) {
			new GroupsDS(id);
		}
		return instanceMap.get(id);
	}

	public GroupsDS(final String id) {
		GroupsDS oldDS = instanceMap.get(id);
		if(oldDS != null) {
			oldDS.destroy();
		}
		
		instanceMap.put(id, this);
		
		setID(PREFIX+id);
		setTitleField("name");
		setRecordXPath("/list/group");
		
		DataSourceTextField idField = new DataSourceTextField("id", I18N.message("id"));
		idField.setPrimaryKey(true);
		idField.setCanEdit(false);
		idField.setHidden(true);
		idField.setRequired(true);
		
		DataSourceTextField parentId = new DataSourceTextField("parentid", I18N.message("parentid"));
		parentId.setRequired(true);
		parentId.setForeignKey(PREFIX+id+".id");
		parentId.setRootValue(String.valueOf(Constants.ADMIN_ROOT));
		
		DataSourceTextField typeField = new DataSourceTextField("type", I18N.message("type"));
		DataSourceTextField nameField = new DataSourceTextField("name", I18N.message("name"));
		DataSourceTextField descriptionField = new DataSourceTextField("description", I18N.message("description"));
		DataSourceTextField idPathField = new DataSourceTextField("idpath", I18N.message("idpath"));
		DataSourceTextField pathField = new DataSourceTextField("path", I18N.message("path"));
		DataSourceTextField personsCountField = new DataSourceTextField("personsCount", I18N.message("personscount"));
		
		setFields(idField, parentId, typeField, nameField, descriptionField, idPathField, pathField, personsCountField);
		setDataURL("data/groups.xml?sid=" + Session.get().getSid() + "&type=" + id);
		setClientOnly(true);
	}
}