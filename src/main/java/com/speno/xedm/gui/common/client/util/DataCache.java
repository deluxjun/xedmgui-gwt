package com.speno.xedm.gui.common.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 서버로 부터 같은 정보를 자주 획득하여 사용하는 data 집합소.
 * @author deluxjun
 *
 */
public enum DataCache {
	DOCTYPES("doctypes"),
	FILETYPES("filetypes"),
	RETENTIONS("retentions"),
	INDEXFIELDS("index_fields"),
	TEMPLATES("templates"),
	FILEMENU("filemenu"),
	TEMPLATE_ATTRS("template_attrs"),
	ECM_INDEX_FIELDS("ecm_index_fields"),
	DOCUMENT_CODES("document_codes"),
	DOCUMENT_SECURITY_CODES("document_security_codes"),
	GROUP_TYPE("group_type"),
	;
	
	DataCache(String id) {
		this.id = id;
	}
	
	private String id;
	
	public String getId() {
		return id;
	}
	
	private static Map<String, Object> caches = new HashMap<String, Object>();
	
	public static Object get(DataCache key) {
		return caches.get(key.id);
	}

	public static Object get(String id) {
		return caches.get(id);
	}

	public static void put(String id, Object data) {
		caches.put(id, data);
	}

	public static void put(DataCache key, Object data) {
		caches.put(key.id, data);
	}

}
