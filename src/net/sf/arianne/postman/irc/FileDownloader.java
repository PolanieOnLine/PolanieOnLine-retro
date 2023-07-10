package net.sf.arianne.postman.irc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

public class FileDownloader implements Runnable {

	private static Logger logger = Logger.getLogger(FileDownloader.class);
	private String url;
	private String filename;

	FileDownloader(String url, String filename) {
		this.url = url;
		this.filename = filename;
	}
	
	@Override
	public void run() {
		try (InputStream in = new URL(url).openStream()) {
			Files.copy(in, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
}
