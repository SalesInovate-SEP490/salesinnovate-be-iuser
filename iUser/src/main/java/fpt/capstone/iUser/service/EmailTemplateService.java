package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.EmailTemplateDTO;
import fpt.capstone.iUser.dto.response.EmailTemplateResponse;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.model.ParameterMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmailTemplateService {
    Long createEmailTemplate(EmailTemplateDTO emailTemplateDTO);

    Long updateEmailTemplate(EmailTemplateDTO emailTemplateDTO);

    boolean deleteEmailTemplate(Long id);

    PageResponse<?> getListEmailTemplateByUserId(String userId, int pageNo, int pageSize);

    EmailTemplateResponse getDetailEmailTeplate(Long EmailTemplateId);

    void sendEmail(Long id, MultipartFile[] file);

    void sendEmails(List<Long> listIds, Long id, MultipartFile[] file);

    List<ParameterMapping> getListParamEmail();

    void sendMailLogCall(
            String sendTo,
            String sendFrom,
            String replyTo,
            String subject,
            String htmlContent,
            String bcc
    );
}
