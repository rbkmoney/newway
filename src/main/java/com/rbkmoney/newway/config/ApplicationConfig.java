package com.rbkmoney.newway.config;

import com.rbkmoney.newway.domain.Nw;
import org.jooq.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Schema schema() {
        return Nw.NW;
    }

}
