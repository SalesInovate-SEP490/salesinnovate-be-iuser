package fpt.capstone.iUser.model.notication;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="notification_to_user")
public class NotificationToUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_to_user_id")
    private Long notificationToUserId;
    @Column(name = "notification_id")
    private Long notificationId;
    @Column(name = "to_user")
    private String toUser;
    @Column(name = "is_seen")
    private Boolean isSeen;
}
