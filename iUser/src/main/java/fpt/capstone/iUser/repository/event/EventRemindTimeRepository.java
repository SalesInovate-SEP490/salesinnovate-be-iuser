package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.EventRemindTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRemindTimeRepository extends JpaRepository<EventRemindTime,Long> {
}
