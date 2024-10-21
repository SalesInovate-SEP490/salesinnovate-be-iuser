package fpt.capstone.iUser.repository.notification;

import fpt.capstone.iUser.model.notication.Notification;
import fpt.capstone.iUser.model.notication.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType,Long> {
}
