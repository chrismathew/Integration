/****************************************************************************
 * TIME WARNER CABLE, INC. CONFIDENTIAL INFORMATION
 *
 * Copyright (c) 2007 Time Warner Cable, Inc.  All Rights Reserved.
 * Unauthorized reproduction, transmission, or distribution of
 * this software is a violation of applicable laws.
 *
 **************************************************************************** 
 *
 * File Name:   ZLibCompander.java	
 * Description: ZLIB implementation of the CompanderMethod interface.
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


public class ZLibCompander implements CompanderMethod {
	
	
	public byte[] compress(byte[] content) {
		
		// Create the compressor with highest level of compression
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		
		// Give the compressor the data to compress
		compressor.setInput(content);
		compressor.finish();
		
		// Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the original because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream(content.length);
		
		// Compress the data
		byte[] buf = new byte[1024];
		
		while (!compressor.finished()) {
			
		    int count = compressor.deflate(buf);
		    bos.write(buf, 0, count);
		}
		try {
		    bos.close();
		}
		catch (IOException e) { }		
		
		// Get the compressed data
		byte[] compressedData = bos.toByteArray();		
		
		return compressedData;
	}
	
	
	public byte[] expand(byte[] content) {
		
		// Create the decompressor and give it the data to compress
		Inflater decompressor = new Inflater();
		decompressor.setInput(content);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(content.length);

		// Decompress the data
		byte[] buf = new byte[1024];
		
		while (!decompressor.finished()) {
			
		    try {
		    	
		        int count = decompressor.inflate(buf);
		        bos.write(buf, 0, count);
		    }
		    catch (DataFormatException e) {  }
		}
		try {			
		    bos.close();
		}
		catch (IOException e) { }

		// Get the decompressed data
		byte[] decompressedData = bos.toByteArray();		
		
		return decompressedData;
	}

}
