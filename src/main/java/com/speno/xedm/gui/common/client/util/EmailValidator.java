package com.speno.xedm.gui.common.client.util;

import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.speno.xedm.gui.common.client.I18N;

/**
 * email validator
 * @author deluxjun
 *
 */
public class EmailValidator extends RegExpValidator {

	public EmailValidator() {
		super();
		setErrorMessage(I18N.message("invalidemail"));
		setExpression("([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9\\-])+");
	}
}
