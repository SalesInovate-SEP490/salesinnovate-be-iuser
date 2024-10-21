package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="event_priority")
public class EventPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_priority_id")
    private Long eventPriorityId;
    @Column(name = "event_priority_content")
    private String eventPriorityContent;
}
