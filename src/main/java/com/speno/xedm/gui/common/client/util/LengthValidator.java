package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.speno.xedm.gui.common.client.I18N;

public class LengthValidator extends CustomValidator{
	FormItem formItem;
	int maxLength;
	int byteSize;
	final int MAX_LENGTH = 3000;
	
	public LengthValidator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LengthValidator(FormItem textItem, int maxLength) {
		// TODO Auto-generated constructor stub
		super();
		this.formItem = textItem;
		this.maxLength = maxLength;
		setValidateOnChange(true);
	}

	/**
	 * 20131202 na
	 * setLength역할을 하는 함수
	 */
	@Override
	protected boolean condition(Object value) {
		//null 포인트 에러 제거를 위한 try catch
		try {
			String str = value.toString();
			String message = "";
			byte[] strByte = str.getBytes("UTF-8");
			
			if(strByte.length > maxLength){
				message += I18N.message("exceedMessage");
				message += "<br>(" +I18N.message("current")+": "+ strByte.length+ 
									"bytes / "+I18N.message("max")+": " + maxLength + "bytes)";
				formItem.setValue(str = Util.strCut(str, maxLength));
				SC.warn(message);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return true;
	}
}