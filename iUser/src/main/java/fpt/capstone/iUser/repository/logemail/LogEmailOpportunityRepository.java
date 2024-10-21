package fpt.capstone.iUser.repository.logemail;

import fpt.capstone.iUser.model.logemail.LogEmailContact;
import fpt.capstone.iUser.model.logemail.LogEmailOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEmailOpportunityRepository extends JpaRepository<LogEmailOpportunity, Long>, JpaSpecificationExecutor<LogEmailOpportunity>{
}
