package gov.hhs.cms.base.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CGIFEDERAL
 * Contains the common functions used in FFE
 */
public class UtilFunctions {
	
	/**
	 * Checks a String for null or empty 
	 * @param String - str
	 * @return boolean - true if the given string is not null and not empty
	 */
	public static boolean isNotNullAndNotEmpty(String str) {
		if(str !=  null && str.trim().length() != 0) {
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
	
	public static boolean isListNotNullAndNotEmpty(List lst) {
		if(lst !=  null && lst.size() != 0) {
			return true;	
		}
		return false;
	}
	
	/**
	 * Check's a String for numeric characters or letters 
	 * @param String - str
	 * @return boolean - true if the given string has numeric characters or letters 
	 */
	public static boolean hasLetterOrDigit(String str) {
		if(str != null && str.trim().length() != 0) {
			for(int i=0; i<str.length(); i++) {
				if(!Character.isLetterOrDigit(str.charAt(i))) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		return true;
	}

	
	/**
	 * @param pattern -- The pattern you want to match to
	 * @param matcher -- The String you need to check
	 * @return true if matches, else false
	 */
	public static boolean isMatch(String pattern, String matcher) {
		
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(matcher);
		int i = 0;
		while(m.find()) {
			i++;
		}
		if(i > 0) {
			return true;
		}else {
			return false;
		}
		
	}
	
    /** Null safe check to determine if a list is null or empty
     *  @param l The list to check
     *  @return boolean True if the list is empty
     */
    public static boolean isEmptyList(Collection<?> l)
    {
        return l == null || l.isEmpty();
    }
    
    /** Null safe check to determine if an array is null or empty
     *  @param o The array to check
     *  @return boolean True if the array is empty
     */
    public static boolean isEmptyList(Object[] o)
    {
        return o == null || o.length == 0;
    }
    
    /** Determine if a string is null or "" or all spaces
     *  @param s The string to check
     *  @return boolean True if the string is empty
     */
    public static boolean isEmptyStr(String s)
    {
        return s == null || "".equals(s.trim());
    }
    
    /** Null safe check to determine if an object is null or empty
     *  @param s The string to check
     *  @return boolean True if the string is empty
     */ 
    public static boolean isEmptyObject(Object o)
    {
    	return o == null || o.equals(null);
    }

    /**
     * returns true if the resonse is affirmative
     * @param s The string to check
     *  @return boolean True if the string is affirmative, false otherwise
     */
    public static boolean responseIsAffirmative(String response) 
    {
    	return (isEmptyStr(response) ? false : "Yes".equalsIgnoreCase(response) || 
    			"Y".equalsIgnoreCase(response) || 
    			Boolean.TRUE.toString().equalsIgnoreCase(response));
    }
    
    public static Object safePut(Map<Object,Object> dynamicParameterMap, String Key, String value ) {
    	
    	if(!UtilFunctions.isEmptyObject(Key) && !UtilFunctions.isEmptyObject(value)) {   		   				
    		return dynamicParameterMap.put(Key, value);			
    	}
    	
    	else return null;   	
    }
    
    /**
     * This method takes into account the literal string "null"
     * 
     * @param str
     * @return boolean, returns true if a valid string
     */
    public static boolean isStringNotNullOrEmpty(String str) {
    	return str != null && str.trim().length() != 0 && !str.equalsIgnoreCase("null");
    }
}
