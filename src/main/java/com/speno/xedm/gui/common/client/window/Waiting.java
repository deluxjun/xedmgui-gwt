package com.speno.xedm.gui.common.client.window;

import java.util.Stack;

import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.speno.xedm.gui.common.client.util.ItemFactory;

/**
 * 20130822, junsoo, waiting lock screen
 * @author deluxjun
 *
 */
public class Waiting {

	private final static Stack<String> stack = new Stack<String>();
	private final static Canvas canvas = createModalMessage();
	private static Label label;

	public static void show(String actionName) {
		if (actionName == null)
			actionName = "";
		stack.push(actionName);
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


	private static Canvas createModalMessage() {
//		Img loadingIcon = new Img("[SKIN]/" + "wait", 16, 16);
		Img loadingIcon = ItemFactory.newImg("loading32.gif");
		loadingIcon.setShowEdges(false);
		loadingIcon.setEdgeSize(0);
//		loadingIcon.setAutoFit(true);
		loadingIcon.setWidth100();
		loadingIcon.setHeight100();
		loadingIcon.setImageType(ImageStyle.CENTER);
		loadingIcon.setValign(VerticalAlignment.CENTER);

		Window window = new Window();
		window.setShowHeader(false);
		window.setShowEdges(false);
		window.setEdgeSize(0);
		window.setShowHeaderBackground(false);
		window.setShowHeaderIcon(false);
		window.setShowStatusBar(false);
		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setHeight(50);
		window.setWidth(50);
		window.addItem(loadingIcon);
		window.centerInPage();
		return window;
	}
}