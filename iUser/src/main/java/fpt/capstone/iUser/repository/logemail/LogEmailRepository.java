package fpt.capstone.iUser.repository.logemail;

import fpt.capstone.iUser.model.logemail.LogEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEmailRepository extends JpaRepository<LogEmail, Long>, JpaSpecificationExecutor<LogEmail> {
}
