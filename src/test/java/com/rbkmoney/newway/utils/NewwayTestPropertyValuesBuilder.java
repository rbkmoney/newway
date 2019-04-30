package com.rbkmoney.newway.utils;

import com.rbkmoney.newway.TestContainers;
import org.springframework.boot.test.util.TestPropertyValues;

import java.util.ArrayList;
import java.util.List;

public class NewwayTestPropertyValuesBuilder {

    public static TestPropertyValues build(TestContainers testContainers) {
        List<String> strings = new ArrayList<>();
        if (!testContainers.isDockerContainersEnable()) {
            withUsingTestContainers(testContainers, strings);
        } else {
            withoutUsingTestContainers(strings);
        }

        strings.add("bm.pollingEnabled=false");
        strings.add("dmt.polling.enable=false");
        return TestPropertyValues.of(strings);
    }

    private static void withUsingTestContainers(TestContainers testContainers, List<String> strings) {
        testContainers.getPostgresSQLTestContainer().ifPresent(
                c -> {
                    strings.add("spring.datasource.url=" + c.getJdbcUrl());
                    strings.add("spring.datasource.username=" + c.getUsername());
                    strings.add("spring.datasource.password=" + c.getPassword());
                    strings.add("flyway.url=" + c.getJdbcUrl());
                    strings.add("flyway.user=" + c.getUsername());
                    strings.add("flyway.password=" + c.getPassword());
                }
        );
    }

    private static void withoutUsingTestContainers(List<String> strings) {
    }
}
