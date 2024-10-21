package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.logcall.LogCallDTO;
import fpt.capstone.iUser.dto.response.logcall.LogCallAccountResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallContactResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallLeadsResponse;
import fpt.capstone.iUser.dto.response.logcall.LogCallResponse;
import fpt.capstone.iUser.model.event.EventStatus;
import fpt.capstone.iUser.model.logcall.LogCall;

import java.util.List;

public interface LogCallService {
    public boolean createLogCall(String userId, LogCallDTO logCallDTO);
    public boolean patchLogCall(String userId,Long logCallId, LogCallDTO logCallDTO);
    public boolean deleteLogCall(String userId, Long logCallId);
    public LogCallResponse getDetailLogCall (Long logCallId);
    public List<LogCall> filterLogCallInLead (Long leadId);
    public List<LogCall> filterLogCallInAccount (Long accountId);
    public List<LogCall> filterLogCallInContact (Long contactId);
    public List<LogCall> filterLogCallInOpportunity (Long opportunityId);
    public List<EventStatus> getListStatus();
    List<LogCallAccountResponse> searchAccount(Long logCallId, String search);
    List<LogCallContactResponse> searchContact(Long logCallId, String search);
    List<LogCallLeadsResponse> searchLead(Long logCallId, String search);
    public boolean convertLogCallToAccCo (Long leadId,Long accountId,Long contactId);
    public boolean convertLogCallToOpp (Long leadId,Long opportunityId) ;
}
