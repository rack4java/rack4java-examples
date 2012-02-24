package org.rack4java.examples;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;
import org.rack4java.utils.StreamHelper;

public class Echo implements Rack {
    @Override public RackResponse call(Context<Object> environment) throws Exception {
    	return new RackResponse(200)
			.withHeader("Content-Type", (String) environment.get(Rack.HTTP_ + "Content-Type"))
    		.withBody(StreamHelper.readRequestBodyAsBytes(environment));
    }
}
