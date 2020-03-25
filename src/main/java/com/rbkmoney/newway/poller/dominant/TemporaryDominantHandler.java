package com.rbkmoney.newway.poller.dominant;

public interface TemporaryDominantHandler<T> {

    boolean acceptAndSet(T change);

    void handle(T change, Long versionId);
}
