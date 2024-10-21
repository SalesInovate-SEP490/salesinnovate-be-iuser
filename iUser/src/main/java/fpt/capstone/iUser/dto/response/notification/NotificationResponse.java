package fpt.capstone.iUser.dto.response.notification;

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
public class NotificationResponse {
    private Long notificationId;
    private String content;
    private LocalDateTime dateTime;
    private Long linkId;
    private String fromUserName;
    private String fromUserId;
    private NotificationType notificationType;
    private Boolean isSeen;

}
