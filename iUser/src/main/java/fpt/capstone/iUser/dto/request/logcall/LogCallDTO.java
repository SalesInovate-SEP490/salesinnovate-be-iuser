package fpt.capstone.iUser.dto.request.logcall;

import fpt.capstone.iUser.dto.request.event.AccountEventDTO;
import fpt.capstone.iUser.dto.request.event.ContactEventDTO;
import fpt.capstone.iUser.dto.request.event.LeadEventDTO;
import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventStatus;
import fpt.capstone.iUser.model.event.EventSubject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallDTO {
    private Date dueDate;
    private String logCallName;
    private String logCallComment;

    private Long eventSubject;
    private Long eventPriority;
    private Long eventStatus;

    private LogCallLeadsDTO logCallLeadsDTO;
    private List<LogCallContactDTO> logCallContactDTOS;
    private LogCallAccountDTO logCallAccountDTO;
    private LogCallOpportunityDTO logCallOpportunityDTO;
}
