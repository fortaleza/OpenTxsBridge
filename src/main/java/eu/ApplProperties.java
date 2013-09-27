package eu;

import java.io.File;
import java.net.URL;

import com.southpark.ApplPropertiesBase;

public class ApplProperties extends ApplPropertiesBase {

	private static final long serialVersionUID = 1L;

	protected ApplProperties() {
		super();
	}

	private static ApplProperties instance = null;

	public static ApplProperties get() {
		if (instance == null)
			instance = new ApplProperties();
		return instance;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		URL url = ClassLoader.getSystemResource("bin");
		File bf = com.southpark.ApplPropertiesBase.getApplBaseFile();
		String key = "hstFolder";
		String propertyNew = get().getString(key);
		String propertyOld = com.southpark.ApplPropertiesBase.get().getString(key);
	}
}
