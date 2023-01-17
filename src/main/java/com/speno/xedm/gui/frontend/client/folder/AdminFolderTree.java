package com.speno.xedm.gui.frontend.client.folder;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * AdminFolder Tree
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class AdminFolderTree extends AdminTree{
	public AdminFolderTree(RecordObserver observer) {
		super(observer);		
		setNodeIcon(ItemFactory.newImgIcon("group.png").getSrc());   
		setFolderIcon(ItemFactory.newImgIcon("group.png").getSrc());
	}
	
	@Override
	public void getFolderDataRpcChild( final TreeNode selectNode, final boolean isForceOpen){
		
		String id = selectNode.getAttributeAsString("id");
		GWT.log("[ AdminFolderTree getFolderDataRpcChild ] id["+id+"]", null);
		
		ServiceUtil.folder().listFolderByTypeAndParentId(Session.get().getSid(), SFolder.TYPE_SHARED, Long.parseLong(id), new AsyncCallback<List<SFolder>>() {
			@Override
			public void onSuccess(List<SFolder> result) {		
				boolean isParentOrNot;
				for(int i=0; i< result.size(); i++){
					TreeNode treeNode = new TreeNode();
					treeNode.setAttribute("id",				result.get(i).getId());
					treeNode.setAttribute("name",			result.get(i).getName());
					treeNode.setAttribute("description",	result.get(i).getDescription());
					treeNode.setAttribute("parentId",		result.get(i).getParentId());
					treeNode.setAttribute("type",			result.get(i).getType());
					treeNode.setAttribute("paths",			result.get(i).getPaths());
					treeNode.setAttribute("profileId",		result.get(i).getSecurityProfileId());					
					
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
		 * ���� �ѹ��� ȣ�� �� ��.
		 */
		GWT.log("[ AdminFolderTree getFolderDataRpc ] id["+id+"] isShowVirtualRoot["+isShowVirtualRoot+"]", null);
		
		ServiceUtil.folder().listFolderByTypeAndParentId(Session.get().getSid(), SFolder.TYPE_SHARED, (Long)id, new AsyncCallback<List<SFolder>>() {
			@Override
			public void onSuccess(List<SFolder> result) {				
				TreeNode rootNode = new TreeNode();
				rootNode.setAttribute("id", Long.toString(Constants.ADMIN_ROOT));
				rootNode.setAttribute("name", "root");
				
				int root = isShowVirtualRoot ? 1 : 0;

				TreeNode[] returnNode = new TreeNode[result.size()+root];
				boolean isParentOrNot;
				
				if(isShowVirtualRoot) {
					returnNode[0] =  new TreeNode();
					returnNode[0].setAttribute("id", Constants.ADMIN_FOLDER_ROOT);					
					returnNode[0].setAttribute("name", "root");
					returnNode[0].setAttribute("description", "virtual root");
					returnNode[0].setAttribute("parentId", Constants.ADMIN_ROOT);
					returnNode[0].setAttribute("type", SFolder.TYPE_SHARED);
					returnNode[0].setAttribute("paths", "root");
					//returnNode[0].setAttribute("profileId", Constants.ADMIN_FOLDER_ROOT); �� ���� ���� null ���η� üũ�ϴ� ���� ����.
					returnNode[0].setAttribute("expand", (result.size() > 0));
				}
				
				for(int i=0; i< result.size(); i++){
					returnNode[i+root] = new TreeNode();
					returnNode[i+root].setAttribute("id",			result.get(i).getId());
					returnNode[i+root].setAttribute("name",			result.get(i).getName());
					returnNode[i+root].setAttribute("description",	result.get(i).getDescription());
					returnNode[i+root].setAttribute("parentId",		result.get(i).getParentId());
					returnNode[i+root].setAttribute("type",			result.get(i).getType());
					returnNode[i+root].setAttribute("paths",		result.get(i).getPaths());
					returnNode[i+root].setAttribute("profileId",	result.get(i).getSecurityProfileId());
					
					isParentOrNot = result.get(i).isParentOrNot();
					returnNode[i+root].setAttribute("expand", isParentOrNot);
					returnNode[i+root].setIsFolder(isParentOrNot);
				}
				
				Tree dataTree = new Tree();
				dataTree.setModelType(TreeModelType.PARENT);
				dataTree.setIdField("id");
				dataTree.setParentIdField("parentId");
				dataTree.setNameProperty("id"); // ((TreeGrid)dragSourceGrid).getSelectedPaths(); ���� ���� path�� �����ϰԵ�.
				
				dataTree.setAutoOpenRoot(true);
				dataTree.setReportCollisions(false);
				dataTree.setShowRoot(false);
				
				dataTree.setRoot(rootNode);
				dataTree.setData(returnNode);
				
				setData(dataTree);
				refreshFields();
				
				// �ֻ�� ���� ���õ� ���·� ������.
				dataTree.openFolder(returnNode[0]);
				//���� ���õ� ���·�
				if(returnNode.length > 0) {
					selectRecord(0);						
					if(recordObserver != null) {
						long id = Util.getAslong(returnNode[0].getAttributeAsString("id"));		
						long parentId = Util.getAslong(returnNode[0].getAttributeAsString("parentId"));
						
						recordObserver.onRecordSelected(id, parentId);
						recordObserver.onRecordSelected(returnNode[0]);
	            	}
				}
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
		});	
	}
}