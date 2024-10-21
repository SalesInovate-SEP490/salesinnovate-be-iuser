package fpt.capstone.iUser.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import fpt.capstone.iUser.dto.Converter;
import fpt.capstone.iUser.dto.request.FileEventDTO;
import fpt.capstone.iUser.dto.request.FileManagerDTO;
import fpt.capstone.iUser.dto.request.FileShareDTO;
import fpt.capstone.iUser.dto.response.FileDetailResponse;
import fpt.capstone.iUser.dto.response.FileResponse;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.model.EventFile;
import fpt.capstone.iUser.model.FileManager;
import fpt.capstone.iUser.model.FileShare;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.repository.FileEvenRepository;
import fpt.capstone.iUser.repository.FileManagerRepository;
import fpt.capstone.iUser.repository.FileShareRepository;
import fpt.capstone.iUser.repository.UsersRepository;
import fpt.capstone.iUser.repository.event.EventRepository;
import fpt.capstone.iUser.service.FileManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FileManagerServiceImpl implements FileManagerService {
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoodleCredentials();
    private final Converter converter;
    private final FileManagerRepository fileManagerRepository;
    private final FileShareRepository fileShareRepository;
    private final UsersRepository usersRepository;
    private final FileEvenRepository fileEvenRepository;
    @Override
    public FileResponse uploadFile(File file,String fileName,String userId) {

        try {
            com.google.api.services.drive.model.File uploadFile = saveFileTODrive(file, fileName);

            FileManagerDTO fileManagerDTO = converter.saveDataFileToDTO(fileName,
                    uploadFile.getId());
            FileManager fileManager = converter.DTOtoFileManager(fileManagerDTO);
            fileManager.setCreatedDate(
                    Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            fileManagerRepository.save(fileManager);
            FileResponse fileResponse = converter.entityToResponse(fileManager);
            FileShareDTO fileShareDTO = converter.saveDataFileShareToDTO(userId,fileResponse.getId());
            fileShareRepository.save(converter.DTOtoFileShare(fileShareDTO));
            file.delete();
            return fileResponse;
        }catch (Exception e){
            return null;
        }
    }

    private com.google.api.services.drive.model.File saveFileTODrive(File file, String fileName) throws GeneralSecurityException, IOException {
        String folderId = "1gY4F2gBf3nBqvNemmTK0tovSRSerXDQO";
        Drive drive = createDriveService();
        com.google.api.services.drive.model.File fileMetaData =
                new com.google.api.services.drive.model.File();
        fileMetaData.setName(fileName);
        fileMetaData.setParents(Collections.singletonList(folderId));
        FileContent mediaContent = new FileContent("pdf/xlsx/xls/docx/doc/zip/jpg/png|gif/sql", file);
        com.google.api.services.drive.model.File uploadFile =
                drive.files().create(fileMetaData,mediaContent).setFields("id").execute();
        String fileUrl = "https://drive.google.com/uc?export=view&id="+uploadFile.getId();
        log.info("File URL: "+fileUrl);
        return uploadFile;
    }

    @Override
    public FileDetailResponse getFileFromDrive(Long fileId) {
        try {
            FileManager fileManager = fileManagerRepository.getById(fileId);

            Drive drive = createDriveService();
            com.google.api.services.drive.model.File file =
                    drive.files().get(fileManager.getFileCloudId()).execute();
            String fileName = file.getName();
            java.io.File downloadedFile = new java.io.File(fileName);
            OutputStream outputStream = new FileOutputStream(downloadedFile);
            drive.files().get(fileManager.getFileCloudId()).executeMediaAndDownloadTo(outputStream);
            outputStream.flush();
            outputStream.close();
            String contentType = fileManager.determineContentType();

            return FileDetailResponse.builder()
                    .resource(new FileSystemResource(downloadedFile))
                    .contentType(contentType)
                    .build();

        } catch (Exception e) {
            log.error("Error downloading file from Drive: " + e.getMessage());
            return null;
        }
    }

    //Lấy ra các file
    @Override
    public PageResponse<?> getListFileFromDrive(String userId, int pageNo, int pageSize) {
        try {
            int page = 0;
            if (pageNo > 0) {
                page = pageNo - 1;
            }
            Pageable pageable = PageRequest.of(page, pageSize);

            Page<FileManager> fileShares =
                    fileManagerRepository.findByUserId(userId,pageable);

            return converter.convertToPageFileReponse(fileShares,pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteFileShare(Long fileShareId) {
        FileShare fileShare = fileShareRepository.findById(fileShareId).orElse(null);
        if(fileShare == null){
            log.info("Not fine file share by file share Id");
            return false;
        }
        fileShareRepository.delete(fileShare);
        return true;
    }

    //Dùng được
    @Override
    public boolean deleteFile(Long fileId) {
        FileManager file = fileManagerRepository.findById(fileId).orElse(null);
        if(file == null){
            log.info("Not fine file by file Id");
            return false;
        }
        fileManagerRepository.delete(file);
        return true;
    }

    @Override
    public Long shareFileForOther(FileShareDTO fileShareDTO) {
        FileShare fileShare = converter.DTOtoFileShare(fileShareDTO);
        String userId = fileShareDTO.getUserId();
        Long fileId = fileShareDTO.getFileId();
        List<FileShare> fileShareList = fileShareRepository
                .findByUserIdOrFileId(userId, fileId);
        boolean userIdExists = fileShareList.stream()
                .anyMatch(fs -> fs.getUsers().getUserId().equals(userId));
        boolean fileIdExists = fileShareList.stream()
                .anyMatch(fs -> fs.getFileManager().getFileId().equals(fileId));
        if (fileShare != null ) {
            if(userIdExists && fileIdExists)
            {
                throw new IllegalArgumentException("Duplicate userId and fileId combination is not allowed.");
            }
            log.info("The file entered is valid");
            fileShareRepository.save(fileShare);
            return fileShare.getFileManager().getFileId();
        }
        log.info("The file entered is null ");
        return null;
    }

    @Override
    public PageResponse<?> getListFileShare(Long fileId, int pageNo, int pageSize) {
        try {
            int page = 0;
            if (pageNo > 0) {
                page = pageNo - 1;
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<FileShare> fileShares =
                    fileShareRepository.findFileShareByUser(fileId,pageable);

            return PageResponse.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .total(fileShares.getTotalPages())
                    .items(fileShares)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FileResponse uploadEventFile(File file, String fileName, Long eventId) {
        try {
            com.google.api.services.drive.model.File uploadFile = saveFileTODrive(file, fileName);

            FileManagerDTO fileManagerDTO = converter.saveDataFileToDTO(fileName,
                    uploadFile.getId());
            FileManager fileManager = converter.DTOtoFileManager(fileManagerDTO);
            fileManager.setCreatedDate(
                    Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            fileManagerRepository.save(fileManager);
            FileResponse fileResponse = converter.entityToResponse(fileManager);
            FileEventDTO fileEventDTO = converter.saveDataFileEventToDTO(eventId,fileResponse.getId());
            fileEvenRepository.save(converter.DTOtoFileEvent(fileEventDTO,eventId));
            file.delete();
            return fileResponse;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public PageResponse<?> getListFileEventFromDrive(Long eventId, int pageNo, int pageSize) {
        try {
            int page = 0;
            if (pageNo > 0) {
                page = pageNo - 1;
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<EventFile> fileEvent =
                    fileEvenRepository.findEventFileByEvent_EventId(eventId,pageable);

            return PageResponse.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .total(fileEvent.getTotalPages())
                    .items(fileEvent)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPathToGoodleCredentials(){
        String currentDirectory = System.getProperty("user.dir");
        Path fiPath = Paths.get(currentDirectory,"cred.json");
        return fiPath.toString();

    }
    public Drive createDriveService() throws GeneralSecurityException, IOException{
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .build();
    }
    @Override
    public boolean deleteEventFile( Long eventFileId) {
        EventFile eventFile = fileEvenRepository.findById(eventFileId).orElse(null);
        FileManager file = fileManagerRepository.findById(eventFile.getFileManager().getFileId()).orElse(null);

        if(file == null && eventFile == null){
            log.info("Not fine file by file Id and event File Id");
            return false;
        }
        fileEvenRepository.delete(eventFile);
        fileManagerRepository.delete(file);
        return true;
    }

    @Override
    public Users uploadAvatar(File file, String fileName, String userId) {

        try {
            com.google.api.services.drive.model.File uploadFile = saveFileTODrive(file, fileName);
            Users user = usersRepository.findById(userId).orElse(null);
            user.setAvatar(uploadFile.getId());
            String avatarLink = "https://drive.google.com/uc?export=view&id="+uploadFile.getId();
            user.setAvatarId(avatarLink);
            usersRepository.save(user);
            file.delete();
            return user;
        }catch (Exception e){
            throw new RuntimeException("Error uploading avatar");
        }
    }

    @Override
    public String updateAvatar(File file, String fileName, String userId) {
        try {
            if(file == null){
                throw new RuntimeException("File is null");
            }
            com.google.api.services.drive.model.File uploadFile = saveFileTODrive(file, fileName);
            Users user = usersRepository.findById(userId).orElse(null);
            if(user.getAvatar() != null && user.getAvatarId() != null){
                deleteFileFromDrive(user.getAvatar());
            }
            user.setAvatar(uploadFile.getId());
            String avatarLink = "https://drive.google.com/uc?export=view&id="+uploadFile.getId();
            user.setAvatarId(avatarLink);
            usersRepository.save(user);
            file.delete();
            return avatarLink;
        }catch (Exception e){
            throw new RuntimeException("Error uploading avatar");
        }
    }

    @Override
    public String getAvatar(String userId) {
        Users user = usersRepository.findByUserId(userId);
        return user.getAvatarId();
    }

    public boolean deleteFileFromDrive(String fileId) {
        try {
            Drive driveService = createDriveService();
            driveService.files().delete(fileId).execute();
            log.info("File with ID " + fileId + " deleted successfully from Google Drive.");
            return true;
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error deleting file from Google Drive: " + e.getMessage());
            return false;
        }
    }
}
