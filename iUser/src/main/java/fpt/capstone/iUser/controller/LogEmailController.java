package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.logemail.LogEmailDTO;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.service.LogEmailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/log-email")
public class LogEmailController {
    @Autowired
    private final LogEmailService logEmailService;

    @PostMapping("/create")
    public ResponseData<?> createLogEmail(@RequestBody LogEmailDTO logEmailDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return logEmailService.createLogEmail(userId, logEmailDTO) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "create log email success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "create log email fail");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @GetMapping("/filter-lead/{leadId}")
    public ResponseData<?> filterLogEmailInLead(@PathVariable Long leadId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logEmailService.filterLogEmailInLead(leadId));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @GetMapping("/filter-account/{accountId}")
    public ResponseData<?> filterLogEmailInAccount(@PathVariable Long accountId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logEmailService.filterLogEmailInAccount(accountId));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @GetMapping("/filter-contact/{contactId}")
    public ResponseData<?> filterLogEmailInContact(@PathVariable Long contactId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logEmailService.filterLogEmailInContact(contactId));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @GetMapping("/filter-opportunity/{opportunityId}")
    public ResponseData<?> filterLogEmailInOpportunity(@PathVariable Long opportunityId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logEmailService.filterLogEmailInOpportunity(opportunityId));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @GetMapping("/get-log-email/{logEmailId}")
    public ResponseData<?> getLogEmailById(@PathVariable Long logEmailId) {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    logEmailService.getLogEmailById(logEmailId));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

    @DeleteMapping("/delete/{logEmailId}")
    public ResponseData<?> deleteLogEmail(@PathVariable Long logEmailId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return logEmailService.deleteLogEmail(userId, logEmailId) ?
                    new ResponseData<>(1, HttpStatus.OK.value(), "delete log email success") :
                    new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "delete log email fail");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), "System error! Please try again later.");
        }
    }

}
