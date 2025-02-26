package org.dav.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        return new ObjectMapper();
    }
}
