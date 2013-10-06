package net.otxs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.southpark.ApplPropertiesBase;

public class ApplProperties extends ApplPropertiesBase {

	private static final long serialVersionUID = 1L;
	public static Logger logger = LoggerFactory
			.getLogger(ApplProperties.class);

	public static ApplProperties get() {
		if (instance == null) {
			instance = new ApplProperties();
			instance.setReadOnly(true);
			instance.init();
			String userDataPath = getUserDataPath();
			if (instance.getPropertiesUrl().getPath().indexOf("jar!") != -1) {
				/** evidently, in production */
				if (get().getString("workingDir").equals(""))
					instance.setString("workingDir", userDataPath);
				instance.setString("database.relpath",
						String.format("%s/%s",
								userDataPath, get().getString("bridgeDir")));
			} else {
				if (get().getString("workingDir").equals(""))
					instance.setString("workingDir", get().getApplBasePath());
			}
			logger.info(String
					.format("Working directory: %s"
							+ " database directory: %s"
							+ " application base path: %s",
							get().getString("workingDir"), 
							get().getString("database.relpath"),
							get().getApplBasePath()));
		}
		return (ApplProperties) instance;
	}

	public static String getUserDataPath() {
		return String.format("%s/%s/%s", System.getenv("APPDATA"),
				get().getString("appData.app"),
				get().getString("appData.clientData"));
	}

	public static void main(String[] args) {
	}

	protected ApplProperties() {
		super();
	}
}
