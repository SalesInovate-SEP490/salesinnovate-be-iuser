package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.AccountEvent;
import fpt.capstone.iUser.model.event.LeadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LeadEventRepository extends JpaRepository<LeadEvent,Long>, JpaSpecificationExecutor<LeadEvent> {
}
