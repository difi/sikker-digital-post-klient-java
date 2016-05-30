package no.difi.sdp.client2.util;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Convenience {

	public static <T> List<T> onlyNonNull(T ... ts) {
		return onlyNonNull(asList(ts));
	}

	public static <T> List<T> onlyNonNull(List<T> ts) {
		List<T> nonNulls = new ArrayList<T>();
		for (T t : ts) {
			if (t != null) nonNulls.add(t);
		}
		return nonNulls;
	}

	private Convenience() { }
}
