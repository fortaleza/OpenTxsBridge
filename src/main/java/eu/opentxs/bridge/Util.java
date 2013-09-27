package eu.opentxs.bridge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import eu.ApplProperties;

public class Util {
	
	public static String getApplicationSourcePath() {
		String path = null;
		try {
			path = URLDecoder.decode(
					Util.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return path;
	}
	
	public static String getUserDataPath() {
		return String.format("%s/%s/%s", 
				System.getenv("APPDATA"), 
				ApplProperties.get().getString("folder.app"), 
				ApplProperties.get().getString("folder.client"));
	}
	
	public static String repeat(String s, int times) {
		String retval = "";
		for (int i = 0; i < times; i++)
			retval += s;
		return retval;
	}
	
	public static boolean isValidString(String s) {
        return !((s == null) || s.isEmpty());
    }
	
	public static String crop(String s, int limit) {
		if (s.length() <= limit)
			return s;
		return String.format("%s..", s.substring(0, limit - 2));
	}
	
	public static String readStringFromPath(Path path) throws Exception {
		FileReader fr = new FileReader(path.toString());
		BufferedReader br = new BufferedReader(fr);
		String content;
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			content = sb.toString();
		} finally {
			br.close();
		}
		return content;
	}
	
	public static String readStringFromPath2(Path path) throws Exception {
		byte[] encoded = Files.readAllBytes(path);
		Charset encoding = Charset.defaultCharset();
		String content = encoding.decode(ByteBuffer.wrap(encoded)).toString();
		return content.replaceAll("\\r", "");
	}
	
}
