package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.EventPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPriorityRepository extends JpaRepository<EventPriority,Long> {
}
