package com.speno.xedm.gui.frontend.client.document;

import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;


/**
 * Document 관련 옵저버.
 * 
 * @author deluxjun
 *
 */
public interface DocumentObserver {

	/**
	 * Invoked after the document has been saved
	 * 
	 * @param document The updated document
	 */
	public void onDocumentSaved(SDocument document);
	public void onServiceComplite(String message);
	
	// document selected
	public void onDocumentSelected(SRecordItem[] items);

	// document selected
	public void onReloadRequest(SFolder folder);

}
