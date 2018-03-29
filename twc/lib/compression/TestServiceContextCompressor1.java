
package com.twc.eis.lib.compression;


import com.twc.eis.asb.core.service.ServiceContext;
import com.twc.eis.lib.logging.DsbLogger;
import com.twc.eis.lib.logging.ILogService;
import com.twc.eis.lib.util.FileUtils;
import com.twc.eis.lib.xml.Element;


public class TestServiceContextCompressor1
{
    
    private static final ILogService log = DsbLogger.getInstance();
    
    
    //private static String scTwcId = "88B3D2C4-1BCB-04F5-8C16-A33A2AA37383";
    //private static String scTwcId = "2B3A69AB-89DD-EAB4-0BD6-2E1F607214BA";
    //private static String scTwcId = "07C007A7-0475-B614-6D3D-E67BB82B3A2A";
    //private static String scTwcId = "755240F2-588A-5C3D-3E7A-A863F17A6858";
    //private static String scTwcId = "53273B75-62A1-783C-CECC-927FDB193056";
    private static String scTwcId = "617C4CB4-1619-5C23-8D9B-8AE8044FE4F2";
    
    public static void main(String[] args) throws Exception
    {
	Class.forName("com.twc.eis.asb.persistence.Template");

	ServiceContext sc = null;	
	
	sc = ServiceContext.getInstance(scTwcId);	
	
	if(sc.getContextDocument() != null)
	{	        	    
	    System.out.println("****BLAIZE**** Fetched EXPANDED Service Context document #### = " + sc.getContextDocument());
	    	        
	}
	else {
	    
	    System.out.println("Error: Could not find Service Context Document for TWC ID " + scTwcId);
	    
	    log.debug("Error: Could not find Service Context Document for TWC ID " + scTwcId);
	}	
	
    }

}
