package com.speno.xedm.gui.frontend.client.admin.documentcode;
   
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * RetentionProfile Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class RetentionProfilePanel extends VLayout implements RefreshObserver{	
	private static RetentionProfilePanel instance;	
	
	private HLayout mainHL;
	private RetentionProfileGridPanel retentionProfileGridPanel;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static RetentionProfilePanel get() {
		if (instance == null) {
			instance = new RetentionProfilePanel();
		}
		return instance;
	}
	
	public RetentionProfilePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("retentionprofile"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		retentionProfileGridPanel = createFileTypeVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(retentionProfileGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	private RetentionProfileGridPanel createFileTypeVL(boolean isRefresh) {		
		return isRefresh ? 
				new RetentionProfileGridPanel("admin.doccode.retentionprofile", I18N.message("retentionprofile")) :
					RetentionProfileGridPanel.get("admin.doccode.retentionprofile", I18N.message("retentionprofile"));
	}
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}