package test;

import java.io.FileInputStream;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;
import org.rack4java.context.MapContext;
import org.rack4java.examples.FileServer;
import org.rack4java.utils.StreamHelper;

public class ExampleTest extends TestCase {
	
	Context<Object> env;
	
	public void setUp() {
		env = new MapContext<Object>();
	}
	
    public void testStringBody() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "text/plain")
					.withBody("Wibble");
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("text/plain", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals("Wibble", ret.getString());
    }
	
    public void testStringToByteConversion() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "text/xml")
					.withBody("<whatever/>");
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("text/xml", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals(11, ret.getBytes().length);
    }
	
    public void testByteToStringConversion() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody("picture".getBytes());
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("image/png", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals(7, ret.getBytes().length);
    	assertEquals("picture", ret.getString());
    }
	
    public void testByteToStringConversionWithCharset() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody(new byte[] {112, 105, 99, 116, -31, -69, -96, 114, 101}, Charset.forName("UTF-8"));
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("image/png", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals(9, ret.getBytes().length);
    	assertEquals("pict\u1ee0re", ret.getString());
    	assertEquals(7, ret.getString().length());
    }
	
    public void testFileBody() throws Exception {
    	env.put(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFile());
    	assertEquals("<p>Hello!</p>", StreamHelper.readAsString(new FileInputStream(ret.getFile())));
    }
	
    public void testFileToByteConversion() throws Exception {
    	env.put(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFile());
    	assertEquals(13, ret.getBytes().length);
    }
	
    public void testFileToStringConversion() throws Exception {
    	env.put(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFile());
    	assertEquals("<p>Hello!</p>", ret.getString());
    }
	
    public void testFileNotFound() throws Exception {
    	env.put(Rack.PATH_INFO, "missing.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(404, ret.getStatus());
    	assertEquals("text/plain", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    }
}
