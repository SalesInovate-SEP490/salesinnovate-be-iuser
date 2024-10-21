package fpt.capstone.iUser.dto.request.logemail;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailDTO {
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    private String emailContent;
    private String bcc;
    private LogEmailLeadsDTO logEmailLeadsDTO;
    private List<LogEmailContactDTO> logEmailContactDTOs;
    private LogEmailAccountDTO logEmailAccountDTO;
    private LogEmailOpportunityDTO logEmailOpportunityDTO;
}
