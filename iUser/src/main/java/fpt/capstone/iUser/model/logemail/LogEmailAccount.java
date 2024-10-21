package fpt.capstone.iUser.model.logemail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log_email_account")
public class LogEmailAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_email_account_id")
    private Long logEmailAccountId;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "log_email_id")
    private Long logEmailId;
}
