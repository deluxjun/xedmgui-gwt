package com.speno.xedm.gui.common.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;

/**
 * 
 * @author deluxjun
 *
 */
public class AdminMenuUtil {
	private static AdminMenuUtil instance;

	private HashMap<Long, List<SAdminMenu>> caches = new HashMap<Long, List<SAdminMenu>>();

	private Map<String,Long> ids = new HashMap<String,Long>(); 

	private AdminMenuUtil() {
	}

	public static AdminMenuUtil get() {
		if (instance == null)
			instance = new AdminMenuUtil();
		return instance;
	}
	

	public long getMenuId(String menuName) {
		Long id = ids.get(menuName);
		if (id == null)
			return -1L;
		return id.longValue();
	}
	
	/**
	 * 권한 체크하고, 서버로 부터 리턴된 정보는 cache 처리함.
	 * 
	 * @param parentId	부모 ID
	 * @param menuName	권한 조사할 menu 명
	 * @return	요청한 메뉴의 ID. 권한이 없을 경우 null 리턴
	 */
	public void hasPriv(final String finalCallbackId, final long parentId, final String menuName, final AsyncCallback<Long> callback){
		Session.get().addActionCount(finalCallbackId);

		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
		    callback.onSuccess(0L);
			Session.get().reduceActionCount(finalCallbackId);
		    return;
		}
		
		if (parentId == -1L) {
			Session.get().reduceActionCount(finalCallbackId);
			return;
		}

		List<SAdminMenu> menus = caches.get(parentId);
		if (menus == null) {

			ServiceUtil.security().findByUserIdAndParentId(Session.get().getSid(), parentId, new AsyncCallback<List<SAdminMenu>>() {
				@Override
				public void onSuccess(List<SAdminMenu> result) {
					if(result.size() == 0) {
						callback.onSuccess(null);
						return;
					}
					
					Long menuId = null;
					if (result != null) {
						for (SAdminMenu menu : result) {
							if (menuName.equals(menu.getTitle())) {
								menuId = menu.getId();
							    break;
							}
						}
						caches.put(parentId, result);
					}
					
					// put id
					if (menuId != null) {
						ids.put(menuName, menuId);
					}
					
					callback.onSuccess(menuId);
					Session.get().reduceActionCount(finalCallbackId);
					Log.debug("[AdminMenuUtil] onSuccess : " + menuId);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(I18N.message("second.client.cannotGetMenus"), caught, true);
				    callback.onFailure(caught);
					Session.get().reduceActionCount(finalCallbackId);
				}
			});
		}
		else {
			for (SAdminMenu menu : menus) {
				if (menuName.equals(menu.getTitle())) {
				    callback.onSuccess(menu.getId());
					Session.get().reduceActionCount(finalCallbackId);
				    return;
				}
			}
		    callback.onSuccess(null);
			Session.get().reduceActionCount(finalCallbackId);
		    return;
		}
	}
}