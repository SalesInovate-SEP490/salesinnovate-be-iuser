package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.EventStatus;
import fpt.capstone.iUser.model.event.EventSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatusRepository extends JpaRepository<EventStatus,Long> {
}
