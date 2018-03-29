package gov.hhs.cms.base.common.util;


import org.apache.log4j.Logger;

public class LogUtils {
	
	static public void dumpObjectToLog(Object obj, Logger log) {
		try {
			if(obj != null) {
				log.debug(obj.toString().replaceAll(",", "\n").replaceAll("\\[", "\n[").replaceAll("]", "\n]"));
			}					
		} catch (Exception e) {
			// just ignore
		}
	}
	
	static public void dumpObjectsToLog(Object[] objs, Logger log) {
		for(Object currentObj : objs) {
			dumpObjectToLog(currentObj, log);
		}
	}

	public static void writeToFile(String writeThis, String inThisLocation) {
		java.io.BufferedWriter bufferW = null;
		try {
			bufferW = new java.io.BufferedWriter(new java.io.FileWriter(inThisLocation));
			bufferW.write(writeThis);
			bufferW.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bufferW != null) {
				try {
					bufferW.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
