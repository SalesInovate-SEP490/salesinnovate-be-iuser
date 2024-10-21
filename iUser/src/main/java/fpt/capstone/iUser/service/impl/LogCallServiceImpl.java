package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.request.event.*;
import fpt.capstone.iUser.dto.request.logcall.*;
import fpt.capstone.iUser.dto.response.event.AccountEventResponse;
import fpt.capstone.iUser.dto.response.event.ContactEventResponse;
import fpt.capstone.iUser.dto.response.event.EventResponse;
import fpt.capstone.iUser.dto.response.event.LeadEventResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallAccountResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallContactResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallLeadsResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallResponse;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.model.event.*;
import fpt.capstone.iUser.model.logcall.*;
import fpt.capstone.iUser.model.logemail.*;
import fpt.capstone.iUser.repository.event.EventPriorityRepository;
import fpt.capstone.iUser.repository.event.EventRepository;
import fpt.capstone.iUser.repository.event.EventStatusRepository;
import fpt.capstone.iUser.repository.event.EventSubjectRepository;
import fpt.capstone.iUser.repository.logcall.*;
import fpt.capstone.iUser.service.EventClientService;
import fpt.capstone.iUser.service.LogCallService;
import fpt.capstone.proto.account.AccountDtoProto;
import fpt.capstone.proto.contact.ContactDtoProto;
import fpt.capstone.proto.lead.LeadDtoProto;
import fpt.capstone.proto.opportunity.OpportunityDtoProto;
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
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class LogCallServiceImpl implements LogCallService {
    private final LogCallRepository logCallRepository;
    private final LogCallLeadsRepository logCallLeadsRepository;
    private final LogCallContactRepository logCallContactRepository;
    private final LogCallAccountRepository logCallAccountRepository;
    private final LogCallOpportunityRepository logCallOpportunityRepository;
    private final EventPriorityRepository eventPriorityRepository;
    private final EventSubjectRepository eventSubjectRepository;
    private final EventStatusRepository eventStatusRepository;
    private final EventClientService eventClientService;


    @Override
    @Transactional
    public boolean createLogCall(String userId, LogCallDTO logCallDTO) {
        try {
            //create event
            LogCall logCall = LogCall.builder()
                    .dueDate(logCallDTO.getDueDate())
                    .logCallName(logCallDTO.getLogCallName())
                    .logCallComment(logCallDTO.getLogCallComment())
                    .createdBy(userId)
                    .createdDate(LocalDateTime.now())
                    .lastModifyBy(userId)
                    .lastModifyDate(LocalDateTime.now())
                    .eventPriority(logCallDTO.getEventPriority() == null ? null
                            : eventPriorityRepository.findById(logCallDTO.getEventPriority()).orElse(null))
                    .eventSubject(logCallDTO.getEventSubject() == null ? null
                            : eventSubjectRepository.findById(logCallDTO.getEventSubject()).orElse(null))
                    .eventStatus(logCallDTO.getEventStatus() == null ? null
                            : eventStatusRepository.findById(logCallDTO.getEventStatus()).orElse(null))
                    .build();
            logCallRepository.save(logCall);

            //Them relation cho opportunity
            if (logCallDTO.getLogCallOpportunityDTO() != null) {
                OpportunityDtoProto proto = eventClientService.getOpportunity(logCallDTO.getLogCallOpportunityDTO().getOpportunityId());
                if (proto.getOpportunityId() == 0) throw new RuntimeException("Can not find lead");
                Specification<LogCallOpportunity> spec = new Specification<LogCallOpportunity>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("opportunityId"), proto.getOpportunityId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logCallOpportunityRepository.exists(spec);
                if (!exists) {
                    LogCallOpportunity logCallOpportunity = LogCallOpportunity.builder()
                            .logCallId(logCall.getLogCallId())
                            .opportunityId(proto.getOpportunityId())
                            .build();
                    logCallOpportunityRepository.save(logCallOpportunity);
                }
            }

            //Them relation cho lead
            if (logCallDTO.getLogCallLeadsDTO() != null) {
                LeadDtoProto proto = eventClientService.getLead(logCallDTO.getLogCallLeadsDTO().getLeadId());
                if (proto.getLeadId() == 0) throw new RuntimeException("Can not find lead");
                Specification<LogCallLeads> spec = new Specification<LogCallLeads>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("leadId"), proto.getLeadId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logCallLeadsRepository.exists(spec);
                if (!exists) {
                    LogCallLeads logCallLeads = LogCallLeads.builder()
                            .logCallId(logCall.getLogCallId())
                            .leadId(proto.getLeadId())
                            .build();
                    logCallLeadsRepository.save(logCallLeads);
                    return true;
                }
                return false;
            }

            //Them relation cho contact va account
            if (logCallDTO.getLogCallContactDTOS()!=null ) {
                for (LogCallContactDTO dto : logCallDTO.getLogCallContactDTOS()) {
                    ContactDtoProto proto = eventClientService.getContact(dto.getContactId());
                    if (proto.getContactId() == 0) throw new RuntimeException("Can not find contact");
                    Specification<LogCallContact> spec = new Specification<LogCallContact>() {
                        @Override
                        public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                            predicates.add(criteriaBuilder.equal(root.get("contactId"), proto.getContactId()));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        }
                    };
                    boolean exists = logCallContactRepository.exists(spec);
                    if (!exists) {
                        LogCallContact logCallContact = LogCallContact.builder()
                                .logCallId(logCall.getLogCallId())
                                .contactId(proto.getContactId())
                                .build();
                        logCallContactRepository.save(logCallContact);
                    }
                }
            }
            if (logCallDTO.getLogCallAccountDTO() != null) {
                AccountDtoProto proto = eventClientService.getAccount(logCallDTO.getLogCallAccountDTO().getAccountId());
                if (proto.getAccountId() == 0) throw new RuntimeException("Can not find account");
                Specification<LogCallAccount> spec = new Specification<LogCallAccount>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), proto.getAccountId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logCallAccountRepository.exists(spec);
                if (!exists) {
                    LogCallAccount logCallAccount = LogCallAccount.builder()
                            .logCallId(logCall.getLogCallId())
                            .accountId(proto.getAccountId())
                            .build();
                    logCallAccountRepository.save(logCallAccount);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Cannot add new Event");
        }
    }

    @Override
    @Transactional
    public boolean patchLogCall(String userId, Long logCallId, LogCallDTO logCallDTO) {
        try {
            Map<String, Object> patchMap = getPatchData(logCallDTO);
            if (patchMap.isEmpty()) {
                return true;
            }

            LogCall logCall = logCallRepository.findById(logCallId)
                    .orElseThrow(() -> new EntityNotFoundException("Cannot find LogCall"));

            if (logCall != null) {
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
                            logCall.setEventSubject(eventSubjectRepository.findById((Long) value).orElse(null));
                            break;
                        case "eventPriority":
                            logCall.setEventPriority(eventPriorityRepository.findById((Long) value).orElse(null));
                            break;
                        case "eventStatus":
                            logCall.setEventStatus(eventStatusRepository.findById((Long) value).orElse(null));
                            break;
                        case "logCallLeadsDTO":
                            //Xóa quan hệ cũ
                            Specification<LogCallLeads> spec1 = new Specification<LogCallLeads>() {
                                @Override
                                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<LogCallLeads> list1 = logCallLeadsRepository.findAll(spec1);
                            logCallLeadsRepository.deleteAll(list1);
                            //Thêm quan hệ mới
                            LogCallLeadsDTO dto = (LogCallLeadsDTO) value;
                            if(((LogCallLeadsDTO) value).getLeadId()==null) break;
                            LogCallLeads leadEvent = LogCallLeads.builder()
                                    .logCallId(logCallId)
                                    .leadId(dto.getLeadId())
                                    .build();
                            logCallLeadsRepository.save(leadEvent);
                            break;
                        case "logCallContactDTOS":
                            //Xóa quan hệ cũ
                            Specification<LogCallContact> spec2 = new Specification<LogCallContact>() {
                                @Override
                                public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<LogCallContact> contactEvents = logCallContactRepository.findAll(spec2);
                            logCallContactRepository.deleteAll(contactEvents);
                            //Thêm quan hệ mới
                            for (LogCallContactDTO dto2 : (List<LogCallContactDTO>) value) {
                                LogCallContact contactEvent = LogCallContact.builder()
                                        .logCallId(logCallId)
                                        .contactId(dto2.getContactId())
                                        .build();
                                logCallContactRepository.save(contactEvent);
                            }
                            break;
                        case "logCallAccountDTO":
                            //Xóa quan hệ cũ
                            Specification<LogCallAccount> spec3 = new Specification<LogCallAccount>() {
                                @Override
                                public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<LogCallAccount> list3 = logCallAccountRepository.findAll(spec3);
                            logCallAccountRepository.deleteAll(list3);
                            //Thêm quan hệ mới
                            LogCallAccountDTO dto3 = (LogCallAccountDTO) value;
                            if(((LogCallAccountDTO) value).getAccountId()==null) break;
                            LogCallAccount accountEvent = LogCallAccount.builder()
                                    .logCallId(logCallId)
                                    .accountId(dto3.getAccountId())
                                    .build();
                            logCallAccountRepository.save(accountEvent);
                            break;
                        case "logCallOpportunityDTO":
                            //Xóa quan hệ cũ
                            Specification<LogCallOpportunity> spec4 = new Specification<LogCallOpportunity>() {
                                @Override
                                public Predicate toPredicate(Root<LogCallOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                                    List<Predicate> predicates = new ArrayList<>();
                                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                                }
                            };
                            List<LogCallOpportunity> list4 = logCallOpportunityRepository.findAll(spec4);
                            logCallOpportunityRepository.deleteAll(list4);
                            //Thêm quan hệ mới
                            LogCallOpportunityDTO dto4 = (LogCallOpportunityDTO) value;
                            if(((LogCallOpportunityDTO) value).getOpportunityId()==null) break;
                            LogCallOpportunity opportunity = LogCallOpportunity.builder()
                                    .logCallId(logCallId)
                                    .opportunityId(dto4.getOpportunityId())
                                    .build();
                            logCallOpportunityRepository.save(opportunity);
                            break;
                        default:
                            if (fieldDTO.getType().isAssignableFrom(value.getClass())) {
                                Field field = ReflectionUtils.findField(Event.class, fieldDTO.getName());
                                assert field != null;
                                field.setAccessible(true);
                                ReflectionUtils.setField(field, logCall, value);
                            } else {
                                return false;
                            }
                    }
                }
                logCallRepository.save(logCall);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteLogCall(String userId, Long logCallId) {
        try {
            // Step 1: Check logEmail exists
            LogCall logCall = logCallRepository.findById(logCallId).orElse(null);
            if (logCall == null) throw new RuntimeException("Can not find log Call");

            // Step 2: Delete relation first, then delete log email
            // 2.1 Delete Log call Account
            Specification<LogCallAccount> spec1 = new Specification<LogCallAccount>() {
                @Override
                public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallAccount> logCallAccounts = logCallAccountRepository.findAll(spec1);
            for (LogCallAccount account : logCallAccounts) {
                logCallAccountRepository.delete(account);
            }

            // 2.2 Delete Log call Opportunity
            Specification<LogCallOpportunity> spec2 = new Specification<LogCallOpportunity>() {
                @Override
                public Predicate toPredicate(Root<LogCallOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallOpportunity> logEmailOpportunities = logCallOpportunityRepository.findAll(spec2);
            for (LogCallOpportunity opportunity : logEmailOpportunities) {
                logCallOpportunityRepository.delete(opportunity);
            }

            // 2.3 Delete Log Email Contact
            Specification<LogCallContact> spec3 = new Specification<LogCallContact>() {
                @Override
                public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallContact> logEmailContacts = logCallContactRepository.findAll(spec3);
            for (LogCallContact contact : logEmailContacts) {
                logCallContactRepository.delete(contact);
            }

            // 2.4 Delete Log Email Leads
            Specification<LogCallLeads> spec4 = new Specification<LogCallLeads>() {
                @Override
                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCallId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallLeads> logEmailLeads = logCallLeadsRepository.findAll(spec4);
            for (LogCallLeads leads : logEmailLeads) {
                logCallLeadsRepository.delete(leads);
            }

            // 2.5 Delete Log Email
            logCallRepository.delete(logCall);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public LogCallResponse getDetailLogCall(Long logCallId) {
        try {
            LogCall logCall = logCallRepository.findById(logCallId).orElse(null);
            if(logCall==null) throw new RuntimeException("Can not get log call");

            //Get Account
            Specification<LogCallAccount> spec1 = new Specification<LogCallAccount>() {
                @Override
                public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallAccount> accountEvents = logCallAccountRepository.findAll(spec1);
            AccountDtoProto accountDtoProto = null;
            if (!accountEvents.isEmpty()) {
                accountDtoProto = eventClientService.getAccount(accountEvents.get(0).getAccountId());
            }

            //Get Contact
            Specification<LogCallContact> spec2 = new Specification<LogCallContact>() {
                @Override
                public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallContact> contactEvents = logCallContactRepository.findAll(spec2);
            List<ContactDtoProto> contactDtoProtos = new ArrayList<>();
            for (LogCallContact contactEvent : contactEvents) {
                contactDtoProtos.add(eventClientService.getContact(contactEvent.getContactId()));
            }

            //Get Lead
            Specification<LogCallLeads> spec3 = new Specification<LogCallLeads>() {
                @Override
                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logCallId"), logCall.getLogCallId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallLeads> leadEvents = logCallLeadsRepository.findAll(spec3);
            LeadDtoProto leadDtoProto = null;
            if (!leadEvents.isEmpty()) {
                leadDtoProto = eventClientService.getLead(leadEvents.get(0).getLeadId());
            }

            LogCallResponse response = LogCallResponse.builder()
                    .logCallId(logCall.getLogCallId())
                    .dueDate(logCall.getDueDate())
                    .logCallName(logCall.getLogCallName())
                    .logCallComment(logCall.getLogCallComment())
                    .createdBy(logCall.getCreatedBy())
                    .createdDate(logCall.getCreatedDate())
                    .lastModifyBy(logCall.getLastModifyBy())
                    .lastModifyDate(logCall.getLastModifyDate())
                    .eventSubject(logCall.getEventSubject())
                    .eventPriority(logCall.getEventPriority())
                    .eventStatus(logCall.getEventStatus())
                    .build();
            if (accountDtoProto != null && accountDtoProto.getAccountId() != 0) {
                LogCallAccountResponse logCallAccountResponse = LogCallAccountResponse.builder()
//                        .logCallAccountId(accountEvents.get(0).getLogCallAccountId())
                        .accountId(accountDtoProto.getAccountId())
                        .accountName(accountDtoProto.getAccountName())
                        .logCallId(logCall.getLogCallId())
                        .build();
                response.setAccountResponse(logCallAccountResponse);
            }

            List<LogCallContactResponse> listResponses = new ArrayList<>();
            for (ContactDtoProto proto : contactDtoProtos) {
                if (proto != null && proto.getContactId() != 0) {
                    LogCallContactResponse eventResponse = LogCallContactResponse.builder()
//                            .logCallContactsId(contactEvents.get(0).getLogCallContactsId())
                            .contactId(proto.getContactId())
                            .contactName(proto.getLastName() + " " + proto.getFirstName())
                            .logCallId(logCall.getLogCallId())
                            .build();
                    listResponses.add(eventResponse);
                }
            }
            if(!listResponses.isEmpty()) response.setContactResponses(listResponses);

            if (leadDtoProto != null && leadDtoProto.getLeadId() != 0) {
                LogCallLeadsResponse eventResponse = LogCallLeadsResponse.builder()
//                        .logCallLeadsId(leadEvents.get(0).getLogCallLeadsId())
                        .leadId(leadDtoProto.getLeadId())
                        .leadName(leadDtoProto.getLastName() + " " + leadDtoProto.getLastName())
                        .logCallId(logCall.getLogCallId())
                        .build();
                response.setLeadsResponse(eventResponse);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Can not get event detail in calendar");
        }
    }


    @Override
    public List<LogCall> filterLogCallInLead(Long leadId) {
        try{
            Specification<LogCallLeads> spec = new Specification<LogCallLeads>() {
                @Override
                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallLeads> logCallLeads = logCallLeadsRepository.findAll(spec);
            List<LogCall> logCalls = new ArrayList<>();
            for (LogCallLeads leads : logCallLeads){
                if(leads != null && leads.getLogCallId()!=null)
                    logCalls.add(logCallRepository.findById(leads.getLogCallId()).orElse(null));
            }
            return logCalls;
        }catch (Exception e ){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogCall> filterLogCallInAccount(Long accountId) {
        try{
            Specification<LogCallAccount> spec = new Specification<LogCallAccount>() {
                @Override
                public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallAccount> logCallAccounts = logCallAccountRepository.findAll(spec);
            List<LogCall> logCalls = new ArrayList<>();
            for (LogCallAccount account : logCallAccounts){
                if(account != null && account.getLogCallId()!=null)
                    logCalls.add(logCallRepository.findById(account.getLogCallId()).orElse(null));
            }
            return logCalls;
        }catch (Exception e ){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogCall> filterLogCallInContact(Long contactId) {
        try{
            Specification<LogCallContact> spec = new Specification<LogCallContact>() {
                @Override
                public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallContact> logCallContacts = logCallContactRepository.findAll(spec);
            List<LogCall> logCalls = new ArrayList<>();
            for (LogCallContact contact : logCallContacts){
                if(contact != null && contact.getLogCallId()!=null)
                    logCalls.add(logCallRepository.findById(contact.getLogCallId()).orElse(null));
            }
            return logCalls;
        }catch (Exception e ){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogCall> filterLogCallInOpportunity(Long opportunityId) {
        try{
            Specification<LogCallOpportunity> spec = new Specification<LogCallOpportunity>() {
                @Override
                public Predicate toPredicate(Root<LogCallOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("opportunityId"), opportunityId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallOpportunity> logCallOpportunities = logCallOpportunityRepository.findAll(spec);
            List<LogCall> logCalls = new ArrayList<>();
            for (LogCallOpportunity opportunity : logCallOpportunities){
                if(opportunity != null && opportunity.getLogCallId()!=null)
                    logCalls.add(logCallRepository.findById(opportunity.getLogCallId()).orElse(null));
            }
            return logCalls;
        }catch (Exception e ){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<EventStatus> getListStatus() {
        return eventStatusRepository.findAll();
    }

    @Override
    public List<LogCallAccountResponse> searchAccount(Long logCallId, String search) {
        return List.of();
    }

    @Override
    public List<LogCallContactResponse> searchContact(Long logCallId, String search) {
        return List.of();
    }

    @Override
    public List<LogCallLeadsResponse> searchLead(Long logCallId, String search) {
        return List.of();
    }

    @Override
    @Transactional
    public boolean convertLogCallToAccCo(Long leadId, Long accountId, Long contactId) {
        try {
            //Liet ke cac quan he co trong lead
            Specification<LogCallLeads> spec = new Specification<LogCallLeads>() {
                @Override
                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallLeads> logCallLeads = logCallLeadsRepository.findAll(spec);
            for (LogCallLeads leads : logCallLeads){
                //Them quan he voi account
                Specification<LogCallAccount> spec1 = new Specification<LogCallAccount>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), leads.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logCallAccountRepository.exists(spec1);
                if (!exists) {
                    LogCallAccount logCallAccount = LogCallAccount.builder()
                            .logCallId(leads.getLogCallId())
                            .accountId(accountId)
                            .build();
                    logCallAccountRepository.save(logCallAccount);
                }
                //Them quan he voi contact
                Specification<LogCallContact> spec2 = new Specification<LogCallContact>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), leads.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists1 = logCallContactRepository.exists(spec2);
                if (!exists1) {
                    LogCallContact logCallContact = LogCallContact.builder()
                            .logCallId(leads.getLogCallId())
                            .contactId(contactId)
                            .build();
                    logCallContactRepository.save(logCallContact);
                }
            }
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean convertLogCallToOpp(Long leadId, Long opportunityId) {
        try {
            //Liet ke cac quan he co trong lead
            Specification<LogCallLeads> spec = new Specification<LogCallLeads>() {
                @Override
                public Predicate toPredicate(Root<LogCallLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogCallLeads> logCallLeads = logCallLeadsRepository.findAll(spec);
            for (LogCallLeads leads : logCallLeads){
                //Them quan he voi opportunity
                Specification<LogCallOpportunity> spec1 = new Specification<LogCallOpportunity>() {
                    @Override
                    public Predicate toPredicate(Root<LogCallOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logCallId"), leads.getLogCallId()));
                        predicates.add(criteriaBuilder.equal(root.get("opportunityId"), opportunityId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logCallOpportunityRepository.exists(spec1);
                if (!exists) {
                    LogCallOpportunity logCallOpportunity = LogCallOpportunity.builder()
                            .logCallId(leads.getLogCallId())
                            .opportunityId(opportunityId)
                            .build();
                    logCallOpportunityRepository.save(logCallOpportunity);
                }
            }
            //Xóa quan he của lead với log khi đã convert xong
            logCallLeadsRepository.deleteAll(logCallLeads);
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
