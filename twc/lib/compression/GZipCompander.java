/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 **************************************************************************** 
 *
 * File Name:   GZipCompander.java	
 * Description: GZIP implementation of the CompanderMethod interface.
 * 
 * @author:     Blaize D'souza
 * @version:    1.0
 * @date:	March 11, 2012
 *
 ****************************************************************************/

package com.twc.eis.lib.compression;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.io.IOUtils;


public class GZipCompander implements CompanderMethod {
	
	public byte[] compress(byte[] content) {
		
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
        	
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        }
        catch(IOException e) {
        	
            throw new RuntimeException(e);
        }        
        
        return byteArrayOutputStream.toByteArray();
	}
	
	
	
	public byte[] expand(byte[] content) {
		
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
        	
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(content)), out);
            
        }
        catch(IOException e) {
        	
            throw new RuntimeException(e);
        }
        
        return out.toByteArray();
	}
}

