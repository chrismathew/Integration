package com.twc.eis.lib.general;

import java.util.Map;
import java.util.HashMap;


public class ProcessTimer {

	private static int nextID = 0;
	private static Map nameList = null;
	private static Map idList = null;


	private boolean quiet = false;

	public ProcessTimer(  ) {
	}
	
	private static synchronized Map getIDList() {
		
		if (idList == null) {
			idList= new HashMap();
		}
		
		return idList;
	}
	
	private static synchronized Map getNameList() {
		
		if (nameList == null) {
			nameList= new HashMap();
		}
		
		return nameList;
	}
	public boolean getQuiet() { return quiet; }
	public static void main(String[] args) {

		ProcessTimer timer = new ProcessTimer();

		int id = timer.startTiming(timer, "Test Loop");

		for (int i = 0; i < 1000; i++) {

			System.out.println(i);
		}

		timer.stopTiming(id);

		

	}
	private synchronized int nextID() {

		return ++nextID;

	}

	public void setQuiet(boolean quiet) { this.quiet = quiet; }
	public int startTiming() {
		return startTiming(null, ""+System.currentTimeMillis());		
	}
	public int startTiming(Object client) {
		return startTiming(client, "");
	}
	public int startTiming(Object client, String processName) {

		long currTime = System.currentTimeMillis();

		String uniqueName = toString(client, processName);
		
		TimingData data = (TimingData)getNameList().get( uniqueName );

		if (data == null) {

			data = new TimingData();
			data.id 	= nextID();
			data.name	= processName;
			
		}

		data.startTime = currTime;

		getNameList().put( uniqueName, data );
		getIDList().put( ""+data.id, data );

		return data.id;
	}
	public TimingData stopTiming(int processID) {

		long currTime = System.currentTimeMillis();

		TimingData data = (TimingData)getIDList().get(""+processID);

		if (data != null) {

			getIDList().remove(""+data.id);
			getNameList().remove(data.name);

			data.stopTime 		= currTime;
			data.elapsedTime	= data.stopTime - data.startTime; 			

			String info = 
				"Process Name: " + data.name + "\n" + 
				"  Start Time: " + data.startTime + "\n" + 
				"    End Time: " + data.stopTime + "\n" + 
				"Elapsed Time: " + data.elapsedTime + " ms.";

			if (! getQuiet()) {
				System.out.println(info);
			}


		}

		return data;

	}
	private static String toString(Object client, String name) {
		return (client == null ? "" : client.toString()) + name;
	}
}