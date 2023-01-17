package com.speno.xedm.gui.common.client.widgets;

import java.util.LinkedHashMap;

import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.speno.xedm.gui.common.client.I18N;

public class PageCmb extends ComboBoxItem {
	
	private LinkedHashMap<Integer, String> valueMap = new LinkedHashMap<Integer, String>();
	
	public PageCmb() {
		
		setName("ListCountPerPage");
		setTitle("List Count Per Page");
		setWidth(60);
        setAttribute("editorType", "ComboBoxItem");
        setTitleColSpan(2);
        setDefaultValue(20);
        
		valueMap.put(20, I18N.message("20items"));   
	    valueMap.put(50, I18N.message("50items"));   
	    valueMap.put(100, I18N.message("100items"));
	    
	    setValueMap(valueMap); 
	    
	}
}
