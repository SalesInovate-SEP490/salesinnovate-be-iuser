package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.RoleDTO;
import fpt.capstone.iUser.dto.request.UsersDTO;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/user-filter")
    public ResponseData<?> filterUser(Pageable pageable,
                                             @RequestParam(required = false) String[] search) {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.filterUser(pageable, search));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "list filter user fail");
        }
    }

    @GetMapping("/admin-filter")
    public ResponseData<?> filterUserForAdmin(Pageable pageable,
                                             @RequestParam(required = false) String[] search) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.filterUserForAdmin(userId,pageable, search));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "list filter user fail");
        }
    }

    @PostMapping("/create-user")
    public ResponseData<?> createUser(@RequestBody UsersDTO dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.createUser(userId,dto));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "create user failed");
        }
    }

    @PostMapping("/toggle-user")
    public ResponseData<?> toggleUserEnabledStatus(@RequestParam String userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.toggleUserEnabledStatus(adminId,userId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "create user failed");
        }
    }

    @PatchMapping("/patch-user")
    public ResponseData<?> patchUser( @RequestBody UsersDTO dto) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return userService.patchUser(userId,dto) ?
                    new ResponseData<>(HttpStatus.OK.value(), "Update User success", 1)
                    : new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "Update User fail");
        }catch (Exception e){
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/detail-user")
    public ResponseData<?> getDetailUser(@RequestParam String userId) {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.getDetailUser(userId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "list filter user fail");
        }
    }

    @GetMapping("/profile-user")
    public ResponseData<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.getUserProfile(userId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "list filter user fail");
        }
    }

    @PostMapping("/add-role/{userId}")
    public ResponseData<?> addRoleToUser(@PathVariable String userId,
                                                   @RequestBody List<RoleDTO> roleName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.addRoleToUser(adminId,userId,roleName));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "add role to user failed");
        }
    }

    @DeleteMapping("/delete-role/{userId}")
    public ResponseData<?> removeRoleFromUser(@PathVariable String userId,
                                                   @RequestBody List<RoleDTO> roleName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.removeRoleFromUser(adminId,userId,roleName));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "add role to user failed");
        }
    }

    @GetMapping("/get-role")
    public ResponseData<?> getListRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    userService.getListRole());
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "list role fail");
        }
    }

    @PatchMapping("/change-password")
    public ResponseData<?> patchOpportunity(@RequestParam String password) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return userService.changePassword(userId,password) ?
                    new ResponseData<>(HttpStatus.OK.value(), "Update User success", 1)
                    : new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "Update User fail");
        }catch (Exception e){
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
