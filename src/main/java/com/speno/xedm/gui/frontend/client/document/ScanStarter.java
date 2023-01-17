package com.speno.xedm.gui.frontend.client.document;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.Base64Utils;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

public class ScanStarter {	
	public static List< String> attrs;
	public static SUser  user;
	public static SInfo info;
	public static SFolder folder;
	public static String docid = new String();
	public static STemplate temp;
      public static void scan(SFolder folder)
      {  	
    	  try{
    	  attrs = new ArrayList<String>();
      	  attrs.clear();
    	  ScanStarter.folder = folder;
    	  user =Session.get().getUser();    
    	  info = Session.get().getInfo();    	   
									
						String incomKey = "eIncomKeys";
						String exFieldItems = "|exFieldItems";
						String Recive = "|eReciveOrLone^xEdms";
						String scanPath = "C:/_RichScan_Pub/_OutPut/RichScan.Batch.exe ";
						String Pname = "RichScan.Batch.exe ";			
					
						/*String paramEcm = "eParamEcm^EcmServer="+ info.getConfig("ecm.ip")
								+ "^EcmiPort="+ info.getConfig("ecm.port")
								+ "^EcmUserID="+ info.getConfig("ecm.username")
								+ "^EcmUserPW="	+ info.getConfig("ecm.password")
								+ "^EcmGateWayID="+ info.getConfig("ecm.gateway")
								+ "^EcmSqlGateEmc="	+ info.getConfig("setting.ecm.sqlGate.emc")
								+ "^EcmSqlGateOp="	+ info.getConfig("setting.ecm.SqlGate.Op");*/			
							//incomKey = incomKey + "^templateName=" + temp.getName();
							
						incomKey = incomKey + "^Screen_No=99991"	+ "^Company_Name="	+ info.getConfig("settings.product.vendor")
								+ "^User_Code="	+ user.getUserName()
								+ "^UserName="+ user.getName()
								+ "^UserPassword=" +Session.get().getSid()
								+ "^Department_Code="	+ user.getDepartmentId()
								+ "^DepartmentName="	+ user.getDepartment()
								+ "^CimsUrl=http://" + info.getConfig("edm.ip") + ":" + info.getConfig("edm.port") + "/xedm/service/cmisatom"
								+ "^LanguageInfo="	+ Offline.get(Constants.COOKIE_LANGUAGE).toString().toUpperCase()	
								+ "^Folder_Code="	+ ScanStarter.folder.getId()
								+ "^Folder_Name=" + ScanStarter.folder.getPathExtended();		
					
						/*for (int i = 0; i < attrs.size(); i++)
						exFieldItems = exFieldItems + "^"	+attrs.get(i) + "=";		*/
						
						try {
							incomKey += "^DutyName="+ user.getDuty().getName()	+ "^PositionName="	+ user.getPosition().getName();
						} catch (Exception e) {
							incomKey += "^DutyName="+ "^PositionName=";					
						}			
						String arg = "\"" +  "\""  + incomKey	+  Recive	+ "\"" + "\""  ;		
					
						//for base 64
						String b64 = null;
						try {							
							b64 = Base64Utils.toBase64(arg.getBytes("UTF-16LE"));
							b64 = "\"" + b64 + "\"";							
						} catch (UnsupportedEncodingException e ) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
					    String addr = "xedm://openscan " + scanPath + Pname;	
						System.out.println(addr+b64);			
						Util.downloadAsFrame(addr+arg);
						//WindowUtils.openUrl(addr + arg);			       		
    	  }
    	  catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
      }

      public static void view(String docid)
      {        
    	  ScanStarter.docid = docid;
    	  user = Session.get().getUser();   
    	  info = Session.get().getInfo();   
    	  attrs = new ArrayList<String>();
    	  attrs.clear();
    	  ServiceUtil.document().getById(Session.get().getSid(), Long.parseLong(docid), new AsyncCallback<SDocument>() {			
			public void onSuccess(SDocument result) {
				// TODO Auto-generated method stub
				String incomKey = "eIncomKeys";
				String scanPath = "C:/_RichScan_Pub/_OutPut/RichScan.Viewer.exe ";  		
		       	String Pname = "RichScan.Viewer.exe ";			
		       	String exFieldItems = "|exFieldItems";;
		       	

				//incomKey = incomKey + "^templateName=" + temp.getName();
			/*	String paramEcm = "eParamEcm^EcmServer=" + info.getConfig("ecm.ip") +"^EcmiPort="+info.getConfig("ecm.port")
						+"^EcmUserID="+ info.getConfig("ecm.username") + "^EcmUserPW=" + info.getConfig("ecm.password") 
						+"^EcmGateWayID=" + info.getConfig("ecm.gateway") + "^EcmSqlGateEmc="+info.getConfig("setting.ecm.sqlGate.emc")
						+"^EcmSqlGateOp=" + info.getConfig("setting.ecm.SqlGate.Op");*/
				
			  incomKey = incomKey + "^Screen_No=99992"+"^Company_Name="+ info.getConfig("settings.product.vendor")
		  					+"^User_Code="+user.getUserName()
		  					+ "^UserName="+ user.getName()//temp.getName()			    			
		  					+  "^UserPassword=" + Session.get().getSid()
		  					+ "^CimsUrl=http://" + info.getConfig("edm.ip") + ":" + info.getConfig("edm.port") + "/xedm/service/cmisatom"
		  					+"^Department_Code="+user.getDepartmentId()	+"^DepartmentName="+ user.getDepartment()
		  					+	"^LanguageInfo=" +Offline.get(Constants.COOKIE_LANGUAGE).toString().toUpperCase()		  					 
		  					+ "^Path_File=C:/_RichScan_Pub/_OutPut/TempViewer" +  "^DocumentID=" +  ScanStarter.docid.trim() ; 	  
				
			  try{
    	    	   incomKey += "^DutyName=" + user.getDuty().getName()	+ "^PositionName=" + user.getPosition().getName();
    	    	  }
    	    	   catch(Exception	e){
    	    		    incomKey += "^DutyName=" +  "^PositionName=";    	    	    		 
    	    	   }    	
			  /*		  try{
			for (int i = 0; i < attrs.size(); i++)
				exFieldItems = exFieldItems + "^"	+attrs.get(i) + "=";		
			  }
			  catch(Exception e){
				  exFieldItems = exFieldItems + "^"	+ "=";		
			  }*/
			    String arg = "\""+ "\""  + incomKey +  "\"" + "\"";			    
			    //for base 64
	    		String b64 = null;
				try {
					b64 = Base64Utils.toBase64(arg.getBytes("UTF-16LE"));
					b64 = "\"" + b64 + "\"";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    	
		    	String addr = "xedm://openscan " + scanPath + Pname;		
				System.out.println(addr+arg);		  	
				Util.downloadAsFrame(addr+arg);
//				WindowUtils.openUrl(addr + arg);	       		
				
			}
			
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				SC.warn("it is not right document!");
			}
		});        	  
      }
      public static void Openview(long docId,String elemId){
    	  Util.downloadAsFrame(docId, elemId);
      }
}
