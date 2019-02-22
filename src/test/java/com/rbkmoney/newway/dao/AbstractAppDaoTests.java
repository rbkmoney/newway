package com.rbkmoney.newway.dao;

import com.rbkmoney.AbstractTestUtils;
import com.rbkmoney.TestContainers;
import com.rbkmoney.TestContainersBuilder;
import com.rbkmoney.newway.NewwayApplication;
import com.rbkmoney.newway.utils.NewwayTestPropertyValuesBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = NewwayApplication.class, initializers = AbstractAppDaoTests.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractAppDaoTests extends AbstractTestUtils {

    private static TestContainers testContainers = TestContainersBuilder.builder(false)
            .addPostgreSQLTestContainer()
            .build();

    @BeforeClass
    public static void beforeClass() {
        testContainers.startTestContainers();
    }

    @AfterClass
    public static void afterClass() {
        testContainers.stopTestContainers();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            NewwayTestPropertyValuesBuilder.build(testContainers).applyTo(configurableApplicationContext);
        }
    }
}
