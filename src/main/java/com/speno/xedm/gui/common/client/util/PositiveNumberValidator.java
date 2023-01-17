package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.speno.xedm.gui.common.client.I18N;

// kimsoeun GS인증용 - 음수 입력 체크  
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
		//null 포인트 에러 제거를 위한 try catch
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