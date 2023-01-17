package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Repositories Panel
 * @author 박상기
 * @since 1.0
 */
public class RepositoriesPanel extends VLayout implements IFHistoryObserver{
	private static RepositoriesPanel instance = null;
	public static final String LASTUPDATE = "lastUpdated";
		
	private HLayout refreshHL;
	private Button refreshBtn;
	private Label lastUpdateLabel; 
	private RepositoriesPiePanel charts;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static RepositoriesPanel get() {
		if (instance == null) {
			instance = new RepositoriesPanel();
		}
		return instance;
	}

	/**
	 * Repositories Panel 생성
	 */
	public RepositoriesPanel() {
		setWidth100();
		setMembersMargin(10);
		
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("statistics")+" > "+ I18N.message("statistics")+" > "+ I18N.message("repositiories"), null));
		
		refreshBtn = new Button(I18N.message("refresh"));
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	createBody(true);
            }
        });
		
		refreshHL = new HLayout(10);
		refreshHL.setWidth100();
		refreshHL.setAutoHeight();
		refreshHL.setAlign(Alignment.RIGHT);
		refreshHL.addMembers(refreshBtn);		
		addMember(refreshHL);

		createBody(false);
	}
	
	/**
	 * Main패널 생성
	 */
	private void createBody(boolean isRefresh) {		
		if(isRefresh) {
			refreshHL.removeMember(lastUpdateLabel);
			removeMember(charts);
		}
		
		ServiceUtil.system().listRepositorySize(Session.get().getSid(), new AsyncCallback<List<Map<String,Object>>>() {
  			@Override
  			public void onFailure(Throwable caught) {
  				SCM.warn(caught);
  				
  			}
  			@Override
  			public void onSuccess(List<Map<String,Object>> result) {
  				if(result != null && result.size() > 0) {
  					HashMap<String,Object> map = (HashMap<String,Object>)result.get(0);
  					
  					lastUpdateLabel = new Label("<b>" + I18N.message("lastupdate") + ": " + map.get(LASTUPDATE) + "</b>");
  					lastUpdateLabel.setHeight(refreshBtn.getHeight());
  					lastUpdateLabel.setWidth100();
  					lastUpdateLabel.setAlign(Alignment.RIGHT);
  					result.remove(0);
  					
  					refreshHL.addMember(lastUpdateLabel, 0);

  					// 외부망에 접속이 가능할 경우 차트가 그려진다.
  					if(Session.get().getInfo().getConfig("settings.connect.exNetwork").toString().equals("true")){
  						charts = new RepositoriesPiePanel(result);
  						addMember(charts);
  					}
  					// 외부망에 접속이 불가능할 경우 차트를 그리드로 대체한다.
  					else{
  						RepositoriesGridPanel gridPanel = new RepositoriesGridPanel(result);
  						addMember(gridPanel);
  					}
  				}				
  			}
  		});
	}

	@Override
	public void selectByHistory(String refid) {
		String[] tags = refid.split(";");
		if (tags != null && tags.length > 0) {
			if ("dashboard".equals(tags[0]) && tags.length > 1) {
//				selectMenu(tags[1], "", true);
			}
		}
		Session.get().setCurrentMenuId(refid);
	}

	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
}
