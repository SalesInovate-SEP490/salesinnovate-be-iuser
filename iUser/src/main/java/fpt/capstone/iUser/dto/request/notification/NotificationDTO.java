package fpt.capstone.iUser.dto.request.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.model.notication.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String content;
    private LocalDateTime dateTime;
    private Long linkId;
    private String fromUser;
    private Long notificationType;

}
