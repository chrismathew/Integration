package com.twc.eis.lib.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.twc.eis.asb.core.eventnotifier.IEventHandler;
import com.twc.eis.asb.core.eventnotifier.event.AsbEvent;


/**
 * <p> Dsb Log Event Handler which is used to initialize the writer,
 * dsb event queue and put the log messages in the dsb event queue </p>
 * 
 * @author Chris Mathew
 * @Task 561
 *
 */
 public class DsbLogEventHandler implements IEventHandler { 
	private DsbLogQueue queue = null;
	private DsbLogWriter dsbLogWriter2 = null;
	public DsbLogEventHandler(DsbLogWriter dsbLogWriter) {
		super();
		queue = new DsbLogQueue();
		dsbLogWriter2 = dsbLogWriter;
		dsbLogWriter2.setQueue(queue);
		ExecutorService execWriter = Executors.newFixedThreadPool(1);
		execWriter.execute(dsbLogWriter2);
		//execSvc.shutdown();

	}
	
	public DsbLogEventHandler(DsbLogWriter dsbLogWriter,Integer queueCapacity) {
		super();
		queue = new DsbLogQueue(queueCapacity);
		dsbLogWriter2 = dsbLogWriter;
		dsbLogWriter2.setQueue(queue);
		ExecutorService execWriter = Executors.newFixedThreadPool(1);
		execWriter.execute(dsbLogWriter2);
		//execSvc.shutdown();

	}

	
	public void onEvent(AsbEvent event) {
		try {
			queue.putAsbEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
