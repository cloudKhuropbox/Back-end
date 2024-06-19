package com.khu.cloudcomputing.khuropbox.configuration.GPT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptConfig {
    @Value("${gpt.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate template = new RestTemplate();
        return template;
    }

    @Bean
    public HttpHeaders headers(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
