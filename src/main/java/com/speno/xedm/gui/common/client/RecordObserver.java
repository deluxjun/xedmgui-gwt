package com.speno.xedm.gui.common.client;

import java.io.Serializable;

import com.smartgwt.client.data.Record;

/**
 * Record Observer
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public interface RecordObserver {
	//public void onRecordSelected(final long id);
	public void onRecordSelected(final Serializable id, final Serializable parentId);
	public void onRecordSelected(final Record record);
	public void onRecordClick(Record record);
	public void onRecordDoubleClick(Record record);	
	public boolean isExistMember();
	public boolean isIDLong();
}
