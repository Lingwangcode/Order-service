package com.example.orderservice;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
@EnableRetry
@Configuration
public class RestTemplateConfig {
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000; //testa med 5

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECTION_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
    }

   /* @Bean
    public RetryTemplate retryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }*/


}
