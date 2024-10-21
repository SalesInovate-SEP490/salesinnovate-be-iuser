package fpt.capstone.iUser.dto.response.logcall;


import fpt.capstone.iUser.dto.response.event.AccountEventResponse;
import fpt.capstone.iUser.dto.response.event.ContactEventResponse;
import fpt.capstone.iUser.dto.response.event.LeadEventResponse;
import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventStatus;
import fpt.capstone.iUser.model.event.EventSubject;
import fpt.capstone.iUser.model.logcall.LogCallLeads;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
public class LogCallResponse {
    private Long logCallId;
    private Date dueDate;
    private String logCallName;
    private String logCallComment;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifyBy;
    private LocalDateTime lastModifyDate;

    private EventSubject eventSubject;
    private EventPriority eventPriority;
    private EventStatus eventStatus;

    private LogCallAccountResponse accountResponse;
    private List<LogCallContactResponse> contactResponses ;
    private LogCallLeadsResponse leadsResponse ;
}
