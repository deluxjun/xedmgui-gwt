package com.speno.xedm.gui.frontend.client.folder;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * AdminGroup Tree
 * 
 * @author 박상기
 * @since 1.0
 */
public class AdminGroupTree extends AdminTree{
	public AdminGroupTree(RecordObserver observer) {
		super(observer, I18N.message("allgroups"));
		
		setNodeIcon(ItemFactory.newImgIcon("group.png").getSrc());   
		setFolderIcon(ItemFactory.newImgIcon("group.png").getSrc());
	}
	
	@Override
	public void getFolderDataRpcChild( final TreeNode selectNode, final boolean isForceOpen){
		
		String id = selectNode.getAttributeAsString("id");
		GWT.log("[ AdminGroupTree getFolderDataRpcChild ] id["+id+"]", null);
		
		ServiceUtil.security().listGroupByParentId(Session.get().getSid(), id, new AsyncCallback<List<SGroup>>() {
			@Override
			public void onSuccess(List<SGroup> result) {		
				boolean isParentOrNot;
				for(int i=0; i< result.size(); i++){
					TreeNode treeNode = new TreeNode();
					treeNode.setAttribute("id",				result.get(i).getId());
					treeNode.setAttribute("name",			result.get(i).getName());
					treeNode.setAttribute("description",	result.get(i).getDescription());
					treeNode.setAttribute("parentId",		result.get(i).getParentId());
					treeNode.setAttribute("type",			result.get(i).getType());
					treeNode.setAttribute("path",			result.get(i).getPath());
					treeNode.setAttribute("IDPath",			result.get(i).getIDPath());
					
					isParentOrNot = result.get(i).isParentOrNot();
					treeNode.setAttribute("expand", isParentOrNot);
					treeNode.setIsFolder(isParentOrNot);
					
					getTree().add(treeNode, selectNode);
			    }							
				refreshFields();
				
				if(isForceOpen) {
					getTree().openFolder(selectNode);
				}
			}						
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});	
	
	}
	
	@Override
	public void getFolderDataRpc(final Serializable id, final boolean isShowVirtualRoot) {
		/*
		 * 최초 한번만 호출 할 것.
		 */
		GWT.log("[ AdminGroupTree getFolderDataRpc ] id["+id+"] isShowVirtualRoot["+isShowVirtualRoot+"]", null);
		
		ServiceUtil.security().listGroupByParentId(Session.get().getSid(), (String)id, new AsyncCallback<List<SGroup>>() {
			@Override
			public void onSuccess(List<SGroup> result) {
				TreeNode rootNode = new TreeNode();
				rootNode.setAttribute("id", Long.toString(Constants.ADMIN_ROOT));
				rootNode.setAttribute("name", "root");
				
				int root = isShowVirtualRoot ? 1 : 0;

				TreeNode[] returnNode = new TreeNode[result.size()+root];
				boolean isParentOrNot;
				
				if(isShowVirtualRoot) {
					returnNode[0] =  new TreeNode();
					returnNode[0].setAttribute("id", Constants.ADMIN_GROUP_ROOT);					
					returnNode[0].setAttribute("name", "root");
					returnNode[0].setAttribute("description", "virtual root");
					returnNode[0].setAttribute("parentId", Constants.ADMIN_ROOT);
					returnNode[0].setAttribute("path", "root");
					returnNode[0].setAttribute("IDPath", Constants.ADMIN_ROOT);
					returnNode[0].setAttribute("expand", (result.size() > 0));
				}
				
				for(int i=0; i< result.size(); i++){
					returnNode[i+root] = new TreeNode();
					returnNode[i+root].setAttribute("id",			result.get(i).getId());
					returnNode[i+root].setAttribute("name",			result.get(i).getName());
					returnNode[i+root].setAttribute("description",	result.get(i).getDescription());
					returnNode[i+root].setAttribute("parentId",		result.get(i).getParentId());
					returnNode[i+root].setAttribute("type",			result.get(i).getType());
					returnNode[i+root].setAttribute("path",			result.get(i).getPath());
					returnNode[i+root].setAttribute("IDPath",		result.get(i).getIDPath());
					
					isParentOrNot = result.get(i).isParentOrNot();
					returnNode[i+root].setAttribute("expand", isParentOrNot);
					returnNode[i+root].setIsFolder(isParentOrNot);
				}
				
				Tree dataTree = new Tree();
				dataTree.setModelType(TreeModelType.PARENT);
				dataTree.setIdField("id");
				dataTree.setParentIdField("parentId");
				dataTree.setNameProperty("id"); // ((TreeGrid)dragSourceGrid).getSelectedPaths(); 에서 나올 path를 결정하게됨.
				
				dataTree.setAutoOpenRoot(true);
				dataTree.setReportCollisions(false);
				dataTree.setShowRoot(false);
				
				
				dataTree.setRoot(rootNode);
				dataTree.setData(returnNode);

				setData(dataTree);
				refreshFields();
				
				// 20140205na 최상단 폴더 선택될 필요 없음
				// 최상단 폴더 선택된 상태로 보여줌.
				if(isShowVirtualRoot) dataTree.openFolder(returnNode[0]);
				//최초 선택된 상태로
//				if(returnNode.length > 0) {
//					selectRecord(0);						
//					if(recordObserver != null) {
//						String id = Util.getSafeString(returnNode[0].getAttributeAsString("id"));		
//						String parentId = Util.getSafeString(returnNode[0].getAttributeAsString("parentId"));
//						
//						recordObserver.onRecordSelected(id, parentId);
//						recordObserver.onRecordSelected(returnNode[0]);
//	            	}
//				}
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});	
	}
}