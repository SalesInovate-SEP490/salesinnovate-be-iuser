package fpt.capstone.iUser.repository.logemail;

import fpt.capstone.iUser.model.logemail.LogEmailAccount;
import fpt.capstone.iUser.model.logemail.LogEmailContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEmailContactRepository extends JpaRepository<LogEmailContact, Long>, JpaSpecificationExecutor<LogEmailContact> {
}
