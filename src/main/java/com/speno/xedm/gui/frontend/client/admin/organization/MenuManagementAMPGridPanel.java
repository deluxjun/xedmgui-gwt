package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ServiceUtil;


/**
 * GroupTree Panel
 * 
 * @author 박상기
 * @since 1.0
 */
//커밋하기 위한 주석
public class MenuManagementAMPGridPanel extends VLayout {	
	
	private static HashMap<String, MenuManagementAMPGridPanel> instanceMap = new HashMap<String, MenuManagementAMPGridPanel>();

	public TreeGrid treeGrid;
	
	
	//상위 RecordObserver에게 Callback하기위해 RecordObserver를 맴버로도 가짐.
	private final RecordObserver recordObserver;

	private String tabname;
	private List<SAdminMenu> admin_list;	

	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param isShowRoot
	 * @return
	 */
	public static MenuManagementAMPGridPanel get(
			final String id, 
			final RecordObserver ob, 
			final boolean isShowRoot) {
		return get(id, ob, isShowRoot, "100%");
		
	}
	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param isShowRoot
	 * @param width
	 * @return
	 */
	public static MenuManagementAMPGridPanel get(
			final String id, 
			final RecordObserver ob,  
			final boolean isShowRoot, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new MenuManagementAMPGridPanel(id, ob, isShowRoot, width);
		}		
		return instanceMap.get(id);
	}
	
	public MenuManagementAMPGridPanel(
			final String id, 
			final RecordObserver ob, 
			final boolean isShowRoot) {
		this(id, ob, isShowRoot, "100%");
	}
	public MenuManagementAMPGridPanel(
			final String id, 
			final RecordObserver ob,
			final boolean isShowRoot,
			final String width) {
		
		instanceMap.put(id, this);
		
		this.recordObserver = ob;
		

		setMembersMargin(10);		
		setPadding(Constants.PADDING_DEFAULT);
        
		/* Sub Title 생성 */
		Label subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("adminmenuprivileges"));
        addMember(subTitleLable);
        
		setHeight100();
        setMembersMargin(10);	

        //트리그리드
        treeGrid = new TreeGrid();   
        treeGrid.setWidth(500);   
        treeGrid.setHeight100();   
        treeGrid.setNodeIcon(ItemFactory.newImgIcon("generic.png").getSrc());   
        
        //DB로부터 메뉴 정보가져오기
        GetMenuData();
        
        treeGrid.setBorder("1px solid gray");
        treeGrid.setShowHeader(false);
        
        //treeGrid 가 그려지고나서의 이벤트
        treeGrid.addDrawHandler(new DrawHandler() {   
            public void onDraw(DrawEvent event) {
            	//
            }   
        });
   
        //treeGrid 를 판넬에 멤버로 추가
        addMember(treeGrid);
        
        //treeGrid selection changed 이벤트
        treeGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				// TODO Auto-generated method stub
				if(event.getSelection().length > 0){
					Long lngId = Long.valueOf(treeGrid.getSelectedRecord().getAttribute("ID"));
					Long lngParentId;
					if(treeGrid.getSelectedRecord().getAttribute("PARENTID") != null){
						lngParentId = Long.valueOf(treeGrid.getSelectedRecord().getAttribute("PARENTID"));
					}else{
						lngParentId = (long) -1;
					}
					
					if(recordObserver != null) {
						recordObserver.onRecordSelected(lngId, lngParentId);
					}
				}
			}
		});
	}
	public void read_tabname(String tabname)
	{
		this.tabname = tabname;
	}

	public void setMenuData(final SSecurityProfile SP)
	{		
		treeGrid.deselectAllRecords();
		int index = 0;
		
		for(SAdminMenu menu : admin_list)
		{			
			for(long ref : menu.getSecurityRefs())
			{				
 				if(ref == SP.getId())
				{
					Record R = treeGrid.getRecord(index); 
					treeGrid.selectRecord(R);
				}			
			}
			index++;
		}				
	}
	
    public void GetMenuData() {
    	ServiceUtil.security().listMenu(Session.get().getSid(), new AsyncCallback<List<SAdminMenu>>() {
			@Override
			public void onSuccess(List<SAdminMenu> result) {
				admin_list = result;				
				int iTotalCnt = result.size();
				//데이터가 존재하면
				if(iTotalCnt > 0){
					TreeNode rootNode = new TreeNode();
					rootNode.setAttribute("ID", Long.toString(Constants.ADMIN_ROOT));
					rootNode.setAttribute("TITLE", "root");

					List<TreeNode> nodes = new ArrayList<TreeNode>();
//					TreeNode[] returnNode = new TreeNode[result.size()];
					
					for(int i=0; i< result.size(); i++){
						if (result.get(i).getId() == 0)
							continue;
						TreeNode node = new TreeNode();
						node.setAttribute("ID",				result.get(i).getId());
						node.setAttribute("TITLE",			I18N.message(result.get(i).getTitle()));
						node.setAttribute("PARENTID",		result.get(i).getParentId());
						node.setAttribute("SECURITYREF",	result.get(i).getSecurityRefs());
						
						nodes.add(node);
					}
					
					Tree dataTree = new Tree();
					dataTree.setModelType(TreeModelType.PARENT);
					dataTree.setIdField("ID");
					dataTree.setParentIdField("PARENTID");
					dataTree.setNameProperty("TITLE"); 
					
					dataTree.setAutoOpenRoot(true);
					dataTree.setReportCollisions(false);
					dataTree.setShowRoot(false);
										
					dataTree.setRoot(rootNode);
					dataTree.setData(nodes.toArray(new TreeNode[0]));

					treeGrid.setData(dataTree);
					treeGrid.getTree().openAll();
					treeGrid.refreshFields();
//					treeGrid.selectRecord(0);
				}
			}		
			
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError( I18N.message("genericerror"), caught, true);
			}
		});
	}  
    
}
