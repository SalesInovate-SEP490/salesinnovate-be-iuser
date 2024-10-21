package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.request.event.*;
import fpt.capstone.iUser.dto.request.notification.NotificationDTO;
import fpt.capstone.iUser.dto.response.event.AccountEventResponse;
import fpt.capstone.iUser.dto.response.event.ContactEventResponse;
import fpt.capstone.iUser.dto.response.event.EventResponse;
import fpt.capstone.iUser.dto.response.event.LeadEventResponse;
import fpt.capstone.iUser.model.EventFile;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.model.event.*;
import fpt.capstone.iUser.model.logemail.LogEmailAccount;
import fpt.capstone.iUser.model.logemail.LogEmailContact;
import fpt.capstone.iUser.model.logemail.LogEmailLeads;
import fpt.capstone.iUser.repository.FileEvenRepository;
import fpt.capstone.iUser.repository.UsersRepository;
import fpt.capstone.iUser.repository.event.*;
import fpt.capstone.iUser.service.EventClientService;
import fpt.capstone.iUser.service.EventService;
import fpt.capstone.iUser.service.NotificationService;
import fpt.capstone.proto.account.AccountDtoProto;
import fpt.capstone.proto.contact.ContactDtoProto;
import fpt.capstone.proto.lead.LeadDtoProto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventPriorityRepository eventPriorityRepository;
    private final EventSubjectRepository eventSubjectRepository;
    private final EventAssigneeRepository eventAssigneeRepository;
    private final EventRemindTimeRepository eventRemindTimeRepository;
    private final AccountEventRepository accountEventRepository;
    private final ContactEventRepository contactEventRepository;
    private final LeadEventRepository leadEventRepository;
    private final UsersRepository usersRepository;
    private final EventClientService eventClientService;
    private final FileEvenRepository fileEvenRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public boolean createEvent(String userId, EventDTO eventDTO) {
        try {
            //create event
            Event event = Event.builder()
                    .content(eventDTO.getContent())
                    .startTime(eventDTO.getStartTime())
                    .endTime(eventDTO.getEndTime())
                    .createdBy(userId)
                    .eventPriority(eventDTO.getEventPriority() == null ? null
                            : eventPriorityRepository.findById(eventDTO.getEventPriority()).orElse(null))
                    .eventSubject(eventDTO.getEventSubject() == null ? null
                            : eventSubjectRepository.findById(eventDTO.getEventSubject()).orElse(null))
                    .eventRemindTime(eventDTO.getEventRemindTime() == null ? null
                            : eventRemindTimeRepository.findById(eventDTO.getEventRemindTime()).orElse(null))
                    .build();
            eventRepository.save(event);

            //Thêm user vào trong event
            for (EventAssigneeDTO dto : eventDTO.getEventAssigneeDTOS()) {
                Specification<EventAssignee> spec = new Specification<EventAssignee>() {
                    @Override
                    public Predicate toPredicate(Root<EventAssignee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                        predicates.add(criteriaBuilder.equal(root.get("userId"), dto.getUserId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = eventAssigneeRepository.exists(spec);
                if (!exists) {
                    EventAssignee eventAssignee = EventAssignee.builder()
                            .eventId(event.getEventId())
                            .userId(dto.getUserId())
                            .build();
                    eventAssigneeRepository.save(eventAssignee);
                }
            }

            //Them nguoi tao vao trong event
            EventAssignee eventAssignee = EventAssignee.builder()
                    .eventId(event.getEventId())
                    .userId(userId)
                    .build();
            eventAssigneeRepository.save(eventAssignee);

            //Them relation cho lead
            if (eventDTO.getLeadEventDTO() != null) {
                LeadDtoProto proto = eventClientService.getLead(eventDTO.getLeadEventDTO().getLeadId());
                if (proto.getLeadId() == 0) throw new RuntimeException("Can not find lead");
                Specification<LeadEvent> spec = new Specification<LeadEvent>() {
                    @Override
                    public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                        predicates.add(criteriaBuilder.equal(root.get("leadId"), proto.getLeadId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = leadEventRepository.exists(spec);
                if (!exists) {
                    LeadEvent leadEvent = LeadEvent.builder()
                            .eventId(event.getEventId())
                            .leadId(proto.getLeadId())
                            .build();
                    leadEventRepository.save(leadEvent);
                    return true;
                }
                return false;
            }

            //Them relation cho contact va account
            if (!eventDTO.getContactEventDTOS().isEmpty()) {
                for (ContactEventDTO dto : eventDTO.getContactEventDTOS()) {
                    ContactDtoProto proto = eventClientService.getContact(dto.getContactId());
                    if (proto.getContactId() == 0) throw new RuntimeException("Can not find contact");
                    Specification<ContactEvent> spec = new Specification<ContactEvent>() {
                        @Override
                        public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                            predicates.add(criteriaBuilder.equal(root.get("contactId"), proto.getContactId()));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        }
                    };
                    boolean exists = contactEventRepository.exists(spec);
                    if (!exists) {
                        ContactEvent contactEvent = ContactEvent.builder()
                                .eventId(event.getEventId())
                                .contactId(proto.getContactId())
                                .build();
                        contactEventRepository.save(contactEvent);
                    }
                }
            }
            if (eventDTO.getAccountEventDTO() != null) {
                AccountDtoProto proto = eventClientService.getAccount(eventDTO.getAccountEventDTO().getAccountId());
                if (proto.getAccountId() == 0) throw new RuntimeException("Can not find account");
                Specification<AccountEvent> spec = new Specification<AccountEvent>() {
                    @Override
                    public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), proto.getAccountId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = accountEventRepository.exists(spec);
                if (!exists) {
                    AccountEvent accountEvent = AccountEvent.builder()
                            .eventId(event.getEventId())
                            .accountId(proto.getAccountId())
                            .build();
                    accountEventRepository.save(accountEvent);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Cannot add new Event");
        }
    }

    @Override
    public List<Event> getEventInCalendar(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Event> eventList = eventRepository
                    .findEventsByStartTimeEndTimeAndUserId(startTime, endTime, userId);
            return eventList;
        } catch (Exception e) {
            throw new RuntimeException("Can not get event in calendar");
        }
    }

    @Override
    public EventResponse getEventDetail(String userId, Long eventId) {
        try {
            Specification<Event> spec = new Specification<Event>() {
                @Override
                public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Join<Event, Users> join = root.join("users", JoinType.INNER);
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(join.get("userId"), userId));
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<Event> eventList = eventRepository.findAll(spec);
            if (eventList.isEmpty()) throw new RuntimeException("Can not find event");
            Event event = eventList.get(0);

            //Get Account
            Specification<AccountEvent> spec1 = new Specification<AccountEvent>() {
                @Override
                public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<AccountEvent> accountEvents = accountEventRepository.findAll(spec1);
            AccountDtoProto accountDtoProto = null;
            if (!accountEvents.isEmpty()) {
                accountDtoProto = eventClientService.getAccount(accountEvents.get(0).getAccountId());
            }

            //Get Contact
            Specification<ContactEvent> spec2 = new Specification<ContactEvent>() {
                @Override
                public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<ContactEvent> contactEvents = contactEventRepository.findAll(spec2);
            List<ContactDtoProto> contactDtoProtos = new ArrayList<>();
            for (ContactEvent contactEvent : contactEvents) {
                contactDtoProtos.add(eventClientService.getContact(contactEvent.getContactId()));
            }

            //Get Lead
            Specification<LeadEvent> spec3 = new Specification<LeadEvent>() {
                @Override
                public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), event.getEventId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LeadEvent> leadEvents = leadEventRepository.findAll(spec3);
            LeadDtoProto leadDtoProto = null;
            if (!leadEvents.isEmpty()) {
                leadDtoProto = eventClientService.getLead(leadEvents.get(0).getLeadId());
            }

            EventResponse response = EventResponse.builder()
                    .eventId(event.getEventId())
                    .content(event.getContent())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .createdBy(event.getCreatedBy())
                    .eventPriority(event.getEventPriority())
                    .eventSubject(event.getEventSubject())
                    .eventRemindTime(event.getEventRemindTime())
                    .build();
            if (accountDtoProto != null && accountDtoProto.getAccountId() != 0) {
                AccountEventResponse eventResponse = AccountEventResponse.builder()
                        .accountEventId(accountEvents.get(0).getAccountEventId())
                        .accountId(accountDtoProto.getAccountId())
                        .accountName(accountDtoProto.getAccountName())
                        .eventId(event.getEventId())
                        .build();
                response.setAccountEventResponse(eventResponse);
            }
            for (ContactDtoProto proto : contactDtoProtos) {
                ContactEventResponse eventResponse = ContactEventResponse.builder()
                        .contactEventId(contactEvents.get(0).getContactEventId())
                        .contactId(proto.getContactId())
                        .contactName(proto.getLastName() + " " + proto.getFirstName())
                        .eventId(event.getEventId())
                        .build();
                response.addContactEventResponse(eventResponse);
            }
            if (leadDtoProto != null && leadDtoProto.getLeadId() != 0) {
                LeadEventResponse eventResponse = LeadEventResponse.builder()
                        .leadEventId(leadEvents.get(0).getLeadEventId())
                        .leadId(leadDtoProto.getLeadId())
                        .leadName(leadDtoProto.getLastName() + " " + leadDtoProto.getLastName())
                        .eventId(event.getEventId())
                        .build();
                response.setLeadEventResponse(eventResponse);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Can not get event detail in calendar");
        }
    }

    @Override
    @Transactional
    public boolean deleteEvent(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event == null) return false;
            //Xoa event assignee
            Specification<EventAssignee> spec = new Specification<EventAssignee>() {
                @Override
                public Predicate toPredicate(Root<EventAssignee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<EventAssignee> list = eventAssigneeRepository.findAll(spec);
            eventAssigneeRepository.deleteAll(list);
            //Xoa account event
            Specification<AccountEvent> spec1 = new Specification<AccountEvent>() {
                @Override
                public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<AccountEvent> accountEvents = accountEventRepository.findAll(spec1);
            if (!accountEvents.isEmpty())
                accountEventRepository.deleteAll(accountEvents);
            //Xoa lead event
            Specification<LeadEvent> spec2 = new Specification<LeadEvent>() {
                @Override
                public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LeadEvent> leadEvents = leadEventRepository.findAll(spec2);
            if (!leadEvents.isEmpty())
                leadEventRepository.deleteAll(leadEvents);
            //Xoa contact event
            Specification<ContactEvent> spec3 = new Specification<ContactEvent>() {
                @Override
                public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<ContactEvent> contactEvents = contactEventRepository.findAll(spec3);
            if (!contactEvents.isEmpty())
                contactEventRepository.deleteAll(contactEvents);

            //Xoa event file
            Specification<EventFile> spec4 = new Specification<EventFile>() {
                @Override
                public Predicate toPredicate(Root<EventFile> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<EventFile> eventFiles = fileEvenRepository.findAll(spec4);
            if (!eventFiles.isEmpty())
                fileEvenRepository.deleteAll(eventFiles);

            eventRepository.delete(event);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean editEvent(Long eventId, EventDTO eventDTO) {
        try {
            Map<String, Object> patchMap = getPatchData(eventDTO);
            if (patchMap.isEmpty()) {
                return true;
            }

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EntityNotFoundException("Cannot find Event "));

            if (event != null) {
                for (Map.Entry<String, Object> entry : patchMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    Field fieldDTO = ReflectionUtils.findField(EventDTO.class, key);

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
                        case "eventSubject":
                            event.setEventSubject(eventSubjectRepository.findById((Long) value).orElse(null));
                            break;
                        case "eventPriority":
                            event.setEventPriority(eventPriorityRepository.findById((Long) value).orElse(null));
                            break;
                        case "eventRemindTime":
                            event.setEventRemindTime(eventRemindTimeRepository.findById((Long) value).orElse(null));
                            break;
                        case "eventAssigneeDTOS":
                            //Xóa quan hệ cũ
                            Specification<EventAssignee> spec = new Specification<EventAssignee>() {
                                @Override
                                public Predicate toPredicate(Root<EventAssignee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<EventAssignee> list = eventAssigneeRepository.findAll(spec);
                            eventAssigneeRepository.deleteAll(list);
                            //Thêm quan hệ mới
                            for (EventAssigneeDTO dto : (List<EventAssigneeDTO>) value) {
                                if (Objects.equals(event.getCreatedBy(), dto.getUserId())) continue;
                                EventAssignee assignee = EventAssignee.builder()
                                        .eventId(eventId)
                                        .userId(dto.getUserId())
                                        .build();
                                eventAssigneeRepository.save(assignee);
                            }
                            break;
                        case "leadEventDTO":
                            //Xóa quan hệ cũ
                            Specification<LeadEvent> spec1 = new Specification<LeadEvent>() {
                                @Override
                                public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<LeadEvent> list1 = leadEventRepository.findAll(spec1);
                            leadEventRepository.deleteAll(list1);
                            //Thêm quan hệ mới
                            LeadEventDTO dto = (LeadEventDTO) value;
                            if (((LeadEventDTO) value).getLeadId() == null) break;
                            LeadEvent leadEvent = LeadEvent.builder()
                                    .eventId(eventId)
                                    .leadId(dto.getLeadId())
                                    .build();
                            leadEventRepository.save(leadEvent);
                            break;
                        case "contactEventDTOS":
                            //Xóa quan hệ cũ
                            Specification<ContactEvent> spec2 = new Specification<ContactEvent>() {
                                @Override
                                public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<ContactEvent> contactEvents = contactEventRepository.findAll(spec2);
                            contactEventRepository.deleteAll(contactEvents);
                            //Thêm quan hệ mới
                            for (ContactEventDTO dto2 : (List<ContactEventDTO>) value) {
                                ContactEvent contactEvent = ContactEvent.builder()
                                        .eventId(eventId)
                                        .contactId(dto2.getContactId())
                                        .build();
                                contactEventRepository.save(contactEvent);
                            }
                            break;
                        case "accountEventDTO":
                            //Xóa quan hệ cũ
                            Specification<AccountEvent> spec3 = new Specification<AccountEvent>() {
                                @Override
                                public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("eventId"), eventId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<AccountEvent> list3 = accountEventRepository.findAll(spec3);
                            accountEventRepository.deleteAll(list3);
                            //Thêm quan hệ mới
                            AccountEventDTO dto3 = (AccountEventDTO) value;
                            if (((AccountEventDTO) value).getAccountId() == null) break;
                            AccountEvent accountEvent = AccountEvent.builder()
                                    .eventId(eventId)
                                    .accountId(dto3.getAccountId())
                                    .build();
                            accountEventRepository.save(accountEvent);
                            break;
                        default:
                            if (fieldDTO.getType().isAssignableFrom(value.getClass())) {
                                Field field = ReflectionUtils.findField(Event.class, fieldDTO.getName());
                                assert field != null;
                                field.setAccessible(true);
                                ReflectionUtils.setField(field, event, value);
                            } else {
                                return false;
                            }
                    }
                }
                eventRepository.save(event);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<AccountEventResponse> searchAccount(Long eventId, String search) {
        return List.of();
    }

    @Override
    public List<ContactEventResponse> searchContact(Long eventId, String search) {
        return List.of();
    }

    @Override
    public List<LeadEventResponse> searchLead(Long eventId, String search) {
        return List.of();
    }

    @Override
    public List<Users> searchUser(Long eventId, String search) {
        return List.of();
    }

    @Override
    public List<EventSubject> getListEventSubject() {
        return eventSubjectRepository.findAll();
    }

    @Override
    public List<EventPriority> getListEventPriority() {
        return eventPriorityRepository.findAll();
    }

    @Override
    public List<EventRemindTime> getListEventRemindTime() {
        return eventRemindTimeRepository.findAll();
    }

    @Override
    public List<Event> getEventInLead(String userId, Long leadId) {
        try{
            Specification<LeadEvent> spec1 = new Specification<LeadEvent>() {
                @Override
                public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LeadEvent> list1 = leadEventRepository.findAll(spec1);
            List<Event> eventList = new ArrayList<>();
            for(LeadEvent leadEvent : list1){
                eventList.add(eventRepository.findById(leadEvent.getEventId()).orElse(null));
            }

            // Sắp xếp eventList theo createTime giảm dần
            eventList.sort((e1, e2) -> {
                return e2.getStartTime().compareTo(e1.getStartTime());
            });
            return eventList;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Event> getEventInAccount(String userId, Long accountId) {
        try{
            Specification<AccountEvent> spec1 = new Specification<AccountEvent>() {
                @Override
                public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<AccountEvent> list1 = accountEventRepository.findAll(spec1);
            List<Event> eventList = new ArrayList<>();
            for(AccountEvent leadEvent : list1){
                eventList.add(eventRepository.findById(leadEvent.getEventId()).orElse(null));
            }

            // Sắp xếp eventList theo createTime giảm dần
            eventList.sort((e1, e2) -> {
                return e2.getStartTime().compareTo(e1.getStartTime());
            });
            return eventList;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Event> getEventInContact(String userId, Long contactId) {
        try{
            Specification<ContactEvent> spec1 = new Specification<ContactEvent>() {
                @Override
                public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<ContactEvent> list1 = contactEventRepository.findAll(spec1);
            List<Event> eventList = new ArrayList<>();
            for(ContactEvent leadEvent : list1){
                eventList.add(eventRepository.findById(leadEvent.getEventId()).orElse(null));
            }

            // Sắp xếp eventList theo createTime giảm dần
            eventList.sort((e1, e2) -> {
                return e2.getStartTime().compareTo(e1.getStartTime());
            });
            return eventList;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void remindEvent() {
        try {
            // Step 1: Get all event from now to 3 days ago (UTC + 7)
            LocalDateTime now = LocalDateTime.now().plusHours(7);
            LocalDateTime threeDaysAfter = now.plusDays(3);
            List<Event> eventList = eventRepository.findEventsByStartTimeEndTime(now, threeDaysAfter);
            // Step 2: Loop through all event and check if it is time to remind
            for (Event event : eventList) {
                EventRemindTime remindTime = event.getEventRemindTime();
                if(remindTime == null) continue; // Skip if remind time is null (event not set remind time)
                String remindTimeType = remindTime.getEventRemindTimeType(); // minute, hour, day
                int remindTimeValue = remindTime.getEventRemindTimeValue();
                LocalDateTime remindTimeStart = event.getStartTime();
                String userId = event.getCreatedBy();
                switch (remindTimeType) {
                    case "minute":
                        remindTimeStart = remindTimeStart.minusMinutes(remindTimeValue);
                        break;
                    case "hour":
                        remindTimeStart = remindTimeStart.minusHours(remindTimeValue);
                        break;
                    case "day":
                        remindTimeStart = remindTimeStart.minusDays(remindTimeValue);
                        break;
                }

                LocalDateTime truncatedNow = now.truncatedTo(ChronoUnit.MINUTES);
                LocalDateTime truncatedRemindTimeStart = remindTimeStart.truncatedTo(ChronoUnit.MINUTES);

                // Create a notification only if truncatedNow is equal to truncatedRemindTimeStart
                if (truncatedNow.isEqual(truncatedRemindTimeStart)) {
                    NotificationDTO notificationDTO = NotificationDTO.builder()
                            .content("Event " + event.getContent() + " is about to start")
                            .dateTime(LocalDateTime.now())
                            .linkId(event.getEventId())
                            .fromUser(userId)
                            .notificationType(6L)
                            .build();
                    notificationService.createNotification(userId, notificationDTO, List.of(userId));
                }
            }
        } catch (Exception e) {
            log.error("Error when remind event: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean convertLogEmailToAccCo(Long leadId, Long accountId, Long contactId) {
        try {
            //Liet ke cac quan he co trong lead
            Specification<LeadEvent> spec = new Specification<LeadEvent>() {
                @Override
                public Predicate toPredicate(Root<LeadEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LeadEvent> leadEvents = leadEventRepository.findAll(spec);
            for (LeadEvent leads : leadEvents){
                //Them quan he voi account
                Specification<AccountEvent> spec1 = new Specification<AccountEvent>() {
                    @Override
                    public Predicate toPredicate(Root<AccountEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("eventId"), leads.getEventId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = accountEventRepository.exists(spec1);
                if (!exists) {
                    AccountEvent logCallAccount = AccountEvent.builder()
                            .eventId(leads.getEventId())
                            .accountId(accountId)
                            .build();
                    accountEventRepository.save(logCallAccount);
                }
                //Them quan he voi contact
                Specification<ContactEvent> spec2 = new Specification<ContactEvent>() {
                    @Override
                    public Predicate toPredicate(Root<ContactEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("eventId"), leads.getEventId()));
                        predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists1 = contactEventRepository.exists(spec2);
                if (!exists1) {
                    ContactEvent logCallContact = ContactEvent.builder()
                            .eventId(leads.getEventId())
                            .contactId(contactId)
                            .build();
                    contactEventRepository.save(logCallContact);
                }
            }
            //Xóa quan he của lead với log khi đã convert xong
            leadEventRepository.deleteAll(leadEvents);
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
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
                log.info(e.getMessage(), e.getCause());
            }
        }
        return patchMap;
    }
}
