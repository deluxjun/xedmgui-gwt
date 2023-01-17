package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.speno.xedm.gui.common.client.I18N;

/**
 * 
 * @author deluxjun
 *
 */
public class SimpleTextValidator extends RegExpValidator {
	public SimpleTextValidator() {
		super();
		setErrorMessage(I18N.message("simpetextinvalid"));
		setExpression("^([a-zA-Z0-9\\-]+)$");
	}
}