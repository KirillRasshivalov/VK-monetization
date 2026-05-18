package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.CampaignActivationLedgerEntryDTO;
import jakarta.transaction.TransactionSynchronizationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CampaignActivationLedgerService {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS campaign_activation_ledger (
                id BIGSERIAL PRIMARY KEY,
                global_tx_id VARCHAR(255) NOT NULL,
                campaign_id BIGINT NOT NULL,
                person_id BIGINT NOT NULL,
                content_id BIGINT NOT NULL,
                debited_amount DOUBLE PRECISION NOT NULL,
                status VARCHAR(64) NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW()
            )
            """;

    private static final String INSERT_SQL = """
            INSERT INTO campaign_activation_ledger
                (global_tx_id, campaign_id, person_id, content_id, debited_amount, status, created_at)
            VALUES
                (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_CAMPAIGN_SQL = """
            SELECT id, global_tx_id, campaign_id, person_id, content_id, debited_amount, status, created_at
            FROM campaign_activation_ledger
            WHERE campaign_id = ?
            ORDER BY id DESC
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectProvider<TransactionSynchronizationRegistry> txRegistryProvider;

    public CampaignActivationLedgerService(
            @Qualifier("ledgerDataSource") ObjectProvider<DataSource> ledgerDataSourceProvider,
            ObjectProvider<DataSource> anyDataSourceProvider,
            ObjectProvider<TransactionSynchronizationRegistry> txRegistryProvider
    ) {
        DataSource ledgerDataSource = ledgerDataSourceProvider.getIfAvailable(anyDataSourceProvider::getIfAvailable);
        if (ledgerDataSource == null) {
            throw new IllegalStateException("Не найден DataSource для ledger.");
        }
        this.jdbcTemplate = new JdbcTemplate(ledgerDataSource);
        this.txRegistryProvider = txRegistryProvider;
    }

    public void recordActivation(Long campaignId, Long personId, Long contentId, Double debitedAmount, String status) {
        ensureLedgerTableExists();
        String globalTxId = resolveGlobalTransactionId();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(
                INSERT_SQL,
                globalTxId,
                campaignId,
                personId,
                contentId,
                debitedAmount,
                status,
                Timestamp.valueOf(now)
        );
        log.info("Ledger entry created. campaignId={}, contentId={}, globalTxId={}", campaignId, contentId, globalTxId);
    }

    public List<CampaignActivationLedgerEntryDTO> getByCampaignId(Long campaignId) {
        ensureLedgerTableExists();
        return jdbcTemplate.query(SELECT_BY_CAMPAIGN_SQL, campaignLedgerRowMapper(), campaignId);
    }

    private void ensureLedgerTableExists() {
        jdbcTemplate.execute(CREATE_TABLE_SQL);
    }

    private String resolveGlobalTransactionId() {
        TransactionSynchronizationRegistry txRegistry = txRegistryProvider.getIfAvailable();
        if (txRegistry != null && txRegistry.getTransactionKey() != null) {
            return txRegistry.getTransactionKey().toString();
        }
        return "local-" + UUID.randomUUID();
    }

    private RowMapper<CampaignActivationLedgerEntryDTO> campaignLedgerRowMapper() {
        return (rs, rowNum) -> new CampaignActivationLedgerEntryDTO(
                rs.getLong("id"),
                rs.getString("global_tx_id"),
                rs.getLong("campaign_id"),
                rs.getLong("person_id"),
                rs.getLong("content_id"),
                rs.getDouble("debited_amount"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
