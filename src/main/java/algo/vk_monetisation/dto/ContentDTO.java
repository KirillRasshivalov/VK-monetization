package algo.vk_monetisation.dto;

public record ContentDTO(
        String imageContentType,
        String imageFileName,
        byte[] imageData,
        byte[] videoData,
        String videoContentType,
        String videoFileName,
        String mediaMetadata
) { }
