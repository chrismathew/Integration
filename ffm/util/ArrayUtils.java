package gov.hhs.cms.base.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class. 
 * 
 * @author juzhang
 *
 */
public class ArrayUtils
{
	/**
	 * Transfer array of integers to list of integers
	 * 
	 * @param intValues the array integers
	 * @return list of integers
	 * 
	 */
	public static List<String> asList(int[] intValues)
	{
		List<String> stringList = new ArrayList<String>();
		if (intValues == null || intValues.length == 0)
		{
			return stringList;
		}
		
		for (int intValue: intValues)
		{
			stringList.add(Integer.toString(intValue));
		}
		return stringList;
	}
}
