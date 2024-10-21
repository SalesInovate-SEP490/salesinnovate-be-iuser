package fpt.capstone.iUser.repository;

import fpt.capstone.iUser.model.UserRole;
import fpt.capstone.iUser.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRoleRepository extends JpaRepository<UserRole, String>, JpaSpecificationExecutor<UserRole> {
}
