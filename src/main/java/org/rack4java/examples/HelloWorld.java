package org.rack4java.examples;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;

public class HelloWorld implements Rack {
    public RackResponse call(Context<Object> input) {
        return new RackResponse(200, 
        		"Hello World", 
        		"Content-Type", "text/plain");
    }
}
