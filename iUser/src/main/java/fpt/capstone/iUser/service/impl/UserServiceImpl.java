package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.Converter;
import fpt.capstone.iUser.dto.request.RoleDTO;
import fpt.capstone.iUser.dto.request.UsersDTO;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.dto.response.RoleResponse;
import fpt.capstone.iUser.dto.response.UserResponse;
import fpt.capstone.iUser.model.AddressInformation;
import fpt.capstone.iUser.model.Role;
import fpt.capstone.iUser.model.UserRole;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.repository.*;
import fpt.capstone.iUser.repository.specification.SpecificationsBuilder;
import fpt.capstone.iUser.service.UserService;
import fpt.capstone.iUser.util.KeycloakSecurityUtil;
import jakarta.persistence.criteria.*;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fpt.capstone.iUser.util.AppConst.SEARCH_SPEC_OPERATOR;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final KeycloakSecurityUtil keycloakUtil ;
    @Autowired
    private final Converter converter;
    private final UsersRepository usersRepository;
    private final SearchRepository searchRepository;
    private final AddressInformationRepository addressInformationRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;


    @Override
    public UserResponse getUsersById(String id) {
        try{
            Users users = usersRepository.findById(id).orElse(null);
            if(users==null) throw new RuntimeException("Can not get User");
            if(!users.getIsActive()) throw new RuntimeException("Can not get User");
            return UserResponse.builder()
                    .userId(users.getUserId())
                    .userName(users.getUserName())
                    .firstName(users.getFirstName())
                    .lastName(users.getLastName())
                    .email(users.getEmail())
                    .build();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<RoleResponse> getUserRoles(String id) {
        Keycloak keycloak = keycloakUtil.getKeycloakInstance();
        List<RoleRepresentation> roles = keycloak.realm("master").users().get(id).roles().realmLevel().listAll();
        return converter.mapRoles(roles);
    }

    @Override
    public PageResponse<?> filterUser(Pageable pageable, String[] search) {
        try{
            SpecificationsBuilder builder = new SpecificationsBuilder();

            if (search != null) {
                Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
                for (String l : search) {
                    Matcher matcher = pattern.matcher(l);
                    if (matcher.find()) {
                        builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
                    }
                }

                Page<Users> page = searchRepository.searchUserByCriteriaWithJoin(null,builder.params, pageable);
                return converter.convertCommonToPageResponse(page, pageable);
            }
            return filterAllUsers(pageable.getPageNumber(),pageable.getPageSize());
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PageResponse<?> filterUserForAdmin(String adminId,Pageable pageable, String[] search) {
        try{
            List<RoleResponse> listRoles = getUserRoles(adminId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(!check)
                throw new RuntimeException("Only admin can get ");

            SpecificationsBuilder builder = new SpecificationsBuilder();

            if (search != null) {
                Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
                for (String l : search) {
                    Matcher matcher = pattern.matcher(l);
                    if (matcher.find()) {
                        builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
                    }
                }

                Page<Users> page = searchRepository.searchUserByCriteriaWithJoin(adminId,builder.params, pageable);
                return converter.convertCommonToPageResponse(page, pageable);
            }
            return filterAllUsers(pageable.getPageNumber(),pageable.getPageSize());
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean createUser(String adminId, UsersDTO usersDTO) {
        try{
            List<RoleResponse> listRoles = getUserRoles(adminId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(!check)
                throw new RuntimeException("Only admin can get ");

            //Lay ra thong tin cac user co tren keycloak
            Keycloak keycloak = keycloakUtil.getKeycloakInstance();
            List<UserRepresentation> users = keycloak.realm("master").users().search(usersDTO.getUserName());

            UserRepresentation userRep = converter.mapUserRep(usersDTO);
            Response res = keycloak.realm("master").users().create(userRep);

            if (!users.isEmpty()) {

                Users user = Users.builder()
                        .userId(users.get(0).getId())
                        .userName(usersDTO.getUserName())
                        .firstName(usersDTO.getFirstName())
                        .lastName(usersDTO.getLastName())
                        .email(usersDTO.getEmail())
                        .createDate(LocalDateTime.now())
                        .build();
                AddressInformation addressInformation = converter.DTOToAddressInformation(usersDTO.getAddressInformation());
                addressInformationRepository.save(addressInformation);
                user.setAddressInformation(addressInformation);
                usersRepository.save(user);

                for (RoleDTO role : usersDTO.getRoles()){
                    RoleRepresentation roleRepresentation = keycloak.realm("master").roles().get(role.getName()).toRepresentation();
                    keycloak.realm("master").users().get(users.get(0).getId()).roles().realmLevel().add(Collections.singletonList(roleRepresentation));

                    UserRole userRole= UserRole.builder()
                            .userId(users.get(0).getId())
                            .roleId(role.getId())
                            .build();
                    userRoleRepository.save(userRole);
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean toggleUserEnabledStatus(String adminId, String userId) {
        try{
            Users users =usersRepository.findById(userId).orElse(null);
            if(users==null){
                throw new RuntimeException("Can not find User");
            }

            List<RoleResponse> listRoles = getUserRoles(adminId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(!check)
                throw new RuntimeException("Only admin can get ");

            Keycloak keycloak = keycloakUtil.getKeycloakInstance();
            UserRepresentation user = keycloak.realm("master").users().get(userId).toRepresentation();
            boolean currentStatus = user.isEnabled();
            user.setEnabled(!currentStatus);
            keycloak.realm("master").users().get(userId).update(user);


            users.setIsActive(!users.getIsActive());
            usersRepository.save(users);
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean patchUser(String userId, UsersDTO dto) {
        try{
            //Check xem user co quyen sua hay khong
            Users users =usersRepository.findById(dto.getUserId()).orElse(null);
            if(users==null){
                throw new RuntimeException("Can not find User");
            }

            List<RoleResponse> listRoles = getUserRoles(userId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(userId.equals(dto.getUserId())) check=true;
            if(!check)
                throw new RuntimeException("Only admin can edit User Information ");

            Map<String, Object> patchMap = getPatchData(dto);
            if (patchMap.isEmpty()) {
                return true;
            }
            for (Map.Entry<String, Object> entry : patchMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Field fieldDTO = ReflectionUtils.findField(UsersDTO.class, key);

                if (fieldDTO == null) {
                    continue;
                }

                fieldDTO.setAccessible(true);
                Class<?> type = fieldDTO.getType();

                try {
                    if (type == long.class && value instanceof String) {
                        value = Long.parseLong((String) value);
                    } else if (type == Long.class && value instanceof String) {
                        value = Long.valueOf((String) value);
                    }
                } catch (NumberFormatException e) {
                    return false;
                }

                switch (key) {
                    case "userId":
                        break;
                    case "userName":
                        break;
                    case "addressInformation":
                        AddressInformation information = (AddressInformation) value;
                        if (!Objects.equals(information.getStreet(), users.getAddressInformation().getStreet()))
                            users.getAddressInformation().setStreet(information.getStreet());
                        if (!Objects.equals(information.getCity(), users.getAddressInformation().getCity()))
                            users.getAddressInformation().setCity(information.getCity());
                        if (!Objects.equals(information.getProvince(), users.getAddressInformation().getProvince()))
                            users.getAddressInformation().setProvince(information.getProvince());
                        if (!Objects.equals(information.getPostalCode(), users.getAddressInformation().getPostalCode()))
                            users.getAddressInformation().setPostalCode(information.getPostalCode());
                        if (!Objects.equals(information.getCountry(), users.getAddressInformation().getCountry()))
                            users.getAddressInformation().setCountry(information.getCountry());
                        break;
                    default:
                        if (fieldDTO.getType().isAssignableFrom(value.getClass())) {
                            Field field = ReflectionUtils.findField(Users.class, fieldDTO.getName());
                            field.setAccessible(true);
                            ReflectionUtils.setField(field, users, value);
                        } else {
                            return false;
                        }
                }
            }
            usersRepository.save(users);
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Users getDetailUser(String userId) {
        try{
            Specification<Users> spec = new Specification<Users>() {
                @Override
                public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), true));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            boolean exist = usersRepository.exists(spec);
            if(!exist) throw new RuntimeException("Can not get user");

            return usersRepository.findById(userId).orElse(null);

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Users getUserProfile(String userId) {
        try{
            Specification<Users> spec = new Specification<Users>() {
                @Override
                public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), true));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            boolean exist = usersRepository.exists(spec);
            if(!exist) throw new RuntimeException("Can not get user");

            return usersRepository.findById(userId).orElse(null);

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean addRoleToUser(String adminId, String userId, List<RoleDTO> roleNames) {
        try{
            Users users =usersRepository.findById(userId).orElse(null);
            if(users==null){
                throw new RuntimeException("Can not find User");
            }

            List<RoleResponse> listRoles = getUserRoles(adminId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(!check)
                throw new RuntimeException("Only admin can access ");

            for(RoleDTO roleName: roleNames){
                Keycloak keycloak = keycloakUtil.getKeycloakInstance();
                RoleRepresentation role = keycloak.realm("master").roles().get(roleName.getName()).toRepresentation();
                keycloak.realm("master").users().get(userId).roles().realmLevel().add(Arrays.asList(role));

                Specification<UserRole> spec = new Specification<UserRole>() {
                    @Override
                    public Predicate toPredicate(Root<UserRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                        predicates.add(criteriaBuilder.equal(root.get("roleId"), roleName.getId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };

                if(!userRoleRepository.exists(spec)){
                    UserRole userRole = UserRole.builder()
                            .userId(userId)
                            .roleId(roleName.getId())
                            .build();
                    userRoleRepository.save(userRole);
                }
            }
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean removeRoleFromUser(String adminId, String userId, List<RoleDTO> roleNames) {
        try{
            Users users =usersRepository.findById(userId).orElse(null);
            if(users==null){
                throw new RuntimeException("Can not find User");
            }

            List<RoleResponse> listRoles = getUserRoles(adminId);
            boolean check =false ;
            for(RoleResponse role : listRoles){
                if(Objects.equals(role.getName(), "administrator")) {
                    check = true;
                    break;
                }
            }
            if(!check)
                throw new RuntimeException("Only admin can access ");

            for(RoleDTO roleName: roleNames){
                Keycloak keycloak = keycloakUtil.getKeycloakInstance();
                RoleRepresentation role = keycloak.realm("master").roles().get(roleName.getName()).toRepresentation();
                keycloak.realm("master").users().get(userId).roles().realmLevel().remove(Collections.singletonList(role));

                Specification<UserRole> spec = new Specification<UserRole>() {
                    @Override
                    public Predicate toPredicate(Root<UserRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                        predicates.add(criteriaBuilder.equal(root.get("roleId"), roleName.getId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };

                List<UserRole> userRoleList= userRoleRepository.findAll(spec);
                userRoleRepository.deleteAll(userRoleList);
            }
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Role> getListRole() {
        try {
            return roleRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    private PageResponse<?> filterAllUsers(int pageNo, int pageSize) {
        try{

            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(new Sort.Order(Sort.Direction.DESC, "createDate"));

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));

            Specification<Users> spec = new Specification<Users>() {
                @Override
                public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), true));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<Users> usersPage = usersRepository.findAll(spec, pageable);
            return converter.convertCommonToPageResponse(usersPage, pageable);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }



    public String createUser(UsersDTO usersDTO) {
        try {
            return null;
        }catch (Exception e){
            log.info(e.getMessage(),e.getCause());
            throw(e);
        }
    }

    public Boolean updateUser(String userId, UsersDTO usersDTO) {
        try {
            UserRepresentation userRep = converter.mapUserRep(usersDTO);
            Keycloak keycloak = keycloakUtil.getKeycloakInstance();
            keycloak.realm("master").users().get(userId).update(userRep);
            return true ;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean changePassword (String userId, String newPassword) {
        try {
            Keycloak keycloak = keycloakUtil.getKeycloakInstance();
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            keycloak.realm("master").users().get(userId).resetPassword(credential);
            return true;
        }catch (Exception e){
            log.info(e.getMessage());
            throw new RuntimeException("Failed to update password user");
        }
    }

    public boolean addRoleToUser(String id, String roleName) {
        try{
            Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		RoleRepresentation role = keycloak.realm("master").roles().get(roleName).toRepresentation();
		keycloak.realm("master").users().get(id).roles().realmLevel().add(Arrays.asList(role));
            return true ;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    private Map<String, Object> getPatchData(Object obj) {
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Map<String, Object> patchMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    patchMap.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                log.info(e.getMessage());
            }
        }
        return patchMap;
    }


}
