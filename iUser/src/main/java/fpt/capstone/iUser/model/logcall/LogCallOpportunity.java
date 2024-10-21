package fpt.capstone.iUser.model.logcall;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="log_call_opportunities")
public class LogCallOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_call_opportunities_id")
    private Long logCallOpportunitiesId;
    @Column(name = "opportunity_id")
    private Long opportunityId;
    @Column(name = "log_call_id")
    private Long logCallId;
}
