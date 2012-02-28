package org.rack4java.examples;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;

public class HelloWorld implements Rack {
    public Context<String> call(Context<String> input) {
        return new RackResponse(200)
        	.withHeader("Content-Type", "text/plain")
        	.withBody("Hello World");
    }
}
