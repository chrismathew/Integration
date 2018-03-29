package com.twc.eis.lib.event;

//import java.util.EventObject;

public abstract class aEvent {
	

	public static final char EVENT_TYPE_ERROR 			= 1;
	public static final char EVENT_TYPE_NOTIFICATION 	= 2; // DEFAULT TYPE IF NONE SET

	public static char EVENT_LEVEL_LOW 		= 0;
	public static char EVENT_LEVEL_MEDIUM 	= 5; // DEFAULT LEVEL IF NONE SET
	public static char EVENT_LEVEL_HIGH 	= 10;

	private char type = EVENT_TYPE_NOTIFICATION;
	
	private char level = EVENT_LEVEL_MEDIUM;
	
	private String message = null;
	
	private Object payload = null;
		
	private Object source = null;

	public aEvent() {}
	
	
	public aEvent(Object source) {
		
		setSource(source);
				
	}
		
	protected abstract void dispatch ( Object receiver );
	
	
	public Object getSource() {
		return source;
	}

	public aEvent setSource(Object source) {
		this.source = source;
		return this;
	}


	public char getLevel() {
		return level;
	}


	public void setLevel(char level) {
		this.level = level;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public Object getPayload() {
		return payload;
	}


	public void setPayload(Object payload) {
		this.payload = payload;
	}


	public char getType() {
		return type;
	}


	public void setType(char type) {
		this.type = type;
	}

	

}
