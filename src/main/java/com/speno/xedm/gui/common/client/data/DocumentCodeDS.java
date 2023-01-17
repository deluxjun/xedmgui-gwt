package com.speno.xedm.gui.common.client.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * DocumentCodeDS
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public class DocumentCodeDS extends DataSource {
	
	public static final String  TYPE_DOC = "doctype";
	public static final String  TYPE_FILE = "filetype";

	public DocumentCodeDS(final String type) {
		setTitleField("name");
		setRecordXPath("/list/doctype");
		
		if(TYPE_DOC.equals(type)) {
			DataSourceTextField typeIdField = new DataSourceTextField("id", I18N.message("typeid"));
			typeIdField.setPrimaryKey(true);
				
			DataSourceTextField typeNmField = new DataSourceTextField("name", I18N.message("typename"));
			
			DataSourceTextField descField = new DataSourceTextField("description", I18N.message("description"));
			
			DataSourceTextField retentionIdField = new DataSourceTextField("retentionId", I18N.message("retentionId"));
			retentionIdField.setHidden(true);
			
			DataSourceTextField retentionNmField = new DataSourceTextField("retentionname", I18N.message("retentionname"));
			retentionNmField.setHidden(true);
			
			DataSourceTextField retentionPeriodField = new DataSourceTextField("retentionperiod", I18N.message("retention"));
			
			DataSourceTextField eClassIdField = new DataSourceTextField("eclassid", I18N.message("eclassid"));
			eClassIdField.setHidden(true);
			
			DataSourceTextField cClassIdField = new DataSourceTextField("cclassid", I18N.message("cclassid"));
			cClassIdField.setHidden(true);
			
			DataSourceTextField indexIdField = new DataSourceTextField("indexid", I18N.message("indexid"));
			indexIdField.setHidden(true);
			
			DataSourceTextField uClassIdField = new DataSourceTextField("uclassid", I18N.message("uclassid"));
			uClassIdField.setHidden(true);

			setFields(typeIdField, typeNmField, descField, retentionIdField, retentionNmField, retentionPeriodField, eClassIdField, cClassIdField, indexIdField, uClassIdField);
//			System.out.println("data/codes.xml?sid=" + Session.get().getSid() + "&type=" + type);
			setDataURL("data/codes.xml?sid=" + Session.get().getSid() + "&type=" + type);
			setClientOnly(true);
		}
		else if(TYPE_FILE.equals(type)) {
			DataSourceTextField typeIdField = new DataSourceTextField("id", I18N.message("typeid"));
			typeIdField.setPrimaryKey(true);
			typeIdField.setHidden(true);
				
			DataSourceTextField typeNmField = new DataSourceTextField("name", I18N.message("filetype"));
			
			LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
			opts.put("10", "10 MB");
			opts.put("20", "20 MB");
			opts.put("50", "50 MB");
			opts.put("100", "100 MB");
			opts.put("1000", "1 GB");
			opts.put("0", "Not Limited");			
			DataSourceTextField sizeField = new DataSourceTextField("size", I18N.message("availablefilesize"));
			sizeField.setValueMap(opts);
			
			DataSourceTextField descField = new DataSourceTextField("description", I18N.message("description"));
			
			LinkedHashMap<String, String> viewerOpts = new LinkedHashMap<String, String>();
			viewerOpts.put("T", "\uD1B5\uD569\uBDF0\uC5B4");
			viewerOpts.put("S", "\uC2DC\uC2A4\uD15C");
			viewerOpts.put("U", "\uC9C0\uC815..");			
			DataSourceTextField viewerField = new DataSourceTextField("viewer", I18N.message("viewer"));
			viewerField.setValueMap(viewerOpts);

			setFields(typeIdField, typeNmField, sizeField, descField, viewerField);
			setDataURL("list/doccode.xml?sid=" + Session.get().getSid() + "&type=" + type);
			setClientOnly(true);
		}
		
	}
}