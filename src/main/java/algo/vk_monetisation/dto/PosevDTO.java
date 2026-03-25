package algo.vk_monetisation.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PosevDTO(
        String title,
        String description,
        List<MultipartFile> images,
        String OKVDCode
) { }
