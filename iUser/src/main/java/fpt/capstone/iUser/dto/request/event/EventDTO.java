package fpt.capstone.iUser.dto.request.event;

import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventRemindTime;
import fpt.capstone.iUser.model.event.EventSubject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long eventId;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long eventSubject;
    private Long eventPriority;

    private List<EventAssigneeDTO> eventAssigneeDTOS;

    private LeadEventDTO leadEventDTO;
    private List<ContactEventDTO> contactEventDTOS;
    private AccountEventDTO accountEventDTO;

    private Long eventRemindTime;

}
