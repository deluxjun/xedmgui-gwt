package com.speno.xedm.gui.frontend.client.folder;

import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.core.service.serials.SFolder;

public abstract class FolderDetailTab extends HLayout {
	protected SFolder folder;

	/**
	 * 
	 * @param document The document this instance refers to
	 * @param changedHandler The handler to be invoked in case of changes in the
	 *        folder
	 */
	public FolderDetailTab(SFolder folder) {
		super();
		this.folder = folder;
	}

	public SFolder getFolder() {
		return folder;
	}

}
