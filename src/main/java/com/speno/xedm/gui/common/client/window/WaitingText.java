package com.speno.xedm.gui.common.client.window;

import java.util.Stack;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.ItemFactory;

/**
 * 20130822, junsoo, waiting lock screen
 * @author deluxjun
 *
 */
public class WaitingText {

	private final static Stack<String> stack = new Stack<String>();
	private final static Canvas canvas = createModalMessage();
	private static Label label;

	public static void show(String actionName) {
		if (actionName == null)
			actionName = "";
		stack.push(actionName);
		setMessage();
		canvas.show();
	}

	public static void hide() {
		if (!stack.isEmpty()) {
			stack.pop();
			if (stack.isEmpty())
				canvas.hide();
		}
	}

	public static void hideFinal() {
		stack.removeAllElements();
		canvas.hide();
	}

	private static void setMessage() {
		String msg = "Loading...";
		for (String action : stack) {
			if (!action.equals("")) {
				msg = action;
				break;
			}
		}
		msg = msg + "<br>(" + I18N.message("pleaseWait") + ")";
		label.setContents(msg);
	}


	private static Canvas createModalMessage() {
//		Img loadingIcon = new Img("[SKIN]/" + "wait", 16, 16);
		Img loadingIcon = ItemFactory.newImg("loading32.gif");
		loadingIcon.setShowEdges(false);
//		loadingIcon.setAutoFit(true);
		loadingIcon.setWidth(50);
		loadingIcon.setHeight(50);
		loadingIcon.setImageType(ImageStyle.CENTER);
		loadingIcon.setValign(VerticalAlignment.CENTER);

		label = new Label();
//		label.setWidth(200);
//		label.setHeight100();
		label.setValign(VerticalAlignment.CENTER);
		label.setAlign(Alignment.CENTER);
		label.setAutoHeight();

		HLayout hLayout = new HLayout();
//		hLayout.setLayoutMargin(20);
		hLayout.setMembersMargin(5);

		VLayout vLayout = new VLayout();
		vLayout.setMembers(loadingIcon);
		vLayout.setAutoWidth();
		vLayout.setAlign(VerticalAlignment.CENTER);
		VLayout vLayout2 = new VLayout();
		vLayout2.setMembers(label);
		vLayout2.setAlign(VerticalAlignment.CENTER);
		vLayout2.setHeight100();
//		vLayout2.setAutoHeight();
		hLayout.setMembers(vLayout, vLayout2);

		Window window = new Window();
		window.setShowHeader(false);
		window.setShowHeaderBackground(false);
		window.setShowHeaderIcon(false);
		window.setShowStatusBar(false);
		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setWidth(300);
		window.setHeight(120);
//		window.setHeight(vLayout2.getHeight()+10);
		window.addItem(hLayout);
		window.centerInPage();
		return window;
	}

}