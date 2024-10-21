package fpt.capstone.iUser.model.logcall;

import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventStatus;
import fpt.capstone.iUser.model.event.EventSubject;
import jakarta.persistence.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "log_call")
public class LogCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_call_id")
    private Long logCallId;
    @Column(name = "due_date")
    private Date dueDate;
    @Column(name = "log_call_name")
    private String logCallName;
    @Column(name = "log_call_comment", length = 500)
    private String logCallComment;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "last_modify_by")
    private String lastModifyBy;
    @Column(name = "last_modify_date")
    private LocalDateTime lastModifyDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "event_subject_id")
    private EventSubject eventSubject;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "event_priority_id")
    private EventPriority eventPriority;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "event_status_id")
    private EventStatus eventStatus;
}

