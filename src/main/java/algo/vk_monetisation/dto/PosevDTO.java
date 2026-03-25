package algo.vk_monetisation.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record PosevDTO(
        String title,
        String description,
        String OKVDCode,
        Long personId,
        Double budget,
        String targetAudience
) { }
