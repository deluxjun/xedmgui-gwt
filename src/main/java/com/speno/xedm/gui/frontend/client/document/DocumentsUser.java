package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;

public class DocumentsUser extends Window {
	public DocumentsUser() {
		setWidth(600);   
        setHeight(300);   
        setTitle(I18N.message("title"));   
        setShowMinimizeButton(false);   
        setIsModal(true);   
        setShowModalMask(true);   
        centerInPage();
        addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();   
			}
		});
        
        DynamicForm form = new DynamicForm();   
        form.setHeight100();   form.setWidth100();   
        form.setPadding(5);    form.setMargin(20);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);   
        form.setNumCols(4);
        form.setColWidths("100", "200", "10", "*");
        addItem(form);

        // 세션에서 유저정보 받아오기
        SUser suser = Session.get().getUser();
        
        // 사용자명
        TextItem txtName = ItemFactory.newTextItem("name", I18N.message("name"), "");
        txtName.setWidth(100);
        txtName.setValue(suser.getName());
        txtName.setCanEdit(false);
        
        // 비밀번호
        TextItem txtPw = ItemFactory.newTextItem("pw", I18N.message("password"), "");
        txtPw.setTextBoxStyle("white");
        txtPw.setHeight(14); 						txtPw.setWidth(95);
        txtPw.setStartRow(true);
        
        // 비밀번호 확인
        TextItem txtVerifyPw = ItemFactory.newTextItem("verifypw", I18N.message("verifypassword"), "");
        txtVerifyPw.setTextBoxStyle("white");
        txtVerifyPw.setHeight(14); 					txtVerifyPw.setWidth(95);
        txtVerifyPw.setStartRow(false); 			txtVerifyPw.setEndRow(false);
        
        // 이메일
        TextItem txtEmail = ItemFactory.newTextItem("email", I18N.message("email"), "");
        txtEmail.setTextBoxStyle("white");
        txtEmail.setHeight(14);				        txtEmail.setWidth(200);
        txtEmail.setStartRow(true);
        txtEmail.setValue(suser.getEmail());
        
        // 전화번호
        TextItem txtPhonel = ItemFactory.newTextItem("phone", I18N.message("phone"), "");
        txtPhonel.setTextBoxStyle("white");
        txtPhonel.setHeight(14);			        txtPhonel.setWidth(95);
        txtPhonel.setStartRow(true);
        txtPhonel.setValue(suser.getPhone());
        
        // 다이나믹 폼에 텍스트아이템 넣기
        // 아이템 정렬을 위해 빈 텍스트 아이템을 사용
        form.setFields(txtName,new Dummy(), new Dummy(), new Dummy(),
        		txtPw, txtVerifyPw, new Dummy(), 
        		txtEmail, new Dummy(), 
        		txtPhonel );   
        
        addItem(form);   
		
        DynamicForm dfButton = new DynamicForm();   
        dfButton.setHeight(30);   			        				dfButton.setWidth100();   
        dfButton.setPadding(5);				    	    			dfButton.setMargin(5);
        dfButton.setShowEdges(false);		        			dfButton.setAlign(Alignment.RIGHT);
        dfButton.setLayoutAlign(VerticalAlignment.TOP);
        dfButton.setNumCols(3);						        	dfButton.setColWidths("*", "10", "10");
        
        ButtonItem btnOk = new ButtonItem();
        btnOk.setTitle(I18N.message("confirm"));
        btnOk.setWidth(100);
        btnOk.setStartRow(false);         
        btnOk.setEndRow(false);
        btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("Sorry not ready yet!");
				return;
				
			}
		});
        
        ButtonItem btnCancel = new ButtonItem();
        btnCancel.setTitle(I18N.message("cancel"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false); 			
        btnCancel.setEndRow(false);
        btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();   				
			}
		});
        
        dfButton.setItems(new Dummy(), btnOk, btnCancel);
        addItem(dfButton);
	}
	
	class Dummy extends StaticTextItem{
		public Dummy(){
			setName("dummy");
			setShowTitle(false);
	        setStartRow(true);
		}
	}
}