package org.rack4java.examples;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.RackResponse;

public class Echo implements Rack {
    @Override public Context<String> call(Context<String> environment) throws Exception {
    	return new RackResponse(200)
			.withHeader("Content-Type", environment.get(Rack.HTTP_ + "Content-Type"))
    		.withBody((RackBody) environment.getObject(Rack.RACK_INPUT));
    }
}
