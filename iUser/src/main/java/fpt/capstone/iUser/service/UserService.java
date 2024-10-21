package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.RoleDTO;
import fpt.capstone.iUser.dto.request.UsersDTO;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.dto.response.RoleResponse;
import fpt.capstone.iUser.dto.response.UserResponse;
import fpt.capstone.iUser.model.Role;
import fpt.capstone.iUser.model.Users;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
     UserResponse getUsersById(String id);
    List<RoleResponse> getUserRoles(String id);

    PageResponse<?> filterUser (Pageable pageable, String[] search);
    PageResponse<?> filterUserForAdmin (String adminId,Pageable pageable,String[] search);
    boolean createUser (String adminId,UsersDTO dto);
    boolean toggleUserEnabledStatus(String adminId,String userId);
    boolean patchUser (String userId, UsersDTO dto);
    Users getDetailUser (String userId);
    Users getUserProfile (String userId);
    boolean addRoleToUser (String adminId,String userId,List<RoleDTO> roleName);
    boolean removeRoleFromUser (String adminId,String userId,List<RoleDTO> roleName);
    List<Role> getListRole();
    boolean changePassword(String userId,String password);
}
