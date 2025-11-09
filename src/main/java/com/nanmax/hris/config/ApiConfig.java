package com.nanmax.hris.config;

import org.springframework.stereotype.Component;

@Component
public class ApiConfig {
    
    private static final String PRODUCTION_URL = "https://hris-jasamedika.onrender.com";
    private static final String DEVELOPMENT_URL = "http://localhost:8080";
    
    public static String getBaseUrl() {
        String profilesActive = System.getProperty("spring.profiles.active");
        String vaadinProductionMode = System.getenv("VAADIN_PRODUCTION_MODE");
        String port = System.getenv("PORT");
        
        boolean isProduction = "production".equals(profilesActive) || 
                              "true".equals(vaadinProductionMode) || 
                              port != null;
        
        return isProduction ? PRODUCTION_URL : DEVELOPMENT_URL;
    }
}