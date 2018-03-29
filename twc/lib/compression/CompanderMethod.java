/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 **************************************************************************** 
 *
 * File Name:   CompanderMethod.java	
 * Description: CompanderMethod Interface for the various Compressor/Expander
 *              implementations.
 * 
 * @author:     Blaize D'souza
 * @version:    1.0
 * @date:	March 11, 2012
 *
 ****************************************************************************/


package com.twc.eis.lib.compression;



public interface CompanderMethod {

	public byte[] compress(byte[] content);
	
	public byte[] expand(byte[] content);
}
