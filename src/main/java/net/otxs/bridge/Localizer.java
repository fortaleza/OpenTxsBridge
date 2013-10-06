package net.otxs.bridge;

import net.otxs.ApplProperties;

import com.southpark.SetupProperties;

public class Localizer extends SetupProperties {
	private static final long serialVersionUID = 1L;

	private static Localizer instance;

	public static Localizer get() {
		ApplProperties.get();
		if (instance == null){
			instance = new Localizer();
			instance.setReadOnly(true);
			instance.init();
		}
		return instance;
	}

	private Localizer() {
		super();
	}
}
