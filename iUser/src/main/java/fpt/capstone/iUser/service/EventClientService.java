package fpt.capstone.iUser.service;

import fpt.capstone.proto.account.AccountDtoProto;
import fpt.capstone.proto.account.AccountServiceGrpc;
import fpt.capstone.proto.account.GetAccountRequest;
import fpt.capstone.proto.account.GetAccountResponse;
import fpt.capstone.proto.contact.ContactDtoProto;
import fpt.capstone.proto.contact.ContactServiceGrpc;
import fpt.capstone.proto.contact.GetContactRequest;
import fpt.capstone.proto.contact.GetContactResponse;
import fpt.capstone.proto.lead.GetLeadRequest;
import fpt.capstone.proto.lead.GetLeadResponse;
import fpt.capstone.proto.lead.LeadDtoProto;
import fpt.capstone.proto.lead.LeadServiceGrpc;
import fpt.capstone.proto.opportunity.GetOpportunityRequest;
import fpt.capstone.proto.opportunity.GetOpportunityResponse;
import fpt.capstone.proto.opportunity.OpportunityDtoProto;
import fpt.capstone.proto.opportunity.OpportunityServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class EventClientService {
    @GrpcClient("iLead")
    LeadServiceGrpc.LeadServiceBlockingStub stub ;

    @GrpcClient("iContact")
    ContactServiceGrpc.ContactServiceBlockingStub stubContact ;

    @GrpcClient("iAccount")
    AccountServiceGrpc.AccountServiceBlockingStub stubAccount ;

    @GrpcClient("iOpportunity")
    OpportunityServiceGrpc.OpportunityServiceBlockingStub stubOpp ;

    public LeadDtoProto getLead (Long leadId){
        GetLeadRequest request = GetLeadRequest.newBuilder()
                .setLeadId(leadId)
                .build();
        GetLeadResponse response = stub.getLead(request);
        return response.getResponse();
    }

    public ContactDtoProto getContact (Long contactId){
        GetContactRequest request = GetContactRequest.newBuilder()
                .setContactId(contactId)
                .build();
        GetContactResponse response = stubContact.getContact(request);
        return response.getResponse();
    }

    public AccountDtoProto getAccount (Long accountId){
        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();
        GetAccountResponse response = stubAccount.getAccount(request);
        return response.getResponse();
    }

    public OpportunityDtoProto getOpportunity (Long opportunityId){
        GetOpportunityRequest request = GetOpportunityRequest.newBuilder()
                .setOpportunityId(opportunityId)
                .build();
        GetOpportunityResponse response = stubOpp.getOpportunity(request);
        return response.getResponse();
    }
}
