package gov.hhs.cms.base.common.util;

import gov.hhs.cms.base.common.util.XSDDateAdaptor;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class DateTimeSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider prov) throws IOException,
			JsonProcessingException {
		gen.writeString(XSDDateAdaptor.printDateTime(value));
	}

}
