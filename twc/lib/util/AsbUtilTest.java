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
 * File Name:   AsbUtilTest.java	
 * Description: test program
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Oct 10, 2007
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
//import com.twc.eis.asb.config.*;
//import com.twc.eis.asb.config.asbcomponent.AsbComponent_Cfg;
//import com.twc.eis.asb.config.asbcomponent.IAsbComponent_Cfg;

public class AsbUtilTest
{
    private static String filename = null;
    
    public AsbUtilTest()
    {
             
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
        String temp = futil.readLocalFile(filename);
        if (xobj == null)
        {
            System.out.println("xmlobject is Null");
        }
        
        XmlObject xobj1 = futil.fileToXmlObj("test_tsdl.xml");
        
      
        /* 
        //AsbComponentUtil autil = new AsbComponentUtil(xobj, "create");
        //String temp = null;
        //AsbComponentCfgFactory fac = AsbComponentCfgFactory.init(temp);
        //IAsbComponent asb = new AsbComponent("test","create");        
        //IAsbMessageHandler mes = ((fac.getAsbComponent()),"create")
        
        IAsbComponent_Cfg asb = new AsbComponent_Cfg(temp,"create");
        //IMessageHandler message = asb.getMessageHandler();
        //String str = (asb.getMessageHandler()).getValidator().getImpl();
        
        //System.out.println("The Impl is "+str); */
        
        searchforHandler(xobj);
        
        for (int j = 0; j < handlerList.size(); j++)
        {
            XmlObject xo = futil.StringToXmlObj((String) handlerList.get(j));
            boolean status = searchInHandler(xo, xobj1);
            if (status)
            {
                break;
            }
        }     
       
    }
    
    /* 
    private static void searchforHandler(XmlObject xo, XmlObject xom)
    {
        String query = "$this//ASBComponent/MessageHandlers/MessageHandler/@filter";
        System.out.println("The query is..." + query);
        XmlCursor xc = XMLUtils.getXmlCursor(xo, query);
        boolean found=false;
        //ArrayList list = null;
       
        while (xc.hasNextSelection())
        {
            xc.toNextSelection();
            //String c = xc.xmlText();
            //System.out.println("the xml text is.."+c);
            String temp = xc.getTextValue();
            //list.add(xc.toNextSelection());
            //System.out.println(temp);
            String[] tempr = temp.split("=");
            
            System.out.println(tempr[0]);
            System.out.println(tempr[1]);
            
            StringBuffer buf = new StringBuffer();
            buf.append("$this");
            buf.append(tempr[0]);
            System.out.println("New query is "+buf.toString());
            XmlCursor xcm = XMLUtils.getXmlCursor(xom,buf.toString());
            
            while (xcm.hasNextSelection())
            {
                xcm.toNextSelection();
                String a = xcm.getTextValue();
                String b = tempr[1].replaceAll("\"","");
                System.out.println("New matched string is "+a);
                System.out.println("New replaced string is "+b);
                if (a.trim().equalsIgnoreCase(b.trim()))
                {
                    System.out.println("Match found "+b);
                    found=true;
                   
                }
                break;
            }
            if (found)
            {
                
             break;   
            }
            
            buf.setLength(0);
            
            
        }
        
        
    } */
    
   
    
    
    private static void searchforHandler(XmlObject xo)
    {
        String query = "$this//ASBComponent/MessageHandlers/MessageHandler";           
        System.out.println("The query is..." + query);
        XmlCursor xc = XMLUtils.getXmlCursor(xo, query);   
        handlerList = new ArrayList();            
       
        while (xc!= null && xc.hasNextSelection())
        {              
            xc.toNextSelection();
            String temp = xc.xmlText();
            System.out.println(temp);
            handlerList.add(temp);               
        } 
        
        for (int i=0;i<handlerList.size();i++)
        {
            System.out.println((String)handlerList.get(i));
        }
        
    }
    
    private static boolean searchInHandler(XmlObject xo, XmlObject tsdl)
    {   
        boolean found=false;
        StringBuffer buf = new StringBuffer();
        buf.append("$this");
        
        String query = "$this//MessageHandler/@filter";           
        System.out.println("The query is..." + query);
        XmlCursor xc = XMLUtils.getXmlCursor(xo, query);           
        String[] filter = null;
       
        while (xc!= null && xc.hasNextSelection())
        {              
            xc.toNextSelection();
            String temp = xc.getTextValue();
            System.out.println(temp);
            filter = temp.split("=");  
           
            break;
        }       
        
        buf.append(filter[0]); // parasoft-suppress BD.EXCEPT.NP "test program "
        System.out.println("The query is..." + buf.toString());
        
        XmlCursor xcm = XMLUtils.getXmlCursor(tsdl,buf.toString());
        
        while (xcm != null && xcm.hasNextSelection())
        {
            
            xcm.toNextSelection();
            String a = xcm.getTextValue();
            String b = filter[1].replaceAll("\"","");
            System.out.println("New matched string is "+a);
            System.out.println("New replaced string is "+b);
            if (a.trim().equalsIgnoreCase(b.trim()))
            {
                System.out.println("Match found "+b);
                found=true;    
                break;
            }
            
        
        }
        
        if (found)
        {
            String query1 = "$this//MessageHandler/rule/@impl";
            System.out.println("The query is..." + query1);
            XmlCursor xc1 = XMLUtils.getXmlCursor(xo, query1);
            
            ruleList = new ArrayList();
            
            while (xc1.hasNextSelection())
            {              
                xc1.toNextSelection();
                String temp = xc1.getTextValue();
                System.out.println("Rule found " +temp);
                ruleList.add(temp);                  
            } 
            
            
            for (int i=0;i<ruleList.size();i++)
            {
                System.out.println((String)ruleList.get(i));
            }            
           
        }
        
        return found;
        
        
    }
    
    private static ArrayList handlerList;
    
    private static ArrayList ruleList;
    
 
 
    
}
