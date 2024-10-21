package fpt.capstone.iUser.repository;

import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.model.event.LeadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<Users, String>, JpaSpecificationExecutor<Users> {
    Users findByUserId(String userId);

}
