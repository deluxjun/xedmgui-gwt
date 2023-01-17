package com.speno.xedm.gui.frontend.client.admin.system.audit;
   
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;

/**
 * Audit Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class AuditPanel extends VLayout {
	private static AuditPanel instance = null;	
	
	private HLayout refreshHL;
	private Button refreshBtn;
	private Label lastUpdateLabel; 
	private HLayout mainHL;
	private AuditGridPanel auditGridPanel;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static AuditPanel get() {
		if (instance == null) {
			instance = new AuditPanel();
		}
		return instance;
	}
	
	public AuditPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);		
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		auditGridPanel = createAuditVL(isRefresh);
				
		refreshBtn = new Button(I18N.message("refresh"));
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
//            	createAuditVL(true);
            	auditGridPanel.executeFetch();
            }
        });
		refreshBtn.setVisible(false);
		
		lastUpdateLabel = new Label();
		lastUpdateLabel.setHeight(refreshBtn.getHeight());
		lastUpdateLabel.setWidth100();
		lastUpdateLabel.setAlign(Alignment.RIGHT);
		
		refreshHL = new HLayout(10);
		refreshHL.setWidth100();
		refreshHL.setAutoHeight();
		refreshHL.setAlign(Alignment.RIGHT);
		refreshHL.addMembers( lastUpdateLabel, refreshBtn);		
		addMember(refreshHL);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(auditGridPanel);
		addMember(mainHL);
	}
	
	private AuditGridPanel createAuditVL(boolean isRefresh) {		
		return isRefresh ? 
				new AuditGridPanel("admin.system.audit", null) :
					AuditGridPanel.get("admin.system.audit", null);
	}
}