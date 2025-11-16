package com.univus.project.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//
//
//import javax.annotation.PostConstruct;
//import java.io.FileInputStream;
//
//
//@Slf4j
//@Configuration
//public class FirebaseConfig {
//
//
//    @PostConstruct
//    public void init() {
//        try {
//            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-key.json");
//
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setStorageBucket("YOUR_BUCKET_NAME.appspot.com")
//                    .build();
//
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//
//
//            log.info("Firebase initialized");
//        } catch (Exception e) {
//            log.error("Firebase init error: {}", e.getMessage());
//        }
//    }
//}
//YOUR_BUCKET_NAME = Firebase Storage 버킷 이름