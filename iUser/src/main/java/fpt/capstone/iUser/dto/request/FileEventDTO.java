package fpt.capstone.iUser.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class FileEventDTO {
    private Long eventFileId;
    private Long eventId;
    private Long fileManagerId;
}

