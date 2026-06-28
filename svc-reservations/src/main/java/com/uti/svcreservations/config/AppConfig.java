package com.uti.svcreservations.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de beans HTTP para comunicación con svc-rooms.
 * RestTemplate: cliente síncrono bloqueante.
 * WebClient: cliente reactivo no bloqueante (usado con block() en este proyecto).
 */
@Configuration
public class AppConfig {

    @Value("${rooms.service.url}")
    private String roomsServiceUrl;

    /**
     * Bean RestTemplate para llamadas síncronas a svc-rooms.
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

    /**
     * Bean WebClient configurado con la URL base de svc-rooms.
     */
    @Bean
    public WebClient roomsServiceWebClient() {
        return WebClient.builder()
                .baseUrl(roomsServiceUrl)
                .build();
    }

    @Bean
    public String roomsServiceUrl() {
        return roomsServiceUrl;
    }
}
