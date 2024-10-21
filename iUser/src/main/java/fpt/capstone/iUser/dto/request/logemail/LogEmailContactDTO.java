package fpt.capstone.iUser.dto.request.logemail;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LogEmailContactDTO {
    private Long logEmailContactsId;
    private Long contactId;
    private Long logEmailId;
}
