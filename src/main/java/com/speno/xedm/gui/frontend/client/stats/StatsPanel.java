package com.speno.xedm.gui.frontend.client.stats;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Statistics Tab�� Content
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class StatsPanel extends HLayout {
	private static StatsPanel instance;

	private VLayout right = new VLayout();
	private Canvas content;

	/**
	 * Statistics Tab�� instance ����.
	 * (instance�� �����ϱ� ���� private�� ������)
	 */
	private StatsPanel() {
		setWidth100();

		StatsMenu leftMenu = StatsMenu.get();
		leftMenu.setWidth(200);
		leftMenu.setShowResizeBar(true);

		addMember(leftMenu);
		addMember(right);
	}

	/**
	 * Statistics Tab�� Content ����
	 * @param content
	 */
	public void setContent(Canvas content) {		
		this.content = content;
		right.setMembers(this.content);
	}

	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static StatsPanel get() {
		if (instance == null)
			instance = new StatsPanel();
		return instance;
	}

	public Canvas getContent() {
		return content;
	}
}