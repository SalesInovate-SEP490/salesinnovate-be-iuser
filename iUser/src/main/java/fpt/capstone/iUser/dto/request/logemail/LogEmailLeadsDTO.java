package fpt.capstone.iUser.dto.request.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailLeadsDTO {
    private Long logEmailLeadsId;
    private Long leadId;
    private Long logEmailId;
}
