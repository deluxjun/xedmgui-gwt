package com.speno.xedm.gui.common.client.util;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRetentionProfile;
import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;
import com.speno.xedm.gwt.service.DocumentService;
import com.speno.xedm.gwt.service.DocumentServiceAsync;
import com.speno.xedm.gwt.service.FolderService;
import com.speno.xedm.gwt.service.FolderServiceAsync;
import com.speno.xedm.gwt.service.InfoService;
import com.speno.xedm.gwt.service.InfoServiceAsync;
import com.speno.xedm.gwt.service.RewriteService;
import com.speno.xedm.gwt.service.RewriteServiceAsync;
import com.speno.xedm.gwt.service.SearchService;
import com.speno.xedm.gwt.service.SearchServiceAsync;
import com.speno.xedm.gwt.service.SecurityService;
import com.speno.xedm.gwt.service.SecurityServiceAsync;
import com.speno.xedm.gwt.service.SystemService;
import com.speno.xedm.gwt.service.SystemServiceAsync;
import com.speno.xedm.gwt.service.TemplateService;
import com.speno.xedm.gwt.service.TemplateServiceAsync;

public class ServiceUtil {

	/** The session service. */
	private static InfoServiceAsync infoService = null;
	private static SecurityServiceAsync securityService = null;
	private static DocumentServiceAsync documentService = null;
	private static FolderServiceAsync folderService = null;
	private static SearchServiceAsync searchService = null;
	private static SystemServiceAsync systemService = null;
	private static DocumentCodeServiceAsync documentCodeService = null;
	private static TemplateServiceAsync templateService = null;
	private static RewriteServiceAsync rewriteService = null;

	public static InfoServiceAsync info() {
		if (infoService == null) {
			infoService = (InfoServiceAsync) GWT.create(InfoService.class);
		}
		return infoService;
	}

	public static SecurityServiceAsync security() {
		if (securityService == null) {
			securityService = (SecurityServiceAsync) GWT
					.create(SecurityService.class);
		}
		return securityService;
	}

	public static DocumentServiceAsync document() {
		if (documentService == null) {
			documentService = (DocumentServiceAsync) GWT
					.create(DocumentService.class);
		}
		return documentService;
	}

	public static FolderServiceAsync folder() {
		if (folderService == null) {
			folderService = (FolderServiceAsync) GWT
					.create(FolderService.class);
		}
		return folderService;
	}

	public static SearchServiceAsync search() {
		if (searchService == null) {
			searchService = (SearchServiceAsync) GWT
					.create(SearchService.class);
		}
		return searchService;
	}

	public static SystemServiceAsync system() {
		if (systemService == null) {
			systemService = (SystemServiceAsync) GWT
					.create(SystemService.class);
		}
		return systemService;
	}

	public static DocumentCodeServiceAsync documentcode() {
		if (documentCodeService == null) {
			documentCodeService = (DocumentCodeServiceAsync) GWT
					.create(DocumentCodeService.class);
		}
		return documentCodeService;
	}

	public static TemplateServiceAsync template() {
		if (templateService == null) {
			templateService = (TemplateServiceAsync) GWT
					.create(TemplateService.class);
		}
		return templateService;
	}

	public static RewriteServiceAsync rewrite() {
		if (rewriteService == null) {
			rewriteService = (RewriteServiceAsync) GWT
					.create(RewriteService.class);
		}
		return rewriteService;
	}

	// =======================================================================
	// ���� ���� �Լ��� ����
	// =======================================================================

	/**
	 * 20130806, junsoo, ��밡���� doctype ����
	 * 
	 * @param folder
	 * @param callback
	 */
	// public static void getAvailableDocTypes(SFolder folder, final
	// ReturnHandler<LinkedHashMap<String, String>> callback) {
	// // TODO: reload folder�� �ش��ϴ� document type, file type�� ���ε� (����������)
	// if (folder != null && folder.getType() == Constants.FOLDER_TYPE_SHARED) {
	// folder().listDocTypesInFolder(Session.get().getSid(), "", folder.getId(),
	// new AsyncCallback<SDocType[]>() {
	// @Override
	// public void onSuccess(SDocType[] result) {
	// // ����������, ����� ���� ���� ��� ������ ������.
	// if (result == null || result.length < 1) {
	// getAvailableDocTypes(null, callback);
	// return;
	// }
	//
	// LinkedHashMap<String, String> doctype = new LinkedHashMap<String,
	// String>();
	// for (int i = 0; i < result.length; i++){
	// String key = result[i].getId() + "!" + result[i].getIndexId() + "!" +
	// result[i].getRetentionId();
	// doctype.put(key, result[i].getName());
	// }
	// callback.onReturn(doctype);
	// }
	// @Override
	// public void onFailure(Throwable caught) {
	// Log.serverError(caught, false);
	// }
	// });
	// }
	// // ��ü ȹ��
	// else {
	// LinkedHashMap<String, String> cached = (LinkedHashMap<String,
	// String>)DataCache.get(DataCache.DOCTYPES);
	// if (cached == null) {
	// final LinkedHashMap<String, String> doctype = new LinkedHashMap<String,
	// String>();
	//
	// DataSource ds = new DocumentCodeDS(DocumentCodeDS.TYPE_DOC);
	// ds.fetchData(null, new DSCallback() {
	// @Override
	// public void execute(DSResponse response, Object rawData, DSRequest
	// request) {
	// RecordList rcList = response.getDataAsRecordList();
	// Record[] records = new Record[ rcList.getLength() ];
	//
	// for (int i = 0; i < records.length; i++){
	// records[i] = rcList.get(i);
	// doctype.put(records[i].getAttribute("id").toString() + "!" +
	// records[i].getAttribute("indexid").toString() + "!" +
	// records[i].getAttribute("retentionId").toString(),
	// records[i].getAttribute("name").toString());
	// }
	// DataCache.put(DataCache.DOCTYPES, doctype);
	//
	// callback.onReturn(doctype);
	// }
	// });
	// } else {
	// callback.onReturn(cached);
	// }
	//
	// }
	// }

	/**
	 * ���� �������� ��� ������ �������ĵ��� ����, ��ü �������ĸ� ĳ����.
	 * 
	 * @param folder
	 * @param callback
	 */
	public static void getAvailableSDocTypes(SFolder folder,
			final ReturnHandler<SDocType[]> callback) {
		// TODO: reload folder�� �ش��ϴ� document type, file type�� ���ε� (����������)
		if (folder != null && folder.getType() == Constants.FOLDER_TYPE_SHARED) {
			folder().listDocTypesInFolder(Session.get().getSid(), "",
					folder.getId(), new AsyncCallback<SDocType[]>() {
						@Override
						public void onSuccess(SDocType[] result) {
							// ����������, ����� ���� ���� ��� ������ ������.
							if (result == null || result.length < 1) {
								getAvailableSDocTypes(null, callback);
								return;
							}

							callback.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		}
		// ��ü ȹ��
		else {
			SDocType[] cached = (SDocType[]) DataCache.get(DataCache.DOCTYPES);
			if (cached == null) {
				documentcode().listDocTypeLikeName(Session.get().getSid(), "",
						false, new AsyncCallback<List<SDocType>>() {
							@Override
							public void onSuccess(List<SDocType> result) {
								if (result == null || result.size() < 1) {
									return;
								}
								SDocType[] doctypes = result
										.toArray(new SDocType[0]);
								DataCache.put(DataCache.DOCTYPES, doctypes);
								callback.onReturn(doctypes);
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, false);
							}
						});
			} else {
				callback.onReturn(cached);
			}

		}
	}

	/**
	 * ���� �������� ��� ������ �������ĵ��� ����, ��ü �������ĸ� ĳ����.
	 * 
	 * @param folder
	 * @param callback
	 */
	public static void getAvailableSFileTypes(SFolder folder,
			final ReturnHandler<SFileType[]> callback) {
		// TODO: reload folder�� �ش��ϴ� document type, file type�� ���ε� (����������)
		if (folder != null && folder.getType() == Constants.FOLDER_TYPE_SHARED) {
			folder().listFileTypesInFolder(Session.get().getSid(), "",
					folder.getId(), new AsyncCallback<SFileType[]>() {
						@Override
						public void onSuccess(SFileType[] result) {
							// ����������, ����� ���� ���� ��� ������ ������.
							if (result == null || result.length < 1) {
								getAvailableSFileTypes(null, callback);
								return;
							}

							callback.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		}
		// ��ü ȹ��
		else {
			SFileType[] cached = (SFileType[]) DataCache
					.get(DataCache.FILETYPES);
			if (cached == null) {
				documentcode().listFileTypeLikeName(Session.get().getSid(), "",
						new AsyncCallback<List<SFileType>>() {
							@Override
							public void onSuccess(List<SFileType> result) {
								// ����������, ����� ���� ���� ��� ������ ������.
								if (result == null || result.size() < 1) {
									return;
								}

								SFileType[] filetypes = result
										.toArray(new SFileType[0]);
								DataCache.put(DataCache.FILETYPES, filetypes);
								callback.onReturn(filetypes);
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, false);
							}
						});
			} else {
				callback.onReturn(cached);
			}

		}
	}

	/**
	 * retention ����Ʈ ȹ��
	 */
	public static void getAllRetentions(
			final ReturnHandler<List<SRetentionProfile>> handler) {
		List<SRetentionProfile> cached = (List<SRetentionProfile>) DataCache
				.get(DataCache.RETENTIONS);
		if (cached == null) {
			documentcode().listRetentionProfilesLikeName(
					Session.get().getSid(), "",
					new AsyncCallback<List<SRetentionProfile>>() {
						@Override
						public void onSuccess(List<SRetentionProfile> result) {
							// save to cache
							DataCache.put(DataCache.RETENTIONS, result);

							handler.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	/**
	 * get all template list
	 * 
	 * @param selectItem
	 */
	public static void getAllTemplates(final ReturnHandler<STemplate[]> handler) {
		STemplate[] cached = (STemplate[]) DataCache.get(DataCache.TEMPLATES
				.getId());
		// LinkedHashMap<String, String> cached = (LinkedHashMap<String,
		// String>)DataCache.get(DataCache.TEMPLATES);
		if (cached == null) {
			// final LinkedHashMap<String, String> templates = new
			// LinkedHashMap<String, String>();
			template().getTemplates(Session.get().getSid(),
					new AsyncCallback<STemplate[]>() {
						@Override
						public void onSuccess(STemplate[] result) {
							DataCache.put(DataCache.TEMPLATES, result);
							handler.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	/**
	 * get template's attribute
	 * 
	 * @param templateId
	 */
	public static void getExtendedAttributes(final Long templateId,
			final ReturnHandler<SExtendedAttribute[]> handler) {
		if (templateId == null || templateId.longValue() < 1L)
			return;
		SExtendedAttribute[] cached = (SExtendedAttribute[]) DataCache
				.get(DataCache.TEMPLATE_ATTRS.getId() + templateId.toString());
		if (cached == null) {
			document().getAttributes(Session.get().getSid(), templateId,
					new AsyncCallback<SExtendedAttribute[]>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, true);
						}

						@Override
						public void onSuccess(SExtendedAttribute[] result) {
							if (result == null)
								return;
							// 20140217, junsoo, ���ø�(Ȯ��Ӽ�)�� �Ź� ������ ���� ���������� ������.
							// DataCache.put(DataCache.TEMPLATE_ATTRS.getId() +
							// templateId.toString(), result);
							handler.onReturn(result);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	public static void getECMIndexFields(String indexId,
			final ReturnHandler<List<String>> handler) {
		List<String> cached = (List<String>) DataCache
				.get(DataCache.ECM_INDEX_FIELDS.getId() + indexId);
		if (cached == null) {
			documentcode().listXvarmIndexFields(Session.get().getSid(),
					indexId, new AsyncCallback<List<String>>() {
						@Override
						public void onSuccess(List<String> result) {
							DataCache.put(DataCache.ECM_INDEX_FIELDS.getId(),
									result);
							handler.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	/**
	 * Document�� ��� �ڵ� ȹ��
	 * 
	 * @param code
	 * @param handler
	 */
	public static void getDocumentCodes(final String code,
			final ReturnHandler<List<SCode>> handler) {
		List<SCode> cached = (List<SCode>) DataCache
				.get(DataCache.DOCUMENT_CODES.getId() + code);
		if (cached == null) {
			documentcode().listCodes(Session.get().getSid(), code,
					new AsyncCallback<List<SCode>>() {
						@Override
						public void onSuccess(List<SCode> result) {
							DataCache.put(DataCache.DOCUMENT_CODES.getId()
									+ code, result);
							handler.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	/**
	 * ��� ���� Profile ȹ��
	 * 
	 * @param handler
	 */
	public static void getAllSecurityProfile(
			final ReturnHandler<List<SSecurityProfile>> handler) {
		List<SSecurityProfile> cached = (List<SSecurityProfile>) DataCache
				.get(DataCache.DOCUMENT_SECURITY_CODES.getId());
		if (cached == null) {
			documentcode().listSecurityProfileLikeName(Session.get().getSid(),
					"", new AsyncCallback<List<SSecurityProfile>>() {
						@Override
						public void onSuccess(List<SSecurityProfile> result) {
							DataCache.put(
									DataCache.DOCUMENT_SECURITY_CODES.getId(),
									result);
							handler.onReturn(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
		} else {
			handler.onReturn(cached);
		}
	}

	/**
	 * ��� �׷� Ÿ�� ȹ��
	 * 
	 * @param handler
	 */
	public static void getAllGroupType(
			final ReturnHandler<LinkedHashMap<Integer, String>> handler) {
		LinkedHashMap<Integer, String> cached = (LinkedHashMap<Integer, String>) DataCache
				.get(DataCache.GROUP_TYPE.getId());
		if (cached == null) {
			LinkedHashMap<Integer, String> opts = new LinkedHashMap<Integer, String>();
			opts.put(SRight.GROUPTYPE_DUTY, "DUTY");
			opts.put(SRight.GROUPTYPE_POSITION, "POSITION");
			opts.put(SRight.GROUPTYPE_GROUP, "GROUP");
			opts.put(SRight.GROUPTYPE_USERGROUP, "USER");

			DataCache.put(DataCache.GROUP_TYPE.getId(), opts);
			handler.onReturn(opts);
		} else {
			handler.onReturn(cached);
		}
	}

}
