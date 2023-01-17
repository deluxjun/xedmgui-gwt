package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stack<T> {
	
	private int max;
	
	private Map<String, T> maps = new HashMap<String, T>();
	private List<String> idList = new ArrayList<String>();		// old ���� �ֽű��� ������ ���� full �� �Ǹ�, old���� ����� ���� ������.
	
	public Stack(int _size) {
		max = _size;
	}
	
	public String put(T obj) {
		String id = GUID.get();
		
		if(idList.size() >= max) {
			// remove old
			String oldId = idList.remove(0);
			if (oldId != null)
				maps.remove(oldId);
		} else {
			idList.add(id);
		}

		maps.put(id, obj);
		
		return id;
	}
	public T get(String key) {
		return maps.get(key);
	}
	
	public int size() {
		return idList.size();
	}
}