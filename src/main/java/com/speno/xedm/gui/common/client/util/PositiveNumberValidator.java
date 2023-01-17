package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.speno.xedm.gui.common.client.I18N;

// kimsoeun GS������ - ���� �Է� üũ  
public class PositiveNumberValidator extends CustomValidator{
	FormItem formItem;
	
	public PositiveNumberValidator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PositiveNumberValidator(FormItem textItem) {
		// TODO Auto-generated constructor stub
		super();
		this.formItem = textItem;
	}

	@Override
	protected boolean condition(Object value) {
		
		boolean ret = true;
		//null ����Ʈ ���� ���Ÿ� ���� try catch
		try {
			
			int val = Integer.parseInt(value.toString());
			
			String message = "";

			if(val<=0) {
				message += I18N.message("positiveNumberMessage");
				SC.warn(message);
				
				ret = false;
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return ret;
	}
}