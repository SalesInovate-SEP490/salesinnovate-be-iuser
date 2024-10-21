package fpt.capstone.iUser.dto.response.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEventResponse {
    private Long accountEventId;
    private Long accountId;
    private String accountName;
    private Long eventId;
}
