package gov.hhs.cms.base.common.util;

import java.util.ArrayList;
import java.util.List;

public class GenericUtilities {
	
	/*
	 * Method will take a List of type Object and safely try to cast it to the specified type
	 */
	public static final <T> List<T> CastListAs(Class<T> type, List<Object> listToCast) {
		List<T> listToReturn = new ArrayList<T>();
		
		for (Object item : listToCast) {
			listToReturn.add((T) item);
		}
		
		return listToReturn;
	}
}
