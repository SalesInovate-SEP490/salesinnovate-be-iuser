package fpt.capstone.iUser.dto.response.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactEventResponse {
    private Long contactEventId;
    private Long contactId;
    private String contactName;
    private Long eventId;
}
