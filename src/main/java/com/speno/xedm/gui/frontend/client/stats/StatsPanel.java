package com.speno.xedm.gui.frontend.client.stats;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Statistics Tab의 Content
 * 
 * @author 박상기
 * @since 1.0
 */
public class StatsPanel extends HLayout {
	private static StatsPanel instance;

	private VLayout right = new VLayout();
	private Canvas content;

	/**
	 * Statistics Tab의 instance 생성.
	 * (instance를 재사용하기 위해 private로 선언함)
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
	 * Statistics Tab의 Content 설정
	 * @param content
	 */
	public void setContent(Canvas content) {		
		this.content = content;
		right.setMembers(this.content);
	}

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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