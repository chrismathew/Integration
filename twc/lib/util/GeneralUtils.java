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
 * File Name:   GeneralUtils.java   
 * Description: General Utilities 
 * @author:     rbadhwar
 * @version:    1.0
 * @date:       Sep 24, 2007
 *
 ****************************************************************************/

package com.twc.eis.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils
{
    public GeneralUtils()
    {
    }
    
    // Remove "ALL" Superfluous whitespaces in source string
    public String ntrim(String source)
    {
        return atrim(ltrim(rtrim(source)));
    }
    
    private String ltrim(String source)
    {
        return source.replaceAll("^\\s+", "");
    }
    
    private String rtrim(String source)
    {
        return source.replaceAll("\\s+$", "");
    }
    
    private String atrim(String source)
    {
        return source.replaceAll("\\b\\s{1,}\\b", "");
    }
    
    public String formatInputDate(Date inputDate, String inputFormat) throws Exception
    {
        if (inputDate.equals(null))
        {
            throw new Exception("Invalid Input Date");
        } else if (inputFormat.equals(null) || inputFormat.length() == 0)
        {
            throw new Exception("Invalid Input Date format");
        }
        
        DateFormat formatter = null;
        String datenewformat = "";
        try
        {
            formatter = new SimpleDateFormat(inputFormat);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            datenewformat = formatter.format(inputDate);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return datenewformat;
    }
    
    public void ReadnLoad(String filename) throws IOException
    {
        InputStream is = null;
        byte[] buffer = new byte[2096];
        
        try
        {
            is = getClass().getClassLoader().getResourceAsStream(filename);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        if (is == null)
        {
            throw new IOException("InputStream from input file = " +filename+ " is NULL");
        }
        
        int readBytes = 0;
        byte[] finalOutput = null;
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        try
        {
            while ((readBytes = is.read(buffer)) != -1)
            {
                output.write(buffer, 0, readBytes);
            }
            finalOutput = output.toByteArray();
        } catch (IOException e)
        {
            throw new IOException(e.getMessage());
        } catch (Exception e)
        {
            throw new IOException(e.getMessage());
        } finally
        {
            if (is != null)
            {
                is.close();
            }
            
            buffer = null;
            
            System.out.println(new String(finalOutput));

        }       
        
        
    }
    
    public static String getLocalDsbHost() throws Exception
    {
        String host = null;
        InetAddress hostname = InetAddress.getLocalHost();
        host = hostname.getCanonicalHostName();
        
        if (host == null)
        {
            System.err.println("The Channel could not find the name of the host it's running on.");
            host = "localhost";
        }
        
        return host;
        
    }
}
