package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.speno.xedm.gui.common.client.I18N;

/**
 * 
 * @author deluxjun
 *
 */
public class EmailsValidator extends RegExpValidator {

	public EmailsValidator() {
		super();
		setErrorMessage(I18N.message("invalidemail"));
		setExpression("(([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}(\\s?[;,\\s]\\s?)?)+");
	}
}
