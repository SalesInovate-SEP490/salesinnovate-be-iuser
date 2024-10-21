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
@Table(name = "log_email_leads")
public class LogEmailLeads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_email_leads_id")
    private Long logEmailLeadsId;
    @Column(name = "lead_id")
    private Long leadId;
    @Column(name = "log_email_id")
    private Long logEmailId;
}
