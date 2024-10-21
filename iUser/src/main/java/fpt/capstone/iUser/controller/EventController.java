package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.event.EventDTO;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    @Autowired
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseData<?> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return eventService.createEvent(userId,eventDTO) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "create Event success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "create Event fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/get-list")
    public ResponseData<?> getEventInCalendar(@RequestParam LocalDateTime startTime,
                                              @RequestParam LocalDateTime endTime) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getEventInCalendar(userId, startTime, endTime));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/{eventId}")
    public ResponseData<?> getEventInCalendar(@PathVariable Long eventId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getEventDetail(userId,eventId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseData<?> deleteEvent(@PathVariable Long eventId) {
        try {
            return eventService.deleteEvent(eventId) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "delete event success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "delete event fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PatchMapping("/{eventId}")
    public ResponseData<?> editEvent(@PathVariable Long eventId,@RequestBody EventDTO eventDTO) {
        try {
            return eventService.editEvent(eventId,eventDTO) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "edit event success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "edit event fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/subject")
    public ResponseData<?> getListEventSubject() {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getListEventSubject());
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/priority")
    public ResponseData<?> getListEventPriority() {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getListEventPriority());
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/remind")
    public ResponseData<?> getListEventRemindTime() {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getListEventRemindTime());
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/get-in-lead")
    public ResponseData<?> getEventInLead(@RequestParam Long leadId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getEventInLead(userId,leadId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/get-in-account")
    public ResponseData<?> getEventInAccount(@RequestParam Long accountId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getEventInAccount(userId, accountId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/get-in-contact")
    public ResponseData<?> getEventInContact(@RequestParam Long contactId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    eventService.getEventInContact(userId, contactId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
