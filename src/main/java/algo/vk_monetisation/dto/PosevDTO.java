package algo.vk_monetisation.dto;

public record PosevDTO(
        String title,
        String description,
        String OKVDCode,
        Long personId,
        Double budget,
        String targetAudience
) { }
