package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="event_subject")
public class EventSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_subject_id")
    private Long eventSubjectId;
    @Column(name = "event_subject_content")
    private String eventSubjectContent;
}
