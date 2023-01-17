package com.speno.xedm.gui.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.speno.xedm.gui.common.client.Constants;
<<<<<<< .mine
=======
import com.speno.xedm.gui.common.client.serials.SDocType;
import com.speno.xedm.gui.common.client.serials.SFileType;
>>>>>>> .r402
import com.speno.xedm.gui.common.client.serials.SFolder;
import com.speno.xedm.gui.common.client.serials.SValuePair;
import com.speno.xedm.gui.common.client.services.FolderService;
import com.speno.xedm.util.GeneralException;

public class MockFolderServiceImpl extends RemoteServiceServlet implements FolderService {

	private static final long serialVersionUID = 1L;

	@Override
	public SFolder getFolder(String sid, String folderId, int type,
			boolean computePath, boolean getPermissions)
			throws GeneralException {

	SFolder folder = new SFolder();
		if(folderId.equals("XVARM_MAIN_RB")) folder.setId(0);
		folder.setName(folderId);
		
		if (computePath) {
			SFolder[] path = new SFolder[] { getFolder(sid, 5, false, true), getFolder(sid, 1007, false, true),
					getFolder(sid, 2009, false, true) };
			folder.setPath(path);
		}
		
		folder.setPermissions(new String[] { "read", "write", "add", "security", "delete", "rename",
				"import", "export", "sign", "archive", "workflow", "immutability" });
		
		return folder;
	}
	
	@Override
	public SFolder getFolder(String sid, long folderId, boolean computePath, boolean permision) {
		SFolder folder = new SFolder();
		folder.setId(folderId);
		folder.setName(folderId != Constants.DOCUMENTS_FOLDERID ? "Folder " + folderId : "/");

		if (computePath) {
			SFolder[] path = new SFolder[] { getFolder(sid, 5, false, true), getFolder(sid, 1007, false, true),
					getFolder(sid, 2009, false, true) };
			folder.setPath(path);
		}

		if (folderId % 2 == 0 || folderId == Constants.DOCUMENTS_FOLDERID)
			folder.setPermissions(new String[] { "read", "write", "add", "security", "delete", "rename",
					"import", "export", "sign", "archive", "workflow", "immutability" });
		else
			folder.setPermissions(new String[] { "read" });
		return folder;
	}

	@Override
	public SFolder save(String sid, SFolder folder)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(String sid, long folderId, String name)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyRights(String sid, SFolder folder, boolean subfolders)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String sid, long folderId)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paste(String sid, long[] docIds, long folderId, String action)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDocTypesToFolder(String sid, long[] docTypes, long folderId)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDocTypesFromFolder(String sid, long[] docTypeIds,
			long folderId) throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFileTypesToFolder(String sid, long[] typeIds, long folderId)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFileTypesFromFolder(String sid, long[] typeIds,
			long folderId) throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SFileType[] listFileTypesInFolder(String sid, String likeName,
			long folderId) throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SDocType[] listDocTypesInFolder(String sid, String likeName,
			long folderId) throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SFolder> listFolderByTypeAndParentId(String sid, int type,
			long parentId) throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}



}