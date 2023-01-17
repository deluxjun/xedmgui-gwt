package com.speno.xedm.gui.common.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.speno.xedm.gui.common.client.log.Log;

/**
 * History °ü¸®
 * @author deluxjun
 *
 */
public class HistoryUtil implements ValueChangeHandler<String>{
	private final static String INIT_TOKEN = "init";
	private static HistoryUtil instance;
	private boolean bNoChange;

	private Map<String, Object[]> histories = new HashMap<String, Object[]>();

	private HistoryUtil() {
		History.addValueChangeHandler(this);
	}

	public static HistoryUtil get() {
		if (instance == null)
			instance = new HistoryUtil();
		return instance;
	}

	public void initHistory(IFHistoryObserver observer){
		bNoChange = true;
		History.newItem(INIT_TOKEN, false);
		bNoChange = false;
		histories.put(INIT_TOKEN, new Object[] {observer, ""});
	}

//	public void initHistory(S){
//		bNoChange = true;
//		History.newItem(INIT_TOKEN, true);
//		bNoChange = false;
//		histories.put(INIT_TOKEN, new Object[] {observer, ""});
//	}

	
	public void newHistory(IFHistoryObserver observer, String refid){
//		String id = GUID.get();
		histories.put(refid, new Object[] {observer, refid});
		bNoChange = true;
		History.newItem(refid, false);
		bNoChange = false;

		observer.onHistoryAdded(refid);
		Log.debug("History putted : " + refid);
	}
	
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if(bNoChange)
			return;
		
		String id = event.getValue();

		Object[] objs = histories.get(id);
		IFHistoryObserver obs = null;
		if (objs == null)
			objs = histories.get(INIT_TOKEN);
		
		if (objs == null)
			return;

		obs = (IFHistoryObserver) objs[0];
		
		String refid = (String) objs[1];

		Log.debug("History changed : " + refid);
		if (obs != null)
			obs.selectByHistory(refid);
	}

}