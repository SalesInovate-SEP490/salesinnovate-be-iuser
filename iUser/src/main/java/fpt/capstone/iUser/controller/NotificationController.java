package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private final NotificationService notificationService;

    @PostMapping("/mark-read")
    public ResponseData<?> markAsRead(@RequestParam Long notificationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return notificationService.markAsRead(userId,notificationId) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "mark as read success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "mark as read fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/mark-all-read")
    public ResponseData<?> markAllAsRead() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return notificationService.markAllAsRead(userId) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "mark all as read success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "mark all as read fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/{notificationId}")
    public ResponseData<?> getDetailNotification(@PathVariable Long notificationId ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    notificationService.getDetailNotification(userId,notificationId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseData<?> getDetailNotification() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    notificationService.getListNotification(userId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
