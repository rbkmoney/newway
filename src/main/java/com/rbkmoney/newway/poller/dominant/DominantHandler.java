package com.rbkmoney.newway.poller.dominant;

public interface DominantHandler<T> {

    boolean accept(T change);

    void handle(T change, Long versionId);
}
