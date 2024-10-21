package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.logemail.LogEmailDTO;
import fpt.capstone.iUser.dto.response.logemail.LogEmailResponse;
import fpt.capstone.iUser.model.logemail.LogEmail;

import java.util.List;

public interface LogEmailService {
    boolean createLogEmail(String userId, LogEmailDTO logEmailDTO);
    List<LogEmail> filterLogEmailInLead (Long leadId);
    List<LogEmail> filterLogEmailInAccount (Long accountId);
    List<LogEmail> filterLogEmailInContact (Long contactId);
    List<LogEmail> filterLogEmailInOpportunity (Long opportunityId);
    LogEmailResponse getLogEmailById(Long logEmailId);
    boolean deleteLogEmail(String userId, Long logEmailId);
    public boolean convertLogEmailToAccCo (Long leadId,Long accountId,Long contactId);
    public boolean convertLogEmailToOpp (Long leadId,Long opportunityId) ;
}
