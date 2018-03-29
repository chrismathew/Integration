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
 * File Name:   IIdmSha1.java	
 * Description: Sha1 Interface for Identity Management
 * @author:     rbadhwar
 * @version:    1.0
 * @date:	    Aug 21, 2007
 *
 ****************************************************************************/
package com.twc.eis.lib.util;

public interface IIdmSha1
{

    public abstract void shaUpdateAsc(String input) throws Exception;

    public abstract void shaInit();

    public abstract void shaFinal();

    public abstract String shaDigestPrint();

}
