package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.event.EventDTO;
import fpt.capstone.iUser.dto.response.event.AccountEventResponse;
import fpt.capstone.iUser.dto.response.event.ContactEventResponse;
import fpt.capstone.iUser.dto.response.event.EventResponse;
import fpt.capstone.iUser.dto.response.event.LeadEventResponse;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.model.event.Event;
import fpt.capstone.iUser.model.event.EventPriority;
import fpt.capstone.iUser.model.event.EventRemindTime;
import fpt.capstone.iUser.model.event.EventSubject;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    boolean createEvent (String userId,EventDTO eventDTO);
    List<Event> getEventInCalendar (String userId, LocalDateTime startTime, LocalDateTime endTime);
    EventResponse getEventDetail (String userId,Long eventId);
    boolean deleteEvent (Long eventId);
    boolean editEvent (Long eventId,EventDTO eventDTO);
    List<AccountEventResponse> searchAccount(Long eventId,String search);
    List<ContactEventResponse> searchContact(Long eventId, String search);
    List<LeadEventResponse> searchLead(Long eventId, String search);
    List<Users> searchUser(Long eventId, String search);
    List<EventSubject> getListEventSubject();
    List<EventPriority> getListEventPriority();
    List<EventRemindTime> getListEventRemindTime();
    List<Event> getEventInLead (String userId, Long leadID);
    List<Event> getEventInAccount (String userId, Long accountId);
    List<Event> getEventInContact (String userId, Long contactId);
    void remindEvent();
    public boolean convertLogEmailToAccCo (Long leadId,Long accountId,Long contactId);

}
