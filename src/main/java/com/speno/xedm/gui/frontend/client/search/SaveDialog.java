package com.speno.xedm.gui.frontend.client.search;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.core.service.serials.SSearches;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * 검색 조건을 저장하기 위한 Dialog
 * 
 * @author deluxjun
 *
 */
public class SaveDialog extends Window {
	public SaveDialog(final SSearchOptions options) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("savesearch"));
		setWidth(350);
		setHeight(100);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final ValuesManager vm = new ValuesManager();
		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setWidth(350);
		form.setMargin(5);

		TextItem name = ItemFactory.newTextItem("name", "name", null);
		name.setRequired(true);
		name.setValidators(new LengthValidator(name, Constants.MAX_LEN_NAME));
		// 한글 입력이 안되 주석처리함 20130926 taesu
//		name.setValidators(new SimpleTextValidator());
		name.setWidth(100);

		TextItem description = ItemFactory.newTextItem("description", "description", null);
		description.setWidth(300);
		description.setValidators(new LengthValidator(description, Constants.MAX_LEN_DESC));

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					SSearches searchOption = new SSearches();
					searchOption.setName(vm.getValueAsString("name"));
					searchOption.setDescription(vm.getValueAsString("description"));
					searchOption.setOptions(options);
					
					ServiceUtil.search().save(Session.get().getSid(), searchOption, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, true);
							destroy();
						}
						@Override
						public void onSuccess(Void v) {
							SC.say(I18N.message("settingssaved"));
							destroy();
							SearchMenu.get().getSearchItems().loadSavedSelectItemValue();
						}
					});
				}
			}
		});

		form.setFields(name, description, save);
		addItem(form);
	}
}
