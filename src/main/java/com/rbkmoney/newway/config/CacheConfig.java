package com.rbkmoney.newway.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.newway.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<InvoicingKey, InvoiceWrapper> invoiceDataCache(@Value("${cache.invoice.size}") int cacheSize) {
        return Caffeine.newBuilder().maximumSize(cacheSize).build();
    }

    @Bean
    public Cache<InvoicingKey, PaymentWrapper> paymentDataCache(@Value("${cache.payment.size}") int cacheSize) {
        return Caffeine.newBuilder().maximumSize(cacheSize).build();
    }
}
