/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 ****************************************************************************
 * Department:  Identity Management
 *
 * File Name:   testXmlData.java	
 * Description: Test for XMLData 
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Sep 26, 2007
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.xmlbeans.*;
import com.twc.eis.lib.util.*;
import javax.xml.xpath.*;

public class testXmlData
{
    private static String filename = null;
    
    public testXmlData()
    {
        super();        
    }
    
    public static void main(String[] args) throws Exception
    {
        
        GeneralUtils util = new GeneralUtils();
        FileUtils futil = new FileUtils();
        Date localtime = new Date();
        System.out.println("INFO: Current TimeStamp is :" + localtime.toString());
        
        if (args.length < 1)
        {
            System.err.println("Invalid arguments!");
            
        }
        
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals(null))
            {
                throw new Exception("An Invalid (null) argument was found");
            }
            
            else if (util.ntrim(args[i]).toLowerCase().startsWith("-file"))
            {
                Pattern p = Pattern.compile("(\\S*)(\\s*)=(\\s*)(\\S*)", Pattern.CASE_INSENSITIVE
                        | Pattern.UNICODE_CASE);
                
                Matcher m = p.matcher(args[i]);
                
                if (m.find())
                {
                    filename = (m.group(4));
                }
                System.out.println("INFO: Filename extracted is: " + filename);
                
            }
        }
        
        if (filename == null)
        {
            System.err.println("File Name is Null");
            
        }
        
        XmlObject xobj = futil.fileToXmlObj(filename);
        if (xobj == null)
        {
            System.out.println("xmlobject is Null");
        }
      
        // Get timestamp
        XmlCursor xmlcursor = XMLUtils.getXmlCursor(xobj, "http://es.twcable.com/schemas/tsdl-v11",
        "$this//twc:ASBMessage/@timestamp");
        
        while (xmlcursor.hasNextSelection())
        {
            System.out.println("timestamp");
            xmlcursor.toNextSelection();
            System.out.println(xmlcursor.currentTokenType());
            System.out.println(xmlcursor.getTextValue()); 
        }
        
//      Get Header from ASBMessage
        xmlcursor = XMLUtils.getXmlCursor(xobj, "http://es.twcable.com/schemas/tsdl-v11",
        "$this//twc:ASBMessage/Header");
        
        while (xmlcursor.hasNextSelection())
        {
            System.out.println("Header");
            xmlcursor.toNextSelection();
            System.out.println(xmlcursor.currentTokenType());
            System.out.println(xmlcursor.xmlText()); 
        }
        // Get Body from ASBMessage        
        xmlcursor = XMLUtils.getXmlCursor(xobj, "http://es.twcable.com/schemas/tsdl-v11",
        "$this//twc:ASBMessage/Body");
        
        while (xmlcursor.hasNextSelection())
        {
            System.out.println("Body");
            xmlcursor.toNextSelection();
            System.out.println(xmlcursor.currentTokenType());
            System.out.println(xmlcursor.xmlText());
        }
        
        // Get attribute "namespace" for element (Account) "ID" 
        xmlcursor = XMLUtils.getXmlCursor(xobj, "http://es.twcable.com/schemas/tsdl-v11",
        "$this//twc:ASBMessage/Body/AccountData/Account/ID/@namespace");
        
        while (xmlcursor.hasNextSelection())
        {
            System.out.println("AccountData");
            xmlcursor.toNextSelection();
            System.out.println(xmlcursor.currentTokenType());            
            System.out.println(xmlcursor.getTextValue());            
        }
        
        xmlcursor.dispose();       
       
    }
    
}
