package com.twc.eis.lib.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides a deep copy of an object without needing every copied object to
 * implement the clonable() interface.
 * 
 * @author takadiri
 * 
 */
public class DeepCopy {

	public static final Object copy(Object object) throws Exception {
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();		
		ObjectOutputStream out = new ObjectOutputStream( b );
		
		out.writeObject(object);
		
		out.close();

		ByteArrayInputStream bi = new ByteArrayInputStream(b.toByteArray());
		ObjectInputStream in = new ObjectInputStream(bi);		
		
		Object clone = in.readObject();
		
		return clone;
	}

}
