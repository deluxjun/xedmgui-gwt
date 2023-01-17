package com.speno.xedm.gui.frontend.client.clipboard;

import com.speno.xedm.core.service.serials.SDocument;

public interface ClipboardObserver {

	public void onAdd(SDocument entry);

	public void onRemove(SDocument entry);
}
