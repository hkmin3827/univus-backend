// src/main/java/com/univus/project/service/MailService.java
package com.univus.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[UnivUs] 비밀번호 재설정 안내");

            String htmlContent =
                    "<div style='font-family:Arial, sans-serif; line-height:1.6;'>" +
                            "<h2 style='color:#5f52ff;'>🔑 비밀번호 재설정 안내</h2>" +
                            "<p>안녕하세요.</p>" +
                            "<p>아래 버튼을 클릭하여 비밀번호를 재설정해 주세요:</p>" +
                            "<a href='" + resetLink + "' " +
                            "style='display:inline-block; padding:12px 22px; margin-top:10px; background-color:#5f52ff; color:white; text-decoration:none; border-radius:8px; font-size:16px;'>비밀번호 재설정</a>" +
                            "<p style='margin-top:20px; font-size:14px; color:#555;'>해당 링크는 일정 시간 후 만료됩니다.</p>" +
                            "<p>감사합니다.<br>- UnivUs 팀</p>" +
                            "</div>";

            helper.setText(htmlContent, true); // ★ HTML 사용 설정

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }
}
