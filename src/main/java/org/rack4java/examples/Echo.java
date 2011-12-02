package org.rack4java.examples;

import java.io.InputStream;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;

public class Echo implements Rack {
    @Override public RackResponse call(Context<Object> environment) throws Exception {
    	String contentLength = (String) environment.get(Rack.HTTP_ + "Content-Length");
    	long length = (null != contentLength) ? Long.parseLong(contentLength) : Long.MAX_VALUE;
    	return new RackResponse(200)
			.withHeader("Content-Type", (String) environment.get(Rack.HTTP_ + "Content-Type"))
    		.withBody((InputStream) environment.get(Rack.RACK_INPUT), length);
    }
}
