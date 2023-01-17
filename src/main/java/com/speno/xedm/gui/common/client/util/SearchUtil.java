package com.speno.xedm.gui.common.client.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.popup.FolderSelectorDialog;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gui.frontend.client.search.SearchPanel;

public class SearchUtil {
	// Item �⺻�� '��ü' ������
	public static String all= I18N.message("s.nolimits");

	/**
	 *	String �� �����͸� Date������ ��ȯ�Ѵ�.
	 *	@param str
	 * */
	@SuppressWarnings("deprecation")
	public static Date setDatebyString(String str){
		if(str!=null && str.length()>1){
			str.replaceAll("/", "");
			String year = str.substring(0, 4);
			String month = str.substring(4, 6);
			String day = str.substring(6, 8);
			
			Date date = new Date();
			// ������ ���� ����ؾ� �������� ��¥�� ����
			date.setYear(Integer.parseInt(year)-1900);
			date.setMonth(Integer.parseInt(month)-1);
			date.setDate(Integer.parseInt(day));
			return date;
		}else
			return null;
	}
	/**
	 *  OperatorItem default Setting
	 * */
	public static void setOperatorItem(SelectItem item){
		LinkedHashMap<String, String> operatorMap = new LinkedHashMap<String, String>();
		operatorMap.put("OPERATOR_EQUAL", "=");
		operatorMap.put("OPERATOR_UNEQUAL", "<>");
		operatorMap.put("OPERATOR_GREATER", ">");
		operatorMap.put("OPERATOR_LESS", "<");
		operatorMap.put("OPERATOR_GREATEREQUAL", ">=");
		operatorMap.put("OPERATOR_LESSEQUAL", "<=");
		operatorMap.put("OPERATOR_LIKE", "like");
		
		item.setValueMap(operatorMap);
		item.setDefaultToFirstOption(true);
		item.setTextAlign(Alignment.CENTER);
		item.setAlign(Alignment.LEFT);
		item.setWidth(40);
		item.setShowTitle(false);
	}
	
	/**
	 *  AndOrItem default Setting
	 * */
	public static void setAndOrItem(SelectItem item){
		item.setValueMap("AND","OR");
		item.setDefaultValue("AND");
		item.setTextAlign(Alignment.CENTER);
		item.setAlign(Alignment.LEFT);
		item.setWidth(70);
		item.setShowTitle(false);
	}
	
	/**
	 * 	FileSize ComboItem default Setting
	 * 	@param item : ComboBoxItem
	 * */
	public static LinkedHashMap<Long[], String> fileSizeMap = new LinkedHashMap<Long[], String>();
	public static void setFileSizeItem(final SelectItem item){
		ServiceUtil.getDocumentCodes("FILESIZE", new ReturnHandler<List<SCode>>() {
			@Override
			public void onReturn(List<SCode> param) {
				if(fileSizeMap.size() == 0){
					fileSizeMap.put(new Long[]{0L, 0L}, all);
					Long beforKeyValue = 0L;
					String showValue = "";
					String beforeShowValue = "";
					for (int i = 0; i < param.size()-1 ; i++) {
						if(i == 0)
							showValue = "~ " + param.get(i).getName();
						else
							showValue = beforeShowValue + " ~ " + param.get(i).getName();
							
						fileSizeMap.put(new Long[]{beforKeyValue, Long.parseLong(param.get(i).getValue())}, showValue);
						beforKeyValue = Long.parseLong(param.get(i).getValue());
						beforeShowValue = param.get(i).getName();
					}
				}
				item.setValueMap(fileSizeMap);
			}
		});
		
		item.setDefaultToFirstOption(true);
		item.setAlign(Alignment.LEFT);
		item.setWidth(130);
		item.setWrapTitle(false);
	}
	
	/**
	 *	����, ����� �˾� �˻�â Action
	 *	@param action : Constants.* FOLDER_PATH, OWNER
	 *	@param returnHandler
	 *	@param getGroup : ����� �׷����� ȹ��
	 * */
	@SuppressWarnings("rawtypes")
	public static void doFindAction(int action, ReturnHandler returnHandler, boolean getGroup){
		doFindAction(action, returnHandler, getGroup, null);
	}
	
	public static void doFindAction(int action,
			ReturnHandler returnHandler, boolean getGroup, String userName) {
		// TODO Auto-generated method stub
		switch(action){
		case Constants.FOLDER_PATH:
//			DocumentsUploadPath selectPath = new DocumentsUploadPath(1, returnHandler);
			FolderSelectorDialog selectPath = FolderSelectorDialog.get();
			selectPath.setReturnHandler(returnHandler);
			selectPath.setSharedSelectable(true); // 20131227, junsoo, �������� ��Ʈ ���� ����
			selectPath.show();
			break;
		case Constants.OWNER:
			// 20130829, junsoo, singleton ���� new �� ����
			OwnerWindow ownerWindow = new OwnerWindow("single", returnHandler, getGroup, userName);
			ownerWindow.show();
			break;
		}
	}
	/**
	 *  DateItem default Setting
	 * */
	public static void dateItemSetting(FormItem item){
		item.setWidth(100);
		item.setShowTitle(false);
		item.setStartRow(false);
		((DateItem) item).setUseTextField(true);	
		((DateItem) item).setPickerIconPrompt(null);
		item.setDateFormatter(DateDisplayFormat.TOJAPANSHORTDATE);
		item.setTop(10);
	}
	
	
	public static void setDateSelectDateItem(final SelectItem selectItem){
		LinkedHashMap<String, String> dateMap = new LinkedHashMap<String, String>();
		dateMap.put(SearchUtil.all, SearchUtil.all);		
		dateMap.put("user", I18N.message("user"));
		dateMap.put("1weeks", "1"+I18N.message("weeks"));
		dateMap.put("1month", "1"+I18N.message("month"));
		dateMap.put("3month", "3"+I18N.message("month"));
		dateMap.put("6month", "6"+I18N.message("month"));
		dateMap.put("1year", "1"+I18N.message("year"));
		selectItem.setValueMap(dateMap);
		selectItem.setDefaultToFirstOption(true);
	}
	/**
	 *  Date ���� Combo �ʱ�ȭ �� ���� ����
	 *  ComboItem Data ���ý� �ڵ����� FDate, SDate ����
	 * @param ComboBoxItem
	 * @param firstDate ���� ��¥
	 * @param secondDate ���� ��¥
	 * @param isExpiredate
	 * */
	public static void setDateComboData(final SelectItem comboItem, final DateItem firstDate, final DateItem secondDate, final boolean isExpiredate){
		// �޺� ������ �ʱ�ȭ
		setDateSelectDateItem(comboItem);
		comboItem.setWidth(80);
		comboItem.setWrapTitle(false);
		
		firstDate.setAlign(Alignment.RIGHT);
		secondDate.setAlign(Alignment.LEFT);
		
		final Date nowDate = new Date();
		firstDate.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// TODO Auto-generated method stub
				event.cancel();
			}
		});
		secondDate.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// TODO Auto-generated method stub
				event.cancel();
			}
		});
		firstDate.addIconClickHandler(new IconClickHandler() {
			
			@Override
			public void onIconClick(IconClickEvent event) {
				// TODO Auto-generated method stub
				comboItem.setValue("user");
			}
		});
		secondDate.addIconClickHandler(new IconClickHandler() {
			
			@Override
			public void onIconClick(IconClickEvent event) {
				// TODO Auto-generated method stub
				comboItem.setValue("user");
			}
		});
		/*
		 *	�޺� ������ ���� ����
		 *	FirstDate�� SecondDate�� ���� �ڵ����� �������ش�. 
		 * */
		comboItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if(comboItem.equals("user"))
				{	firstDate.setValue("");
				secondDate.setValue("");	}
				else
				{
				if(!isExpiredate){
					secondDate.setValue(nowDate);
					if(comboItem.getValue().equals(all)){
						firstDate.setValue("");
						secondDate.setValue("");	
					}else{
						firstDate.setValue(getComboDate(comboItem.getValueAsString(), false));
					}
				}else{
					firstDate.setValue(nowDate);
					if(comboItem.getValue().equals(all)){
						secondDate.setValue("");
						firstDate.setValue("");	
					}else{
						secondDate.setValue(getComboDate(comboItem.getValueAsString(), true));
					}
				}
				}
				
			}
		});
		
		// ��¥ validation
		firstDate.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				dateAlert(firstDate, secondDate, nowDate, true, isExpiredate);
			}
		});
		
		secondDate.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				dateAlert(secondDate, firstDate, nowDate, false, isExpiredate);
			}
		});
	}

	/**
	 * 	���� dateItem�� ���Ϸ��� dateItem�� �� �׸��� ���� ��¥�� ���Ͽ� ���â�� ����.
	 * 
	 *	@param currentDate
	 *	@param compareDate
	 * 	@param nowDate
	 * 	@param isFirstDate : �� dateItem �� ù dateItem�� ��� true  
	 * 	@param isExpireDate
	 * 	130805 taesu
	 * */
	@SuppressWarnings("deprecation")
	private static void dateAlert(DateItem currentDate, DateItem compareDate, Date nowDate, boolean isFirstDate, boolean isExpireDate){
		try {
			/* ���� ��¥�� ���� ���� �ð��� ���ԵǾ��ְ�,
			 * DateItem ���� ������ ���� �ð��� 12:00�� �����Ǿ� ������ ������ ���� �ð��� �������־���.
			 * 130806 taesu 
			*/
			nowDate.setHours(23);
			nowDate.setMinutes(59);
			nowDate.setSeconds(59);
			if(isExpireDate){
				if(isFirstDate){
					if(currentDate.getValueAsDate().compareTo(compareDate.getValueAsDate())>0){
						SC.warn(I18N.message("youcantchoiceafterday"));
						currentDate.setValue("");
					}
				}else{
					if(currentDate.getValueAsDate().compareTo(compareDate.getValueAsDate())<0){
						SC.warn(I18N.message("youcantchoiceafterday"));
						currentDate.setValue("");
					}
				}
			}else{
				if(isFirstDate){
					if(currentDate.getValueAsDate().compareTo(nowDate)>0){
						SC.warn(I18N.message("youcantchoicebeforeday")); // 20140318 �޼��� ����
						currentDate.setValue("");
					}else if(currentDate.getValueAsDate().compareTo(compareDate.getValueAsDate())>0){
						SC.warn(I18N.message("youcantchoiceafterday"));
						currentDate.setValue("");
					}
				}else{
					if(currentDate.getValueAsDate().compareTo(nowDate)>0){
						SC.warn(I18N.message("youcantchoicebeforeday"));
						currentDate.setValue("");}
					if(currentDate.getValueAsDate().compareTo(compareDate.getValueAsDate())<0){
						SC.warn(I18N.message("youchoosebeforeday"));
						currentDate.setValue("");
					}
				}
			}
		} catch (Exception e) {	}
	}
	
	/**
	 *  comboItem ���� ������ ���� �ش��ϴ� ���ڸ� ��ȯ
	 * 	@param comboData - String
	 * 	@param isExpireDate
	 * */
	@SuppressWarnings("deprecation")
	public static Date getComboDate(String comboData, boolean isExpireDate){
		Date date = new Date();
		// �Ϸ� getTime = 60*60*24*1000
		Long week = (long) (60*60*24*1000*7);
		
		//ExpireDate Item�� ��� After ����, �ƴҰ�� Before ����
		if(!isExpireDate){
			if(comboData.equals("1weeks")){
				date.setTime(date.getTime() - week);
			}else if(comboData.equals("1month")){
				date.setMonth(date.getMonth()-1);
			}else if(comboData.equals("3month")){
				date.setMonth(date.getMonth()-3);
			}else if(comboData.equals("6month")){
				date.setMonth(date.getMonth()-6);
			}else if(comboData.equals("1year")){
				date.setYear(date.getYear()-1);
			}else{
				date = null;
			}
		}else{
			if(comboData.equals("1weeks")){
				date.setTime(date.getTime() + week);
			}else if(comboData.equals("1month")){
				date.setMonth(date.getMonth()+1);
			}else if(comboData.equals("3month")){
				date.setMonth(date.getMonth()+3);
			}else if(comboData.equals("6month")){
				date.setMonth(date.getMonth()+6);
			}else if(comboData.equals("1year")){
				date.setYear(date.getYear()+1);
			}else{
				date = null;
			}
		}
		
		return date;
	}
	
	/**
	 * Date ���� form�� comboItem ���� 
	 * @param createDateForm : ������ DynamicForm
	 * @param formTitle : null�� ��� form �׷�ȭ ����
	 * @param createDateCombo : ������ ComboItem
	 * 
	 * */
	public static void setDateForm(DynamicForm createDateForm, String formTitle ,FormItem createDateCombo){
		createDateForm.setHeight("20%");
		createDateForm.setWidth(230);
		createDateForm.setMargin(5);
		createDateForm.setPadding(5);
		createDateForm.setNumCols(9*2);
//		createDateForm.setColWidths("25","25","25","25","25","25","25","25","*");
		createDateForm.setAlign(Alignment.LEFT);
		createDateForm.setTitleWidth(150);
		
		if(formTitle!=null){
			createDateForm.setIsGroup(true);
			createDateForm.setGroupTitle(formTitle);
		}
		
		if(createDateCombo!=null){
			createDateCombo.setShowTitle(false);
			createDateCombo.setStartRow(true);
			createDateCombo.setEndRow(true);
			createDateCombo.setColSpan(7);
			createDateCombo.setWidth(100);
		}
	}
	
	/**
	 * �˻� ���ǿ� ���Ǵ� Item�� option ����
	 * */
	public static void initItem(FormItem item, int width, Alignment align){
		item.setWidth(width);
		item.setWrapTitle(false);
		item.setAlign(align);
	}
	
	/**
	 * �⺻ �� ����
	 * Align : left, autoHeight, autoWidth or Width(width), Margin(5), Padding(5)
	 * */
	public static void initForm(DynamicForm form, int numCols, int width){
		form.setNumCols(numCols*2);
		form.setAlign(Alignment.LEFT);
		form.setAutoHeight();
//		form.setMargin(5);
//		form.setPadding(5);
		
		if(width>0)
			form.setWidth(width);
		else
			form.setAutoWidth();
	}
	
	
	/**
	 * ��¥�� ������ �����ϱ� ���� ����
	 * */
	public static String setSearchDate(Date date){
		if(date != null)
			return Util.getSearchFormattedDate(date).replaceAll("/", "");
		else
			return "";
	}
	
	public static LinkedHashMap<STemplate, String> templateMap = new LinkedHashMap<STemplate, String>();
	/**
	 * Template �����͸� �޾Ƽ� SelectItem�� �����Ѵ�.
	 * 130807 taesu
	 * */
	public static void getTemplateSelectItem(final SelectItem item){
		templateMap.clear();
		templateMap.put(new STemplate(), all);
		// ���� �ε��� Template ����� ����
		ServiceUtil.getAllTemplates(new ReturnHandler<STemplate[]>() {
			@Override
			public void onReturn(STemplate[] result) {
				//20140102na ������ �߰�
				templateMap.put(new STemplate(STemplate.uncheck),I18N.message("notspecified"));
				
				for (STemplate sTemplate : result) {
					templateMap.put(sTemplate, sTemplate.getName());
				}
				item.setValueMap(templateMap);
				item.setDefaultToFirstOption(true);
				item.setWidth(130);
			}
		});
	}
	
	public static LinkedHashMap<String, String> doctype = new LinkedHashMap<String, String>();
	/**
	 * Default DocType �����͸� �޾Ƽ� SelectItem�� �����Ѵ�.
	 * 130807 taesu
	 * */
	public static void getDefaultDocType(final SelectItem item) {
		doctype.put(0+"/"+all,all);
		ServiceUtil.getAvailableSDocTypes(null, new ReturnHandler<SDocType[]>() {
			@Override
			public void onReturn(SDocType[] result) {
				for (int i = 0; i < result.length; i++){
					String key = result[i].getId().toString();
					// Select Item�� option ���� ���� ���¸� �����ϱ� ���ؼ� �տ� ��ȣ�� ����.
					doctype.put("/"+key, result[i].getName());
				}
				item.setValueMap(doctype);
				item.setDefaultToFirstOption(true);
			}
		});
	}
	
	/**
	 * ���� ���Ĺ�� ���� �ʱ�ȭ
	 * DocumentsPanel���� ���
	 * 20130812 taesu
	 * */
	static LinkedHashMap<String, String> sortType = new LinkedHashMap<String, String>();
	public static void setSorter(final SelectItem sorterSelect){
		
		// DESC
		sortType.put(Constants.ORDER_BY_MODIFIEDDATE_DESC, 	I18N.message("lastModified_desc"));
		sortType.put(Constants.ORDER_BY_CREATEDATE_DESC, 	I18N.message("creationDate_desc"));
		sortType.put(Constants.ORDER_BY_TITLE_DESC,			I18N.message("title_desc"));
		sortType.put(Constants.ORDER_BY_DOCTYPE_DESC, 		I18N.message("docType_desc"));
		sortType.put(Constants.ORDER_BY_RETENTION_DESC,		I18N.message("expireDate_desc"));
		sortType.put(Constants.ORDER_BY_LOCK_DESC, 			I18N.message("lock_desc"));
		
		// ASC
		sortType.put(Constants.ORDER_BY_MODIFIEDDATE_ASC, 	I18N.message("lastModified_asc"));
		sortType.put(Constants.ORDER_BY_CREATEDATE_ASC, 	I18N.message("creationDate_asc"));
		sortType.put(Constants.ORDER_BY_TITLE_ASC,			I18N.message("title_asc"));
		sortType.put(Constants.ORDER_BY_DOCTYPE_ASC, 		I18N.message("docType_asc"));
		sortType.put(Constants.ORDER_BY_RETENTION_ASC,		I18N.message("expireDate_asc"));
		sortType.put(Constants.ORDER_BY_LOCK_ASC, 			I18N.message("lock_asc"));
		
		sorterSelect.setValueMap(sortType);
		sorterSelect.setShowTitle(false);
		sorterSelect.setWidth(130);
		sorterSelect.setDefaultToFirstOption(true);
		
		sorterSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String orderBy = sorterSelect.getValueAsString();
				if(MainPanel.get().getTabSet().getSelectedTab().getID().equals(Constants.MENU_SEARCHS)){
					SearchPanel.get().executeFetch();

//					int tabNum = SearchPanel.get().getTabSet().getSelectedTabNumber();
//					if(tabNum == 0)
//						SearchPanel.get().getPListingPanel().executeFetch(1, Session.get().getUser().getPageSize(), orderBy);
//					else
//						SearchPanel.get().getSListingPanel().executeFetch(1, Session.get().getUser().getPageSize(), orderBy);
					
					// TODO: sorter �׼� ����
//					SearchPanel.get().getPListingPanel().executeFetch(1, Session.get().getUser().getPageSize(), orderBy);
				}
				else{
					DocumentsPanel.get().getListingPanel().executeFetch(1, Session.get().getUser().getPageSize(), orderBy);
				}
			}
		});
	}
	public static LinkedHashMap<String, String> getSortType() {
		return sortType;
	}
	public static LinkedHashMap<STemplate, String> getTemplatemap() {
		return templateMap;
	}
	public static LinkedHashMap<String, String> getDoctype() {
		return doctype;
	}
	public static LinkedHashMap<Long[], String> getFileSizeMap() {
		return fileSizeMap;
	}
}
