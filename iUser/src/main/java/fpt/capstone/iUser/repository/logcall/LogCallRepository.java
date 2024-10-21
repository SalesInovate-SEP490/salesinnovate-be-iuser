package fpt.capstone.iUser.repository.logcall;

import fpt.capstone.iUser.model.event.ContactEvent;
import fpt.capstone.iUser.model.logcall.LogCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogCallRepository extends JpaRepository<LogCall,Long>, JpaSpecificationExecutor<LogCall> {
}
