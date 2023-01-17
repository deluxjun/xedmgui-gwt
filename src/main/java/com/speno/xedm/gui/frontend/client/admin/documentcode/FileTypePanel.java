package com.speno.xedm.gui.frontend.client.admin.documentcode;
   
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * FileType Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class FileTypePanel extends VLayout implements RefreshObserver{	
	private static FileTypePanel instance;	
	
	private HLayout mainHL;
	private FileTypeGridPanel fileTypeGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static FileTypePanel get() {
		if (instance == null) {
			instance = new FileTypePanel();
		}
		return instance;
	}
	
	public FileTypePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("filetype"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		fileTypeGridPanel = createFileTypeVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(fileTypeGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	private FileTypeGridPanel createFileTypeVL(boolean isRefresh) {		
		return isRefresh ? 
				new FileTypeGridPanel("admin.doccode.filetype", I18N.message("filetype")) :
					FileTypeGridPanel.get("admin.doccode.filetype", I18N.message("filetype"));
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}