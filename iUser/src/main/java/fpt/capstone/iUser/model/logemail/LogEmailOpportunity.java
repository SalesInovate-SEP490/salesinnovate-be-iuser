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
@Table(name = "log_email_opportunities")
public class LogEmailOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_email_opportunities_id")
    private Long logEmailOpportunitiesId;
    @Column(name = "opportunity_id")
    private Long opportunityId;
    @Column(name = "log_email_id")
    private Long logEmailId;
}
