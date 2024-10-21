package fpt.capstone.iUser.service;

import fpt.capstone.iUser.dto.request.FileShareDTO;
import fpt.capstone.iUser.dto.response.FileDetailResponse;
import fpt.capstone.iUser.dto.response.FileResponse;
import fpt.capstone.iUser.dto.response.PageResponse;
import fpt.capstone.iUser.model.FileShare;
import fpt.capstone.iUser.model.Users;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

public interface FileManagerService {
     FileResponse uploadFile(File file,String fileName,String userId);
     FileDetailResponse getFileFromDrive(Long fileId);
     PageResponse<?> getListFileFromDrive(String userId, int pageNo, int pageSize);
     boolean deleteFileShare(Long fileShareId);
     boolean deleteFile(Long fileId);
     Long shareFileForOther(FileShareDTO fileShareDTO);
     PageResponse<?> getListFileShare(Long fileId, int pageNo, int pageSize);
     FileResponse uploadEventFile(File file,String fileName,Long eventId);
     PageResponse<?> getListFileEventFromDrive(Long eventId, int pageNo, int pageSize);
     boolean deleteEventFile( Long eventFileId);
     Users uploadAvatar(File file, String fileName, String userId);
     String updateAvatar(File file, String fileName, String userId);
     String getAvatar(String userId);
}
