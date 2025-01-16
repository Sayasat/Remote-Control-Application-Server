package com.programming.firebaselearnauth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path}")
    private String configPath;

    @Value("${firebase.database.url}")
    private String firebaseDbUrl;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource(configPath);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .setDatabaseUrl(firebaseDbUrl)
                .build();
        FirebaseApp.initializeApp(options);
        logger.info("App name: " + FirebaseApp.getInstance().getName());
    }
}
