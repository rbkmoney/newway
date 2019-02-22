package com.rbkmoney;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

@NoArgsConstructor
@Setter
public class TestContainers {

    private Boolean dockerContainersEnable;
    private PostgreSQLContainer postgresSQLTestContainer;

    public Optional<PostgreSQLContainer> getPostgresSQLTestContainer() {
        return Optional.ofNullable(postgresSQLTestContainer);
    }

    public Boolean isDockerContainersEnable() {
        return dockerContainersEnable;
    }

    public void startTestContainers() {
        if (!isDockerContainersEnable()) {
            getPostgresSQLTestContainer().ifPresent(GenericContainer::start);
        }
    }

    public void stopTestContainers() {
        if (!isDockerContainersEnable()) {
            getPostgresSQLTestContainer().ifPresent(GenericContainer::stop);
        }
    }
}
