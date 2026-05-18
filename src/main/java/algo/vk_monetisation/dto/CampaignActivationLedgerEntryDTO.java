package algo.vk_monetisation.dto;

import java.time.LocalDateTime;

public record CampaignActivationLedgerEntryDTO(
        Long id,
        String globalTxId,
        Long campaignId,
        Long personId,
        Long contentId,
        Double debitedAmount,
        String status,
        LocalDateTime createdAt
) {
}
