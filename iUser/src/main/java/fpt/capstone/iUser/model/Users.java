package fpt.capstone.iUser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.capstone.iUser.model.event.Event;
import fpt.capstone.iUser.model.notication.Notification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class Users {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "username")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "background")
    private String background;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "avatar_id")
    private String avatarId;
    @Column(name = "created_at")
    private LocalDateTime createDate;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_information_id")
    private AddressInformation addressInformation;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles ;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "event_assignee",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @JsonIgnore
    private List<Event> events ;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "notification_to_user",
            joinColumns = @JoinColumn(name = "to_user"),
            inverseJoinColumns = @JoinColumn(name = "notification_id")
    )
    @JsonIgnore
    private List<Notification> notifications ;

}
