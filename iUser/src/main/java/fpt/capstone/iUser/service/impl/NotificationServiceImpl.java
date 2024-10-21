package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.request.notification.NotificationDTO;
import fpt.capstone.iUser.dto.response.UserResponse;
import fpt.capstone.iUser.dto.response.notification.NotificationResponse;
import fpt.capstone.iUser.model.logcall.LogCallLeads;
import fpt.capstone.iUser.model.notication.Notification;
import fpt.capstone.iUser.model.notication.NotificationToUser;
import fpt.capstone.iUser.model.notication.NotificationType;
import fpt.capstone.iUser.repository.notification.NotificationRepository;
import fpt.capstone.iUser.repository.notification.NotificationToUserRepository;
import fpt.capstone.iUser.repository.notification.NotificationTypeRepository;
import fpt.capstone.iUser.service.NotificationService;
import fpt.capstone.iUser.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationToUserRepository notificationToUserRepository;
    private final UserService userService ;
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean createNotification(String userId, NotificationDTO dto, List<String> listUser) {
        try{
            NotificationType notificationType = dto.getNotificationType() == null ? null :
                    notificationTypeRepository.findById(dto.getNotificationType())
                            .orElse(null);

            Notification notification = Notification.builder()
                    .content(dto.getContent())
                    .dateTime(LocalDateTime.now())
                    .linkId(dto.getLinkId())
                    .fromUser(userId)
                    .notificationType(notificationType)
                    .build();

            notificationRepository.save(notification);
            //Them relation giua notification voi user
            for(String user : listUser) {
                Specification<NotificationToUser> spec = new Specification<NotificationToUser>() {
                    @Override
                    public Predicate toPredicate(Root<NotificationToUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("notificationId"), notification.getNotificationId()));
                        predicates.add(criteriaBuilder.equal(root.get("toUser"), user));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = notificationToUserRepository.exists(spec);
                if(!exists) {
                    NotificationToUser toUser = NotificationToUser.builder()
                            .notificationId(notification.getNotificationId())
                            .toUser(user)
                            .isSeen(false)
                            .build();
                    notificationToUserRepository.save(toUser);
                }
            }
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean markAsRead(String userId,Long notificationId) {
        try{
            Specification<NotificationToUser> spec = new Specification<NotificationToUser>() {
                @Override
                public Predicate toPredicate(Root<NotificationToUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("notificationId"), notificationId));
                    predicates.add(criteriaBuilder.equal(root.get("toUser"), userId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<NotificationToUser> list = notificationToUserRepository.findAll(spec);
            if(list.isEmpty()) return false ;
            for (NotificationToUser toUser :list){
                toUser.setIsSeen(true);
                notificationToUserRepository.save(toUser);
            }
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean markAllAsRead(String userId) {
        try{
            Specification<NotificationToUser> spec = new Specification<NotificationToUser>() {
                @Override
                public Predicate toPredicate(Root<NotificationToUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("toUser"), userId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<NotificationToUser> list = notificationToUserRepository.findAll(spec);
            for (NotificationToUser toUser :list){
                toUser.setIsSeen(true);
                notificationToUserRepository.save(toUser);
            }
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public NotificationResponse getDetailNotification(String userId,Long notificationId) {
        try{
            Specification<NotificationToUser> spec = new Specification<NotificationToUser>() {
                @Override
                public Predicate toPredicate(Root<NotificationToUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("notificationId"), notificationId));
                    predicates.add(criteriaBuilder.equal(root.get("toUser"), userId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<NotificationToUser> toUsers = notificationToUserRepository.findAll(spec);
            if(toUsers.isEmpty()) return null ;
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if(notification==null) return null;

            UserResponse userResponse = userService.getUsersById(notification.getFromUser());
            NotificationResponse response = NotificationResponse.builder()
                    .notificationId(notificationId)
                    .content(notification.getContent())
                    .dateTime(notification.getDateTime())
                    .linkId(notification.getLinkId())
                    .fromUserName(userResponse.getUserName())
                    .fromUserId(userResponse.getUserId())
                    .notificationType(notification.getNotificationType())
                    .isSeen(toUsers.get(0).getIsSeen())
                    .build();
            return response;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<NotificationResponse> getListNotification(String userId) {
        try{
            Specification<NotificationToUser> spec = new Specification<NotificationToUser>() {
                @Override
                public Predicate toPredicate(Root<NotificationToUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("toUser"), userId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<NotificationToUser> toUsers = notificationToUserRepository.findAll(spec);
            List<NotificationResponse> responseList = new ArrayList<>();
            for(NotificationToUser toUser: toUsers){
                responseList.add(getDetailNotification(userId,toUser.getNotificationId()));
            }
            return responseList;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
