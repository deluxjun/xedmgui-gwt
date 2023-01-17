package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;

public class DocumentUtil {
	/**
	 * default Template 정보를 얻어와 data를 grid에 set 해준다.
	 * @param ListGrid
	 * @param SExtendedAttribute[]
	 * @param SDocument
	 * @param int : 보여줄 위치 Constants.PANEL_*
	 * 130730 taesu
	 * */
	public void setExtendedAttribute(ListGrid templateGrid, SExtendedAttribute[] exAttr, SDocument document, int showWhere){
		int i=0;
		// grid의 field와 record set
		switch(showWhere){
		case Constants.PANEL_STANDARD_PROPERTY_DOCUMENT:
			SContent[] contents = document.getContents();
			List<ListGridField> extFields = new ArrayList<ListGridField>();
			List<ListGridRecord> extRecords = new ArrayList<ListGridRecord>();
			
			ListGridField docID = new ListGridField("elementId", I18N.message("elementId"));
			docID.setHidden(true);
			
			ListGridField type = new ListGridField("type");
			type.setShowTitle(false);
			type.setType(ListGridFieldType.IMAGE);
			type.setImageURLPrefix(Util.imagePrefix());
			type.setAlign(Alignment.CENTER);
			type.setAutoFitWidth(true);
			type.setCanFilter(false);
			type.setCanEdit(false);
			
			ListGridField filename = new ListGridField("filename", I18N.message("filename"));
			filename.setType(ListGridFieldType.TEXT);
			filename.setAlign(Alignment.CENTER);
			filename.setCanFilter(false);
			filename.setCanEdit(false);
			
			ListGridField size = new ListGridField("size", I18N.message("size"));
			size.setType(ListGridFieldType.TEXT);
			size.setAlign(Alignment.CENTER);
			size.setCanFilter(false);
			size.setCanEdit(false);
			
			extFields.add(docID);
			extFields.add(type);
			extFields.add(filename);
			extFields.add(size);
			
			for (SContent content : contents) {
				ListGridRecord contentRecord = new ListGridRecord();
				contentRecord.setAttribute("elementId", content.getElementId());
				contentRecord.setAttribute("type", content.getIcon());
				contentRecord.setAttribute("filename", content.getFileName());
				contentRecord.setAttribute("size", Util.setFileSize(content.getFileSize(), true));
				for(int j = 0 ; j < content.getFieldNames().length ; j++){
					ListGridField contentField = new ListGridField(content.getFieldNames()[j]);
					contentRecord.setAttribute(content.getFieldNames()[j], content.getFieldValues()[j]);
					extFields.add(contentField);
				}
				extRecords.add(contentRecord);
			}
			templateGrid.setFields(extFields.toArray(new ListGridField[0]));
			templateGrid.setRecords(extRecords.toArray(new ListGridRecord[0]));
			break;
		case Constants.PANEL_STANDARD_PROPERTY_TEMPLATE:
			ListGridField[] templateFields = new ListGridField[exAttr.length];
			ListGridRecord[] templateRecord = new ListGridRecord[1];
			templateRecord[0] = new ListGridRecord();
			for (SExtendedAttribute sAttr : exAttr) {
				String fieldName = sAttr.getName()+"("+sAttr.getLabel()+")";
				templateFields[i] = new ListGridField(fieldName);
				templateFields[i].setAlign(Alignment.CENTER);
				setGridAttribute(templateRecord[0], fieldName, sAttr);
				i++;
			}
			templateGrid.setFields(templateFields);
			templateGrid.setRecords(templateRecord);
			break;
		case Constants.PANEL_UPLOADER:
			ListGridRecord[] templateRecords = new ListGridRecord[exAttr.length];
			for (SExtendedAttribute sAttr : exAttr) {
				String exName = sAttr.getName()+"("+ sAttr.getLabel() +")";
				templateRecords[i] = new ListGridRecord();
				templateRecords[i].setAttribute("label", sAttr.getLabel());
				templateRecords[i].setAttribute("position", sAttr.getPosition());
				templateRecords[i].setAttribute("name", exName);
				templateRecords[i].setAttribute("editor", sAttr.getEditor());
				templateRecords[i].setAttribute("type", sAttr.getType());
				templateRecords[i].setAttribute("mandatory", sAttr.isMandatory());
				setGridAttribute(templateRecords[i], "value", sAttr);
				i++;
			}
			templateGrid.setRecords(templateRecords);
			break;
		}
	}
	private void setGridAttribute(ListGridRecord record, String fieldName, SExtendedAttribute sAttr){
		int type = sAttr.getType();
		switch(type){
		case Constants.TYPE_STRING:
			record.setAttribute(fieldName, sAttr.getStringValue());
			break;
		case Constants.TYPE_INT:
			record.setAttribute(fieldName, sAttr.getIntValue());
			break;
		case Constants.TYPE_DOUBLE:
			record.setAttribute(fieldName, sAttr.getDoubleValue());
			break;
		case Constants.TYPE_DATE:
			record.setAttribute(fieldName, sAttr.getDateValue());
			break;	
		}
	}
}
