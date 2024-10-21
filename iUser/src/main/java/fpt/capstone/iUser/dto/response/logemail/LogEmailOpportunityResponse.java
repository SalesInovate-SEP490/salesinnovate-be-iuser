package fpt.capstone.iUser.dto.response.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailOpportunityResponse {
    private Long logEmailOpportunityId;
    private Long opportunityId;
    private String opportunityName;
    private Long logEmailId;
}
