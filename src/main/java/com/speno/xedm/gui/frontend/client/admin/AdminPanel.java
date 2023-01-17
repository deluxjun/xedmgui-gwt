package com.speno.xedm.gui.frontend.client.admin;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Admin Tab�� Content
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class AdminPanel extends HLayout {
	private static AdminPanel instance;

	private VLayout right = new VLayout();
	private Canvas content;
	
	private AdminMenu adminMenu;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
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
	 * Admin Tab�� instance ����.
	 * (instance�� �����ϱ� ���� private�� ������)
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
	 * Admin Tab�� Content ����
	 * @param content
	 */
	public void setContent(Canvas content) {
		/* Left Tree �޴��� ���� ������� �����ϱ� ���� �ּ� ó����.
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
	 * Content�� �������� �ʾҴ��� ���� ��ȯ
	 * @return
	 */
	public boolean isNotExistContent() {
		return (content == null);
	}
}