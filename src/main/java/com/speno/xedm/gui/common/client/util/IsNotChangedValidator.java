package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.speno.xedm.core.service.serials.SRight;

// kimsoeun GS인증용 - 변경사항 여부 확인
public class IsNotChangedValidator {
	Map oldValue = new HashMap();
	List<Map> oldValueList = new ArrayList<Map>();
	List<SRight> oldSRights = new ArrayList<SRight>();
	long profileId;
	boolean flag;
	
	public boolean check(List<SRight> list, long profileId, boolean isRetention){
		if(list.size() != oldSRights.size() || profileId != this.profileId || isRetention != flag)  return false;
		
		for (SRight sRight : list) {
			for (SRight oldsRight : oldSRights) {
				if(sRight.getEntityId().equals(oldsRight.getEntityId())){
					if(sRight.isAdd() == oldsRight.isAdd() &&  sRight.isCheck() == oldsRight.isCheck() && sRight.isControl() == oldsRight.isControl() 
							&& sRight.isDelete() == oldsRight.isDelete() && sRight.isDownload() == oldsRight.isDownload() && sRight.isExtend() == oldsRight.isExtend() 
//							&& sRight.isPrint() == oldsRight.isPrint() 
							&& sRight.isRead() == oldsRight.isRead() && sRight.isRename() == oldsRight.isRename() 
							&& sRight.isView() == oldsRight.isView() && sRight.isWrite() == oldsRight.isWrite()
							){
						if(!flag) break;
						else{
							DateTimeFormat day = DateTimeFormat.getFormat("yyyy/MM/dd");
							boolean isStartday = day.format(sRight.getStartday()).equals(day.format(oldsRight.getStartday()));
							boolean isExpireDay = day.format(sRight.getExpiredday()).equals(day.format(oldsRight.getExpiredday()));
							if(isStartday && isExpireDay) break;
							else return false;
						}
					}
					else
						return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean check(List<SRight> list, Boolean isRetention) {
		if(list.size() != oldSRights.size() || isRetention != flag)  return false;
		
		for (SRight sRight : list) {
			for (SRight oldsRight : oldSRights) {
				if(sRight.getEntityId().equals(oldsRight.getEntityId())){
					if(sRight.isAdd() == oldsRight.isAdd() &&  sRight.isCheck() == oldsRight.isCheck() && sRight.isControl() == oldsRight.isControl() 
							&& sRight.isDelete() == oldsRight.isDelete() && sRight.isDownload() == oldsRight.isDownload() && sRight.isExtend() == oldsRight.isExtend() 
//							&& sRight.isPrint() == oldsRight.isPrint() 
							&& sRight.isRead() == oldsRight.isRead() && sRight.isRename() == oldsRight.isRename() 
							&& sRight.isView() == oldsRight.isView() && sRight.isWrite() == oldsRight.isWrite()
							){
						if(!flag) break;
						else{
							DateTimeFormat day = DateTimeFormat.getFormat("yyyy/MM/dd");
							boolean isStartday = day.format(sRight.getStartday()).equals(day.format(oldsRight.getStartday()));
							boolean isExpireDay = day.format(sRight.getExpiredday()).equals(day.format(oldsRight.getExpiredday()));
							if(isStartday && isExpireDay) break;
							else return false;
						}
					}
					else
						return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean check(DynamicForm currentFrom) {
		
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
		
		int changed = 0;
		
		for (int i = 0; i < currentFrom.getFields().length; i++) {
			Object value = oldValue.get(currentFrom.getFields()[i].getTitle());
			if(value == null && currentFrom.getFields()[i].getValue() == null) continue;
			else if(value == null && currentFrom.getFields()[i].getValue() != null) return false;
			else if(!value.equals(currentFrom.getFields()[i].getValue()))
				return false;
		}
		
		return true;
	}
	
	public boolean check(DynamicForm currentFrom, boolean isFlag) {
		if(isFlag != this.flag) return false;
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
		
		int changed = 0;
		
		for (int i = 0; i < currentFrom.getFields().length; i++) {
			Object value = oldValue.get(currentFrom.getFields()[i].getTitle());
			if(value == null && currentFrom.getFields()[i].getValue() == null) continue;
			else if(value == null && currentFrom.getFields()[i].getValue() != null) return false;
			else if(!value.equals(currentFrom.getFields()[i].getValue()))
				return false;
		}
		
		return true;
	}
	
	public boolean check(ListGrid grid) {
		
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
		for(int i=0; i<grid.getRecords().length; i++){
			String[] attributes = grid.getRecord(i).getAttributes();
			Map map = oldValueList.get(i);
			for(int j=0 ; j < attributes.length ; j++ ){
				String key = attributes[j];
				String value = grid.getRecord(i).getAttribute(key);
				if(value == null) continue;
				if(key.startsWith("_")) continue;
				else if(!value.equals(map.get(key))) return false;
			}
		}
		
		return true;
	}
	
	public int check(DynamicForm newForm, List oldForm) {
		
//		System.out.println(oldForm.size());
//		System.out.println(newForm.getFields().length);
		
		int changed = 0;
		
		for (int i = 0; i < newForm.getFields().length; i++) {
			
//			System.out.println("1= "+newForm.getFields()[i].getFieldName());
//			System.out.println("2= "+newForm.getFields()[i].getValue());
//			System.out.println("=========================");
//			System.out.println("3= "+oldForm.get(i).toString());
//			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");
			
			if(newForm.getFields()[i].getValue().toString().equals(oldForm.get(i).toString())) {
				changed++;
			}
		}
		
		return changed;
	}
	
	public void setMap(DynamicForm newForm) {
		oldValue.clear();
		
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
			for(int i=0; i<newForm.getFields().length; i++)
				oldValue.put(newForm.getFields()[i].getTitle(), newForm.getFields()[i].getValue());
	}
	
	public void setMap(DynamicForm newForm, boolean isDownload) {
		oldValue.clear();
		this.flag = isDownload;
		
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
			for(int i=0; i<newForm.getFields().length; i++)
				oldValue.put(newForm.getFields()[i].getTitle(), newForm.getFields()[i].getValue());
	}
	
	public void setMap(ListGrid grid) {
		oldValueList.clear();
		
		// kimsoeun GS인증용 - 변경사항 없을 때 리턴
		for(int i=0; i<grid.getRecords().length; i++){
			Map map = new  HashMap<String, String>();
			String[] attributes = grid.getRecord(i).getAttributes();
			for(int j=0 ; j < attributes.length ; j++ ){
				String key = attributes[j];
				String value = grid.getRecord(i).getAttribute(attributes[j]);
				if(key.startsWith("_")) continue;
				map.put(key, value);
			}
			oldValueList.add(map);
		}
	}
	
	public void setMap(List<SRight> list, boolean retention){
		oldSRights = list;
		this.flag = retention;
	}
	
	public int check2(List newForm, List oldForm) {
		
		int changed = 0;
		
		for (int i = 0; i < newForm.size(); i++) {
			
			String s = newForm.get(i)!=null ? newForm.get(i).toString() : "" ;
			String s2 = oldForm.get(i)!=null ? oldForm.get(i).toString() : "" ;
			
			if(s.equals(s2)) {
//				System.out.println(i+" = "+newForm.get(i));
				changed++;
			}
		}
		
		return changed;
	}
	
	public int check3(List newForm, List oldForm) {
		
		int changed = 0;
		
		for (int i = 0; i < newForm.size(); i++) {
			if(newForm.get(i) == oldForm.get(i)) {
//				System.out.println(i+" = "+newForm.get(i));
				changed++;
			}
		}
		
		return changed;
	}
	
	public boolean check4(List<Map> newForm, List<Map> oldForm) {
		
		boolean changed = false;
//		System.out.println(newForm.size());
//		System.out.println(oldForm.size());
		
		if(newForm.size() == oldForm.size()) {
			for (int i = 0; i < newForm.size(); i++) {
				
//				System.out.println(newForm.get(i).keySet());
				if(newForm.get(i).equals(oldForm.get(i))) {
					changed = true;
				}
			}
		}
		
		return changed;
	}
	
	
	public void setList(DynamicForm newForm, List oldForm) {
		oldForm.removeAll(oldForm);
		
//		System.out.println(newForm.getFields().length);
		for(int i=0; i<newForm.getFields().length; i++){
			Object value = newForm.getFields()[i].getValue();
			if(value == null ) continue;
			oldForm.add(newForm.getFields()[i].getValue().toString());
			//System.out.println(newForm.getFields()[i].getFieldName());
		};
		
//		System.out.println(oldForm.size());		
	}
	
	public void setList2(String message, List oldForm) {
		
		oldForm.add(message);
		
//		System.out.println(oldForm.get(0).toString());
		
	}
	
	public void setList3(DynamicForm newForm, List<Map> oldForm) {
		oldForm.removeAll(oldForm);
		
//		System.out.println(newForm.getFields().length);
		for(int i=0; i<newForm.getFields().length; i++){
			if(!"_extended".equals(newForm.getFields()[i].getFieldName())) {
				Map map = new HashMap();
				map.put(newForm.getFields()[i].getFieldName(), newForm.getFields()[i].getValue());
				oldForm.add(map);
				System.out.println(newForm.getFields()[i].getFieldName());
			}
		};
		
//		System.out.println(oldForm.size());		
	}
	

	public void setValue(long profileId) {
		this.profileId = profileId;
	}
}