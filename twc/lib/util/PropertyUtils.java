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
 * File Name:   PropertyUtils.java	
 * Description: Utilities for handling java properties (files)
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Oct 8, 2007
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

import java.io.*;
import java.util.*;

public final class PropertyUtils
{
    
    public PropertyUtils()
    {
    }
    
    public static Properties readProperties(String s, ClassLoader classloader)
    {
        InputStream inputstream = openResourceStream(s, classloader);
        return load(inputstream);
    }
    
    public static StringBuffer readResource(String s, ClassLoader classloader)
    {
        InputStream inputstream = openResourceStream(s, classloader);
        StringBuffer buf = null;
        
        try
        {
            buf = readResourceStream(inputstream);
        } catch (Exception e)
        {           
            e.printStackTrace();
        }
        
        return buf;
    }
    
    public static synchronized Properties StringToProperties(String string) throws Exception
    {
        Properties props = new Properties();
        ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
        props.load(bais);
        return props;
    }
    
    public static InputStream openResourceStream(String s, ClassLoader classloader)
    {
        InputStream inputstream = null;
        if (classloader != null)
            inputstream = classloader.getResourceAsStream(s);
        if (inputstream == null)
        {
            Thread thread = Thread.currentThread();
            inputstream = thread.getContextClassLoader().getResourceAsStream(s);
        }
        if (inputstream == null)
            inputstream = ClassLoader.getSystemResourceAsStream(s);
        return inputstream;
    }
    
    public static void copyStream(InputStream inputstream, OutputStream outputstream)
    throws IOException
    {
        byte abyte0[] = new byte[1024];
        for (int i = 0; (i = inputstream.read(abyte0)) != -1;)
            outputstream.write(abyte0, 0, i);
        
        outputstream.flush();
    }
    
    public static StringBuffer readResourceStream(InputStream inputstream) throws IOException, Exception
    {
        StringBuffer stringbuffer;
        BufferedReader bufferedreader;
        stringbuffer = null;
        bufferedreader = null;

        if (inputstream != null)
        {
            stringbuffer = new StringBuffer();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            bufferedreader = new BufferedReader(inputstreamreader);
            
            try
            {
                for (String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
                {
                    stringbuffer.append(s);
                }
            } catch (IOException e)
            {
                throw new IOException(e.getMessage());
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            }finally
            {
                if (inputstream != null)
                {
                    try
                    {
                        inputstream.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (bufferedreader != null)
                {
                    bufferedreader.close();
                }
            }

        }
        
        
      /*  try
        {
            if (bufferedreader != null)
                bufferedreader.close();
        } catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }*/

        return stringbuffer;
    }
    
    private static Properties load(InputStream inputstream)
    {
        Properties properties = null;
        if (inputstream != null)
        {
            try
            {
                properties = new Properties();
                properties.load(inputstream);
            } catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
            try
            {
                inputstream.close();
            } catch (IOException ioexception1)
            {
                ioexception1.printStackTrace();
            }
            if (!properties.isEmpty())
            {
                @SuppressWarnings("unused") Object obj = null;
                @SuppressWarnings("unused") Object obj1 = null;
                Set set = properties.keySet();
                Iterator iterator = set == null ? null : set.iterator();
                
                if (iterator != null)
                {
                    do
                    {
                        if (!iterator.hasNext())
                            break;
                        String s = (String) iterator.next();
                        String s1 = properties.getProperty(s);
                        if (s1 != null)
                            properties.setProperty(s, s1.trim());
                    } while (true);
                }
            }
        }
        return properties;
    }
}
