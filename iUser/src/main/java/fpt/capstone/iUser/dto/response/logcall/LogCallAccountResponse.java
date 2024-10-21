package fpt.capstone.iUser.dto.response.logcall;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogCallAccountResponse {
    private Long logCallAccountId;
    private Long accountId;
    private String accountName;
    private Long logCallId;
}
