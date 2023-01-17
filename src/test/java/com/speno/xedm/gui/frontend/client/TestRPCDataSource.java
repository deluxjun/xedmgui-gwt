package com.speno.xedm.gui.frontend.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.services.ECMService;
import com.speno.xedm.gui.common.client.services.ECMServiceAsync;
import com.speno.xedm.gui.common.client.util.GwtRPCDataSource;
import com.speno.xedm.gui.common.client.util.PagingConfig;
import com.speno.xedm.gui.common.client.util.PagingResult;

public class TestRPCDataSource extends GwtRPCDataSource
{
	ECMServiceAsync securityService = (ECMServiceAsync) GWT.create(ECMService.class);
	
	public static int totalRows=0;

	public TestRPCDataSource()
	{
		DataSourceTextField idField = new DataSourceTextField("id");
		idField.setPrimaryKey(true);

		DataSourceTextField nameField = new DataSourceTextField("desc");
		
		setFields(idField, nameField);
	}


	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response)
	{
	
		GWT.log(" called from "+request.getStartRow()+" to "+request.getEndRow(), null);

		//assume we have 1000 items.
		int end=request.getEndRow();
		if (totalRows != 0 && end > totalRows)
		{
			end=totalRows;
		}
		final int max = end - request.getStartRow();

		// call service
		PagingConfig config = new PagingConfig(totalRows, request.getStartRow(), max);
		
		
		
		securityService.pagingDocuments("", "DOC", config, new AsyncCallback<PagingResult<SDocument>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(PagingResult<SDocument> result) {
				List<SDocument> data = result.getData();
				ListGridRecord records[] = new ListGridRecord[data.size()];
				for (int j = 0; j < data.size(); j++) {
					SDocument user = data.get(j);
					ListGridRecord r=new ListGridRecord();
					r.setAttribute("id", user.getId());
					r.setAttribute("desc", user.getComment());
					records[j]=r;
				}
				GWT.log("offset = "+result.getOffset()+", total = "+ result.getTotalLength() + ", max = " + data.size(), null);
				totalRows = result.getTotalLength();
				
				response.setTotalRows (totalRows);
				response.setData(records);
				processResponse(requestId, response);
			}
		});
	}


	@Override
	protected void executeAdd(String requestId, DSRequest request, DSResponse response)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void executeRemove(String requestId, DSRequest request, DSResponse response)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void executeUpdate(String requestId, DSRequest request, DSResponse response)
	{
		// TODO Auto-generated method stub
		
	}
}