package net.otxs.bridge;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarInputStream;

public class Util {

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

	public static void copyStream(JarInputStream in, FileOutputStream out,
			long size) throws IOException {
		byte[] buffer = new byte[1024 * 4];
		long count = 0;
		int n = 0;
		while (-1 != (n = in.read(buffer)) && count < size) {
			out.write(buffer, 0, n);
			count += n;
		}
	}

	public static void copyStream(InputStream in, FileOutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while (-1 != (n = in.read(buffer)))
			out.write(buffer, 0, n);
	}

}
