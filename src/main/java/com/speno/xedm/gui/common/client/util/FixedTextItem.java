package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;

public class FixedTextItem extends TextItem {
    public FixedTextItem(String name) {
    	super(name);
    }

    public FixedTextItem(String name, String title) {
    	super(name, title);
    }
    
    public FixedTextItem() {
        FormItemIcon formItemIcon = new FormItemIcon();
        setIcons(formItemIcon);
 
        addIconClickHandler(new IconClickHandler() {
            @Override
            public void onIconClick(IconClickEvent event) {
                FormItem item = event.getItem();
                System.out.println("getValue() is " + item.getValue());
 
                item.clearValue();
            }
        });
        

    }
}