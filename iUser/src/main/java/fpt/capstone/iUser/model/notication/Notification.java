package fpt.capstone.iUser.model.notication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.capstone.iUser.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;
    @Column(name = "content")
    private String content;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "link_id")
    private Long linkId;
    @Column(name = "from_user")
    private String fromUser;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                     CascadeType.REFRESH})
    @JoinTable(name = "notification_to_user",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "to_user")
    )
    @JsonIgnore
    List<Users> users;

}
