package com.speno.xedm.gui.server;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.core.service.serials.SParameter;
import com.speno.xedm.core.service.serials.SValuePair;
import com.speno.xedm.gui.common.client.services.InfoService;

/**
 * 
 * @author deluxjun
 *
 */
public class MockInfoServiceImpl extends RemoteServiceServlet implements InfoService {

	private static final long serialVersionUID = 1L;

//	@Override
	public SInfo getInfo(String locale) {
		/*
		 * The product version must be taken from context.properties.
		 */
		SInfo info = new SInfo();

		StringTokenizer st = new StringTokenizer("en,kr,ja", ",", false);
		List<SValuePair> locs = new ArrayList<SValuePair>();
		while (st.hasMoreElements()) {
			String token = (String) st.nextElement();
			Locale a = new Locale(token);
			SValuePair l = new SValuePair();
			l.setCode(a.toString());
			l.setValue(a.getDisplayName());
			locs.add(l);
		}
		info.setSupportedGUILanguages(locs.toArray(new SValuePair[0]));

		SValuePair[] languages = new SValuePair[2];
		SValuePair l = new SValuePair();
		Locale loc = new Locale("en");
		l.setCode(loc.toString());
		l.setValue(loc.getDisplayName());
		languages[0] = l;
		l = new SValuePair();
		loc = new Locale("kr");
		l.setCode(loc.toString());
		l.setValue(loc.getDisplayName());
		languages[1] = l;
		info.setSupportedLanguages(languages);

		info.setBundle(getBundle(locale));
		info.setInstallationId("this_is_mock");

		// set config
		ArrayList<SValuePair> values = new ArrayList<SValuePair>();
		values.add(new SValuePair("search.extattr",""));
		values.add(new SValuePair("gui.doubleclick","download"));
		info.setConfig(values.toArray(new SValuePair[0]));
		
		List<SMessage> messages = new ArrayList<SMessage>();

		SMessage message = new SMessage();
		message.setMessage("Test message 1");
		messages.add(message);
		message = new SMessage();
		message.setMessage("Test message 2");
		message.setUrl("http://www.google.com");
		messages.add(message);
//		info.setMessages(messages.toArray(new SMessage[0]));

		return info;
	}

	public SValuePair[] getBundle(String locale) {
		System.out.println("** get bundle " + locale);

		// In production, use our LocaleUtil to instantiate the locale
		Locale l = new Locale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		
//		Vector<SValuePair> vbuf = new Vector<SValuePair>();
//		Enumeration<String> en = rb.getKeys();
//		while (en.hasMoreElements()) {
//			String key = (String) en.nextElement();
//			
//			SValuePair entry = new SValuePair();
//			entry.setCode(key);
//			entry.setValue(rb.getString(key));
//			vbuf.add(entry);
//		}
//		SValuePair[] buf = new SValuePair[vbuf.size()];
//		vbuf.toArray(buf);
//		vbuf.clear();

		SValuePair[] buf = new SValuePair[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			SValuePair entry = new SValuePair();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}

//	@Override
	public SParameter[] getSessionInfo(String sid) {
		return null;
	}
}
