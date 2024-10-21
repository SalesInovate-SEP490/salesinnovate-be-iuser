package fpt.capstone.iUser.dto.request.logcall;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallContactDTO {
    private Long logCallContactsId;
    private Long contactId;
    private Long logCallId;
}
