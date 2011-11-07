package org.rack4java.examples;

import java.io.InputStream;
import java.util.Map;

import org.rack4java.Rack;
import org.rack4java.RackResponse;
import org.rack4java.utils.StreamHelper;

public class Echo implements Rack {
    @Override public RackResponse call(Map<String, Object> environment) throws Exception {
    	RackResponse ret = new RackResponse(200, 
    			StreamHelper.readAsString((InputStream) environment.get(Rack.RACK_INPUT)));
    	
        String type = (String) environment.get(Rack.HTTP_ + "Content-Type");
        if (null != type) {
        	ret.addHeader("Content-Type", type);
        }
        
        return ret;
    }
}
