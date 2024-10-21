package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.Converter;
import fpt.capstone.iUser.dto.request.EmailTemplateDTO;
import fpt.capstone.iUser.dto.response.EmailTemplateResponse;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.model.EmailTemplate;
import fpt.capstone.iUser.model.ParameterMapping;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.repository.EmailTemplateRepository;
import fpt.capstone.iUser.repository.ParameterMappingRepository;
import fpt.capstone.iUser.repository.UsersRepository;
import fpt.capstone.iUser.service.AsyncSendEmail;
import fpt.capstone.iUser.service.EmailTemplateService;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private final JavaMailSender mailSender;
    private final EmailTemplateRepository emailTemplateRepository;
    private final UsersRepository usersRepository;
    private final Converter converter;
    private final ParameterMappingRepository parameterMappingRepository;
    private final AsyncSendEmail asyncSendEmail;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long createEmailTemplate(EmailTemplateDTO emailTemplateDTO) {
        EmailTemplate emailTemplate = converter.DTOToEmailTemplate(emailTemplateDTO);
        if (emailTemplate != null) {
            log.info("The email entered is valid");
            emailTemplateRepository.save(emailTemplate);
            return emailTemplate.getEmailTemplateId();
        }
        log.info("The email entered is null ");
        return null;
    }

    @Override
    public Long updateEmailTemplate(EmailTemplateDTO emailTemplateDTO) {
        Optional<EmailTemplate> emailTemplateOptional = emailTemplateRepository.
                findById(emailTemplateDTO.getId());
        if (emailTemplateOptional.isPresent()) {
            log.info("Exist email template in database");
            EmailTemplate emailTemplateExisted = emailTemplateOptional.get();
            EmailTemplate emailTemplate =
                    converter.UpdateEmailTemplateFromDTO(emailTemplateDTO, emailTemplateExisted);
            emailTemplateRepository.save(emailTemplate);
            return emailTemplate.getEmailTemplateId();
        }
        log.info("There are not exists a emailTemplate containing that ID: " + emailTemplateDTO.getId());
        return null;
    }

    @Override
    public boolean deleteEmailTemplate(Long id) {
        Optional<EmailTemplate> emailTemplate = emailTemplateRepository.
                findById(id);
        if (emailTemplate.isPresent()) {
            log.info("Exist email template in database");
            EmailTemplate emailTemplateExisted = emailTemplate.get();
            emailTemplateExisted.setIsDeleted(1);
            emailTemplateExisted.setDeleteDate(getCurrentLocalDate());
            emailTemplateRepository.save(emailTemplateExisted);
            return true;
        }
        log.info("There are not exists a emailTemplate containing that ID: " + id);
        return false;
    }

    @Override
    public PageResponse<?> getListEmailTemplateByUserId(String userId, int pageNo, int pageSize) {
        try {
            int page = 0;
            if (pageNo > 0) {
                page = pageNo - 1;
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Users users = usersRepository.findById(userId).orElse(null);
            Page<EmailTemplate> emailTemplates =
                    emailTemplateRepository.findByUsers(users, pageable);
            return converter.convertToPageResponse(emailTemplates, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EmailTemplateResponse getDetailEmailTeplate(Long emailTemplateId) {
        EmailTemplate emailTemplate = emailTemplateRepository.findById(emailTemplateId).orElse(null);
        if (emailTemplate != null && emailTemplate.getIsDeleted() == 0) {
            log.info("Exist email template in database");
            EmailTemplateResponse emailTemplateResponse = converter.entityToEmailTemplateReource(emailTemplate);
            return emailTemplateResponse;
        }
        log.info("Not Exist email template in database");
        return null;
    }

    @Override
    public void sendEmail(Long id, MultipartFile[] file) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            EmailTemplateDTO emailTemplateDTO = converter.
                    entityToEmailTemplateDTO
                            (emailTemplateRepository.findById(id).orElse(null));
            if (emailTemplateDTO != null) {
                Users fromUser = usersRepository.findById(emailTemplateDTO.getUserId()).orElse(null);
                log.info("Exist email template in database");
                String htmlContent = emailTemplateDTO.getHtmlContent();
                helper.setTo(emailTemplateDTO.getSendTo());
                helper.setSubject(emailTemplateDTO.getMailSubject());
                helper.setText(htmlContent, true);
                helper.setFrom("mailtrap@sales-innova.top");
                helper.setReplyTo("dungntkhe163962", "mailtrap@sales-innova.top");
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

    public void sendEmails(List<Long> leadIds, Long id, MultipartFile[] file) {
        long startTime = System.nanoTime();
        Optional<EmailTemplate> emailTemplateDTO = emailTemplateRepository.findById(id);
        if (emailTemplateDTO == null) {
            log.info("Email template not existed");
            return;
        }

        EmailTemplate emailTemplate = emailTemplateDTO.get();
        String emailContent = decodeHtmlEntities(emailTemplate.getHtmlContent());
        // Extract unique params from email content
        List<String> params = extractParams(emailContent);

        // Fetch all mappings for these params
        List<ParameterMapping> mappings = parameterMappingRepository.findByParamIn(params);

        // Fetch all necessary data for these leads in a single query
        Map<Long, Map<String, String>> leadData = fetchLeadData(leadIds, mappings);

        // Replace params with actual values and send emails
        for (Long leadId : leadIds) {
            String personalizedContent = emailContent;
            Map<String, String> dataForLead = leadData.get(leadId);

            for (ParameterMapping mapping : mappings) {
                String param = mapping.getParam();
                String columnValue = dataForLead.get(mapping.getColumnName());
                personalizedContent = personalizedContent.replace(param, columnValue);
            }
            // Fetch email and send
            String email = dataForLead.get("email");
            asyncSendEmail.sendEmail(file, email, emailTemplate, personalizedContent);
        }
    }

    @Override
    public List<ParameterMapping> getListParamEmail() {
        try {
            List<ParameterMapping> list = parameterMappingRepository.findAll();
            return list;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void sendMailLogCall(String sendTo, String sendFrom, String replyTo, String subject, String htmlContent, String bcc) {
        asyncSendEmail.sendMailLogCall(sendTo, sendFrom, replyTo, subject, htmlContent, bcc);
    }

    private List<String> extractParams(String content) {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(content);
        List<String> params = new ArrayList<>();
        while (matcher.find()) {
            params.add(matcher.group());
        }
        return params;
    }

    private Map<Long, Map<String, String>> fetchLeadData(List<Long> leadIds, List<ParameterMapping> mappings) {
        List<String> columns = mappings.stream()
                .map(ParameterMapping::getColumnName)
                .distinct()
                .collect(Collectors.toList());

        if (!columns.contains("email")) {
            columns.add("email");
        }

        String selectColumns = String.join(", ", columns);
        String sql = String.format("SELECT lead_id, %s FROM leads WHERE lead_id IN (%s)",
                selectColumns, leadIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Map<String, String>> result = new HashMap<>();
            while (rs.next()) {
                Long id = rs.getLong("lead_id");
                Map<String, String> leadData = result.computeIfAbsent(id, k -> new HashMap<>());
                for (String column : selectColumns.split(", ")) {
                    leadData.put(column, rs.getString(column));
                }
            }
            return result;
        });
    }

    public String decodeHtmlEntities(String content) {
        return StringEscapeUtils.unescapeHtml4(content);
    }

    public Date getCurrentLocalDate() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}