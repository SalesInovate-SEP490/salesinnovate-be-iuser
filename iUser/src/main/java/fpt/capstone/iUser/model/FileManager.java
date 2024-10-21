package fpt.capstone.iUser.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="file_manager")
public class FileManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;
    @Column(name = "file_cloud_id")
    private String fileCloudId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "userId")
    private String userId;
    @Transient // This annotation indicates that this field is not persistent
    private String contentType;


    // Add a method to determine content type based on file extension
    public String determineContentType() {
        if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch (extension) {
                case "txt":
                    return "text/plain";
                case "pdf":
                    return "application/pdf";
                case "xls":
                    return "application/vnd.ms-excel";
                case "xlsx":
                    return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                case "doc":
                    return "application/msword";
                case "docx":
                    return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                case "zip":
                    return "application/zip";
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "sql":
                    return "application/sql";
                // Add more cases for other file types as needed
                default:
                    return "application/octet-stream";
            }
        }
        return "application/octet-stream";

    }
}
