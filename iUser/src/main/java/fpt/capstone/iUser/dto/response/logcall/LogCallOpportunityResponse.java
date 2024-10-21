package fpt.capstone.iUser.dto.response.logcall;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallOpportunityResponse {
    private Long logCallOpportunitiesId;
    private Long opportunityId;
    private String opportunityName;
    private Long logCallId;
}
