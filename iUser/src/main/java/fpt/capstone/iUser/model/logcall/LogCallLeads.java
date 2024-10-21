package fpt.capstone.iUser.model.logcall;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="log_call_leads")
public class LogCallLeads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_call_leads_id")
    private Long logCallLeadsId;
    @Column(name = "lead_id")
    private Long leadId;
    @Column(name = "log_call_id")
    private Long logCallId;
}
