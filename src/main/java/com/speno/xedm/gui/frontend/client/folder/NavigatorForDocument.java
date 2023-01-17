package com.speno.xedm.gui.frontend.client.folder;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Useful;
import com.speno.xedm.gui.frontend.client.clipboard.Clipboard;
import com.speno.xedm.gui.frontend.client.document.DocumentsGrid;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * Modified : 20130816, junsoo, FOLDER_TYPE_ALL ���� ���� ��� ������.
 * FolderSelectorDialog ���� ó���ϵ��� ��.
 * 
 * @author deluxjun
 * 
 */
/**
 * @author test
 *
 */
public class NavigatorForDocument extends TreeGrid implements FolderObserver {
	private TreeNode rootNode;
	private int folderType;
	private List<Boolean> isFolderInfos = new ArrayList<Boolean>();
	
	public int getFolderType() {
		return folderType;
	}

	// 20130731, junsoo, �̱��� ����. �ǹ� �����Ƿ�.
	// private static NavigatorForDocument instance;
	// public static NavigatorForDocument get(int folderType) {
	// if (instance == null)
	// instance = new NavigatorForDocument(folderType);
	// return instance;
	// }

	public NavigatorForDocument(final int folderType) {
		this.folderType = folderType;

		setWidth("100%");
		setHeight("100%");
		setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
		setAutoFitFieldWidths(true);
		setShowAllRecords(true);
		setCanResizeFields(true);

		setBorder("0px");
		setBodyStyleName("normal");
		setLoadDataOnDemand(false);
		setNodeIcon(ItemFactory.newImgIcon("folder.png").getSrc());
		setFolderIcon(ItemFactory.newImgIcon("folder.png").getSrc());
		setOpenIconSuffix("");
		setShowOpenIcons(true);
		setShowDropIcons(false);
		setClosedIconSuffix("");
		setShowHeader(false);
		setSelectionType(SelectionStyle.SINGLE);

		// 20130910, junsoo, �巡�� �Ұ��ϵ��� ������
		setCanDragRecordsOut(false);
		setCanDrag(false);
		setCanDragSelect(false);
		
		setLeaveScrollbarGap(false);
		setCanReorderRecords(false);
		setCanDragRecordsOut(false);
		setCanSelectAll(false);
		setShowConnectors(true);
		setShowRoot(false);
		
		setCanAcceptDrop(false);
		setCanAcceptDroppedRecords(false);


		// ���� Ʈ�� �����̸�
		ListGridField name = new ListGridField("name");
		setFields(name);

		if (folderType != Constants.FOLDER_TYPE_ETC) {
			// ���� + Ŭ���� ���� ���� ��������
			addFolderOpenedHandler(new FolderOpenedHandler() {
				@Override
				public void onFolderOpened(FolderOpenedEvent event) {
					getFolderDataRpcChild(folderType,Long.parseLong(event.getNode().getAttribute("folderId")), event.getNode(), "folderId","name", "parent", "type");
				}
			});

			// ���� Ŭ���� ���� ���� �������� ���� ����
			addCellClickHandler(new CellClickHandler() {
				@Override
				public void onCellClick(CellClickEvent event) {
					ListGridRecord rc = event.getRecord();
					TreeNode tn = getTree().findById(rc.getAttribute("folderId"));
					boolean isopen = getTree().isOpen(tn);

					if (!isopen) {
						TreeNode selectedNode = (TreeNode) getSelectedRecord();
						if (selectedNode.getAttributeAsBoolean("expand"))
							getFolderDataRpcChild(folderType,Long.parseLong(selectedNode.getAttribute("folderId")),selectedNode, "folderId", "name", "parent","type");
						getTree().openFolder(selectedNode);
					}
				}
			});

			// 20130910, junsoo, drop ���ɿ��� ����
			setCanAcceptDroppedRecords(true);
			setCanAcceptDrop(true);

			// 20130911, junsoo, drag ���� �Ұ��ϰ� �������� ��� �����Ͽ� �Ʒ� ���� �߰��ϰ� �ذ���.
			addDragStartHandler(new DragStartHandler() {
				@Override
				public void onDragStart(DragStartEvent ev) {
					if (EventHandler.getDragTarget() instanceof NavigatorForDocument){
						//�������� ������������ drag�̺�Ʈ ����
						if ( ! (((NavigatorForDocument)EventHandler.getDragTarget()).getFolderType() == Constants.FOLDER_TYPE_MYDOC
							||	(((NavigatorForDocument)EventHandler.getDragTarget()).getFolderType() == Constants.FOLDER_TYPE_SHARED)))	
								ev.cancel();
					}
				}
			});
			
			addDropHandler(new DropHandler() {
				public void onDrop(final DropEvent event) {
					try {
						ListGrid list = null;
						if (EventHandler.getDragTarget() instanceof NavigatorForDocument) {
							// event ����ϰ� UI ����ȭ�� reload�� ���ؼ� ��.
							event.cancel();
							for (ListGridField field : NavigatorForDocument.this.getAllFields()) {
								field.setAttribute("isFolder", true);
							}
							final NavigatorForDocument navi = ((NavigatorForDocument)EventHandler.getDragTarget());
							if ( !(navi.getFolderType() == Constants.FOLDER_TYPE_MYDOC
									|| (navi.getFolderType() == Constants.FOLDER_TYPE_SHARED))) 
								return;
							

							final long source = Long.parseLong(getDragData()[0].getAttributeAsString("folderId"));
							final long target = Long.parseLong(getDropFolder().getAttributeAsString("folderId"));

							final String sourceName = getDragData()[0].getAttributeAsString("name");
							final String targetName = getDropFolder().getAttributeAsString("name");

							Useful.ask(I18N.message("move"), I18N.message("moveask", new String[] { sourceName, targetName }),
									new BooleanCallback() {

										@Override
										public void execute(Boolean value) {
											if (value) {
												ServiceUtil.folder().move(Session.get().getSid(), source, target, null,
														new AsyncCallback<Void>() {
															@Override
															public void onFailure(Throwable caught) {
																Log.serverError(caught, true);
															}

															@Override
															public void onSuccess(Void ret) {
																// source, target ��� ����
																TreeNode node = getTree().find("folderId", source);
																if (node != null) {
																	getFolderDataRpcChild(navi.getFolderType(), source, node, "folderId", "name", "parent", "type");
																}
																node = getTree().find("folderId", target);
																if (node != null) {
																	getFolderDataRpcChild(navi.getFolderType(), target, node, "folderId", "name", "parent", "type");
																}
																redraw();
																setIsFolrderInfo();
																Log.infoWithPopup(I18N.message("move"), I18N.message("second.client.successfully"));

															}
														});
											}

										}
									});
						} else
						if (EventHandler.getDragTarget() instanceof DocumentsGrid) {
							// event ����ϰ� UI ����ȭ�� reload�� ���ؼ� ��.
							event.cancel();

							list = (ListGrid) EventHandler.getDragTarget();

							final ListGridRecord[] selection = list.getSelectedRecords();
							if (selection == null || selection.length == 0) {
								return;
							}
							final long[] docIds = new long[selection.length];
							for (int i = 0; i < selection.length; i++) {
								docIds[i] = Long.parseLong(selection[i].getAttribute("id"));
							}

							final TreeNode selectedNode = getDropFolder();
							final long folderId = Long.parseLong(selectedNode.getAttribute("folderId"));

							if (Session.get().getCurrentFolder().getId() == folderId) {
								return;
							}

							final String sourceName = selection.length == 1 ? selection[0].getAttribute("titlenm") : (selection.length + " " + I18N.message("documents"));
							final String targetName = selectedNode.getAttributeAsString("name");

							String message = I18N.message("moveask", new String[] { sourceName, targetName });
							String action = Clipboard.CUT;
							DocumentsGrid grid = (DocumentsGrid)EventHandler.getDragTarget();
							if (grid.getDragDataAction() == DragDataAction.COPY) {
								message = I18N.message("copyask", new String[] { sourceName, targetName });
								action = Clipboard.COPY;
							}
							final String fAction = action;

							Useful.ask(I18N.message("move"), message,
									new BooleanCallback() {
										@Override
										public void execute(Boolean value) {
											if (value) {
												// move action!
												ServiceUtil.folder().paste(Session.get().getSid(), docIds, folderId, fAction, new AsyncCallback<Void>() {
													@Override
													public void onSuccess(Void result) {
														// ����ǥ��
														DocumentsPanel.get().onReloadRequest(Session.get().getCurrentFolder());
														String message = I18N.message("second.client.actionCopy");
														if (fAction == Clipboard.CUT)
															message = I18N.message("move");
														setIsFolrderInfo();
														Log.infoWithPopup(message, I18N.message("second.client.successfully"));
													}
													
													@Override
													public void onFailure(Throwable caught) {
														Log.serverError(caught, true);
													}
												});
											} else {
												event.cancel();
												return;
											}

//											TreeNode node = getTree().find("folderId", folderId);
//											if (node != null) {
//												getTree().reloadChildren(node);
//											}
										}
									});
						}
					} catch (Throwable e) {
					}
				}
			});

		} else {
			// 20130731, junsoo, ETC ������ ������ �ʱ�ȭ
			setEtcSection();
		}
	}

	// etc ���� ����
	// 20130731, junsoo, enum ���� ����.
	private void setEtcSection() {

		setData(EtcMenus.getTree());

		// 20130731, junsoo, ��� ����
		getTree().openAll();
	}

	/*
	 * ��Ʈ���� ������ ���� ���� ������ �������� getFolderDataRpc(Ʈ��Ÿ��, Ÿ�پ��̵�, �������̵�, ������,
	 * �θ��������̵�, ����Ÿ��)
	 */
	public void getFolderDataRpc(final int treeType, final int parentId,
			final String id, final String name, final String parent,
			final String type, final boolean setCurrentFolderYn) {
		ServiceUtil.folder().listFolderByTypeAndParentId(Session.get().getSid(), treeType,
				parentId, new AsyncCallback<List<SFolder>>() {
					@Override
					public void onSuccess(List<SFolder> result) {
						SFolder[] sfolder = new SFolder[result.size()];
						long rootId = Constants.DOCUMENTS_FOLDERID;
						// ��Ʈ ����
						rootNode = new TreeNode();
						rootNode.setAttribute(id,
								Long.toString(Constants.DOCUMENTS_FOLDERID));
						rootNode.setAttribute(name, "");

						// for(int i=0; i< result.size(); i++){
						// // ���� ���� ����
						// sfolder[i] = result.get(i);
						// returnNode[i] = new TreeNode();
						// returnNode[i].setAttribute(id, sfolder[i].getId());
						// returnNode[i].setAttribute(name,
						// sfolder[i].getName());
						// returnNode[i].setAttribute(parent,
						// sfolder[i].getParentId());
						// returnNode[i].setAttribute(type,
						// sfolder[i].getType());
						//
						// // �ڽſ��� ���� ������ �ִ��� ����
						// if(sfolder[i].isParentOrNot()){
						// returnNode[i].setAttribute("expand", true);
						// returnNode[i].setIsFolder(true);
						// }
						// else{
						// returnNode[i].setAttribute("expand", false);
						// returnNode[i].setIsFolder(false);
						// }
						//
						// }
						boolean isShowVirtualRoot = true;
						if (treeType != Constants.FOLDER_TYPE_MYDOC)
							isShowVirtualRoot = false;
						int root = isShowVirtualRoot ? 1 : 0;

						TreeNode[] returnNode;
						
						returnNode = new TreeNode[result.size() + root];
						if (isShowVirtualRoot) {
							returnNode[0] = new TreeNode();
							returnNode[0].setAttribute(id, Session.get().getHomeFolderId());
							returnNode[0].setAttribute(name, "Home");
							returnNode[0].setAttribute(parent,Constants.DOCUMENTS_FOLDERID);
							returnNode[0].setAttribute(type,Constants.FOLDER_TYPE_MYDOC);
							
//							rootNode.setAttribute("folderId", Session.get().getHomeFolderId());
//							rootId = Session.get().getHomeFolderId();
//							rootNode.setAttribute("name", "Home");
						}
						else{
							rootNode.setAttribute("folderId", Constants.SHARED_DEFAULTID);
							rootId = Constants.SHARED_DEFAULTID;

							rootNode.setAttribute("name", I18N.message("shareddoc"));
						}
						
						for (int i = 0; i < result.size(); i++) {
							// ���������� �Ӽ����� load
							sfolder[i] = result.get(i);
							returnNode[i + root] = new TreeNode();
							returnNode[i + root].setAttribute(id,sfolder[i].getId());

							// soeun �����뷮/�ִ��뷮 ǥ��
							if(sfolder[i].getTotalSpace() != null) {
								
								// soeun ����� �ڽ��� ������ ��� ������ Home���� ǥ��
								String folderName = Session.get().getHomeFolderId()==sfolder[i].getId() ? "Home" : sfolder[i].getName();
								
								long uSpace = (long) (sfolder[i].getUsedSpace()*0.00000000093132);
								long tSpace = (long) (sfolder[i].getTotalSpace()*0.00000000093132);
								returnNode[i + root].setAttribute(name,folderName+" <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>");
								
							} else {
								returnNode[i + root].setAttribute(name,sfolder[i].getName());
							}
							returnNode[i + root].setAttribute(parent,sfolder[i].getParentId());
							returnNode[i + root].setAttribute(type,sfolder[i].getType());

							// �ڽſ��� ���� ������ �ִ��� ����
							if (sfolder[i].isParentOrNot()) {
								returnNode[i + root].setAttribute("expand", true);
								returnNode[i + root].setIsFolder(true);
							} else {
								returnNode[i + root].setAttribute("expand", false);
								returnNode[i + root].setIsFolder(false);
							}
//							returnNode[i + root].setIsFolder(true);
						}

						// Ʈ�� �����
						Tree dataTree = new Tree();

						dataTree.setModelType(TreeModelType.PARENT);
						dataTree.setIdField(id);
						dataTree.setParentIdField(parent);
						dataTree.setNameProperty(name);

						dataTree.setAutoOpenRoot(true);
						dataTree.setReportCollisions(false);

						dataTree.setShowRoot(false);

						// ��Ʈ�� Ʈ�� ����
						dataTree.setRoot(rootNode);
						dataTree.setData(returnNode);

						setData(dataTree);
						refreshFields();

						// �����Ӽ��� ������ ������ ����� ������ ��ȸ�Ѵ�
						// �ش� ������ �����Ѵ�.
						if (setCurrentFolderYn) {
							rootNode = getTree().find(id, Long.toString(rootId));
							TreeNode[] children = getTree().getChildren(rootNode);

							// 20130822, junsoo, ��尡 ���� ��찡 �����Ƿ�..
							if (children != null && children.length > 0) {
								// xvarm�ΰ��
								if (treeType == 2) {
									ServiceUtil.folder().getFolder(Session.get().getSid(),children[0].getAttribute(name),SFolder.TYPE_ECM, false, true,
											new AsyncCallback<SFolder>() {
												@Override
												public void onSuccess(
														SFolder result) {
													// ������ ��ȸ�Ѵ�.
													result.setPathExtended(getPath(result.getName()));
													Session.get().setCurrentFolder(result);
													// ������ �����Ѵ�.
													TreeNode selectNode = getTree().find(result.getName());
													selectRecord(selectNode);
													
												}

												@Override
												public void onFailure(Throwable caught) {
													SCM.warn(caught);
												}
											});

								} else {
									ServiceUtil.folder().getFolder(Session.get().getSid(), Long.parseLong(children[0].getAttribute(id)), false, true,
											new AsyncCallback<SFolder>() {
												@Override
												public void onFailure(Throwable caught) {
													SCM.warn(caught);
												}

												@Override
												public void onSuccess(SFolder folder) {
													// ������ ��ȸ�Ѵ�.
													folder.setPathExtended(getPath(folder.getId()));
													Session.get().setCurrentFolder(folder);
													// ������ �����Ѵ�.
													TreeNode selectNode = getTree().findById(String.valueOf(folder.getId()));
													selectRecord(selectNode);
												}
											});
									// 20150602, soeun, home folder�� space ȹ��
									ServiceUtil.folder().getFolder(Session.get().getSid(), Session.get().getHomeFolderId(), false, true,
											new AsyncCallback<SFolder>() {
												@Override
												public void onFailure(Throwable caught) {
													SCM.warn(caught);
												}

												@Override
												public void onSuccess(SFolder folder) {
													TreeNode selectNode = getTree().findById(String.valueOf(Session.get().getHomeFolderId()));
													if(Session.get().getHomeFolderId()!=4) {
														long uSpace = (long) (folder.getUsedSpace()*0.00000000093132);
														long tSpace = (long) (folder.getTotalSpace()*0.00000000093132);
														selectNode.setAttribute("name", "Home <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>");														
													} else {
														if(selectNode != null)
															selectNode.setAttribute("name", "Home");
													}

													refreshFields();
												}
											});

								}
							}
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});
		
	}
	
	/**
	 * ������ Ȯ�� ���� ���θ� �缳�����ش�.
	 */
	private void setIsFolrderInfo(){
		int i=0;
		if(isFolderInfos.size() == 0) return;
		for (ListGridRecord record : this.getRecords()) {
			record.setAttribute("isFolder", isFolderInfos.get(i));
			i++;
		}
	}
	/*
	 * Ư�������� ���� ������ �������� getFolderDataRpcChild(Ʈ��Ÿ��, Ÿ�پ��̵�, ������ ������ Ʈ�����, �������̵�,
	 * ������, �θ��������̵�, ����Ÿ��)
	 */
	public void getFolderDataRpcChild(final int treeType, final Long folderId,
			final TreeNode selectNode, final String id, final String name,
			final String parent, final String type) {

		ServiceUtil.folder().listFolderByTypeAndParentId(Session.get().getSid(), treeType,
				folderId, new AsyncCallback<List<SFolder>>() {
					@Override
					public void onSuccess(List<SFolder> result) {
						SFolder[] sfolder = new SFolder[result.size()];

						TreeNode[] returnNode = new TreeNode[result.size()];
						for (int i = 0; i < result.size(); i++) {
							sfolder[i] = result.get(i);
							returnNode[i] = new TreeNode();
							returnNode[i].setAttribute(id, sfolder[i].getId());
							// soeun �����뷮/�ִ��뷮 ǥ��
							if(sfolder[i].getUsedSpace() != null) {
								long uSpace = (long) (sfolder[i].getUsedSpace()*0.00000000093132);
								long tSpace = (long) (sfolder[i].getTotalSpace()*0.00000000093132);
								returnNode[i].setAttribute(name,sfolder[i].getName()+" <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>");
							} else {
								returnNode[i].setAttribute(name,sfolder[i].getName());
							}
							returnNode[i].setAttribute(parent,sfolder[i].getParentId());
							returnNode[i].setAttribute(type,sfolder[i].getType());

							if (sfolder[i].isParentOrNot()) {
								returnNode[i].setAttribute("expand", true);
								returnNode[i].setIsFolder(true);
							} else {
								returnNode[i].setAttribute("expand", false);
								returnNode[i].setIsFolder(false);
							}
//							returnNode[i].setIsFolder(true);
							// Ʈ���� ����
							if (returnNode[i] != null)
								getTree().add(returnNode[i], selectNode);
						}

						refreshFields();
					}

					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});

	}

	// ���� ���̵�� �н� ��������
	public String getPath(long folderId) {
		try {
			TreeNode selectedNode = getTree().find("folderId", Long.toString(folderId));
			String path = "";
			TreeNode[] parents = getTree().getParents(selectedNode);
			for (int i = parents.length - 1; i >= 0; i--) {
				if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
					path += "/" + parents[i].getName();
			}
			path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());

			return path;
		} catch (Exception ex) {
			SC.warn(ex.getMessage());
		}

		return "";
	}

	// ���� �̸����� �н� ��������
	public String getPath(String folderName) {
		try {
			TreeNode selectedNode = getTree().find("name", folderName);
			String path = "";
			TreeNode[] parents = getTree().getParents(selectedNode);
			for (int i = parents.length - 1; i >= 0; i--) {
				if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
					path += "/" + parents[i].getName();
			}
			path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
			return path;
		} catch (Exception ex) {
			SC.warn(ex.getMessage());
		}

		return "";
	}

	public List getIsFolderInfos() {
		return isFolderInfos;
	}

	public void setIsFolderInfos(List isFolderInfos) {
		this.isFolderInfos = isFolderInfos;
	}

	@Override
	public void onFolderSelected(SFolder folder) {
	}

	@Override
	public void onFolderSaved(SFolder folder) {
	}

	@Override
	public void onFolderReload() {
	}

}