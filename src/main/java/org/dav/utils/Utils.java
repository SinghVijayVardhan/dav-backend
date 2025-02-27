package org.dav.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class Utils {

    @Bean("HttpClient")
    public HttpClient getHttpClient(){
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).build();
    }

    @Bean("ObjectMapper")
    public ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
