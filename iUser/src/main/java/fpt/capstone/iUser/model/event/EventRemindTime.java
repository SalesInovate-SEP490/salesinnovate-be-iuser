package fpt.capstone.iUser.model.event;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="event_remind_time")
public class EventRemindTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_remind_time_id")
    private Long eventRemindTimeId;
    @Column(name = "event_remind_time_content")
    private String eventRemindTimeContent;
    @Column(name = "event_remind_time_type")
    private String eventRemindTimeType;
    @Column(name = "event_remind_time_value")
    private Integer eventRemindTimeValue;

}
