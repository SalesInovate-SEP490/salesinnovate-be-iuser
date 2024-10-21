package fpt.capstone.iUser.repository.logcall;

import fpt.capstone.iUser.model.logcall.LogCall;
import fpt.capstone.iUser.model.logcall.LogCallContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogCallContactRepository extends JpaRepository<LogCallContact,Long>, JpaSpecificationExecutor<LogCallContact> {
}
