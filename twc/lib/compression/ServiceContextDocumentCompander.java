/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 **************************************************************************** 
 *
 * File Name:   ServiceContextDocumentCompander.java	
 * Description: Service Context Document Compressor and Expander.
 * 
 * @author:     Blaize D'souza
 * @version:    1.0
 * @date:	March 11, 2012
 *
 ****************************************************************************/

package com.twc.eis.lib.compression;


public class ServiceContextDocumentCompander {

	
	public byte[] compressDocument(CompanderMethod method, byte[] content) {
		
		return method.compress(content);
	}
	
	
	public byte[] expandDocument(CompanderMethod method, byte[] content) {
	
		return method.expand(content);
	}
	
}
