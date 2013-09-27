package eu.opentxs.bridge;

import com.southpark.SetupProperties;

public class Localizer extends SetupProperties {
	private static final long serialVersionUID = 1L;

	private static Localizer instance;

	public static Localizer get() {
		if (instance == null)
			instance = new Localizer();
		return instance;
	}

	private Localizer() {
		super();
	}
}
