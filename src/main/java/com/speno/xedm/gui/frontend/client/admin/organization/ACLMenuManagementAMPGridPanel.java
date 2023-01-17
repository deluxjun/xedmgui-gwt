package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SAdminMenu;
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
 * @author �ڻ��
 * @since 1.0
 */
public class ACLMenuManagementAMPGridPanel extends VLayout {

	private static ACLMenuManagementAMPGridPanel instance;
	public TreeGrid treeGrid;
	// kimsoeun GS������ - ������� �ִ��� ��ȿ�� �˻�
	private List oldList = new ArrayList();

	// ���� RecordObserver���� Callback�ϱ����� RecordObserver�� �ɹ��ε� ����.
	private final RecordObserver recordObserver;

	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * 
	 * @param id
	 * @param ob
	 * @param isShowRoot
	 * @return
	 */
	public static ACLMenuManagementAMPGridPanel get(final String id,
			final RecordObserver ob, final boolean isShowRoot) {
		return get(id, ob, isShowRoot, "100%");
	}

	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * 
	 * @param id
	 * @param ob
	 * @param isShowRoot
	 * @param width
	 * @return
	 */
	public static ACLMenuManagementAMPGridPanel get(final String id, final RecordObserver ob, final boolean isShowRoot,	final String width) {
		reset();
		instance =new ACLMenuManagementAMPGridPanel(id, ob, isShowRoot, width);	
		return instance;
	}
	
	private static void reset() {
		if (instance != null) {
			instance.destroy();
			instance = null;
		}
	}


	public ACLMenuManagementAMPGridPanel(final String id,
			final RecordObserver ob, final boolean isShowRoot) {
		this(id, ob, isShowRoot, "100%");
	}

	public ACLMenuManagementAMPGridPanel(final String id,
			final RecordObserver ob, final boolean isShowRoot,
			final String width) {
		
		this.recordObserver = ob;

		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);

		/* Sub Title ���� */
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();
		subTitleLable.setAlign(Alignment.LEFT);
		subTitleLable.setValign(VerticalAlignment.CENTER);
		subTitleLable.setStyleName("subTitle");
		// kimsoeun GS������ - ������ �޴� ���� ���� ��
//		subTitleLable.setContents(I18N.message("adminmenuprivileges"));
		subTitleLable.setContents(I18N.message(""));
		addMember(subTitleLable);

		setHeight100();
		setMembersMargin(10);

		// Ʈ���׸���
		treeGrid = new TreeGrid();
		treeGrid.setWidth(500);
		treeGrid.setHeight100();
		treeGrid.setNodeIcon(ItemFactory.newImgIcon("generic.png").getSrc());
		treeGrid.setCascadeSelection(true);

		// DB�κ��� �޴� ������������
		GetMenuData();

		treeGrid.setBorder("1px solid gray");
		treeGrid.setShowHeader(false);

		// treeGrid �� �ǳڿ� ����� �߰�
		addMember(treeGrid);
	}

	public void SetMenuData(long id) {
		
		treeGrid.deselectAllRecords();
		final Tree tree = treeGrid.getData();
		final TreeNode[] nodelist = tree.getDescendantLeaves();
		String Sid = Session.get().getSid();
		ServiceUtil.security().listMenuIdsBySecurityProfileId(
				Sid, id, new AsyncCallback<List<Long>>() {
					@Override
					public void onSuccess(List<Long> result) {
						// TODO Auto-generated method stub
						ListGridRecord[] records = treeGrid.getRecords();
						
						// kimsoeun GS������ - ���� �޴� ����Ʈ �߰�
						oldList.removeAll(oldList);
						
						for (int i = 0; i < records.length; i++) {						
							
							if(result.contains(records[i].getAttributeAsLong("ID")))
							{
								// kimsoeun GS������ - ���� �޴� ����Ʈ �߰�
								oldList.add(records[i]);
								
								for(TreeNode node : nodelist)
								{									
									if(node.getAttributeAsLong("ID").compareTo(records[i].getAttributeAsLong("ID")) == 0)
									{								
									treeGrid.selectRecord(records[i]);	

									}
								}								
							}
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						SC.warn(I18N.message("runButError"));
					}
				});
		
		
	}
	
	// kimsoeun GS������ - ���� �޴� ����Ʈ
	public List getOldCheckedList() {
		return oldList;
	}
	
	public void GetMenuData() {
		ServiceUtil.security().listMenu(Session.get().getSid(),
				new AsyncCallback<List<SAdminMenu>>() {
					@Override
					public void onSuccess(List<SAdminMenu> result) {
						int iTotalCnt = result.size();
						// �����Ͱ� �����ϸ�
						if (iTotalCnt > 0) {
							TreeNode rootNode = new TreeNode();
							rootNode.setAttribute("ID",
									Long.toString(Constants.ADMIN_ROOT));
							rootNode.setAttribute("TITLE", "root");

							List<TreeNode> nodes = new ArrayList<TreeNode>();
							// TreeNode[] returnNode = new
							// TreeNode[result.size()];

							for (int i = 0; i < result.size(); i++) {
								if (result.get(i).getId() == 0)
									continue;
								TreeNode node = new TreeNode();
								node.setAttribute("ID", result.get(i).getId());
								node.setAttribute("TITLE",
										I18N.message(result.get(i).getTitle()));
								node.setAttribute("PARENTID", result.get(i)
										.getParentId());
								node.setAttribute("SECURITYREF", result.get(i)
										.getSecurityRefs());

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
							SetMenuData(MenuManagementPanel.seleted_recordid);
							treeGrid.refreshFields();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(I18N.message("genericerror"), caught,
								true);
					}
				});
	}
}
