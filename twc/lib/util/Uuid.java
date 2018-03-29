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
 * File Name:   Uuid.java    
 * Description: Generates an UUID
 * @author:     rbadhwar
 * @version:    1.0
 * @date:       Oct 10, 2007
 *
 ****************************************************************************/

package com.twc.eis.lib.util;

import java.util.Random;

public class Uuid
{

    public synchronized static String generateUuid()
    {
        String temp = null;
        long currTime = System.currentTimeMillis();

        if (currTime <= prevTime)
        {
            currTime = prevTime + 1;
        }
        prevTime = currTime;

        try
        {
            temp = Long.toHexString(UUID_SHIFT | currTime);
        } catch (Exception e)
        {
            Random secureRandom = new Random();
            temp = Long.toHexString(secureRandom.nextLong() | currTime);
        }

        System.out.println("Returning Unique UUID..." + temp);

        return temp;
    }

    private static final long UUID_SHIFT = 'U' << 56;

    private static long       prevTime;
    
    public static void main(String argv[])
    {
        generateUuid();
    }
}
