package fpt.capstone.iUser.repository;

import fpt.capstone.iUser.model.EventFile;
import fpt.capstone.iUser.model.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileEvenRepository extends JpaRepository<EventFile, Long>, JpaSpecificationExecutor<EventFile> {
    @Query("SELECT fv FROM EventFile fv WHERE fv.eventId = :eventId")
    Page<EventFile> findEventFileByEvent_EventId(@Param("eventId") Long eventId, Pageable pageable);
}
