package com.twc.eis.lib.logging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twc.eis.asb.core.eventnotifier.event.AsbEvent;

/**
 * *
 * <p>
 * Log Queue which receives the log messages from the ENS Topic.
 * </p>
 * 
 * @author Chris Mathew
 * @Task 559
 * 
 */
public class DsbLogQueue {

	private BlockingQueue<AsbEvent> dsbLogQueue = null;
	private ArrayBlockingQueue<AsbEvent> dsbArrayLogQueue = null;
    
	private static final int EVENT_QUEUE_TIMEOUT_VALUE = 1; // 1 milliseconds
	private static final int DEFAULT_EVENT_QUEUE_SIZE = 200000;

	public DsbLogQueue() {

		dsbLogQueue = new LinkedBlockingQueue<AsbEvent>();
		dsbArrayLogQueue = new ArrayBlockingQueue<AsbEvent>(DEFAULT_EVENT_QUEUE_SIZE);
	}

	public DsbLogQueue(int capacity) {

		dsbLogQueue = new LinkedBlockingQueue<AsbEvent>(capacity);
		dsbArrayLogQueue = new ArrayBlockingQueue<AsbEvent>(capacity);
	}
	/**
	 * <p> API to get the log messages from the queue. </p>
	 * 
	 * @return AsbEvent object
	 * @throws Exception
	 */
	public AsbEvent getAsbEvent() throws Exception {
		AsbEvent asbEvent = null;
		try {
			asbEvent = dsbArrayLogQueue.take(); //dsbLogQueue.take(); // blocks if empty
		} catch (Exception ie) {
			System.out.println("ERROR IN RECEIVING MSGS TO THE QUEUE");
			//ie.printStackTrace();
			
			//throw new Exception(ie);
		}
		return asbEvent;
	}
	
	/**
	 * <p> API to put the log messages to the queue. </p>
	 * 
	 * @param asbEvent
	 * @return true or false
	 * @throws Exception
	 */
	public boolean putAsbEvent(AsbEvent asbEvent) throws Exception {
		boolean eventSuccess = false;
		try {
			dsbArrayLogQueue.put(asbEvent);
			//eventSuccess = dsbLogQueue.offer(asbEvent,
				//	EVENT_QUEUE_TIMEOUT_VALUE, TimeUnit.MILLISECONDS);

		} catch (Exception ie) {
			//throw new Exception(ie);
			System.out.println("ERROR IN SENDING MSGS TO THE QUEUE");
		}
		return eventSuccess;
	}

}
