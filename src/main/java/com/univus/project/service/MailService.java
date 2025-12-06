// src/main/java/com/univus/project/service/MailService.java
package com.univus.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // 비밀번호 재설정 메일 보내기
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[UnivUs] 비밀번호 재설정 안내");
        message.setText(
                "안녕하세요.\n\n" +
                        "아래 링크를 클릭하여 비밀번호를 재설정해 주세요.\n\n" +
                        resetLink + "\n\n" +
                        "해당 링크는 일정 시간 후 만료됩니다.\n\n" +
                        "- UnivUs 팀"
        );
        mailSender.send(message);
    }
}
