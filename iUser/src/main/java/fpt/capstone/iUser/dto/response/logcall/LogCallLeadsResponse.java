package fpt.capstone.iUser.dto.response.logcall;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallLeadsResponse {
    private Long logCallLeadsId;
    private Long leadId;
    private String leadName;
    private Long logCallId;
}
