package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="event_assignee")
public class EventAssignee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_assignee_id")
    private Long eventAssigneeId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "user_id")
    private String userId;
}
