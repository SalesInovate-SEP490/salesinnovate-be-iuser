package fpt.capstone.iUser.dto.response.logcall;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallContactResponse {
    private Long logCallContactsId;
    private Long contactId;
    private String contactName;
    private Long logCallId;
}
