package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="lead_event")
public class LeadEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_event_id")
    private Long leadEventId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "lead_id")
    private Long leadId;
}
