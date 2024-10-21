package fpt.capstone.iUser.service;

import fpt.capstone.iUser.model.EmailTemplate;
import org.springframework.web.multipart.MultipartFile;

public interface AsyncSendEmail {
    void sendEmail(MultipartFile[] file, String email, EmailTemplate emailTemplate, String content);
    void sendMailLogCall(
            String sendTo,
            String sendFrom,
            String replyTo,
            String subject,
            String htmlContent,
            String bcc
    );
}
