package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.event.EventDTO;
import fpt.capstone.iUser.dto.request.logcall.LogCallDTO;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.service.LogCallService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/log-call")
public class LogCallController {
    @Autowired
    private final LogCallService logCallService;

    @PostMapping("/create")
    public ResponseData<?> createLogCall(@RequestBody LogCallDTO logCallDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return logCallService.createLogCall(userId,logCallDTO) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "create log call success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "create log call fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/{logCallId}")
    public ResponseData<?> getDetailLogCall(@PathVariable Long logCallId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.getDetailLogCall(logCallId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/filter-lead/{leadId}")
    public ResponseData<?> filterLogCallInLead(@PathVariable Long leadId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.filterLogCallInLead(leadId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/filter-account/{accountId}")
    public ResponseData<?> filterLogCallInAccount(@PathVariable Long accountId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.filterLogCallInAccount(accountId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/filter-contact/{contactId}")
    public ResponseData<?> filterLogCallInContact(@PathVariable Long contactId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.filterLogCallInContact(contactId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/filter-opportunity/{opportunityId}")
    public ResponseData<?> filterLogCallInOpportunity(@PathVariable Long opportunityId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.filterLogCallInOpportunity(opportunityId));
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/list-status")
    public ResponseData<?> getEventInCalendar() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logCallService.getListStatus());
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{logCallId}")
    public ResponseData<?> deleteLogCall(@PathVariable Long logCallId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return logCallService.deleteLogCall(userId,logCallId) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "delete LogCall success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "delete LogCall fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PatchMapping("/{logCallId}")
    public ResponseData<?> patchLogCall(@PathVariable Long logCallId,@RequestBody LogCallDTO logCallDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return logCallService.patchLogCall(userId,logCallId,logCallDTO) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "edit LogCall success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "edit LogCall fail");
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

}
