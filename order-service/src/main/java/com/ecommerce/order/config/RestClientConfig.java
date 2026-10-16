package com.ecommerce.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class RestClientConfig {

    @Bean("cartRestClient")
    public RestClient cartRestClient(
            @Value("${services.cart.url}") String url) {
        return RestClient.create(url);
    }
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean("productRestClient")
    public RestClient productRestClient(
            @Value("${services.product.url}") String url) {
        return RestClient.create(url);
    }

    @Bean("inventoryRestClient")
    public RestClient inventoryRestClient(
            @Value("${services.inventory.url}") String url) {
        return RestClient.create(url);
    }
}