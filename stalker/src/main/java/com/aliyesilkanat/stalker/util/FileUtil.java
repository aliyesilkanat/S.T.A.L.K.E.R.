package com.aliyesilkanat.stalker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

public class FileUtil {

	private final static Logger logger = Logger.getLogger(FileUtil.class);

	/**
	 * This method used to read given file into a string.
	 * 
	 * @param filePath
	 *            path of the file.
	 * @return content of the file.
	 */
	@SuppressWarnings("resource")
	public static String readFile(String filePath) {
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					filePath)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			String msg = "error while reading file {\"filePath\":\"%s\"}";
			getLogger().error(String.format(msg, filePath), e);
		}
		return buffer.toString();
	}

	public static Logger getLogger() {
		return logger;
	}
}
