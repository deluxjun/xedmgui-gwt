package com.speno.xedm.gui.common.client;

public final class Constants {
	// The currenly logged user name
	public static final String AUTH_USERNAME = "authUser";

	// The currenly logged user identifier
	public static final String AUTH_USERID = "authUserId";

	// The currenly logged user password
	public static final String AUTH_PASSWORD = "authPassword";

	// The current user session
	public static final String USER_SESSION = "UserSession";

	// The language of the currently logged user
	public static final String LANGUAGE = "language";

	// Sessions the context key of the sessions map
	public static final String SESSIONS = "sessions";

	public static final long DOCUMENTS_FOLDERID = 5;

	public static final long WORKSPACE_DEFAULTID = 4;

	public static final long SHARED_DEFAULTID = 3;
	
	public static final String PERMISSION_DELETE = "delete";

	public static final String PERMISSION_CONTROL	= "control";

	public static final String PERMISSION_WRITE = "write";

	public static final String PERMISSION_ADD = "add";

	public static final String PERMISSION_RENAME = "rename";

	public static final String PERMISSION_DOWNLOAD = "download";

	public static final String PERMISSION_EXTEND = "extend";

	public static final String PERMISSION_CHECK = "check";
	
	public static final String PERMISSION_PRINT = "print";

	public static final String PERMISSION_VIEW = "view";
	
	
	public static final int DOC_UNLOCKED = 0;

	public static final int DOC_CHECKED_OUT = 1;

	public static final int DOC_LOCKED = 2;

	public static final String SMTP_SECURITY_NONE = "0";

	public static final String SMTP_SECURITY_TLS_IF_AVAILABLE = "1";

	public static final String SMTP_SECURITY_TLS = "2";

	public static final String SMTP_SECURITY_SSL = "3";

	public static final String KEY_ENTER = "enter";

	public static final String GROUP_ADMIN = "admin";

	public static final String GROUP_PUBLISHER = "publisher";
	
	public final static String EVENT_LOCKED = "event.locked";

	public final static String EVENT_CHECKEDOUT = "event.checkedout";

	public static final String EVENT_DOWNLOADED = "event.downloaded";

	public final static String EVENT_CHANGED = "update";

	public final static String EVENT_CHECKEDIN = "event.checkedin";

	public final static String EVENT_NOTICE = "event.notice";

	public final static String EVENT_MESSAGE = "event.messege";
	
	public final static String EVENT_RECENT = "event.recent";
	
	public final static int INDEX_TO_INDEX = 0;

	public final static int INDEX_INDEXED = 1;

	// The document is un-indexable
	public final static int INDEX_SKIP = 2;

	public static final String LOCALE = "locale";

	public static final String BLANK_PLACEHOLDER = "___";
	
	public static final String COOKIE_HITSLIST = "xedm-hitslist";
	
	public static final String COOKIE_DOCSLIST = "xedm-docslist";
	
	public static final String COOKIE_DOCSLIST_MAX = "xedm-docslist-max";
	
	public static final String COOKIE_DOCSMENU_W = "xedm-docsmenu-w";
	
	public static final String COOKIE_SAVELOGIN = "xedm-savelogin";
	
	public static final String COOKIE_USER = "xedm-user";
	
	public static final String COOKIE_PASSWORD = "xedm-password";
	
	public static final String COOKIE_VERSION = "xedm-version";
	
	public static final String COOKIE_SID = "xedm-sid";
	
	public static final String COOKIE_LANGUAGE = "";
	
//	public final static int DASHLET_CHECKOUT = 1;
//
//	public final static int DASHLET_CHECKIN = 2;
//
//	public final static int DASHLET_LOCKED = 3;
//	
//	public final static int DASHLET_DOWNLOADED = 4;
//
//	public final static int DASHLET_CHANGED = 5;
//	
//	public final static int DASHLET_LAST_NOTES = 6;
//	
//	public final static int DASHLET_TAGCLOUD = 7;
	
	public final static int DASHLET_NOTICE = 0;
	public final static int DASHLET_LOCKED = 1;
	public final static int DASHLET_MESSAGE= 2;
	public final static int DASHLET_CHECKOUT= 3;
	public final static int DASHLET_RECENT= 4;

	public final static String PORTAL_NOTICE = "notice";
	public final static String PORTAL_LOCKED = "lockeddocuments";
	public final static String PORTAL_MESSAGES= "uncheckedMessage";
	public final static String PORTAL_CHECKOUT= "CheckedOut";
	public final static String PORTAL_RECENT= "recentdoc";
	
	// 20130820, junsoo, dashboard 메뉴
	public final static String DASHBOARD_HOME = "dash_home";
	public final static String DASHBOARD_MESSAGE = "dash_messages";
	public final static String DASHBOARD_SHARING = "dash_sharing";
	public final static String DASHBOARD_SETTINGS = "dash_settings";
	public final static String DASHBOARD_MESSAGE_NOTICE = "notice";
	public final static String DASHBOARD_MESSAGE_RECEIVED = "receivedbox";
	public final static String DASHBOARD_MESSAGE_SENT = "sentbox";

	
	//Admin Root Info
	public static final long ADMIN_ROOT = -100;	
	public static final String ADMIN_GROUP_ROOT = "@@@0";
	public static final long ADMIN_FOLDER_ROOT = 3L;
	
	public static final long INVALID_LONG = -1L;
	
	//Field max length
	public static final int MAX_LEN_ID = 255;	
	public static final int MAX_LEN_NAME = 230;
	public static final int MAX_LEN_DOCUMENTtYPE_NAME = 30;
	public static final int MAX_LEN_DESC = 1000;
	public static final int MAX_STRINGVALUE = 4000;
	public final static int MAX_INTVALUE = 19;
	public final static int MAX_DOUBLEVALUE = 22;
	public static final int MAX_LEN_MODULE = 100;
	
	public static final int INFO_TIMER = 20;
	
	public final static int FOLDER_TYPE_ETC = 3;
	public final static int FOLDER_TYPE_XVARM = 2;
	public final static int FOLDER_TYPE_MYDOC = 1;
	public final static int FOLDER_TYPE_SHARED = 0;
	public final static int FOLDER_TYPE_ALL = 9;
	
	
	public final static int LIST_LINE_FILE_COUNT = 5;
	
	public final static String INVALID_SESSION_ERR01 = "SEC0001";
	public final static String INVALID_SESSION_ERR02 = "SEC0002";
	
	public final static String SEARCH_CONTENT_COLOR = "#789EEF";
	
	public final static int PADDING_DEFAULT = 5;
	public final static int SUBTITLE_MARGIN = 5;
	public final static int ARROW_MARGIN = 10;
	
	
	// 검색 Action 동작용 변수
	// taesu
	public final static int FOLDER_PATH = 1;
	public final static int OWNER = 2;
	
	// 검색 Place 구분 변수
	public final static int SEARCH_PLACE_DEFAULT= 0;
	public final static int SEARCH_PLACE_SHAREDTRASH = 1;
	public final static int SEARCH_PLACE_SEARCH = 2;
	// 업로드 Template type 구분 변수
	// taesu
	public final static int TYPE_STRING = 0;
	public final static int TYPE_INT= 1;
	public final static int TYPE_DOUBLE= 2;
	public final static int TYPE_DATE = 3;
	
	// ExtAttribute 값을 보여주는 그리드 구분 변수
	// taesu
	public final static int PANEL_STANDARD_PROPERTY_DOCUMENT= 0;
	public final static int PANEL_STANDARD_PROPERTY_TEMPLATE = 1;
	public final static int PANEL_UPLOADER = 2;
	
	// Folder 구분 변수
	public final static String FOLDER_SHARED = "3";
	
	// Search Section 구분 변수
	public final static int SEARCH_SECTION_NORMAL	= 10;
	public final static int SEARCH_SECTION_SAVED	= 11;
	public final static int SEARCH_SECTION_ECM		= 12;
	
	/*
	 * 문서 정렬방법 구분
	 * */
	public final static String ORDER_BY_TITLE_DESC			= "title/DESC";
	public final static String ORDER_BY_DOCTYPE_DESC		= "docType/DESC";
	public final static String ORDER_BY_CREATEDATE_DESC		= "creationDate/DESC";
	public final static String ORDER_BY_MODIFIEDDATE_DESC	= "lastModified/DESC";
	public final static String ORDER_BY_RETENTION_DESC		= "expireDate/DESC";
	public final static String ORDER_BY_VERSION_DESC		= "version/DESC";
	public final static String ORDER_BY_LOCK_DESC			= "status/DESC";
	
	public final static String ORDER_BY_TITLE_ASC			= "title/ASC";
	public final static String ORDER_BY_DOCTYPE_ASC			= "docType/ASC";
	public final static String ORDER_BY_CREATEDATE_ASC		= "creationDate/ASC";
	public final static String ORDER_BY_MODIFIEDDATE_ASC	= "lastModified/ASC";
	public final static String ORDER_BY_RETENTION_ASC		= "expireDate/ASC";
	public final static String ORDER_BY_VERSION_ASC			= "version/ASC";
	public final static String ORDER_BY_LOCK_ASC			= "status/ASC";
	
	// Search Tab에서 현재 Tab 구분
	public final static int CURRENT_TAB_PERSONAL = 0;
	public final static int CURRENT_TAB_SHARED = 1;
	
	// Main Tab(속성창 보기용)
	public final static int MAIN_TAB_DOCUMENT = 1;
	public final static int MAIN_TAB_SEARCH = 2;
	public final static int MAIN_TAB_ADMIN = 3;
	
	// 20130822, junsoo, documents의 메뉴 ID (DB와 일치해야함.)
	public final static long MENUID_ROOT = 0L;
	public final static long MENUID_DOCUMENTS = 50L;
	public final static long MENUID_DOCUMENTS_ETC = 53L;

	// 20130903, junsoo, 메뉴 Name (es_adminmenu 테이블과 동일함)
	public final static String MENU_DOCUMENTS	= "documents";
	public final static String MENU_ADMIN		= "admin";
	public final static String MENU_STATS		= "stats";
	public final static String MENU_SEARCHS		= "searchs";
	// kimsoeun GS인증용 - 본문 검색 탭 숨기기	
	public final static String MENU_FULLTEXTSEARCHS		= "searchs_fulltext";
	public final static String MENU_DASHBOARD	= "dashboard";
	public final static String MENU_DOCUMENTS_ETC	= "documents_etc";
	public final static String MENU_DOCUMENTS_MYDOC	= "documents_mydoc";
	public final static String MENU_DOCUMENTS_SHAREDDOC	= "documents_shareddoc";
	
	// 20130829, taesu, Draft Type 지정(서버에 지정되어있는 것과 같음)
	public final static int DRAFT_TYPE_REGISTRATION = 0;
	public final static int DRAFT_TYPE_DELETE 		= 1;
	public final static int DRAFT_TYPE_DOWNLOAD		= 2;
	public final static int DRAFT_TYPE_CHECKOUT		= 5;
	public final static int DRAFT_TYPE_CHECKIN		= 6;
	
	// 20130830, taesu, Message Type지정
	public final static int MESSAGE_SEND = 1;
	public final static int MESSAGE_VIEW = 2;
	public final static int MESSAGE_NOTICE = 3;
	public final static int MESSAGE_CONTAIN_FILES= 4;
	
	// 20130924, taesu, rewrite command
	public final static int REWRITE_COMMAND_REGISTRATION 		= 0;
	public final static int REWRITE_COMMAND_DELETE				= 1;
	public final static int REWRITE_COMMAND_DOWNLOAD			= 2;
	public final static int REWRITE_COMMAND_CHECKOUT			= 5;
	
	// 20130924, taesu, rewrite status
	public final static int REWRITE_STATUS_COMPLETE	= -1;
	public final static int REWRITE_STATUS_PROGRESS				= 0;
	public final static int REWRITE_STATUS_COMPLETE_APPROVAL	= 1;
	public final static int REWRITE_STATUS_COMPLETE_RETURN		= 2;
	public final static int REWRITE_STATUS_COMTLETE_RECOVERY	= 3;
}