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
 * File Name:   FileUtils.java	
 * Description: Common file handler utilities
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Oct 2, 2007
 ***************************************************************************
 * @author:     rbadhwar
 * @version:    1.1
 * @date:       Aug 30, 2010 
 * Note : Updated to resolve jtest static analysis issues/optimizations and misc
 ****************************************************************************/
package com.twc.eis.lib.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.nio.Buffer;
import java.util.Properties;

import org.apache.xmlbeans.XmlObject;

public class FileUtils
{    
    public String readLocalFile(String fileName) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        String s;
        BufferedReader in = null;
        
        if (fileName == null)
        {
            throw new IOException("Input File is Invalid or NULL");
        } else
        {
            try
            {
                in = new BufferedReader(new FileReader(fileName));

                while ((s = in.readLine()) != null)
                {
                    buf.append(s);
                }
            } catch (IOException e)
            {
                throw new IOException(e.getMessage());
            } catch (Exception e)
            {
                throw new IOException(e.getMessage());
            } finally
            {
                if (in != null)
                {
                    in.close();
                }
            }
        }
        
        return buf.toString();
    }    
    
    public String ReadFileInClasspath(String filename) throws IOException
    {
        InputStream is = null;
        
        byte[] buffer = new byte[10240];
        
        int readBytes = 0;
        
        String buf  = null;        
        
        try
        {
            is = getClass().getClassLoader().getResourceAsStream(filename);
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            while ((readBytes = is.read(buffer)) != -1)
            {
                output.write(buffer, 0, readBytes);
            }
            
            byte[] finalOutput = output.toByteArray();
            
            buf = new String(finalOutput);
            
        } catch (IOException e)
        {
            throw new IOException(e.getMessage());
        } catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }finally
        {
            if (is != null)
            {
                is.close();
            }
            buffer = null;
        }
        
        return buf;
        
    }
    
    public InputStream ReadFileToStreamInClasspath(String filename) throws IOException
    {
        return (getClass().getClassLoader().getResourceAsStream(filename));
    }
    
    public XmlObject fileToXmlObj(String filename) throws Exception
    {
        String string = readLocalFile(filename);
        
        if (string == null)
        {
            string = ReadFileInClasspath(filename);
        }
        
        XmlObject xobj = StringToXmlObj(string);
        
        return xobj;
    }
    
    public static XmlObject StringToXmlObj(String string) throws Exception
    {
        XmlObject xo = XmlObject.Factory.parse(string);
        
        return xo;
    }
    
    public static Properties StringToProperties(String string) throws Exception
    {
        Properties props = new Properties();
        ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
        props.load(bais);
        return props;
    }
    
    public static StringBuffer readResource(String resource, ClassLoader cl)
    {
        InputStream is = null;
        StringBuffer buf = null;

        try
        {
            is = openResourceStream(resource, cl);
            buf = readResourceStream(is);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }

        return buf;
    }
    
    public static InputStream openResourceStream(String resource, ClassLoader cl)
    {
        InputStream is = null;

        if (resource != null)
        {
            is = (cl != null) ? cl.getResourceAsStream(resource) : null;

            if (is == null)
            {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            }
            if (is == null)
            {
                is = ClassLoader.getSystemResourceAsStream(resource);
            }
        }
        
        return is;
    }
    
    public static void copyStream(InputStream is, OutputStream os) throws IOException
    {
        byte[] buf = new byte[1024];
        int num = 0;
        
        while ((num = is.read(buf)) != -1)
        {
            os.write(buf, 0, num);
        }
        
        os.flush();
    }
    
    private static StringBuffer readResourceStream(InputStream is)
    {
        StringBuffer buffer = null;
        
        try
        {
            if (is != null)
            {
                buffer = new StringBuffer();
                BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
                String line = rdr.readLine();
                
                while (line != null)
                {
                    buffer.append(line);
                    line = rdr.readLine();
                }
                
                is.close();
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return buffer;
    }
    
    public static void main(String[] args)
    {
        //FileUtils fu = new FileUtils();  
        //System.out.println(fu.readResource("template.xml",null));       
    }
    
}
