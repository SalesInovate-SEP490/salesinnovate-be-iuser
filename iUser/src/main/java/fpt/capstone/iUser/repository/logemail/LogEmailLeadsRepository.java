package fpt.capstone.iUser.repository.logemail;

import fpt.capstone.iUser.model.logemail.LogEmail;
import fpt.capstone.iUser.model.logemail.LogEmailLeads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEmailLeadsRepository extends JpaRepository<LogEmailLeads, Long>, JpaSpecificationExecutor<LogEmailLeads> {
}
