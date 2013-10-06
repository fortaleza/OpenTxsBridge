package net.otxs.bridge;

public class UTC extends com.southpark.utc.UTC {

	private static final long serialVersionUID = 1L;

	private UTC() {
		super();
	}

	public UTC(String s) {
		super(s);
	}

	public static UTC getDateUTC(String s) {
		if (!Util.isValidString(s))
			return null;
		Long seconds = new Long(s);
		if (seconds > 0) {
			UTC utc = new UTC();
			utc.clear();
			utc.setTimeInMillis(seconds * SECOND_MILS);
			return utc;
		}
		return null;
	}

	public static String timeToString(String s) {
		UTC utc = getDateUTC(s);
		if (utc != null)
			return timeToString(utc);
		return "";
	}

	public boolean isBefore(UTC utc) {
		return (getSeconds() < utc.getSeconds());
	}

	public boolean isAfter(UTC utc) {
		return (getSeconds() > utc.getSeconds());
	}
}
