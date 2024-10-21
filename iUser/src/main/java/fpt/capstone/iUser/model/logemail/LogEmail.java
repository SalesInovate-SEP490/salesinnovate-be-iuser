package fpt.capstone.iUser.model.logemail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log_email")
public class LogEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_email_id")
    private Long logEmailId;
    @Column(name = "message_date")
    private LocalDateTime messageDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "from_user")
    private String fromUser;
    @Column(name = "to_address")
    private String toAddress;
    @Column(name = "mail_subject")
    private String mailSubject;
    @Column(name = "html_content")
    private String htmlContent;
}
