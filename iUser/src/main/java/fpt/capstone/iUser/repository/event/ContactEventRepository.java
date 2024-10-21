package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.ContactEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactEventRepository extends JpaRepository<ContactEvent,Long>, JpaSpecificationExecutor<ContactEvent> {
}
