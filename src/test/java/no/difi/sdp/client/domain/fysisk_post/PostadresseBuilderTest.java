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

import org.junit.Test;

import static no.difi.sdp.client.domain.fysisk_post.KonvoluttAdresse.Type.NORSK;
import static no.difi.sdp.client.domain.fysisk_post.KonvoluttAdresse.Type.UTENLANDSK;
import static no.difi.sdp.client.domain.fysisk_post.Landkoder.Predefinert.USA;
import static org.fest.assertions.api.Assertions.assertThat;

public class PostadresseBuilderTest {

	@Test
	public void inkludererKunIkkeNullAdresselinjer() {
		KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iNorge("Osloveien 5", null, null, "0560", "Oslo").build();
		assertThat(adresse.getAdresselinjer()).containsExactly("Osloveien 5");

		adresse = KonvoluttAdresse.build("Ola Hansen").iUtlandet("Somewhere St. 5", null, "70482 City", null, USA).build();
		assertThat(adresse.getAdresselinjer()).containsExactly("Somewhere St. 5", "70482 City");
	}

	@Test
	public void norskAdresse() {
		KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iNorge("Osloveien 5", null, null, "0560", "Oslo").build();
		assertThat(adresse.getType()).isEqualTo(NORSK);
	}

	@Test
	public void utenlandskAdresse() {
		KonvoluttAdresse adresse = KonvoluttAdresse.build("Ola Hansen").iUtlandet("Somewhere St. 5", "10592 New York", null, null, USA).build();
		assertThat(adresse.getType()).isEqualTo(UTENLANDSK);
	}

}
