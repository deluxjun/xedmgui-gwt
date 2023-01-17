package com.speno.xedm.guitest.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.gui.common.client.util.BlinkColor;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryControl implements EntryPoint {

	public void onModuleLoad() {
		final TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setWidth(400);  
        topTabSet.setHeight(200); 
//        topTabSet.setStyleName("tabButtonTop");
  
       
        Tab tTab1 = new Tab("Blue");  
        Img tImg1 = new Img("skin/images/delete.png", 48, 48);  
        tTab1.setPane(tImg1);
//        tTab1.setTitleStyle("tabButtonTop");
  
        Tab tTab2 = new Tab("Green");  
        Img tImg2 = new Img("skin/images/delete.png", 48, 48);  
        tTab2.setPane(tImg2);
        
//        topTabSet.setPaneContainerClassName("tabButtonTop");
  
//        topTabSet.setUseSimpleTabs(true);
//        topTabSet.setSimpleTabBaseStyle("tabButton");
        topTabSet.addTab(tTab1);  
        topTabSet.addTab(tTab2);  
        
        final HTMLFlow flow = new HTMLFlow();             
        flow.setID("messageBox");  
        flow.setContents("<font color=red><img src='skin/images/delete.png'> <span class='exampleDropTitle'>Ajax  </span> <b>A</b>synchronous <b>J</b>avaScript <b>A</b>nd <b>X</b>ML (AJAX) is a Web development technique for creating interactive <b>web applications</b>. The intent is to make web pages feel more responsive by exchanging small amounts of data with the server behind the scenes, so that the entire Web page does not have to be reloaded each time the user makes a change. This is meant to increase the Web page's <b>interactivity</b>, <b>speed</b>, and <b>usability</b>. (Source: <a href='http://www.wikipedia.org' title='Wikipedia' target='_blank'>Wikipedia</a>)</font>");  
        flow.setStyleName("statusMessageBox");  
        flow.setOverflow(Overflow.HIDDEN);  
        flow.setShowEdges(true);  
        flow.setPadding(5);  
        flow.setWidth(75);  
        flow.setHeight(45);  
        flow.setTop(50);  
        flow.setAnimateTime(800); //in milliseconds  
        
        final IButton expandButton = new IButton();  
        expandButton.setTitle("Expand");  
        expandButton.setLeft(40);  
          
        expandButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	BlinkColor<HTMLFlow> blink = new BlinkColor<HTMLFlow>(flow) {
					@Override
					protected void job(String value) {
						element.setBackgroundColor(value);
					}
					@Override
					protected void lastJob(String value) {
						element.setBackgroundColor(null);
					}
            	};
            	blink.animate(500, "ff0000", "000000");
            }  
        });  
  
  
        HLayout hLayout = new HLayout();  
        hLayout.setBackgroundColor("gray");
        hLayout.setMembersMargin(10);  
        hLayout.addMember(expandButton);  
        hLayout.addMember(flow);
        
        RootLayoutPanel.get().add(hLayout);
        
	}
	

}
