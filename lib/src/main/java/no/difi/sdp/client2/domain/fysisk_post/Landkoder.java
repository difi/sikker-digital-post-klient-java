/*
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
package no.difi.sdp.client2.domain.fysisk_post;

/**
 * Et knippe {@link Predefinert predefinerte} {@link Landkode landkoder},
 * samt en metode for å generere ny Landkode fra en arbitrær to-bokstavs
 * <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO_3166-1_alpha-2</a> landkode.
 */
public final class Landkoder {

	public enum Predefinert implements Landkode {

		SVALBARD_OG_JAN_MAYEN("SJ"),
		SVERIGE("SE"),
		DANMARK("DK"),
		FINLAND("FI"),
		STORBRITANNIA("UK"),
		IRLAND("IE"),
		USA("US"),
		TYSKLAND("DE"),
		OESTERIKE("AT"),
		SVEITS("CH"),
		LUXEMBOURG("LU"),
		FRANKRIKE("FR"),
		NEDERLAND("NL"),
		SPANIA("ES"),
		ITALIA("IT"),
		HELLAS("GR"),
		TSJEKKIA("CZ"),
		SLOVAKIA("SK"),
		UNGARN("HU"),
		KORATIA("HR"),
		ESTLAND("EE"),
		LATVIA("LV"),
		LITAUEN("LT"),
		POLEN("PL");

		private final String kode;

		Predefinert(String kode) {
			this.kode = kode;
		}

		@Override
	    public String getKode() {
			return kode;
	    }

	}


	public static Landkode landkode(final String kode) {
		return new Landkode() {
			@Override
			public String getKode() {
				return kode;
			}
		};
	}


	private Landkoder() {
	}
}
