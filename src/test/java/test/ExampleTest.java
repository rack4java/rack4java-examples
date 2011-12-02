package test;

import java.io.FileInputStream;
import java.io.IOException;
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
	
	private String getBodyAsString(RackResponse response, Charset charset) throws IOException {
		return new String(response.getBodyAsBytes(), charset);
	}
	
	private String getBodyAsString(RackResponse response) throws IOException {
		return new String(response.getBodyAsBytes());
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
    	assertNull(ret.getFileBody());
    	assertEquals("Wibble", getBodyAsString(ret));
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
    	assertNull(ret.getFileBody());
    	assertEquals(11, ret.getBodyLength());
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
    	assertNull(ret.getFileBody());
    	assertEquals(7, ret.getBodyLength());
    	assertEquals("picture", getBodyAsString(ret));
    }
	
    public void testByteToStringConversionWithCharset() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody(new byte[] {112, 105, 99, 116, -31, -69, -96, 114, 101});
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("image/png", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFileBody());
    	assertEquals(9, ret.getBodyLength());
    	assertEquals("pict\u1ee0re", getBodyAsString(ret, Charset.forName("UTF-8")));
    }
	
    public void testBytesWithSmallerContentLength() throws Exception {
    	RackResponse ret = new Rack() {
			@Override public RackResponse call(Context<Object> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody(new byte[] {112, 105, 99, 116, -31, -69, -96, 114, 101})
					.withContentLength(4);
			}
		}.call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertEquals("image/png", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFileBody());
    	assertEquals(4, ret.getBodyLength());
    	assertEquals("pict", getBodyAsString(ret, Charset.forName("UTF-8")));
    }
	
    public void testFileBody() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFileBody());
    	assertEquals("<p>Hello!</p>", StreamHelper.readAsString(new FileInputStream(ret.getFileBody())));
    }
	
    public void testFileToByteConversion() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFileBody());
    	assertEquals(13, ret.getBodyAsBytes().length);
    }
	
    public void testFileToStringConversion() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(200, ret.getStatus());
    	assertNotNull(ret.getFileBody());
    	assertEquals("<p>Hello!</p>", getBodyAsString(ret));
    }
	
    public void testFileNotFound() throws Exception {
    	env.with(Rack.PATH_INFO, "missing.html");
    	RackResponse ret = new FileServer("src/test/files").call(env);
    	
    	assertEquals(404, ret.getStatus());
    	assertEquals("text/plain", ret.getHeaders().get("Content-Type"));
    	assertNull(ret.getFileBody());
    }
}
