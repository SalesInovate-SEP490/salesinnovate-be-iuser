package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="contact_event")
public class ContactEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_event_id")
    private Long contactEventId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "contact_id")
    private Long contactId;
}
