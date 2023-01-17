package com.speno.xedm.gui.common.client;

/**
 * Monitoring Observer
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public interface MonitoringObserver {
	
	public static final String TERM_TYPE_S = "Second"; 
	public static final String TERM_TYPE_M = "Minute";
	public static final String TERM_TYPE_T = "Time";
	public static final String TERM_TYPE_D = "Day";
	
	public void onCapUpperStack(String termType, String gXname);
}
