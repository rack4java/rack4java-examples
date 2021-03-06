package org.rack4java.examples;

import java.io.File;

import org.rack4java.Context;
import org.rack4java.Rack;
import org.rack4java.RackResponse;

public class FileServer implements Rack {

	private File root;

	public FileServer(String root) {
		this.root = new File(root);
	}

	@Override public Context<String> call(Context<String> environment) throws Exception {
		String filename = environment.get(Rack.PATH_INFO);
		File file = new File(root, filename);
		if (file.canRead()) {
			return new RackResponse(200).withBody(file);
		}
		
		return new RackResponse(404)
			.withHeader("Content-Type", "text/plain")
			.withBody("File [" + filename + "] not found");
	}

}
