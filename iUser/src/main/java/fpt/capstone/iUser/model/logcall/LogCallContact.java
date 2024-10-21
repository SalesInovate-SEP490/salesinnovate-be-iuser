package fpt.capstone.iUser.model.logcall;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="log_call_contacts")
public class LogCallContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_call_contacts_id")
    private Long logCallContactsId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "log_call_id")
    private Long logCallId;
}
