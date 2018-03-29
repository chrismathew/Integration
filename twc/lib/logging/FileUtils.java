package com.twc.eis.lib.logging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

public class FileUtils {
	
	public static String getFileContent(File file) throws IOException {
		FileReader in = new FileReader(file);
		StringWriter w = new StringWriter();
		char buffer[] = new char[2048];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			w.write(buffer, 0, n);
		}
		w.flush();
		in.close();
		return w.toString();
	}

}
