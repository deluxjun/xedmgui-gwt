package com.speno.xedm.gui.common.client.util;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.IsFloatValidator;
import com.smartgwt.client.widgets.form.validator.IsIntegerValidator;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SVersion;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
//import com.speno.xedm.gui.common.client.data.UsersDS;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;

/**
 * 
 * @author deluxjun
 *
 */
public class ItemFactory {
	
	private static DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);

	/**
	 * Creates a new DateItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (optional)
	 */
	public static DateItem newDateItem(String name, String title) {
		DateItem date = new DateItem(name);
//		if (title != null)
//			date.setTitle(I18N.message(title));
//		else
			date.setShowTitle(false);
		date.setUseTextField(true);
		date.setUseMask(true);
		date.setShowPickerIcon(true);
		date.setWidth(90);
		date.setName(name);
		date.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		date.setHintStyle("hint");
		return date;
	}

	/**
	 * Creates a new DateItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 */
	public static DateItem newDateItemForExtendedAttribute(String name, String label) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		final DateItem date = new DateItem(itemName);
		date.setTitle(name + "&nbsp&nbsp<br>" +" (" + label + ")");
		date.setUseTextField(true);
		date.setUseMask(false);
		date.setShowPickerIcon(true);
		date.setWidth(90);
		date.setName(itemName);
		date.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		date.setHintStyle("hint");
		return date;
	}

//	public static SelectItem newUserSelectorForExtendedAttribute(String name, String title) {
//		final SelectItem item = new UserSelector("_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER), title);
//		return item;
//	}

	public static SelectItem newDateOperator(String name, String title) {
		SelectItem dateOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("before", I18N.message("before"));
		opts.put("after", I18N.message("after"));
		dateOperator.setValueMap(opts);
		dateOperator.setName(name);
		if (title != null)
			dateOperator.setTitle(I18N.message(title));
		else
			dateOperator.setShowTitle(false);
		dateOperator.setDefaultValue("nolimits");
		dateOperator.setWidth(80);
		dateOperator.setHintStyle("hint");
		return dateOperator;
	}

	public static SelectItem newSizeOperator(String name, String title) {
		SelectItem sizeOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("nolimits", I18N.message("nolimits"));
		opts.put("lessthan", I18N.message("lessthan"));
		opts.put("greaterthan", I18N.message("greaterthan"));
		sizeOperator.setValueMap(opts);
		sizeOperator.setName(name);
		if (title != null)
			sizeOperator.setTitle(I18N.message(title));
		else
			sizeOperator.setShowTitle(false);
		sizeOperator.setDefaultValue("nolimits");
		sizeOperator.setWidth(85);
		sizeOperator.setHintStyle("hint");
		return sizeOperator;
	}

	public static SelectItem newLanguageSelector(String name, boolean withEmpty, boolean gui) {
		SelectItem item = new SelectItem();
		if (gui)
			item.setValueMap(I18N.getSupportedGuiLanguages(withEmpty));
		else
			item.setValueMap(I18N.getSupportedLanguages(withEmpty));
		item.setName(name);
		item.setTitle(I18N.message("language"));
		item.setWrapTitle(false);
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newEncodingSelector(String name) {
		SelectItem item = new SelectItem();
		item.setName(name);
		item.setTitle(I18N.message("encoding"));
		item.setWrapTitle(false);
		item.setDefaultValue("UTF8");

		// String platformEncoding = System.getProperty("file.encoding");
		// Charset ce = Charset.forName(platformEncoding);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		// Eight-bit Unicode (or UCS) Transformation Format
		map.put("UTF8", "UTF-8 Unicode");
		// PC Greek
		map.put("Cp737", "PC Greek");
		// ISO-8859-1, Latin Alphabet No. 1
		map.put("ISO8859_1", "Latin Alphabet No. 1");
		// Latin/Cyrillic Alphabet
		map.put("ISO8859_5", "Latin/Cyrillic");
		// Latin/Arabic Alphabet
		map.put("ISO8859_6", "Latin/Arabic");
		// Latin/Greek Alphabet (ISO-8859-7:2003)
		map.put("ISO8859_7", "Latin/Greek");
		// Simplified Chinese, PRC standard
		map.put("GB18030", "GB18030 Simplified Chinese");
		// GB2312, EUC encoding, Simplified Chinese
		map.put("EUC_CN", "GB2312 Simplified Chinese");
		// JISX 0201, 0208 and 0212, EUC encoding Japanese
		map.put("EUC_JP", "EUC-JP Japanese");
		// Shift-JIS, Japanese
		map.put("SJIS", "Shift_JIS Japanese");
		// KS C 5601, EUC encoding, Korean
		map.put("EUC_KR", "EUC-KR Korean");
		// Windows Eastern European
		map.put("Cp1250", "windows-1250 Eastern European");
		// Windows Latin-1
		map.put("Cp1252", "windows-1252 Latin-1");
		// Windows Greek
		map.put("Cp1253", "windows-1253 Greek");
		// Windows Arabic
		map.put("Cp1256", "windows-1256 Arabic");

		item.setValueMap(map);
		item.setHintStyle("hint");
		return item;
	}

	public static TextItem newEmailItem(String name, String title, boolean multiple) {
		TextItem item = new TextItem();
		item.setName(name);
		if (title != null)
			item.setTitle(I18N.message(title));
		else
			item.setShowTitle(false);
		if (multiple)
			item.setValidators(new EmailsValidator());
		else
			item.setValidators(new EmailValidator());
		item.setHintStyle("hint");
		return item;
	}

//	public static SelectItem newGroupSelector(String name, String title) {
//		SelectItem group = new SelectItem(name);
//		group.setTitle(I18N.message(title));
//		group.setWrapTitle(false);
//		group.setValueField("id");
//		group.setDisplayField("name");
//		group.setPickListWidth(300);
//		ListGridField n = new ListGridField("name", I18N.message("name"));
//		ListGridField description = new ListGridField("description", I18N.message("description"));
//		group.setPickListFields(n, description);
//		group.setOptionDataSource(GroupsDS.get());
//		group.setHintStyle("hint");
//		return group;
//	}
//
//	public static SelectItem newUserSelector(String name, String title) {
//		SelectItem user = new SelectItem(name);
//		user.setTitle(I18N.message(title));
//		user.setWrapTitle(false);
//		ListGridField username = new ListGridField("username", I18N.message("username"));
//		ListGridField label = new ListGridField("label", I18N.message("name"));
//		user.setValueField("id");
//		user.setDisplayField("username");
//		user.setPickListWidth(300);
//		user.setPickListFields(username, label);
//		user.setOptionDataSource(new UsersDS());
//		user.setHintStyle("hint");
//		return user;
//	}

	public static RadioGroupItem newBooleanSelector(String name, String title) {
		RadioGroupItem radioGroupItem = new RadioGroupItem();
		radioGroupItem.setName(name);
		radioGroupItem.setVertical(false);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("yes", I18N.message("yes"));
		map.put("no", I18N.message("no"));
		radioGroupItem.setValueMap(map);
		radioGroupItem.setRedrawOnChange(true);
		radioGroupItem.setTitle(I18N.message(title));
		radioGroupItem.setWidth(80);
		radioGroupItem.setHintStyle("hint");
		return radioGroupItem;
	}

	public static CheckboxItem newCheckbox(String name, String title) {
		CheckboxItem item = new CheckboxItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newMultipleSelector(String name, String title) {
		SelectItem selectItemMultipleGrid = new SelectItem();
		selectItemMultipleGrid.setName(name);
		selectItemMultipleGrid.setTitle(I18N.message(title));
		selectItemMultipleGrid.setMultiple(true);
		selectItemMultipleGrid.setValueMap("");
		selectItemMultipleGrid.setHintStyle("hint");
		return selectItemMultipleGrid;
	}

	public static SelectItem newPrioritySelector(String name, String title) {
		SelectItem select = new SelectItem(name, title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("low"));
		map.put("1", I18N.message("medium"));
		map.put("2", I18N.message("high"));
		select.setValueMap(map);
		select.setValue("0");
		select.setHintStyle("hint");
		return select;
	}

	public static SelectItem newWelcomeScreenSelector(String name, Integer value) {
		SelectItem select = new SelectItem(name, I18N.message("welcomescreen"));
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("1500", I18N.message("documents"));
		map.put("1510", I18N.message("search"));
		map.put("1520", I18N.message("dashboard"));
		select.setValueMap(map);
		if (value != null)
			select.setValue(value.toString());
		else
			select.setValue("1500");
		return select;
	}

//	public static SelectItem newDashletSelector(String name, String title) {
//		SelectItem select = new SelectItem(name, title);
//		select.setAllowEmptyValue(false);
//		
//		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//		map.put("" + Constants.DASHLET_CHECKOUT, I18N.message(Constants.EVENT_CHECKEDOUT + "docs"));
//		map.put("" + Constants.DASHLET_CHECKIN, I18N.message(Constants.EVENT_CHECKEDIN + "docs"));
//		map.put("" + Constants.DASHLET_LOCKED, I18N.message(Constants.EVENT_LOCKED + "docs"));
//		map.put("" + Constants.DASHLET_CHANGED, I18N.message(Constants.EVENT_CHANGED + "docs"));
//		map.put("" + Constants.DASHLET_DOWNLOADED, I18N.message(Constants.EVENT_DOWNLOADED + "docs"));
//		map.put("" + Constants.DASHLET_LAST_NOTES, I18N.message("lastnotes"));
//		map.put("" + Constants.DASHLET_TAGCLOUD, I18N.message("tagcloud"));
//
//		select.setValueMap(map);
//		select.setValue(map.keySet().iterator().next());
//		return select;
//	}

//	public static SelectItem newEventsSelector(String name, String title, boolean folder, boolean workflow, boolean user) {
//		SelectItem select = newMultipleSelector(name, title);
//		select.setWidth(300);
//		select.setHeight(200);
//		select.setMultipleAppearance(MultipleAppearance.GRID);
//		select.setMultiple(true);
//		select.setOptionDataSource(new EventsDS(SSession.get().getUser().getLanguage(), folder, workflow, user));
//		select.setValueField("code");
//		select.setDisplayField("label");
//		select.setHintStyle("hint");
//		return select;
//	}

	public static SelectItem newSelectItem(String name, String title) {
		SelectItem select = newMultipleSelector(name, title != null ? I18N.message(title) : I18N.message(name));
		select.setMultiple(false);
		select.setWrapTitle(false);
		select.setHintStyle("hint");
		return select;
	}

	public static Img newImgIcon(String name) {
		Img img = newImg(name);
		img.setWidth("16px");
		return img;
	}

	public static Img newImg(String name) {
		Img img = new Img(Util.imageUrl(name));
		return img;
	}

	public static Img newBrandImg(String name) {
		Img img = new Img(Util.brandUrl(name));
		return img;
	}

	public static FormItemIcon newItemIcon(String image) {
		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(ItemFactory.newImgIcon(image).getSrc());
		return icon;
	}

	public static HeaderIcon newHeaderIcon(String image) {
		HeaderIcon icon = new HeaderIcon(ItemFactory.newImgIcon(image).getSrc());
		return icon;
	}

	/**
	 * Creates a new TextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newTextItem(String name, String title, String value) {
		TextItem item = new TextItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		return item;
	}
	
	// description 용. inputstring box로 입력 가능하도록 함.
	public static TextAreaItem newTextAreaItemWithInputBox(String name, String title, String value, int width, int height, final int length) {
		final TextAreaItem item = new TextAreaItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		item.setWidth(width);
		item.setHeight(height);
		
//		final TextItem item = new TextItem();
//		item.setName(name);
//		item.setTitle(I18N.message(title));
//		if (value != null)
//			item.setValue(value);
//		else
//			item.setValue("");
//		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		
        // 삭제 버튼
        PickerIcon picker = new PickerIcon(new Picker("[SKIN]/actions/add.png"), new FormItemClickHandler() {   
        	public void onFormItemClick(FormItemIconClickEvent event) { 
				InputStringDialog dialog = new InputStringDialog(item.getValueAsString(), 525, 300, length) {
					@Override
					public void onOk(String text) {
						item.setValue(text);
					}
				};
				dialog.draw();
            }   
        });
        picker.setWidth(18);
        picker.setHeight(18);
        
        item.setIcons(picker);
		
		return item;
	}

	/**
	 * Creates a new TextItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static FormItem newStringItemForExtendedAttribute(SExtendedAttribute att) {
		// We cannot use spaces in items name
		String itemName = "_" + att.getName().replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FormItem item = new TextItem();
		if (att.getEditor() == SExtendedAttribute.EDITOR_LISTBOX) {
			item = new SelectItem();
			SelectItem select = (SelectItem) item;
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			if (!att.isMandatory())
				map.put("", "");
			if (att.getOptions() != null)
				for (String a : att.getOptions()) {
					if (a.startsWith("(") && a.endsWith("")) {
						String value = a.substring(1, a.length()-1);
						select.setDefaultValue(value);
						map.put(value, value);
					} else {
						map.put(a, a);
					}
				}
			select.setValueMap(map.values().toArray(new String[0]));
		} else if (att.getEditor() == SExtendedAttribute.EDITOR_TEXTAREA) {
			int length = att.getIntValue().intValue();
			item = newTextAreaItemWithInputBox(itemName, "", "", 200, 50, length);
		}

		item.setName(itemName);
		item.setTitle(att.getName() + " (" + att.getLabel() + ")");
		item.setWrapTitle(false);
		item.setTitleVAlign(VerticalAlignment.TOP);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		item.setWidth(200);
		item.setRequired(att.isMandatory());
		
		return item;
	}

	public static PasswordItem newPasswordItem(String name, String title, String value) {
		PasswordItem password = new PasswordItem();
		password.setTitle(I18N.message(title));
		password.setName(name);
		if (value != null)
			password.setValue(value);
		password.setHintStyle("hint");
		return password;
	}

	/**
	 * Creates a new TextItem that validates a simple text.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
//	public static TextItem newSimpleTextItem(String name, String title, String value) {
//		TextItem item = newTextItem(name, I18N.message(title), value);
//		item.setValidators(new SimpleTextValidator());
//		item.setHintStyle("hint");
//		return item;
//	}

	/**
	 * Creates a new StaticTextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static StaticTextItem newStaticTextItem(String name, String title, String value) {
		StaticTextItem item = new StaticTextItem();
		if (name.trim().equals(""))
			item.setShouldSaveValue(false);
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		item.setHintStyle("hint");
		return item;
	}
	
	//20140102na =====; 라이브러리 에러에 따른 수정
	public static StaticTextItem newStaticTextItem2(String name, String title, String value) {
		StaticTextItem item = new StaticTextItem(name, title);
		item.setValue(value);
		item.setShowTitle(false);
		item.setWrapTitle(false);
		item.setHintStyle("hint");
		item.setWrap(false);
		return item;
	}
	
	public static Label newLabel(int height, String icon, boolean edges, String content){
        Label label = new Label();  
        label.setHeight(height);  
        label.setAlign(Alignment.CENTER);  
        label.setValign(VerticalAlignment.CENTER);  
        label.setWrap(false);  
        if (icon != null && icon.length() > 0)
        	label.setIcon(icon);  
        label.setShowEdges(edges);  
        label.setContents(content); 
        
        return label;
	}

	/**
	 * Creates a new IntegerItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newIntegerItem(String name, String title, Integer value) {
		IntegerItem item = new IntegerItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new IntegerItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newIntegerItemForExtendedAttribute(String name, String label, Integer value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		IntegerItem item = newIntegerItem(itemName, name + " (" + label + ")", value);
		return item;
	}

	/**
	 * Creates a new IntegerItem with a range validator.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 * @param min The item minimum value (optional)
	 * @param min The item maximum value (optional)
	 */
	public static IntegerItem newValidateIntegerItem(String name, String title, Integer value, Integer min, Integer max) {
		IntegerItem item = newIntegerItem(name, I18N.message(title), value);
		IntegerRangeValidator rv = null;
		if (min != null || max != null) {
			rv = new IntegerRangeValidator();
			if (min != null)
				rv.setMin(min);
			if (max != null)
				rv.setMax(max);
		}
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		if (rv == null)
			item.setValidators(iv);
		else
			item.setValidators(iv, rv);

		item.setHintStyle("hint");
		return item;
	}

	public static LinkItem newLinkItem(String name, String title) {
		LinkItem linkItem = new LinkItem(name);
//		if (!title.trim().isEmpty()) {
		if (!title.trim().equals("")) {
			linkItem.setTitle(I18N.message(title));
			linkItem.setLinkTitle(I18N.message(title));
		}
		linkItem.setWrapTitle(false);
		linkItem.setHintStyle("hint");
		return linkItem;
	}


	
	/**
	 * Creates a new TextAreaItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */

	public static TextAreaItem newTextAreaItem(String name, String title, String value) {
		TextAreaItem item = new TextAreaItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		item.setWidth(200);
		item.setHeight(50);
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setHintStyle("hint");
		return item;
	}

	public static SelectItem newTimeSelector(String name, String title) {
		SelectItem select = new SelectItem(name, I18N.message(title));
		select.setWidth(110);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("minute", I18N.message("minutes"));
		map.put("hour", I18N.message("hours"));
		map.put("day", I18N.message("ddays"));
		map.put("week", I18N.message("weeks"));
		select.setValueMap(map);
		select.setValue("minute");
		select.setHintStyle("hint");
		return select;
	}

	public static SelectItem newTemplateSelector(boolean multipleSelection, Long templateId) {
		SelectItem templateItem = new SelectItem("template", I18N.message("template"));
		templateItem.setDisplayField("name");
		templateItem.setValueField("id");
//		templateItem.setPickListWidth(250);
		templateItem.setMultiple(true);
		templateItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
//		if (!multipleSelection)
//			templateItem.setOptionDataSource(new TemplatesDS(true, templateId, null, false));
//		else
//			templateItem.setOptionDataSource(new TemplatesDS(false, templateId, null, false));
		templateItem.setHintStyle("hint");
		
		return templateItem;
	}

	public static SelectItem newEmailProtocolSelector(String name, String title) {
		SelectItem select = new SelectItem(name, I18N.message(title));
		select.setWidth(110);
		select.setValueMap("pop3", "imap");
		return select;
	}

	public static SelectItem newEmailFolderingSelector(String name, String title) {
		SelectItem select = new SelectItem(name, I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("none"));
		map.put("1", I18N.message("year"));
		map.put("2", I18N.message("month"));
		map.put("3", I18N.message("day"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEffectSelector(String name, String title) {
		SelectItem select = new SelectItem(name, I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "");
		map.put("move", I18N.message("move"));
		map.put("copy", I18N.message("copy"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEmailFields(String name, String title) {
		SelectItem select = new SelectItem(name, I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("title"));
		map.put("1", I18N.message("sender"));
		map.put("2", I18N.message("content"));
		select.setValueMap(map);
		select.setHintStyle("hint");
		return select;
	}

//	public static SelectItem newArchiveTypeSelector() {
//		SelectItem item = new SelectItem();
//		item.setName("archivetype");
//		item.setTitle(I18N.message("type"));
//		item.setWrapTitle(false);
//		item.setDefaultValue(Integer.toString(GUIArchive.TYPE_DEFAULT));
//
//		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//		map.put(Integer.toString(GUIArchive.TYPE_DEFAULT), I18N.message("default"));
//		map.put(Integer.toString(GUIArchive.TYPE_STORAGE), I18N.message("paperdematerialization"));
//
//		item.setValueMap(map);
//
//		if (!Feature.enabled(Feature.ARCHIVES))
//			item.setDisabled(true);
//		item.setHintStyle("hint");
//		return item;
//	}
//
//	public static SelectItem newArchiveSelector(int mode, Integer status) {
//		SelectItem item = new SelectItem("archive");
//		item.setTitle("");
//		item.setRequiredMessage(I18N.message("fieldrequired"));
//		ListGridField name = new ListGridField("name", I18N.message("name"));
//		ListGridField description = new ListGridField("description", I18N.message("description"));
//		item.setValueField("id");
//		item.setDisplayField("name");
//		item.setPickListWidth(300);
//		item.setPickListFields(name, description);
//		item.setOptionDataSource(new ArchivesDS(mode, null, status, null));
//		if (!Feature.enabled(Feature.ARCHIVES))
//			item.setDisabled(true);
//		item.setHintStyle("hint");
//		return item;
//	}

//	public static SelectItem newImportCustomIds() {
//		SelectItem item = newSelectItem("importcids", null);
//
//		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//		map.put(Integer.toString(GUIArchive.CUSTOMID_NOT_IMPORT), I18N.message("ignore"));
//		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_RELEASE), I18N.message("importasnewversion"));
//		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_SUBVERSION), I18N.message("importasnewsubversion"));
//		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_DOCUMENT), I18N.message("importasnewdoc"));
//		item.setValueMap(map);
//		item.setHintStyle("hint");
//		return item;
//	}

	public static Label newLinkLabel(String title) {
		Label label = new Label(I18N.message(title));
		label.setWrap(false);
		label.setCursor(Cursor.HAND);
		label.setAutoWidth();
		return label;
	}
	//yukyong soo -  top notice wiget 20140303
	public static TextArea newTextArea()
	{
		TextArea textarea = new TextArea();		
		textarea.setWidth("1300px");
		textarea.setHeight("40px");
		textarea.setReadOnly(true);		
		return textarea;
	}
	
	public static Label newLabelWithIcon(String icon, String title, boolean showEdges, int height, int padding) {
		Label label = new Label(I18N.message(title));

        label.setHeight(height);  
        label.setPadding(padding);  
        label.setAlign(Alignment.CENTER);  
        label.setValign(VerticalAlignment.CENTER);  
        label.setWrap(false);  
        label.setIcon(icon);  
        label.setShowEdges(showEdges);  
        label.draw();  
		return label;
	}

	/**
	 * Creates a new FloatItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static FloatItem newFloatItem(String name, String title, Float value) {
		FloatItem item = new FloatItem();
		item.setName(name);
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		IsFloatValidator iv = new IsFloatValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new FloatItem for the Extended Attributes.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static FloatItem newFloatItemForExtendedAttribute(String name, String label, Float value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FloatItem item = newFloatItem(itemName, name + " (" + label + ")", value);
		return item;
	}

	/**
	 * Simple yes/no radio button. yes=true, no=false
	 */
	public static RadioGroupItem newYesNoItem(String name, String label) {
		RadioGroupItem item = new RadioGroupItem(name, I18N.message(label));
		item.setVertical(false);
		item.setShowTitle(true);
		item.setWrap(false);
		item.setWrapTitle(false);

		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		values.put("true", I18N.message("yes"));
		values.put("false", I18N.message("no"));
		item.setValueMap(values);
		item.setValue("true");

		return item;
	}

	public static SelectItem newTagInputMode(String name, String title) {
		SelectItem mode = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("free", I18N.message("free"));
		opts.put("preset", I18N.message("preset"));
		mode.setValueMap(opts);
		mode.setName(name);
		if (title != null)
			mode.setTitle(I18N.message(title));
		else
			mode.setShowTitle(false);
		mode.setDefaultValue("free");
		mode.setWidth(100);
		mode.setHintStyle("hint");
		return mode;
	}
	
    public static ImgButton newImgButton(String title, String iconName, int width, int height, boolean rollOver, boolean showDown) {
    	if (title == null) title = "";
    	ImgButton button = new ImgButton();  
        if (title.length() < 1)
        	button.setShowTitle(false);
        else {
        	button.setShowTitle(true);
        	button.setTitle(title);
        }

//        button.setShowDisabledIcon(false);
        if (iconName == null) iconName = "text";  
        String icon = ItemFactory.newImgIcon(iconName + ".png").getSrc();
        button.setSrc(icon);

//        button.setSize(size);
        button.setWidth(width);
        button.setHeight(height);
        button.setActionType(SelectionType.BUTTON);
        button.setShowRollOver(rollOver);
        button.setShowDown(showDown);
        
        return button;  
    }  

    public static ListGridItem newMultiValueItem(String name, String title, int width, int length, boolean beRequired) {
    	ListGridItem item = new ListGridItem(name, length, beRequired);  
    	
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		item.setName(itemName);
		item.setTitleVAlign(VerticalAlignment.TOP);
		item.setShowTitle(true);
		item.setTitle(name + " (" + title + ")");
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		item.setWidth(240);
		
		ListGridField field = new ListGridField(name, title, width);
		
		LengthRangeValidator valid = new LengthRangeValidator();
		valid.setMax(length);
		valid.setErrorMessage(I18N.message("invalidLength"));
		field.setValidators(valid);
		field.setValidateOnChange(true);

		item.setGridFields(field);
    	
    	return item;
    }
    
    public static ListGridMultipleItem newListGridMultipleItem(String name, String title,
    		String[] columnNames, String[] columnTitles, int[] length, int width, boolean beRequired) {
    	ListGridMultipleItem item = new ListGridMultipleItem(name, beRequired);  
    	
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		item.setName(itemName);
		item.setTitleVAlign(VerticalAlignment.TOP);
		item.setShowTitle(true);
		item.setTitle(title);
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setHintStyle("hint");
		item.setWidth(240);
		
		ListGridField[] fields = new ListGridField[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			fields[i] = new ListGridField(columnNames[i], columnTitles[i], width);

			LengthRangeValidator valid = new LengthRangeValidator();
			valid.setMax(length[i]);
			valid.setErrorMessage(I18N.message("invalidLength"));
			fields[i].setValidators(valid);
			fields[i].setValidateOnChange(true);

		}

		item.setGridFields(fields);
    	
    	return item;
    }

    // 20140121, junsoo, launcher 에 filename을 전달하고자 content를 전달하는 것으로 추가
	public static void setFileMenuByExt(final SDocument document, final SContent content, final String ext, final ReturnHandler returnHandler) {
		final SFileType filetype = (SFileType)DataCache.get(DataCache.FILEMENU.getId() + ext);
		final SDocument doc = document;
		final	SContent contents = content;
		if (filetype == null) {
			documentCodeService.getFileTypeByName(Session.get().getSid(), ext, new AsyncCallback<SFileType>() {
				@Override
				public void onSuccess(SFileType result) {
					// 서버에 존재하지 않으면 download만 가능하도록 함.
					if (result == null) {
						result = new SFileType();
						result.setViewer(SFileType.VIEWER_DOWNLOAD);
					}
					DataCache.put(DataCache.FILEMENU.getId() + ext, result);
					
					setFileMenuByExt(document, content, ext, returnHandler);
				}
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught, false);
//
//					// 서버오류면 다운로드만 가능하도록 함.
//					SFileType result = new SFileType();
//					result.setViewer(SFileType.VIEWER_DOWNLOAD);
//
//					DataCache.put(DataCache.FILEMENU.getId() + ext, result);
//					getFileMenuByExt(docId, elementId, ext);
				}
			});
			
			return;
		}
		
		// set menu
		Menu menu = new Menu();
		
		MenuItem internalViewer = new MenuItem();
		internalViewer.setTitle(I18N.message("second.edmViewer"));
		internalViewer.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
//				Util.preview(document.getId(), elementId, document.getFolder().isPrint());		
				if(doc.isView() || ((doc.isView() && filetype.getLinkViewer().equals("$Launcher$")))){
				Util.open(doc.getId(),contents.getElementId(),content.getFileName());
				}
				else
				Util.previewWindow(document.getId(), content.getElementId(), document.getFolder().isPrint());
			}
		});
		
		MenuItem osViewer = new MenuItem();
		osViewer.setTitle(I18N.message("second.osViewer"));
		osViewer.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				// TODO: os viewer 연동하는 어떤 방법?
				if (document instanceof SVersion) {
					Util.openVersion(((SVersion)document).getDocumentId(), ((SVersion)document).getId(), content.getElementId(), content.getFileName());
				}
				else
					Util.open(document.getId(), content.getElementId(), content.getFileName());
			}
		});
		
		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (document instanceof SVersion) {
					Util.downloadVersion(((SVersion)document).getDocumentId(), ((SVersion)document).getId(), content.getElementId());
				}
				else
					Util.downloadAsFrame(document.getId(), content.getElementId());
			}
		});

		// 권한!
		// 20131218, junsoo, 문서권한까지 적용함. (document.getFolder().isView() -> document.isView() 등으로 변경.)
		// 		잠금의 경우 view 및 download 방지
		boolean isProcessed = false;
		if(content.getProcessed() == SContent.PROCESS_TO_PROCESS
				|| content.getProcessed() == SContent.PROCESS_ERROR){
			isProcessed = true;
		}
		
//		boolean isProcessed = false;
//		SContent[] contents = document.getContents();
//		for (int i = 0; i < contents.length; i++) {
//			if(contents[i].getProcessed() == SContent.PROCESS_TO_PROCESS){
//				isProcessed = true;
//				break;
//			}
//		}
		internalViewer.setEnabled(!isProcessed && filetype.isInternalViewer() && document.isView());
		osViewer.setEnabled(!isProcessed && filetype.isOSViewer() && document.isView() && document.getStatus() != SDocument.DOC_LOCKED);
		download.setEnabled(!isProcessed && filetype.isDownload() && 
				(document.isDownload() || document.getFolder().getType() ==SFolder.TYPE_WORKSPACE)  
				&& document.getStatus() != SDocument.DOC_LOCKED
//				&& document.getStatus() != SDocument.DOC_CHECKED_OUT
				&& document.getType() !=  SDocument.TYPE_DELETED
				);
		
		boolean isOsViewer = Util.getSetting("setting.contextmenu.edmos");
		// kimsoeun GS인증용 - 문서 우클릭-EDM 기본 뷰어 숨기기
//		if(isOsViewer) menu.setItems(internalViewer, osViewer, download);
//		else menu.setItems(internalViewer, download);
		if(isOsViewer) menu.setItems(osViewer, download);
		else menu.setItems(download);
//			return menu;
		
		returnHandler.onReturn(menu);
	}

    /**
     * 비동기 방식이므로, 초기에 미리 호출하여 놓아야 캐쉬에 저장됨. 첫 호출 이후부터는 캐쉬에 저장된 내용을 활용함.
     * @param ext
     * @return
     */
	public static void setFileMenuByExt(final SDocument document, final SContent content,final String elementId, final String ext, final ReturnHandler returnHandler) {
		
		SFileType filetype = (SFileType)DataCache.get(DataCache.FILEMENU.getId() + ext);
		if (filetype == null) {
			documentCodeService.getFileTypeByName(Session.get().getSid(), ext, new AsyncCallback<SFileType>() {
				@Override
				public void onSuccess(SFileType result) {
					// 서버에 존재하지 않으면 download만 가능하도록 함.
					if (result == null) {
						result = new SFileType();
						result.setViewer(SFileType.VIEWER_DOWNLOAD);
					}
					DataCache.put(DataCache.FILEMENU.getId() + ext, result);
					
					setFileMenuByExt(document, content, elementId, ext, returnHandler);
				}
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught, false);
//
//					// 서버오류면 다운로드만 가능하도록 함.
//					SFileType result = new SFileType();
//					result.setViewer(SFileType.VIEWER_DOWNLOAD);
//
//					DataCache.put(DataCache.FILEMENU.getId() + ext, result);
//					getFileMenuByExt(docId, elementId, ext);
				}
			});
			
			return;
		}
		
		// set menu
		Menu menu = new Menu();
		
		MenuItem internalViewer = new MenuItem();
		internalViewer.setTitle(I18N.message("second.edmViewer"));
		internalViewer.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
//				Util.preview(document.getId(), elementId, document.getFolder().isPrint());
				Util.previewWindow(document.getId(), elementId, document.getFolder().isPrint());
			}
		});
		
		MenuItem osViewer = new MenuItem();
		osViewer.setTitle(I18N.message("second.osViewer"));
		osViewer.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				// TODO: os viewer 연동하는 어떤 방법?
				if (document instanceof SVersion) {
					Util.openVersion(((SVersion)document).getDocumentId(), ((SVersion)document).getId(), elementId);
				}
				else
					Util.open(document.getId(), elementId);
			}
		});
		
		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (document instanceof SVersion) {
					Util.downloadVersion(((SVersion)document).getDocumentId(), ((SVersion)document).getId(), elementId);
				}
				else
					Util.downloadAsFrame(document.getId(), elementId);
			}
		});

		// 권한!
		// 20131218, junsoo, 문서권한까지 적용함. (document.getFolder().isView() -> document.isView() 등으로 변경.)
		// 		잠금의 경우 view 및 download 방지
		//20140120na 후처리중인 문서는 파일 매뉴 권한이 없음
//		boolean isProcessed = false;
//		SContent[] contents = document.getContents();
//		for (int i = 0; i < contents.length; i++) {
//			if(contents[i].getProcessed() == SContent.PROCESS_TO_PROCESS){
//				isProcessed = true;
//				break;
//			}
//		}
		boolean isProcessed = false;
		if(content.getProcessed() == SContent.PROCESS_TO_PROCESS
				|| content.getProcessed() == SContent.PROCESS_ERROR){
			isProcessed = true;
		}
		
		internalViewer.setEnabled(!isProcessed && filetype.isInternalViewer() && document.getFolder().isView());
		osViewer.setEnabled(!isProcessed && filetype.isOSViewer() && document.isView() && document.getStatus() != SDocument.DOC_LOCKED);
		download.setEnabled(!isProcessed && filetype.isDownload() && 
				(document.isDownload() || document.getFolder().getType() ==SFolder.TYPE_WORKSPACE)  
				&& document.getStatus() != SDocument.DOC_LOCKED
//				&& document.getStatus() != SDocument.DOC_CHECKED_OUT
				&& document.getType() !=  SDocument.TYPE_DELETED
				);

		boolean isOsViewer = Util.getSetting("setting.contextmenu.edmos");
		// kimsoeun GS인증용 - 문서 우클릭-EDM 기본 뷰어 숨기기
//		if(isOsViewer) menu.setItems(internalViewer, osViewer, download);
//		else menu.setItems(internalViewer, download);
		if(isOsViewer) menu.setItems(osViewer, download);
		else menu.setItems(download);

//			return menu;
		
		returnHandler.onReturn(menu);
			
	}


//	public static SelectItem newFolderTemplateSelector() {
//		SelectItem item = new SelectItem("foldertemplate");
//		item.setTitle(I18N.message("ttemplate"));
//		item.setRequiredMessage(I18N.message("fieldrequired"));
//		item.setValueField("id");
//		item.setDisplayField("name");
//		item.setOptionDataSource(new FolderTemplatesDS());
//		if (!Feature.enabled(Feature.FOLDER_TEMPLATE))
//			item.setDisabled(true);
//		item.setHintStyle("hint");
//		return item;
//	}
}