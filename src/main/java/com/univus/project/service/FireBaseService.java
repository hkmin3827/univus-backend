//package com.univus.project.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@RequiredArgsConstructor
//public class FirebaseService {
//
//    public String uploadImage(MultipartFile file) {
//        try {
//            // Firebase Storage 업로드 코드
//            // 업로드 후 다운로드 URL을 반환
//
//            return downloadUrl; // 이 URL을 Post에 저장
//        } catch (Exception e) {
//            throw new RuntimeException("Firebase 업로드 실패");
//        }
//    }
//}

//
//
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import java.util.UUID;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class FirebaseService {
//
//
//    public String uploadImage(MultipartFile file) {
//        try {
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            String bucketName = "YOUR_BUCKET_NAME.appspot.com";
//
//
//            Storage storage = StorageOptions.getDefaultInstance().getService();
//
//
//            BlobId blobId = BlobId.of(bucketName, fileName);
//            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
//
//
//            Blob blob = storage.create(blobInfo, file.getBytes());
//
//
//            return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + fileName.replace("/", "%2F") + "?alt=media";
//
//
//        } catch (Exception e) {
//            throw new RuntimeException("Firebase 업로드 실패: " + e.getMessage());
//        }
//    }
//}