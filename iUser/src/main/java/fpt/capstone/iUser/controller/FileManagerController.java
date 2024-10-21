package fpt.capstone.iUser.controller;

import fpt.capstone.iUser.dto.request.FileShareDTO;
import fpt.capstone.iUser.dto.response.FileDetailResponse;
import fpt.capstone.iUser.dto.response.FileResponse;
import fpt.capstone.iUser.dto.response.ResponseData;
import fpt.capstone.iUser.dto.response.ResponseError;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.service.FileManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/file-manager")
public class FileManagerController {
    private final FileManagerService fileManagerService;
    @PostMapping("/upload")
    public ResponseData<?> uploadFile(
            @RequestParam(value  = "file", required = false) MultipartFile file)
    {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            String fileName = file.getOriginalFilename();
            File templFile = File.createTempFile(file.getOriginalFilename(), null);
            file.transferTo(templFile);
            FileResponse fileResponse = fileManagerService.uploadFile(templFile,fileName,userId);
            return new ResponseData<>(HttpStatus.OK.value(),"Upload file success", fileResponse,1);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Upload file failed");
        }
    }
    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        FileDetailResponse fileDetailResponse = fileManagerService.getFileFromDrive(fileId);
        if (fileDetailResponse != null) {
            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
            if (fileDetailResponse.getContentType() != null) {
                responseBuilder.header(HttpHeaders.CONTENT_TYPE, fileDetailResponse.getContentType());
            }
            return responseBuilder.body(fileDetailResponse.getResource());

        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/get-list-file")
    public ResponseData<?> getListFile(
            @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage
    ) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    fileManagerService.getListFileFromDrive(userId, currentPage, perPage));
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Get list file failed");
        }
    }
    @DeleteMapping("/delete-file-share/{id}")
    public ResponseData<?> deleteFileShare(
            @PathVariable(name = "id") Long id
    ) {
        try {
            boolean isDelete = fileManagerService.deleteFileShare(id);
            if(isDelete == true){
                return new ResponseData<>(HttpStatus
                        .OK.value(), "Delete File success", 1);
            }
            return new ResponseData<>(HttpStatus
                    .NOT_FOUND.value(), "Not exist File share", 0);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @DeleteMapping("/delete-file/{id}")
    public ResponseData<?> deleteFile(
            @PathVariable(name = "id") Long id
    ) {
        try {
            boolean isDelete = fileManagerService.deleteFile(id);
            if(isDelete == true){
            return new ResponseData<>(HttpStatus
                    .OK.value(), "Delete File success", 1);
            }
            return new ResponseData<>(HttpStatus
                    .NOT_FOUND.value(), "Not exist File share", 0);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @PostMapping("/share-file")
    public ResponseData<?> ShareFile(
            @RequestBody FileShareDTO fileShareDTO){
        try {
            Long fileId = fileManagerService.shareFileForOther(fileShareDTO);
            return new ResponseData<>(HttpStatus.OK.value(), "Share File success", fileId, 1);
        } catch (Exception e) {
            return new ResponseError(0, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

    }
    @GetMapping("/get-list-file-share")
    public ResponseData<?> getListFileShare(
            @RequestParam Long fileId,
            @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage
    ) {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    fileManagerService.getListFileShare(fileId, currentPage, perPage));
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Get list file failed");
        }
    }
    @PostMapping("/upload-event-file")
    public ResponseData<?> uploadEventFile(
            @RequestParam(value  = "eventId") String eventId,
            @RequestParam(value  = "file", required = false) MultipartFile file)
    {
        try{
            String fileName = file.getOriginalFilename();
            File templFile = File.createTempFile(file.getOriginalFilename(), null);
            file.transferTo(templFile);
            FileResponse fileResponse = fileManagerService.uploadEventFile(templFile,fileName,Long.parseLong(eventId));
            return new ResponseData<>(HttpStatus.OK.value(),"Upload event file success", fileResponse,1);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Upload file failed");
        }
    }
    @GetMapping("/get-list-event-file")
    public ResponseData<?> getListEventFile(
            @RequestParam Long eventId,
            @RequestParam(value = "currentPage", defaultValue = "0") int currentPage,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage
    ) {
        try {
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    fileManagerService.getListFileEventFromDrive(eventId, currentPage, perPage));
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Get list file failed");
        }
    }
    @DeleteMapping("/delete-event-file/{eventId}")
    public ResponseData<?> deleteEventFile(
            @PathVariable(name = "eventId") Long eventFileId
    ) {
        try {
            boolean isDelete = fileManagerService.deleteEventFile(eventFileId);
            if(isDelete == true){
                return new ResponseData<>(HttpStatus
                        .OK.value(), "Delete File success", 1);
            }
            return new ResponseData<>(HttpStatus
                    .NOT_FOUND.value(), "Not exist File event", 0);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @PostMapping("/upload-avatar")
    public ResponseData<?> uploadAvatar(
            @RequestParam(value  = "file", required = false) MultipartFile file)
    {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            String fileName = file.getOriginalFilename();
            File templFile = File.createTempFile(file.getOriginalFilename(), null);
            file.transferTo(templFile);
            Users fileResponse = fileManagerService.uploadAvatar(templFile,fileName,userId);
            return new ResponseData<>(HttpStatus.OK.value(),"Upload avatar success", fileResponse,1);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Upload avatar failed");
        }
    }
    @PostMapping("/update-avatar")
    public ResponseData<?> upDateAvatar(
            @RequestParam(value  = "file", required = false) MultipartFile file)
    {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            String fileName = file.getOriginalFilename();
            File templFile = File.createTempFile(file.getOriginalFilename(), null);
            file.transferTo(templFile);
            String fileResponse = fileManagerService.updateAvatar(templFile,fileName,userId);
            return new ResponseData<>(HttpStatus.OK.value(),"Upload avatar success", fileResponse,1);
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Upload avatar failed");
        }
    }

    @GetMapping("/get-avatar")
    public ResponseData<?> getAvatarFile(
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            return new ResponseData<>(1, HttpStatus.OK.value(),
                    fileManagerService.getAvatar(userId));
        } catch (Exception e) {
            return new ResponseError(0,
                    HttpStatus.BAD_REQUEST.value(), "Get avatar  file failed");
        }
    }

}
