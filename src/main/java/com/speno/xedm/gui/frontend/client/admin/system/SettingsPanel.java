package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.Date;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SParameter;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * Settings Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class SettingsPanel extends VLayout {	
	private static SettingsPanel instance = null;
	
	private VLayout mainPanel;
	private VLayout bodyPanel;

	private HLayout headerPanel;
	
	private Label navigation; //임시 Navigation
	
	LinkedHashMap<String, String> profileMap;
	
	private SectionStack sectionStack;
	
	private SectionStackSection generalSection;
	private SectionStackSection databaseSection;
	private SectionStackSection xvamSection; 
	
	private Button refreshBtn;
	private HLayout refreshHL;
	private Label lastUpdateLabel; 
	
	
	//General Settings 텍스트필드
    private TextItem itemIndexingDir;
    private TextItem itemViewerUrl;
    private TextItem itemDownloadPath;
    private TextItem itemDownloadUrl;
    private TextItem itemCopyRight;
    private TextItem itemBanner;
    private TextItem itemIpWhiteList;
    private TextItem itemIpBlackList;
    
    
    //Database Settings 텍스트필드
    private TextItem itemClass;
    private TextItem itemConnectionUrl;
    private TextItem itemUserId;
    private TextItem itemPassword;
    private TextItem itemValidation;
    private TextItem itemDbms;
    
    
    //Xvarm Settings 텍스트필드
    private TextItem itemIp;
    private TextItem itemPort;
    private TextItem itemXvamUserId;
    private TextItem itemXvamPassword;
    private TextItem itemGateway;
    private TextItem itemXvamDbms;   
    

	
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
	
	/**
	 * Settings Panel 생성
	 */
	public SettingsPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		createNavi(); 		//Navigator 생성
		createMainPanel(); 	//Main패널 생성
		
		setMembers(headerPanel, mainPanel);
		
		
		//화면에 컨트롤들이 다 그려지고 난 지금 시점에서 각 필드의 값들을 context.properties로부터 읽어와서 채워넣기...  Reload 시와 동일하게 이용
		setDataOnField();
		
	}
	
	/**
	 * 화면상의 각 필드에 데이터 넣기
	 */
	private void setDataOnField(){
		
		//판넬에 최종정보 갱신 일자 표시
		lastUpdateLabel.setContents("<b>" + I18N.message("lastupdate") + ": " + new Date() + "</b>");
		
		
		//General Setting 섹션 초기화
		setClearOnGeneralSettings();
		//Database Setting 섹션 초기화
		setClearOnDatabaseSettings();
		//Xvarm Setting 섹션 초기화
		setClearOnXvarmSettings();
		
		
		//General Setting 섹션에 데이터 넣기
		setDataOnGeneralSettings();
		//Database Setting 섹션에 데이터 넣기
		setDataOnDatabaseSettings();
		//Xvarm Setting 섹션에 데이터 넣기
		setDataOnXvarmSettings();
		
	}
	

	/**
	 * SParameter[] 를 이용하여 context.properties에 업데이트 하기
	 */
	private void runUpdateByParameter(SParameter[] objResult){
		ServiceUtil.system().saveSettings(Session.get().getSid(), objResult, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Log.serverError(caught.getMessage(), caught, true);
			}
			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				
				//업데이트 완료여부를 로그로 남기기
				Log.info(I18N.message("updatedelems"), null);
				
            	//화면 Refresh
            	setDataOnField();
			}
		});
		
	}
	
	
	
	
	/**
	 * SParameter[] 로부터 원하는 데이터 가져오기
	 */
	private String getValueFromParameter(SParameter[] objResult, String sName){
		
		String sRtnValue = "";
		
		//SParameter[] 가 name, value 형식의 배열이므로
		for(int i=0; i<objResult.length;i++){
			//외부에서 넘겨받은 문구와 name 이 일치하면 value 값 반환
			if(sName.equals(objResult[i].getName())){
				sRtnValue = objResult[i].getValue();
				break;
			}
		}
		return sRtnValue;
	}
	
	
	
	/**
	 * General Setting 섹션 초기화
	 */
	private void setClearOnGeneralSettings(){
		itemIndexingDir.setValue("");
		itemViewerUrl.setValue("");
		itemDownloadPath.setValue("");
		itemDownloadUrl.setValue("");
		itemCopyRight.setValue("");
		itemBanner.setValue("");
		itemIpWhiteList.setValue("");
		itemIpBlackList.setValue("");
	}
	
	
	/**
	 * Database Setting 섹션 초기화
	 */
	private void setClearOnDatabaseSettings(){
		itemClass.setValue("");
		itemConnectionUrl.setValue("");
		itemUserId.setValue("");
		itemPassword.setValue("");
		itemValidation.setValue("");
		itemDbms.setValue("");
	}
	
	
	/**
	 * Xvarm Setting 섹션 초기화
	 */
	private void setClearOnXvarmSettings(){
		itemIp.setValue("");
		itemPort.setValue("");
		itemXvamUserId.setValue("");
		itemXvamPassword.setValue("");
		itemGateway.setValue("");
		itemXvamDbms.setValue(""); 
	}
	
	
	
	/**
	 * General Setting 섹션에 데이터 넣기
	 */
	private void setDataOnGeneralSettings(){
		ServiceUtil.system().loadSettings(Session.get().getSid(), new AsyncCallback<SParameter[]>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Log.serverError( I18N.message("genericerror"), caught, true);
			}
			@Override
			public void onSuccess(SParameter[] result) {
				// TODO Auto-generated method stub
				
				// indexing dir  : settings.index.dir=d:/temp/index
				// viewer url    : settings.viewer.url=/frontend/viewer/viewersat.jsp?url=param_url&filename=param_filename
				// download path : settings.document.download.path=d:/temp/down
				// download url  : settings.document.download.urlpath=/xedmdown/down
				// CopyRight     : settings.product.copyright=copyright Spenocom ALL RIGHTS RESERVED.
				// Banner        : settings.product.banner=logo.png
				// ip white list : settings.ip.whitelist=
				// ip black list : settings.ip.blacklist=
				
				String rtnVal = "";
				
				
				//General Setting 섹션 초기화
				setClearOnGeneralSettings();
				
				rtnVal = getValueFromParameter(result, "settings.index.dir");
				itemIndexingDir.setValue(rtnVal);

				rtnVal = getValueFromParameter(result, "settings.viewer.url");
				itemViewerUrl.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "settings.document.download.path");
				itemDownloadPath.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "settings.document.download.urlpath");
				itemDownloadUrl.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "settings.product.copyright");
				itemCopyRight.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "settings.product.banner");
				itemBanner.setValue(rtnVal);

				rtnVal = getValueFromParameter(result, "settings.ip.whitelist");
				itemIpWhiteList.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "settings.ip.blacklist");
				itemIpBlackList.setValue(rtnVal);
			}
		});
	}

	

	/**
	 * Database Setting 섹션에 데이터 넣기
	 */
	private void setDataOnDatabaseSettings(){
		ServiceUtil.system().loadDBSettings(Session.get().getSid(), new AsyncCallback<SParameter[]>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Log.serverError( I18N.message("genericerror"), caught, true);
			}
			@Override
			public void onSuccess(SParameter[] result) {
				// TODO Auto-generated method stub
				
				// Class          : jdbc.driver=oracle.jdbc.driver.OracleDriver
				// Connection Url : jdbc.url=jdbc:oracle:thin:@10.1.61.43:1521:XE
				// User Id        : jdbc.username=XEDM
				// Password       : jdbc.password=XEDM
				// Validation     : jdbc.validationQuery=select * from dual
				// dbms           : jdbc.dbms=oracle
				
				String rtnVal = "";

			    
				//Database Setting 섹션 초기화
				setClearOnDatabaseSettings();
				
				rtnVal = getValueFromParameter(result, "jdbc.driver");
				itemClass.setValue(rtnVal);

				rtnVal = getValueFromParameter(result, "jdbc.url");
				itemConnectionUrl.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "jdbc.username");
				itemUserId.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "jdbc.password");
				itemPassword.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "jdbc.validationQuery");
				itemValidation.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "jdbc.dbms");
				itemDbms.setValue(rtnVal);

			}
		});
	}
	

	
	/**
	 * Xvarm Setting 섹션에 데이터 넣기
	 */
	private void setDataOnXvarmSettings(){
		ServiceUtil.system().loadECMSettings(Session.get().getSid(), new AsyncCallback<SParameter[]>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Log.serverError( I18N.message("genericerror"), caught, true);
			}
			@Override
			public void onSuccess(SParameter[] result) {
				// TODO Auto-generated method stub
				
				// IP       : ecm.ip=10.1.61.6,10.1.61.6
				// Port     : ecm.port=2102,2102
				// User Id  : ecm.username=SUPER,SUPER
				// Password : ecm.password=SUPER,SUPER
				// Gateway  : ecm.gateway=XVARM_MAIN
				// dbms     : ecm.db=oracle
				
				String rtnVal = "";

				//Xvarm Setting 섹션 초기화
				setClearOnXvarmSettings();
				
				rtnVal = getValueFromParameter(result, "ecm.a.ip");
				itemIp.setValue(rtnVal);

				rtnVal = getValueFromParameter(result, "ecm.b.port");
				itemPort.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "ecm.c.username");
				itemXvamUserId.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "ecm.d.password");
				itemXvamPassword.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "ecm.e.gateway");
				itemGateway.setValue(rtnVal);
				
				rtnVal = getValueFromParameter(result, "ecm.f.db");
				itemXvamDbms.setValue(rtnVal);
			}
		});
	}
	
	
	
	
	/**
	 * Navigator 생성
	 */
	private void createNavi() {

		//navigation===================================================
		navigation = new Label();   
		navigation.setHeight(20);   
		navigation.setPadding(10);   
		navigation.setAlign(Alignment.LEFT);   
		navigation.setValign(VerticalAlignment.CENTER);   
		navigation.setWrap(true);   
		navigation.setShowEdges(false);   
		navigation.addStyleName("subTitle");
		navigation.setContents(I18N.message("admin") + " > " + I18N.message("system") + " > " + I18N.message("settings"));
		navigation.setWidth("50%");
		//=============================================================
		

		//Refresh======================================================
		refreshBtn = new Button(I18N.message("refresh"));
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	//화면 Refresh
            	setDataOnField();
            }
        });
		
		lastUpdateLabel = new Label();
		lastUpdateLabel.setHeight(refreshBtn.getHeight());
		lastUpdateLabel.setWidth100();
		lastUpdateLabel.setAlign(Alignment.RIGHT);
		
		refreshHL = new HLayout(10);
		refreshHL.setWidth("50%");
		refreshHL.setAutoHeight();
		refreshHL.setAlign(Alignment.RIGHT);
		refreshHL.addMembers(lastUpdateLabel, refreshBtn);		
		//=============================================================
		
		
		headerPanel = new HLayout();
		headerPanel.setMembersMargin(10);
		headerPanel.addMember(navigation);
		headerPanel.addMember(refreshHL);
		headerPanel.setAlign(Alignment.RIGHT);
		
	}
	
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel() {
		createBodyPanel(); //Body패널 생성
		
		mainPanel = new VLayout();
        mainPanel.setHeight100();        
        mainPanel.setMembers(bodyPanel);
	}	
	
	/**
	 * Body패널 생성
	 */
	private void createBodyPanel() {
		createSectionStack(); //섹션 Stack 생성
	    
        bodyPanel= new VLayout();
        bodyPanel.setMembers(sectionStack);
	}
		
	/**
	 * 섹션 Stack 생성
	 */
	private void createSectionStack() {
        createGeneralSection();
        createDatabaseSection();
        createXvamSection(); 
  
        sectionStack = new SectionStack();
        sectionStack.setSections(generalSection, databaseSection, xvamSection);   
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);   
        sectionStack.setAnimateSections(true);   
        sectionStack.setWidth(610);   
        sectionStack.setHeight100();
        sectionStack.setCanResizeSections(false);
        sectionStack.setOverflow(Overflow.AUTO);
	} 
	
	/**
	 * General Settings 섹션 생성
	 */
	private void createGeneralSection() {		

		final IButton btnApply = new IButton(I18N.message("apply"));
		btnApply.setIcon(ItemFactory.newImgIcon("accept.png").getSrc());
		btnApply.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
				
            	// indexing dir  : settings.index.dir=d:/temp/index
				// viewer url    : settings.viewer.url=/frontend/viewer/viewersat.jsp?url=param_url&filename=param_filename
				// download path : settings.document.download.path=d:/temp/down
				// download url  : settings.document.download.urlpath=/xedmdown/down
				// CopyRight     : settings.product.copyright=copyright Spenocom ALL RIGHTS RESERVED.
				// Banner        : settings.product.banner=logo.png
				// ip white list : settings.ip.whitelist=
				// ip black list : settings.ip.blacklist=

				//파라미터 세팅
				SParameter[] parameters = new SParameter[8];
				
				SParameter param1 = new SParameter("settings.index.dir", itemIndexingDir.getValueAsString());
				SParameter param2 = new SParameter("settings.viewer.url", itemViewerUrl.getValueAsString());
				SParameter param3 = new SParameter("settings.document.download.path", itemDownloadPath.getValueAsString());
				SParameter param4 = new SParameter("settings.document.download.urlpath", itemDownloadUrl.getValueAsString());
				SParameter param5 = new SParameter("settings.product.copyright", itemCopyRight.getValueAsString());
				SParameter param6 = new SParameter("settings.product.banner", itemBanner.getValueAsString());
				SParameter param7 = new SParameter("settings.ip.whitelist", itemIpWhiteList.getValueAsString());
				SParameter param8 = new SParameter("settings.ip.blacklist", itemIpBlackList.getValueAsString());
				
				parameters[0] = param1;
				parameters[1] = param2;
				parameters[2] = param3;
				parameters[3] = param4;
				parameters[4] = param5;
				parameters[5] = param6;
				parameters[6] = param7;
				parameters[7] = param8;

				runUpdateByParameter(parameters);
				
            }   
        });
  
        final IButton btnCancel = new IButton(I18N.message("cancel"));   
        btnCancel.setIcon(ItemFactory.newImgIcon("generic.png").getSrc());   
        btnCancel.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	//General Setting 섹션 초기화 및 정보 리로드
				setDataOnGeneralSettings();
            }   
        });
        
		HLayout hLayout = new HLayout();
		hLayout.setMembersMargin(10);
		hLayout.addMember(btnApply);
		hLayout.addMember(btnCancel);
		hLayout.setAlign(Alignment.RIGHT);
		
		
		
        itemIndexingDir = new TextItem();
        itemIndexingDir.setTitle("indexing dir");
        itemIndexingDir.setWidth(500);
        
        itemViewerUrl = new TextItem();
        itemViewerUrl.setTitle("viewer url");
        itemViewerUrl.setWidth(500);
        
        itemDownloadPath = new TextItem();
        itemDownloadPath.setTitle("download path");
        itemDownloadPath.setWidth(500);
        
        itemDownloadUrl = new TextItem();
        itemDownloadUrl.setTitle("download url");
        itemDownloadUrl.setWidth(500);
        
        itemCopyRight = new TextItem();
        itemCopyRight.setTitle("CopyRight");
        itemCopyRight.setWidth(500);
        
        itemBanner = new TextItem();
        itemBanner.setTitle("Banner");
        itemBanner.setWidth(500);
        
        itemIpWhiteList = new TextItem();
        itemIpWhiteList.setTitle("ip white list");
        itemIpWhiteList.setWidth(500);
        
        itemIpBlackList = new TextItem();
        itemIpBlackList.setTitle("ip black list");
        itemIpBlackList.setWidth(500);
        
        
		DynamicForm formGeneral = new DynamicForm();   
		formGeneral.setHeight100();   
		formGeneral.setWidth100();   
		formGeneral.setNumCols(2);
		formGeneral.setFields(itemIndexingDir, itemViewerUrl, itemDownloadPath, itemDownloadUrl, itemCopyRight, itemBanner, itemIpWhiteList, itemIpBlackList);
        
		
		VLayout vLayout = new VLayout();
		vLayout.setMembersMargin(5);
		vLayout.setMargin(5);
		vLayout.addMember(formGeneral);
		vLayout.addMember(hLayout);
		vLayout.draw();
		
		
        generalSection = new SectionStackSection();   
		generalSection.setTitle("General Settings");
		generalSection.setItems(vLayout);
		generalSection.setExpanded(true);   

	}

	
	/**
	 * Database Settings 섹션 생성
	 */
	private void createDatabaseSection() {

		final IButton btnApply = new IButton(I18N.message("apply"));
		btnApply.setIcon(ItemFactory.newImgIcon("accept.png").getSrc());
		btnApply.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
				
				// Class          : jdbc.driver=oracle.jdbc.driver.OracleDriver
				// Connection Url : jdbc.url=jdbc:oracle:thin:@10.1.61.43:1521:XE
				// User Id        : jdbc.username=XEDM
				// Password       : jdbc.password=XEDM
				// Validation     : jdbc.validationQuery=select * from dual
				// dbms           : jdbc.dbms=oracle

				//파라미터 세팅
				SParameter[] parameters = new SParameter[6];
				
				SParameter param1 = new SParameter("jdbc.driver", itemClass.getValueAsString());
				SParameter param2 = new SParameter("jdbc.url", itemConnectionUrl.getValueAsString());
				SParameter param3 = new SParameter("jdbc.username", itemUserId.getValueAsString());
				SParameter param4 = new SParameter("jdbc.password", itemPassword.getValueAsString());
				SParameter param5 = new SParameter("jdbc.validationQuery", itemValidation.getValueAsString());
				SParameter param6 = new SParameter("jdbc.dbms", itemDbms.getValueAsString());
				
				parameters[0] = param1;
				parameters[1] = param2;
				parameters[2] = param3;
				parameters[3] = param4;
				parameters[4] = param5;
				parameters[5] = param6;

				runUpdateByParameter(parameters);
				
            }   
        });
  
        final IButton btnCancel = new IButton(I18N.message("cancel"));   
        btnCancel.setIcon(ItemFactory.newImgIcon("generic.png").getSrc()); 
        btnCancel.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	//Database Setting 섹션 초기화 및 정보 리로드
				setDataOnDatabaseSettings();
            }   
        });
        
		HLayout hLayout = new HLayout();
		hLayout.setMembersMargin(10);
		hLayout.addMember(btnApply);
		hLayout.addMember(btnCancel);
		hLayout.setAlign(Alignment.RIGHT);
		
		
        itemClass = new TextItem();
        itemClass.setTitle("Class");
        itemClass.setWidth(500);
        
        itemConnectionUrl = new TextItem();
        itemConnectionUrl.setTitle("Connection Url");
        itemConnectionUrl.setWidth(500);
        
        itemUserId = new TextItem();
        itemUserId.setTitle("User Id");
        itemUserId.setWidth(500);
        
        itemPassword = new TextItem();
        itemPassword.setTitle("Password");
        itemPassword.setWidth(500);
        
        itemValidation = new TextItem();
        itemValidation.setTitle("Validation");
        itemValidation.setWidth(500);
        
        itemDbms = new TextItem();
        itemDbms.setTitle("dbms");
        itemDbms.setWidth(500);
        
        
		DynamicForm formDatabase = new DynamicForm();   
		formDatabase.setHeight100();   
		formDatabase.setWidth100();   
		formDatabase.setNumCols(2);
		formDatabase.setFields(itemClass, itemConnectionUrl, itemUserId, itemPassword, itemValidation, itemDbms);
 
		
		VLayout vLayout = new VLayout();
		vLayout.setMembersMargin(5);
		vLayout.setMargin(5);
		vLayout.addMember(formDatabase);
		vLayout.addMember(hLayout);
		vLayout.draw();
		
		
		databaseSection = new SectionStackSection();   
		databaseSection.setTitle("Database Settings");
		databaseSection.setItems(vLayout);  
		databaseSection.setExpanded(true);

	}
	
	
	/**
	 * Xvarm Settings 섹션 생성
	 */
	private void createXvamSection() {

		final IButton btnApply = new IButton(I18N.message("apply"));
		btnApply.setIcon(ItemFactory.newImgIcon("accept.png").getSrc());
		btnApply.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
				
				// IP       : ecm.ip=10.1.61.6,10.1.61.6
				// Port     : ecm.port=2102,2102
				// User Id  : ecm.username=SUPER,SUPER
				// Password : ecm.password=SUPER,SUPER
				// Gateway  : ecm.gateway=XVARM_MAIN
				// dbms     : ecm.db=oracle

				//파라미터 세팅
				SParameter[] parameters = new SParameter[6];
				
				SParameter param1 = new SParameter("ecm.ip", itemIp.getValueAsString());
				SParameter param2 = new SParameter("ecm.port", itemPort.getValueAsString());
				SParameter param3 = new SParameter("ecm.username", itemXvamUserId.getValueAsString());
				SParameter param4 = new SParameter("ecm.password", itemXvamPassword.getValueAsString());
				SParameter param5 = new SParameter("ecm.gateway", itemGateway.getValueAsString());
				SParameter param6 = new SParameter("ecm.db", itemXvamDbms.getValueAsString());
				
				parameters[0] = param1;
				parameters[1] = param2;
				parameters[2] = param3;
				parameters[3] = param4;
				parameters[4] = param5;
				parameters[5] = param6;

				runUpdateByParameter(parameters);
				
            }   
        });
  
        final IButton btnCancel = new IButton(I18N.message("cancel"));   
        btnCancel.setIcon(ItemFactory.newImgIcon("generic.png").getSrc());   
        btnCancel.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	//Xvarm Setting 섹션 초기화 및 정보 리로드
				setDataOnXvarmSettings();
            }   
        });
        
		HLayout hLayout = new HLayout();
		hLayout.setMembersMargin(10);
		hLayout.addMember(btnApply);
		hLayout.addMember(btnCancel);
		hLayout.setAlign(Alignment.RIGHT);
		
		
        itemIp = new TextItem();
        itemIp.setTitle("IP");
        itemIp.setWidth(500);
        
        itemPort = new TextItem();
        itemPort.setTitle("Port");
        itemPort.setWidth(500);
        
        itemXvamUserId = new TextItem();
        itemXvamUserId.setTitle("User Id");
        itemXvamUserId.setWidth(500);
        
        itemXvamPassword = new TextItem();
        itemXvamPassword.setTitle("Password");
        itemXvamPassword.setWidth(500);
        
        itemGateway = new TextItem();
        itemGateway.setTitle("Gateway");
        itemGateway.setWidth(500);
        
        itemXvamDbms = new TextItem();
        itemXvamDbms.setTitle("dbms");
        itemXvamDbms.setWidth(500);
        
        
        
		DynamicForm formXvarm = new DynamicForm();   
		formXvarm.setHeight100();   
		formXvarm.setWidth100();   
		formXvarm.setNumCols(2);
		formXvarm.setFields(itemIp, itemPort, itemXvamUserId, itemXvamPassword, itemGateway, itemXvamDbms);
		
		
		VLayout vLayout = new VLayout();
		vLayout.setMembersMargin(5);
		vLayout.setMargin(5);
		vLayout.addMember(formXvarm);
		vLayout.addMember(hLayout);
		vLayout.draw();
		
		xvamSection = new SectionStackSection();   
		xvamSection.setTitle("Xvarm Settings");
		xvamSection.setItems(vLayout);  
		xvamSection.setExpanded(true);
		
	}
}