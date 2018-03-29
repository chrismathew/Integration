package gov.hhs.cms.base.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberUtil {

	/**
	 * return a max value from a dynamic argument list of Big Integers
	 * @param values
	 * @return BigInteger max value
	 */
	public static final BigInteger getMaxValue(BigInteger... values)
    {
        BigInteger retVal = BigInteger.ZERO;
        for (BigInteger v : values) {
            retVal = v.max(retVal);
	    }
        return retVal;
    }
	
	/**
	 * return minimum value from a dynamic argument list of Big Integers
	 * @param values
	 * @return BigInteger min value
	 */
	public static final BigInteger getMinValue(BigInteger... values)
    {
        BigInteger retVal = BigInteger.ZERO;
        for (BigInteger v : values) {
            retVal = v.min(retVal);
	    }
        return retVal;
    }
	
	public static BigInteger getMax(List<BigInteger> list){        
        return Collections.max(list);        
    }
    
	public static BigInteger getMaxFrom4Integer(BigInteger int1, BigInteger int2, BigInteger int3, BigInteger int4) {
    	List<BigInteger> bigIntegerList= new ArrayList<BigInteger>();
    	if(int1 != null ) {
        	bigIntegerList.add(int1);    		
    	}
    	if(int2 != null ) {    		
        	bigIntegerList.add(int2);
    	}
    	if(int3 != null ) {
        	bigIntegerList.add(int3);
    	}
    	if(int4 != null ) {
        	bigIntegerList.add(int4);
    	}

    	BigInteger largestInteger = null;
    	if(!bigIntegerList.isEmpty()) {
    		largestInteger = getMax(bigIntegerList);
    	}
    	
    	return largestInteger;    	
    }
	
	public static BigInteger getMaxFrom8Integer(BigInteger i1, BigInteger i2, BigInteger i3, BigInteger i4, BigInteger i5, BigInteger i6, BigInteger i7, BigInteger i8) {
		return getMaxFrom4Integer(getMaxFrom4Integer(i1,i2,i3,i4),getMaxFrom4Integer(i5,i6,i7,i8),null,null);
	}
	
	/**
	 * @param number  :- number to be rounded
	 * @param scale   :- scale to be rounded
	 * @return
	 */
	public static BigDecimal round(BigDecimal number, int scale) {
		if(!UtilFunctions.isEmptyObject(number)) {
			return number.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		return null;
	}
	
	/**
	 * return the larger from two BigDecimals
	 * @param val1
	 * @param val2
	 * @return BigDecimal larger value
	 */
	public static BigDecimal getLargerValue(BigDecimal val1, BigDecimal val2) {
		return val1.compareTo(val2) >= 0 ? val1 : val2;
	}
	
	/**
	 * return the smaller from two BigDecimals
	 * @param val1
	 * @param val2
	 * @return BigDecimal smaller value
	 */
	public static BigDecimal getSmallerValue(BigDecimal val1, BigDecimal val2) {
		return val1.compareTo(val2) <= 0 ? val1 : val2;
	}
}