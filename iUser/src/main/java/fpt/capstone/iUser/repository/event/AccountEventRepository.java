package fpt.capstone.iUser.repository.event;

import fpt.capstone.iUser.model.event.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEventRepository extends JpaRepository<AccountEvent,Long>, JpaSpecificationExecutor<AccountEvent> {
}
