package fpt.capstone.iUser.dto.request.logcall;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallAccountDTO {
    private Long logCallAccountId;
    private Long accountId;
    private Long logCallId;
}
