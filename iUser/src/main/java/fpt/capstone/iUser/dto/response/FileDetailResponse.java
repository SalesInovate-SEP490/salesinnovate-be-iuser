package fpt.capstone.iUser.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDetailResponse {
    private Resource resource;
    private String contentType;
}

