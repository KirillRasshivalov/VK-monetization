package algo.vk_monetisation.controllers;

import algo.vk_monetisation.dto.ClusterStatusDTO;
import algo.vk_monetisation.dto.EisIntegrationLogDTO;
import algo.vk_monetisation.entities.EisIntegrationLog;
import algo.vk_monetisation.repositories.EisIntegrationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ops")
@RequiredArgsConstructor
public class ClusterOpsController {

    private final EisIntegrationLogRepository eisIntegrationLogRepository;

    @Value("${app.cluster.node-id}")
    private String nodeId;

    @Value("${app.cluster.compliance-listener.enabled}")
    private boolean complianceListenerEnabled;

    @Value("${app.cluster.notification-listener.enabled}")
    private boolean notificationListenerEnabled;

    @Value("${app.scheduler.engagement.enabled:true}")
    private boolean engagementSchedulerEnabled;

    @Value("${app.eis.stub-mode:true}")
    private boolean eisStubMode;

    @GetMapping("/cluster")
    @PreAuthorize("hasRole('MODERATOR')")
    public ClusterStatusDTO clusterStatus() {
        List<EisIntegrationLogDTO> logs = eisIntegrationLogRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
        return new ClusterStatusDTO(
                nodeId,
                complianceListenerEnabled,
                notificationListenerEnabled,
                engagementSchedulerEnabled,
                eisStubMode,
                logs
        );
    }

    @GetMapping("/eis-logs/campaign/{campaignId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<EisIntegrationLogDTO> logsByCampaign(@PathVariable Long campaignId) {
        return eisIntegrationLogRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private EisIntegrationLogDTO toDto(EisIntegrationLog log) {
        return new EisIntegrationLogDTO(
                log.getId(),
                log.getProvider(),
                log.getOperation(),
                log.getCampaignId(),
                log.getStatus(),
                log.getProcessedByNode(),
                log.getCreatedAt()
        );
    }
}
