package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.EventSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSubjectRepository extends JpaRepository<EventSubject,Long> {
}
