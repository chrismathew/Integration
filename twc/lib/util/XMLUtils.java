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
 * File Name:   XMLUtils.java	
 * Description: Common XML processing utilities
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Sep 24, 2007
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

public class XMLUtils
{

    public XMLUtils()
    {
    }

    @SuppressWarnings("unchecked")
    public static XmlObject buildXmlBean(XmlCursor xmlcursor, String s)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Class class1 = Class.forName((new StringBuilder()).append(s).append("$Factory").toString());
        Method method = class1.getMethod("parse", new Class[] {String.class});
        return (XmlObject)method.invoke(class1, new Object[] {
            xmlcursor.xmlText()
        });
    }

    @SuppressWarnings("unchecked")
    public static XmlObject newXmlBean(String s)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Class class1 = Class.forName((new StringBuilder()).append(s).append("$Factory").toString());
        Method method = class1.getMethod("newInstance", new Class[0]);
        return (XmlObject)method.invoke(class1, new Object[0]);
    }

    public static XmlObject newXmlDocument(String s)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        return newXmlBean((new StringBuilder()).append(s).append("Document").toString());
    }

    public static void insertChildren(XmlObject xmlobject, XmlObject axmlobject[])
    {
        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            xmlcursor.toFirstContentToken();
            /*
             * Object obj = null; Object obj1 = null;
             */
            if (axmlobject != null && axmlobject.length > 0)
            {
                for (int i = 0; i < axmlobject.length; i++)
                {
                    XmlObject xmlobject1 = axmlobject[i];
                    XmlCursor xmlcursor1 = xmlobject1.newCursor();
                    xmlcursor1.copyXmlContents(xmlcursor);
                    xmlcursor1.dispose();
                }
            }

            xmlcursor.dispose();
        }
    }

    
    public static XmlObject getAnytypeXmlBean(XmlObject xmlobject, String s)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        XmlObject xmlobject1 = null;

        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            org.apache.xmlbeans.XmlCursor.TokenType tokentype = toFirstContentToken(xmlcursor);

            if (tokentype == org.apache.xmlbeans.XmlCursor.TokenType.START)
                xmlobject1 = buildXmlBean(xmlcursor, s);
            xmlcursor.dispose();
        }
        
        return xmlobject1;
    }

    public static boolean findQName(XmlObject xmlobject, QName qname)
    {
        if(xmlobject == null || qname == null)
        {
            return false;
        } else
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("declare namespace twc=");
            stringbuffer.append((new StringBuilder()).append('\'').append(qname.getNamespaceURI()).append('\'').toString());
            stringbuffer.append((new StringBuilder()).append(" .//twc:").append(qname.getLocalPart()).toString());
            xmlcursor.selectPath(stringbuffer.toString());
            xmlcursor.toNextSelection();
            boolean flag = qname.equals(xmlcursor.getName());
            xmlcursor.dispose();
            return flag;
        }
    }

    public static void insertXbeanIntoXsAny(XmlObject xmlobject, XmlObject xobj)
    {
        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xobj.newCursor();
            XmlCursor xmlcursor1 = xmlobject.newCursor();
            xmlcursor.toEndToken();
            xmlcursor1.toFirstContentToken();
            xmlcursor1.copyXml(xmlcursor);
            xmlcursor.dispose();
            xmlcursor1.dispose();
        }
    }

    public static XmlObject removeXmlBean(XmlObject xmlobject, String s, String s1, String s2)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        XmlObject xmlobject1 = null;

        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("declare namespace twc=");
            stringbuffer.append((new StringBuilder()).append('\'').append(s).append('\'')
                    .toString());
            stringbuffer.append((new StringBuilder()).append(" .//twc:").append(s1).toString());
            xmlcursor.selectPath(stringbuffer.toString());
            xmlcursor.toNextSelection();
            xmlobject1 = buildXmlBean(xmlcursor, s2);
            xmlcursor.removeXml();
            xmlcursor.dispose();
        }
        
        return xmlobject1;
    }

    public static XmlObject getXmlBean(XmlObject xmlobject, String s, String s1, String s2)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        XmlObject xmlobject1 = null;

        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("declare namespace twc=");
            stringbuffer.append((new StringBuilder()).append('\'').append(s).append('\'')
                    .toString());
            stringbuffer.append((new StringBuilder()).append(" .//twc:").append(s1).toString());
            xmlcursor.selectPath(stringbuffer.toString());
            xmlcursor.toNextSelection();
            xmlobject1 = buildXmlBean(xmlcursor, s2);
            xmlcursor.dispose();
        }
        
        return xmlobject1;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList getXmlBeans(XmlObject xmlobject, String s, String s1, String s2)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        ArrayList arraylist = null;

        if (xmlobject != null)
        {
            arraylist = new ArrayList();
            XmlCursor xmlcursor = xmlobject.newCursor();
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("declare namespace twc=");
            stringbuffer.append((new StringBuilder()).append('\'').append(s).append('\'')
                    .toString());
            stringbuffer.append((new StringBuilder()).append(" .//twc:").append(s1).toString());
            xmlcursor.selectPath(stringbuffer.toString());
            do
            {
                xmlcursor.toNextSelection();
                arraylist.add(buildXmlBean(xmlcursor, s2));
            } while (xmlcursor.hasNextSelection());
            xmlcursor.dispose();
        }

        return arraylist;
    }

    public static Node toDomNode(XmlObject xmlobject, String namespace, String xpath)
    {
        Node node = null;

        if (xmlobject != null)
        {
            XmlCursor xmlcursor = xmlobject.newCursor();
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("declare namespace twc=");
            stringbuffer.append((new StringBuilder()).append('\'').append(namespace).append('\'')
                    .toString());
            stringbuffer.append((new StringBuilder()).append(" .//twc:").append(xpath).toString());
            xmlcursor.selectPath(stringbuffer.toString());
            xmlcursor.toNextSelection();
            node = xmlcursor.newDomNode();
            xmlcursor.dispose();
        }

        return node;
    }
    
    public static XmlCursor getXmlCursor(XmlObject xmlobject, String namespace, String xpath)
    {
        XmlCursor xmlcursor = null;

        if (xmlobject != null)
        {
            xmlcursor = xmlobject.newCursor();
            if (xmlcursor != null)
            {
                StringBuffer stringbuffer = new StringBuffer();
                stringbuffer.append("declare namespace twc=");
                stringbuffer.append((new StringBuilder()).append('\'').append(namespace).append(
                        '\'').toString());
                stringbuffer.append((new StringBuilder()).append(xpath).toString());

                xmlcursor.selectPath(stringbuffer.toString());
            }
        }

        return xmlcursor;
    }
    
    public static XmlCursor getXmlCursor(XmlObject xmlobject, String xpath)
    {
        XmlCursor xmlcursor = null;

        if (xmlobject != null)
        {
            xmlcursor = xmlobject.newCursor();

            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append((new StringBuilder()).append(xpath).toString());

            xmlcursor.selectPath(stringbuffer.toString());
        }

        return xmlcursor;
    }
    
    public static XmlCursor execXpath(XmlObject xmlobject, String xpath)
    {
        XmlCursor xmlcursor = null;

        if (xmlobject != null)
        {
            xmlcursor = xmlobject.newCursor();

            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append((new StringBuilder()).append(xpath).toString());

            xmlcursor.execQuery(stringbuffer.toString());
        }
        
        return xmlcursor;
    }

    private static org.apache.xmlbeans.XmlCursor.TokenType toFirstContentToken(XmlCursor xmlcursor)
    {
        org.apache.xmlbeans.XmlCursor.TokenType tokentype = null;

        if (xmlcursor != null)
        {
            tokentype = xmlcursor.toFirstContentToken();
            if (tokentype != org.apache.xmlbeans.XmlCursor.TokenType.START)
                do
                {
                    if (!xmlcursor.hasNextToken())
                        break;
                    tokentype = xmlcursor.toNextToken();
                } while (tokentype != org.apache.xmlbeans.XmlCursor.TokenType.START);
        }
        
        return tokentype;
    }  
    
}
