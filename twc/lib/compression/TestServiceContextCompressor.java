
package com.twc.eis.lib.compression;


import com.twc.eis.asb.core.service.ServiceContext;
import com.twc.eis.lib.logging.DsbLogger;
import com.twc.eis.lib.logging.ILogService;
import com.twc.eis.lib.util.FileUtils;
import com.twc.eis.lib.xml.Element;


public class TestServiceContextCompressor
{
    
    private static final ILogService log = DsbLogger.getInstance();
    
    private static String scTwcId = null;
    
    public static void main(String[] args) throws Exception
    {
	Class.forName("com.twc.eis.asb.persistence.Template");
	
	Element xmlDoc = null;
	String xmlMsg = null;
	ServiceContext sc = null;	
	
	FileUtils futil = new FileUtils();
	
	//xmlMsg = futil.ReadFileInClasspath("test-service-context-1.xml");
	//xmlMsg = futil.ReadFileInClasspath("test-sc-6.xml");
	//xmlMsg = futil.ReadFileInClasspath("test-sc-1.xml");
	xmlMsg = futil.ReadFileInClasspath("test-sc-7.xml");
	
	
	if(xmlMsg == null || xmlMsg.length() < 1)
	{
	    System.out.println("Please make sure the test-sc-7.xml file exists in the  input folder!");
	    
	    log.debug("Please make sure the test-sc-7.xml file exists in the  input folder!");
	    
	    return;
	}
	
	System.out.println("****BLAIZE**** RAW Service Context document length = " + xmlMsg.length());
	
	xmlDoc = Element.parse(xmlMsg);
	
	sc = ServiceContext.getInstance(xmlDoc, false);
	
	//sc = ServiceContext.getInstance(xmlDoc);
	
	if(sc != null)
	{	    
	    // cache the ServiceContext TWC namespace ID
	    scTwcId = sc.getTwcNamespaceId();
	    
	    
	    
	    // Persist the Service Context document
	    //sc.persist(xmlDoc);
	    
	    sc.persist(sc.getContextDocument());
	    
	    System.out.println("****BLAIZE**** Successfully persisted ServiceContext document");
	    
	    System.out.println("****BLAIZE**** ServiceContext TWC ID = " + scTwcId);
	    
	    System.out.println("****BLAIZE**** Fetching persisted compressed ServiceContext document from repository....");
	    
	    Element fetchedXmlDoc = sc.fetch(scTwcId);
	    
	    System.out.println("****BLAIZE**** Fetched EXPANDED Service Context document length = " + fetchedXmlDoc.toString().length());
	    
	    System.out.println("****BLAIZE**** Fetched EXPANDED Service Context document #### = " + fetchedXmlDoc.toString());
	    	        
	}
	else {
	    
	    System.out.println("Please make sure the test-sc-7.xml file exists in the  input folder!");
	    
	    log.debug("Please make sure the test-sc-7.xml file exists in the  input folder!");
	}
	
	
    }

}
