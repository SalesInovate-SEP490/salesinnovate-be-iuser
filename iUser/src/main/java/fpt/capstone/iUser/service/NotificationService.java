package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.notification.NotificationDTO;
import fpt.capstone.iUser.dto.response.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {
    boolean createNotification (String userId, NotificationDTO dto, List<String> listUser);
    boolean markAsRead (String userId,Long notificationId);
    boolean markAllAsRead (String userId);
    NotificationResponse getDetailNotification (String userId,Long notificationId);
    List<NotificationResponse>  getListNotification(String userId);
}
