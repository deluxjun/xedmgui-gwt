package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.WindowUtils;





/**
 * System Menu
 * 
 * @author
 * @since 1.0
 */
public class ButtonLinkPanel extends VLayout{	
	private Button histroy;
	private Button outlist;
	private Button recovery;
	private Button doctcd;
	
	protected List<String> menuList = new ArrayList<String>();				// menu list
	protected Map<String, Object> subMenus = new HashMap<String, Object>();	// submenu name, button

	//프로퍼티 땡겨와 보자
	
	

	public ButtonLinkPanel(){
		setMargin(10);
		setMembersMargin(5);
//		System.out.println("history = " + PathProperties.message("history"));
		histroy = new Button(I18N.message("ecm.history"));
		histroy.setWidth100();
		histroy.setHeight(25);
//		general.hide();
		addMember(histroy);

		outlist = new Button(I18N.message("ecm.outlist"));
		outlist.setWidth100();
		outlist.setHeight(25);
//		general.hide();
		addMember(outlist);
		
		recovery = new Button(I18N.message("ecm.recovery"));
		recovery.setWidth100();
		recovery.setHeight(25);
//		general.hide();
		addMember(recovery);
		
		doctcd = new Button(I18N.message("ecm.doctcd"));
		doctcd.setWidth100();
		doctcd.setHeight(25);
//		general.hide();
		addMember(doctcd);

		
		initMenus(	new Object[]{"ecm.history" 	, histroy},
					new Object[]{"ecm.outlist" 	, outlist},
					new Object[]{"ecm.recovery"	, recovery},
					new Object[]{"ecm.doctcd"	, doctcd}
		);

//
//		http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=histroy
//
//		http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=outlist
//
//		http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=recovery
//
//		http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=doctcd

//		1.     이력조회 화면 : /agent/dmsMain.jsp?TR_ID=history
//
//		2.     출력조회 화면 : /agent/dmsMain.jsp?TR_ID=outlist
//
//		3.     복원신청 화면 : /agent/dmsMain.jsp?TR_ID=recovery
//
//		4.     문서보존년한 조회 : /agent/dmsMain.jsp?TR_ID=doctcd


	}


	
	
	// menu 초기화
    public void initMenus(Object[]... members) {
    	
    	// clear all
		Canvas[] membersToRemove = getMembers();
		for(Canvas member : membersToRemove) {
			removeMember(member);
		}
		menuList.clear();
		subMenus.clear();
		
		// reset
		for (final Object[] member : members) {
			Button btn = (Button)member[1];
			addMember(btn);
			
			menuList.add((String)member[0]);
			subMenus.put((String)member[0], btn);
			
			btn.addClickHandler(new ClickHandler() {
				private String title = (String)member[0];
				@Override
				public void onClick(ClickEvent event) {
//					System.out.println(title);
//					http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=histroy
					//
//					http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=outlist
					//
//					http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=recovery
					//
//					http://edmdev.allianzlife.co.kr/agent/dmsMain.jsp?TR_ID=doctcd
					if(title.equals("ecm.history"))
						WindowUtils.openPopupUrl(Session.get().getInfo().getConfig("setting.ecm.history.url"), "_blank", "width=1250, height=800, resizable=yes, copyhistory=no");
					else if(title.equals("ecm.outlist"))
						WindowUtils.openPopupUrl(Session.get().getInfo().getConfig("setting.ecm.outlist.url"), "_blank", "width=1250, height=800, resizable=yes, copyhistory=no");
					else if(title.equals("ecm.recovery"))
						WindowUtils.openPopupUrl(Session.get().getInfo().getConfig("setting.ecm.recovery.url"), "_blank", "width=1250, height=800, resizable=yes, copyhistory=no");	
					else if(title.equals("ecm.doctcd"))
						WindowUtils.openPopupUrl(Session.get().getInfo().getConfig("setting.ecm.doctcd.url"), "_blank", " width=1250, height=800, resizable=yes, copyhistory=no");
				}
			});
		}
		
    }




}