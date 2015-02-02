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
package no.difi.sdp.client.domain.fysisk_post;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static no.difi.sdp.client.util.Convenience.onlyNonNull;


public class KonvoluttAdresse {

	public enum Type { NORSK, UTENLANDSK }

	private Type type;
	private String navn;
	private List<String> adresselinjer;

	private String postnummer;
	private String poststed;

	private String landkode;
	private String land;

	public static KonvoluttAdresse.Builder build(String mottakersNavn) {
		return new Builder(mottakersNavn);
	}

	public boolean er(Type type) {
		return this.type == type;
    }

	public Type getType() {
		return type;
    }

	public String getNavn() {
		return navn;
	}

	public List<String> getAdresselinjer() {
		return unmodifiableList(adresselinjer);
	}

	public String getLandkode() {
		return landkode;
	}

	public String getLand() {
		return land;
	}

	public String getPostnummer() {
		return postnummer;
	}

	public String getPoststed() {
		return poststed;
	}






	public static final class Builder {

		private final KonvoluttAdresse postadresse;
		private boolean built = false;

		private Builder(String mottakersNavn) {
			postadresse = new KonvoluttAdresse();
			postadresse.navn = mottakersNavn;
		}

		public Builder iNorge(String adresselinje1, String adresselinje2, String adresselinje3, String postnummer, String poststed) {
			postadresse.type = Type.NORSK;
			postadresse.adresselinjer = onlyNonNull(adresselinje1, adresselinje2, adresselinje3);
			postadresse.postnummer = postnummer;
			postadresse.poststed = poststed;
			return this;
		}

		public Builder iUtlandet(String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, String land) {
			return iUtlandet(adresselinje1, adresselinje2, adresselinje3, adresselinje4, land, null);
		}

		public Builder iUtlandet(String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, Landkode landkode) {
			return iUtlandet(adresselinje1, adresselinje2, adresselinje3, adresselinje4, null, landkode);
		}
		private Builder iUtlandet(String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4, String land, Landkode landkode) {
			postadresse.type = Type.UTENLANDSK;
			postadresse.adresselinjer = onlyNonNull(adresselinje1, adresselinje2, adresselinje3, adresselinje4);
			postadresse.land = land;
			postadresse.landkode = landkode != null ? landkode.getKode() : null;
			return this;
		}

		public KonvoluttAdresse build() {
			if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return postadresse;
		}
	}


}
