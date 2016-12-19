package no.difi.sdp.client2.domain;

public enum EnvironmentGroup {
    PROD,
    TEST

    public boolean is(EnvironmentGroup group) {
        return this == group;
    }
}
