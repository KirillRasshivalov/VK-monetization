package algo.vk_monetisation.dto;

public record ContentStatsDTO(
        Long campaignId,
        Long contentId,
        Long views,
        Long likes
) { }

