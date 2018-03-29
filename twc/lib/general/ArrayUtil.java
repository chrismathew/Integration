/* See javadoc output for more info.
 *
 * $Date: 2001/03/22 22:25:48 $ 
 * 
 */

package com.twc.eis.lib.general;

import java.lang.reflect.Array;

/** Static methods for working with arrays.
 *
 * A companion to the java.util.Arrays class.
 *
 * @author $Author: mcarter $
 * @version $Header: /home/cvsroot/qS/1.0/Development/source/java/com.twc.eis/lib/general/ArrayUtil.java,v 1.11 2001/03/22 22:25:48 mcarter Exp $
 */
public class ArrayUtil
{
  /** Create a new array from a section of an existing array.
   * Copies (endIndex-startIndex) elements from 'arr' starting at
   *   startIndex into a new array. <br>
   * Returns the newly created array or null if the index parameters are
   *   out of bounds or out of order.
   * <p>
   * Note that 'arr' and the return value are Object rather than Object[]
   * so that arrays of primitive types can also be used.
   */
  public static Object getSlice(Object arr, int startIndex, int endIndex)
  {
	if (endIndex < startIndex
		|| startIndex < 0
		|| endIndex > Array.getLength(arr))
	  return null;

	Class elementType = arr.getClass().getComponentType();
	Object ret_val = Array.newInstance(elementType,
									   endIndex - startIndex);

	if      (elementType.equals(Boolean.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((boolean[])ret_val)[i-startIndex] = ((boolean[])arr)[i];
	else if (elementType.equals(Byte.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((byte[])ret_val)[i-startIndex] = ((byte[])arr)[i];
	else if (elementType.equals(Character.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((char[])ret_val)[i-startIndex] = ((char[])arr)[i];
	else if (elementType.equals(Double.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((double[])ret_val)[i-startIndex] = ((double[])arr)[i];
	else if (elementType.equals(Float.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((float[])ret_val)[i-startIndex] = ((float[])arr)[i];
	else if (elementType.equals(Integer.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((int[])ret_val)[i-startIndex] = ((int[])arr)[i];
	else if (elementType.equals(Long.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((long[])ret_val)[i-startIndex] = ((long[])arr)[i];
	else if (elementType.equals(Short.TYPE))
	  for (int i = startIndex; i < endIndex; i++)
		((short[])ret_val)[i-startIndex] = ((short[])arr)[i];
	else // elementType.isPrimitive() must be false
	  for (int i = startIndex; i < endIndex; i++)
		((Object [])ret_val)[i-startIndex] = ((Object [])arr)[i];

	return ret_val;
  }  

  /** A convenience call to {@link #getSlice(Object, int, int)
   *                                getSlice(arr,
   *                                         startIndex,
   *                                         Array.getLength(arr))}.
   */
  public static Object getSlice(Object arr, int startIndex)
  {
	return getSlice(arr, startIndex, Array.getLength(arr));
  }  
}
