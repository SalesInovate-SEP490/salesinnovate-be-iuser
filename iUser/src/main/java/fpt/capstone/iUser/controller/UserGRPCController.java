package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.notification.NotificationDTO;
import fpt.capstone.iUser.dto.response.RoleResponse;
import fpt.capstone.iUser.dto.response.UserResponse;
import fpt.capstone.iUser.service.*;
import fpt.capstone.proto.account.AccountDtoProto;
import fpt.capstone.proto.user.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class UserGRPCController extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    UserService userService;
    @Autowired
    LogCallService logCallService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    LogEmailService logEmailService;
    @Autowired
    EventService eventService;


    @Override
    public void getUser (GetUserRequest request, StreamObserver<GetUserResponse> responseObserver){
        String userId = request.getUserId();
        UserResponse userResponse = userService.getUsersById(userId);
        try {
            GetUserResponse getUserResponse ;

            if(userResponse != null){
                UserDtoProto proto = UserDtoProto.newBuilder()
                        .setUserId(userResponse.getUserId()==null?"":userResponse.getUserId())
                        .setUserName(userResponse.getUserName()==null?"":userResponse.getUserName())
                        .setFirstName(userResponse.getFirstName()==null?"":userResponse.getFirstName())
                        .setLastName(userResponse.getLastName()==null?"":userResponse.getLastName())
                        .setEmail(userResponse.getEmail()==null?"":userResponse.getEmail())
                        .build();
                getUserResponse = GetUserResponse.newBuilder()
                        .setResponse(proto)
                        .build();
            }else{
                getUserResponse = GetUserResponse.getDefaultInstance();
            }
            responseObserver.onNext(getUserResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void convertLogCallToAccCo(ConvertLogCallToAccCoRequest request,
                                      StreamObserver<ConvertLogCallToAccCoResponse> responseObserver) {
        long leadId = request.getLeadId();
        long accountId = request.getAccountId();
        long contactId = request.getContactId();
        boolean convertLog = logCallService.convertLogCallToAccCo(leadId,accountId, contactId);
        try {
            ConvertLogCallToAccCoResponse response = ConvertLogCallToAccCoResponse.newBuilder()
                    .setResponse(convertLog)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void convertLogCallToOpp(ConvertLogCallToOppRequest request,
                                    StreamObserver<ConvertLogCallToOppResponse> responseObserver) {
        long leadId = request.getLeadId();
        long opportunityId = request.getOpportunityId();
        boolean convertLog = logCallService.convertLogCallToOpp(leadId,opportunityId);
        try {
            ConvertLogCallToOppResponse response = ConvertLogCallToOppResponse.newBuilder()
                    .setResponse(convertLog)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void convertLogEmailToAccCo(ConvertLogEmailToAccCoRequest request,
                                      StreamObserver<ConvertLogEmailToAccCoResponse> responseObserver) {
        long leadId = request.getLeadId();
        long accountId = request.getAccountId();
        long contactId = request.getContactId();
        boolean convertLog = logEmailService.convertLogEmailToAccCo(leadId,accountId, contactId);
        try {
            ConvertLogEmailToAccCoResponse response = ConvertLogEmailToAccCoResponse.newBuilder()
                    .setResponse(convertLog)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void convertLogEmailToOpp(ConvertLogEmailToOppRequest request,
                                    StreamObserver<ConvertLogEmailToOppResponse> responseObserver) {
        long leadId = request.getLeadId();
        long opportunityId = request.getOpportunityId();
        boolean convertLog = logEmailService.convertLogEmailToOpp(leadId,opportunityId);
        try {
            ConvertLogEmailToOppResponse response = ConvertLogEmailToOppResponse.newBuilder()
                    .setResponse(convertLog)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void convertLogEventToAccCo(ConvertLogEventToAccCoRequest request,
                                       StreamObserver<ConvertLogEventToAccCoResponse> responseObserver) {
        long leadId = request.getLeadId();
        long accountId = request.getAccountId();
        long contactId = request.getContactId();
        boolean convertLog = eventService.convertLogEmailToAccCo(leadId,accountId, contactId);
        try {
            ConvertLogEventToAccCoResponse response = ConvertLogEventToAccCoResponse.newBuilder()
                    .setResponse(convertLog)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.asRuntimeException());
        }
    }

    @Override
    public void createNotification(CreateNotificationRequest request,
                                   StreamObserver<CreateNotificationResponse> responseObserver) {
        String userId = request.getUserId();
        String content = request.getContent();
        Long linkId = request.getLinkId();
        Long notificationType = request.getNotificationType();
        // Lấy danh sách người dùng từ request
        List<String> listUser = request.getListUserList();

        NotificationDTO dto = NotificationDTO.builder()
                .content(content)
                .linkId(linkId)
                .notificationType(notificationType)
                .build();

        boolean createNotification;
        try {
            createNotification = notificationService.createNotification(userId, dto, listUser);

            CreateNotificationResponse response = CreateNotificationResponse.newBuilder()
                    .setResponse(createNotification)
                    .build();

            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("An error occurred while creating notification").asRuntimeException());
            return;
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUserRoles(GetUserRolesRequest request,
                                   StreamObserver<GetUserRolesResponse> responseObserver) {
        String userId = request.getUserId();

        try {
            List<RoleResponse> userRoles = userService.getUserRoles(userId);
            // Tạo đối tượng response
            GetUserRolesResponse.Builder responseBuilder = GetUserRolesResponse.newBuilder();
            // Thêm các role vào response
            for (RoleResponse r : userRoles) {
                responseBuilder.addListRoles(r.getName());
            }
            // Xây dựng response
            GetUserRolesResponse response = responseBuilder.build();
            // Trả về response
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("An error occurred while get roles").asRuntimeException());
            return;
        } finally {
            responseObserver.onCompleted();
        }
    }

}
