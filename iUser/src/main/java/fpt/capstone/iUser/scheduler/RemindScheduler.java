package fpt.capstone.iUser.scheduler;

import fpt.capstone.iUser.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class RemindScheduler {

    private final EventService eventService;

    @Scheduled(fixedRate = 30000)
    public void sendEmailPassExpired(){
        LocalDateTime now = LocalDateTime.now();
        log.info("[JOB-REMINDER-TASK] START AT: " + now.toString());
        eventService.remindEvent();
    }
}
