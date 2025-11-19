package com.techup.spring_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SupabaseConfig {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.apiKey}")
    private String supabaseApiKey;
    
    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl + "/auth/v1")
                .defaultHeader("apikey", supabaseApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    public String getSupabaseUrl() {
        return supabaseUrl;
    }
    
    public String getSupabaseApiKey() {
        return supabaseApiKey;
    }
}
