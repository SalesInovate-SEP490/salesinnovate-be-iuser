package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="account_event")
public class AccountEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_event_id")
    private Long accountEventId;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "account_id")
    private Long accountId;
}
