package fpt.capstone.iUser.model;

import fpt.capstone.iUser.model.event.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="event_file")
public class EventFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_file_id")
    private Long eventFileId;
    @Column(name = "event_id")
    private Long eventId;
    @OneToOne
    @JoinColumn(name = "file_id")
    private FileManager fileManager;
}
