package com.github.discovery126.bookingservice.config;

import com.github.discovery126.bookingservice.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClientAuth(@Value("${restclient.url.auth}") String authUrl) {
        return RestClient.builder()
                .baseUrl(authUrl)
                .build();
    }

    @Bean
    public RestClient restClientMovie(
            @Value("${restclient.url.movie}") String movieUrl,
            TokenCacheService tokenCacheService) {
        return RestClient.builder()
                .baseUrl(movieUrl)
                .requestInterceptor((request, body, execution) -> {
                    String token = tokenCacheService.getToken();
                    request.getHeaders().add("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
                .build();
    }
}