package com.speno.xedm.gui.frontend.client.admin;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;

/**
 * ����� Navigator bar
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class TrackPanel extends VLayout {	
	private RefreshObserver observer;
	private Label naviLabel;
	private Button refreshBtn;
	
	// ���� ���� ���ǥ�� �ؿ� �߰�
	private StaticTextItem description;
	private DynamicForm form = new DynamicForm();
	private ValuesManager vm = new ValuesManager();
	
	/**
	 * Navigator bar ����
	 * @param tr : track ���ڿ�
	 * @param ob : RefreshObserver
	 */
	public TrackPanel(String tr, RefreshObserver ob) {            
		this.observer = ob;
		setTrackBody(tr, null);	
	}
	
	/**
	 * Navigator bar ����
	 * @param tr : track ���ڿ�
	 * @param icon : track ���ڿ��� icon
	 * @param ob : RefreshObserver
	 */
	public TrackPanel(String tr, String icon, RefreshObserver ob) {            
		this.observer = ob;
		setTrackBody(tr, icon);	
	}
	
	private void setTrackBody(String tr, String icon) {
		setAutoHeight();
		naviLabel = new Label();   
		naviLabel.setHeight(15);   
		naviLabel.setPadding(5);
		naviLabel.setWrap(false);
		naviLabel.setAlign(Alignment.LEFT);   
		naviLabel.setValign(VerticalAlignment.CENTER);
		naviLabel.setShowEdges(false);   
		naviLabel.setStyleName("subTitle");
		naviLabel.setContents(tr);
		naviLabel.setIconSize(32);
		if(icon != null) {
			naviLabel.setIcon(icon);
		}
		
		//navigator + reflash ��ư
		HLayout topHL = new HLayout();
		topHL.setWidth100();
		topHL.addMember(naviLabel);
		
		if(observer != null) {
			refreshBtn = new Button(I18N.message("refresh"));
			refreshBtn.setWidth(80);
			refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
			refreshBtn.addClickHandler(new ClickHandler() {
	            public void onClick(ClickEvent event) {
	            	observer.onRefreshBtnClicked(event);
	            }
	        });
			
			//refrash ��ư�� �������� ���������� HL
			HLayout naviHL = new HLayout();		
			naviHL.setWidth100();
			naviHL.setAlign(Alignment.RIGHT);
			naviHL.addMember(refreshBtn);
			topHL.addMember(naviHL);
		}	
		
		
		
		//�ӽ�
		Label line = new Label();   
        line.setHeight(1);   
        line.setWidth100();
        line.setContents("___________________________________________________________________________");
        
        addMember(topHL, 0);
	}
	
	/**
	 * track ���ڿ� ����
	 * @param tr
	 */
	public void setTrack(String tr) {
		naviLabel.setContents(tr);
	}
	
	/**
	 * icon ����
	 * @param icon
	 */
	public void setIcon(String icon) {
		naviLabel.setIcon(icon);		
	}
	
	/**
	 * 
	 */
	public void setDescription(String str) {
		vm = new ValuesManager();
		if (form != null)
			form.destroy();

		if (contains(form))
			removeChild(form);
		form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setTitleWidth(30);
		
//		description = ItemFactory.newStaticTextItem("", "", str);
		description = new StaticTextItem();
		description.setShowTitle(false);
		description.setValue(str);
		description.setWidth(300);
		description.setCanEdit(false);
		
		form.setItems(description);
		
		if(str == null) {
			removeMember(form);
		}
		else{
			addMember(form);
		}
	}
}