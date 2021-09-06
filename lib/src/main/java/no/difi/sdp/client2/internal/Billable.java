package no.difi.sdp.client2.internal;

public class Billable<T> {

    public final long billableBytes;
    public final T entity;

    public Billable(T entity, long billableBytes) {
        this.entity = entity;
        this.billableBytes = billableBytes;
    }

}
