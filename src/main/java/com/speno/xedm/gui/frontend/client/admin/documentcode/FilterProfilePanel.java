package com.speno.xedm.gui.frontend.client.admin.documentcode;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * FilterProfile Panel
 * 
 * @author ������
 * @since 1.0
 */
public class FilterProfilePanel extends VLayout implements RefreshObserver{	
	private static FilterProfilePanel instance;	
	
	private HLayout mainHL;
	private FilterProfileGridPanel filterProfileGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static FilterProfilePanel get() {
		if (instance == null) {
			instance = new FilterProfilePanel();
		}
		return instance;
	}
	
	public FilterProfilePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("second.filterprofile"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		filterProfileGridPanel = createFilterProfileGridVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(filterProfileGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * FilterProfilePanel ����
	 */
	private FilterProfileGridPanel createFilterProfileGridVL(boolean isRefresh) {		
		return isRefresh ? 
				new FilterProfileGridPanel("admin.system.filterprofile", I18N.message("second.filterprofile"), null, true) :
					FilterProfileGridPanel.get("admin.system.filterprofile", I18N.message("second.filterprofile"), null, true);
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}