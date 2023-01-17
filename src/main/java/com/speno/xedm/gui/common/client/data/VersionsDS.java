package com.speno.xedm.gui.common.client.data;

import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class VersionsDS extends DataSource {
	public VersionsDS(Long docId, Long archiveId, int max) {
		setRecordXPath("/list/version");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField event = new DataSourceTextField("event");
		DataSourceTextField version = new DataSourceTextField("version");
		DataSourceTextField fileVersion = new DataSourceTextField("fileVersion");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField comment = new DataSourceTextField("comment");
		DataSourceTextField docid = new DataSourceTextField("docid");
		DataSourceTextField customid = new DataSourceTextField("customid");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceFloatField size = new DataSourceFloatField("size");
		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField template = new DataSourceTextField("template");
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField creationdate = new DataSourceTextField("creationdate");
		DataSourceTextField retentiondate = new DataSourceTextField("retentiondate");
		
		setFields(id, user, event, version, fileVersion, date, comment, docid, customid, title, type, size, icon, template, creationdate, retentiondate);
		setClientOnly(true);
		setDataURL("data/versions.xml?sid=" + Session.get().getSid()
				+ (docId != null ? "&docId=" + docId : "&archiveId=" + archiveId) + "&locale=" + I18N.getLocale()
				+ "&max=" + max);
	}
}