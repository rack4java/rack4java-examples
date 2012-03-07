package test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackBody;
import org.rack4java.RackResponse;
import org.rack4java.context.MapContext;
import org.rack4java.examples.FileServer;
import org.rack4java.utils.StreamHelper;

public class ExampleTest extends TestCase {
	
	Context<String> env;
	File file;
	
	public void setUp() {
		env = new MapContext<String>();
	}

	private RackBody getBody(Context<String> response) {
		return (RackBody) response.getObject(Rack.MESSAGE_BODY);
	}
	
	private String getBodyAsString(Context<String> response, Charset charset) {
		StringBuilder ret = new StringBuilder();
		for (byte[] chunk : getBody(response).getBodyAsBytes()) {
			ret.append(new String(chunk, charset));
		}
		return ret.toString();
	}
	
	private String getBodyAsString(Context<String> response) {
		StringBuilder ret = new StringBuilder();
		for (byte[] chunk : getBody(response).getBodyAsBytes()) {
			ret.append(new String(chunk));
		}
		return ret.toString();
	}
	
	private byte[] getBodyAsBytes(Context<String> response) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		int length = 0;
		for (byte[] chunk : getBody(response).getBodyAsBytes()) {
			length += chunk.length;
			chunks.add(chunk);
		}
		byte[] ret = new byte[length];
		int offset = 0;
		for (byte[] chunk : chunks) {
			int step = 0;
			for (byte b : chunk) {
				ret[offset + step++] = b;
			}
			offset += step;
		}
		return ret;
	}
	
	private File getBodyAsFile(Context<String> response) {
		RackBody body = getBody(response);
		if (RackBody.Type.file != body.getType()) return null;
		return body.getBodyAsFile();
	}
	
    public void testStringBody() throws Exception {
    	Context<String> ret = new Rack() {
			@Override public Context<String> call(Context<String> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "text/plain")
					.withBody("Wibble");
			}
		}.call(env);

		RackResponse response = new RackResponse(ret);

    	assertEquals(200, response.getStatus());
    	assertEquals("text/plain", response.getHeaders().get("Content-Type"));
    	RackBody body = response.getBody();
    	assertNotNull(body);
    	try { body.getBodyAsFile(); } catch (IllegalStateException e) { /* expected */ }  
    	assertEquals("Wibble", getBodyAsString(ret));
    }
	
    public void testStringToByteConversion() throws Exception {
    	Context<String> ret = new Rack() {
			@Override public Context<String> call(Context<String> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "text/xml")
					.withBody("<whatever/>");
			}
		}.call(env);
		
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
    	assertEquals("text/xml", response.getHeaders().get("Content-Type"));
    	assertNull(getBodyAsFile(ret));
    }
	
    public void testByteToStringConversion() throws Exception {
    	Context<String> ret = new Rack() {
			@Override public Context<String> call(Context<String> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody("picture".getBytes());
			}
		}.call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
    	assertEquals("image/png", response.getHeaders().get("Content-Type"));
    	assertNull(getBodyAsFile(ret));
    	assertEquals("picture", getBodyAsString(ret));
    }
	
    public void testByteToStringConversionWithCharset() throws Exception {
    	Context<String> ret = new Rack() {
			@Override public Context<String> call(Context<String> environment) throws Exception {
				return new RackResponse(200)
					.withHeader("Content-Type", "image/png")
					.withBody(new byte[] {112, 105, 99, 116, -31, -69, -96, 114, 101});
			}
		}.call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
    	assertEquals("image/png", response.getHeaders().get("Content-Type"));
    	assertNull(getBodyAsFile(ret));
    	assertEquals("pict\u1ee0re", getBodyAsString(ret, Charset.forName("UTF-8")));
    }
	
    public void testFileBody() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	Context<String> ret = new FileServer("src/test/files").call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
       	assertNotNull(getBodyAsFile(ret));
    	assertEquals("<p>Hello!</p>", StreamHelper.readAsString(new FileInputStream(getBodyAsFile(ret))));
    }
	
    public void testFileToByteConversion() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	Context<String> ret = new FileServer("src/test/files").call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
       	assertNotNull(getBodyAsFile(ret));
    	assertEquals(13, getBodyAsBytes(ret).length);
    }
	
    public void testFileToStringConversion() throws Exception {
    	env.with(Rack.PATH_INFO, "static.html");
    	Context<String> ret = new FileServer("src/test/files").call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(200, response.getStatus());
       	assertNotNull(getBodyAsFile(ret));
           	assertEquals("<p>Hello!</p>", getBodyAsString(ret));
    }
	
    public void testFileNotFound() throws Exception {
    	env.with(Rack.PATH_INFO, "missing.html");
    	Context<String> ret = new FileServer("src/test/files").call(env);
    	
		RackResponse response = new RackResponse(ret);
    	
    	assertEquals(404, response.getStatus());
    	assertEquals("text/plain", response.getHeaders().get("Content-Type"));
    	assertNull(getBodyAsFile(ret));
    }
}
