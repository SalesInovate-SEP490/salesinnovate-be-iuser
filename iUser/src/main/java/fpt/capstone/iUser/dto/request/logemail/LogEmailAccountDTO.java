package fpt.capstone.iUser.dto.request.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailAccountDTO {
    private Long logEmailAccountId;
    private Long accountId;
    private Long logEmailId;
}
