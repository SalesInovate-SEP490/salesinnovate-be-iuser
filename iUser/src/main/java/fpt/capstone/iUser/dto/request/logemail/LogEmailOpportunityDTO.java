package fpt.capstone.iUser.dto.request.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailOpportunityDTO {
    private Long logEmailOpportunityId;
    private Long opportunityId;
    private Long logEmailId;
}
