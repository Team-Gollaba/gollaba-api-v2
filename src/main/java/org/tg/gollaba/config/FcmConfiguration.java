package org.tg.gollaba.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tg.gollaba.config.property.GoogleServiceAccountCredential;

import java.io.IOException;
import java.net.URI;

@Configuration
public class FcmConfiguration {

    @Bean
    public FirebaseApp initializeFirebaseApp(GoogleCredentials googleCredentials) {
        var options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public GoogleCredentials googleCredentials(GoogleServiceAccountCredential properties) throws IOException {
        return ServiceAccountCredentials.newBuilder()
            .setProjectId(properties.projectId())
            .setPrivateKeyId(properties.privateKeyId())
            .setPrivateKeyString(properties.privateKey().replace("\\n","\n"))
            //ㄴ> Caused by: java.io.IOException: Invalid PKCS#8 data.
            .setClientEmail(properties.clientEmail())
            .setClientId(properties.clientId())
            .setHttpTransportFactory(NetHttpTransport::new)
            .setTokenServerUri(URI.create(properties.tokenUri()))
            .build();
    }
}