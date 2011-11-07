package test;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.rack4java.Rack;
import org.rack4java.RackResponse;
import org.rack4java.examples.FileServer;
import org.rack4java.utils.StreamHelper;

public class ExampleTest extends TestCase {
	
	Map<String, Object> env;
	
	public void setUp() {
		env = new HashMap<String, Object>();
	}
	
    public void testStringBody() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Map<String, Object> environment) throws Exception {
				return new RackResponse(200, "Wibble", "Content-Type", "text/plain");
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("text/plain", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals("Wibble", ret.getString());
    }
	
    public void testStringToByteConversion() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Map<String, Object> environment) throws Exception {
				return new RackResponse(200, "<whatever/>", "Content-Type", "text/xml");
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("text/xml", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFile());
    	assertEquals(11, ret.getBytes().length);
    }
	
    public void testByteToStringConversion() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Map<String, Object> environment) throws Exception {
				return new RackResponse(200, "picture".getBytes(), "Content-Type", "image/png");
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
			@Override public RackResponse call(Map<String, Object> environment) throws Exception {
				return new RackResponse(200, new byte[] {112, 105, 99, 116, -31, -69, -96, 114, 101}, Charset.forName("UTF-8"), "Content-Type", "image/png");
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
