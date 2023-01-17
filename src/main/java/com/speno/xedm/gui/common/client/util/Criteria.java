package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Criteria implements IsSerializable {
	private static final long serialVersionUID = 1L;

	List<String> fields = new ArrayList<String>();
	List<String> operators = new ArrayList<String>();
	List<Object> values = new ArrayList<Object>();
	List<String> relations = new ArrayList<String>();
	
	public void add(String field, String operator, Object value, String relation){
		fields.add(field);
		operators.add(operator);
		values.add(value);
		values.add(relation);
	}
	
	public String getQuery() {
		StringBuffer query = new StringBuffer("");
		int index = 0;
		for (String field : fields) {
			if (index > 0) {
				String relation = relations.get(index-1);
				query.append(" " + relation);
			}

			String operator = operators.get(index);
			Object value = values.get(index);
			query.append(field + " " +operator + " "); 
			
			query.append("?");
			
			index ++;
		}
		
		return query.toString();
	}
	
	public Object[] getValues() {
		Object[] objects = new Object[values.size()];
		values.toArray(objects);
		return objects;
	}
}
