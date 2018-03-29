/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2009 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 ****************************************************************************
 * Department:  Identity Management
 *
 * File Name:   DsbHttpUtils.java	
 * Description: DSB HTTP Utility method(s) 
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    July 23rd, 2009
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

public final class DsbHttpUtils extends HttpUtils
{    
    public static String requestToString(HttpServletRequest request) throws IOException, Exception
    {
        if (request != null)
        {
            int readBytes = -1;
            int lengthOfBuffer = request.getContentLength();
            InputStream input = request.getInputStream();

            byte[] buffer = new byte[lengthOfBuffer];
            ByteArrayOutputStream output = new ByteArrayOutputStream(lengthOfBuffer);
            while ((readBytes = input.read(buffer, 0, lengthOfBuffer)) != -1)
            {
                output.write(buffer, 0, readBytes);
            }
            byte[] finalOutput = output.toByteArray();
            return (new String(finalOutput, 0, lengthOfBuffer));
        } else
        {
            return null;
        }

    }
    
}
