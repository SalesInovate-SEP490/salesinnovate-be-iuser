package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.Event;
import fpt.capstone.iUser.model.notication.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e JOIN EventAssignee ea ON e.eventId = ea.eventId " +
            "WHERE e.startTime >= :startTime AND e.endTime <= :endTime AND ea.userId = :userId")
    List<Event> findEventsByStartTimeEndTimeAndUserId(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("userId") String userId);


    @Query("SELECT e FROM Event e WHERE e.startTime >= :startTime AND e.startTime <= :endTime")
    List<Event> findEventsByStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime);
}
