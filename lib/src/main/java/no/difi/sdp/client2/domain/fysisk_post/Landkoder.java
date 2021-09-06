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
