package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.Util;

public class BatchdownloderPanel extends VLayout {
	private SFolder folder;
	private ListGrid grid;

	
	public BatchdownloderPanel(final SFolder folder,
			final com.smartgwt.client.widgets.Canvas parent) {
		this.folder = folder;
		Long folder_id = folder.getId();
		final String folder_id_string = folder_id.toString();
		VLayout top_grid = new VLayout();
		top_grid.setHeight("50%");

		// grid = new ListGrid();
		// init_grid(grid);

		HLayout mid_tool = new HLayout();
		mid_tool.setHeight100();
		mid_tool.setWidth100();

		Button batch_down = new Button();
		batch_down.setTitle(I18N.message("batch"));
		batch_down.setMargin(10);
		batch_down.setWidth(135);
		batch_down.setHeight(80);
		batch_down.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

				SC.ask(I18N.message("batch"),I18N.message("batch.accept"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						// TODO Auto-generated method stub
						if (value == true) {
							Util.batchdownload(folder_id_string);							
							parent.destroy();
						}						
					}// 여길 고쳐야됨

				});
			}
		});

		Button cancle = new Button();
		cancle.setTitle(I18N.message("cancel"));
		cancle.setMargin(10);
		cancle.setWidth(135);
		cancle.setHeight(80);
		cancle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub				
				parent.destroy();
			}
		});

		mid_tool.addMember(batch_down);
		mid_tool.addMember(cancle);

		// this.addMember(top_grid);
		this.addMember(mid_tool);
	}

	public void init_grid(ListGrid grid) {
		// 그리드 에 뿌리는 방식

	}
}
