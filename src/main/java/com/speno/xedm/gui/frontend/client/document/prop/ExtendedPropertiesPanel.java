package com.speno.xedm.gui.frontend.client.document.prop;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ListGridItem;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.StringTokenizer;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * 
 * @author deluxjun
 *
 */
public class ExtendedPropertiesPanel extends DocumentDetailTab implements ChangedHandler{
	private DynamicForm form1 = new DynamicForm();

	private DynamicForm form2 = new DynamicForm();

	private ValuesManager vm = new ValuesManager();

	private SExtendedAttribute[] currentExtAttributes = null;
	private List<ListGridItem> multiValueItems = new ArrayList<ListGridItem>();
	
	private SelectItem templateItem;

	public ExtendedPropertiesPanel(SDocument document, ChangedHandler changedHandler) {
		super(document, changedHandler);
		setWidth100();
		setHeight100();
		setMembersMargin(20);
		
//		if (form1 != null)
//			form1.destroy();
//
//		if (contains(form1))
//			removeChild(form1);
//		form1 = new DynamicForm();
		
		form1.setValuesManager(vm);
		List<FormItem> items = new ArrayList<FormItem>();

		templateItem = ItemFactory.newTemplateSelector(false, null);
		templateItem.addChangedHandler(changedHandler);
		templateItem.setMultiple(false);
		templateItem.addChangedHandler(this);
		
		setTemplates(templateItem);
		
		items.add(templateItem);

		form1.setItems(items.toArray(new FormItem[0]));
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setTitleWidth(70);
		form1.setNumCols(2);
		form1.setWidth100();
		addMember(form1);
		
		
//		form2.setValuesManager(vm);
//		form2.setNumCols(2);
//		form2.setTitleOrientation(TitleOrientation.LEFT);
//		form2.clearValues();
//		form2.clear();
//		form2.setWidth100();
//		
//		addMember(form2);

//		refresh();
	}
	
	private void setTemplates(final SelectItem selectItem) {
		final LinkedHashMap<String, String> templateMap = new LinkedHashMap<String, String>();
		ServiceUtil.getAllTemplates(new ReturnHandler<STemplate[]>() {
			@Override
			public void onReturn(STemplate[] param) {
				templateMap.put("0", "(" + I18N.message("notspecified") + ")");		// 20131206, junsoo, 미지정 추가
				for (STemplate template : param) {
					templateMap.put(String.valueOf(template.getId()), template.getName());
				}
				selectItem.setValueMap(templateMap);
			}
		});
	}
	
	// call from template combo box
	@Override
	public void onChanged(ChangedEvent event) {
		if (templateItem.getValue() != null && !"".equals(templateItem.getValue().toString())) {
			document.setAttributes(new SExtendedAttribute[0]);
			prepareExtendedAttributes(new Long(event.getValue().toString()));
		} else {
			document.setAttributes(new SExtendedAttribute[0]);
			prepareExtendedAttributes(null);

		}
	}


	private SExtendedAttribute getExtendedAttribute(String name) {
		if (currentExtAttributes != null)
			for (SExtendedAttribute extAttr : currentExtAttributes)
				if (extAttr.getName().equals(name))
					return extAttr;
		return null;
	}

	@Override
	public void refresh() {
		vm.clearValues();
		vm.clearErrors(false);

		// 20130819, junsoo, template id 변경은 write권한만 있으면 가능하도록 수정.
//		templateItem.setDisabled(!update || !control);
		templateItem.setDisabled(!update);
		
		if (document.getTemplateId() != null && document.getTemplateId() > 0)
			templateItem.setValue(document.getTemplateId().toString());
		else 
			templateItem.setValue("0");		// 20131209, junsoo, 미지정으로 세팅

		prepareExtendedAttributes(document.getTemplateId());
	}
	
	@Override
	protected void updatePermission() {
		super.updatePermission();
		
		// 신규등록이면 가능해야 할 권한들.
		if (isNew()) {
			update = true;
			rename = true;
			control = true;
		}
	}	


	/*
	 * Prepare the second form for the extended attributes
	 */
	private void prepareExtendedAttributes(final Long templateId) {
		if (form2 != null)
			form2.destroy();
		if (contains(form2))
			removeChild(form2);
		form2 = new DynamicForm();
		form2.setValuesManager(vm);
		form2.clearValues();
		form2.clear();
		form2.setTitleOrientation(TitleOrientation.LEFT);
		form2.setTitleWidth(100);
		form2.setNumCols(2);
		form2.setColWidths("100","300");
		form2.setWidth("400");
		form2.setAutoHeight();
		addMember(form2);

		multiValueItems.clear();

		if (templateId == null || templateId < 1) {
			return;
		}
		
		ServiceUtil.getExtendedAttributes(templateId, new ReturnHandler<SExtendedAttribute[]>() {
			
			@Override
			public void onReturn(SExtendedAttribute[] param) {
				setItemsFromAttributes(param);
			}
		});
	}

	// validate 및 저장을 하기 위해 document에 세팅
	@SuppressWarnings("unchecked")
	public boolean validate() {
		// 20130812, junsoo, update 권한이 없으면 validation할 필요 없음.
		if (!update)
			return true;
		
		if (multiValueItems != null && multiValueItems.size() > 0) {
			for (ListGridItem item : multiValueItems) {
				if (!item.validate()) {
					SCM.warn(I18N.message("invalidMultiValue") + "<br>" + item.getLastError());
					return false;
				}
			}
		}
		
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		
		//20140120na validatte()시 오류 발생
		vm.validate();
		if (!vm.hasErrors()) {

			if (values.get("template") == null || "".equals(values.get("template").toString()))
				document.setTemplateId(null);
			else {
				document.setTemplateId(Long.parseLong(values.get("template").toString()));
			}
			// attributes 초기화
			document.setAttributes(new SExtendedAttribute[0]);

			for (String name : values.keySet()) {
				if (name.startsWith("_")) {
					Object val = values.get(name);
					String nm = name.substring(1).replaceAll(Constants.BLANK_PLACEHOLDER, " ");
					SExtendedAttribute att = getExtendedAttribute(nm);
					if (att == null)
						continue;

					if (val != null) {
						if (val instanceof List) {	// multivalue
							String[] strs = new String[((List) val).size()];
							int count = 0;
							for (Object o : (List)val) {
								String str = "";
								if(o instanceof Record){
									Record record = (Record)o;
									str = record.getAttribute(nm);
								}
								else if(o instanceof Map){
									Map map = (Map)o;
									str = (String)map.get(nm);
								}
								
								// 문자열 길이 체크.
								if (str.getBytes().length > att.getIntValue())
									return false;
								
								strs[count++] = str;
							}
							document.setValue(nm, strs);

						} else if (val instanceof String) {
							// 문자열 길이 체크.
//							String size1 = Integer.toString(((String)val).getBytes().length);
//							String size2 = Long.toString(att.getIntValue());
							try {
								long size1 = ((String)val).getBytes("UTF-8").length;
								long size2 = att.getIntValue();
								
								if(att.getEditor() == SExtendedAttribute.EDITOR_LISTBOX) document.setValue(nm, val);
								else if (size1 > size2){
									String message = "";
									message += I18N.message("exceedMessage");
									message += "<br>(" +I18N.message("current")+": "+ size1+ 
														"bytes / "+I18N.message("max")+": " + size2 + "bytes)";
									SC.warn(message);
//										SC.warn(I18N.message("exceedMessage")+"<br>"+size1 + ">" + size2);
										return false;
								}
								else document.setValue(nm, val);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								return false;
							}
							
							
						} else {
							document.setValue(nm, val);
						}
					} else {
						if (att != null) {
							if (att.getType() == SExtendedAttribute.TYPE_INT) {
								document.getExtendedAttribute(nm).setIntValue(null);
								break;
							} else if (att.getType() == SExtendedAttribute.TYPE_DOUBLE) {
								document.getExtendedAttribute(nm).setDoubleValue(null);
								break;
							} else if (att.getType() == SExtendedAttribute.TYPE_DATE) {
								document.getExtendedAttribute(nm).setDateValue(null);
								break;
							} else if (att.getType() == SExtendedAttribute.TYPE_MULTIVALUE) {
								document.setValue(nm, new String[0]);
								break;
							} else {
								document.setValue(nm, "");
								break;
							}
						}
					}
				}
			}
//			return true;
		}
		
		
		
		return !vm.hasErrors();
	}
	
	private void setItemsFromAttributes(SExtendedAttribute[] result) {
		currentExtAttributes = result;
		
		List<FormItem> items = new ArrayList<FormItem>();
		for (SExtendedAttribute att : result) {
			if (att.getType() == SExtendedAttribute.TYPE_STRING) {
				final FormItem item = ItemFactory.newStringItemForExtendedAttribute(att);
				
				//20140113na 코드와 설명이 동시에 저장되던걸 코드값만 저장하도록 변경하기 위해 만든 코드
				if (document.getValue(att.getName()) != null && att.getEditor() == SExtendedAttribute.EDITOR_LISTBOX){
					String str = (String) document.getValue(att.getName());
					String strMap = item.getAttribute("valueMap");
					StringTokenizer strToken = new StringTokenizer(strMap, ",");
					
					while(strToken.hasMoreElements()) {
						String strValue = strToken.nextToken();
						int blank = strValue.indexOf(" ");
						if(blank > 0) {
							String strTemp = strValue.substring(0, blank);
							
							if(strTemp.equals(str)){
								item.setValue(strValue);
								break;
							}
						}
						else item.setValue(str);
					}
				}
				else if (document.getValue(att.getName()) != null){
					String str = (String) document.getValue(att.getName());
					item.setValue(str);
				}
					
				else {
					if (att.getEditor() != SExtendedAttribute.EDITOR_LISTBOX) {
						item.setValue((String)att.getValue());
					}
				}
				
				// String의 경우는 length 설정.
				if (	att.getEditor() != SExtendedAttribute.EDITOR_LISTBOX &&
						att.getIntValue() != null) {
					item.setHint("(" + att.getIntValue() + ")");
					if (item instanceof TextItem)
//						((TextItem)item).setLength(att.getIntValue().intValue());
						((TextItem)item).setValidators(new LengthValidator(((TextItem)item), att.getIntValue().intValue()));
					else if (item instanceof TextAreaItem)
						((TextAreaItem)item).setValidators(new LengthValidator(((TextAreaItem)item), att.getIntValue().intValue()));
				}
				
				item.addChangedHandler(changedHandler);
				item.setDisabled(!update);
				items.add(item);
				item.addChangedHandler(new ChangedHandler() {
					@Override
					public void onChanged(ChangedEvent event) {
//						System.out.println(item.getValue());
					}
				});
			} else if (att.getType() == SExtendedAttribute.TYPE_INT) {
				IntegerItem item = ItemFactory.newIntegerItemForExtendedAttribute(att.getName(),
						att.getLabel(), null);
				if (document.getValue(att.getName()) != null)
					item.setValue((Long) document.getValue(att.getName()));
				else
					item.setValue((Long)att.getValue());
				item.setRequired(att.isMandatory());
				item.addChangedHandler(changedHandler);
				item.setDisabled(!update);
				items.add(item);
			} else if (att.getType() == SExtendedAttribute.TYPE_DOUBLE) {
				FloatItem item = ItemFactory.newFloatItemForExtendedAttribute(att.getName(), att.getLabel(),
						null);
				if (document.getValue(att.getName()) != null)
					item.setValue((Double) document.getValue(att.getName()));
				else
					item.setValue((Double)att.getValue());
				item.setRequired(att.isMandatory());
				item.addChangedHandler(changedHandler);
				item.setDisabled(!update);
				items.add(item);
			} else if (att.getType() == SExtendedAttribute.TYPE_DATE) {
				final DateItem item = ItemFactory.newDateItemForExtendedAttribute(att.getName(), att.getLabel());
				item.setDateFormatter(DateDisplayFormat.TOJAPANSHORTDATE);
				if (document.getValue(att.getName()) != null)
					item.setValue((Date) document.getValue(att.getName()));
				else
					item.setValue((Date)att.getValue());
				item.setRequired(att.isMandatory());
				item.addChangedHandler(changedHandler);
				item.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						if ("backspace".equals(event.getKeyName().toLowerCase())
								|| "delete".equals(event.getKeyName().toLowerCase())) {
							item.clearValue();
							item.setValue((Date) null);
							changedHandler.onChanged(null);
						} else {
							changedHandler.onChanged(null);
						}
					}
				});
				item.setDisabled(!update);
				items.add(item);
			} else if (att.getType() == SExtendedAttribute.TYPE_MULTIVALUE) {
		        ListGridItem multiValueItem = ItemFactory.newMultiValueItem(att.getName(), I18N.message("values"), 200, 
		        		Util.getSafeNumber(att.getIntValue(), 1000L).intValue(), att.isMandatory());  
		        multiValueItem.setRequired(att.isMandatory());
		        multiValueItem.setHeight(150);
		        
		        multiValueItems.add(multiValueItem);

				Object valueObj = document.getValue(att.getName());
				if (valueObj == null || valueObj instanceof String[]) {

					if (valueObj != null)
						multiValueItem.setData((String[])valueObj);
					else {
						// set default template item.
						if (att.getMultiValue() != null && att.getMultiValue().length > 0)
						multiValueItem.setData(att.getMultiValue());
					}

					multiValueItem.addChangedHandler(changedHandler);
					multiValueItem.setDisabled(!update);
					items.add(multiValueItem);
				} else {
					Log.warn("Type is multivalue, but assigned data is not string[]");
				}
			}
		}
		
		form2.setItems(items.toArray(new FormItem[0]));
	}
}