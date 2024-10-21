package fpt.capstone.iUser.dto.response.logemail;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
public class LogEmailResponse {
    private Long logEmailId;
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    private String emailContent;
    private String bcc;
    private String createdBy;
    private LocalDateTime messageDate;
    private LogEmailAccountResponse logEmailAccountResponse;
    private LogEmailOpportunityResponse logEmailOpportunityResponse;
}
