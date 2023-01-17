package com.speno.xedm.gui.common.client.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ReadyStateWatch.ReadyState;
import com.speno.xedm.gui.frontend.client.document.PreviewPopup;

public class Util {
	public static String[] OFFICE_EXTS = new String[] { ".doc", ".xls", ".ppt", ".docx", ".xlsx", ".pptx" };

	public static String[] IMAGE_EXTS = new String[] { ".gif", ".jpg", ".jpeg", ".bmp", ".tif", ".tiff", ".png" };

	public static String[] MEDIA_EXTS = new String[] { ".mp3", ".mp4", ".wav", ".avi", ".mpg", ".wmv", ".wma", ".asf",
			".mov", ".rm", ".flv", ".aac", ".vlc", ".ogg", ".webm", ".swf", ".mpeg", ".swf" };
	public static String workid;


	/**
	 * Generates HTML image code with style.
	 * 
	 * @param imageName the name of the icon image
	 * @param alt the image alt
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName, String alt) {
		return "<img border=\"0\" align=\"absmidle\" alt=\"" + alt + "\" title=\"" + alt + "\" src='"
				+ Util.imageUrl(imageName) + "' />";
	}

	public static String imageHTML(String imageName, int width, int height) {
		return "<img width=\"" + width + "\"" + "\" height=" + height + "\"" + " border=\"0\" align=\"TEXTTOP\" suppress=\"TRUE\" src=\""
				+ Util.imageUrl(imageName) + "\" ></img>";
	}
	
	public static String imageUrl(String imageName) {
		return imagePrefix() + imageName;
	}

	public static String brandUrl(String imageName) {
		return brandPrefix() + imageName;
	}

	public static String strip(String src) {
		if (src == null)
			return null;
		else
			return src.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public static String contextPath() {
		return GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "");
	}

	public static void changeLocale(String locale) {
		String url;
		if (Session.get().isDefaultSSO())
			url = "sso.jsp?locale=" + locale;
		else
			url = GWT.getModuleName() + ".jsp?locale=" + locale;
		
		redirect(contextPath() + GWT.getModuleName() + ".jsp?locale=" + locale);
	}

	public static String thumbnailPrefix(){
		return contextPath() + "thumbnail/view/";
	}
	public static String imagePrefix() {
		return contextPath() + "skin/images/";
	}

	public static String brandPrefix() {
		return contextPath() + "skin/brand/";
	}

	/**
	 * Generates HTML image code with style.
	 * 
	 * @param imageName the image name
	 * @return the resultant HTML
	 */
	public static String imageHTML(String imageName) {
		return imageHTML(imageName, "");
	}

	public static boolean isPreviewable(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : OFFICE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isOfficeFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : OFFICE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isImageFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : IMAGE_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isMediaFile(String fileName) {
		String tmp = fileName.toLowerCase();
		for (String ext : MEDIA_EXTS) {
			if (tmp.endsWith(ext))
				return true;
		}
		return false;
	}

	public static boolean isOfficeFileType(String type) {
		for (String ext : OFFICE_EXTS) {
			if (type.equalsIgnoreCase(ext))
				return true;
		}
		return false;
	}

	public static void showWaitCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
	}

	public static void showDefaultCursor() {
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
	}
	
	// 20130830, junsoo, 서버에 저장된 이미지 정보 획득
	public static String getNameOfServerImage(String info) {
		if (info == null) {
			Log.debug("server settings is null");
			return "";
		}
		String[] strs = info.split(",");
		if (strs != null && strs.length > 0)
			return strs[0];
		return "";
	}
	public static int getWidthOfServerImage(String info) {
		if (info == null) {
			Log.debug("server settings is null");
			return 0;
		}
		String[] strs = info.split(",");
		if (strs != null && strs.length > 1) {
			try {
				int value = Integer.parseInt(strs[1].trim()); 
				return value;
			} catch (Exception e) {
				return 0;
			}
		}
		return 0;
	}
	public static int getHeightOfServerImage(String info) {
		if (info == null){
			Log.debug("server settings is null");
			return 0;
		}
		String[] strs = info.split(",");
		if (strs != null && strs.length > 2) {
			try {
				int value = Integer.parseInt(strs[2].trim()); 
				return value;
			} catch (Exception e) {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Format file size in Bytes, KBytes or MBytes.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static native String formatSize(double size) /*-{
		if (size / 1024 < 1) {
			str = size + " Bytes";
		} else if (size / 1048576 < 1) {
			str = (size / 1024).toFixed(1) + " KBytes";
		} else if (size / 1073741824 < 1) {
			str = (size / 1048576).toFixed(1) + " MBytes";
		} else {
			str = (size / 1073741824).toFixed(1) + " GBytes";
		}
		return str;
	}-*/;

	/**
	 * Format file size in KB.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static String formatSizeKB(double size) {
		String str;
		if (size < 1) {
			str = "0 KB";
		} else if (size < 1024) {
			str = "1 KB";
		} else {
			NumberFormat fmt = NumberFormat.getFormat("#,###");
			str = fmt.format(Math.ceil(size / 1024)) + " KB";
			str = str.replace(',', I18N.groupingSepator());
		}
		return str;
	}

	/**
	 * Format file size in Windows 7 Style.
	 * 
	 * @param size The file size in bytes.
	 * @return The formated file size.
	 */
	public static String formatSizeW7(double size) {
		String str = "";
		if (size < 1) {
			str = "0 bytes";
		} else if (size < 1024) {
			str = size + " bytes";
		} else if (size < 1048576) {
			double tmp = size / 1024;
			if (tmp < 10) {
				NumberFormat fmt = NumberFormat.getFormat("###.##");
				str = fmt.format(tmp) + " KB";
			} else if (tmp < 100) {
				NumberFormat fmt = NumberFormat.getFormat("###.#");
				str = fmt.format(tmp) + " KB";
			} else {
				NumberFormat fmt = NumberFormat.getFormat("###");
				str = fmt.format(tmp) + " KB";
			}
			str = str.replace('.', I18N.decimalSepator());
		} else {
			double tmp = size / 1048576;
			if(size < 1073741824){
				if (tmp < 10) {
					NumberFormat fmt = NumberFormat.getFormat("###.##");
					str = fmt.format(tmp) + " MB";
				} else if (tmp < 100) {
					NumberFormat fmt = NumberFormat.getFormat("###.#");
					str = fmt.format(tmp) + " MB";
				} else {
					NumberFormat fmt = NumberFormat.getFormat("###");
					str = fmt.format(tmp) + " MB";
				}
			}else if(size >= 1073741824){
				tmp = tmp / 1024;
				if (tmp < 10) {
					NumberFormat fmt = NumberFormat.getFormat("###.##");
					str = fmt.format(tmp) + " GB";
				} else if (tmp < 100) {
					NumberFormat fmt = NumberFormat.getFormat("###.#");
					str = fmt.format(tmp) + " GB";
				} else {
					NumberFormat fmt = NumberFormat.getFormat("###");
					str = fmt.format(tmp) + " GB";
				}
			}
			str = str.replace('.', I18N.decimalSepator());
		}
		return str;
	}

	/**
	 * Format file size in bytes
	 * 
	 * @param size The file size in bytes.
	 */
	public static String formatSizeBytes(double size) {
		String str;
		NumberFormat fmt = NumberFormat.getFormat("#,###");
		str = fmt.format(size) + " bytes";
		str = str.replace(',', I18N.groupingSepator());
		return str;
	}

	/**
	 * Format number percentage.
	 * 
	 * @param value The value to be formatted.
	 * @param fixed The number of decimal places.
	 * @return The formated value.
	 */
	public static native String formatPercentage(double value, int fixed) /*-{
		str = value.toFixed(fixed);

		return str + "%";
	}-*/;

	/**
	 * Get browser language
	 * 
	 * @return The language in ISO 639 format.
	 */
	public static native String getBrowserLanguage() /*-{
		var lang = navigator.language ? navigator.language
				: navigator.userLanguage;

		if (lang) {
			return lang;
		} else {
			return "en";
		}
	}-*/;

	/**
	 * returns 'opera', 'safari', 'ie6', 'ie7', 'gecko', or 'unknown'.
	 */
	public static native String getUserAgent() /*-{
		try {
			if (window.opera)
				return 'opera';
			var ua = navigator.userAgent.toLowerCase();
			if (ua.indexOf('webkit') != -1)
				return 'safari';
			if (ua.indexOf('msie 6.0') != -1)
				return 'ie6';
			if (ua.indexOf('msie 7.0') != -1)
				return 'ie7';
			if (ua.indexOf('gecko') != -1)
				return 'gecko';
			return 'unknown';
		} catch (e) {
			return 'unknown'
		}
	}-*/;

	public static native void copyToClipboard(String text) /*-{
		new $wnd.copyToClipboard(text);
	}-*/;

	public static native boolean isValidEmail(String email) /*-{
		var reg1 = /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)/; // not valid
		var reg2 = /^.+\@(\[?)[a-zA-Z0-9\-\.]+\.([a-zA-Z]{2,3}|[0-9]{1,3})(\]?)$/; // valid
		return !reg1.test(email) && reg2.test(email);
	}-*/;

	public static native void redirect(String url)
	/*-{
		$wnd.location.replace(url);
	}-*/;

	public static String padLeft(String s, int n) {
		if (s.length() > n) {
			return s.substring(0, n - 3) + "...";
		} else
			return s;
	}

	/**
	 * Exports into the CSV format the content of a ListGrid.
	 * 
	 * @param listGrid Grid containing the data
	 * @return The CSV document as tring
	 */
	public static void exportCSV(ListGrid listGrid) {
		StringBuilder stringBuilder = new StringBuilder(); // csv data in here

		// column names
		ListGridField[] fields = listGrid.getFields();
		for (int i = 0; i < fields.length; i++) {
			ListGridField listGridField = fields[i];
			if (listGridField.getType().equals(ListGridFieldType.ICON)
					|| listGridField.getType().equals(ListGridFieldType.IMAGE)
					|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
					|| listGridField.getType().equals(ListGridFieldType.BINARY))
				continue;

			stringBuilder.append("\"");
			stringBuilder.append(listGridField.getTitle());
			stringBuilder.append("\";");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove last
																// ";"
		stringBuilder.append("\n");

		// column data
		Record[] records = new Record[0];
		try {
			records = listGrid.getRecords();
		} catch (Throwable t) {
		}

		if (records == null || records.length < 1) {
			/*
			 * In case of data bound grid, we need to call the original records
			 * list
			 */
			RecordList buf = listGrid.getOriginalRecordList();
			if (buf != null) {
				records = new Record[buf.getLength()];
				for (int i = 0; i < records.length; i++)
					records[i] = buf.get(i);
			}
		}

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_dateshort"));
		for (int i = 0; i < records.length; i++) {
			Record record = records[i];
			ListGridField[] listGridFields = listGrid.getFields();

			for (int j = 0; j < listGridFields.length; j++) {
				try {
					ListGridField listGridField = listGridFields[j];
					if (listGridField.getType().equals(ListGridFieldType.ICON)
							|| listGridField.getType().equals(ListGridFieldType.IMAGE)
							|| listGridField.getType().equals(ListGridFieldType.IMAGEFILE)
							|| listGridField.getType().equals(ListGridFieldType.BINARY))
						continue;

					stringBuilder.append("\"");
					if (listGridField.getType().equals(ListGridFieldType.DATE)) {
						stringBuilder.append(formatter.format(record.getAttributeAsDate(listGridField.getName())));
					} else {
						stringBuilder.append(record.getAttribute(listGridField.getName()));
					}
					stringBuilder.append("\";");
				} catch (Throwable t) {
					/*
					 * May be that not all the rows are available, since we can
					 * count just on the rows that were rendered.
					 */
				}
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove
																	// last ";"
			stringBuilder.append("\n");
		}
		String content = stringBuilder.toString();

		/*
		 * Now post the CSV content to the server
		 */
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Util.contextPath() + "/csv?sid="
				+ Session.get().getSid());
		builder.setHeader("Content-type", "application/csv");

		try {
			builder.sendRequest(content, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Log.error(exception.getMessage(), null, exception, false);
				}

				public void onResponseReceived(Request request, Response response) {
					/*
					 * Now we can download the complete file
					 */
					// TODO : windowUtils
//					WindowUtils.openUrl(GWT.getHostPageBaseURL() + "/csv?sid=" + Session.get().getSid());
				}
			});
		} catch (RequestException e) {
			GWT.log("error", e);
		}
	}
	
	/**
	 * String 값을 long값으로 변환하여 반환함. 만약 null이거나 ""일 경우 Constants.INVALID (-1)을 반환함
	 * @param value
	 * @return
	 */
	public static long getAslong(String value) {
		if(value == null || "".equals(value.trim())) {
			return Constants.INVALID_LONG;
		}
		return Long.parseLong(value);
	}
	
	public static String getSafeString(String value) {
		if(value == null) {
			return "";
		}
		return value;
	}
	
	/**
	 * file size 포멧 변환 후 리턴.
	 * @param fileSize
	 * @return
	 */
	public static String setFileSize(long fileSize, boolean kb){
		NumberFormat nf = NumberFormat.getFormat("###,###,###,###,###");
		if(kb && fileSize/1024>0)
			return nf.format(fileSize/1024 + 1)+" kb";
		else
			return nf.format(fileSize)+" byte";
	}
	
	public static String getFormattedFileSize(long fileSize, boolean kb){
		NumberFormat nf = NumberFormat.getFormat("###,###,###,###,###");
		if (kb && fileSize/(1073741824)>0)
			return nf.format(fileSize/1073741824 + 1)+" GB";
		else if (kb && fileSize/(1048576)>0)
			return nf.format(fileSize/1048576 + 1)+" MB";
		else if (kb && fileSize/1024>0)
			return nf.format(fileSize/1024 + 1)+" KB";
		else
			return nf.format(fileSize)+" byte";
	}

	/**
	 * 문자열의 첫 줄 문자열 자르기 (한글 포함) 
	 * @param inputStr
	 * @param limit
	 * @param fixStr
	 * @return
	 */
	public static String getStringLimitRemoveEnter(String inputStr, int limit, String fixStr){
		if (inputStr == null)	return "";
		if(!inputStr.split("<br>").equals(null) && inputStr.split("<br>").length != 0) 
			return strCut(inputStr.split("<br>")[0], limit, fixStr);
		else return "";
	}
	
	/**
	 * 문자열 자르기 (한글 포함). 일부 브라우저에서 오류 발생하므로 strCut 을 사용할 것.
	 * 
	 * @param inputStr		원래 문자열
	 * @param limit			자를 길이
	 * @param fixStr		끝에 붙일 문자열
	 * @return
	 */
//	public static String getStringLimit(String inputStr, int limit, String fixStr) {
//        if (inputStr == null)
//            return "";
//        if (limit <= 0)
//            return inputStr;
//        byte[] strbyte = null;
//        strbyte = inputStr.getBytes();
//
//        if (strbyte.length <= limit) {
//            return inputStr;
//        }
//        char[] charArray = inputStr.toCharArray();
//        int checkLimit = limit;
//        for ( int i = 0 ; i < charArray.length ; i++ ) {
//            if (charArray[i] < 256) {
//                checkLimit -= 1;
//            }
//            else {
//                checkLimit -= 2;
//            }
//            if (checkLimit <= 0) {
//                break;
//            }
//        }
//        //대상 문자열 마지막 자리가 2바이트의 중간일 경우 제거함
//        byte[] newByte = new byte[limit + checkLimit];
//        for ( int i = 0 ; i < newByte.length ; i++ ) {
//            newByte[i] = strbyte[i];
//        }
//        if (fixStr == null) {
//            return new String(newByte);
//        }
//        else {
//            return new String(newByte) + fixStr;
//        }
//    }
	
	public static String strCut(String szText, int nLength) {
		String postFix = "";
		return strCut(szText, nLength, postFix);
	}
	
	public static String strCut(String szText, int nLength, String postFix) { // 문자열 자르기
		String r_val = szText;
		int oF = 0, oL = 0, rF = 0, rL = 0;
		int nLengthPrev = 0;
		try {
			byte[] bytes = r_val.getBytes("UTF-8"); // 바이트로 보관
			// x부터 y길이만큼 잘라낸다. 한글안깨지게.
			int j = 0;
			if (nLengthPrev > 0)
				while (j < bytes.length) {
					if ((bytes[j] & 0x80) != 0) {
						oF += 2;
						rF += 3;
						if (oF + 2 > nLengthPrev) {
							break;
						}
						j += 3;
					} else {
						if (oF + 1 > nLengthPrev) {
							break;
						}
						++oF;
						++rF;
						++j;
					}
				}
			j = rF;
			while (j < bytes.length) {
				if ((bytes[j] & 0x80) != 0) {
					if (oL + 3 > nLength) {
						break;
					}
					oL += 3;
					rL += 3;
					j += 3;
				} else {
					if (oL + 1 > nLength) {
						break;
					}
					++oL;
					++rL;
					++j;
				}
			}
			r_val = new String(bytes, rF, rL, "UTF-8"); // charset 옵션
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (!szText.equals(r_val))
			return r_val + postFix;
		return r_val;
	}

	/**
	 * i18n 설정에 따라 date 포멧 결정
	 * 
	 * @param date : Set date
	 * @param setHour : Show Hour
	 */
	public static String getFormattedDate(Date date, boolean bDetail){
		DateTimeFormat day;
		if(date == null)	return "";
		
		if(bDetail)
			day = DateTimeFormat.getFormat(I18N.message("format_date"));
		else
			day = DateTimeFormat.getFormat(I18N.message("format_dateshort"));
		return day.format(date).toString();
	}

	/**
	 * i18n 설정에 따라 date 포멧 결정
	 * 
	 * @param date : Set date
	 * @param setHour : Show Hour
	 * 
	 * 20131128 na foever의 경우 텍스트 메시지 추가
	 */
	public static String getFormattedExpireDate(Date expireDate, boolean bDetail){
		DateTimeFormat day;
		if(expireDate == null)	return "";
		
		// 오늘 날짜
		Date today = new Date();
		// 문서의 생성일과 expireDate를 비교하여 forever 처리
		long retentionYear = (expireDate.getTime() - today.getTime())/(60*60*24*1000*365L);
		
		if(retentionYear>=999) return I18N.message("forever");
		
		if(bDetail)
			day = DateTimeFormat.getFormat(I18N.message("format_date"));
		else
			day = DateTimeFormat.getFormat(I18N.message("format_dateshort"));
		return day.format(expireDate).toString();
	}	
	
	/**
	 *	검색 조건에 맞는 Date 포멧 설정 (형식 : yyyyMMdd)
	 *	@param date
	 * */
	public static String getSearchFormattedDate(Date date){
		DateTimeFormat day = DateTimeFormat.getFormat("yyyy/MM/dd");
		return day.format(date).toString();
	}
	
	/**
	 *	expire date의 값을 생성한다.
	 *	StandardPropertiesPanel에서도 사용하기 때문에 static 처리
	 *	130731 taesu
	 * */ 
	public static String getFormattedExpireDate(Date createDate, Date expireDate){
		// 문서 retention 날짜
		if (expireDate == null)
			// 20130819, junsoo, 설정되어 있지 않으면, 빈공간 리턴.
			// 20130829, taesu, 값을 채우도록 변경
			return I18N.message("notspecified");
//			return I18N.message("forever");
		// 오늘 날짜
		Date today = new Date();
		// expire date
		String leftDays = String.valueOf((expireDate.getTime() - today.getTime())/(60*60*24*1000L));
		// 문서의 생성일과 expireDate를 비교하여 forever 처리
		long retentionYear = (expireDate.getTime() - createDate.getTime())/(60*60*24*1000*365L);
		if(retentionYear>=999){
			return I18N.message("forever");
		}else
			// 20131126, na, 문구 수정
			return Util.getFormattedDate(expireDate, false) + " (" + leftDays + ")";
//		 	return Util.getFormattedDate(expireDate, false) + " (" + leftDays + " " + I18N.message("second.dayleft") + ")";
	}
	
	/**
	 * 폐기 일자 획득.
	 * @param createdDate
	 * @param retentionDays
	 * @return
	 */
	public static Date getExpireDateFromRetention(Date createdDate, long retentionDays){
		// 오늘 날짜
		if (createdDate == null)
			createdDate = new Date();
		
		return new Date(createdDate.getTime() + (retentionDays * 24*60*60*1000L));
	}
	
	public static long getRetentionFromExpireDate(Date createdDate, Date expireDate){
		if (createdDate == null && expireDate == null)
			return 0;
		
		// 20130819, junsoo, 설정되어 있지 않으면, forever가 아닌 0으로 리턴
		if (expireDate == null)
			return 0L;
		
		// 오늘 날짜
		if (createdDate == null)
			createdDate = new Date();

		return (expireDate.getTime() - createdDate.getTime())/(60*60*24*1000L);

	}


	/**
	 * 20130807, junsoo
	 * document preview
	 * @param docId
	 * @param elementId
	 */
	public static void preview(long docId, String elementId, boolean isPrint){
//		if (docId > 0L)
//			WindowUtils.openPopupUrl(GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&docId=" + docId + "&elementId=" + elementId, "", "");
//		else
//			WindowUtils.openPopupUrl(GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&elementId=" + elementId, "", "");

//		Util.preview(docId, elementId);
		PreviewPopup view = new PreviewPopup(docId, elementId, isPrint);
		view.show();

	}

	public static void previewECMWindow(long docId, String elementId, boolean isPrint){
//		String urlEid = (elementId != null && elementId.length() > 0)? "&elementId=" + elementId : "";
		
		String url = "";
		url += GWT.getHostPageBaseURL() + "ecm-preview?sid=" + Session.get().getSid() + "&elementId=" + elementId;
//		if (docId > 0L)
//			url += GWT.getHostPageBaseURL() + "ecm-preview?sid=" + Session.get().getSid() + "&docId=" + docId + urlEid;
//		else
//			url += GWT.getHostPageBaseURL() + "ecm-preview?sid=" + Session.get().getSid() + urlEid;
		
		url += "&viewerType=1";		// inline 뷰어는 1
		url += "&print=" + isPrint;
		
		int width = 800;
		int height = 600;
		try {
			width = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.width"));
			height = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.height"));
		} catch (Exception e) {}
		
		
		WindowUtils.openPopupUrl(url, "_blank", "width=" + width + ", height=" + height + ", copyhistory=no");
	}
	
	
	public static void previewWindow(long docId, String elementId, boolean isPrint){
		String urlEid = (elementId != null && elementId.length() > 0)? "&elementId=" + elementId : "";
		
		String url = "";
		if (docId > 0L)
			url += GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + "&docId=" + docId + urlEid;
		else
			url += GWT.getHostPageBaseURL() + "preview?sid=" + Session.get().getSid() + urlEid;

		url += "&viewerType=1";		// inline 뷰어는 1
		url += "&print=" + isPrint;
		
		int width = 800;
		int height = 600;
		try {
			width = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.width"));
			height = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.height"));
		} catch (Exception e) {}

		
		WindowUtils.openPopupUrl(url, "_blank", "width=" + width + ", height=" + height + ", copyhistory=no");
	}

	/**
	 * 20130807, junsoo
	 * document download
	 * 현재 위치에서 표시하므로, 에러가 발생하면 back 으로 돌아가야함.
	 * @param docId
	 * @param elementId
	 */
	public static void download(long docId, String elementId){
		if (elementId != null && elementId.length() > 0)
		{
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&elementId=" + elementId);
		
		}
		else
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId);
	}
	
	public static void downloadAsFrame(String url){
		Frame frame = Frame.wrap(RootPanel.get("__DownloadFrame").getElement());
		frame.setUrl(url);
//		Frame frame = new Frame(url); 
        new ReadyStateWatch(frame).addReadyStateChangeHandler(
        		
                        new ValueChangeHandler<ReadyState>() { 
                        	 
                        	@Override
							public void onValueChange(ValueChangeEvent<ReadyState> event) { 
							        switch(event.getValue()){ 
							                case COMPLETE: 							              
//							                        Window.alert("I am loaded"); 
							                        break; 
							                case INTERACTIVE: 
							                case LOADING: 
							                case UNINITIALIZED: 
							        } 
							} 
        }); 
//        RootPanel.get().add(frame);
	}
	public static void getOpenPost(String url)
	{		
		try {
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
			builder.sendRequest("", new RequestCallback() {				
				public void onResponseReceived(Request request, Response response) {
					// TODO Auto-generated method stub				
					System.out.println("success");
				}				
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub			
					System.out.println("fail");
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void batchdownload(String name)
	{			
		downloadAsFrame(GWT.getHostPageBaseURL() + "BatchCheckServlet?sid=" + Session.get().getSid() + "&foldername="+name);			
	}
	/**
	 * 20130906, junsoo, frame 방식 다운로드. 기존의 download 의 단점이 back으로 돌아가야 하는 문제 해결.
	 * @param docId
	 * @param elementId
	 */
	public static void downloadAsFrame(long docId, String elementId){
		String url;
		if (elementId != null && elementId.length() > 0)
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&elementId=" + elementId;
//			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&elementId=" + elementId);
		else
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId;
//			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId);
		
		downloadAsFrame(url);
	}
	



	/**
	 * 20130807, junsoo
	 * document download
	 * @param docId
	 * @param elementId
	 */
	public static void open(long docId, String elementId){
		if (elementId != null && elementId.length() > 0)
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&elementId=" + elementId);
		else
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId);
	}

	/**
	 * 20140121, junsoo, launcher 기동
	 * @param docId
	 * @param elementId
	 * @param fileName
	 */
	public static void open(long docId, String elementId, String fileName){
		String uploadUrl = Session.get().getInfo().getConfig("gui.launcher.uploadurl");
		if (uploadUrl == null || uploadUrl.length() < 1)
			uploadUrl = "TMP";

		String command = Session.get().getInfo().getConfig("gui.launcher.command");
		if (command == null || command.length() < 1)
			command = "explorer.exe";

		String url;
		if (elementId != null && elementId.length() > 0) {
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&elementId=" + elementId;
		} else {
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId;
		}

		fileName = elementId + "." + getExtByFileName(fileName);
//		fileName = fileName.replaceAll(" ", "_");
		String fullUrl = "xedm://openremote " + uploadUrl + " " + command + " " + url + " " + fileName;
		downloadAsFrame(fullUrl);
	}

	/**
	 * download version
	 * @param docId
	 * @param versionId
	 * @param elementId
	 */
//	public static void downloadVersion(long docId, long versionId, String elementId){
//		if (elementId != null && elementId.length() > 0)
//			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&versionId=" + versionId + "&elementId=" + elementId);
//		else
//			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&versionId=" + versionId);
//	}
	public static void downloadVersion(long docId, long versionId, String elementId){
		String url;
		if (elementId != null && elementId.length() > 0)
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&versionId=" + versionId + "&elementId=" + elementId;
		else
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + docId + "&versionId=" + versionId;
		
		downloadAsFrame(url);
	}

	/**
	 * open version
	 * @param docId
	 * @param versionId
	 * @param elementId
	 */
	public static void openVersion(long docId, long versionId, String elementId){
		if (elementId != null && elementId.length() > 0)
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&versionId=" + versionId + "&elementId=" + elementId);
		else
			WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&versionId=" + versionId);
	}
	
	/**
	 * 20140121, junsoo, launcher 기동
	 * 
	 * @param docId
	 * @param elementId
	 * @param fileName
	 */
	public static void openVersion(long docId, long versionId, String elementId, String fileName){
		String uploadUrl = Session.get().getInfo().getConfig("gui.launcher.uploadurl");
		if (uploadUrl == null || uploadUrl.length() < 1)
			uploadUrl = "TMP";
		String command = Session.get().getInfo().getConfig("gui.launcher.command");
		if (command == null || command.length() < 1)
			command = "explorer.exe";

		String url;
		if (elementId != null && elementId.length() > 0) {
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&versionId=" + versionId + "&elementId=" + elementId;
		} else {
			url = GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&open=true" + "&docId=" + docId + "&versionId=" + versionId;
		}

		fileName = elementId + "." + getExtByFileName(fileName);
		WindowUtils.openUrl("xedm://openremote " + uploadUrl + " " + command + " " + url + " " + fileName);

	}


	/**
	 * 20130807, junsoo
	 * document download
	 * @param docId
	 * @param elementId
	 */
	public static void downloadTemplate(long templateId){
		WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&templateId=" + templateId);
	}

	
	
	public static String getExtByFileName(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos >=0) {
			return fileName.substring(pos+1);
		}
		return "";
	}
	
	// 파일 타입을 구한다.
	public static String getFileType(String data){
		String getType="";
		String[] splitData = data.split("\\.");
		String type = splitData[splitData.length-1];
		
		if(type.equals("txt") || type.equals("text")){
			getType = "text";
		}else if(type.equals("excel") || type.equals("xlsx") || type.equals("xls")){
			getType = "excel";
		}else if(type.equals("ppt") || type.equals("pptx")){
			getType = "powerpoint";
		}else{
			getType = "text";
		}
		return getType+".png";
	}
	
	/**
	 * 서버 IconSelector 클래스로 부터 복사.
	 * returns the icon by parsing the provided file extension
	 * @param ext
	 * @return
	 */
	public static String getIconByExt(String ext) {
		String icon = "";
		if (ext != null)
			ext = ext.toLowerCase();

		if (ext == null || ext.equalsIgnoreCase(""))
			icon = "generic.png";
		else if (ext.equals("pdf"))
			icon = "pdf.png";
		else if (ext.equals("txt") || ext.equals("properties"))
			icon = "text.png";
		else if (ext.equals("doc") || ext.equals("docx") || ext.equals("odt") || ext.equals("rtf") || ext.equals("ott")
				|| ext.equals("sxw") || ext.equals("wpd") || ext.equals("kwd") || ext.equals("dot"))
			icon = "word.png";
		else if (ext.equals("xls") || ext.equals("xlsx") || ext.equals("ods") || ext.equals("xlt") || ext.equals("ots")
				|| ext.equals("sxc") || ext.equals("dbf") || ext.equals("ksp") || ext.equals("odb"))
			icon = "excel.png";
		else if (ext.equals("ppt") || ext.equals("pptx") || ext.equals("odp") || ext.equals("pps") || ext.equals("otp")
				|| ext.equals("pot") || ext.equals("sxi") || ext.equals("kpr"))
			icon = "powerpoint.png";
		else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("png") || ext.equals("bmp")
				|| ext.equals("tif") || ext.equals("tiff") || ext.equals("psd"))
			icon = "picture.png";
		else if (ext.equals("htm") || ext.equals("html") || ext.equals("xml") || ext.equals("xhtml"))
			icon = "html.png";
		else if (ext.equals("eml") || ext.equals("msg") || ext.equals("mail"))
			icon = "page_white_email.png";
		else if (ext.equals("zip") || ext.equals("rar") || ext.equals("gz") || ext.equals("tar") || ext.equals("jar")
				|| ext.equals("7z"))
			icon = "zip.png";
		else if (ext.equals("p7m") || ext.equals("m7m"))
			icon = "p7m.png";
		else
			icon = "generic.png";

		return icon;
	}
	
	// folder 경로 찾기
	public static String getPath(TreeGrid tgrid, long folderId) {
		TreeNode selectedNode = tgrid.getTree().find("folderId", Long.toString(folderId));
		String path = "";
		TreeNode[] parents = tgrid.getTree().getParents(selectedNode);
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
				path += "/" + parents[i].getName();
		}
		path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
		return path;
	}
	
	/**
	 * HTML 테그 제거 
	 * @param str
	 * @return
	 */
	public static String removeTag(String str){		
		return str.replaceAll("\\<.*?\\>", "");
//		Matcher mat;   
//		// tag 처리 
//		Pattern tag = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");  
//		mat = tag.matcher(str);  
//		str = mat.replaceAll("");  
//		// ntag 처리 
//		Pattern ntag = Pattern.compile("<\\w+\\s+[^<]*\\s*>");  
//		mat = ntag.matcher(str);  
//		str = mat.replaceAll("");  
//		// entity ref 처리
//		Pattern Eentity = Pattern.compile("&[^;]+;");  
//		mat = Eentity.matcher(str);  
//		str = mat.replaceAll("");
//		// whitespace 처리 
//		Pattern wspace = Pattern.compile("\\s\\s+");  
//		mat = wspace.matcher(str); 
//		str = mat.replaceAll(""); 	         
//		return str ;		
	}
	/**
	 * 20131119, na, 파일명 추출함수
	 */
	public static String removeExtend(String str){
		return str.substring(str.lastIndexOf("\\")+1, str.lastIndexOf("."));
	}
	
	public static String getFileName(String str){
		int pos = str.lastIndexOf("\\");
		if (pos >=0 )
			return str.substring(str.lastIndexOf("\\")+1);
		else 
			return str;
	}
	
	public static <T extends Number> T getSafeNumber(T value, T defaultValue){
		if (value == null)
			return defaultValue;
		
		return value;
	}
	
	public static boolean isEmpty(String str) {
		if (str == null || str.length() < 1)
			return true;
		return false;
	}
	
	public static Date convertNoTimeDate(Date date){
		long time = date.getTime();
		Long halfDay = (long) (60*60*12*1000);
		return new Date(time - halfDay);
	}
	
	public static boolean getSetting(String property){
		boolean isSetting = true;
		try {
			isSetting = !"false".equals(Session.get().getInfo().getConfig(property));
		} catch (Exception e) {}
		return isSetting;
	}
	
	// 20140325, junsoo, string의 size 체크
	public static boolean isValidSize(FormItem formItem, String value, int maxLength, boolean withPopup, String header) {
		if (formItem != null)
			value = (String)formItem.getValue();
		if(value == null) return true;
		byte[] strByte;
		try {
			strByte = value.getBytes("UTF-8");
		} catch (Exception e) {
			strByte = value.getBytes();
		}
		
		if(strByte.length > maxLength){
			if (formItem != null)
				formItem.setValue(value = Util.strCut(value, maxLength));
			
			if (withPopup) {
				String message = I18N.message("exceedMessage");
				message += "<br>";
				message += header + " (" +I18N.message("current")+": "+ strByte.length+ 
									"bytes / "+I18N.message("max")+": " + maxLength + "bytes)";
				SC.warn(message);
			}
			
			return false;
		}

		return true;
	}
}
