/**
 * Created on Aug 2, 2007
 * UtilFunctions.java
 */
package org.cms.hios.common.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.cms.hios.common.transfer.ErrorTO;
import org.cms.hios.common.transfer.LookupTO;

/**
 * @author CGIFEDERAL
 * Contains the common functions used in STAR
 */
public class CommonUtilFunctions {
    public static final int PASSWORD_LENGTH = 8;
    private static final Logger LOG = Logger.getLogger(CommonUtilFunctions.class);
	
	/**
	 * @return unencrypted password
	 */
	public static String stringGenerator() {
		Random random = new SecureRandom();
	      String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

	      String pw = "";
	      for (int i=0; i<PASSWORD_LENGTH; i++)
	      {
	          int index = (int)(random.nextDouble()*letters.length());
	          pw += letters.substring(index, index+1);
	      }
	      return pw;
	}
	
	/**
	 * Generates and encrypts a password and returns it
	 * @return
	 * @throws Exception
	 */
	public static String generateAndEncryptPassword() throws Exception {
		String password=CommonUtilFunctions.stringGenerator();
		CryptographyUtil crypt=CryptographyUtil.getInstance();
		return crypt.encrypt(password);
	}
	
	/**
	 * Checks a String for null or empty 
	 * @param String - str
	 * @return boolean - true if the given string is not null and not empty
	 */
	public static boolean isNotNullAndNotEmpty(String str) {
		if(str !=  null && str.trim().length() != 0 && !"null".equalsIgnoreCase(str)) {
			return true;	
		}
		return false;
	}
	
	/**
	 * 
	 * @param str
	 * @return boolean - true if the given string is  null or empty
	 */
	public static boolean isNullOrEmpty(String str){
		return !isNotNullAndNotEmpty(str);
	}
	
	public static ErrorTO constructErrorMsg(int errorCode){
		ErrorTO  error = null;
		
		switch (errorCode) {
		case 400:
			 error = new ErrorTO();
			 error.setCode(ErrorCodeEnum.BAD_RQUEST.getErrorCode());
			 error.setStatus(ErrorCodeEnum.BAD_RQUEST.getStatus());
			 error.setMessage(ErrorCodeEnum.BAD_RQUEST.getDescription());
			 break;
		case 404:
			 error = new ErrorTO();
			 error.setCode(ErrorCodeEnum.DATA_NOT_FOUND.getErrorCode());
			 error.setStatus(ErrorCodeEnum.DATA_NOT_FOUND.getStatus());
			 error.setMessage(ErrorCodeEnum.DATA_NOT_FOUND.getDescription());
			 break;
		case 500:
			 error = new ErrorTO();
			 error.setCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getErrorCode());
			 error.setStatus(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getStatus());
			 error.setMessage(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getDescription());
			 break;
		default:
			break;
		}
		return error;
	}
	
	public static String getCurrentDate(String format){
		SimpleDateFormat simpleDtFmt = new SimpleDateFormat(format);
		String date = simpleDtFmt.format(System.currentTimeMillis());
		return date;
	}
	
	/**
	 * convert list into comma delimited string
	 * @param list
	 * @return
	 */
	 public static String convertToCommaDelimited(List<String> list) {
	     StringBuilder sb = new StringBuilder();   
		 int i = 0;
	     if(list!=null && !list.isEmpty()){
	        	for (String string : list) {
					sb.append(string);
					if(i < (list.size() -1)){
						sb.append(",");
					}
					++i;
					}
	        	return sb.toString();
	     }
	        return null;
	 }

	 public static boolean isListContainsData(List<LookupTO> lkpList, String data){
		 boolean isListContainsData = false;
		 if(lkpList!=null&&!lkpList.isEmpty()){
			 for(LookupTO lkpObj : lkpList){
				if(lkpObj.getLookup_Id() == Integer.valueOf(data)){
					isListContainsData = true;
					break;	
				}else{
					continue;
				}
			 }	
		 }
		 return isListContainsData;
	 }
	 /**
	 * Checks if the List is null or empty
	 * 
	 * @param lst
	 * @return boolean - true if the given list is not null and not empty
	 */
	public static boolean isListNotNullAndNotEmpty(List lst) {
		if(lst !=  null && lst.size() != 0) {
			return true;	
		}
		return false;
	}
}