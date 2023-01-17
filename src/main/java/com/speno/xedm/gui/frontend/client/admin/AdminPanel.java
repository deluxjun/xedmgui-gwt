package com.speno.xedm.gui.frontend.client.admin;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Admin Tab의 Content
 * 
 * @author 박상기
 * @since 1.0
 */
public class AdminPanel extends HLayout {
	private static AdminPanel instance;

	private VLayout right = new VLayout();
	private Canvas content;
	
	private AdminMenu adminMenu;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static AdminPanel get() {
		if (instance == null)
			instance = new AdminPanel();
		return instance;
	}
	
	public static AdminPanel getNew() {
		instance = null;
		instance = new AdminPanel();
		return instance;
	}


	/**
	 * Admin Tab의 instance 생성.
	 * (instance를 재사용하기 위해 private로 선언함)
	 */
	public AdminPanel() {
		setWidth100();

		adminMenu = AdminMenu.get();
		adminMenu.setWidth(200);
		adminMenu.setShowResizeBar(true);

		addMember(adminMenu);
		addMember(right);
	}

	public AdminMenu getAdminMenu() {
		return adminMenu;
	}

	/**
	 * Admin Tab의 Content 설정
	 * @param content
	 */
	public void setContent(Canvas content) {
		/* Left Tree 메뉴에 의한 재생성을 방지하기 위해 주석 처리함.
		 * 2013-03-05
		if (this.content != null) {
			if (right.contains(this.content))
				right.removeChild(this.content);
			this.content.destroy();
		}
		this.content = content;
		right.addMember(this.content);
		*/
		
		if (this.content != null && right.hasMember(this.content))
			right.removeMember(this.content);
		this.content = content;
		right.addMember(this.content);
	}
	
	public Canvas getContent() {
		return content;
	}

	/**
	 * Content가 설정되지 않았는지 여부 반환
	 * @return
	 */
	public boolean isNotExistContent() {
		return (content == null);
	}
}