package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.model.EmailTemplate;
import fpt.capstone.iUser.service.AsyncSendEmail;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AsyncSendEmailImpl implements AsyncSendEmail {
    private final JavaMailSender mailSender;
    @Override
    @Async("thread-pool-send-email")
    public void sendEmail(MultipartFile[] file, String email, EmailTemplate emailTemplate, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            if (emailTemplate != null) {
                helper.setTo(email);
                helper.setSubject(emailTemplate.getMailSubject());
                helper.setText(content, true);
                helper.setFrom("mailtrap@sales-innova.top");
                helper.setReplyTo("trungnqhe161514@fpt.edu.vn", "trungnqhe161514@fpt.edu.vn");
                if (file != null) {
                    Map<String, String> convertFilesToBase64 = convertFilesToBase64(file);
                    convertFilesToBase64.forEach((fileName, base64String) -> {
                        try {
                            byte[] data = Base64.getDecoder().decode(base64String);
                            ByteArrayDataSource dataSource = new ByteArrayDataSource(data, "application/octet-stream");

                            helper.addAttachment(fileName, dataSource);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                mailSender.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("thread-pool-send-email")
    public void sendMailLogCall(String sendTo, String sendFrom, String replyTo, String subject, String htmlContent, String bcc) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String[] sendToArr = sendTo.split(",,");
            helper.setTo(sendToArr);
            helper.setFrom("mailtrap@sales-innova.top", sendFrom);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setReplyTo(replyTo, "mailtrap@sales-innova.top");
            if (bcc != null && !bcc.trim().isEmpty()) {
                helper.setBcc(bcc.split(","));
            }
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> convertFilesToBase64(MultipartFile[] files) {
        Map<String, String> fileBase64Map = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String base64Image = "";
            try {
                byte[] bytes = file.getBytes();
                base64Image = Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileBase64Map.put(fileName, base64Image);
        }
        return fileBase64Map;
    }
}
