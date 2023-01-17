package com.speno.xedm.gui.frontend.client.document;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;

/**
 * Settings Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class SettingsPanel extends VLayout implements RefreshObserver{	
	private static SettingsPanel instance;	
	
	private VLayout mainHL;
	private DynamicForm form;
	private VLayout actionHL;
	private PasswordItem  oldPasswordItem;
	private PasswordItem  newPasswordItem;
	private PasswordItem  verifyPasswordItem;
	private SelectItem pageSize;
	private SelectItem daysInTrash;	
	private SUser sessionUser ;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static SettingsPanel get() {
		if (instance == null) {
			instance = new SettingsPanel();
		}
		return instance;
	}
	
	public SettingsPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("dashboard")+" > "+ I18N.message("settings"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		sessionUser = Session.get().getUser();
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		mainHL = new VLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(createFormVL(),createActHL());
		addMember(mainHL);
		
		executeGetOptionsUserPazesizeSet();
		executeGetOptionsUserDaysintrashSet();
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {
		
		StaticTextItem idItem = new StaticTextItem("id", I18N.message("id"));
		idItem.setWidth(500);
		idItem.setCanEdit(true);
		idItem.setWrapTitle(false);
		idItem.setRequired(true);
		idItem.setValue(sessionUser.getUserName());
		
		TextItem nameItem = new TextItem("name", I18N.message("name"));
		nameItem.setWidth(500);
		nameItem.setCanEdit(true);
		nameItem.setWrapTitle(false);
		nameItem.setRequired(true);
		nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
		nameItem.setValue(sessionUser.getName());
		nameItem.setValidators(new LengthValidator(nameItem, Constants.MAX_LEN_NAME));
		
		oldPasswordItem = new PasswordItem ("oldPassword", I18N.message("password"));
		oldPasswordItem.setWidth(500);
		oldPasswordItem.setCanEdit(true);
		oldPasswordItem.setWrapTitle(false);
		oldPasswordItem.setRequired(true);
		oldPasswordItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		oldPasswordItem.setLength(Constants.MAX_LEN_NAME);
		oldPasswordItem.setValidators(new LengthValidator(oldPasswordItem, Constants.MAX_LEN_NAME));
		oldPasswordItem.setVisible(false);
		
		newPasswordItem = new PasswordItem ("newPassword", I18N.message("newpassword"));
		newPasswordItem.setWidth(500);
		newPasswordItem.setCanEdit(true);
		newPasswordItem.setWrapTitle(false);
		newPasswordItem.setRequired(true);
		newPasswordItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		newPasswordItem.setLength(Constants.MAX_LEN_NAME);
		newPasswordItem.setValidators(new LengthValidator(newPasswordItem, Constants.MAX_LEN_NAME));
		newPasswordItem.setVisible(false);
		
		verifyPasswordItem = new PasswordItem ("verifyPassword", I18N.message("newpasswordagain"));
		verifyPasswordItem.setWidth(500);
		verifyPasswordItem.setCanEdit(true);
		verifyPasswordItem.setWrapTitle(false);
		verifyPasswordItem.setRequired(true);
		verifyPasswordItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		verifyPasswordItem.setLength(Constants.MAX_LEN_NAME);
		verifyPasswordItem.setVisible(false);
		
		MatchesFieldValidator matchesValidator = new MatchesFieldValidator();  
        matchesValidator.setOtherField("newPassword");  
        matchesValidator.setErrorMessage(I18N.message("passwordsdonotmatch"));          
//        verifyPasswordItem.setValidators(matchesValidator);  
        verifyPasswordItem.setValidators(matchesValidator, new LengthValidator(verifyPasswordItem, Constants.MAX_LEN_NAME));
		
		ButtonItem changePasswordItem = new ButtonItem();
		
		changePasswordItem.setTitle(I18N.message("changepassword"));
		changePasswordItem.setWidth(120);
		changePasswordItem.setAlign(Alignment.RIGHT);		
		changePasswordItem.setStartRow(false);		
		changePasswordItem.setEndRow(true);
		
		changePasswordItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if(oldPasswordItem.getVisible()){
					oldPasswordItem.hide();
					newPasswordItem.hide();
					verifyPasswordItem.hide();
				}else{
					oldPasswordItem.show();
					newPasswordItem.show();
					verifyPasswordItem.show();
					
					oldPasswordItem.setValue("");
					newPasswordItem.setValue("");
					verifyPasswordItem.setValue("");
				}
				
			}
        });
		
		RegExpValidator emailValidator = new RegExpValidator();  
        emailValidator.setErrorMessage("Invalid email address");  
        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");  
        
		TextItem emailItem = new TextItem("email", I18N.message("email"));
		emailItem.setWidth(500);
		emailItem.setCanEdit(true);
		emailItem.setWrapTitle(false);
		emailItem.setRequired(true);
		emailItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		emailItem.setLength(Constants.MAX_LEN_NAME);
		emailItem.setValidators(emailValidator, new LengthValidator(emailItem, Constants.MAX_LEN_NAME));
//		emailItem.setValidators(emailValidator);
		emailItem.setValue(sessionUser.getEmail());
				
		pageSize = new SelectItem("pagesize", I18N.message("pagesize"));
		pageSize.setType("combobox");
		pageSize.setEmptyDisplayValue(I18N.message("choosetype"));
		
		daysInTrash = new SelectItem("daysintrash", I18N.message("second.daysintrash"));
		daysInTrash.setType("combobox");
		daysInTrash.setEmptyDisplayValue(I18N.message("choosetype"));
		daysInTrash.setHint(I18N.message("days"));
		
		TextItem smtpUserItem = new TextItem("smtpUser", I18N.message("smtpUser"));
		smtpUserItem.setWidth(500);
		smtpUserItem.setCanEdit(true);
		smtpUserItem.setWrapTitle(false);
//		smtpUserItem.setLength(Constants.MAX_LEN_NAME);
		smtpUserItem.setValidators(new LengthValidator(smtpUserItem, Constants.MAX_LEN_NAME));
		smtpUserItem.setValue(sessionUser.getSmtpUser());
		
		MatchesFieldValidator validator = new MatchesFieldValidator();   
        validator.setOtherField("smtpPasswordAgain");   
        validator.setErrorMessage(I18N.message("passwordsdonotmatch"));
		
        PasswordItem smtpPasswordItem = new PasswordItem("smtpPassword", I18N.message("smtpPassword"));
		smtpPasswordItem.setWidth(500);
		smtpPasswordItem.setCanEdit(true);
		smtpPasswordItem.setWrapTitle(false);
//		smtpPasswordItem.setLength(Constants.MAX_LEN_NAME);
//		smtpPasswordItem.setValue(sessionUser.getSmtpPassword());
		smtpPasswordItem.setValidators(validator);
		smtpPasswordItem.setValidators(validator, new LengthValidator(smtpPasswordItem, Constants.MAX_LEN_NAME));
		
        PasswordItem smtpPasswordAgain = new PasswordItem("smtpPasswordAgain", I18N.message("smtpPasswordAgain"));   
        smtpPasswordAgain.setRequired(false);
//        smtpPasswordAgain.setLength(Constants.MAX_LEN_NAME);
        smtpPasswordAgain.setValidators(new LengthValidator(smtpPasswordAgain, Constants.MAX_LEN_NAME));
		
		TextItem smtpHostItem = new TextItem("smtpHost", I18N.message("smtpHost"));
		smtpHostItem.setWidth(500);
		smtpHostItem.setCanEdit(true);
		smtpHostItem.setWrapTitle(false);
//		smtpHostItem.setLength(Constants.MAX_LEN_NAME);
		smtpHostItem.setValidators(new LengthValidator(smtpHostItem, Constants.MAX_LEN_NAME));
		smtpHostItem.setValue(sessionUser.getSmtpHost());
		
		RegExpValidator regExpIdValidator = new RegExpValidator();   
		regExpIdValidator.setExpression("[0-9]");
		
		TextItem smtpPortItem = new TextItem("smtpPort", I18N.message("smtpPort"));
		smtpPortItem.setWidth(500);
		smtpPortItem.setCanEdit(true);
		smtpPortItem.setWrapTitle(false);
		smtpPortItem.setValidators(regExpIdValidator, new LengthValidator(smtpPortItem, 6));
		smtpPortItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(null == event.getKeyName()){
					event.cancel();
					return;
				}else{
					String temp = event.getKeyName().toLowerCase();
					if(!temp.matches("[0-9]*")&&!temp.matches("backspace")&&!temp.matches("delete")&&!temp.matches("arrow_left")&&!temp.matches("arrow_right")){
						event.cancel();
						return;
					}
				}
			}
		});
		
		smtpPortItem.setValue(sessionUser.getSmtpPort());
        
		idItem.setStartRow(false);					idItem.setEndRow(false);		
		nameItem.setStartRow(false);				nameItem.setEndRow(false);
		oldPasswordItem.setStartRow(false);			oldPasswordItem.setEndRow(false);
		newPasswordItem.setStartRow(false);			newPasswordItem.setEndRow(false);
		verifyPasswordItem.setStartRow(false);		verifyPasswordItem.setEndRow(false);
		emailItem.setStartRow(false);				emailItem.setEndRow(false);
		smtpUserItem.setStartRow(false);			smtpUserItem.setEndRow(false);
		smtpPasswordItem.setStartRow(false);		smtpPasswordItem.setEndRow(false);
		smtpHostItem.setStartRow(false);			smtpHostItem.setEndRow(false);
		smtpPortItem.setStartRow(false);			smtpPortItem.setEndRow(false);
		
		if(!Util.getSetting("setting.email")){
    		emailItem.setDisabled(true);
    		smtpUserItem.setDisabled(true);
    		smtpPasswordItem.setDisabled(true);
    		smtpPasswordAgain.setDisabled(true);
    		smtpHostItem.setDisabled(true);
    		smtpPortItem.setDisabled(true);
    	}
		
		form = new DynamicForm();
		form.setMargin(10);
		form.setAutoWidth();
		form.setColWidths("200","1");
		form.setItems(idItem, nameItem, changePasswordItem, oldPasswordItem, newPasswordItem, verifyPasswordItem, emailItem,  pageSize, daysInTrash, smtpUserItem,
				smtpPasswordItem, smtpPasswordAgain, smtpHostItem, smtpPortItem);
//		form.reset();
		
    	VLayout formVL = new VLayout(10);
    	formVL.setBorder("1px solid gray");
    	formVL.setAutoWidth();
    	formVL.setAutoHeight();    	
    	formVL.addMembers(form);
    	return formVL;
	}
	
	/**
	 * Action Panel 생성
	 * @return
	 */
	private VLayout createActHL() {				
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
	    		 if(form.validate()) {
	    			 executeUpdate();
	    		 }
            }   
        });
		
		actionHL = new VLayout(10);
		actionHL.setHeight(10);
		actionHL.setAutoWidth();
		actionHL.setMembers(btnSave);		
		return actionHL;
	}
	
	/**
	 * 저장
	 */
	private void executeUpdate() {
		Log.debug("[ SettingsPanel executeUpdate ]");
		
		SUser sUser = new SUser();
		sUser.setId(sessionUser.getId());
		sUser.setUserName(form.getValueAsString("id"));
		sUser.setName(form.getValueAsString("name"));
		sUser.setPassword(form.getValueAsString("newPassword"));
		sUser.setOldPassword(form.getValueAsString("oldPassword"));
		sUser.setEmail(form.getValueAsString("email"));
		sUser.setPageSize(Integer.parseInt(pageSize.getValueAsString()));
		sUser.setDaysInTrash(Integer.parseInt(daysInTrash.getValueAsString()));
		sUser.setSmtpUser(form.getValueAsString("smtpUser"));
		sUser.setSmtpPassword(form.getValueAsString("smtpPassword"));
		sUser.setSmtpHost(form.getValueAsString("smtpHost"));
		sUser.setSmtpPort(Integer.parseInt(form.getValueAsString("smtpPort")));
		
		ServiceUtil.security().saveUserSetting(Session.get().getSid(), sUser, new AsyncCallbackWithStatus<SUser>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SUser result) {
				Log.debug("[ SettingsPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				oldPasswordItem.setValue("");
				newPasswordItem.setValue("");
				verifyPasswordItem.setValue("");
				sessionUser.setEmail(result.getEmail());
				sessionUser.setPageSize(result.getPageSize());
				sessionUser.setDaysInTrash(result.getDaysInTrash());
				sessionUser.setSmtpUser(result.getSmtpUser());
				sessionUser.setSmtpPassword(result.getSmtpPassword());
				sessionUser.setSmtpHost(result.getSmtpHost());
				sessionUser.setSmtpPort(result.getSmtpPort());
				
				
				ServiceUtil.security().login(Session.get().getSid(), new AsyncCallback<SSession>() {
					@Override
					public void onSuccess(SSession session) {
						Session.get().setSession(session);
					}
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
				SC.say(I18N.message("operationcompleted"));		
				
			}
		});
	}	
	
	/**
	 * 페이지 사이즈 코드 가져오기
	 */
	private void executeGetOptionsUserPazesizeSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		documentCodeService.listCodes(Session.get().getSid(), "USER_PAGESIZE", new AsyncCallback<List<SCode>>() {
			@Override
			public void onSuccess(List<SCode> result) {
				final LinkedHashMap<String, String> pOpts = new LinkedHashMap<String, String>() ;
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						pOpts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				
				pageSize.setValueMap(pOpts);
				pageSize.setDefaultValue(Integer.toString(sessionUser.getPageSize()));
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * 휴지통 보존 기간 코드 가져오기
	 */
	private void executeGetOptionsUserDaysintrashSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		documentCodeService.listCodes(Session.get().getSid(), "USER_DAYSINTRASH", new AsyncCallback<List<SCode>>() {
			@Override
			public void onSuccess(List<SCode> result) {
				final LinkedHashMap<String, String> dOpts = new LinkedHashMap<String, String>() ;
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						dOpts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				daysInTrash.setValueMap(dOpts);
				daysInTrash.setDefaultValue(Integer.toString(sessionUser.getDaysInTrash()));
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}

