package fpt.capstone.iUser.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_user_roles_id")
    private Long userRoleId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "role_id")
    private String roleId;
}
