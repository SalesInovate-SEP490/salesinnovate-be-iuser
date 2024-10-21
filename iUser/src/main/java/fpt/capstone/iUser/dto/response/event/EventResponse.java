package fpt.capstone.iUser.dto.response.event;

import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventRemindTime;
import fpt.capstone.iUser.model.event.EventSubject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class EventResponse {
    private Long eventId;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String createdBy;

    private EventSubject eventSubject;
    private EventPriority eventPriority;
    private EventRemindTime eventRemindTime;

    private AccountEventResponse accountEventResponse;
    private List<ContactEventResponse> contactEventResponses ;
    private LeadEventResponse leadEventResponse;

    public void addContactEventResponse(ContactEventResponse response){
        contactEventResponses.add(response);
    }

    public EventResponse(Long eventId, String content, LocalDateTime startTime, LocalDateTime endTime, String createdBy, EventSubject eventSubject, EventPriority eventPriority, EventRemindTime eventRemindTime, AccountEventResponse accountEventResponse, List<ContactEventResponse> contactEventResponses, LeadEventResponse leadEventResponse) {
        this.eventId = eventId;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.eventSubject = eventSubject;
        this.eventPriority = eventPriority;
        this.eventRemindTime = eventRemindTime;
        this.accountEventResponse = accountEventResponse;
        this.contactEventResponses = new ArrayList<>();
        this.leadEventResponse = leadEventResponse;
    }
}
