package fpt.capstone.iUser.model.logemail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log_email_contacts")
public class LogEmailContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_email_contacts_id")
    private Long logEmailContactsId;
    @Column(name = "contact_id")
    private Long contactId;
    @Column(name = "log_email_id")
    private Long logEmailId;
}
