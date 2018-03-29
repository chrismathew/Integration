/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2010 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 ****************************************************************************
 * Department:  Identity Management
 *
 * File Name:   Debug  
 * Description: Generic Debug Class 
 * @author:     rbadhwar
 * @version:    1.0
 * @date:       July 24, 2010
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

import org.apache.log4j.Category;

public final class Debug
{
    private static Category               log       = Category.getInstance(Debug.class.getName());
    
    public static String getWhoCalledMe()
    {
        Throwable throwable = new Throwable("debug stack");
        StackTraceElement[] stEl = throwable.getStackTrace();
        StringBuffer result = new StringBuffer();

        result.append('[');
        if (stEl != null && stEl.length > 1)
        {
            result.append(stEl[2].getClassName());
            result.append('.');
            result.append(stEl[2].getMethodName());
            result.append("():");
            result.append(stEl[2].getLineNumber());
        }
        result.append("] ");

        return result.toString();
    }

    public static void debug(String debugString, Category logger)
    {

        String who = getWhoCalledMe();

        if (who != null) // parasoft-suppress BD.PB.CC "working as designed."
        {
            if (logger != null)
            {
                logger.debug(who + debugString);
            }
        } else
        {
            if (logger != null)
            {
                logger.debug(debugString);
            }

        }

    }

    public static void error(String debugString, Category logger)
    {

        String who = getWhoCalledMe();

        if (who != null) // parasoft-suppress BD.PB.CC "working as designed."
        {
            if (logger != null)
            {
                logger.error(who + debugString);
            }
        } else
        {
            if (logger != null)
            {
                logger.error(debugString);
            }

        }

    }

    public static void warn(String debugString, Category logger)
    {
        String who = getWhoCalledMe();

        if (who != null) // parasoft-suppress BD.PB.CC "working as designed."
        {
            if (logger != null)
            {
                logger.warn(who + debugString);
            }
        } else
        {
            if (logger != null)
            {
                logger.warn(debugString);
            }

        }

    }
    
    public static void info(String debugString, Category logger)
    {
        String who = getWhoCalledMe();

        if (who != null) // parasoft-suppress BD.PB.CC "working as designed."
        {
            if (logger != null)
            {
                logger.info(who + debugString);
            }
        } else
        {
            if (logger != null)
            {
                logger.info(debugString);
            }

        }

    }
    
    public static void main(String[] args) throws Exception
    {
        debug("Hi",log);        
    }    

}
