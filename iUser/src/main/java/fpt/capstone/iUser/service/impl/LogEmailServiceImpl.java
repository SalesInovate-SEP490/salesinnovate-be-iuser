package fpt.capstone.iUser.service.impl;

import fpt.capstone.iUser.dto.request.logcall.LogCallContactDTO;
import fpt.capstone.iUser.dto.request.logemail.LogEmailContactDTO;
import fpt.capstone.iUser.dto.request.logemail.LogEmailDTO;
import fpt.capstone.iUser.dto.response.logemail.LogEmailAccountResponse;
import fpt.capstone.iUser.dto.response.logemail.LogEmailOpportunityResponse;
import fpt.capstone.iUser.dto.response.logemail.LogEmailResponse;
import fpt.capstone.iUser.model.logcall.LogCallAccount;
import fpt.capstone.iUser.model.logcall.LogCallContact;
import fpt.capstone.iUser.model.logcall.LogCallLeads;
import fpt.capstone.iUser.model.logcall.LogCallOpportunity;
import fpt.capstone.iUser.model.logemail.*;
import fpt.capstone.iUser.repository.logemail.*;
import fpt.capstone.iUser.service.EmailTemplateService;
import fpt.capstone.iUser.service.EventClientService;
import fpt.capstone.iUser.service.LogEmailService;
import fpt.capstone.proto.account.AccountDtoProto;
import fpt.capstone.proto.contact.ContactDtoProto;
import fpt.capstone.proto.lead.LeadDtoProto;
import fpt.capstone.proto.opportunity.OpportunityDtoProto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LogEmailServiceImpl implements LogEmailService {
    private final LogEmailRepository logEmailRepository;
    private final LogEmailAccountRepository logEmailAccountRepository;
    private final LogEmailContactRepository logEmailContactRepository;
    private final LogEmailOpportunityRepository logEmailOpportunityRepository;
    private final LogEmailLeadsRepository logEmailLeadsRepository;
    private final EventClientService eventClientService;
    private final EmailTemplateService emailTemplateService;

    @Override
    public boolean createLogEmail(String userId, LogEmailDTO logEmailDTO) {
        try {
            // Step 1: Log Email first
            LogEmail logEmail = LogEmail.builder()
                    .createdBy(userId)
                    .fromUser(logEmailDTO.getEmailFrom())
                    .toAddress(logEmailDTO.getEmailTo())
                    .mailSubject(logEmailDTO.getEmailSubject())
                    .htmlContent(logEmailDTO.getEmailContent())
                    .messageDate(LocalDateTime.now())
                    .build();
            logEmailRepository.save(logEmail);

            // Step 2: Check relation and save
            // 2.1 Log Email Account

            if (logEmailDTO.getLogEmailAccountDTO() != null) {
                AccountDtoProto proto = eventClientService.getAccount(logEmailDTO.getLogEmailAccountDTO().getAccountId());
                if (proto.getAccountId() == 0) throw new RuntimeException("Can not find account");
                Specification<LogEmailAccount> spec = new Specification<LogEmailAccount>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), logEmailDTO.getLogEmailAccountDTO().getAccountId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logEmailAccountRepository.exists(spec);
                if (!exists) {
                    LogEmailAccount logEmailAccount = LogEmailAccount.builder()
                            .logEmailId(logEmail.getLogEmailId())
                            .accountId(logEmailDTO.getLogEmailAccountDTO().getAccountId())
                            .build();
                    logEmailAccountRepository.save(logEmailAccount);
                }
            }

            // 2.2 Log Email Opportunity
            if (logEmailDTO.getLogEmailOpportunityDTO() != null) {
                OpportunityDtoProto proto = eventClientService.getOpportunity(logEmailDTO.getLogEmailOpportunityDTO().getOpportunityId());
                if (proto.getOpportunityId() == 0) throw new RuntimeException("Can not find opportunity");
                Specification<LogEmailOpportunity> spec = new Specification<LogEmailOpportunity>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("opportunityId"), logEmailDTO.getLogEmailOpportunityDTO().getOpportunityId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logEmailOpportunityRepository.exists(spec);
                if (!exists) {
                    LogEmailOpportunity logEmailOpportunity = LogEmailOpportunity.builder()
                            .logEmailId(logEmail.getLogEmailId())
                            .opportunityId(logEmailDTO.getLogEmailOpportunityDTO().getOpportunityId())
                            .build();
                    logEmailOpportunityRepository.save(logEmailOpportunity);
                }
            }

            // 2.3 Log Email Contact
            if (logEmailDTO.getLogEmailContactDTOs() != null && !logEmailDTO.getLogEmailContactDTOs().isEmpty()) {
                for (LogEmailContactDTO dto : logEmailDTO.getLogEmailContactDTOs()) {
                    ContactDtoProto proto = eventClientService.getContact(dto.getContactId());
                    if (proto.getContactId() == 0) throw new RuntimeException("Can not find contact");
                    Specification<LogEmailContact> spec = new Specification<LogEmailContact>() {
                        @Override
                        public Predicate toPredicate(Root<LogEmailContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                            predicates.add(criteriaBuilder.equal(root.get("contactId"), dto.getContactId()));
                            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        }
                    };
                    boolean exists = logEmailContactRepository.exists(spec);
                    if (!exists) {
                        LogEmailContact logEmailContact = LogEmailContact.builder()
                                .logEmailId(logEmail.getLogEmailId())
                                .contactId(dto.getContactId())
                                .build();
                        logEmailContactRepository.save(logEmailContact);
                    }
                }
            }

            // 2.4 Log Email Leads
            if (logEmailDTO.getLogEmailLeadsDTO() != null) {
                LeadDtoProto proto = eventClientService.getLead(logEmailDTO.getLogEmailLeadsDTO().getLeadId());
                if (proto.getLeadId() == 0) throw new RuntimeException("Can not find lead");
                Specification<LogEmailLeads> spec = new Specification<LogEmailLeads>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("leadId"), logEmailDTO.getLogEmailLeadsDTO().getLeadId()));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logEmailLeadsRepository.exists(spec);
                if (!exists) {
                    LogEmailLeads logEmailContact = LogEmailLeads.builder()
                            .logEmailId(logEmail.getLogEmailId())
                            .leadId(logEmailDTO.getLogEmailLeadsDTO().getLeadId())
                            .build();
                    logEmailLeadsRepository.save(logEmailContact);
                }
            }

            // Step 3: Send Email
            emailTemplateService.sendMailLogCall(
                    logEmailDTO.getEmailTo(),
                    logEmailDTO.getEmailFrom(),
                    logEmailDTO.getEmailFrom(),
                    logEmailDTO.getEmailSubject(),
                    logEmailDTO.getEmailContent(),
                    logEmailDTO.getBcc()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<LogEmail> filterLogEmailInLead(Long leadId) {
        try {
            Specification<LogEmailLeads> spec = new Specification<LogEmailLeads>() {
                @Override
                public Predicate toPredicate(Root<LogEmailLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailLeads> logEmailLeads = logEmailLeadsRepository.findAll(spec);
            List<LogEmail> logEmails = new ArrayList<>();
            for (LogEmailLeads leads : logEmailLeads) {
                if (leads != null && leads.getLogEmailId() != null)
                    logEmails.add(logEmailRepository.findById(leads.getLogEmailId()).orElse(null));
            }
            return logEmails;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogEmail> filterLogEmailInAccount(Long accountId) {
        try {
            Specification<LogEmailAccount> spec = new Specification<LogEmailAccount>() {
                @Override
                public Predicate toPredicate(Root<LogEmailAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailAccount> logEmailAccounts = logEmailAccountRepository.findAll(spec);
            List<LogEmail> logEmails = new ArrayList<>();
            for (LogEmailAccount account : logEmailAccounts) {
                if (account != null && account.getLogEmailId() != null)
                    logEmails.add(logEmailRepository.findById(account.getLogEmailId()).orElse(null));
            }
            return logEmails;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogEmail> filterLogEmailInContact(Long contactId) {
        try {
            Specification<LogEmailContact> spec = new Specification<LogEmailContact>() {
                @Override
                public Predicate toPredicate(Root<LogEmailContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailContact> logEmailContacts = logEmailContactRepository.findAll(spec);
            List<LogEmail> logEmails = new ArrayList<>();
            for (LogEmailContact contact : logEmailContacts) {
                if (contact != null && contact.getLogEmailId() != null)
                    logEmails.add(logEmailRepository.findById(contact.getLogEmailId()).orElse(null));
            }
            return logEmails;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LogEmail> filterLogEmailInOpportunity(Long opportunityId) {
        try {
            Specification<LogEmailOpportunity> spec = new Specification<LogEmailOpportunity>() {
                @Override
                public Predicate toPredicate(Root<LogEmailOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("opportunityId"), opportunityId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailOpportunity> logEmailOpportunities = logEmailOpportunityRepository.findAll(spec);
            List<LogEmail> logEmails = new ArrayList<>();
            for (LogEmailOpportunity opportunity : logEmailOpportunities) {
                if (opportunity != null && opportunity.getLogEmailId() != null)
                    logEmails.add(logEmailRepository.findById(opportunity.getLogEmailId()).orElse(null));
            }
            return logEmails;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LogEmailResponse getLogEmailById(Long logEmailId) {
        try {
            // Step 1: Get Log Email
            LogEmail logEmail = logEmailRepository.findById(logEmailId).orElse(null);
            if (logEmail == null) throw new RuntimeException("Can not get log email");

            // Step 2: Get Account and Opportunity by Log Email Id
            // 2.1 Get Account
            Specification<LogEmailAccount> spec1 = new Specification<LogEmailAccount>() {
                @Override
                public Predicate toPredicate(Root<LogEmailAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailAccount> accountEvents = logEmailAccountRepository.findAll(spec1);
            AccountDtoProto accountDtoProto = null;
            if (!accountEvents.isEmpty()) {
                accountDtoProto = eventClientService.getAccount(accountEvents.get(0).getAccountId());
            }

            // 2.2 Get Opportunity
            Specification<LogEmailOpportunity> spec3 = new Specification<LogEmailOpportunity>() {
                @Override
                public Predicate toPredicate(Root<LogEmailOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmail.getLogEmailId()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailOpportunity> opportunityEvents = logEmailOpportunityRepository.findAll(spec3);
            OpportunityDtoProto opportunityDtoProto = null;
            if (!opportunityEvents.isEmpty()) {
                opportunityDtoProto = eventClientService.getOpportunity(opportunityEvents.get(0).getOpportunityId());
            }

            // Step 3: Return response

            LogEmailResponse response = LogEmailResponse.builder()
                    .logEmailId(logEmail.getLogEmailId())
                    .emailFrom(logEmail.getFromUser())
                    .emailTo(logEmail.getToAddress())
                    .emailSubject(logEmail.getMailSubject())
                    .emailContent(logEmail.getHtmlContent())
                    .messageDate(logEmail.getMessageDate())
                    .createdBy(logEmail.getCreatedBy())
                    .build();
            if (accountDtoProto != null && accountDtoProto.getAccountId() != 0) {
                LogEmailAccountResponse logEmailAccountResponse = LogEmailAccountResponse.builder()
                        .accountId(accountDtoProto.getAccountId())
                        .accountName(accountDtoProto.getAccountName())
                        .logEmailId(logEmail.getLogEmailId())
                        .build();
                response.setLogEmailAccountResponse(logEmailAccountResponse);
            }
            if (opportunityDtoProto != null && opportunityDtoProto.getOpportunityId() != 0) {
                LogEmailOpportunityResponse logEmailOpportunityResponse = LogEmailOpportunityResponse.builder()
                        .opportunityId(opportunityDtoProto.getOpportunityId())
                        .opportunityName(opportunityDtoProto.getOpportunityName())
                        .logEmailId(logEmail.getLogEmailId())
                        .build();
                response.setLogEmailOpportunityResponse(logEmailOpportunityResponse);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public boolean deleteLogEmail(String userId, Long logEmailId) {
        try {
            // Step 1: Check logEmail exists
            LogEmail logEmail = logEmailRepository.findById(logEmailId).orElse(null);
            if (logEmail == null) throw new RuntimeException("Can not find log email");

            // Step 2: Delete relation first, then delete log email
            // 2.1 Delete Log Email Account
            Specification<LogEmailAccount> spec1 = new Specification<LogEmailAccount>() {
                @Override
                public Predicate toPredicate(Root<LogEmailAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmailId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailAccount> logEmailAccounts = logEmailAccountRepository.findAll(spec1);
            for (LogEmailAccount account : logEmailAccounts) {
                logEmailAccountRepository.delete(account);
            }

            // 2.2 Delete Log Email Opportunity
            Specification<LogEmailOpportunity> spec2 = new Specification<LogEmailOpportunity>() {
                @Override
                public Predicate toPredicate(Root<LogEmailOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmailId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailOpportunity> logEmailOpportunities = logEmailOpportunityRepository.findAll(spec2);
            for (LogEmailOpportunity opportunity : logEmailOpportunities) {
                logEmailOpportunityRepository.delete(opportunity);
            }

            // 2.3 Delete Log Email Contact
            Specification<LogEmailContact> spec3 = new Specification<LogEmailContact>() {
                @Override
                public Predicate toPredicate(Root<LogEmailContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmailId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailContact> logEmailContacts = logEmailContactRepository.findAll(spec3);
            for (LogEmailContact contact : logEmailContacts) {
                logEmailContactRepository.delete(contact);
            }

            // 2.4 Delete Log Email Leads
            Specification<LogEmailLeads> spec4 = new Specification<LogEmailLeads>() {
                @Override
                public Predicate toPredicate(Root<LogEmailLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("logEmailId"), logEmailId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailLeads> logEmailLeads = logEmailLeadsRepository.findAll(spec4);
            for (LogEmailLeads leads : logEmailLeads) {
                logEmailLeadsRepository.delete(leads);
            }

            // 2.5 Delete Log Email
            logEmailRepository.delete(logEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean convertLogEmailToAccCo(Long leadId, Long accountId, Long contactId) {
        try {
            //Liet ke cac quan he co trong lead
            Specification<LogEmailLeads> spec = new Specification<LogEmailLeads>() {
                @Override
                public Predicate toPredicate(Root<LogEmailLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailLeads> logEmailLeads = logEmailLeadsRepository.findAll(spec);
            for (LogEmailLeads leads : logEmailLeads){
                //Them quan he voi account
                Specification<LogEmailAccount> spec1 = new Specification<LogEmailAccount>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailAccount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), leads.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logEmailAccountRepository.exists(spec1);
                if (!exists) {
                    LogEmailAccount logCallAccount = LogEmailAccount.builder()
                            .logEmailId(leads.getLogEmailId())
                            .accountId(accountId)
                            .build();
                    logEmailAccountRepository.save(logCallAccount);
                }
                //Them quan he voi contact
                Specification<LogEmailContact> spec2 = new Specification<LogEmailContact>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailContact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), leads.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("contactId"), contactId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists1 = logEmailContactRepository.exists(spec2);
                if (!exists1) {
                    LogEmailContact logCallContact = LogEmailContact.builder()
                            .logEmailId(leads.getLogEmailId())
                            .contactId(contactId)
                            .build();
                    logEmailContactRepository.save(logCallContact);
                }
            }
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean convertLogEmailToOpp(Long leadId, Long opportunityId) {
        try {
            Specification<LogEmailLeads> spec = new Specification<LogEmailLeads>() {
                @Override
                public Predicate toPredicate(Root<LogEmailLeads> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.equal(root.get("leadId"), leadId));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            List<LogEmailLeads> logEmailLeads = logEmailLeadsRepository.findAll(spec);
            for (LogEmailLeads leads : logEmailLeads){
                //Them quan he voi opportunity
                Specification<LogEmailOpportunity> spec1 = new Specification<LogEmailOpportunity>() {
                    @Override
                    public Predicate toPredicate(Root<LogEmailOpportunity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(criteriaBuilder.equal(root.get("logEmailId"), leads.getLogEmailId()));
                        predicates.add(criteriaBuilder.equal(root.get("opportunityId"), opportunityId));
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    }
                };
                boolean exists = logEmailOpportunityRepository.exists(spec1);
                if (!exists) {
                    LogEmailOpportunity logCallOpportunity = LogEmailOpportunity.builder()
                            .logEmailId(leads.getLogEmailId())
                            .opportunityId(opportunityId)
                            .build();
                    logEmailOpportunityRepository.save(logCallOpportunity);
                }
            }
            //Xóa quan he của lead với log khi đã convert xong
            logEmailLeadsRepository.deleteAll(logEmailLeads);
            return true ;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
