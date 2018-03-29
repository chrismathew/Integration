/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 **************************************************************************** 
 *
 * File Name:   CompressServiceContextUsingGZip.java	
 * Description: GZipCompander unit test.
 * 
 * @author:     Blaize D'souza
 * @version:    1.0
 * @date:	March 11, 2012
 *
 ****************************************************************************/

package com.twc.eis.lib.compression;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class CompressServiceContextUsingGZip {

	  
	private static String readFileAsString(String filePath) throws java.io.IOException {
		  
		byte[] buffer = new byte[(int) new File(filePath).length()];
		   
		BufferedInputStream f = null;
		    
		try {
		    	
			f = new BufferedInputStream(new FileInputStream(filePath));
		    f.read(buffer);
		}
		finally {
		    	
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		    
		return new String(buffer);		  
	}		
	  
	  
	public static void main(String[] args)  throws Exception {
		
		long startTime, endTime;
		
		ServiceContextDocumentCompander scCompander = new ServiceContextDocumentCompander();
		
		CompanderMethod gzip = new GZipCompander();
		
		System.out.println("Compressing Service context using GZIP.....");
		
		String inputFileStr = readFileAsString("test-sc-7.xml");
		 
		byte[] inputFileStrBytes = inputFileStr.getBytes();
		 
		System.out.println("Size of inputFileStrBytes[] = " + inputFileStrBytes.length);
		
		startTime = System.currentTimeMillis();

		byte[] compressedBytes = scCompander.compressDocument(gzip, inputFileStrBytes);
		
		endTime = System.currentTimeMillis();
		 
		System.out.println("Size of compressedBytes[] = " + compressedBytes.length);
		
		System.out.println("Time taken to compress " + inputFileStrBytes.length + " bytes = " + (endTime - startTime) + " msecs.");
		
		startTime = System.currentTimeMillis();
		
		byte[] uncompressedBytes = scCompander.expandDocument(gzip, compressedBytes);
		
		endTime = System.currentTimeMillis();
		 
		System.out.println("Size of uncompressedBytes[] = " + uncompressedBytes.length);
		
		System.out.println("Time taken to expand " + compressedBytes.length + " bytes = " + (endTime - startTime) + " msecs.");
		 
		String destStr = new String(uncompressedBytes);
		 
	}

}
