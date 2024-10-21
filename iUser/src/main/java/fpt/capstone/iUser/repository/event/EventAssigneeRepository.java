package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.EventAssignee;
import fpt.capstone.iUser.model.notication.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAssigneeRepository extends JpaRepository<EventAssignee,Long>, JpaSpecificationExecutor<EventAssignee> {
}
