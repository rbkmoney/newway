package com.rbkmoney;

import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

public class TestContainersBuilder {

    private boolean dockerContainersEnable;
    private boolean postgreSQLTestContainerEnable;

    private TestContainersBuilder(boolean dockerContainersEnable) {
        this.dockerContainersEnable = dockerContainersEnable;
    }

    public static TestContainersBuilder builder(boolean dockerContainersEnable) {
        return new TestContainersBuilder(dockerContainersEnable);
    }

    public TestContainersBuilder addPostgreSQLTestContainer() {
        postgreSQLTestContainerEnable = true;
        return this;
    }

    public TestContainers build() {
        TestContainers testContainers = new TestContainers();

        if (!dockerContainersEnable) {
            addTestContainers(testContainers);
        } else {
            testContainers.setDockerContainersEnable(true);
        }
        return testContainers;
    }

    private void addTestContainers(TestContainers testContainers) {
        if (postgreSQLTestContainerEnable) {
            testContainers.setPostgresSQLTestContainer(
                    new PostgreSQLContainer<>("postgres:9.6")
                            .withStartupTimeout(Duration.ofMinutes(5))
            );
        }
        testContainers.setDockerContainersEnable(false);
    }
}
