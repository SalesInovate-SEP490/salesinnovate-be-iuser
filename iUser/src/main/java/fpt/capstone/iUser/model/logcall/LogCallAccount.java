package fpt.capstone.iUser.model.logcall;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="log_call_account")
public class LogCallAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_call_account_id")
    private Long logCallAccountId;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "log_call_id")
    private Long logCallId;
}
