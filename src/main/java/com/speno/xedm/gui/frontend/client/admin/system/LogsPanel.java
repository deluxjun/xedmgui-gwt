package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;

/**
 * Logs Panel
 * @author 박상기
 * @since 1.0
 */
public class LogsPanel extends VLayout {	
	private static LogsPanel instance = null;
	
	private Label lastUpdateLabel = new Label();
	private HTMLPane htmlPane = new HTMLPane();
	private SelectItem appenderItem = new SelectItem("appender", I18N.message("selectLog"));
	private Button refreshBtn = new Button(I18N.message("refresh"));
	private LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>() ;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static LogsPanel get() {
		if (instance == null) {
			instance = new LogsPanel();
		}
		return instance;
		//return new LogsPanel();
	}
	
	public LogsPanel() {
		setHeight100();
		setMembersMargin(10);
		
		executeGetOptionsAndSet();
		
		appenderItem.setWrapTitle(false);
		appenderItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				refresh();
			}
		});
		
		DynamicForm searchForm = new DynamicForm();
		searchForm.setAutoWidth();
		searchForm.setAlign(Alignment.LEFT);
		searchForm.setItems(appenderItem);
		
		lastUpdateLabel.setHeight(refreshBtn.getHeight());
		lastUpdateLabel.setWidth100();
		lastUpdateLabel.setAlign(Alignment.RIGHT);
		lastUpdateLabel.setContents("<b>" + I18N.message("lastupdate") + ": " + new Date() + "</b>");
		
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	refresh();
            }
        });
		
		htmlPane.setWidth100();
		htmlPane.setHeight100();
		htmlPane.setBorder("1px solid #E1E1E1");
		htmlPane.setContentsType(ContentsType.PAGE);
		
		HLayout refreshHL = new HLayout(10);
		refreshHL.setWidth100();
		refreshHL.setAutoHeight();
		refreshHL.setAlign(Alignment.RIGHT);
		refreshHL.addMembers(searchForm, lastUpdateLabel, refreshBtn);		
		addMember(refreshHL);
		addMember(htmlPane);
	}
	
	private void refresh() {
		htmlPane.setContentsURL(GWT.getHostPageBaseURL() + "log?sid=" + Session.get().getSid() + "&"+appenderItem.getValueAsString());
		htmlPane.redraw();
		htmlPane.setWidth100();
		htmlPane.setHeight100();		
		lastUpdateLabel.setContents("<b>" + I18N.message("lastupdate") + ": " + new Date() + "</b>");
	}
	
	private void executeGetOptionsAndSet() {
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		documentCodeService.listCodes(Session.get().getSid(), "LOG_APPENDER", new AsyncCallbackWithStatus<List<SCode>>() {
			@Override
			public String getSuccessMessage() {
				return "";
			}
			@Override
			public String getProcessMessage() {
				return "";
			}
			@Override
			public void onSuccessEvent(List<SCode> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						opts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}			
				
				appenderItem.setValueMap(opts);
				
				appenderItem.setDefaultToFirstOption(true); //??
				if( result.size() > 0) {
					appenderItem.setDefaultValues(result.get(0).getValue());
					refresh();
				}
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
}
