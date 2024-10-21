package fpt.capstone.iUser.dto.response.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailAccountResponse {
    private Long logEmailAccountId;
    private Long accountId;
    private String accountName;
    private Long logEmailId;
}
