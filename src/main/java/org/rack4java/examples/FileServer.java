package org.rack4java.examples;

import java.io.File;
import java.util.Map;

import org.rack4java.Rack;
import org.rack4java.RackResponse;

public class FileServer implements Rack {

	private File root;

	public FileServer(String root) {
		this.root = new File(root);
	}

	@Override public RackResponse call(Map<String, Object> environment) throws Exception {
		String filename = (String) environment.get(Rack.PATH_INFO);
		File file = new File(root, filename);
		if (file.canRead()) {
			return new RackResponse(200, file);
		}
		return new RackResponse(404, "File [" + filename + "] not found", "Content-Type", "text/plain");
	}

}
