package com.speno.xedm.gui.frontend.client.admin.organization;

import com.smartgwt.client.types.State;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;

/**
 * 색인버튼
 * @author 박상기
 *
 */
public class IndexBtn extends Button {
	
	private boolean iSelected = false;
	private String iValue;
	
	public IndexBtn(String title, String value) {
		super(title);		
		this.iValue = value;		
		setWidth("28px");
		
		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(iSelected) {
					IndexBtn.this.setState(State.STATE_DOWN);
				}
				else {
					IndexBtn.this.setState(State.STATE_UP);
				}
				event.cancel();
			}
		});
	}
	
	public static IndexBtn[] getBtns() {		
		IndexBtn[] btns = new IndexBtn[16];
		btns[0] = new IndexBtn("All", "");
		btns[1] = new IndexBtn("\u3131", "\u3131");
		btns[2] = new IndexBtn("\u3134", "\u3134");
		btns[3] = new IndexBtn("\u3137", "\u3137");
		btns[4] = new IndexBtn("\u3139", "\u3139");
		btns[5] = new IndexBtn("\u3141", "\u3141");
		btns[6] = new IndexBtn("\u3142", "\u3142");
		btns[7] = new IndexBtn("\u3145", "\u3145");
		btns[8] = new IndexBtn("\u3147", "\u3147");
		btns[9] = new IndexBtn("\u3148", "\u3148");
		btns[10] = new IndexBtn("\u314A", "\u314A");
		btns[11] = new IndexBtn("\u314B", "\u314B");
		btns[12] = new IndexBtn("\u314C", "\u314C");
		btns[13] = new IndexBtn("\u314D", "\u314D");
		btns[14] = new IndexBtn("\u314E", "\u314E");
		btns[15] = new IndexBtn("A~Z", "0");
		
		btns[0].setState(State.STATE_DOWN);
		return btns;
	}
	
	public void setISelected(boolean iSelected) {
		this.iSelected = iSelected;
		
		if(iSelected) { 
			setState(State.STATE_DOWN);
		}
		else {
			setState(State.STATE_UP);
		}
	}
	
	public boolean IsISelected() {
		return iSelected;
	}
	
	public String getIValue() {
		return iValue;
	}
	
	public void setAddClickHandler(ClickHandler clickHandler) {
		addClickHandler(clickHandler);
	}
}
