package com.speno.xedm.gui.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.speno.xedm.gui.common.client.serials.SGroup;
import com.speno.xedm.gui.common.client.serials.SInfo;
import com.speno.xedm.gui.common.client.serials.SRight;
import com.speno.xedm.gui.common.client.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.serials.SSession;
import com.speno.xedm.gui.common.client.serials.SUser;
import com.speno.xedm.gui.common.client.services.SecurityService;
import com.speno.xedm.gui.common.client.util.PagingConfig;
import com.speno.xedm.gui.common.client.util.PagingResult;
import com.speno.xedm.util.GeneralException;

/**
 * DAO 없는 mock
 * 
 * @author deluxjun
 *
 */
public class MockSecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

//	@Override
	public SSession login(String username, String password, String locale) {
		
		if(locale == null || "".equals(locale))
			locale = "en";

		SSession session = new SSession();
		SInfo info = new MockInfoServiceImpl().getInfo(locale);
		session.setInfo(info);

		session.setLoggedIn(false);
		if ("admin".equals(username)) {
			SUser user = new SUser();
			user.setLanguage(locale);
			session.setUser(user);
			user.setUserName(username);
			session.setSid("sid" + new Date().getTime());

			SGroup group = new SGroup();
			group.setId(1);
			group.setName("admin");
			group.setDescription("Administrators");
			user.setGroups(new SGroup[] { group });
			user.setName("Bae");
			user.setEmail("jsbae@speno.co.kr");
			user.setExpired(false);
			user.setPasswordMinLenght(8);
			user.setLockedDocs(5);
			user.setCheckedOutDocs(1);
			session.setLoggedIn(true);

			Long[] menues = new Long[1000];
			for (int i = 0; i < 1000; i++) {
				menues[i] = (long) i - 100;
			}

			return session;
		} else if ("author".equals(username)) {
			SUser user = new SUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			user.setLanguage(locale);
			session.setUser(user);
			session.setLoggedIn(false);
			return session;
		} else {
			session.setLoggedIn(false);
			return session;
		}
	}

//	@Override
	public SSession login(String sid) {
		return login("admin", null, null);
	}

//	@Override
	public void logout(String sid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill(String sid) throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int changePassword(long userId, String oldPassword,
			String newPassword) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
<<<<<<< .mine
	public void deleteUser(String sid, long userId)
			throws GeneralException {
=======
	public void deleteUser(String sid, long[] userId)
			throws GeneralException {
>>>>>>> .r402
		// TODO Auto-generated method stub
		
	}

	@Override
	public SUser saveUser(String sid, SUser user)
			throws GeneralException {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public SUser getUser(String sid, long userId)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SGroup getGroup(String sid, long groupId)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SGroup saveGroup(String sid, SGroup group)
			throws GeneralException {
		// TODO Auto-generated method stub
		return group;
	}

	@Override
	public void deleteGroup(String sid, long groupId)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] userIds)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
<<<<<<< .mine
	public void addUserToGroup(String sid, long groupId, long userId)
			throws GeneralException {
=======
	public void addUsersToGroup(String sid, long groupId, long[] userIds)
			throws GeneralException {
>>>>>>> .r402
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPassword(String username, String emailAddress,
			String productName) throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
<<<<<<< .mine
	public SUser[] searchUsers(String sid, String username, long groupId)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
=======
	public List<SUser> listUsersByUserNameAndGroupId(String sid, String username, long groupId) 
			throws GeneralException {
		System.out.println("********* MockSecurityServiceImpl listUsersByUserNameAndGroupId Thomas ********* ");
		
		List<SUser> result = new ArrayList();
		
		for(int i=0; i<9; i++) {
			SUser user = new SUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			user.setLanguage(null);
			
			user.setId((long)i);
			user.setUserName("id_"+i);
			user.setName("Thomas_"+i);
			
			result.add(user);
		}
		return result;
>>>>>>> .r402
	}
	
	@Override
	public List<SUser> listUsersByNameAndGroupId(String sid, String name, long groupId) 
			throws GeneralException {
		System.out.println("********* MockSecurityServiceImpl listUsersByNameAndGroupId Thomas ********* ");
		
		List<SUser> result = new ArrayList();
		
		for(int i=0; i<9; i++) {
			SUser user = new SUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			user.setLanguage(null);
			
			user.setId((long)i);
			user.setUserName("id_"+i);
			user.setName("Thomas_"+i);
			
			result.add(user);
		}
		return result;
	}
	
	/* thomas test added--------------------------------------------------
	@Override
	public SSecurityProfile[] getSecurityProfileList(String sid)
			throws GeneralException {
		
		System.out.println("********* MockSecurityServiceImpl getSecurityProfileList ********* ");
		
		SRight[] sRightArry = new SRight[9];
		for(int i=0; i<sRightArry.length; i++) {
			
			//현재 SRight가 Duty인지 Position인지 Group인지 User인지 구분할 수 있는 값이 없음
			//일단 Lable을 그 구분자 역할로 사용하기로 함.
			if( i < 3 ) {
				sRightArry[i].setLabel("DUTY");
			}
			else if( i == 3 ) {
				sRightArry[i].setLabel("POSITION");
			}
			else if( i == 4 ) {
				sRightArry[i].setLabel("GROUP");
			}
			else if( i > 4 ) {
				sRightArry[i].setLabel("USER");
			}
			
			sRightArry[i] = new SRight();
			sRightArry[i].setEntityId(i); //duty id, position id .. id값
			sRightArry[i].setName("test name_"+i);
			sRightArry[i].setView(true);
			sRightArry[i].setDownload(true);
		}
		
		SSecurityProfile[] profArry = new SSecurityProfile[3];
		for(int i=0; i<profArry.length; i++) {
			profArry[i] = new SSecurityProfile();
			profArry[i].setId(i);
			profArry[i].setName("Test_Profile_Name_"+i);
			profArry[i].setDescription("test_description_"+i);
			profArry[i].setRights(sRightArry);
		}
		return profArry;
	}
	//thomas test added--------------------------------------------------*/

	@Override
	public SSecurityProfile getSecurityProfile(String sid, long profileId)
<<<<<<< .mine
			throws GeneralException {
=======
			throws GeneralException {
		
		System.out.println("********* MockSecurityServiceImpl getSecurityProfile ********* ");
		
>>>>>>> .r402
		// TODO Auto-generated method stub
		SRight[] sRightArry = new SRight[9];
		for(int i=0; i<sRightArry.length; i++) {
			
			//현재 SRight가 Duty인지 Position인지 Group인지 User인지 구분할 수 있는 값이 없음
			//일단 Lable을 그 구분자 역할로 사용하기로 함.
			if( i < 3 ) {
				sRightArry[i].setLabel("DUTY");
			}
			else if( i == 3 ) {
				sRightArry[i].setLabel("POSITION");
			}
			else if( i == 4 ) {
				sRightArry[i].setLabel("GROUP");
			}
			else if( i > 4 ) {
				sRightArry[i].setLabel("USER");
			}
			
			sRightArry[i] = new SRight();
			sRightArry[i].setEntityId(i); //duty id, position id .. id값
			sRightArry[i].setName("this name created by profileId["+i+"]");
			sRightArry[i].setView(true);
			sRightArry[i].setDownload(true);
		}
		
		
		SSecurityProfile temp = new SSecurityProfile();
		temp.setId(123456);
		temp.setName("TECHNICAL");
		temp.setDescription("test_script_test_scipt");
		temp.setRights(sRightArry);
		return temp;
	}

	@Override
	public SSecurityProfile saveSecurityProfile(String sid,
			SSecurityProfile profile) throws GeneralException {
		// TODO Auto-generated method stub
		return profile;
	}

	@Override
	public void deleteSecurityProfile(String sid, long profileId)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applySecurityProfileRights(String sid, SSecurityProfile profile)
			throws GeneralException {
		// TODO Auto-generated method stub
		
	}
	
// ==================================================
// paging

	@Override
	public PagingResult<SUser> pagingUsers(String username, PagingConfig config) {
		
		SGroup sDuty = new SGroup();
		sDuty.setId(1L);
		sDuty.setName("Duty_Name_1");
		sDuty.setType(SGroup.TYPE_DUTY);
		sDuty.setDescription("Duty_Description_1");
		
		SGroup sPosition = new SGroup();
		sPosition.setId(1L);
		sPosition.setName("Pos_Name_1");
		sPosition.setType(SGroup.TYPE_POSITION);
		sPosition.setDescription("Pos_Description_1");
		
		SGroup sGroup = new SGroup();
		sGroup.setId(0L);
		sGroup.setParentId(-100L);
		sGroup.setName("first");
		sGroup.setType(SGroup.TYPE_GROUP);
		sGroup.setDescription("first group7");
		sGroup.setPath("aaa > bbb > ccc");
		sGroup.setIDPath("0");
		
		SGroup sGroup2 = new SGroup();
		sGroup2.setId(1L);
		sGroup.setParentId(0L);
		sGroup2.setName("second");
		sGroup2.setType(SGroup.TYPE_GROUP);
		sGroup2.setDescription("second group");
		sGroup2.setPath("ppp > xxx > jjj");
		sGroup2.setIDPath("0>1");
		
		SGroup[] groups = new SGroup[2];
		groups[0] = sGroup;
		groups[1] = sGroup2;
		
		
		//-------------------------------------------------
		
		int totalLength = config.getTotalLength();
		
		System.out.println("start=" + config.getOffset() + ", max=" + config.getLimit() + ", total=" + config.getTotalLength());

		if (totalLength < 1)
			totalLength = 200;
		ArrayList<SUser> sublist = new ArrayList<SUser>();
		for (int i = 0; i < config.getLimit(); i++) {
			SUser user = new SUser();
			long index = i + config.getOffset();
			user.setId(index);
		    user.setUserName("deluxjun" + index);
		    user.setName("deluxjun" + index);
		    user.setGroups(groups);
			sublist.add(user);
		}
		
		System.out.println("start=" + config.getOffset() + ", max=" + sublist.size() + ", total=" + totalLength);
		
		return new PagingResult<SUser>(sublist, totalLength, config.getOffset());
	}

	@Override
	public PagingResult<SUser> pagingUsersInGroupId(String sid,
			String username, long groupId, PagingConfig config) {
		// TODO Auto-generated method stub
		int totalLength = config.getTotalLength();
		
		System.out.println("start=" + config.getOffset() + ", max=" + config.getLimit() + ", total=" + config.getTotalLength());

		if (totalLength < 1)
			totalLength = 200;
		ArrayList<SUser> sublist = new ArrayList<SUser>();
		for (int i = 0; i < config.getLimit(); i++) {
			SUser user = new SUser();
			int index = i + config.getOffset();
			  user.setId((long)index);
		      user.setUserName("deluxjun" + index);
		      user.setName("deluxjun" + index);		  
			sublist.add(user);
		}
		
		System.out.println("start=" + config.getOffset() + ", max=" + sublist.size() + ", total=" + totalLength);
		
		return new PagingResult<SUser>(sublist, totalLength, config.getOffset());
	}

	@Override
	public SUser saveUserProfile(String sid, SUser user)
			throws GeneralException {
		// TODO Auto-generated method stub
		
		GWT.log("[MockSecurityServiceImpl.saveUserProfile] sid["+sid+"] id["+user.getUserName()+"], name["+user.getName()+"]");
		
		return user;
	}

	@Override
	public List<SGroup> listGroupByParentId(String sid, long parentId)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
<<<<<<< .mine
	public SUser saveUserProfile(String sid, SUser user)
			throws GeneralException {
=======
	public List<String> listNameOfAllSecurityProfile(String sid)
			throws GeneralException {
>>>>>>> .r402
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagingResult<SUser> pagingUsersByName(String name, PagingConfig config) {
		
		GWT.log("[MockSecurityServiceImpl.pagingUsersByName] name["+name+"]");
		
		SGroup sDuty = new SGroup();
		sDuty.setId(1L);
		sDuty.setName("Duty_Name_1");
		sDuty.setType(SGroup.TYPE_DUTY);
		sDuty.setDescription("Duty_Description_1");
		
		SGroup sPosition = new SGroup();
		sPosition.setId(1L);
		sPosition.setName("Pos_Name_1");
		sPosition.setType(SGroup.TYPE_POSITION);
		sPosition.setDescription("Pos_Description_1");
		
		SGroup sGroup = new SGroup();
		sGroup.setId(0L);
		sGroup.setParentId(-100L);
		sGroup.setName("first");
		sGroup.setType(SGroup.TYPE_GROUP);
		sGroup.setDescription("first group7");
		sGroup.setPath("aaa > bbb > ccc");
		sGroup.setIDPath("0");
		
		SGroup sGroup2 = new SGroup();
		sGroup2.setId(1L);
		sGroup.setParentId(0L);
		sGroup2.setName("second");
		sGroup2.setType(SGroup.TYPE_GROUP);
		sGroup2.setDescription("second group");
		sGroup2.setPath("ppp > xxx > jjj");
		sGroup2.setIDPath("0>1");
		
		SGroup[] groups = new SGroup[2];
		groups[0] = sGroup;
		groups[1] = sGroup2;
		
		
		//-------------------------------------------------
		
		int totalLength = config.getTotalLength();

		if (totalLength < 1)
			totalLength = 200;
		ArrayList<SUser> sublist = new ArrayList<SUser>();
		for (int i = 0; i < config.getLimit(); i++) {
			SUser user = new SUser();
			long index = i + config.getOffset();
			user.setId(index);
		    user.setUserName("username(eq. userid)" + index);
		    user.setName("name(eq. username)" + index);
		    user.setDescription("test description");
		    user.setCreationDate(new Date());
		    user.setGroups(groups);
		    user.setDuty(sDuty);
		    user.setPosition(sPosition);
			sublist.add(user);
		}
		
		return new PagingResult<SUser>(sublist, totalLength, config.getOffset());
	}

	@Override
	public SGroup getGroup(String sid, long groupId, boolean computePath)
			throws GeneralException {
		// TODO Auto-generated method stub
		return null;
	}



}
