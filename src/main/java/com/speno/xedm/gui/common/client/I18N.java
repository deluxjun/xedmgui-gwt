package com.speno.xedm.gui.common.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.core.service.serials.SValuePair;

/**
 * internalization class
 * 
 * @author deluxjun
 *
 */
public class I18N {
	private static String locale = "en";

	private static SValuePair[] languages;

	private static SValuePair[] guiLanguages;

	private static HashMap<String, String> bundle = new HashMap<String, String>();
	
	public final static String LOCALE_EN = "en";
	public final static String LOCALE_KO= "ko";

	public static String message(String key) {
		if (bundle.containsKey(key))
			return bundle.get(key);
		else
			return key;
	}

	public static String message(String key, String val) {
		String tmp = message(key);
		try {
			tmp = tmp.replaceAll("\\{0\\}", val);
		} catch (Throwable t) {
		}
		return tmp;
	}

	public static String message(String key, String[] vals) {
		String tmp = message(key);
		try {
			for (int i = 0; i < vals.length; i++) {
				tmp = tmp.replaceAll("\\{" + i + "\\}", vals[i]);
			}
		} catch (Throwable t) {
		}
		return tmp;

	}

	public static String getLocale() {
		return locale;
	}

	/**
	 * Computes the default suitable language for documents
	 */
	public static String getDefaultLocaleForDoc() {
		// Search for exact match
		for (SValuePair l : languages) {
			if (l.getCode().equals(locale))
				return l.getCode();
		}

		// Check the first 2 letters(the language)
		for (SValuePair l : languages) {
			if (l.getCode().startsWith(locale.substring(0, 2)))
				return l.getCode();
		}

		return languages[0].getCode();
	}

	public static char groupingSepator() {
		String gs = message("grouping_separator");
		return gs.charAt(gs.length() - 1);
	}

	public static char decimalSepator() {
		String gs = message("decimal_separator");
		return gs.charAt(gs.length() - 1);
	}

	public static void setLocale(String locale) {
		I18N.locale = locale;
	}

	public static LinkedHashMap<String, String> getSupportedLanguages(boolean addEmpty) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (addEmpty)
			map.put("", " ");
		if (languages != null)
			for (SValuePair l : languages) {
				map.put(l.getCode(), l.getValue());
			}
		return map;
	}

	public static LinkedHashMap<String, String> getSupportedGuiLanguages(boolean addEmpty) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (addEmpty)
			map.put("", " ");
		if (guiLanguages != null)
			for (SValuePair l : guiLanguages) {
				map.put(l.getCode(), l.getValue());
			}
		return map;
	}

	public SValuePair[] getLanguages() {
		return languages;
	}

	public static void setLanguages(SValuePair[] languages) {
		I18N.languages = languages;
	}

	public static void initBundle(SValuePair[] messages) {
		bundle.clear();
		for (SValuePair val : messages) {
			bundle.put(val.getCode(), val.getValue());
		}
	}

	public static void init(SInfo info) {
		setLanguages(info.getSupportedLanguages());
		setGuiLanguages(info.getSupportedGUILanguages());
		initBundle(info.getBundle());
	}

	public static void init(SSession session) {
		init(session.getInfo());
		I18N.locale = session.getUser().getLanguage();
	}

	public static SValuePair[] getGuiLanguages() {
		return guiLanguages;
	}

	public static void setGuiLanguages(SValuePair[] guiLanguages) {
		I18N.guiLanguages = guiLanguages;
	}

	public static String messageForFolderPath(String pathExtended) {
		String shared = "Shared";
		String mydoc = "Mydoc";
		
		if(pathExtended.contains(shared)){
			return pathExtended.replace(shared, message("shareddoc"));
		}
		else if(pathExtended.contains(mydoc)){
			return pathExtended.replace(mydoc, message("mydoc"));
		}
		return message(pathExtended);
	}
}