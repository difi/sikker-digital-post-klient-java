/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
