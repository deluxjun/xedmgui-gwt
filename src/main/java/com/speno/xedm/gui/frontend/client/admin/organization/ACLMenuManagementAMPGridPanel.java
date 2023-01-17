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
 * @author 박상기
 * @since 1.0
 */
public class ACLMenuManagementAMPGridPanel extends VLayout {

	private static ACLMenuManagementAMPGridPanel instance;
	public TreeGrid treeGrid;
	// kimsoeun GS인증용 - 변경사항 있는지 유효성 검사
	private List oldList = new ArrayList();

	// 상위 RecordObserver에게 Callback하기위해 RecordObserver를 맴버로도 가짐.
	private final RecordObserver recordObserver;

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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

		/* Sub Title 생성 */
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();
		subTitleLable.setAlign(Alignment.LEFT);
		subTitleLable.setValign(VerticalAlignment.CENTER);
		subTitleLable.setStyleName("subTitle");
		// kimsoeun GS인증용 - 관리자 메뉴 권한 문구 뺌
//		subTitleLable.setContents(I18N.message("adminmenuprivileges"));
		subTitleLable.setContents(I18N.message(""));
		addMember(subTitleLable);

		setHeight100();
		setMembersMargin(10);

		// 트리그리드
		treeGrid = new TreeGrid();
		treeGrid.setWidth(500);
		treeGrid.setHeight100();
		treeGrid.setNodeIcon(ItemFactory.newImgIcon("generic.png").getSrc());
		treeGrid.setCascadeSelection(true);

		// DB로부터 메뉴 정보가져오기
		GetMenuData();

		treeGrid.setBorder("1px solid gray");
		treeGrid.setShowHeader(false);

		// treeGrid 를 판넬에 멤버로 추가
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
						
						// kimsoeun GS인증용 - 기존 메뉴 리스트 추가
						oldList.removeAll(oldList);
						
						for (int i = 0; i < records.length; i++) {						
							
							if(result.contains(records[i].getAttributeAsLong("ID")))
							{
								// kimsoeun GS인증용 - 기존 메뉴 리스트 추가
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
	
	// kimsoeun GS인증용 - 기존 메뉴 리스트
	public List getOldCheckedList() {
		return oldList;
	}
	
	public void GetMenuData() {
		ServiceUtil.security().listMenu(Session.get().getSid(),
				new AsyncCallback<List<SAdminMenu>>() {
					@Override
					public void onSuccess(List<SAdminMenu> result) {
						int iTotalCnt = result.size();
						// 데이터가 존재하면
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
